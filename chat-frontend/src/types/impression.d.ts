/** 印象（评价）模块类型定义 @module types/impression */

/** 印象/评价视图对象 */
export interface ImpressionVO {
  /** 评价ID */
  id: number
  /** 评价者用户ID */
  fromUserId: number
  /** 评价者昵称 */
  fromUserNickname: string
  /** 评价者头像 */
  fromUserAvatar: string | null
  /** 被评价者用户ID */
  toUserId: number
  /** 被评价者昵称 */
  toUserNickname: string
  /** 被评价者头像 */
  toUserAvatar: string | null
  /** 评价内容 */
  content: string
  /** 创建时间 */
  createdAt: string
}

/** 添加印象/评价参数 */
export interface AddImpressionParams {
  /** 被评价者用户ID */
  toUserId: number
  /** 评价内容 */
  content: string
}
