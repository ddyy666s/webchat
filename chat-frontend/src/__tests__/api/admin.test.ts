import { describe, it, expect, vi, beforeEach } from 'vitest'
import request from '@/api/request'

vi.mock('@/api/request', () => ({
  default: { get: vi.fn(), post: vi.fn(), put: vi.fn(), delete: vi.fn() },
}))

const { mockSilentInstance } = vi.hoisted(() => {
  const mockSilentInstance = {
    get: vi.fn(),
    interceptors: { request: { use: vi.fn() }, response: { use: vi.fn() } },
  }
  return { mockSilentInstance }
})

vi.mock('axios', () => ({
  default: { create: vi.fn(() => mockSilentInstance) },
}))

import { getAdminUsersApi, updateUserStatusApi, getAdminMessagesApi, getAdminStatsApi } from '@/api/admin'
import { getAdminNotificationsApi } from '@/api/notification'

describe('adminApi', () => {
  beforeEach(() => { vi.clearAllMocks() })

  it('getAdminUsersApi sends GET /admin/users with pagination', async () => {
    await getAdminUsersApi(1, 20)
    expect(request.get).toHaveBeenCalledWith('/admin/users', { params: { page: 1, size: 20, keyword: undefined } })
  })

  it('getAdminUsersApi accepts keyword filter', async () => {
    await getAdminUsersApi(1, 10, 'alice')
    expect(request.get).toHaveBeenCalledWith('/admin/users', { params: { page: 1, size: 10, keyword: 'alice' } })
  })

  it('updateUserStatusApi sends PUT /admin/user/:id/status', async () => {
    await updateUserStatusApi(3, 0)
    expect(request.put).toHaveBeenCalledWith('/admin/user/3/status', null, { params: { status: 0 } })
  })

  it('getAdminMessagesApi sends GET /admin/messages with params', async () => {
    const params = { page: 1, size: 20, fromUserId: 5 }
    await getAdminMessagesApi(params)
    expect(request.get).toHaveBeenCalledWith('/admin/messages', { params })
  })

  it('getAdminStatsApi sends GET /admin/stats', async () => {
    await getAdminStatsApi()
    expect(request.get).toHaveBeenCalledWith('/admin/stats')
  })

  it('getAdminNotificationsApi calls silentRequest.get /admin/notifications', async () => {
    mockSilentInstance.get.mockResolvedValue({ data: { data: [{ id: 1, title: 'Notice' }] } })
    const result = await getAdminNotificationsApi()
    expect(mockSilentInstance.get).toHaveBeenCalledWith('/admin/notifications')
    expect(result).toEqual({ data: { data: [{ id: 1, title: 'Notice' }] } })
  })
})
