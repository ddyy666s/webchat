<template>
  <ViewDialog v-model="visible" title="系统通知" width="500px" @close="handleClose">
    <div class="notification-content">
      <div class="notification-title">{{ notification?.title }}</div>
      <el-divider />
      <div class="notification-body">{{ notification?.content }}</div>
      <div class="notification-meta">
        <span>发布者: {{ notification?.adminNickname }}</span>
        <span>{{ notification?.createdAt }}</span>
      </div>
    </div>
  </ViewDialog>
</template>

<script setup lang="ts">
/** 系统通知详情对话框组件 @component */
import { ref, watch } from 'vue'
import type { SystemNotification } from '@/api/notification'
import ViewDialog from '@/components/common/ViewDialog.vue'

/** 组件属性：显示状态、通知对象 */
const props = defineProps<{
  modelValue: boolean
  notification: SystemNotification | null
}>()

/** 组件事件：更新显示状态、关闭 */
const emit = defineEmits(['update:modelValue', 'close'])

/** 对话框可见性 */
const visible = ref(false)

/** 同步外部 modelValue */
watch(() => props.modelValue, (val) => {
  visible.value = val
})

/** 关闭对话框 @returns void */
const handleClose = () => {
  visible.value = false
  emit('close')
}
</script>

<style scoped>
.notification-content {
  padding: 8px 0;
}

.notification-title {
  font-size: 18px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 4px;
}

.notification-body {
  font-size: 14px;
  color: var(--text-regular);
  line-height: 1.9;
  white-space: pre-wrap;
}

.notification-meta {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px solid var(--border-color-lighter);
  font-size: 12px;
  color: var(--text-secondary);
}
</style>
