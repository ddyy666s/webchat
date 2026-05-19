<template>
  <div class="request-list">
    <div v-for="req in requests" :key="req.id" class="request-item">
      <el-avatar :size="40" :src="req.fromUserAvatar || ''">
        {{ req.fromUserNickname?.charAt(0) || 'U' }}
      </el-avatar>
      <div class="info">
        <div class="name">{{ req.fromUserNickname }}</div>
        <div class="message">{{ req.message || '申请添加好友' }}</div>
      </div>
      <div class="actions">
        <el-button size="small" type="success" @click="$emit('agree', req.id)">同意</el-button>
        <el-button size="small" type="danger" @click="$emit('reject', req.id)">拒绝</el-button>
      </div>
    </div>

    <el-empty v-if="requests.length === 0" description="暂无申请" />
  </div>
</template>

<script setup lang="ts">
/** 好友申请列表组件 @component */
import type { FriendRequestVO } from '@/api/friend'

/** 组件属性：申请列表 */
defineProps<{
  requests: FriendRequestVO[]
}>()

/** 组件事件：同意/拒绝申请 */
defineEmits<{
  agree: [id: number]
  reject: [id: number]
}>()
</script>

<style scoped>
.request-list {
  padding: 12px;
}

.request-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 12px;
  border-radius: 14px;
  transition: all 0.2s;
  margin-bottom: 4px;
  background: var(--bg-color-white);
  box-shadow: 0 1px 3px rgba(0,0,0,0.03);
}

.request-item:hover {
  background: #f3f0ff;
  transform: translateX(2px);
}

.request-item .info {
  flex: 1;
}

.request-item .name {
  font-weight: 600;
  margin-bottom: 3px;
  font-size: 14px;
  color: var(--text-primary);
}

.request-item .message {
  font-size: 12px;
  color: var(--text-secondary);
}

.request-item .actions {
  display: flex;
  gap: 8px;
}

.request-item .actions .el-button {
  border-radius: 10px !important;
  font-weight: 600 !important;
  font-size: 12px !important;
  padding: 6px 14px !important;
}
</style>
