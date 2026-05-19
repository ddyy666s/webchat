<template>
  <div class="toolbar" v-show="isExpanded">
    <div class="tool-item" @click="$emit('openImageUpload')">
      <el-icon :size="24">
        <Picture />
      </el-icon>
      <span>图片</span>
    </div>
    <div class="tool-item" @mousedown="$emit('startRecord')" @mouseup="$emit('stopRecord')"
      @mouseleave="$emit('cancelRecord')">
      <el-icon :size="24">
        <Microphone />
      </el-icon>
      <span>语音</span>
    </div>
    <div class="tool-item" @click="$emit('startVoiceCall')">
      <el-icon :size="24">
        <Phone />
      </el-icon>
      <span>语音通话</span>
    </div>
    <div class="tool-item" @click="$emit('startVideoCall')">
      <el-icon :size="24">
        <VideoCamera />
      </el-icon>
      <span>视频通话</span>
    </div>
    <div class="tool-item" @click="$emit('openEmojiPicker')">
      <el-icon :size="24">
        <Sunny />
      </el-icon>
      <span>表情</span>
    </div>
  </div>
</template>

<script setup lang="ts">
/** 工具栏组件，展示图片/语音/通话/表情功能入口 @component */
import { Picture, Microphone, Phone, VideoCamera, Sunny } from '@element-plus/icons-vue'

/** 组件属性：是否展开 */
defineProps<{
  isExpanded: boolean
}>()

/** 组件事件：各功能按钮的点击回调 */
defineEmits<{
  (e: 'openImageUpload'): void
  (e: 'startRecord'): void
  (e: 'stopRecord'): void
  (e: 'cancelRecord'): void
  (e: 'startVoiceCall'): void
  (e: 'startVideoCall'): void
  (e: 'openEmojiPicker'): void
}>()
</script>

<style scoped>
.toolbar {
  display: flex;
  justify-content: space-around;
  padding: 12px 16px;
}

.tool-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  cursor: pointer;
  color: var(--text-regular);
  padding: 8px 16px;
  border-radius: 14px;
  transition: all 0.25s;
}

.tool-item:hover {
  color: var(--color-primary);
  background: #f3f0ff;
  transform: translateY(-2px);
}

.tool-item:active {
  transform: translateY(0) scale(0.95);
}


</style>
