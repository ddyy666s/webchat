/** API 通用类型定义 @module types/api */

/** 通用 API 响应结构 */
export interface ApiResponse<T = any> {
  /** 状态码 */
  code: number
  /** 提示消息 */
  message: string
  /** 响应数据 */
  data: T
}

/** 通用分页结果 */
export interface PageResult<T = any> {
  /** 总记录数 */
  total: number
  /** 当前页数据列表 */
  records: T[]
}

/** 错误响应结构 */
export interface ErrorResponse {
  /** 错误码 */
  code: number
  /** 错误消息 */
  message: string
}
