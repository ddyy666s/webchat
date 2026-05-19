<template>
  <div class="chat-view">
    <ChatWindow v-if="currentChatUser && currentChatUser.userId && !currentGroup" :friend="currentChatUser" :key="currentChatUser.userId" />
    <GroupChatWindow v-else-if="currentGroup" :group="currentGroup" :key="currentGroup.id" @update:list="refreshGroupList" />
    <EmptyChatState v-else />
  </div>
</template>

<script setup lang="ts">
/** 聊天主视图组件，根据路由参数切换好友聊天或群聊 @component */
import { ref, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import ChatWindow from '@/components/message/ChatWindow.vue'
import GroupChatWindow from '@/components/group/GroupChatWindow.vue'
import EmptyChatState from '@/components/common/EmptyChatState.vue'
import { useFriendStore } from '@/stores/friendStore'
import { getGroupDetailApi } from '@/api/group'

const route = useRoute(); const router = useRouter(); const friendStore = useFriendStore()
const currentChatUser = ref<any>(null); const currentGroup = ref<any>(null)

const loadFriendById = async (friendId: number) => {
  if (friendStore.friendList.length === 0) await friendStore.loadFriendList()
  const friend = friendStore.getFriendById(friendId)
  if (friend) { currentChatUser.value = friend; currentGroup.value = null }
  else { currentChatUser.value = { userId: friendId, nickname: '好友' } }
}

const loadGroupById = async (groupId: number) => {
  try { const group = await getGroupDetailApi(groupId); currentGroup.value = group; currentChatUser.value = null }
  catch (error) { console.error('加载群聊失败', error) }
}

watch(() => route.query.friendId, (friendId) => { if (friendId) loadFriendById(Number(friendId)) }, { immediate: true })
watch(() => route.query.groupId, (groupId) => { if (groupId) loadGroupById(Number(groupId)) }, { immediate: true })

const refreshGroupList = () => { currentGroup.value = null; currentChatUser.value = null; router.push({ query: {} }) }
</script>

<style scoped>
.chat-view { height: 100%; }
</style>
