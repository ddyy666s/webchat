import { describe, it, expect, vi, beforeEach } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'

const { mockGetFriendListApi, mockGetFriendRequestsApi } = vi.hoisted(() => ({
  mockGetFriendListApi: vi.fn(),
  mockGetFriendRequestsApi: vi.fn()
}))

vi.mock('@/api/friend', () => ({
  getFriendListApi: mockGetFriendListApi,
  getFriendRequestsApi: mockGetFriendRequestsApi
}))

import { useFriendStore } from '@/stores/friendStore'
import type { FriendGroupVO, FriendVO } from '@/api/friend'

const makeFriend = (id: number, unread = 0): FriendVO => ({
  id, userId: id, nickname: `User${id}`, avatar: null,
  signature: null, remark: null, groupName: '家人', isOnline: false, unreadCount: unread
})

const makeGroup = (name: string, ...friends: FriendVO[]): FriendGroupVO => ({
  groupName: name, friends
})

describe('friendStore', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
  })

  it('starts with empty lists', () => {
    const store = useFriendStore()
    expect(store.friendList).toEqual([])
    expect(store.friendRequests).toEqual([])
  })

  it('loadFriendList sets friendList from API', async () => {
    const data = [makeGroup('家人', makeFriend(1), makeFriend(2))]
    mockGetFriendListApi.mockResolvedValue(data)
    const store = useFriendStore()
    await store.loadFriendList()
    expect(store.friendList).toEqual(data)
    expect(mockGetFriendListApi).toHaveBeenCalledOnce()
  })

  it('loadFriendRequests sets friendRequests from API', async () => {
    const data = [{ id: 1, fromUserId: 3, fromUserNickname: 'Req', fromUserAvatar: null, message: 'hi', status: 0, createdAt: '2024-01-01' }]
    mockGetFriendRequestsApi.mockResolvedValue(data)
    const store = useFriendStore()
    await store.loadFriendRequests()
    expect(store.friendRequests).toEqual(data)
    expect(mockGetFriendRequestsApi).toHaveBeenCalledOnce()
  })

  it('updateFriendOnlineStatus updates a friend isOnline', () => {
    const store = useFriendStore()
    store.friendList = [makeGroup('家人', makeFriend(1)), makeGroup('同事', makeFriend(2))]
    store.updateFriendOnlineStatus(2, true)
    expect(store.friendList[1].friends[0].isOnline).toBe(true)
    expect(store.friendList[0].friends[0].isOnline).toBe(false)
  })

  it('clearUnreadForFriend resets unreadCount to 0', () => {
    const store = useFriendStore()
    store.friendList = [makeGroup('家人', makeFriend(1, 5), makeFriend(2, 3))]
    store.clearUnreadForFriend(1)
    expect(store.friendList[0].friends[0].unreadCount).toBe(0)
    expect(store.friendList[0].friends[1].unreadCount).toBe(3)
  })

  it('getGroupNames returns unique group names', () => {
    const store = useFriendStore()
    store.friendList = [makeGroup('家人', makeFriend(1)), makeGroup('家人', makeFriend(2)), makeGroup('同事', makeFriend(3))]
    expect(store.getGroupNames()).toEqual(['家人', '同事'])
  })

  it('getFriendById returns the correct friend', () => {
    const store = useFriendStore()
    const f1 = makeFriend(1); const f2 = makeFriend(2)
    store.friendList = [makeGroup('家人', f1), makeGroup('同事', f2)]
    const result = store.getFriendById(2)
    expect(result).toBeTruthy()
    if (result) expect(result.userId).toBe(2)
    expect(store.getFriendById(99)).toBeNull()
  })

  it('loadFriendList handles API error gracefully', async () => {
    mockGetFriendListApi.mockRejectedValue(new Error('fail'))
    const store = useFriendStore()
    await store.loadFriendList()
    expect(store.friendList).toEqual([])
  })
})
