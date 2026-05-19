import { describe, it, expect, vi, beforeEach } from 'vitest'

const { mockSendCallSignal } = vi.hoisted(() => ({ mockSendCallSignal: vi.fn() }))

vi.mock('@/utils/websocket', () => ({
  websocketService: { sendCallSignal: mockSendCallSignal }
}))

const mockAddTrack = vi.fn()
const mockCreateOffer = vi.fn()
const mockCreateAnswer = vi.fn()
const mockSetLocalDescription = vi.fn()
const mockSetRemoteDescription = vi.fn()
const mockAddIceCandidate = vi.fn()
const mockClose = vi.fn()

class MockRTCPeerConnection {
  localDescription: RTCSessionDescription | null = null
  connectionState = 'new'
  onicecandidate: ((e: any) => void) | null = null
  ontrack: ((e: any) => void) | null = null
  onconnectionstatechange: (() => void) | null = null
  addTrack = mockAddTrack
  createOffer = mockCreateOffer
  createAnswer = mockCreateAnswer
  setLocalDescription = mockSetLocalDescription
  setRemoteDescription = mockSetRemoteDescription
  addIceCandidate = mockAddIceCandidate
  close = mockClose
}

const mockGetUserMedia = vi.fn()
const mockTrackStop = vi.fn()
const mockGetTracks = vi.fn(() => [{ stop: mockTrackStop }])

beforeEach(() => {
  vi.clearAllMocks()
  vi.stubGlobal('RTCPeerConnection', MockRTCPeerConnection)
  vi.stubGlobal('RTCSessionDescription', vi.fn((d: any) => d))
  vi.stubGlobal('RTCIceCandidate', vi.fn((d: any) => d))
  vi.stubGlobal('navigator', { mediaDevices: { getUserMedia: mockGetUserMedia } })
})

afterEach(() => {
  vi.unstubAllGlobals()
})

import { useWebRTC } from '@/composables/useWebRTC'

describe('useWebRTC', () => {
  it('starts disconnected with zero duration', () => {
    const { isConnected, callDuration } = useWebRTC('video')
    expect(isConnected.value).toBe(false)
    expect(callDuration.value).toBe(0)
  })

  it('hangup cleans up peer connection and stream', async () => {
    mockGetUserMedia.mockResolvedValue({ getTracks: mockGetTracks })
    const { createOffer, hangup } = useWebRTC('voice')
    const onRemoteStream = vi.fn()
    mockCreateOffer.mockResolvedValue({ sdp: 'offer-sdp' })
    mockSetLocalDescription.mockResolvedValue(undefined)

    await createOffer(onRemoteStream, 1)
    hangup()

    expect(mockClose).toHaveBeenCalled()
    expect(mockTrackStop).toHaveBeenCalled()
  })

  it('addIceCandidate calls pc.addIceCandidate', async () => {
    mockGetUserMedia.mockResolvedValue({ getTracks: mockGetTracks })
    const { createOffer, addIceCandidate } = useWebRTC('voice')
    mockCreateOffer.mockResolvedValue({ sdp: 's' })
    mockSetLocalDescription.mockResolvedValue(undefined)
    mockAddIceCandidate.mockResolvedValue(undefined)

    const onRemote = vi.fn()
    await createOffer(onRemote, 1)
    await addIceCandidate('candidate', '0', 0)

    expect(mockAddIceCandidate).toHaveBeenCalledWith({ candidate: 'candidate', sdpMid: '0', sdpMLineIndex: 0 })
  })

  it('createOffer gets user media and sends offer via websocket', async () => {
    const mockStream = { getTracks: mockGetTracks }
    mockGetUserMedia.mockResolvedValue(mockStream)
    mockCreateOffer.mockResolvedValue({ sdp: 'my-offer' })
    mockSetLocalDescription.mockResolvedValue(undefined)
    mockAddTrack.mockReturnValue(undefined)

    const { createOffer } = useWebRTC('video')
    const onRemoteStream = vi.fn()
    await createOffer(onRemoteStream, 42)

    expect(mockGetUserMedia).toHaveBeenCalledWith({ audio: true, video: true })
    expect(mockCreateOffer).toHaveBeenCalled()
    expect(mockSetLocalDescription).toHaveBeenCalled()
    expect(mockSendCallSignal).toHaveBeenCalledWith(expect.objectContaining({ action: 'offer', toUserId: 42, callType: 'video' }))
  })
})
