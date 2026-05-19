<template>
  <div v-if="isRecording" class="recording-tip">
    <el-icon>
      <Microphone />
    </el-icon>
    <span>正在录音... {{ formatDuration(recordDuration) }} 松手发送</span>
    <div class="recording-wave"><span v-for="i in 5" :key="i"></span></div>
  </div>
</template>

<script setup lang="ts">
/** 录音提示浮动组件 @component */
import { Microphone } from '@element-plus/icons-vue'
import { formatDuration } from '@/utils/date'

/** 组件属性：是否正在录音、录音时长 */
defineProps<{
  isRecording: boolean
  recordDuration: number
}>()
</script>

<style scoped>
.recording-tip {
  position: fixed;
  bottom: 180px;
  left: 50%;
  transform: translateX(-50%);
  background: linear-gradient(135deg, var(--color-primary), var(--color-primary-dark));
  color: white;
  padding: 16px 28px;
  border-radius: 50px;
  display: flex;
  align-items: center;
  gap: 16px;
  z-index: 1000;
  box-shadow: 0 8px 30px rgba(108, 92, 231, 0.3);
  animation: floatIn 0.3s ease;
}

@keyframes floatIn {
  from { opacity: 0; transform: translateX(-50%) translateY(10px); }
  to { opacity: 1; transform: translateX(-50%) translateY(0); }
}

.recording-wave {
  display: flex;
  gap: 4px;
  align-items: center;
}

.recording-wave span {
  width: 4px;
  height: 12px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 4px;
  animation: wave 0.6s ease-in-out infinite;
}

.recording-wave span:nth-child(2) { animation-delay: 0.1s; }
.recording-wave span:nth-child(3) { animation-delay: 0.2s; }
.recording-wave span:nth-child(4) { animation-delay: 0.3s; }
.recording-wave span:nth-child(5) { animation-delay: 0.4s; }

@keyframes wave {
  0%, 100% { height: 12px; }
  50% { height: 28px; }
}
</style>
