<template>
  <div class="local-video">
    <video ref="videoRef" autoplay muted playsinline class="video-element"></video>
    <div class="local-label">我</div>
  </div>
</template>

<script setup lang="ts">
/** 本地摄像头视频展示组件 @component */
import { ref, watch } from 'vue'

/** 组件属性：本地媒体流 */
const props = defineProps<{
  stream: MediaStream | null
}>()

/** 视频元素引用 */
const videoRef = ref<HTMLVideoElement>()

/** 监听媒体流变化，自动绑定到 video 元素 */
watch(() => props.stream, (newStream) => {
  if (videoRef.value && newStream) {
    videoRef.value.srcObject = newStream
  }
}, { immediate: true })
</script>

<style scoped>
.local-video {
  position: absolute;
  bottom: 16px;
  right: 16px;
  width: 120px;
  height: 90px;
  background: #000;
  border-radius: 8px;
  overflow: hidden;
  border: 2px solid #fff;
  z-index: 10;
}

.local-video .video-element {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.local-label {
  position: absolute;
  bottom: 4px;
  left: 8px;
  color: white;
  font-size: 10px;
  background: rgba(0, 0, 0, 0.5);
  padding: 2px 6px;
  border-radius: 4px;
}
</style>
