import { vi } from 'vitest'

vi.mock('@/stores/userStore', () => ({
  useUserStore: () => ({
    token: 'test-token',
    userInfo: { id: 1, username: 'test', nickname: 'Test', avatar: null, signature: '', role: 'user' }
  })
}))

Object.defineProperty(window, 'matchMedia', {
  writable: true, value: vi.fn().mockImplementation(query => ({ matches: false, media: query, onchange: null, addListener: vi.fn(), removeListener: vi.fn(), addEventListener: vi.fn(), removeEventListener: vi.fn(), dispatchEvent: vi.fn() }))
})

URL.createObjectURL = vi.fn(() => 'blob:mock') as any
URL.revokeObjectURL = vi.fn()

class MockRTCPeerConnection {
  localDescription: RTCSessionDescription | null = null
  connectionState = 'new'
  onicecandidate: ((e: any) => void) | null = null
  ontrack: ((e: any) => void) | null = null
  onconnectionstatechange: (() => void) | null = null
  addTrack = vi.fn()
  createOffer = vi.fn().mockResolvedValue({ sdp: 'mock-sdp', type: 'offer' })
  createAnswer = vi.fn()
  setLocalDescription = vi.fn().mockResolvedValue(undefined)
  setRemoteDescription = vi.fn()
  addIceCandidate = vi.fn()
  close = vi.fn()
}
globalThis.RTCPeerConnection = MockRTCPeerConnection as any

Element.prototype.scrollIntoView = vi.fn()

import ElementPlus from 'element-plus'
import { config } from '@vue/test-utils'
config.global.plugins = [ElementPlus]
