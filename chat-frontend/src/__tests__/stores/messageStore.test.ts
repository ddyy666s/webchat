import { describe, it, expect, vi, beforeEach } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'

const { mockGetUnreadCountApi } = vi.hoisted(() => ({
  mockGetUnreadCountApi: vi.fn()
}))

vi.mock('@/api/message', () => ({
  getUnreadCountApi: mockGetUnreadCountApi
}))

import { useMessageStore } from '@/stores/messageStore'
import type { UnreadCountVO } from '@/api/message'

const makeUnreadCount = (overrides: Partial<UnreadCountVO> = {}): UnreadCountVO => ({
  total: 3,
  details: [
    { friendId: 1, friendNickname: 'A', friendAvatar: null, unreadCount: 2 },
    { friendId: 2, friendNickname: 'B', friendAvatar: null, unreadCount: 1 }
  ],
  messages: [
    { id: 10, fromUserId: 1, fromUserNickname: 'A', fromUserAvatar: null, content: 'hi', sendTime: '2024-01-01T00:00:00Z' },
    { id: 11, fromUserId: 1, fromUserNickname: 'A', fromUserAvatar: null, content: 'there', sendTime: '2024-01-01T00:00:01Z' },
    { id: 12, fromUserId: 2, fromUserNickname: 'B', fromUserAvatar: null, content: 'yo', sendTime: '2024-01-01T00:00:02Z' }
  ],
  ...overrides
})

describe('messageStore', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
  })

  it('starts with null unreadCount', () => {
    const store = useMessageStore()
    expect(store.unreadCount).toBeNull()
  })

  it('loadUnreadCount sets unreadCount from API', async () => {
    const data = makeUnreadCount()
    mockGetUnreadCountApi.mockResolvedValue(data)
    const store = useMessageStore()
    await store.loadUnreadCount()
    expect(store.unreadCount).toEqual(data)
    expect(mockGetUnreadCountApi).toHaveBeenCalledOnce()
  })

  it('clearUnreadForFriend removes messages and adjusts total', () => {
    const store = useMessageStore()
    store.unreadCount = makeUnreadCount()
    store.clearUnreadForFriend(1)
    expect(store.unreadCount!.messages).toHaveLength(1)
    expect(store.unreadCount!.messages[0].fromUserId).toBe(2)
    expect(store.unreadCount!.details).toHaveLength(1)
    expect(store.unreadCount!.details[0].friendId).toBe(2)
    expect(store.unreadCount!.total).toBe(1)
  })

  it('clearUnreadForFriend handles non-existent friendId', () => {
    const store = useMessageStore()
    store.unreadCount = makeUnreadCount()
    store.clearUnreadForFriend(99)
    expect(store.unreadCount!.messages).toHaveLength(3)
    expect(store.unreadCount!.details).toHaveLength(2)
    expect(store.unreadCount!.total).toBe(3)
  })

  it('clearUnreadForFriend is noop when unreadCount is null', () => {
    const store = useMessageStore()
    expect(() => store.clearUnreadForFriend(1)).not.toThrow()
  })

  it('loadUnreadCount handles API error gracefully', async () => {
    mockGetUnreadCountApi.mockRejectedValue(new Error('fail'))
    const store = useMessageStore()
    await store.loadUnreadCount()
    expect(store.unreadCount).toBeNull()
  })
})
