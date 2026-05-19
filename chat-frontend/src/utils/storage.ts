/** 本地存储工具模块（带前缀） @module storage */

/** 存储键名前缀 */
const PREFIX = 'chat_'

/** 本地存储封装（自动添加前缀、序列化/反序列化） */
export const storage = {
  /** 设置值 @param key 键名 @param value 值 */
  set<T>(key: string, value: T): void {
    try { localStorage.setItem(PREFIX + key, JSON.stringify(value)) }
    catch (e) { console.error('存储失败', e) }
  },
  /** 获取值 @param key 键名 @param defaultValue 默认值 @returns 值或默认值 */
  get<T>(key: string, defaultValue?: T): T | null {
    try {
      const data = localStorage.getItem(PREFIX + key)
      if (data === null) return defaultValue || null
      return JSON.parse(data) as T
    } catch (e) { console.error('读取失败', e); return defaultValue || null }
  },
  /** 删除值 @param key 键名 */
  remove(key: string): void { localStorage.removeItem(PREFIX + key) },
  /** 清空所有带前缀的存储项 */
  clear(): void { Object.keys(localStorage).forEach(key => { if (key.startsWith(PREFIX)) localStorage.removeItem(key) }) }
}
