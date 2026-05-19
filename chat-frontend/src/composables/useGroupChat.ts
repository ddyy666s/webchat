import { ref, computed, watch, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  getGroupHistoryApi, quitGroupApi, disbandGroupApi, inviteMemberApi,
  getGroupMembersApi, clearGroupUnreadApi, updateGroupNoticeApi,
  type GroupVO, type GroupMessageVO, type GroupMemberVO
} from '@/api/group'
import { getFriendListApi, type FriendVO } from '@/api/friend'
import { websocketService } from '@/utils/websocket'
import { useUserStore } from '@/stores/userStore'
export function useGroupChat(props: { group: GroupVO | null }, emit: (event: string, ...args: any[]) => void) {
  const userStore = useUserStore()
  const currentUserId = userStore.userInfo?.id
  const isOwner = computed(() => props.group?.ownerId === currentUserId)
  const memberList = ref<GroupMemberVO[]>([])
  const canEditNotice = computed(() => {
    const member = memberList.value.find(m => m.userId === currentUserId)
    return isOwner.value || member?.role === 1
  })
  const isMuted = computed(() => {
    const member = memberList.value.find(m => m.userId === currentUserId)
    return member?.muted === true
  })
  const messages = ref<GroupMessageVO[]>([])
  const loading = ref(false)
  const page = ref(1)
  const hasMore = ref(true)
  const messageListRef = ref()
  const showNoticeDialog = ref(false)
  const showMembers = ref(false)
  const showManage = ref(false)
  const showInvite = ref(false)
  const showQuitDialog = ref(false)
  const showDisbandDialog = ref(false)
  const friendList = ref<FriendVO[]>([])
  const loadHistory = async (reset = true) => {
    if (!props.group?.id) return
    if (reset) { page.value = 1; hasMore.value = true; messages.value = [] }
    if (!hasMore.value) return
    loading.value = true
    try {
      const res = await getGroupHistoryApi(props.group.id, page.value, 20)
      const newMessages = res.records.reverse()
      if (reset) messages.value = newMessages
      else messages.value = [...newMessages, ...messages.value]
      hasMore.value = newMessages.length > 0
      page.value++
    } catch (error) { console.error(error) }
    finally { loading.value = false }
  }
  const loadMore = () => {
    if (!loading.value && hasMore.value) loadHistory(false)
  }
  const clearUnreadCount = async () => {
    if (!props.group?.id) return
    try { await clearGroupUnreadApi(props.group.id); emit('updateUnreadCount', props.group.id) }
    catch (error) { console.error('清除未读失败', error) }
  }
  const sendMessage = (content: string) => {
    if (!props.group?.id) return
    if (isMuted.value) { ElMessage.warning('你已被禁言'); return }
    const tempMsg: GroupMessageVO = {
      id: Date.now(), groupId: props.group.id, fromUserId: currentUserId!,
      fromUserNickname: userStore.userInfo?.nickname || '我', fromUserAvatar: userStore.userInfo?.avatar ?? null,
      content: content, messageType: 1, sendTime: new Date().toISOString()
    }
    messages.value.push(tempMsg)
    messageListRef.value?.scrollToBottom()
    websocketService.sendGroupMessage(props.group.id, content)
  }
  const onGroupMessage = (data: any) => {
    if (props.group && data.groupId === props.group.id) {
      messages.value.push({
        id: data.messageId, groupId: data.groupId, fromUserId: data.fromUserId,
        fromUserNickname: data.fromUserNickname, fromUserAvatar: data.fromUserAvatar ?? null,
        content: data.content, messageType: data.messageType, sendTime: data.sendTime
      })
      messageListRef.value?.scrollToBottom()
    }
  }
  const loadMembers = async () => {
    if (!props.group?.id) return
    try { memberList.value = await getGroupMembersApi(props.group.id) }
    catch (error) { console.error('加载群成员失败', error) }
  }
  const loadFriendList = async () => {
    try {
      const res = await getFriendListApi(); const friends: FriendVO[] = []
      for (const group of res) friends.push(...group.friends)
      friendList.value = friends
    } catch (error) { console.error('加载好友列表失败', error) }
  }
  const handleUpdateNotice = async (notice: string) => {
    if (!props.group?.id) return
    try {
      await updateGroupNoticeApi(props.group.id, notice)
      if (props.group) props.group.notice = notice
      ElMessage.success('群公告已更新'); emit('update:list')
    } catch (error) {
      console.error(error); ElMessage.error('更新失败'); throw error
    }
  }
  const openNotice = () => { showNoticeDialog.value = true }
  const handleGroupCommand = async (command: string) => {
    if (!props.group) return
    if (command === 'notice' || command === 'viewNotice') showNoticeDialog.value = true
    else if (command === 'members') { await loadMembers(); showMembers.value = true }
    else if (command === 'invite') { await loadFriendList(); showInvite.value = true }
    else if (command === 'manage') { await loadMembers(); showManage.value = true }
    else if (command === 'quit') showQuitDialog.value = true
    else if (command === 'disband') showDisbandDialog.value = true
  }
  const confirmQuit = async () => {
    await quitGroupApi(props.group!.id); ElMessage.success('已退出群聊'); emit('update:list')
  }
  const confirmDisband = async () => {
    await disbandGroupApi(props.group!.id); ElMessage.success('群聊已解散'); emit('update:list')
  }
  const handleInvite = async (userId: number) => {
    if (!props.group?.id) return
    try { await inviteMemberApi({ groupId: props.group.id, userId }); ElMessage.success('邀请已发送') }
    catch (error) { console.error(error); ElMessage.error('邀请失败'); throw error }
  }
  watch(() => props.group, (newGroup) => {
    if (newGroup?.id) { loadHistory(true); clearUnreadCount(); loadMembers() }
  }, { immediate: true })
  onMounted(() => { websocketService.onGroupMessage(onGroupMessage) })
  return {
    currentUserId, isOwner, canEditNotice, isMuted, messages, loading, page, hasMore, messageListRef,
    showNoticeDialog, showMembers, showManage, showInvite, showQuitDialog, showDisbandDialog,
    friendList, loadMore, sendMessage, loadMembers, loadFriendList,
    handleUpdateNotice, openNotice, handleGroupCommand, confirmQuit, confirmDisband, handleInvite
  }
}
