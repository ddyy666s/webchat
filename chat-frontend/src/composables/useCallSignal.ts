/** 通话信令收发 composable @module useCallSignal */
import { websocketService } from '@/utils/websocket'

/** 通话信令操作 hook @returns 各信令发送方法 */
export const useCallSignal = () => {
  /** 发送 Offer 信令 @param toUserId 目标用户ID @param callType 通话类型 @param sdp SDP描述 */
  const sendOffer = (toUserId: number, callType: 'voice' | 'video', sdp: string) => websocketService.sendCallSignal({ action: 'offer', toUserId, callType, sdp })

  /** 发送 Answer 信令 @param toUserId 目标用户ID @param callType 通话类型 @param sdp SDP描述 */
  const sendAnswer = (toUserId: number, callType: 'voice' | 'video', sdp: string) => websocketService.sendCallSignal({ action: 'answer', toUserId, callType, sdp })

  /** 发送 ICE Candidate 信令 @param toUserId 目标用户ID @param callType 通话类型 @param candidate ICE候选信息 */
  const sendIceCandidate = (toUserId: number, callType: 'voice' | 'video', candidate: any) => websocketService.sendCallSignal({ action: 'ice-candidate', toUserId, callType, candidate: candidate.candidate, sdpMid: candidate.sdpMid, sdpMLineIndex: candidate.sdpMLineIndex })

  /** 发送挂断信令 @param toUserId 目标用户ID @param callType 通话类型 */
  const sendHangup = (toUserId: number, callType: 'voice' | 'video') => websocketService.sendCallSignal({ action: 'hangup', toUserId, callType })

  return { sendOffer, sendAnswer, sendIceCandidate, sendHangup }
}
