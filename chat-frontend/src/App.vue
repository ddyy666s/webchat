<template>
  <router-view />
  <RtcCallDialog />
</template>

<script setup lang="ts">
/** 应用根组件，全局监听 token 状态以管理 WebSocket 连接 @component */
import { watch } from 'vue'
import { useUserStore } from '@/stores/userStore'
import { useRtcStore } from '@/stores/rtcStore'
import { websocketService } from '@/utils/websocket'
import RtcCallDialog from '@/components/rtc/RtcCallDialog.vue'

const userStore = useUserStore()
const rtcStore = useRtcStore()

/** 监听 token 变化，自动连接/断开 WebSocket */
watch(() => userStore.token, (token) => {
  if (!token) { rtcStore.disconnect(); return }
  websocketService.connect()
  rtcStore.ensureSocket()
}, { immediate: true })
</script>

<style>
/* 全局样式已在 main.css 中定义 */
</style>
