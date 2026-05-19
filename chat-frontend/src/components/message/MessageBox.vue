<template>
  <el-drawer v-model="visible" title="消息盒子" direction="rtl" size="440px" :before-close="handleClose" class="beautiful-drawer">
    <div class="message-box-tabs">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="聊天消息" name="chat">
          <MessageList :messages="unreadMessages" @click="handleMessageClick" />
        </el-tab-pane>
        <el-tab-pane :label="`系统通知${notificationCount > 0 ? ` (${notificationCount})` : ''}`" name="notification">
          <NotificationList :notifications="notifications" @open="openNotification" />
        </el-tab-pane>
      </el-tabs>
    </div>
    <NotificationDialog v-model="showNotificationDetail" :notification="selectedNotification" @close="handleNotificationRead" />
  </el-drawer>
</template>

<script setup lang="ts">
/** 消息盒子抽屉组件，聚合未读聊天消息和系统通知 @component */
import { ref, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useMessageStore } from '@/stores/messageStore'
import { markAsReadApi } from '@/api/message'
import { getUnreadNotificationsApi, markNotificationAsReadApi } from '@/api/notification'
import type { SystemNotification } from '@/api/notification'
import MessageList from '../messageBox/MessageList.vue'
import NotificationList from '../messageBox/NotificationList.vue'
import NotificationDialog from '../messageBox/NotificationDialog.vue'

const props = defineProps<{ modelValue: boolean }>()
const emit = defineEmits(['update:modelValue'])
const router = useRouter()
const messageStore = useMessageStore()

const visible = computed({ get: () => props.modelValue, set: (val) => emit('update:modelValue', val) })
const activeTab = ref('chat')
const notifications = ref<SystemNotification[]>([])
const notificationCount = ref(0)
const showNotificationDetail = ref(false)
const selectedNotification = ref<SystemNotification | null>(null)

const unreadMessages = computed(() => {
  const messages = messageStore.unreadCount?.messages || []
  return messages.map((msg: any) => ({
    id: msg.id, fromUserId: msg.fromUserId, fromUserNickname: msg.fromUserNickname,
    fromUserAvatar: msg.fromUserAvatar || undefined, content: msg.content,
    messageType: msg.messageType ?? 0, sendTime: msg.sendTime,
    unreadCount: msg.unreadCount || 1, isOnline: msg.isOnline || false
  }))
})

const loadNotifications = async () => {
  const res = await getUnreadNotificationsApi()
  if (res && res.notifications) { notifications.value = res.notifications; notificationCount.value = res.total || 0 }
}

const openNotification = (n: SystemNotification) => { selectedNotification.value = n; showNotificationDetail.value = true }

const handleNotificationRead = async () => {
  showNotificationDetail.value = false
  if (selectedNotification.value) {
    try {
      await markNotificationAsReadApi(selectedNotification.value.id)
      notifications.value = notifications.value.filter(n => n.id !== selectedNotification.value!.id)
      notificationCount.value = Math.max(0, notificationCount.value - 1)
    } catch { /* ignore */ }
  }
  selectedNotification.value = null
}

const handleClose = () => { visible.value = false }

const handleMessageClick = async (friendId: number) => {
  visible.value = false
  try { await markAsReadApi(friendId); messageStore.loadUnreadCount() } catch (error) { console.error(error) }
  router.push(`/?friendId=${friendId}`)
}

watch(() => props.modelValue, (val) => { if (val) { messageStore.loadUnreadCount(); loadNotifications() } })
</script>

<style scoped>
.message-box-tabs { height: 100%; }
.message-box-tabs :deep(.el-tabs__content) { height: calc(100% - 40px); overflow: hidden; }
.message-box-tabs :deep(.el-tab-pane) { height: 100%; overflow-y: auto; }
</style>
