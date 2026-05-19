<template>
  <div class="impression-item">
    <div class="avatar">
      <el-avatar :size="36" :src="avatarUrl">
        {{ avatarText }}
      </el-avatar>
    </div>
    <div class="content">
      <div class="name">{{ displayName }}</div>
      <div class="text">{{ impression.content }}</div>
      <div class="time">{{ formatDate(impression.createdAt) }}</div>
    </div>
    <div v-if="showDelete" class="actions">
      <el-button class="delete-btn" size="small" @click="$emit('delete', impression.id)">
        <el-icon :size="13"><Delete /></el-icon>
        <span>删除</span>
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
/** 评价项展示组件 @component */
import { computed } from 'vue'
import { Delete } from '@element-plus/icons-vue'
import type { ImpressionVO } from '@/api/impression'
import { formatDate } from '@/utils/date'

/** 组件属性：评价对象、类型（to-me: 我收到的, by-me: 我给出的） */
const props = defineProps<{
  impression: ImpressionVO
  type: 'to-me' | 'by-me'
}>()

/** 组件事件：删除评价 */
defineEmits<{
  delete: [id: number]
}>()

/** 显示的昵称 */
const displayName = computed(() => {
  return props.type === 'to-me'
    ? props.impression.fromUserNickname
    : props.impression.toUserNickname
})

/** 头像 URL */
const avatarUrl = computed(() => {
  return props.type === 'to-me'
    ? props.impression.fromUserAvatar
    : props.impression.toUserAvatar
})

/** 头像占位字符 */
const avatarText = computed(() => {
  return displayName.value?.charAt(0) || 'U'
})

/** 是否显示删除按钮——仅"我给出的"可删除 */
const showDelete = computed(() => props.type === 'by-me')
</script>

<style scoped>
.impression-item {
  display: flex;
  gap: 12px;
  padding: 14px 0;
  border-bottom: 1px solid var(--border-color-lighter);
  transition: background 0.2s;
}

.impression-item:hover {
  background: #fafaff;
  margin: 0 -8px;
  padding: 14px 8px;
  border-radius: 12px;
  border-bottom-color: transparent;
}

.impression-item .content {
  flex: 1;
}

.impression-item .name {
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 4px;
  color: var(--text-primary);
}

.impression-item .text {
  font-size: 14px;
  color: var(--text-regular);
  margin-bottom: 4px;
  word-break: break-word;
  line-height: 1.6;
}

.impression-item .time {
  font-size: 12px;
  color: var(--text-secondary);
}

.impression-item .actions {
  display: flex;
  align-items: center;
  flex-shrink: 0;
}

.delete-btn {
  height: 32px;
  padding: 0 14px !important;
  border-radius: 8px !important;
  font-size: 12px !important;
  font-weight: 600 !important;
  gap: 4px;
  border: 1.5px solid var(--color-danger) !important;
  color: var(--color-danger) !important;
  background: #fff5f5 !important;
  transition: all 0.2s !important;
  opacity: 0;
}

.impression-item:hover .delete-btn {
  opacity: 1;
}

.delete-btn:hover {
  background: var(--color-danger) !important;
  color: white !important;
  transform: scale(1.05);
  box-shadow: 0 3px 10px rgba(255, 118, 117, 0.3);
}

.delete-btn:active {
  transform: scale(0.95);
}
</style>
