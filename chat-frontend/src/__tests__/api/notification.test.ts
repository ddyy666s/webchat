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

import { sendNotificationApi, getUnreadNotificationsApi, markNotificationAsReadApi } from '@/api/notification'

describe('notificationApi', () => {
  beforeEach(() => { vi.clearAllMocks() })

  it('sendNotificationApi sends POST /system-notification/send', async () => {
    await sendNotificationApi('Title', 'Content')
    expect(request.post).toHaveBeenCalledWith('/system-notification/send', {
      title: 'Title',
      content: 'Content',
    })
  })

  it('getUnreadNotificationsApi calls silentRequest.get /system-notification/unread', async () => {
    mockSilentInstance.get.mockResolvedValue({ data: { data: { total: 0, notifications: [] } } })
    const result = await getUnreadNotificationsApi()
    expect(mockSilentInstance.get).toHaveBeenCalledWith('/system-notification/unread')
    expect(result).toEqual({ data: { data: { total: 0, notifications: [] } } })
  })

  it('getUnreadNotificationsApi returns fallback on nullish response', async () => {
    mockSilentInstance.get.mockResolvedValue(null)
    const result = await getUnreadNotificationsApi()
    expect(result).toEqual({ total: 0, notifications: [] })
  })

  it('markNotificationAsReadApi sends PUT /system-notification/read/:id', async () => {
    await markNotificationAsReadApi(3)
    expect(request.put).toHaveBeenCalledWith('/system-notification/read/3')
  })
})
