<template>
  <div class="message-item" :class="{ own: isOwn }">
    <el-avatar :size="40" :src="message.fromUserAvatar || ''">{{ message.fromUserNickname?.charAt(0) || 'U' }}</el-avatar>
    <div class="message-content">
      <div class="message-info" v-if="showInfo"><span class="name">{{ message.fromUserNickname }}</span><span class="time">{{ formatRelativeTime(message.sendTime) }}</span></div>
      <div class="message-bubble" :class="{ recalled: message.isRecalled }">
        <span v-if="message.messageType === 1 && !message.isRecalled">{{ message.content }}</span>
        <div v-else-if="message.messageType === 2 && !message.isRecalled" class="image-message"><el-image :src="message.content" :preview-src-list="[message.content]" fit="cover" class="message-image" /></div>
        <VoiceMessage v-else-if="message.messageType === 4 && !message.isRecalled" :url="message.content" :duration="message.duration" />
        <span v-else-if="message.isRecalled" class="recalled">{{ isOwn ? '你撤回了一条消息' : '对方撤回了一条消息' }}</span>
        <span v-else>{{ message.content }}</span>
        <el-button v-if="isOwn && !message.isRecalled && canRecall" class="recall-btn" text size="small" @click.stop="handleRecall">撤回</el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
/** 聊天消息气泡组件，支持文字/图片/语音/撤回消息展示 @component */
import { computed } from 'vue'
import { ElMessage } from 'element-plus'
import { formatRelativeTime } from '@/utils/date'
import { recallMessageApi } from '@/api/message'
import VoiceMessage from './VoiceMessage.vue'

const props = defineProps<{ message: any; isOwn: boolean; showInfo?: boolean }>()
const RECALL_LIMIT = 2 * 60 * 1000
const canRecall = computed(() => { if (!props.message.sendTime) return false; return Date.now() - new Date(props.message.sendTime).getTime() <= RECALL_LIMIT })

const handleRecall = async () => {
  if (!canRecall.value) { ElMessage.warning('消息发送超过2分钟，无法撤回'); return }
  try { await recallMessageApi(props.message.id); ElMessage.success('已撤回'); props.message.isRecalled = true }
  catch { ElMessage.error('撤回失败') }
}
</script>

<style scoped>
.message-item { display: flex; gap: 14px; margin-bottom: 16px; padding: 0 12px; animation: messageIn 0.3s ease; }
@keyframes messageIn { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: translateY(0); } }
.message-item.own { flex-direction: row-reverse; }
.message-item.own .message-content { align-items: flex-end; }
.message-content { display: flex; flex-direction: column; max-width: 68%; }
.message-info { display: flex; gap: 10px; font-size: 12px; color: var(--text-secondary); margin-bottom: 6px; }
.message-bubble { background: var(--bg-color-white); padding: 12px 20px; border-radius: 22px; word-wrap: break-word; line-height: 1.6; font-size: 15px; box-shadow: 0 2px 8px rgba(0,0,0,0.06); position: relative; border-bottom-left-radius: 6px; }
.message-item:not(.own) .message-bubble { border-bottom-left-radius: 6px; border-top-left-radius: 22px; }
.message-item.own .message-bubble { background: linear-gradient(135deg,#7c6df0,#a08aff); color: white; border-bottom-right-radius: 6px; border-bottom-left-radius: 22px; border-top-right-radius: 6px; }
.image-message { cursor: pointer; }
.message-image { max-width: 240px; border-radius: 14px; border: 1px solid var(--border-color-lighter); }
.message-bubble:hover .recall-btn { display: inline-flex; }
.recall-btn { position: absolute; top: -28px; right: 0; display: none; font-size: 11px; color: var(--color-primary); }
.message-item.own .recall-btn { color: white; opacity: 0.8; }
.recalled { color: var(--text-secondary); font-style: italic; font-size: 13px; }
</style>
