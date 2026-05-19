import { describe, it, expect, vi, beforeEach } from 'vitest'

const { mockSendCallSignal } = vi.hoisted(() => ({ mockSendCallSignal: vi.fn() }))

vi.mock('@/utils/websocket', () => ({
  websocketService: { sendCallSignal: mockSendCallSignal }
}))

import { useCallSignal } from '@/composables/useCallSignal'

describe('useCallSignal', () => {
  beforeEach(() => { vi.clearAllMocks() })

  it('sendOffer calls websocketService.sendCallSignal with offer action', () => {
    const { sendOffer } = useCallSignal()
    sendOffer(42, 'video', 'sdp-offer')
    expect(mockSendCallSignal).toHaveBeenCalledWith({
      action: 'offer', toUserId: 42, callType: 'video', sdp: 'sdp-offer'
    })
  })

  it('sendAnswer calls websocketService.sendCallSignal with answer action', () => {
    const { sendAnswer } = useCallSignal()
    sendAnswer(7, 'voice', 'sdp-answer')
    expect(mockSendCallSignal).toHaveBeenCalledWith({
      action: 'answer', toUserId: 7, callType: 'voice', sdp: 'sdp-answer'
    })
  })

  it('sendIceCandidate calls websocketService.sendCallSignal with ice-candidate action', () => {
    const { sendIceCandidate } = useCallSignal()
    const candidate = { candidate: 'candidate-string', sdpMid: '0', sdpMLineIndex: 0 }
    sendIceCandidate(7, 'voice', candidate)
    expect(mockSendCallSignal).toHaveBeenCalledWith({
      action: 'ice-candidate', toUserId: 7, callType: 'voice',
      candidate: 'candidate-string', sdpMid: '0', sdpMLineIndex: 0
    })
  })

  it('sendHangup calls websocketService.sendCallSignal with hangup action', () => {
    const { sendHangup } = useCallSignal()
    sendHangup(1, 'video')
    expect(mockSendCallSignal).toHaveBeenCalledWith({
      action: 'hangup', toUserId: 1, callType: 'video'
    })
  })

  it('returns all four signal functions', () => {
    const result = useCallSignal()
    expect(result).toHaveProperty('sendOffer')
    expect(result).toHaveProperty('sendAnswer')
    expect(result).toHaveProperty('sendIceCandidate')
    expect(result).toHaveProperty('sendHangup')
    expect(typeof result.sendOffer).toBe('function')
    expect(typeof result.sendAnswer).toBe('function')
    expect(typeof result.sendIceCandidate).toBe('function')
    expect(typeof result.sendHangup).toBe('function')
  })
})
