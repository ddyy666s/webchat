import { describe, it, expect, vi, beforeEach } from 'vitest'

const { mockElMessage } = vi.hoisted(() => ({
  mockElMessage: { success: vi.fn(), warning: vi.fn(), error: vi.fn(), info: vi.fn() }
}))

vi.mock('element-plus', () => ({ ElMessage: mockElMessage }))

import { notify } from '@/utils/notify'

describe('notify', () => {
  beforeEach(() => { vi.clearAllMocks() })

  it('notify.success calls ElMessage.success', () => {
    notify.success('操作成功')
    expect(mockElMessage.success).toHaveBeenCalledWith('操作成功')
  })

  it('notify.warning calls ElMessage.warning', () => {
    notify.warning('请谨慎操作')
    expect(mockElMessage.warning).toHaveBeenCalledWith('请谨慎操作')
  })

  it('notify.error calls ElMessage.error', () => {
    notify.error('操作失败')
    expect(mockElMessage.error).toHaveBeenCalledWith('操作失败')
  })

  it('notify.info calls ElMessage.info', () => {
    notify.info('这是一条信息')
    expect(mockElMessage.info).toHaveBeenCalledWith('这是一条信息')
  })

  it('notify.success returns the result of ElMessage.success', () => {
    const expected = { close: vi.fn() }
    mockElMessage.success.mockReturnValue(expected)
    const result = notify.success('ok')
    expect(result).toBe(expected)
  })

  it('all notify methods exist and are functions', () => {
    expect(typeof notify.success).toBe('function')
    expect(typeof notify.warning).toBe('function')
    expect(typeof notify.error).toBe('function')
    expect(typeof notify.info).toBe('function')
  })
})
