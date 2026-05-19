import { ref, watch, nextTick, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getChatHistoryApi, downloadChatHistoryApi, markAsReadApi } from '@/api/message'
import { websocketService } from '@/utils/websocket'
import { useUserStore } from '@/stores/userStore'
import { useMessageStore } from '@/stores/messageStore'
import { useFriendStore } from '@/stores/friendStore'
import { useRtcStore } from '@/stores/rtcStore'

export function useChat(props: { friend: any }) {
  const userStore = useUserStore(); const messageStore = useMessageStore()
  const friendStore = useFriendStore(); const rtcStore = useRtcStore()
  const currentUserId = userStore.userInfo?.id

  const messages = ref<any[]>([]); const loading = ref(false)
  const page = ref(1); const hasMore = ref(true); const totalMessageCount = ref(0)
  const showDownloadDialog = ref(false)

  const voiceCallVisible = ref(false); const incomingCallVisible = ref(false)
  const incomingCaller = ref<any>(null); const incomingCallType = ref<'voice' | 'video'>('voice')
  const pendingOffer = ref<any>(null)

  let _ringUrl: string | null = null
  function getRingUrl(): string {
    if (_ringUrl === null) {
      try { _ringUrl = new URL('../../assets/audio/ring.MP3', import.meta.url).href }
      catch { _ringUrl = '' }
    }
    return _ringUrl
  }
  let _ringAudio: HTMLAudioElement | null = null
  function startRingtone() {
    const url = getRingUrl()
    if (!url || _ringAudio) return
    try {
      _ringAudio = new Audio(url); _ringAudio.loop = true; _ringAudio.volume = 0.5
      _ringAudio.preload = 'auto'; _ringAudio.load()
      _ringAudio.addEventListener('canplaythrough', () => { _ringAudio?.play().catch(() => {}) }, { once: true })
    } catch { /* ignore */ }
  }
  function stopRingtone() {
    if (_ringAudio) { _ringAudio.pause(); _ringAudio.loop = false; _ringAudio.currentTime = 0; _ringAudio = null }
  }

  const maxDownloadLimit = 500; const messageListRef = ref()

  const loadHistory = async (reset = true) => {
    if (!props.friend?.userId) return
    if (reset) { page.value = 1; hasMore.value = true; messages.value = [] }
    if (!hasMore.value) return
    loading.value = true
    try {
      const res = await getChatHistoryApi(props.friend.userId, page.value, 20)
      if (page.value === 1) totalMessageCount.value = res.total
      const newMessages = res.records.reverse()
      if (reset) messages.value = newMessages
      else messages.value = [...newMessages, ...messages.value]
      hasMore.value = newMessages.length > 0; page.value++
      if (reset) { await nextTick(); messageListRef.value?.scrollToBottom() }
    } catch (error) { ElMessage.error('加载消息失败') }
    finally { loading.value = false }
  }

  const loadMore = () => { if (!loading.value && hasMore.value) loadHistory(false) }

  const addLocalMessage = (content: string, messageType: number, duration?: number) => {
    messages.value.push({
      id: Date.now() + Math.random(), fromUserId: currentUserId,
      fromUserNickname: userStore.userInfo?.nickname || '我', fromUserAvatar: userStore.userInfo?.avatar,
      content, messageType, duration, sendTime: new Date().toISOString(), isRecalled: false
    })
    messageListRef.value?.scrollToBottom()
  }

  const sendMessage = (content: string) => {
    if (!props.friend?.userId) return; addLocalMessage(content, 1); websocketService.sendMessage(props.friend.userId, content, 1)
  }
  const sendImage = (url: string) => {
    if (!props.friend?.userId) return; addLocalMessage(url, 2); websocketService.sendMessage(props.friend.userId, url, 2)
  }
  const sendVoice = (url: string, duration: number) => {
    if (!props.friend?.userId) return; addLocalMessage(url, 4, duration); websocketService.sendMessage(props.friend.userId, url, 4, duration)
  }
  const sendEmoji = (url: string) => {
    if (!props.friend?.userId) return; addLocalMessage(url, 2); websocketService.sendMessage(props.friend.userId, url, 2)
  }

  const startVoiceCall = (toUserId: number) => {
    if (!toUserId) return ElMessage.warning('请先选择聊天对象')
    if (toUserId === currentUserId) return ElMessage.error('不能给自己打电话')
    voiceCallVisible.value = true
  }

  const startVideoCall = (toUserId: number) => {
    if (!toUserId) return ElMessage.warning('请先选择聊天对象')
    if (toUserId === currentUserId) return ElMessage.error('不能给自己打电话')
    void rtcStore.startDirectVideoCall(props.friend)
  }

  const endVoiceCall = () => { voiceCallVisible.value = false }
  const endIncomingCall = () => { incomingCallVisible.value = false; incomingCaller.value = null; pendingOffer.value = null; stopRingtone() }

  const handleDownload = async (limit: number) => {
    try { await downloadChatHistoryApi(props.friend.userId, props.friend.nickname, limit); ElMessage.success('开始下载') }
    catch { ElMessage.error('下载失败') }
  }

  const markAsRead = async () => {
    if (!props.friend?.userId) return
    try {
      await markAsReadApi(props.friend.userId); messageStore.clearUnreadForFriend(props.friend.userId)
      friendStore.clearUnreadForFriend(props.friend.userId)
    } catch (error) { console.error(error) }
  }

  const onNewMessage = (data: any) => {
    if (props.friend?.userId === data.fromUserId) {
      messages.value.push({
        id: data.messageId, fromUserId: data.fromUserId, fromUserNickname: data.fromUserNickname,
        fromUserAvatar: data.fromUserAvatar, content: data.content, messageType: data.messageType || 1,
        duration: data.duration, sendTime: data.sendTime, isRecalled: false
      })
      messageListRef.value?.scrollToBottom()
    }
  }

  const onCallSignal = (data: any) => {
    if (data.action === 'offer' && data.fromUserId !== currentUserId) {
      pendingOffer.value = data; startRingtone()
      incomingCaller.value = { id: data.fromUserId, userId: data.fromUserId, nickname: data.fromUserNickname }
      incomingCallType.value = data.callType === 'video' ? 'video' : 'voice'
      incomingCallVisible.value = true
    }
  }

  watch(() => props.friend, (newFriend) => {
    if (newFriend?.userId) { loadHistory(true); markAsRead() }
  }, { immediate: true, deep: true })

  onMounted(() => { websocketService.onMessage(onNewMessage); websocketService.onCallSignal(onCallSignal) })
  onUnmounted(() => { if (props.friend) markAsRead() })

  return {
    currentUserId, messages, loading, totalMessageCount, showDownloadDialog,
    voiceCallVisible, incomingCallVisible, incomingCaller, incomingCallType, pendingOffer,
    maxDownloadLimit, messageListRef, loadMore, sendMessage, sendImage, sendVoice, sendEmoji,
    startVoiceCall, startVideoCall, endVoiceCall, endIncomingCall, handleDownload, stopRingtone
  }
}
