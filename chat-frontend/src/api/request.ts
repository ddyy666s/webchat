/** Axios HTTP 请求封装模块 @module request */
import axios from 'axios'
import type { AxiosInstance, InternalAxiosRequestConfig } from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/userStore'

/** API 通用响应结构 */
interface ApiResponse<T = any> { code: number; message: string; data: T }

/** 已配置的 Axios 请求实例 */
const request: AxiosInstance = axios.create({
  baseURL: '/api', timeout: 10000, headers: { 'Content-Type': 'application/json' }
})

/** 请求拦截器：自动注入 Token */
request.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = useUserStore().token
    if (token && config.headers) config.headers.Authorization = `Bearer ${token}`
    return config
  },
  (error) => Promise.reject(error)
)

/** 响应拦截器：统一处理状态码和错误提示 */
request.interceptors.response.use(
  (response): any => {
    const { code, message, data } = response.data as ApiResponse
    if (code === 200) return data
    if (code === 1005) {
      ElMessage.error('登录已过期，请重新登录')
      useUserStore().logout()
      window.location.href = '/login'
      return Promise.reject(new Error(message))
    }
    ElMessage.error(message || '请求失败')
    return Promise.reject(new Error(message))
  },
  (error) => {
    if (error.response) {
      const { status } = error.response
      if (status === 401) { useUserStore().logout(); window.location.href = '/login' }
      else if (status === 403) ElMessage.error('无权限访问')
      else if (status === 404) ElMessage.error('请求资源不存在')
      else ElMessage.error(error.message || '网络错误')
    } else ElMessage.error('网络连接失败')
    return Promise.reject(error)
  }
)

export default request
