<template>
  <div class="loading-container" v-if="visible">
    <div class="loading-mask">
      <div class="loading-spinner">
        <el-icon class="is-loading">
          <Loading />
        </el-icon>
        <span class="loading-text">{{ text || '加载中...' }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
/** 全屏加载遮罩组件 @component */
import { Loading } from '@element-plus/icons-vue'

/** 组件属性：是否显示、加载文本 */
defineProps<{
  visible?: boolean
  text?: string
}>()
</script>

<style scoped>
.loading-container {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 9999;
}

.loading-mask {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.35);
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(2px);
}

.loading-spinner {
  background: white;
  padding: 24px 36px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  gap: 14px;
  font-size: 15px;
  color: var(--color-primary);
  font-weight: 600;
  box-shadow: var(--box-shadow-dark);
  animation: popIn 0.3s ease;
}

@keyframes popIn {
  from { opacity: 0; transform: scale(0.9); }
  to { opacity: 1; transform: scale(1); }
}

.is-loading {
  animation: rotate 1.2s linear infinite;
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
</style>
