/** WebSocket 连接管理 composable @module useWebSocket */
import { ref, onUnmounted } from 'vue'
import { websocketService } from '@/utils/websocket'

/** WebSocket 连接与消息收发 hook @returns 连接状态与方法 */
export const useWebSocket = () => {
  /** 连接状态 */
  const isConnected = ref(false)
  /** 消息回调映射表 */
  const messageCallbacks: Map<string, (data: any) => void> = new Map()

  /** 连接 WebSocket */
  const connect = () => { websocketService.connect(); isConnected.value = true }

  /** 发送单聊消息 @param toUserId 目标用户ID @param content 消息内容 @param messageType 消息类型 */
  const sendMessage = (toUserId: number, content: string, messageType = 1) => websocketService.sendMessage(toUserId, content, messageType)

  /** 监听消息 @param type 消息类型 @param callback 回调函数 */
  const onMessage = (type: string, callback: (data: any) => void) => {
    messageCallbacks.set(type, callback)
    if (type === 'message') websocketService.onMessage(callback)
    else if (type === 'status') websocketService.onStatus(callback)
  }

  /** 断开连接 */
  const disconnect = () => { websocketService.disconnect(); isConnected.value = false }

  onUnmounted(() => { disconnect() })

  return { isConnected, connect, sendMessage, onMessage, disconnect }
}
