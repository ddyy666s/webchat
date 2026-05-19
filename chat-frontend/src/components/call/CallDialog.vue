<template>
  <BaseDialog v-model="visible" :title="callTitle" :width="callType === 'video' ? '800px' : '400px'" :close-on-click-modal="false" :show-close="false" destroy-on-close @close="hangupCall">
    <div class="call-container" :class="{ 'video-call': callType === 'video' }">
      <template v-if="callType === 'video'">
        <RemoteVideo :target-user="targetUser" :is-connected="isConnected" :status-text="callStatusText" :stream="remoteStream" />
        <LocalVideo :stream="localStream" />
      </template>
      <template v-else>
        <VoiceCallUI :target-user="targetUser" :is-connected="isConnected" :status-text="callStatusText" :duration="callDuration" :stream="remoteStream" />
      </template>
    </div>
    <template #footer>
      <CallActions :is-connected="isConnected" :is-caller="isCaller" @accept="acceptCall" @hangup="hangupCall" />
    </template>
  </BaseDialog>
</template>

<script setup lang="ts">
/** 通话对话框组件（语音/视频），管理 WebRTC 通话生命周期 @component */
import { ref, computed, watch, onUnmounted } from 'vue'
import BaseDialog from '@/components/common/BaseDialog.vue'
import { notify } from '@/utils/notify'
import { websocketService } from '@/utils/websocket'
import { useWebRTC } from '@/composables/useWebRTC'
import RemoteVideo from './RemoteVideo.vue'
import LocalVideo from './LocalVideo.vue'
import VoiceCallUI from './VoiceCallUI.vue'
import CallActions from './CallActions.vue'

const props = defineProps<{ modelValue: boolean; targetUser: any; callType: 'voice' | 'video'; isCaller: boolean; initialOffer?: any }>()
const emit = defineEmits(['update:modelValue', 'endCall', 'callAccepted'])

const visible = ref(false); const remoteStream = ref<MediaStream | null>(null)
const targetUserId = computed(() => props.targetUser?.userId || props.targetUser?.id)
const pendingCandidates: any[] = []; let hangupSent = false

const { isConnected, callDuration, localStream, createOffer, handleOffer, handleAnswer, addIceCandidate, hangup } = useWebRTC(props.callType)
const callTitle = computed(() => `${props.callType === 'voice' ? '语音' : '视频'}通话 - ${props.targetUser?.nickname}`)
const callStatusText = computed(() => isConnected.value ? '通话中' : (props.isCaller ? '正在呼叫...' : '来电'))
const onRemoteStream = (s: MediaStream) => { remoteStream.value = s }
const flushCandidates = async () => { for (const c of pendingCandidates.splice(0)) { await addIceCandidate(c.candidate, c.sdpMid, c.sdpMLineIndex) } }

const doHangup = (showMessage: string | null) => {
  if (hangupSent) return; hangupSent = true; hangup(); remoteStream.value = null
  if (showMessage) notify.info(showMessage)
  if (visible.value) { visible.value = false; emit('update:modelValue', false); emit('endCall') }
}

const handleSignal = async (data: any) => {
  if (data.fromUserId !== targetUserId.value) return
  if (data.action === 'answer') { await handleAnswer(data.sdp); flushCandidates() }
  else if (data.action === 'ice-candidate' && data.candidate) { remoteStream.value ? await addIceCandidate(data.candidate, data.sdpMid, data.sdpMLineIndex) : pendingCandidates.push(data) }
  else if (data.action === 'hangup') { doHangup('对方已挂断') }
}

const startCall = async () => {
  if (!targetUserId.value) { doHangup('通话对象信息错误'); return }
  try { await createOffer(onRemoteStream, targetUserId.value) }
  catch (err) { console.error('发起通话失败:', err); hangup() }
}
const acceptCall = async () => {
  if (!targetUserId.value) return
  try {
    if (props.initialOffer?.sdp) { await handleOffer(props.initialOffer.sdp, onRemoteStream, targetUserId.value); flushCandidates(); emit('callAccepted') }
    else { doHangup('未收到通话请求') }
  } catch (err) { console.error('接听通话失败:', err); hangup() }
}
const hangupCall = () => {
  if (hangupSent) return
  if (targetUserId.value) websocketService.sendCallSignal({ action: 'hangup', toUserId: targetUserId.value, callType: props.callType })
  doHangup(null)
}

let registered = false
watch(() => props.modelValue, async (val) => {
  visible.value = val
  if (val) { hangupSent = false; if (!registered) { websocketService.onCallSignal(handleSignal); registered = true }; if (props.isCaller) await startCall() }
  else { hangup() }
}, { immediate: true })
onUnmounted(() => { if (!hangupSent) hangup() })
</script>

<style scoped>
.call-container { text-align: center; padding: 20px; position: relative; }
.video-call { height: 400px; position: relative; }
</style>
