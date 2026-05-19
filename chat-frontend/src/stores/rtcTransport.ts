/** mediasoup 传输层管理模块 @module rtcTransport */
import { type Ref } from 'vue'
import { Device } from 'mediasoup-client'
import type { RtcUser, RoomMember } from './rtcSocket'

/** 远端媒体流 */
export interface RemoteMedia {
  producerId: string
  kind: 'audio' | 'video'
  stream: MediaStream
  user: RtcUser | null
}

/** 创建传输管理器 @param st 响应式状态 @param request Socket 请求函数 @returns 传输管理方法 */
export function createTransportModule(
  st: {
    roomId: Ref<string>; roomTitle: Ref<string>; statusText: Ref<string>; visible: Ref<boolean>
    pendingInvite: Ref<any>; members: Ref<RoomMember[]>; remoteMedias: Ref<RemoteMedia[]>
    localStream: Ref<MediaStream | null>; isJoined: Ref<boolean>; isPublishing: Ref<boolean>
    isMicMuted: Ref<boolean>; isCameraOff: Ref<boolean>
  },
  request: <T = any>(type: string, data?: Record<string, unknown>) => Promise<T>
) {
  let device: Device | null = null
  let sendTransport: any = null
  const recvTransports = new Map<string, any>()
  const producers = new Map<string, any>()
  const consumers = new Map<string, any>()
  const consumersByProducer = new Map<string, any>()
  const consumedProducerIds = new Set<string>()
  const pendingProducerIds = new Set<string>()
  let isFlushingPendingProducers = false

  function resetMediaState() {
    for (const c of consumers.values()) try { c.close() } catch {}
    for (const p of producers.values()) try { p.close() } catch {}
    for (const t of recvTransports.values()) try { t.close() } catch {}
    if (sendTransport) try { sendTransport.close() } catch {}
    if (st.localStream.value) st.localStream.value.getTracks().forEach(t => t.stop())
    device = null; sendTransport = null; recvTransports.clear(); producers.clear(); consumers.clear()
    consumersByProducer.clear(); consumedProducerIds.clear(); pendingProducerIds.clear()
    st.remoteMedias.value = []; st.localStream.value = null; st.isJoined.value = false
    st.isPublishing.value = false; st.isMicMuted.value = false; st.isCameraOff.value = false
  }

  function setRoomFromResponse(data: any, fallbackTitle: string) {
    st.roomId.value = data.roomId; st.roomTitle.value = data.roomName || fallbackTitle
    st.members.value = Array.isArray(data.members) ? data.members : []
    st.pendingInvite.value = null; st.visible.value = true
  }

  function removeRemoteMedia(producerId: string) {
    const consumer = consumersByProducer.get(producerId)
    if (consumer) { try { consumer.close() } catch {}; consumers.delete(consumer.id); consumersByProducer.delete(producerId) }
    consumedProducerIds.delete(producerId); pendingProducerIds.delete(producerId)
    st.remoteMedias.value = st.remoteMedias.value.filter(m => m.producerId !== producerId)
  }

  async function createSendTransport() {
    const params = await request('createWebRtcTransport', { direction: 'send' })
    sendTransport = device!.createSendTransport(params)
    sendTransport.on('connect', async ({ dtlsParameters }: any, cb: () => void, eb: (e: Error) => void) => {
      try { await request('connectTransport', { transportId: sendTransport.id, dtlsParameters }); cb() } catch (e: any) { eb(e) }
    })
    sendTransport.on('produce', async (params: any, cb: (d: { id: string }) => void, eb: (e: Error) => void) => {
      try { const { id } = await request<{ id: string }>('produce', { transportId: sendTransport.id, kind: params.kind, rtpParameters: params.rtpParameters }); cb({ id }) } catch (e: any) { eb(e) }
    })
  }

  async function consumeProducer(producerId: string, fallbackUser: RtcUser | null = null) {
    if (!device || consumedProducerIds.has(producerId)) return
    consumedProducerIds.add(producerId)
    try {
      const data = await request('consume', { producerId, rtpCapabilities: device.rtpCapabilities })
      const { transportOptions, consumerOptions, user } = data
      let recvTransport = recvTransports.get(transportOptions.id)
      if (!recvTransport) {
        recvTransport = device.createRecvTransport(transportOptions)
        recvTransport.on('connect', async ({ dtlsParameters }: any, cb: () => void, eb: (e: Error) => void) => {
          try { await request('connectTransport', { transportId: recvTransport.id, dtlsParameters }); cb() } catch (e: any) { eb(e) }
        })
        recvTransports.set(transportOptions.id, recvTransport)
      }
      const consumer = await recvTransport.consume(consumerOptions)
      consumers.set(consumer.id, consumer); consumersByProducer.set(producerId, consumer)
      consumer.on('transportclose', () => removeRemoteMedia(producerId))
      consumer.on('producerclose', () => removeRemoteMedia(producerId))
      st.remoteMedias.value.push({ producerId, kind: consumer.kind, stream: new MediaStream([consumer.track]), user: user || fallbackUser })
      await request('resume', { consumerId: consumer.id })
    } catch (error) { consumedProducerIds.delete(producerId); throw error }
  }

  function enqueueProducerForConsume(producerId: string, user: RtcUser | null = null) {
    if (!producerId || consumedProducerIds.has(producerId) || pendingProducerIds.has(producerId)) return
    pendingProducerIds.add(producerId); void flushPendingProducers(user)
  }

  async function flushPendingProducers(user: RtcUser | null = null) {
    if (isFlushingPendingProducers || !device || !st.isJoined.value) return
    isFlushingPendingProducers = true
    try {
      for (const pid of [...pendingProducerIds]) {
        try { await consumeProducer(pid, user) } catch (e: any) { console.error('订阅远端媒体失败:', e?.message || e) }
        finally { pendingProducerIds.delete(pid) }
      }
    } finally {
      isFlushingPendingProducers = false
      if (pendingProducerIds.size > 0 && device && st.isJoined.value) void flushPendingProducers()
    }
  }

  async function publishLocalMedia() {
    if (st.localStream.value) return
    st.localStream.value = await navigator.mediaDevices.getUserMedia({ video: true, audio: true })
    st.isPublishing.value = true
    for (const track of st.localStream.value.getTracks()) {
      const p = await sendTransport.produce({ track }); producers.set(p.id, p)
    }
  }

  async function joinAndPublish() {
    if (!st.roomId.value) throw new Error('房间不存在')
    resetMediaState(); st.statusText.value = '正在加入媒体房间...'
    const data = await request('joinRoom', { roomId: st.roomId.value })
    device = new Device(); await device.load({ routerRtpCapabilities: data.rtpCapabilities })
    st.members.value = Array.isArray(data.members) ? data.members : []
    st.isJoined.value = true; await createSendTransport()
    for (const p of data.existingProducers || []) enqueueProducerForConsume(p.producerId, p.user)
    await publishLocalMedia(); st.statusText.value = '通话中'
  }

  async function leaveCall() {
    try { if (st.isJoined.value) await request('leaveRoom') } catch (e) { console.error(e) }
    finally { resetMediaState(); st.pendingInvite.value = null; st.visible.value = false; st.roomId.value = ''; st.members.value = [] }
  }

  async function endCall() {
    try { if (st.isJoined.value) await request('endRoom') } catch (e) { console.error(e) }
    finally { resetMediaState(); st.pendingInvite.value = null; st.visible.value = false; st.roomId.value = ''; st.members.value = [] }
  }

  return {
    resetMediaState, setRoomFromResponse, removeRemoteMedia, createSendTransport,
    consumeProducer, enqueueProducerForConsume, flushPendingProducers,
    publishLocalMedia, joinAndPublish, leaveCall, endCall,
    getProducers: () => producers
  }
}
