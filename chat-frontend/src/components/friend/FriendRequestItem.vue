<template>
  <div class="friend-request-item">
    <div class="avatar"><el-avatar :size="44" :src="request.fromUserAvatar || defaultAvatar">{{ request.fromUserNickname?.charAt(0) || 'U' }}</el-avatar></div>
    <div class="info">
      <div class="nickname">{{ request.fromUserNickname }}</div>
      <div class="message">{{ request.message || '申请添加好友' }}</div>
      <div class="time">{{ formatTime(request.createdAt) }}</div>
    </div>
    <div class="actions" v-if="request.status === 0">
      <el-button size="small" type="success" @click="handleAccept">同意</el-button>
      <el-button size="small" type="danger" @click="handleReject">拒绝</el-button>
    </div>
    <div class="status-badge" v-else><el-tag :type="request.status === 1 ? 'success' : 'danger'" size="small">{{ request.status === 1 ? '已同意' : '已拒绝' }}</el-tag></div>
  </div>
</template>

<script setup lang="ts">
/** 好友申请项组件，支持同意/拒绝操作 @component */
import { ElMessage } from 'element-plus'
import { handleFriendRequestApi } from '@/api/friend'
import defaultAvatar from '@/assets/images/default-avatar.png'

const props = defineProps<{ request: { id: number; fromUserId: number; fromUserNickname: string; fromUserAvatar: string | null; message: string | null; status: number; createdAt: string } }>()
const emit = defineEmits(['refresh'])

const formatTime = (time: string) => {
  const date = new Date(time); const now = new Date(); const diff = now.getTime() - date.getTime(); const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  if (days === 0) return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
  if (days === 1) return '昨天'; if (days < 7) return `${days}天前`
  return date.toLocaleDateString()
}

const handleAccept = async () => { try { await handleFriendRequestApi(props.request.id, 1); ElMessage.success('已添加好友'); emit('refresh') } catch (error) { console.error(error); ElMessage.error('操作失败') } }
const handleReject = async () => { try { await handleFriendRequestApi(props.request.id, 2); ElMessage.success('已拒绝'); emit('refresh') } catch (error) { console.error(error); ElMessage.error('操作失败') } }
</script>

<style scoped>
.friend-request-item { display: flex; align-items: center; gap: 12px; padding: 14px 12px; background: var(--bg-color-white); border-radius: 14px; margin-bottom: 8px; transition: all 0.2s; border: 1px solid var(--border-color-extra-light); }
.friend-request-item:hover { background: #f3f0ff; border-color: var(--color-primary-light); transform: translateX(3px); }
.info { flex: 1; min-width: 0; }
.nickname { font-size: 15px; font-weight: 600; color: var(--text-primary); margin-bottom: 4px; }
.message { font-size: 13px; color: var(--text-secondary); margin-bottom: 4px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.time { font-size: 11px; color: var(--text-secondary); }
.actions { display: flex; gap: 8px; flex-shrink: 0; }
.actions .el-button { border-radius: 10px !important; font-weight: 600 !important; font-size: 12px !important; padding: 6px 14px !important; }
.status-badge { flex-shrink: 0; }
</style>
