/** 好友模块API @module friend */
import request from '../request'

/** 好友视图对象 */
export interface FriendVO {
  /** 关系ID */ id: number
  /** 好友用户ID */ userId: number
  /** 好友昵称 */ nickname: string
  /** 好友头像 */ avatar: string | null
  /** 好友签名 */ signature: string | null
  /** 好友备注 */ remark: string | null
  /** 所在分组名称 */ groupName: string
  /** 是否在线 */ isOnline: boolean
  /** 未读消息数 */ unreadCount: number
}
/** 好友分组视图对象 */
export interface FriendGroupVO {
  /** 分组名称 */ groupName: string
  /** 该分组下的好友列表 */ friends: FriendVO[]
}
/** 好友申请视图对象 */
export interface FriendRequestVO {
  /** 申请ID */ id: number
  /** 申请者用户ID */ fromUserId: number
  /** 申请者昵称 */ fromUserNickname: string
  /** 申请者头像 */ fromUserAvatar: string | null
  /** 申请附言 */ message: string | null
  /** 申请状态：0-待处理 1-已同意 2-已拒绝 */ status: number
  /** 创建时间 */ createdAt: string
}
/** 搜索用户视图对象 */
export interface SearchUserVO {
  /** 用户ID */ userId: number
  /** 用户昵称 */ nickname: string
  /** 用户头像 */ avatar: string | null
  /** 用户签名 */ signature: string | null
  /** 当前用户对该用户的备注 */ remark: string | null
  /** 是否在线 */ isOnline: boolean
}
/** 搜索用户 @param keyword 搜索关键词 @returns 搜索结果列表 */
export const searchUsersApi = (keyword: string) => request.get<any, SearchUserVO[]>('/friend/search', { params: { keyword } })
/** 发送好友申请 @param toUserId 目标用户ID @param message 附言 @returns 操作结果 */
export const sendFriendRequestApi = (toUserId: number, message?: string) => request.post('/friend/request', { toUserId, message })
/** 获取好友申请列表 @returns 好友申请列表 */
export const getFriendRequestsApi = () => request.get<any, FriendRequestVO[]>('/friend/requests')
/** 处理好友申请 @param requestId 申请ID @param status 处理状态（1-同意 2-拒绝） @returns 操作结果 */
export const handleFriendRequestApi = (requestId: number, status: number) => request.put(`/friend/request/${requestId}`, { status })
/** 获取好友列表（按分组） @returns 分组好友列表 */
export const getFriendListApi = () => request.get<any, FriendGroupVO[]>('/friend/list')
/** 删除好友 @param friendId 好友关系ID @returns 操作结果 */
export const deleteFriendApi = (friendId: number) => request.delete(`/friend/${friendId}`)
/** 移动好友分组 @param friendId 好友关系ID @param groupName 目标分组名称 @returns 操作结果 */
export const moveFriendGroupApi = (friendId: number, groupName: string) => request.put(`/friend/${friendId}/group`, { groupName })
/** 更新好友备注 @param friendId 好友关系ID @param remark 新备注 @returns 操作结果 */
export const updateFriendRemarkApi = (friendId: number, remark: string) => request.put(`/friend/${friendId}/remark`, null, { params: { remark } })
