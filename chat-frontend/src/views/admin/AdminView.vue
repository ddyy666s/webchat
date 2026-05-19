<template>
  <div class="admin-layout">
    <AdminSidebar :active-menu="activeMenu" @select="handleMenuSelect" />

    <div class="admin-main">
      <AdminHeader :title="pageTitle" />

      <div class="admin-content">
        <template v-if="activeMenu === 'stats'">
          <StatsCards :stats="stats" />
          <StatsChart :stats="stats" @refresh="loadStats" />
        </template>

        <UserManage v-if="activeMenu === 'users'" />
        <MessageAudit v-if="activeMenu === 'messages'" />
        <NotificationManage v-if="activeMenu === 'notifications'" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
/** 管理后台主页面组件 @component */
import { ref, computed, onMounted } from 'vue'
import { getAdminStatsApi, type StatisticsVO } from '@/api/admin'
import AdminSidebar from './components/AdminSidebar.vue'
import AdminHeader from './components/AdminHeader.vue'
import StatsCards from './components/StatsCards.vue'
import StatsChart from './components/StatsChart.vue'
import UserManage from './components/UserManage.vue'
import MessageAudit from './components/MessageAudit.vue'
import NotificationManage from './components/NotificationManage.vue'

/** 当前激活的菜单项 */
const activeMenu = ref('stats')
/** 统计数据 */
const stats = ref<StatisticsVO>({
  totalUsers: 0,
  todayActiveUsers: 0,
  todayMessages: 0,
  onlineUsers: 0
})

/** 根据激活菜单计算页面标题 @returns 标题文本 */
const pageTitle = computed(() => {
  const titles: Record<string, string> = {
    stats: '数据统计',
    users: '用户管理',
    messages: '消息审计',
    notifications: '系统通知'
  }
  return titles[activeMenu.value] || '管理后台'
})

/** 加载统计数据 @returns Promise<void> */
const loadStats = async () => {
  stats.value = await getAdminStatsApi()
}

/** 菜单选择处理 @param menu 菜单标识 @returns void */
const handleMenuSelect = (menu: string) => {
  activeMenu.value = menu
  if (menu === 'stats') {
    loadStats()
  }
}

onMounted(() => {
  loadStats()
})
</script>

<style scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
  background: var(--bg-color);
}

.admin-main {
  flex: 1;
  margin-left: 240px;
  display: flex;
  flex-direction: column;
}

.admin-content {
  padding: 24px;
}
</style>
