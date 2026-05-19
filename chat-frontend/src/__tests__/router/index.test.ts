import { describe, it, expect } from 'vitest'
import router from '@/router'
import { routes } from '@/router/routes'

describe('router', () => {
  it('creates router instance', () => {
    expect(router).toBeDefined()
  })

  it('contains all expected route names', () => {
    expect(router.hasRoute('Login')).toBe(true)
    expect(router.hasRoute('Register')).toBe(true)
    expect(router.hasRoute('Main')).toBe(true)
    expect(router.hasRoute('Profile')).toBe(true)
    expect(router.hasRoute('Admin')).toBe(true)
  })

  it('route /login does not require auth', () => {
    const route = routes.find(r => r.path === '/login')
    expect(route?.meta?.requiresAuth).toBe(false)
  })

  it('route / requires auth', () => {
    const route = routes.find(r => r.path === '/')
    expect(route?.meta?.requiresAuth).toBe(true)
  })

  it('route /admin requires auth and admin role', () => {
    const route = routes.find(r => r.path === '/admin')
    expect(route?.meta?.requiresAuth).toBe(true)
    expect(route?.meta?.requiresAdmin).toBe(true)
  })

  it('routes have lazy-loaded components', () => {
    routes.forEach(route => {
      expect(typeof route.component).toBe('function')
    })
  })
})
