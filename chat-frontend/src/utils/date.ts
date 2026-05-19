/** 日期时间格式化工具模块 @module date */
import dayjs from 'dayjs'
import relativeTime from 'dayjs/plugin/relativeTime'
import 'dayjs/locale/zh-cn'

dayjs.extend(relativeTime)
dayjs.locale('zh-cn')

/** 格式化日期 @param date 日期 @param format 格式模板 @returns 格式化后的日期字符串 */
export const formatDate = (date: string | Date, format = 'YYYY-MM-DD HH:mm:ss'): string => dayjs(date).format(format)

/** 格式化相对时间（如：刚刚、5分钟前） @param date 日期 @returns 相对时间字符串 */
export const formatRelativeTime = (date: string | Date): string => {
  const now = dayjs(); const target = dayjs(date); const diffMinutes = now.diff(target, 'minute')
  if (diffMinutes < 1) return '刚刚'
  if (diffMinutes < 60) return `${diffMinutes}分钟前`
  if (diffMinutes < 1440) return `${Math.floor(diffMinutes / 60)}小时前`
  return target.format('MM-DD HH:mm')
}

/** 判断是否为今天 @param date 日期 @returns 是否为今天 */
export const isToday = (date: string | Date): boolean => dayjs(date).isSame(dayjs(), 'day')

/** 格式化语音时长（秒 -> mm:ss） @param seconds 秒数 @returns 格式化后的时长字符串 */
export const formatVoiceDuration = (seconds?: number): string => {
  if (!seconds || seconds <= 0) return '0:05'
  const mins = Math.floor(seconds / 60); const secs = seconds % 60
  return `${mins}:${secs.toString().padStart(2, '0')}`
}

/** 通用时长格式化 @param seconds 秒数 @returns mm:ss 格式时长 */
export const formatDuration = (seconds: number): string => {
  const mins = Math.floor(seconds / 60); const secs = seconds % 60
  return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
}
