<template>
  <div class="sidebar">
    <SidebarHeader />
    <SidebarTabs :active-tab="activeTab" :badge-count="friendStore.friendRequests.length" :is-admin="userStore.isAdmin()" @update:active-tab="activeTab = $event" @go-to-admin="goToAdmin" />
    <div class="sidebar-content">
      <FriendList v-show="activeTab === 'friends'" :current-chat-user-id="currentChatUserId" @select-chat="handleSelectChat" @write-impression="handleWriteImpression" />
      <GroupList v-show="activeTab === 'groups'" :groups="groupList" :current-group-id="currentGroupId" @select="selectGroup" @create="showCreateGroupDialog = true" />
      <RequestList v-show="activeTab === 'requests'" :requests="friendStore.friendRequests" @agree="handleRequest($event, 1)" @reject="handleRequest($event, 2)" />
      <ImpressionBoard v-show="activeTab === 'impressions'" :target-user-id="impressionTargetUserId" @clear-target="impressionTargetUserId = null" />
    </div>
    <CreateGroupDialog v-model="showCreateGroupDialog" :friend-list="friendListForGroup" @submit="handleCreateGroup" />
  </div>
</template>

<script setup lang="ts">
/** 侧边栏组件，整合好友/群聊/申请/印象选项卡 @component */
import { ref, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/userStore'
import { useFriendStore } from '@/stores/friendStore'
import { handleFriendRequestApi } from '@/api/friend'
import { getGroupListApi, createGroupApi, type GroupVO } from '@/api/group'
import { getFriendListApi, type FriendVO } from '@/api/friend'
import { websocketService } from '@/utils/websocket'
import FriendList from '@/components/friend/FriendList.vue'
import ImpressionBoard from '@/components/impression/ImpressionBoard.vue'
import SidebarHeader from './sidebar/SidebarHeader.vue'
import SidebarTabs from './sidebar/SidebarTabs.vue'
import GroupList from './sidebar/GroupList.vue'
import RequestList from './sidebar/RequestList.vue'
import CreateGroupDialog from './sidebar/CreateGroupDialog.vue'

const emit = defineEmits(['selectChat', 'selectGroup'])
const router = useRouter(); const route = useRoute(); const userStore = useUserStore(); const friendStore = useFriendStore()

const activeTab = ref('friends'); const currentChatUserId = ref<number | null>(null); const currentGroupId = ref<number | null>(null)
const groupList = ref<GroupVO[]>([]); const showCreateGroupDialog = ref(false); const friendListForGroup = ref<FriendVO[]>([]); const impressionTargetUserId = ref<number | null>(null)

const goToAdmin = () => { router.push('/admin') }
const loadGroupList = async () => { try { groupList.value = await getGroupListApi() } catch (error) { console.error('加载群聊列表失败', error) } }
const selectGroup = (group: GroupVO) => { currentGroupId.value = group.id; group.unreadCount = 0; emit('selectGroup', group) }

const handleCreateGroup = async (data: { name: string; notice?: string; memberIds: number[] }) => {
  try { const newGroup = await createGroupApi(data); groupList.value.unshift(newGroup); ElMessage.success('群聊创建成功'); selectGroup(newGroup) }
  catch (error) { console.error(error); ElMessage.error('创建失败'); throw error }
}
const loadFriendListForGroup = async () => { const res = await getFriendListApi(); const friends: FriendVO[] = []; for (const group of res) friends.push(...group.friends); friendListForGroup.value = friends }

const handleRequest = async (requestId: number, status: number) => {
  try { await handleFriendRequestApi(requestId, status); ElMessage.success(status === 1 ? '已同意' : '已拒绝'); friendStore.loadFriendRequests(); friendStore.loadFriendList() }
  catch (error) { console.error(error); ElMessage.error('操作失败') }
}

const handleWriteImpression = (userId: number) => { impressionTargetUserId.value = userId; activeTab.value = 'impressions' }
const handleSelectChat = (friend: any) => {
  const userId = friend?.userId || friend?.id
  if (userId) { currentChatUserId.value = userId; emit('selectChat', { id: userId, userId, nickname: friend?.nickname || friend?.name || '好友', avatar: friend?.avatar, isOnline: friend?.isOnline, remark: friend?.remark }) }
}
const onGroupMessage = (data: any) => { const group = groupList.value.find(g => g.id === data.groupId); if (group && currentGroupId.value !== data.groupId) group.unreadCount = (group.unreadCount || 0) + 1 }

watch(() => route.query, () => { loadGroupList(); friendStore.loadFriendList(); friendStore.loadFriendRequests() }, { deep: true })

onMounted(() => {
  friendStore.loadFriendList(); friendStore.loadFriendRequests(); loadGroupList(); loadFriendListForGroup()
  websocketService.onStatus((data: any) => { if (data.userId) friendStore.updateFriendOnlineStatus?.(data.userId, data.online) })
  websocketService.onGroupMessage(onGroupMessage)
  websocketService.onFriendRequest(() => { friendStore.loadFriendRequests() })
  websocketService.onFriendRequestHandled(() => { friendStore.loadFriendList(); friendStore.loadFriendRequests() })
})
</script>

<style scoped>
.sidebar { width: var(--sidebar-width); background: var(--bg-color-white); display: flex; flex-direction: column; flex-shrink: 0; position: relative; z-index: 20; border: 3px solid #b3d9ff; border-radius: 24px; box-shadow: var(--box-shadow-base); overflow: hidden; }
.sidebar-content { flex: 1; overflow-y: auto; overflow-x: hidden; }
.sidebar-content::-webkit-scrollbar { width: 4px; }
.sidebar-content::-webkit-scrollbar-thumb { background: transparent; border-radius: 4px; }
.sidebar-content:hover::-webkit-scrollbar-thumb { background: var(--text-secondary); }
</style>
