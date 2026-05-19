import { describe, it, expect, vi, beforeEach } from 'vitest'
import { downloadChatRecord, downloadJson } from '@/utils/download'

describe('download utils', () => {
  beforeEach(() => {
    vi.restoreAllMocks()
    localStorage.clear()
    global.fetch = vi.fn().mockResolvedValue({
      blob: vi.fn().mockResolvedValue(new Blob(['test']))
    })
    URL.createObjectURL = vi.fn(() => 'blob:test')
    URL.revokeObjectURL = vi.fn()
  })

  it('downloadChatRecord fetches and triggers file download', async () => {
    const clickSpy = vi.spyOn(HTMLAnchorElement.prototype, 'click')
    localStorage.setItem('chat_token', '"test-token"')

    await downloadChatRecord(1, 'Alice')

    expect(fetch).toHaveBeenCalledWith('/api/message/download/1', {
      headers: { Authorization: 'Bearer test-token' }
    })
    expect(clickSpy).toHaveBeenCalled()
    expect(URL.revokeObjectURL).toHaveBeenCalled()
  })

  it('downloadChatRecord handles fetch error gracefully', async () => {
    global.fetch = vi.fn().mockRejectedValue(new Error('network error'))
    const consoleSpy = vi.spyOn(console, 'error').mockImplementation(() => {})
    await downloadChatRecord(1, 'Alice')
    expect(consoleSpy).toHaveBeenCalled()
  })

  it('downloadJson creates blob and triggers download', () => {
    const clickSpy = vi.spyOn(HTMLAnchorElement.prototype, 'click')
    downloadJson({ foo: 'bar' }, 'data.json')
    expect(clickSpy).toHaveBeenCalled()
  })
})
