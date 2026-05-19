<template>
  <div class="search-item">
    <div class="user-info">
      <el-avatar :size="36" :src="user.avatar || ''">
        {{ user.nickname?.charAt(0) || 'U' }}
      </el-avatar>
      <div class="user-detail">
        <div class="username">{{ user.nickname }}</div>
        <div class="signature">{{ user.signature || '暂无签名' }}</div>
      </div>
    </div>
    <el-button v-if="user.remark" class="btn-already" disabled>
      已是好友
    </el-button>
    <el-button v-else class="btn-add-friend" @click="$emit('add', user.userId)">
      <el-icon :size="14"><Plus /></el-icon>
      <span>添加好友</span>
    </el-button>
  </div>
</template>

<script setup lang="ts">
/** 用户搜索结果显示组件，支持添加好友操作 @component */
import { Plus } from '@element-plus/icons-vue'

defineProps<{
  user: any
}>()

/** 组件事件：添加好友 @param userId 目标用户 ID */
defineEmits<{
  (e: 'add', userId: number): void
}>()
</script>

<style scoped>
.search-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 12px;
  border-bottom: 1px solid var(--border-color-lighter);
  transition: all 0.2s;
  border-radius: 4px;
}

.search-item:hover {
  background: #f3f0ff;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-detail {
  display: flex;
  flex-direction: column;
}

.username {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
}

.signature {
  font-size: 12px;
  color: var(--text-secondary);
  margin-top: 3px;
}

.btn-add-friend {
  height: 36px;
  padding: 0 18px !important;
  border-radius: 10px !important;
  font-size: 13px !important;
  font-weight: 600 !important;
  gap: 5px;
  border: 2px solid var(--color-primary) !important;
  background: linear-gradient(135deg, var(--color-primary), var(--color-primary-dark)) !important;
  color: white !important;
  transition: all 0.25s !important;
  flex-shrink: 0;
}

.btn-add-friend:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 14px rgba(108, 92, 231, 0.3) !important;
  border-color: var(--color-primary-light) !important;
}

.btn-add-friend:active {
  transform: translateY(0) scale(0.97);
}

.btn-already {
  height: 36px;
  padding: 0 18px !important;
  border-radius: 10px !important;
  font-size: 13px !important;
  font-weight: 600 !important;
  border: 2px solid var(--border-color) !important;
  color: var(--text-secondary) !important;
  background: var(--bg-color) !important;
  cursor: default !important;
}
</style>
