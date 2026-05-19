/** 用户状态管理 @module userStore */
import { defineStore } from 'pinia'
import { ref } from 'vue'
import { storage } from '@/utils/storage'

/** 用户信息类型 */
export interface UserInfo {
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
}

/** 用户认证相关状态 store */
export const useUserStore = defineStore('user', () => {
  /** JWT Token */
  const token = ref<string | null>(storage.get<string>('token'))
  /** 用户信息 */
  const userInfo = ref<UserInfo | null>(storage.get<UserInfo>('userInfo'))

  /** 设置 Token @param newToken JWT Token */
  const setToken = (newToken: string) => { token.value = newToken; storage.set('token', newToken) }

  /** 设置用户信息 @param info 用户信息对象 */
  const setUserInfo = (info: UserInfo) => { userInfo.value = info; storage.set('userInfo', info) }

  /** 退出登录（清除 Token 和用户信息） */
  const logout = () => { token.value = null; userInfo.value = null; storage.clear() }

  /** 是否已登录 @returns 是否已登录 */
  const isLoggedIn = () => !!token.value

  /** 是否为管理员 @returns 是否为管理员 */
  const isAdmin = () => userInfo.value?.role === 'admin'

  return { token, userInfo, setToken, setUserInfo, logout, isLoggedIn, isAdmin }
})
