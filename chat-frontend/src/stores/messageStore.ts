/** 消息状态管理 @module messageStore */
import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getUnreadCountApi, type UnreadCountVO } from '@/api/message'

/** 消息相关状态 store */
export const useMessageStore = defineStore('message', () => {
  /** 未读消息计数 */
  const unreadCount = ref<UnreadCountVO | null>(null)

  /** 加载未读计数 */
  const loadUnreadCount = async () => {
    try { const res = await getUnreadCountApi(); unreadCount.value = res }
    catch (error) { console.error('加载未读计数失败', error) }
  }

  /** 清除指定好友的未读计数 @param friendId 好友用户ID */
  const clearUnreadForFriend = (friendId: number) => {
    if (unreadCount.value) {
      unreadCount.value.messages = unreadCount.value.messages.filter(m => m.fromUserId !== friendId)
      const detail = unreadCount.value.details.find(d => d.friendId === friendId)
      if (detail) {
        unreadCount.value.total -= detail.unreadCount
        unreadCount.value.details = unreadCount.value.details.filter(d => d.friendId !== friendId)
      }
    }
  }

  return { unreadCount, loadUnreadCount, clearUnreadForFriend }
})
