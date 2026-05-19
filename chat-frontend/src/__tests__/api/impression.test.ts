import { describe, it, expect, vi, beforeEach } from 'vitest'
import request from '@/api/request'
import {
  addImpressionApi,
  getImpressionsToMeApi,
  getImpressionsByMeApi,
  deleteImpressionApi,
} from '@/api/impression'

vi.mock('@/api/request', () => ({
  default: { get: vi.fn(), post: vi.fn(), put: vi.fn(), delete: vi.fn() },
}))

describe('impressionApi', () => {
  beforeEach(() => { vi.clearAllMocks() })

  it('addImpressionApi sends POST /impression', async () => {
    await addImpressionApi(2, 'nice person')
    expect(request.post).toHaveBeenCalledWith('/impression', { toUserId: 2, content: 'nice person' })
  })

  it('getImpressionsToMeApi sends GET /impression/to-me', async () => {
    await getImpressionsToMeApi()
    expect(request.get).toHaveBeenCalledWith('/impression/to-me')
  })

  it('getImpressionsByMeApi sends GET /impression/by-me', async () => {
    await getImpressionsByMeApi()
    expect(request.get).toHaveBeenCalledWith('/impression/by-me')
  })

  it('deleteImpressionApi sends DELETE /impression/:id', async () => {
    await deleteImpressionApi(10)
    expect(request.delete).toHaveBeenCalledWith('/impression/10')
  })
})
