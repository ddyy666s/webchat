import { describe, it, expect, vi, beforeEach } from 'vitest'
import request from '@/api/request'
import axios from 'axios'
import {
  getChatHistoryApi,
  getUnreadCountApi,
  markAsReadApi,
  recallMessageApi,
  uploadImageApi,
  uploadVoiceApi,
  downloadChatHistoryApi,
} from '@/api/message'

vi.mock('@/api/request', () => ({
  default: { get: vi.fn(), post: vi.fn(), put: vi.fn(), delete: vi.fn() },
}))

describe('messageApi', () => {
  beforeEach(() => { vi.clearAllMocks() })

  it('getChatHistoryApi sends GET /message/history/:friendId with defaults', async () => {
    await getChatHistoryApi(1)
    expect(request.get).toHaveBeenCalledWith('/message/history/1', { params: { page: 1, size: 20 } })
  })

  it('getChatHistoryApi accepts custom page and size', async () => {
    await getChatHistoryApi(1, 3, 50)
    expect(request.get).toHaveBeenCalledWith('/message/history/1', { params: { page: 3, size: 50 } })
  })

  it('getUnreadCountApi sends GET /message/unread/count', async () => {
    await getUnreadCountApi()
    expect(request.get).toHaveBeenCalledWith('/message/unread/count')
  })

  it('markAsReadApi sends PUT /message/read/:friendId', async () => {
    await markAsReadApi(5)
    expect(request.put).toHaveBeenCalledWith('/message/read/5')
  })

  it('recallMessageApi sends PUT /message/recall/:messageId', async () => {
    await recallMessageApi(42)
    expect(request.put).toHaveBeenCalledWith('/message/recall/42')
  })

  it('uploadImageApi sends POST /message/upload/image with multipart/form-data', async () => {
    const file = new File([''], 'img.png', { type: 'image/png' })
    await uploadImageApi(file)
    expect(request.post).toHaveBeenCalledWith('/message/upload/image', expect.any(FormData), {
      headers: { 'Content-Type': 'multipart/form-data' },
    })
  })

  it('uploadVoiceApi sends POST /message/upload/voice with multipart/form-data', async () => {
    const file = new File([''], 'voice.wav', { type: 'audio/wav' })
    await uploadVoiceApi(file)
    expect(request.post).toHaveBeenCalledWith('/message/upload/voice', expect.any(FormData), {
      headers: { 'Content-Type': 'multipart/form-data' },
    })
  })

  it('downloadChatHistoryApi uses axios.get with blob responseType', async () => {
    vi.spyOn(Storage.prototype, 'getItem').mockReturnValue('"test-token"')
    vi.spyOn(URL, 'createObjectURL').mockReturnValue('blob:mock')
    vi.spyOn(URL, 'revokeObjectURL').mockImplementation(() => undefined)
    const blob = new Blob(['content'], { type: 'text/plain' })
    const getSpy = vi.spyOn(axios, 'get').mockResolvedValue({ data: blob })

    await downloadChatHistoryApi(1, 'friend', 100)

    expect(getSpy).toHaveBeenCalledWith('/api/message/download/1', {
      params: { limit: 100 },
      responseType: 'blob',
      headers: { Authorization: 'Bearer test-token' },
    })
    expect(URL.createObjectURL).toHaveBeenCalledWith(blob)
  })
})
