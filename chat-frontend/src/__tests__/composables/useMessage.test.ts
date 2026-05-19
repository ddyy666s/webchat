import { describe, it, expect, vi, beforeEach } from 'vitest'

const { mockElMessage, mockElMessageBox } = vi.hoisted(() => ({
  mockElMessage: { success: vi.fn(), error: vi.fn(), warning: vi.fn(), info: vi.fn() },
  mockElMessageBox: { confirm: vi.fn().mockResolvedValue(undefined), alert: vi.fn().mockResolvedValue(undefined), prompt: vi.fn().mockResolvedValue({ value: 'input' }) }
}))

vi.mock('element-plus', () => ({
  ElMessage: mockElMessage,
  ElMessageBox: mockElMessageBox
}))

import { useMessage } from '@/composables/useMessage'

describe('useMessage', () => {
  beforeEach(() => { vi.clearAllMocks() })

  it('success calls ElMessage.success', () => {
    const { success } = useMessage()
    success('ok')
    expect(mockElMessage.success).toHaveBeenCalledWith('ok')
  })

  it('error calls ElMessage.error', () => {
    const { error } = useMessage()
    error('fail')
    expect(mockElMessage.error).toHaveBeenCalledWith('fail')
  })

  it('warning calls ElMessage.warning', () => {
    const { warning } = useMessage()
    warning('warn')
    expect(mockElMessage.warning).toHaveBeenCalledWith('warn')
  })

  it('info calls ElMessage.info', () => {
    const { info } = useMessage()
    info('info')
    expect(mockElMessage.info).toHaveBeenCalledWith('info')
  })

  it('confirm calls ElMessageBox.confirm', async () => {
    const { confirm } = useMessage()
    await confirm('Are you sure?')
    expect(mockElMessageBox.confirm).toHaveBeenCalledWith('Are you sure?', '提示', expect.objectContaining({ confirmButtonText: '确定' }))
  })

  it('confirm with custom title', async () => {
    const { confirm } = useMessage()
    await confirm('Sure?', '自定义标题')
    expect(mockElMessageBox.confirm).toHaveBeenCalledWith('Sure?', '自定义标题', expect.any(Object))
  })

  it('alert calls ElMessageBox.alert', async () => {
    const { alert } = useMessage()
    await alert('Notice')
    expect(mockElMessageBox.alert).toHaveBeenCalledWith('Notice', '提示', expect.objectContaining({ confirmButtonText: '确定' }))
  })

  it('prompt calls ElMessageBox.prompt', async () => {
    const { prompt } = useMessage()
    const result = await prompt('Enter name')
    expect(mockElMessageBox.prompt).toHaveBeenCalledWith('Enter name', '提示', expect.any(Object))
    expect(result).toBe('input')
  })
})
