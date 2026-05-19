/** 好友模块类型定义 @module types/friend */

/** 好友视图对象 */
export interface FriendVO {
  /** 关系ID */
  id: number
  /** 好友用户ID */
  userId: number
  /** 好友昵称 */
  nickname: string
  /** 好友头像 */
  avatar: string | null
  /** 好友签名 */
  signature: string | null
  /** 好友备注 */
  remark: string | null
  /** 所在分组名称 */
  groupName: string
  /** 是否在线 */
  isOnline: boolean
  /** 未读消息数 */
  unreadCount: number
}

/** 好友分组视图对象 */
export interface FriendGroupVO {
  /** 分组名称 */
  groupName: string
  /** 该分组下的好友列表 */
  friends: FriendVO[]
}

/** 好友申请视图对象 */
export interface FriendRequestVO {
  /** 申请ID */
  id: number
  /** 申请者用户ID */
  fromUserId: number
  /** 申请者昵称 */
  fromUserNickname: string
  /** 申请者头像 */
  fromUserAvatar: string | null
  /** 申请附言 */
  message: string | null
  /** 申请状态：0-待处理 1-已同意 2-已拒绝 */
  status: number
  /** 创建时间 */
  createdAt: string
}

/** 搜索用户视图对象 */
export interface SearchUserVO {
  /** 用户ID */
  userId: number
  /** 用户昵称 */
  nickname: string
  /** 用户头像 */
  avatar: string | null
  /** 用户签名 */
  signature: string | null
  /** 当前用户对该用户的备注 */
  remark: string | null
  /** 是否在线 */
  isOnline: boolean
}

/** 发送好友申请参数 */
export interface SendFriendRequestParams {
  /** 目标用户ID */
  toUserId: number
  /** 附言 */
  message?: string
}

/** 处理好友申请参数 */
export interface HandleFriendRequestParams {
  /** 处理状态：1-同意 2-拒绝 */
  status: number
}

/** 移动好友分组参数 */
export interface MoveFriendGroupParams {
  /** 目标分组名称 */
  groupName: string
}
