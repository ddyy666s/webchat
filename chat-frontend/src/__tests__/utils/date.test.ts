import { describe, it, expect } from 'vitest'
import { formatDate, formatRelativeTime, isToday, formatVoiceDuration, formatDuration } from '@/utils/date'

describe('date utils', () => {
  it('formatDate formats with default pattern', () => {
    expect(formatDate('2024-01-15T10:30:00')).toMatch(/2024-01-15/)
  })

  it('formatDate formats with custom pattern', () => {
    expect(formatDate('2024-01-15T10:30:00', 'YYYY-MM')).toBe('2024-01')
  })

  it('formatDate handles Date object', () => {
    expect(formatDate(new Date('2024-06-01'), 'YYYY/MM/DD')).toBe('2024/06/01')
  })

  it('formatRelativeTime returns 刚刚 for less than 1 minute', () => {
    expect(formatRelativeTime(new Date())).toBe('刚刚')
  })

  it('formatRelativeTime returns X分钟前', () => {
    const date = new Date(Date.now() - 5 * 60 * 1000)
    expect(formatRelativeTime(date)).toBe('5分钟前')
  })

  it('formatRelativeTime returns X小时前', () => {
    const date = new Date(Date.now() - 3 * 60 * 60 * 1000)
    expect(formatRelativeTime(date)).toMatch(/小时前/)
  })

  it('formatRelativeTime returns MM-DD HH:mm for older dates', () => {
    const date = new Date('2023-01-01T12:00:00')
    expect(formatRelativeTime(date)).toMatch(/01-01/)
  })

  it('isToday returns true for today', () => {
    expect(isToday(new Date())).toBe(true)
  })

  it('isToday returns false for yesterday', () => {
    const yesterday = new Date(Date.now() - 86400000)
    expect(isToday(yesterday)).toBe(false)
  })

  it('formatVoiceDuration returns default for undefined', () => {
    expect(formatVoiceDuration()).toBe('0:05')
  })

  it('formatVoiceDuration returns 0:05 for zero', () => {
    expect(formatVoiceDuration(0)).toBe('0:05')
  })

  it('formatVoiceDuration formats seconds to mm:ss', () => {
    expect(formatVoiceDuration(65)).toBe('1:05')
  })

  it('formatDuration pads single digit minutes', () => {
    expect(formatDuration(5)).toBe('00:05')
  })

  it('formatDuration formats minutes and seconds', () => {
    expect(formatDuration(125)).toBe('02:05')
  })
})
