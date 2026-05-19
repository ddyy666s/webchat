<template>
  <div class="message-list" ref="listRef" @scroll="handleScroll">
    <div v-if="loading && messages.length === 0" class="loading"><el-skeleton :rows="2" animated /></div>
    <div v-for="msg in messages" :key="msg.id" class="message-item" :class="{ own: msg.fromUserId === currentUserId }">
      <el-avatar :size="40" :src="msg.fromUserAvatar || ''">{{ msg.fromUserNickname?.charAt(0) || 'U' }}</el-avatar>
      <div class="message-content">
        <div class="message-info"><span class="name">{{ msg.fromUserNickname }}</span><span class="time">{{ formatRelativeTime(msg.sendTime) }}</span></div>
        <div class="message-bubble"><span>{{ msg.content }}</span></div>
      </div>
    </div>
    <div v-if="loading && messages.length > 0" class="loading-more"><el-icon class="is-loading"><Loading /></el-icon><span>加载更多...</span></div>
    <div ref="scrollBottomRef"></div>
  </div>
</template>

<script setup lang="ts">
/** 群聊消息列表组件 @component */
import { ref, watch, nextTick } from 'vue'
import { Loading } from '@element-plus/icons-vue'
import { formatRelativeTime } from '@/utils/date'

const props = defineProps<{ messages: any[]; currentUserId: number | undefined; loading: boolean }>()
const emit = defineEmits(['loadMore'])
const listRef = ref<HTMLElement>(); const scrollBottomRef = ref<HTMLElement>()

const scrollToBottom = () => { nextTick(() => { scrollBottomRef.value?.scrollIntoView({ behavior: 'smooth' }) }) }
const handleScroll = () => {
  if (!listRef.value) return; const { scrollTop, scrollHeight, clientHeight } = listRef.value
  if (scrollTop + clientHeight >= scrollHeight - 100) emit('loadMore')
}
watch(() => props.messages.length, () => { scrollToBottom() }, { immediate: true })
defineExpose({ scrollToBottom })
</script>

<style scoped>
.message-list { flex: 1; overflow-y: auto; padding: 20px 16px; background: var(--bg-color); }
.message-item { display: flex; gap: 14px; margin-bottom: 16px; padding: 0 12px; animation: messageIn 0.3s ease; }
@keyframes messageIn { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: translateY(0); } }
.message-item.own { flex-direction: row-reverse; }
.message-item.own .message-content { align-items: flex-end; }
.message-content { display: flex; flex-direction: column; max-width: 68%; }
.message-info { display: flex; gap: 10px; font-size: 12px; color: var(--text-secondary); margin-bottom: 6px; }
.message-info .name { font-weight: 600; color: var(--color-primary); }
.message-bubble { background: var(--bg-color-white); padding: 12px 20px; border-radius: 22px; word-wrap: break-word; line-height: 1.6; font-size: 15px; box-shadow: 0 2px 8px rgba(0,0,0,0.06); border-bottom-left-radius: 6px; }
.message-item.own .message-bubble { background: linear-gradient(135deg,#7c6df0,#a08aff); color: white; border-bottom-right-radius: 6px; border-bottom-left-radius: 22px; border-top-right-radius: 6px; }
.loading { padding: 24px; text-align: center; }
.loading-more { display: flex; align-items: center; justify-content: center; gap: 8px; padding: 16px; color: var(--text-secondary); font-size: 13px; }
.is-loading { animation: rotate 1.2s linear infinite; }
@keyframes rotate { from { transform: rotate(0deg); } to { transform: rotate(360deg); } }
</style>
