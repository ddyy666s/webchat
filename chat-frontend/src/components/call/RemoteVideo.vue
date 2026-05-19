<template>
  <div class="remote-video">
    <video ref="videoRef" autoplay playsinline class="video-element"></video>
    <div class="remote-placeholder" v-if="!isConnected">
      <el-avatar :size="100" :src="targetUser?.avatar || ''">
        {{ targetUser?.nickname?.charAt(0) || 'U' }}
      </el-avatar>
      <div class="status-text">{{ statusText }}</div>
    </div>
  </div>
</template>

<script setup lang="ts">
/** 远程视频展示组件，显示对方摄像头画面 @component */
import { ref, watch } from 'vue'

/** 组件属性：目标用户信息、连接状态、状态文本、远程媒体流 */
const props = defineProps<{
  targetUser: any
  isConnected: boolean
  statusText: string
  stream: MediaStream | null
}>()

/** 视频元素引用 */
const videoRef = ref<HTMLVideoElement>()

/** 监听远程流变化，自动绑定到 video 元素 */
watch(() => props.stream, (newStream) => {
  if (videoRef.value && newStream) {
    videoRef.value.srcObject = newStream
  }
}, { immediate: true })
</script>

<style scoped>
.remote-video {
  width: 100%;
  height: 100%;
  background: #1a1a2e;
  border-radius: 12px;
  overflow: hidden;
  position: relative;
}

.video-element {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.remote-placeholder {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
}

.status-text {
  margin-top: 12px;
  color: #fff;
  font-size: 14px;
}
</style>
