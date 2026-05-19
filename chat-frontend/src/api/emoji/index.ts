/** 表情模块API @module emoji */
import request from '../request'

/** 表情视图对象 */
export interface EmojiVO {
  id: number; name: string; url: string; category: string
  userId?: number; isSystem: boolean; createdAt: string
}
/** 获取系统表情包 @returns 系统表情列表 */
export const getSystemEmojisApi = () => request.get<any, EmojiVO[]>('/emoji/system')
/** 获取用户自定义表情包 @returns 用户自定义表情列表 */
export const getUserEmojisApi = () => request.get<any, EmojiVO[]>('/emoji/user')
/** 上传自定义表情 @param file 表情文件 @param name 表情名称 @param category 表情分类 @returns 上传后的表情对象 */
export const uploadEmojiApi = (file: File, name: string, category?: string) => {
  const fd = new FormData(); fd.append('file', file); fd.append('name', name)
  if (category) fd.append('category', category)
  return request.post<any, EmojiVO>('/emoji/upload', fd, { headers: { 'Content-Type': 'multipart/form-data' } })
}
/** 删除自定义表情 @param emojiId 表情ID @returns 操作结果 */
export const deleteEmojiApi = (emojiId: number) => request.delete(`/emoji/${emojiId}`)
