import { describe, it, expect, vi, beforeEach } from 'vitest'
import request from '@/api/request'
import { loginApi, registerApi, getMeApi, updateProfileApi, updateAvatarApi } from '@/api/user'

vi.mock('@/api/request', () => ({
  default: { get: vi.fn(), post: vi.fn(), put: vi.fn(), delete: vi.fn() }
}))

describe('userApi', () => {
  beforeEach(() => { vi.clearAllMocks() })

  it('loginApi sends POST /user/login', async () => {
    const params = { username: 'alice', password: '123456' }
    await loginApi(params)
    expect(request.post).toHaveBeenCalledWith('/user/login', params)
  })

  it('registerApi sends POST /user/register', async () => {
    const params = { username: 'bob', password: '123456', nickname: 'Bob' }
    await registerApi(params)
    expect(request.post).toHaveBeenCalledWith('/user/register', params)
  })

  it('getMeApi sends GET /user/me', async () => {
    await getMeApi()
    expect(request.get).toHaveBeenCalledWith('/user/me')
  })

  it('updateProfileApi sends PUT /user/profile', async () => {
    const data = { nickname: 'new', signature: 'hello' }
    await updateProfileApi(data)
    expect(request.put).toHaveBeenCalledWith('/user/profile', data)
  })

  it('updateAvatarApi sends POST /user/avatar with multipart/form-data', async () => {
    const file = new File([''], 'avatar.png', { type: 'image/png' })
    await updateAvatarApi(file)
    expect(request.post).toHaveBeenCalledWith('/user/avatar', expect.any(FormData), {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  })
})
