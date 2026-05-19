<template>
  <div class="message-item" :class="{ 'is-impression': message.messageType === 3 }" @click="$emit('click', message.fromUserId)">
    <div class="avatar-wrapper">
      <el-avatar :size="48" :src="message.fromUserAvatar || ''">{{ message.fromUserNickname?.charAt(0) || 'U' }}</el-avatar>
      <span class="online-dot" v-if="message.isOnline"></span>
    </div>
    <div class="message-content">
      <div class="message-header">
        <span class="name">{{ message.fromUserNickname }}</span>
        <span class="time">{{ formatTime(message.sendTime) }}</span>
      </div>
      <div class="message-preview">
        <span class="preview-icon-wrap" :class="message.messageType === 3 ? 'impression' : 'message'">
          <el-icon v-if="message.messageType === 3" class="preview-icon"><Star /></el-icon>
          <el-icon v-else class="preview-icon"><ChatDotRound /></el-icon>
        </span>
        <span class="content">{{ message.content }}</span>
      </div>
    </div>
    <div class="message-badge"><el-badge :value="message.unreadCount || 1" :hidden="false" /></div>
  </div>
</template>

<script setup lang="ts">
/** 消息盒子中的消息项组件 @component */
import { Star, ChatDotRound } from '@element-plus/icons-vue'
import { formatRelativeTime } from '@/utils/date'

interface Message { id: number; fromUserId: number; fromUserNickname: string; fromUserAvatar?: string; content: string; messageType: number; sendTime: string; unreadCount?: number; isOnline?: boolean }

const props = defineProps<{ message: Message }>()
defineEmits<{ click: [userId: number] }>()

const formatTime = (time: string) => {
  const date = new Date(time); const now = new Date(); const isToday = date.toDateString() === now.toDateString()
  if (isToday) return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
  return formatRelativeTime(time)
}
</script>

<style scoped>
.message-item { display: flex; gap: 14px; padding: 14px 14px; border-radius: 14px; margin-bottom: 8px; cursor: pointer; transition: all 0.25s; background: var(--bg-color-white); border: 1px solid var(--border-color-extra-light); position: relative; animation: slideInItem 0.35s ease both; }
@keyframes slideInItem { from { opacity: 0; transform: translateX(-12px); } to { opacity: 1; transform: translateX(0); } }
.message-item:hover { background: #f3f0ff; border-color: var(--color-primary-light); transform: translateX(4px); box-shadow: 0 4px 16px rgba(108,92,231,0.08); }
.message-item.is-impression { background: linear-gradient(135deg,#fef9e7,#fef3d5); border-color: #ffe7ba; }
.message-item.is-impression:hover { background: linear-gradient(135deg,#fef3d5,#fdecc0); box-shadow: 0 4px 16px rgba(230,162,60,0.12); }
.avatar-wrapper { position: relative; flex-shrink: 0; }
.online-dot { position: absolute; bottom: 2px; right: 2px; width: 10px; height: 10px; border-radius: 50%; background: var(--color-success); border: 2px solid var(--bg-color-white); box-shadow: 0 1px 3px rgba(0,184,148,0.3); }
.message-content { flex: 1; min-width: 0; }
.message-header { display: flex; justify-content: space-between; align-items: baseline; margin-bottom: 6px; }
.message-header .name { font-size: 15px; font-weight: 600; color: var(--text-primary); }
.message-header .time { font-size: 11px; color: var(--text-secondary); }
.message-preview { display: flex; align-items: center; gap: 8px; overflow: hidden; }
.preview-icon-wrap { width: 28px; height: 28px; border-radius: 8px; display: flex; align-items: center; justify-content: center; flex-shrink: 0; transition: all 0.25s; }
.preview-icon-wrap.message { background: #f3f0ff; }
.preview-icon-wrap.impression { background: #fef3d5; }
.message-item:hover .preview-icon-wrap.message { background: linear-gradient(135deg, var(--color-primary), var(--color-primary-dark)); transform: scale(1.1); }
.message-item:hover .preview-icon-wrap.impression { background: linear-gradient(135deg, var(--color-warning), #f0a830); transform: scale(1.1); }
.preview-icon { font-size: 14px; transition: color 0.25s; }
.preview-icon-wrap.message .preview-icon { color: var(--color-primary); }
.preview-icon-wrap.impression .preview-icon { color: #b8860b; }
.message-item:hover .preview-icon { color: white; }
.message-preview .content { font-size: 13px; color: var(--text-regular); overflow: hidden; text-overflow: ellipsis; white-space: nowrap; flex: 1; }
.message-badge { flex-shrink: 0; margin-left: 8px; }
.message-badge :deep(.el-badge__content) { border: 2px solid var(--bg-color-white) !important; }
</style>
