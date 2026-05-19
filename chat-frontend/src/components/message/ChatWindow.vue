<template>
  <div class="chat-window">
    <ChatHeader :friend="friend" @download="showDownloadDialog = true" />
    <MessageList ref="messageListRef" :messages="messages" :current-user-id="currentUserId" :loading="loading" @load-more="loadMore" />
    <MessageInput :current-chat-user-id="friend?.userId" @send="sendMessage" @send-image="sendImage" @send-voice="sendVoice" @send-emoji="sendEmoji" @start-voice-call="startVoiceCall" @start-video-call="startVideoCall" />
    <CallDialog v-model="voiceCallVisible" :target-user="friend" call-type="voice" :is-caller="true" @end-call="endVoiceCall" />
    <CallDialog v-model="incomingCallVisible" :target-user="incomingCaller" :call-type="incomingCallType" :is-caller="false" :initial-offer="pendingOffer" @end-call="endIncomingCall" @call-accepted="stopRingtone" />
    <DownloadDialog v-model="showDownloadDialog" :friend-id="friend?.userId" :friend-name="friend?.nickname" :total-messages="totalMessageCount" :max-limit="maxDownloadLimit" @download="handleDownload" />
  </div>
</template>

<script setup lang="ts">
/** 单聊聊天窗口组件，管理消息/通话/下载等功能 @component */
import { useChat } from '@/composables/useChat'
import ChatHeader from '../chat/ChatHeader.vue'
import MessageList from '../chat/MessageList.vue'
import MessageInput from '../chat/MessageInput.vue'
import CallDialog from '../call/CallDialog.vue'
import DownloadDialog from '../common/DownloadDialog.vue'

const props = defineProps<{ friend: any }>()

const {
  currentUserId, messages, loading, totalMessageCount, showDownloadDialog,
  voiceCallVisible, incomingCallVisible, incomingCaller, incomingCallType, pendingOffer,
  maxDownloadLimit, messageListRef, loadMore, sendMessage, sendImage, sendVoice, sendEmoji,
  startVoiceCall, startVideoCall, endVoiceCall, endIncomingCall, handleDownload, stopRingtone
} = useChat(props)
</script>

<style scoped>
.chat-window { height: 100%; display: flex; flex-direction: column; background: var(--bg-color-white); }
</style>
