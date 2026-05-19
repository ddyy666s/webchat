/** 用户模块类型定义 @module types/user */

/** 用户实体 */
export interface User {
  /** 用户ID */
  id: number
  /** 用户名 */
  username: string
  /** 昵称 */
  nickname: string
  /** 头像URL */
  avatar: string | null
  /** 个性签名 */
  signature: string | null
  /** 角色：user-普通用户 admin-管理员 */
  role: 'user' | 'admin'
  /** 状态：0-禁用 1-启用 */
  status: number
  /** 创建时间 */
  createdAt: string
  /** 更新时间 */
  updatedAt: string
}

/** 登录参数 */
export interface LoginParams {
  /** 用户名 */
  username: string
  /** 密码 */
  password: string
}

/** 注册参数 */
export interface RegisterParams {
  /** 用户名 */
  username: string
  /** 密码 */
  password: string
  /** 昵称 */
  nickname: string
}

/** 登录响应 */
export interface LoginResponse {
  /** JWT Token */
  token: string
  /** 用户信息 */
  user: User
}

/** 用户信息响应 */
export interface UserInfoResponse {
  /** 用户ID */
  id: number
  /** 用户名 */
  username: string
  /** 昵称 */
  nickname: string
  /** 头像URL */
  avatar: string | null
  /** 个性签名 */
  signature: string | null
  /** 角色 */
  role: string
}
