/** RTC 视频通话状态管理 Store @module rtcStore */
import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import { ElMessage } from 'element-plus'
import { createSocketModule } from './rtcSocket'
import type { RtcInvite, RoomMember } from './rtcSocket'
import { createTransportModule } from './rtcTransport'
import type { RemoteMedia } from './rtcTransport'
import { createMediaModule } from './rtcMedia'
import { useUserStore } from '@/stores/userStore'

export const useRtcStore = defineStore('rtc', () => {
  const visible = ref(false)
  const roomId = ref('')
  const roomTitle = ref('视频通话')
  const statusText = ref('未连接')
  const pendingInvite = ref<RtcInvite | null>(null)
  const members = ref<RoomMember[]>([])
  const remoteMedias = ref<RemoteMedia[]>([])
  const localStream = ref<MediaStream | null>(null)
  const isJoined = ref(false)
  const isPublishing = ref(false)
  const isMicMuted = ref(false)
  const isCameraOff = ref(false)

  const reactive = { visible, roomId, roomTitle, statusText, pendingInvite, members, remoteMedias, localStream, isJoined, isPublishing, isMicMuted, isCameraOff }
  const isIncoming = computed(() => Boolean(pendingInvite.value && !isJoined.value))
  const remoteVideos = computed(() => remoteMedias.value.filter(m => m.kind === 'video'))
  const remoteAudios = computed(() => remoteMedias.value.filter(m => m.kind === 'audio'))

  const socketModule = createSocketModule(reactive)
  const { ensureSocket, request, disconnect } = socketModule
  const transportModule = createTransportModule(reactive, request)
  socketModule.setTransportCallbacks({
    onNewProducer: transportModule.enqueueProducerForConsume,
    onProducerClosed: transportModule.removeRemoteMedia,
    onResetMediaState: transportModule.resetMediaState
  })
  const mediaModule = createMediaModule(reactive, request, transportModule.getProducers)

  function currentUserPayload() {
    const userStore = useUserStore()
    return { id: String(userStore.userInfo?.id || ''), username: userStore.userInfo?.username, nickname: userStore.userInfo?.nickname || userStore.userInfo?.username }
  }

  async function startDirectVideoCall(targetUser: any) {
    if (!targetUser?.userId && !targetUser?.id) { ElMessage.warning('请选择视频通话对象'); return }
    const me = currentUserPayload()
    const targetName = targetUser.nickname || targetUser.remark || '好友'
    statusText.value = '正在发起视频通话...'; visible.value = true
    try {
      const data = await request('createRoomInvite', { targetUserId: String(targetUser.userId || targetUser.id), callType: 'video', roomName: `${me.nickname || '我'} 与 ${targetName} 的视频通话` })
      transportModule.setRoomFromResponse(data, `${targetName}的视频通话`)
      await transportModule.joinAndPublish()
      statusText.value = `已邀请 ${targetName}`
    } catch (error: any) { visible.value = false; ElMessage.error(error?.message || '发起视频通话失败') }
  }

  async function startGroupVideoCall(group: any, participantIds: number[]) {
    if (!group?.id) return
    statusText.value = '正在创建群视频通话...'; visible.value = true
    try {
      const data = await request('createGroupRoom', { roomId: `group-${group.id}`, roomName: `${group.name || '群聊'}的视频通话`, callType: 'video', participantIds: participantIds.map(id => String(id)) })
      transportModule.setRoomFromResponse(data, `${group.name || '群聊'}的视频通话`)
      await transportModule.joinAndPublish()
      statusText.value = '群视频通话已开始'
    } catch (error: any) { visible.value = false; ElMessage.error(error?.message || '创建群视频通话失败') }
  }

  async function acceptIncoming() {
    if (!pendingInvite.value) return
    try {
      const data = await request('respondRoomInvite', { inviteId: pendingInvite.value.inviteId, accept: true })
      transportModule.setRoomFromResponse(data, pendingInvite.value.roomName || '视频通话')
      await transportModule.joinAndPublish()
      statusText.value = '已加入视频通话'
    } catch (error: any) { ElMessage.error(error?.message || '加入视频通话失败') }
  }

  async function rejectIncoming() {
    if (!pendingInvite.value) { visible.value = false; return }
    try { await request('respondRoomInvite', { inviteId: pendingInvite.value.inviteId, accept: false }) } catch (e) { console.error(e) }
    finally { pendingInvite.value = null; visible.value = false }
  }

  const disconnectAll = () => {
    transportModule.resetMediaState(); pendingInvite.value = null
    visible.value = false; roomId.value = ''; members.value = []; disconnect()
  }

  return {
    visible, roomId, roomTitle, statusText, pendingInvite, members, remoteMedias, remoteVideos, remoteAudios,
    localStream, isJoined, isIncoming, isPublishing, isMicMuted, isCameraOff,
    ensureSocket, startDirectVideoCall, startGroupVideoCall, acceptIncoming, rejectIncoming,
    toggleMic: mediaModule.toggleMic, toggleCamera: mediaModule.toggleCamera,
    leaveCall: transportModule.leaveCall, endCall: transportModule.endCall, disconnect: disconnectAll
  }
})
