/** Socket.io 连接管理模块 @module rtcSocket */
import { type Ref } from 'vue'
import { io, type Socket } from 'socket.io-client'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/userStore'

const RTC_SOCKET_URL = import.meta.env.VITE_RTC_SOCKET_URL || window.location.origin

/** RTC 通话用户 */
export interface RtcUser { id: string; username?: string; nickname?: string }
/** RTC 通话邀请 */
export interface RtcInvite { inviteId: string; roomId: string; roomName?: string; roomType?: 'direct' | 'group'; callType?: 'video'; fromUser: RtcUser }
/** 房间成员 */
export interface RoomMember { id: string; username?: string; nickname?: string; isCreator?: boolean; isJoined?: boolean }
/** 传输层回调，用于解耦 socket 与 transport 的循环依赖 */
export interface TransportCallbacks { onNewProducer?: (producerId: string, user: RtcUser) => void; onProducerClosed?: (producerId: string) => void; onResetMediaState?: () => void }

/** 创建 Socket.io 连接管理器 @param reactive 响应式状态引用集合 @returns 连接管理方法 */
export function createSocketModule(reactive: {
  statusText: Ref<string>; pendingInvite: Ref<RtcInvite | null>; roomId: Ref<string>; roomTitle: Ref<string>
  visible: Ref<boolean>; members: Ref<RoomMember[]>; isJoined: Ref<boolean>; remoteMedias: Ref<any[]>
}) {
  let socket: Socket | null = null
  let transportCbs: TransportCallbacks = {}

  function setTransportCallbacks(cbs: TransportCallbacks) { transportCbs = cbs }

  function ensureSocket(): Socket {
    const userStore = useUserStore()
    const token = userStore.token?.replace(/"/g, '')
    if (!token) throw new Error('请先登录')
    if (socket) { if (!socket.connected) socket.connect(); return socket }
    socket = io(RTC_SOCKET_URL, { autoConnect: false, auth: { token }, reconnection: true, reconnectionAttempts: Infinity, reconnectionDelay: 1000, reconnectionDelayMax: 10000 })
    socket.on('connect_error', (err: Error) => {
      reactive.statusText.value = `媒体服务连接失败：${err.message}`
      if (err.message.startsWith('UNAUTHORIZED')) ElMessage.error('视频服务登录已过期，请重新登录')
    })
    socket.on('incomingInvite', (invite: RtcInvite) => {
      reactive.pendingInvite.value = invite; reactive.roomId.value = invite.roomId
      reactive.roomTitle.value = invite.roomName || `${invite.fromUser.nickname || invite.fromUser.username || '好友'}的视频通话`
      reactive.statusText.value = `${invite.fromUser.nickname || invite.fromUser.username || '有人'} 邀请你加入视频通话`
      reactive.visible.value = true
    })
    socket.on('inviteResponded', ({ accepted, by }: { accepted: boolean; by: RtcUser }) => {
      reactive.statusText.value = accepted ? `${by.nickname || by.username || '对方'} 已接受邀请` : `${by.nickname || by.username || '对方'} 已拒绝邀请`
    })
    socket.on('roomMembersUpdate', ({ roomId: rid, members: next }: { roomId: string; members: RoomMember[] }) => {
      if (rid === reactive.roomId.value) reactive.members.value = Array.isArray(next) ? next : []
    })
    socket.on('newProducer', ({ producerId, user }: { producerId: string; user: RtcUser }) => { transportCbs.onNewProducer?.(producerId, user) })
    socket.on('producerClosed', ({ producerId }: { producerId: string }) => { transportCbs.onProducerClosed?.(producerId) })
    socket.on('peerLeft', ({ user, reason }: { user: RtcUser; reason?: string }) => {
      reactive.statusText.value = `${user.nickname || user.username || '成员'} ${reason || '离开了通话'}`
    })
    socket.on('roomEnded', ({ reason }: { reason?: string }) => {
      reactive.statusText.value = reason || '通话已结束'; transportCbs.onResetMediaState?.(); reactive.visible.value = false
    })
    socket.on('disconnect', () => { if (reactive.isJoined.value) reactive.statusText.value = '媒体连接断开，正在重连...' })
    socket.connect()
    return socket
  }

  async function waitForSocketConnected(): Promise<void> {
    const s = ensureSocket()
    if (s.connected) return
    await new Promise<void>((resolve, reject) => {
      const cleanup = () => { s.off('connect', onConnect); s.off('connect_error', onError) }
      const onConnect = () => { cleanup(); resolve() }
      const onError = (err: Error) => { cleanup(); reject(err) }
      s.on('connect', onConnect); s.on('connect_error', onError)
    })
  }

  async function request<T = any>(type: string, data: Record<string, unknown> = {}): Promise<T> {
    await waitForSocketConnected()
    return await new Promise<T>((resolve, reject) => {
      socket!.emit(type, data, (response: any) => {
        if (response?.error) { reject(new Error(response.error)); return }
        resolve(response)
      })
    })
  }

  function disconnect() { if (socket) { socket.disconnect(); socket = null } }

  return { ensureSocket, waitForSocketConnected, request, disconnect, setTransportCallbacks }
}
