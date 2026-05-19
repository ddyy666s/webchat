import { describe, it, expect, vi, beforeEach } from 'vitest'

const { mockWsService } = vi.hoisted(() => ({
  mockWsService: {
    connect: vi.fn(),
    disconnect: vi.fn(),
    sendMessage: vi.fn(),
    onMessage: vi.fn(),
    onStatus: vi.fn()
  }
}))

vi.mock('@/utils/websocket', () => ({ websocketService: mockWsService }))

import { useWebSocket } from '@/composables/useWebSocket'

describe('useWebSocket', () => {
  beforeEach(() => { vi.clearAllMocks() })

  it('connect triggers websocketService.connect and sets isConnected', () => {
    const { connect, isConnected } = useWebSocket()
    expect(isConnected.value).toBe(false)
    connect()
    expect(mockWsService.connect).toHaveBeenCalled()
    expect(isConnected.value).toBe(true)
  })

  it('disconnect triggers websocketService.disconnect and clears isConnected', () => {
    const { disconnect, isConnected } = useWebSocket()
    disconnect()
    expect(mockWsService.disconnect).toHaveBeenCalled()
    expect(isConnected.value).toBe(false)
  })

  it('sendMessage delegates to websocketService', () => {
    const { sendMessage } = useWebSocket()
    sendMessage(1, 'hello', 1)
    expect(mockWsService.sendMessage).toHaveBeenCalledWith(1, 'hello', 1)
  })

  it('sendMessage defaults messageType to 1', () => {
    const { sendMessage } = useWebSocket()
    sendMessage(1, 'hello')
    expect(mockWsService.sendMessage).toHaveBeenCalledWith(1, 'hello', 1)
  })

  it('onMessage registers callback for message type', () => {
    const { onMessage } = useWebSocket()
    const cb = vi.fn()
    onMessage('message', cb)
    expect(mockWsService.onMessage).toHaveBeenCalledWith(cb)
  })
})
