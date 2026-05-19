<template>
  <div v-if="notice" class="notice-bar" @click="$emit('click')">
    <el-icon>
      <InfoFilled />
    </el-icon>
    <span class="notice-text">{{ notice }}</span>
    <el-button text size="small">详情</el-button>
  </div>
</template>

<script setup lang="ts">
/** 群公告栏组件，展示公告概览 @component */
import { InfoFilled } from '@element-plus/icons-vue'

/** 组件属性：公告内容 */
defineProps<{
  notice: string | null
}>()

/** 组件事件：点击查看详情 */
defineEmits<{
  (e: 'click'): void
}>()
</script>

<style scoped>
.notice-bar {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  background: linear-gradient(135deg, #fef9e7, #fef3d5);
  border-bottom: 1px solid var(--border-color-lighter);
  cursor: pointer;
  transition: all 0.2s;
}

.notice-bar:hover {
  background: linear-gradient(135deg, #fef3d5, #fdecc0);
}

.notice-bar .el-icon {
  color: var(--color-warning);
  font-size: 18px;
  flex-shrink: 0;
}

.notice-bar .notice-text {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 13px;
  color: #b8860b;
  font-weight: 500;
}
</style>
