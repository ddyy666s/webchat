<template>
  <div class="communication-bar" :class="{ expanded: isExpanded }">
    <div class="toggle-btn" @click="toggleExpand"><el-icon :size="24"><component :is="isExpanded ? 'ArrowDown' : 'ArrowUp'" /></el-icon></div>
    <Toolbar :is-expanded="isExpanded" @open-image-upload="openImageUpload" @start-record="startRecord" @stop-record="stopRecord" @cancel-record="cancelRecord" @start-voice-call="startVoiceCall" @start-video-call="startVideoCall" @open-emoji-picker="openEmojiPicker" />
    <RecordingTip :is-recording="isRecording" :record-duration="recordDuration" />
    <EmojiPicker v-model="showEmojiPicker" :system-emojis="systemEmojis" :user-emojis="userEmojis" @select="sendEmoji" @upload="uploadCustomEmoji" @delete="deleteEmoji" />
    <input type="file" ref="imageInput" accept="image/*" style="display:none" @change="handleImageUpload" />
    <input type="file" ref="emojiInput" accept="image/*" style="display:none" @change="handleEmojiUpload" />
    <PromptDialog v-model="showEmojiNamePrompt" title="上传表情" message="请输入表情名称" confirm-text="确定" cancel-text="取消" :input-pattern="/^[\u4e00-\u9fa5a-zA-Z0-9]{1,20}$/" input-error-message="请输入1-20个字符" @confirm="onEmojiNameConfirm" />
  </div>
</template>

<script setup lang="ts">
/** 通信工具栏组件，提供图片/语音/表情/通话等功能的集成面板 @component */
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { uploadImageApi } from '@/api/message'
import { getSystemEmojisApi, getUserEmojisApi, uploadEmojiApi, deleteEmojiApi, type EmojiVO } from '@/api/emoji'
import { useRecording } from '@/composables/useRecording'
import Toolbar from '../communication/Toolbar.vue'
import RecordingTip from '../communication/RecordingTip.vue'
import PromptDialog from '@/components/common/PromptDialog.vue'
import EmojiPicker from '../communication/EmojiPicker.vue'

const props = defineProps<{ currentChatUserId?: number }>()
const emit = defineEmits(['sendImage', 'sendVoice', 'sendEmoji', 'startVoiceCall', 'startVideoCall'])

const { isRecording, recordDuration, startRecord, stopRecord, cancelRecord } = useRecording(emit)

const isExpanded = ref(false)
const showEmojiPicker = ref(false)
const systemEmojis = ref<EmojiVO[]>([])
const userEmojis = ref<EmojiVO[]>([])
const imageInput = ref<HTMLInputElement>()
const emojiInput = ref<HTMLInputElement>()

const toggleExpand = () => { isExpanded.value = !isExpanded.value }
const openImageUpload = () => { imageInput.value?.click() }

const handleImageUpload = async (event: Event) => {
  const input = event.target as HTMLInputElement; const file = input.files?.[0]
  if (!file) return
  if (!file.type.startsWith('image/')) return ElMessage.error('请选择图片文件')
  try { const url = await uploadImageApi(file); emit('sendImage', url) } catch { /* ignore */ }
  input.value = ''
}

const startVoiceCall = () => {
  if (props.currentChatUserId) emit('startVoiceCall', props.currentChatUserId)
  else ElMessage.warning('请先选择聊天对象')
}
const startVideoCall = () => {
  if (props.currentChatUserId) emit('startVideoCall', props.currentChatUserId)
  else ElMessage.warning('请先选择聊天对象')
}

const openEmojiPicker = async () => { await loadEmojis(); showEmojiPicker.value = true }
const loadEmojis = async () => { try { systemEmojis.value = await getSystemEmojisApi(); userEmojis.value = await getUserEmojisApi() } catch { console.error('加载表情失败') } }

const sendEmoji = (emoji: EmojiVO) => { console.log('发送表情:', emoji); emit('sendEmoji', emoji.url); ElMessage.success('表情已发送') }

const showEmojiNamePrompt = ref(false)
let pendingEmojiFile: File | null = null
const uploadCustomEmoji = () => emojiInput.value?.click()
const handleEmojiUpload = (event: Event) => {
  const input = event.target as HTMLInputElement; const file = input.files?.[0]; input.value = ''
  if (!file) return
  if (!file.type.startsWith('image/')) { ElMessage.error('请选择图片文件'); return }
  pendingEmojiFile = file; showEmojiNamePrompt.value = true
}
const onEmojiNameConfirm = async (name: string) => {
  const file = pendingEmojiFile; pendingEmojiFile = null
  if (!file || !name) return
  try { const newEmoji = await uploadEmojiApi(file, name); userEmojis.value.unshift(newEmoji); ElMessage.success('表情上传成功') }
  catch { ElMessage.error('上传失败') }
}
const deleteEmoji = async (emojiId: number) => {
  try { await deleteEmojiApi(emojiId); userEmojis.value = await getUserEmojisApi(); ElMessage.success('删除成功') }
  catch { ElMessage.error('删除失败') }
}
</script>

<style scoped>
.communication-bar { border-top: 1px solid var(--border-color-lighter); background: var(--bg-color-white); }
.toggle-btn { display: flex; justify-content: center; padding: 6px 0; cursor: pointer; color: var(--text-secondary); transition: color 0.2s; }
.toggle-btn:hover { color: var(--color-primary); }
</style>
