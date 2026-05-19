import { describe, it, expect, vi, beforeEach } from 'vitest'
import request from '@/api/request'
import {
  searchUsersApi, sendFriendRequestApi, getFriendRequestsApi,
  handleFriendRequestApi, getFriendListApi, deleteFriendApi
} from '@/api/friend'

vi.mock('@/api/request', () => ({
  default: { get: vi.fn(), post: vi.fn(), put: vi.fn(), delete: vi.fn() }
}))

describe('friendApi', () => {
  beforeEach(() => { vi.clearAllMocks() })

  it('searchUsersApi sends GET /friend/search with keyword', async () => {
    await searchUsersApi('alice')
    expect(request.get).toHaveBeenCalledWith('/friend/search', { params: { keyword: 'alice' } })
  })

  it('sendFriendRequestApi sends POST /friend/request', async () => {
    await sendFriendRequestApi(2, 'hello')
    expect(request.post).toHaveBeenCalledWith('/friend/request', { toUserId: 2, message: 'hello' })
  })

  it('sendFriendRequestApi works without message', async () => {
    await sendFriendRequestApi(2)
    expect(request.post).toHaveBeenCalledWith('/friend/request', { toUserId: 2, message: undefined })
  })

  it('getFriendRequestsApi sends GET /friend/requests', async () => {
    await getFriendRequestsApi()
    expect(request.get).toHaveBeenCalledWith('/friend/requests')
  })

  it('handleFriendRequestApi sends PUT /friend/request/:id', async () => {
    await handleFriendRequestApi(1, 1)
    expect(request.put).toHaveBeenCalledWith('/friend/request/1', { status: 1 })
  })

  it('getFriendListApi sends GET /friend/list', async () => {
    await getFriendListApi()
    expect(request.get).toHaveBeenCalledWith('/friend/list')
  })

  it('deleteFriendApi sends DELETE /friend/:id', async () => {
    await deleteFriendApi(5)
    expect(request.delete).toHaveBeenCalledWith('/friend/5')
  })
})
