<template>
  <div class="friend-list">
    <div class="friend-header">
      <h3>好友列表</h3>
      <el-button class="add-friend-btn" @click="showAddDialog = true"><el-icon :size="16"><Plus /></el-icon><span>添加</span></el-button>
    </div>
    <el-scrollbar class="friend-scroll">
      <FriendGroup v-for="group in friendStore.friendList" :key="group.groupName" :group-name="group.groupName" :friends="group.friends" :current-chat-user-id="currentChatUserId" @select-chat="handleSelectChat" @command="handleCommand" @write-impression="(uid) => emit('writeImpression', uid)" />
    </el-scrollbar>
    <AddFriendDialog v-model="showAddDialog" @success="refresh" />
    <ConfirmDialog v-model="showDeleteDialog" title="删除好友" :message="deleteMessage" type="danger" confirm-text="删除" cancel-text="取消" @confirm="confirmDelete" />
    <PromptDialog v-model="showRemarkPrompt" title="修改备注" message="请输入备注" confirm-text="确定" cancel-text="取消" placeholder="请输入备注名称" @confirm="onRemarkConfirm" />
    <PromptDialog v-model="showMovePrompt" title="移动分组" message="请输入分组名称" confirm-text="确定" cancel-text="取消" placeholder="请输入分组名称" @confirm="onMoveConfirm" />
  </div>
</template>

<script setup lang="ts">
/** 好友列表组件，管理好友展示/搜索/删除/备注/分组操作 @component */
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { useFriendStore } from '@/stores/friendStore'
import { deleteFriendApi, updateFriendRemarkApi, moveFriendGroupApi } from '@/api/friend'
import FriendGroup from './FriendGroup.vue'
import AddFriendDialog from './AddFriendDialog.vue'
import ConfirmDialog from '@/components/common/ConfirmDialog.vue'
import PromptDialog from '@/components/common/PromptDialog.vue'

const props = defineProps<{ currentChatUserId: number | null }>()
const emit = defineEmits<{ (e: 'selectChat', friend: any): void; (e: 'writeImpression', userId: number): void }>()

const friendStore = useFriendStore(); const route = useRoute(); const router = useRouter()
const showAddDialog = ref(false); const showDeleteDialog = ref(false); const deleteMessage = ref(''); const deleteTargetId = ref(0)
const showRemarkPrompt = ref(false); const showMovePrompt = ref(false); let pendingFriend: any = null

onMounted(() => { friendStore.loadFriendList(); friendStore.loadFriendRequests() })

const handleSelectChat = (friend: any) => { emit('selectChat', friend) }
const handleCommand = (command: string, friend: any) => {
  if (command === 'remark') { pendingFriend = friend; showRemarkPrompt.value = true }
  else if (command === 'move') { pendingFriend = friend; showMovePrompt.value = true }
  else if (command === 'delete') { deleteMessage.value = `确定要删除好友 <strong>${friend.nickname}</strong> 吗？`; deleteTargetId.value = friend.userId; showDeleteDialog.value = true }
}
const onRemarkConfirm = async (value: string) => {
  const friend = pendingFriend; pendingFriend = null
  if (!friend || value === undefined) return
  try { await updateFriendRemarkApi(friend.userId, value); ElMessage.success('修改成功'); friendStore.loadFriendList() }
  catch { ElMessage.error('修改失败') }
}
const onMoveConfirm = async (value: string) => {
  const friend = pendingFriend; pendingFriend = null
  if (!friend || !value || value === friend.groupName) return
  try { await moveFriendGroupApi(friend.userId, value); ElMessage.success('移动成功'); friendStore.loadFriendList() }
  catch { ElMessage.error('移动失败') }
}
const confirmDelete = async () => {
  try { await deleteFriendApi(deleteTargetId.value); ElMessage.success('删除成功'); refresh(); if (route.query.friendId && Number(route.query.friendId) === deleteTargetId.value) router.push({ query: {} }) }
  catch { ElMessage.error('删除失败') }
}
const refresh = () => { friendStore.loadFriendList(); friendStore.loadFriendRequests() }
</script>

<style scoped>
.friend-list { height: 100%; display: flex; flex-direction: column; background: var(--bg-color-white); }
.friend-header { padding: 14px 20px; display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid var(--border-color-lighter); }
.friend-header h3 { margin: 0; font-size: 16px; font-weight: 600; color: var(--text-primary); }
.add-friend-btn { height: 36px; padding: 0 16px !important; border-radius: 10px !important; font-size: 13px !important; font-weight: 600 !important; gap: 5px; border: 2px solid var(--border-color) !important; color: var(--text-regular) !important; transition: all 0.25s !important; }
.add-friend-btn:hover { border-color: var(--color-primary) !important; color: var(--color-primary) !important; background: #f3f0ff !important; transform: translateY(-1px); box-shadow: 0 4px 12px rgba(108,92,231,0.12); }
.add-friend-btn:active { transform: scale(0.97); }
.friend-scroll { flex: 1; overflow-y: auto; padding: 4px 0; }
</style>
