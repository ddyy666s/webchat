/** WebRTC 点对点音视频通话 composable @module useWebRTC */
import { ref } from 'vue'
import { websocketService } from '@/utils/websocket'

/** 获取 ICE 服务器配置 @returns STUN/TURN 服务器列表 */
const getIceServers = (): RTCIceServer[] => {
  const servers: RTCIceServer[] = [{ urls: 'stun:stun.l.google.com:19302' }]
  const turnUrl = import.meta.env.VITE_TURN_URL
  const turnUser = import.meta.env.VITE_TURN_USERNAME
  const turnCred = import.meta.env.VITE_TURN_CREDENTIAL
  if (turnUrl && turnUser && turnCred) servers.push({ urls: turnUrl, username: turnUser, credential: turnCred })
  return servers
}

/** WebRTC 通话管理 hook @param callType 通话类型 @returns 通话状态与方法 */
export const useWebRTC = (callType: 'voice' | 'video') => {
  /** 是否已连接 */
  const isConnected = ref(false)
  /** 通话时长（秒） */
  const callDuration = ref(0)
  let pc: RTCPeerConnection | null = null
  let localStream: MediaStream | null = null
  let timer: number | null = null
  const configuration = { iceServers: getIceServers() }

  /** 启动本地媒体流 @returns 本地媒体流 */
  const startLocalStream = async () => {
    stopLocalStream()
    localStream = await navigator.mediaDevices.getUserMedia({ audio: true, video: callType === 'video' })
    return localStream
  }

  /** 停止本地媒体流 */
  const stopLocalStream = () => {
    if (localStream) { localStream.getTracks().forEach(t => t.stop()); localStream = null }
  }

  /** 创建 RTCPeerConnection @param onRemoteStream 远端流回调 @param targetUserId 目标用户ID @returns RTCPeerConnection */
  const createPC = (onRemoteStream: (s: MediaStream) => void, targetUserId: number) => {
    pc = new RTCPeerConnection(configuration)
    if (localStream) localStream.getTracks().forEach(t => pc!.addTrack(t, localStream!))
    pc.ontrack = (e) => onRemoteStream(e.streams[0])
    pc.onicecandidate = (e) => {
      if (e.candidate) websocketService.sendCallSignal({ action: 'ice-candidate', toUserId: targetUserId, callType, candidate: e.candidate.candidate, sdpMid: e.candidate.sdpMid, sdpMLineIndex: e.candidate.sdpMLineIndex })
    }
    pc.onconnectionstatechange = () => {
      if (pc?.connectionState === 'connected') { isConnected.value = true; timer = setInterval(() => callDuration.value++, 1000) as unknown as number }
    }
    return pc
  }

  /** 创建 Offer（发起方） @param onRemoteStream 远端流回调 @param targetUserId 目标用户ID */
  const createOffer = async (onRemoteStream: (s: MediaStream) => void, targetUserId: number) => {
    await startLocalStream(); const p = createPC(onRemoteStream, targetUserId)
    const offer = await p.createOffer(); await p.setLocalDescription(offer)
    websocketService.sendCallSignal({ action: 'offer', toUserId: targetUserId, callType, sdp: offer.sdp })
  }

  /** 处理 Offer（接收方） @param sdp 收到的SDP @param onRemoteStream 远端流回调 @param targetUserId 目标用户ID */
  const handleOffer = async (sdp: string, onRemoteStream: (s: MediaStream) => void, targetUserId: number) => {
    await startLocalStream(); const p = createPC(onRemoteStream, targetUserId)
    await p.setRemoteDescription(new RTCSessionDescription({ type: 'offer', sdp }))
    const answer = await p.createAnswer(); await p.setLocalDescription(answer)
    websocketService.sendCallSignal({ action: 'answer', toUserId: targetUserId, callType, sdp: answer.sdp })
  }

  /** 处理 Answer @param sdp 收到的SDP */
  const handleAnswer = async (sdp: string) => {
    if (!pc) throw new Error('No peer connection')
    await pc.setRemoteDescription(new RTCSessionDescription({ type: 'answer', sdp }))
  }

  /** 添加 ICE Candidate @param candidate 候选信息 @param sdpMid SDP mid @param sdpMLineIndex SDP 行索引 */
  const addIceCandidate = async (candidate: string, sdpMid: string, sdpMLineIndex: number) => {
    if (!pc) return
    try { await pc.addIceCandidate(new RTCIceCandidate({ candidate, sdpMid, sdpMLineIndex })) } catch {}
  }

  /** 挂断通话 */
  const hangup = () => {
    if (pc) { pc.onconnectionstatechange = null; pc.close(); pc = null }
    stopLocalStream(); if (timer) { clearInterval(timer); timer = null }
    isConnected.value = false; callDuration.value = 0
  }

  return { isConnected, callDuration, localStream, createOffer, handleOffer, handleAnswer, addIceCandidate, hangup }
}
