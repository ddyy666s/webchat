import { describe, it, expect, beforeEach } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useAppStore } from '@/stores/appStore'

describe('appStore', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    localStorage.clear()
  })

  it('starts with sidebar expanded', () => {
    const store = useAppStore()
    expect(store.sidebarCollapsed).toBe(false)
  })

  it('toggleSidebar flips sidebarCollapsed', () => {
    const store = useAppStore()
    store.toggleSidebar()
    expect(store.sidebarCollapsed).toBe(true)
    store.toggleSidebar()
    expect(store.sidebarCollapsed).toBe(false)
  })

  it('sets theme and persists to localStorage', () => {
    const store = useAppStore()
    store.setTheme('dark')
    expect(store.theme).toBe('dark')
    expect(localStorage.getItem('chat_theme')).toBe('dark')
  })

  it('initTheme restores theme from localStorage', () => {
    localStorage.setItem('chat_theme', 'dark')
    const store = useAppStore()
    store.initTheme()
    expect(store.theme).toBe('dark')
  })

  it('setGlobalLoading updates loading state', () => {
    const store = useAppStore()
    expect(store.globalLoading).toBe(false)
    store.setGlobalLoading(true)
    expect(store.globalLoading).toBe(true)
    store.setGlobalLoading(false)
    expect(store.globalLoading).toBe(false)
  })
})
