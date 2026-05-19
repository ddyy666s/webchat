<template>
  <div class="message-list">
    <template v-if="groupedMessages.length > 0">
      <template v-for="group in groupedMessages" :key="group.date">
        <div class="date-divider">
          <span>{{ group.date }}</span>
        </div>

        <MessageItem v-for="msg in group.messages" :key="msg.id" :message="msg" @click="handleClick" />
      </template>
    </template>

    <Empty v-else title="暂无未读消息" description="快去和好友聊天吧" />
  </div>
</template>

<script setup lang="ts">
/** 未读消息列表组件，按日期分组展示 @component */
import { computed } from 'vue'
import Empty from '@/components/common/Empty.vue'
import MessageItem from './MessageItem.vue'

/** 消息数据结构定义 */
export interface Message {
  id: number
  fromUserId: number
  fromUserNickname: string
  fromUserAvatar?: string
  content: string
  messageType: number
  sendTime: string
  unreadCount?: number
  isOnline?: boolean
}

/** 组件属性：消息列表 */
const props = defineProps<{
  messages: Message[]
}>()

/** 组件事件：点击消息 */
const emit = defineEmits<{
  click: [userId: number]
}>()

/** 按日期分组的消息列表 @returns 按日期分组后的消息数组 */
const groupedMessages = computed(() => {
  const groups: { date: string; messages: Message[] }[] = []
  const today = new Date().toDateString()
  const yesterday = new Date(Date.now() - 86400000).toDateString()

  for (const msg of props.messages) {
    const msgDate = new Date(msg.sendTime).toDateString()
    let dateLabel = ''

    if (msgDate === today) {
      dateLabel = '今天'
    } else if (msgDate === yesterday) {
      dateLabel = '昨天'
    } else {
      dateLabel = msg.sendTime.substring(0, 10)
    }

    const existingGroup = groups.find(g => g.date === dateLabel)
    if (existingGroup) {
      existingGroup.messages.push(msg)
    } else {
      groups.push({ date: dateLabel, messages: [msg] })
    }
  }

  return groups
})

/** 点击消息处理 @param userId 好友用户 ID @returns void */
const handleClick = (userId: number) => {
  emit('click', userId)
}
</script>

<style scoped>
.message-list {
  height: 100%;
  overflow-y: auto;
  padding: 0 12px;
}

.date-divider {
  text-align: center;
  margin: 18px 0;
  position: relative;
  display: flex;
  align-items: center;
  gap: 12px;
}

.date-divider::before,
.date-divider::after {
  content: '';
  flex: 1;
  height: 1px;
  background: linear-gradient(90deg, transparent, var(--border-color), transparent);
}

.date-divider span {
  font-size: 12px;
  color: var(--text-secondary);
  background: var(--bg-color);
  padding: 4px 16px;
  border-radius: 20px;
  font-weight: 500;
  flex-shrink: 0;
}
</style>
