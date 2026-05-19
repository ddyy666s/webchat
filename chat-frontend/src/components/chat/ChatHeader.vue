<template>
  <div class="chat-header">
    <div class="friend-info">
      <el-avatar :size="40" :src="friend?.avatar || ''">
        {{ friend?.nickname?.charAt(0) || 'U' }}
      </el-avatar>
      <div class="friend-detail">
        <div class="name">{{ friend?.remark || friend?.nickname }}</div>
        <div class="status">{{ friend?.isOnline ? '在线' : '离线' }}</div>
      </div>
    </div>
    <div class="actions">
      <el-tooltip content="下载聊天记录" placement="bottom">
        <el-button class="download-trigger" @click="$emit('download')">
          <el-icon :size="18"><Download /></el-icon>
          <span>下载</span>
        </el-button>
      </el-tooltip>
    </div>
  </div>
</template>

<script setup lang="ts">
/** 聊天窗口头部组件，展示好友信息和操作按钮 @component */
import { Download } from '@element-plus/icons-vue'

/** 组件属性：好友信息 */
defineProps<{ friend: any }>()
/** 组件事件：下载聊天记录 */
defineEmits<{ (e: 'download'): void }>()
</script>

<style scoped>
.chat-header {
  padding: 14px 24px;
  border-bottom: 1px solid var(--border-color-lighter);
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: var(--bg-color-white);
  position: relative;
  z-index: 5;
}

.friend-info {
  display: flex;
  align-items: center;
  gap: 14px;
}

.friend-detail .name {
  font-size: 17px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 2px;
}

.friend-detail .status {
  font-size: 12px;
  color: var(--text-secondary);
  font-weight: 500;
}

.actions {
  display: flex;
  gap: 8px;
  align-items: center;
}

.download-trigger {
  height: 38px;
  padding: 0 18px !important;
  border-radius: 12px !important;
  font-size: 13px !important;
  font-weight: 600 !important;
  gap: 6px;
  border: 2px solid var(--border-color) !important;
  color: var(--text-regular) !important;
  transition: all 0.25s !important;
}

.download-trigger:hover {
  border-color: var(--color-primary) !important;
  color: var(--color-primary) !important;
  background: #f3f0ff !important;
  transform: translateY(-2px);
  box-shadow: 0 4px 14px rgba(108, 92, 231, 0.15);
}

.download-trigger:active {
  transform: translateY(0) scale(0.97);
}
</style>
