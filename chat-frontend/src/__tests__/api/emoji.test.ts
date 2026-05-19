import { describe, it, expect, vi, beforeEach } from 'vitest'
import request from '@/api/request'
import { getSystemEmojisApi, getUserEmojisApi, uploadEmojiApi, deleteEmojiApi } from '@/api/emoji'

vi.mock('@/api/request', () => ({
  default: { get: vi.fn(), post: vi.fn(), put: vi.fn(), delete: vi.fn() },
}))

describe('emojiApi', () => {
  beforeEach(() => { vi.clearAllMocks() })

  it('getSystemEmojisApi sends GET /emoji/system', async () => {
    await getSystemEmojisApi()
    expect(request.get).toHaveBeenCalledWith('/emoji/system')
  })

  it('getUserEmojisApi sends GET /emoji/user', async () => {
    await getUserEmojisApi()
    expect(request.get).toHaveBeenCalledWith('/emoji/user')
  })

  it('uploadEmojiApi sends POST /emoji/upload with multipart/form-data', async () => {
    const file = new File([''], 'emoji.png', { type: 'image/png' })
    await uploadEmojiApi(file, 'smile', 'faces')
    expect(request.post).toHaveBeenCalledWith('/emoji/upload', expect.any(FormData), {
      headers: { 'Content-Type': 'multipart/form-data' },
    })
  })

  it('uploadEmojiApi works without category', async () => {
    const file = new File([''], 'emoji.png', { type: 'image/png' })
    await uploadEmojiApi(file, 'smile')
    expect(request.post).toHaveBeenCalledWith('/emoji/upload', expect.any(FormData), {
      headers: { 'Content-Type': 'multipart/form-data' },
    })
  })

  it('deleteEmojiApi sends DELETE /emoji/:id', async () => {
    await deleteEmojiApi(7)
    expect(request.delete).toHaveBeenCalledWith('/emoji/7')
  })
})
