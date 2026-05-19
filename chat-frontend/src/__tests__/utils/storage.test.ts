import { describe, it, expect, beforeEach } from 'vitest'
import { storage } from '@/utils/storage'

describe('storage', () => {
  beforeEach(() => { localStorage.clear() })

  it('set stores JSON-serialized value with prefix', () => {
    storage.set('key1', { a: 1 })
    expect(localStorage.getItem('chat_key1')).toBe('{"a":1}')
  })

  it('get retrieves and parses stored value', () => {
    localStorage.setItem('chat_key1', '{"a":1}')
    expect(storage.get<{ a: number }>('key1')).toEqual({ a: 1 })
  })

  it('get returns default value for missing key', () => {
    expect(storage.get('nonexistent', 'default')).toBe('default')
  })

  it('get returns null for missing key without default', () => {
    expect(storage.get('nonexistent')).toBeNull()
  })

  it('remove deletes key with prefix', () => {
    localStorage.setItem('chat_key1', 'value')
    storage.remove('key1')
    expect(localStorage.getItem('chat_key1')).toBeNull()
  })

  it('clear removes only prefixed keys', () => {
    localStorage.setItem('chat_key1', 'v1')
    localStorage.setItem('chat_key2', 'v2')
    localStorage.setItem('other', 'v3')
    storage.clear()
    expect(localStorage.getItem('chat_key1')).toBeNull()
    expect(localStorage.getItem('chat_key2')).toBeNull()
    expect(localStorage.getItem('other')).toBe('v3')
  })
})
