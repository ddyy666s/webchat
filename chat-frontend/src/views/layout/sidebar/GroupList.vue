<template>
  <div class="group-list">
    <div class="group-header-actions">
      <el-button type="primary" size="small" @click="$emit('create')"><el-icon><Plus /></el-icon>创建群聊</el-button>
    </div>
    <div v-for="group in groups" :key="group.id" class="group-item" :class="{ active: currentGroupId === group.id }" @click="$emit('select', group)">
      <div class="group-avatar">
        <el-avatar :size="40" :src="group.avatar || ''">{{ group.name?.charAt(0) || '群' }}</el-avatar>
        <span v-if="group.unreadCount > 0" class="unread-dot">{{ group.unreadCount > 99 ? '99+' : group.unreadCount }}</span>
      </div>
      <div class="group-info">
        <div class="group-name">{{ group.name }}</div>
        <div class="group-desc">{{ group.memberCount }}人 · {{ formatTime(group.createdAt) }}</div>
      </div>
    </div>
    <el-empty v-if="groups.length === 0" description="暂无群聊" />
  </div>
</template>

<script setup lang="ts">
/** 群聊列表组件 @component */
import { Plus } from '@element-plus/icons-vue'
import type { GroupVO } from '@/api/group'

defineProps<{ groups: GroupVO[]; currentGroupId: number | null }>()
defineEmits<{ select: [group: GroupVO]; create: [] }>()

const formatTime = (time: string) => {
  const date = new Date(time); const now = new Date(); const diff = now.getTime() - date.getTime(); const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  if (days === 0) return '今天'; if (days === 1) return '昨天'; if (days < 7) return `${days}天前`
  return date.toLocaleDateString()
}
</script>

<style scoped>
.group-list { padding: 12px; }
.group-header-actions { padding: 0 4px 12px 4px; }
.group-header-actions .el-button { border-radius: 14px !important; font-weight: 600 !important; width: 100%; height: 46px; font-size: 15px !important; }
.group-item { display: flex; align-items: center; gap: 12px; padding: 12px; border-radius: 14px; cursor: pointer; transition: all 0.2s; margin: 2px 4px; }
.group-item:hover { background: #f3f0ff; transform: translateX(2px); }
.group-item.active { background: linear-gradient(135deg,#f3f0ff,#e8e3ff); box-shadow: 0 2px 8px rgba(108,92,231,0.08); }
.group-avatar { position: relative; flex-shrink: 0; }
.group-avatar :deep(.el-avatar) { border: 2px solid transparent; }
.group-item.active .group-avatar :deep(.el-avatar) { border-color: var(--color-primary-light); }
.unread-dot { position: absolute; top: -4px; right: -4px; background: var(--color-danger); color: white; font-size: 10px; font-weight: 600; padding: 0 5px; border-radius: 10px; min-width: 18px; height: 18px; line-height: 18px; text-align: center; box-shadow: 0 2px 6px rgba(255,118,117,0.3); }
.group-info { flex: 1; min-width: 0; }
.group-name { font-size: 15px; font-weight: 600; margin-bottom: 3px; color: var(--text-primary); }
.group-item.active .group-name { color: var(--color-primary); }
.group-desc { font-size: 12px; color: var(--text-secondary); }
</style>
