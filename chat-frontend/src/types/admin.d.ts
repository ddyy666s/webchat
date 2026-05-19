/** 管理员模块类型定义 @module types/admin */

/** 用户管理视图对象 */
export interface UserManageVO {
  /** 用户ID */
  id: number
  /** 用户名 */
  username: string
  /** 昵称 */
  nickname: string
  /** 角色 */
  role: string
  /** 状态：0-禁用 1-启用 */
  status: number
  /** 创建时间 */
  createdAt: string
}

/** 消息审计视图对象 */
export interface MessageAuditVO {
  /** 消息ID */
  id: number
  /** 发送者用户ID */
  fromUserId: number
  /** 发送者昵称 */
  fromUserNickname: string
  /** 接收者用户ID */
  toUserId: number
  /** 接收者昵称 */
  toUserNickname: string
  /** 消息内容 */
  content: string
  /** 发送时间 */
  sendTime: string
  /** 消息类型 */
  messageType: number
}

/** 统计信息视图对象 */
export interface StatisticsVO {
  /** 用户总数 */
  totalUsers: number
  /** 今日活跃用户数 */
  todayActiveUsers: number
  /** 今日消息数 */
  todayMessages: number
  /** 当前在线用户数 */
  onlineUsers: number
}

/** 用户列表响应 */
export interface UserListResponse {
  /** 总记录数 */
  total: number
  /** 用户列表 */
  records: UserManageVO[]
}

/** 消息审计响应 */
export interface MessageAuditResponse {
  /** 总记录数 */
  total: number
  /** 消息列表 */
  records: MessageAuditVO[]
}
