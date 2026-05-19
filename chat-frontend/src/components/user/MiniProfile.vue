<template>
  <el-popover placement="right-start" :width="280" trigger="hover" :show-after="500" :hide-after="200">
    <template #reference>
      <slot />
    </template>

    <div class="mini-profile">
      <div class="profile-header">
        <el-avatar :size="60" :src="userInfo?.avatar || defaultAvatar" />
        <div class="info">
          <div class="name">{{ userInfo?.nickname || userInfo?.username }}</div>
          <div class="status" :class="{ online: userInfo?.isOnline }">
            {{ userInfo?.isOnline ? '在线' : '离线' }}
          </div>
        </div>
      </div>

      <div class="profile-detail">
        <div class="row">
          <span class="label">签名：</span>
          <span class="value">{{ userInfo?.signature || '这个人很懒，什么都没写' }}</span>
        </div>
      </div>

      <div class="profile-actions">
        <el-button type="primary" size="small" @click="startChat">发消息</el-button>
        <el-button size="small" @click="showImpression">写印象</el-button>
      </div>
    </div>
  </el-popover>
</template>

<script setup lang="ts">
/** 迷你个人资料卡片组件，通过 Popover 悬浮展示 @component */
import { ref, onMounted } from 'vue'
import { getUserProfileApi } from '@/api/user'
import defaultAvatar from '@/assets/images/default-avatar.png'

/** 组件属性：用户 ID */
const props = defineProps<{
  userId: number
}>()

/** 组件事件：发起聊天、写印象 */
const emit = defineEmits<{
  (e: 'startChat', userId: number): void
  (e: 'writeImpression', userId: number): void
}>()
/** 用户信息 */
const userInfo = ref<any>(null)

/** 获取用户信息 @returns Promise<void> */
const fetchUserInfo = async () => {
  try {
    const res = await getUserProfileApi(props.userId)
    userInfo.value = res
  } catch {
  }
}

/** 发起聊天 @returns void */
const startChat = () => {
  emit('startChat', props.userId)
}

/** 写印象 @returns void */
const showImpression = () => {
  emit('writeImpression', props.userId)
}

onMounted(() => {
  fetchUserInfo()
})
</script>

<style scoped>
.mini-profile {
  padding: 12px;
}

.profile-header {
  display: flex;
  gap: 16px;
  margin-bottom: 16px;
}

.info .name {
  font-size: 17px;
  font-weight: 600;
  margin-bottom: 4px;
  color: var(--text-primary);
}

.info .status {
  font-size: 13px;
  color: var(--text-secondary);
  font-weight: 500;
}

.info .status.online {
  color: var(--color-success);
}

.profile-detail {
  margin-bottom: 16px;
  background: var(--bg-color);
  padding: 12px 14px;
  border-radius: 12px;
}

.profile-detail .row {
  font-size: 13px;
  margin-bottom: 6px;
}

.profile-detail .row:last-child {
  margin-bottom: 0;
}

.profile-detail .label {
  color: var(--text-secondary);
}

.profile-detail .value {
  color: var(--text-regular);
}

.profile-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

.profile-actions .el-button {
  border-radius: 12px !important;
  font-weight: 600 !important;
}
</style>
