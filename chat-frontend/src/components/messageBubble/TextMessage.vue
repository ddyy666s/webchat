<template>
  <span class="text-message">{{ content }}</span>
</template>

<script setup lang="ts">
/** 文字消息展示组件 @component */
defineProps<{
  content: string
}>()
</script>

<style scoped>
.text-message {
  word-break: break-word;
  font-size: 14px;
  line-height: 1.5;
  color: #303133;
}
</style>
