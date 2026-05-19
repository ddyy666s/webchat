/** 印象（评价）模块API @module impression */
import request from '../request'

/** 印象/评价视图对象 */
export interface ImpressionVO {
  id: number; fromUserId: number; fromUserNickname: string
  fromUserAvatar: string | null; toUserId: number; toUserNickname: string
  toUserAvatar: string | null; content: string; createdAt: string
}
/** 添加印象/评价参数 */
export interface AddImpressionParams { toUserId: number; content: string }

/** 添加评价 @param toUserId 被评价用户ID @param content 评价内容 @returns 操作结果 */
export const addImpressionApi = (toUserId: number, content: string) => request.post('/impression', { toUserId, content })
/** 获取对我的评价 @returns 我收到的评价列表 */
export const getImpressionsToMeApi = () => request.get<any, ImpressionVO[]>('/impression/to-me')
/** 获取我给出的评价 @returns 我给出的评价列表 */
export const getImpressionsByMeApi = () => request.get<any, ImpressionVO[]>('/impression/by-me')
/** 删除评价 @param impressionId 评价ID @returns 操作结果 */
export const deleteImpressionApi = (impressionId: number) => request.delete(`/impression/${impressionId}`)
