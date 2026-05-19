<template>
  <div class="voice-call">
    <el-avatar :size="120" :src="targetUser?.avatar || ''">
      {{ targetUser?.nickname?.charAt(0) || 'U' }}
    </el-avatar>
    <div class="target-name">{{ targetUser?.nickname }}</div>
    <div class="call-status">{{ statusText }}</div>
    <div class="call-duration" v-if="isConnected">{{ formatDuration(duration) }}</div>
    <audio ref="audioRef" autoplay />
  </div>
</template>

<script setup lang="ts">
/** 语音通话界面组件，展示对方头像、状态和通话时长 @component */
import { ref, watch, type Ref } from 'vue'
import { formatDuration } from '@/utils/date'

/** 组件属性：目标用户、连接状态、状态文本、通话时长、远程音频流 */
const props = defineProps<{
  targetUser: any
  isConnected: boolean
  statusText: string
  duration: number
  stream?: MediaStream | null
}>()

/** 音频元素引用 */
const audioRef = ref<HTMLAudioElement>()

/** 监听远程音频流，自动绑定到 audio 元素 */
watch(() => props.stream, (stream) => {
  const el = audioRef.value
  if (!el) return
  if (stream) {
    el.srcObject = stream
  } else {
    el.srcObject = null
  }
}, { immediate: true })
</script>

<style scoped>
.voice-call {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  padding: 30px;
}
.target-name { font-size: 18px; font-weight: 500; }
.call-status { font-size: 14px; color: #909399; }
.call-duration { font-size: 28px; font-weight: 500; color: #409eff; }
</style>
