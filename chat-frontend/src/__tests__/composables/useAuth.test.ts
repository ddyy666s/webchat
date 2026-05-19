import { describe, it, expect, vi, beforeEach } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'

const push = vi.fn()

const { mockLoginApi, mockRegisterApi } = vi.hoisted(() => ({
  mockLoginApi: vi.fn(),
  mockRegisterApi: vi.fn()
}))

vi.mock('vue-router', () => ({ useRouter: () => ({ push }) }))

vi.mock('@/api/user', () => ({ loginApi: mockLoginApi, registerApi: mockRegisterApi }))

vi.mock('element-plus', () => ({
  ElMessage: { success: vi.fn(), warning: vi.fn(), error: vi.fn() },
  ElMessageBox: { confirm: vi.fn().mockResolvedValue('confirm') }
}))

vi.mock('@/stores/userStore', () => ({
  useUserStore: vi.fn(() => ({
    token: null, userInfo: null, setToken: vi.fn(), setUserInfo: vi.fn(),
    logout: vi.fn(), isLoggedIn: vi.fn(() => false), isAdmin: vi.fn(() => false)
  }))
}))

import { useAuth } from '@/composables/useAuth'

describe('useAuth', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    setActivePinia(createPinia())
  })

  it('login succeeds and navigates to home', async () => {
    mockLoginApi.mockResolvedValue({ token: 't', user: { id: 1, username: 'u', nickname: 'U', avatar: null, signature: '', role: 'user' } })
    const { login } = useAuth()
    const result = await login('user', 'pass')
    expect(result).toBe(true)
    expect(push).toHaveBeenCalledWith('/')
  })

  it('login failure returns false', async () => {
    mockLoginApi.mockRejectedValue(new Error('invalid'))
    const { login } = useAuth()
    const result = await login('user', 'wrong')
    expect(result).toBe(false)
    expect(push).not.toHaveBeenCalled()
  })

  it('register succeeds and navigates to login', async () => {
    mockRegisterApi.mockResolvedValue(undefined)
    const { register } = useAuth()
    const result = await register('u', 'p', 'U')
    expect(result).toBe(true)
    expect(push).toHaveBeenCalledWith('/login')
  })

  it('register failure returns false', async () => {
    mockRegisterApi.mockRejectedValue(new Error('fail'))
    const { register } = useAuth()
    const result = await register('u', 'p', 'U')
    expect(result).toBe(false)
  })

  it('logout confirms then navigates to login', async () => {
    const { logout } = useAuth()
    await logout()
    expect(push).toHaveBeenCalledWith('/login')
  })
})
