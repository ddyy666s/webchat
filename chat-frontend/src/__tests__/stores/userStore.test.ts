import { describe, it, expect, vi, beforeEach } from 'vitest'

vi.unmock('@/stores/userStore')

import { setActivePinia, createPinia } from 'pinia'
import { useUserStore } from '@/stores/userStore'

describe('userStore', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    localStorage.clear()
  })

  it('starts with null token and null userInfo', () => {
    const store = useUserStore()
    expect(store.token).toBeNull()
    expect(store.userInfo).toBeNull()
  })

  it('setToken updates token', () => {
    const store = useUserStore()
    store.setToken('abc123')
    expect(store.token).toBe('abc123')
  })

  it('setUserInfo updates userInfo', () => {
    const store = useUserStore()
    const info = { id: 1, username: 'test', nickname: 'Test', avatar: null, signature: '', role: 'user' as const }
    store.setUserInfo(info)
    expect(store.userInfo).toEqual(info)
  })

  it('logout clears token and userInfo', () => {
    const store = useUserStore()
    store.setToken('abc')
    store.setUserInfo({ id: 1, username: 'test', nickname: 'Test', avatar: null, signature: '', role: 'user' as const })
    store.logout()
    expect(store.token).toBeNull()
    expect(store.userInfo).toBeNull()
  })

  it('isLoggedIn returns false without token', () => {
    const store = useUserStore()
    expect(store.isLoggedIn()).toBe(false)
  })

  it('isLoggedIn returns true with token', () => {
    const store = useUserStore()
    store.setToken('abc')
    expect(store.isLoggedIn()).toBe(true)
  })

  it('isAdmin returns true for admin role', () => {
    const store = useUserStore()
    store.setUserInfo({ id: 1, username: 'admin', nickname: 'Admin', avatar: null, signature: '', role: 'admin' as const })
    expect(store.isAdmin()).toBe(true)
  })

  it('isAdmin returns false for user role', () => {
    const store = useUserStore()
    store.setUserInfo({ id: 1, username: 'user', nickname: 'User', avatar: null, signature: '', role: 'user' as const })
    expect(store.isAdmin()).toBe(false)
  })
})
