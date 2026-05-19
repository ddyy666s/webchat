<template>
  <div class="notification-list" v-if="notifications.length > 0">
    <div v-for="n in notifications" :key="n.id" class="notification-item" @click="$emit('open', n)">
      <div class="notif-icon-wrap"><el-icon class="notif-icon"><Bell /></el-icon></div>
      <div class="notif-content">
        <div class="notif-title">{{ n.title }}</div>
        <div class="notif-meta">{{ n.adminNickname }} · {{ formatTime(n.createdAt) }}</div>
      </div>
      <el-icon class="notif-arrow"><ArrowRight /></el-icon>
    </div>
  </div>
  <Empty v-else title="暂无系统通知" />
</template>

<script setup lang="ts">
/** 通知列表组件，展示系统通知列表 @component */
import { Bell, ArrowRight } from '@element-plus/icons-vue'
import type { SystemNotification } from '@/api/notification'
import Empty from '@/components/common/Empty.vue'

defineProps<{ notifications: SystemNotification[] }>()
defineEmits<{ open: [n: SystemNotification] }>()

/** 格式化通知时间 @param time ISO 时间字符串 @returns 格式化后的时间文本 */
const formatTime = (time: string) => {
  const d = new Date(time); const now = new Date()
  const isToday = d.toDateString() === now.toDateString()
  if (isToday) return d.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
  const yesterday = new Date(now.getTime() - 86400000).toDateString()
  if (d.toDateString() === yesterday) return '昨天 ' + d.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
  return `${d.getMonth() + 1}/${d.getDate()}`
}
</script>

<style scoped>
.notification-list { padding: 8px 0; }
.notification-item { display: flex; align-items: center; gap: 14px; padding: 16px 14px; border-radius: 14px; margin-bottom: 10px; cursor: pointer; transition: all 0.25s; background: var(--bg-color-white); border: 1px solid var(--border-color-extra-light); position: relative; overflow: hidden; animation: slideInItem 0.35s ease both; }
@keyframes slideInItem { from { opacity: 0; transform: translateX(-12px); } to { opacity: 1; transform: translateX(0); } }
.notification-item::before { content: ''; position: absolute; left: 0; top: 0; height: 100%; width: 3px; background: linear-gradient(180deg, var(--color-warning), #f0a830); border-radius: 0 3px 3px 0; opacity: 0; transition: opacity 0.25s; }
.notification-item:hover { background: linear-gradient(135deg, #fef9e7, #fef3d5); border-color: #ffe7ba; transform: translateX(4px); box-shadow: 0 4px 16px rgba(230, 162, 60, 0.12); }
.notification-item:hover::before { opacity: 1; }
.notif-icon-wrap { width: 42px; height: 42px; border-radius: 12px; background: linear-gradient(135deg, #fef3d5, #fdecc0); display: flex; align-items: center; justify-content: center; flex-shrink: 0; transition: all 0.25s; }
.notification-item:hover .notif-icon-wrap { background: linear-gradient(135deg, var(--color-warning), #f0a830); transform: scale(1.08); box-shadow: 0 4px 12px rgba(230, 162, 60, 0.3); }
.notif-icon { font-size: 20px; color: #b8860b; transition: color 0.25s; }
.notification-item:hover .notif-icon { color: white; }
.notif-content { flex: 1; min-width: 0; }
.notif-title { font-size: 14px; font-weight: 600; color: var(--text-primary); margin-bottom: 4px; }
.notif-meta { font-size: 12px; color: var(--text-secondary); }
.notif-arrow { color: var(--text-secondary); flex-shrink: 0; font-size: 16px; transition: all 0.25s; }
.notification-item:hover .notif-arrow { color: #b8860b; transform: translateX(4px); }
</style>
