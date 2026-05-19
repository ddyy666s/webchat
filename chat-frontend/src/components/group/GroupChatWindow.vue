<template>
  <div class="group-chat-window">
    <GroupChatHeader :group="group" :can-edit-notice="canEditNotice" :is-owner="isOwner" @command="handleGroupCommand" />
    <GroupNoticeBar :notice="group?.notice ?? null" @click="openNotice" />
    <GroupMessageList ref="messageListRef" :messages="messages" :current-user-id="currentUserId" :loading="loading" @load-more="loadMore" />
    <GroupMessageInput :muted="isMuted" @send="sendMessage" />
    <GroupNoticeDialog v-model="showNoticeDialog" :notice="group?.notice ?? null" :can-edit="canEditNotice" @save="handleUpdateNotice" />
    <GroupMembersDialog v-model="showMembers" :members="memberList" />
    <GroupManagementDialog v-model="showManage" :group-id="props.group?.id || 0" :members="memberList" :current-user-id="currentUserId!" :is-owner="isOwner" :can-manage="canEditNotice" @refresh="loadMembers" />
    <InviteFriendDialog v-model="showInvite" :friends="friendList" @invite="handleInvite" />
    <ConfirmDialog v-model="showQuitDialog" title="退出群聊" message="确定要退出该群聊吗？" type="warning" confirm-text="退出" cancel-text="取消" @confirm="confirmQuit" />
    <ConfirmDialog v-model="showDisbandDialog" title="解散群聊" message="确定要解散该群聊吗？此操作不可恢复" type="danger" confirm-text="解散" cancel-text="取消" @confirm="confirmDisband" />
  </div>
</template>

<script setup lang="ts">
/** 群聊聊天窗口组件，管理群聊消息/成员/公告/管理等完整功能 @component */
import type { GroupVO } from '@/api/group'
import { useGroupChat } from '@/composables/useGroupChat'
import GroupChatHeader from './GroupChatHeader.vue'
import GroupNoticeBar from './GroupNoticeBar.vue'
import GroupMessageList from './GroupMessageList.vue'
import GroupMessageInput from './GroupMessageInput.vue'
import GroupNoticeDialog from './GroupNoticeDialog.vue'
import GroupMembersDialog from './GroupMembersDialog.vue'
import GroupManagementDialog from './GroupManagementDialog.vue'
import InviteFriendDialog from './InviteFriendDialog.vue'
import ConfirmDialog from '@/components/common/ConfirmDialog.vue'

const props = defineProps<{ group: GroupVO | null }>()
const emit = defineEmits(['update:list', 'updateUnreadCount'])

const {
  currentUserId, isOwner, canEditNotice, isMuted, messages, loading, messageListRef,
  showNoticeDialog, showMembers, showManage, showInvite, showQuitDialog, showDisbandDialog,
  memberList, friendList, loadMore, sendMessage, handleUpdateNotice, openNotice,
  handleGroupCommand, confirmQuit, confirmDisband, handleInvite, loadMembers
} = useGroupChat(props, emit)
</script>

<style scoped>
.group-chat-window { height: 100%; display: flex; flex-direction: column; background: white; }
</style>
