/** 管理员API模块 @module admin */
import request from '../request'

/** 用户管理视图对象 */
export interface UserManageVO {
  id: number; username: string; nickname: string; role: string
  status: number; createdAt: string
}
/** 消息审计视图对象 */
export interface MessageAuditVO {
  id: number; fromUserId: number; fromUserNickname: string
  toUserId: number; toUserNickname: string; content: string
  sendTime: string; messageType: number
}
/** 统计信息视图对象 */
export interface StatisticsVO {
  totalUsers: number; todayActiveUsers: number
  todayMessages: number; onlineUsers: number
}
/** 分页结果泛型接口 */
export interface PageResult<T> { total: number; records: T[] }

/** 获取用户列表 @param page 页码 @param size 每页条数 @param keyword 搜索关键词 @returns 分页用户管理列表 */
export const getAdminUsersApi = (page: number, size: number, keyword?: string) => request.get<any, PageResult<UserManageVO>>('/admin/users', { params: { page, size, keyword } })
/** 禁用/启用用户 @param userId 用户ID @param status 状态值 @returns 操作结果 */
export const updateUserStatusApi = (userId: number, status: number) => request.put(`/admin/user/${userId}/status`, null, { params: { status } })
/** 获取聊天记录（审计） @param params 查询参数 @returns 分页消息审计列表 */
export const getAdminMessagesApi = (params: any) => request.get<any, PageResult<MessageAuditVO>>('/admin/messages', { params })
/** 获取统计数据 @returns 统计信息视图对象 */
export const getAdminStatsApi = () => request.get<any, StatisticsVO>('/admin/stats')
