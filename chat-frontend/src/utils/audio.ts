/** 音频播放工具模块 @module audio */

/** 当前播放的音频元素 */
let audioElement: HTMLAudioElement | null = null

/** 播放语音消息 @param url 语音文件URL */
export const playVoice = (url: string) => {
  if (audioElement) { audioElement.pause(); audioElement = null }
  audioElement = new Audio(url)
  audioElement.play().catch(() => {})
}

/** 停止播放语音 */
export const stopVoice = () => {
  if (audioElement) { audioElement.pause(); audioElement = null }
}

/** 是否启用音效 */
let soundEnabled = true

/** 设置音效开关 @param v 是否启用 */
export const setSoundEnabled = (v: boolean) => { soundEnabled = v }

/** 通知音效URL（缓存） */
let _noticeUrl: string | null = null
/** 铃声URL（缓存） */
let _ringUrl: string | null = null

/** 获取通知音效URL @returns 通知音效URL */
function getNoticeUrl(): string {
  if (_noticeUrl === null) {
    try { _noticeUrl = new URL('../assets/audio/notice.MP3', import.meta.url).href }
    catch { _noticeUrl = '' }
  }
  return _noticeUrl
}

/** 获取铃声URL @returns 铃声URL */
function getRingUrl(): string {
  if (_ringUrl === null) {
    try { _ringUrl = new URL('../assets/audio/ring.MP3', import.meta.url).href }
    catch { _ringUrl = '' }
  }
  return _ringUrl
}

/** 播放通知提示音 */
export const playNotificationSound = () => {
  if (!soundEnabled) return
  const url = getNoticeUrl()
  if (!url) return
  try { const a = new Audio(url); a.volume = 0.5; a.play().catch(() => {}) } catch {}
}

/** 播放来电铃声 @returns 停止铃声的函数 */
export const playRingtone = () => {
  if (!soundEnabled) return () => {}
  const url = getRingUrl()
  if (!url) return () => {}
  try {
    const a = new Audio(url); a.loop = true; a.volume = 0.8; a.play().catch(() => {})
    return () => { a.pause(); a.loop = false }
  } catch { return () => {} }
}
