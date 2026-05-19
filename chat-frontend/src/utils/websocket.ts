/** WebSocket 服务单例 @module websocket */
import { useUserStore } from '@/stores/userStore'
import {
  messageCallbacks, statusCallbacks, groupMessageCallbacks,
  callSignalCallbacks, notificationCallbacks,
  friendRequestCallbacks, friendRequestHandledCallbacks,
  onMessage, onStatus, onGroupMessage, onCallSignal,
  onNotification, onFriendRequest, onFriendRequestHandled
} from './websocketCallbacks'

export { onMessage, onStatus, onGroupMessage, onCallSignal, onNotification, onFriendRequest, onFriendRequestHandled }

/** WebSocket 服务类（单例模式） */
class WebSocketService {
  private ws: WebSocket | null = null
  private reconnectTimer: number | null = null
  private heartbeatTimer: number | null = null

  /** 是否已连接 @returns 连接状态 */
  isConnected(): boolean { return this.ws !== null && this.ws.readyState === WebSocket.OPEN }

  /** 建立 WebSocket 连接 */
  connect() {
    const userStore = useUserStore()
    const token = userStore.token
    if (!token) { console.warn('未登录，无法连接WebSocket'); return }
    const cleanToken = token.replace(/"/g, '')
    const wsBaseUrl = import.meta.env.VITE_WS_URL || `${location.protocol === 'https:' ? 'wss:' : 'ws:'}//${location.host}/ws`
    this.ws = new WebSocket(`${wsBaseUrl}?token=${cleanToken}`)
    this.ws.onopen = () => { this.startHeartbeat(); if (this.reconnectTimer) clearTimeout(this.reconnectTimer) }
    this.ws.onmessage = (event) => {
      const data = JSON.parse(event.data)
      if (data.type === 'message' || data.type === 'impression') messageCallbacks.forEach(cb => cb(data))
      else if (data.type === 'status') statusCallbacks.forEach(cb => cb(data))
      else if (data.type === 'group_message') groupMessageCallbacks.forEach(cb => cb(data))
      else if (data.type === 'call') callSignalCallbacks.forEach(cb => cb(data))
      else if (data.type === 'notification') notificationCallbacks.forEach(cb => cb(data))
      else if (data.type === 'friend_request') friendRequestCallbacks.forEach(cb => cb(data))
      else if (data.type === 'friend_request_handled') friendRequestHandledCallbacks.forEach(cb => cb(data))
    }
    this.ws.onclose = () => { this.stopHeartbeat(); this.reconnect() }
    this.ws.onerror = (error) => console.error('WebSocket错误', error)
  }

  /** 自动重连（3秒后） */
  private reconnect() {
    if (this.reconnectTimer) return
    this.reconnectTimer = setTimeout(() => { this.reconnectTimer = null; this.connect() }, 3000) as unknown as number
  }

  /** 开始心跳（每30秒发送ping） */
  private startHeartbeat() {
    this.heartbeatTimer = setInterval(() => {
      if (this.ws && this.ws.readyState === WebSocket.OPEN) this.ws.send(JSON.stringify({ type: 'ping' }))
    }, 30000) as unknown as number
  }

  /** 停止心跳 */
  private stopHeartbeat() { if (this.heartbeatTimer) { clearInterval(this.heartbeatTimer); this.heartbeatTimer = null } }

  /** 发送单聊消息 @param toUserId 目标用户ID @param content 消息内容 @param messageType 消息类型 @param duration 语音时长 */
  sendMessage(toUserId: number, content: string, messageType = 1, duration?: number) {
    if (this.ws && this.ws.readyState === WebSocket.OPEN) {
      const msg: any = { type: 'message', toUserId, content, messageType }
      if (duration !== undefined && messageType === 4) msg.duration = duration
      this.ws.send(JSON.stringify(msg))
    } else console.warn('WebSocket未连接')
  }

  /** 发送群消息 @param groupId 群ID @param content 消息内容 @param messageType 消息类型 */
  sendGroupMessage(groupId: number, content: string, messageType = 1) {
    if (this.ws && this.ws.readyState === WebSocket.OPEN) this.ws.send(JSON.stringify({ type: 'group_message', groupId, content, messageType }))
    else console.warn('WebSocket未连接')
  }

  /** 发送通话信令 @param data 信令数据 */
  sendCallSignal(data: any) {
    if (this.ws && this.ws.readyState === WebSocket.OPEN) this.ws.send(JSON.stringify({ type: 'call', ...data }))
    else console.warn('WebSocket未连接，无法发送通话信令')
  }

  /** 断开连接 */
  disconnect() { this.stopHeartbeat(); if (this.ws) { this.ws.close(); this.ws = null } }
}

/** WebSocket 服务单例导出 */
export const websocketService = new WebSocketService()
