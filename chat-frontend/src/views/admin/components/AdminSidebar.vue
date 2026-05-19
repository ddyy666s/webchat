<template>
  <div class="admin-sidebar">
    <div class="logo">
      <h3>管理后台</h3>
    </div>
    <el-menu :default-active="activeMenu" class="sidebar-menu" @select="handleSelect">
      <el-menu-item index="stats">
        <el-icon>
          <DataLine />
        </el-icon>
        <span>数据统计</span>
      </el-menu-item>
      <el-menu-item index="users">
        <el-icon>
          <User />
        </el-icon>
        <span>用户管理</span>
      </el-menu-item>
      <el-menu-item index="messages">
        <el-icon>
          <ChatDotRound />
        </el-icon>
        <span>消息审计</span>
      </el-menu-item>
      <el-menu-item index="notifications">
        <el-icon>
          <Bell />
        </el-icon>
        <span>系统通知</span>
      </el-menu-item>
    </el-menu>
  </div>
</template>

<script setup lang="ts">
/** 管理后台侧边栏导航组件 @component */
import { DataLine, User, ChatDotRound, Bell } from '@element-plus/icons-vue'

/** 组件属性：当前激活菜单 */
defineProps<{
  activeMenu: string
}>()

/** 组件事件：菜单选择 */
const emit = defineEmits<{
  select: [menu: string]
}>()

/** 菜单选择处理 @param index 菜单标识 @returns void */
const handleSelect = (index: string) => {
  emit('select', index)
}
</script>

<style scoped>
.admin-sidebar {
  width: 240px;
  height: 100vh;
  background: var(--bg-color-white);
  border-right: 1px solid var(--border-color-lighter);
  position: fixed;
  left: 0;
  top: 0;
  display: flex;
  flex-direction: column;
  box-shadow: 2px 0 12px rgba(0, 0, 0, 0.04);
}

.logo {
  height: var(--header-height);
  display: flex;
  align-items: center;
  justify-content: center;
  border-bottom: 1px solid var(--border-color-lighter);
  padding: 0 20px;
}

.logo h3 {
  margin: 0;
  font-size: 22px;
  font-weight: 800;
  background: linear-gradient(135deg, var(--color-primary), var(--color-success));
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  letter-spacing: 2px;
}

.sidebar-menu {
  border-right: none;
  flex: 1;
  padding: 8px;
}

.sidebar-menu :deep(.el-menu-item) {
  border-radius: 12px !important;
  margin: 2px 0;
  height: 48px;
  line-height: 48px;
  font-weight: 500;
}

.sidebar-menu :deep(.el-menu-item.is-active) {
  background: #f3f0ff !important;
  color: var(--color-primary) !important;
  font-weight: 600;
}

.sidebar-menu :deep(.el-menu-item:hover) {
  background: #f8f6ff !important;
}

.sidebar-menu :deep(.el-menu-item .el-icon) {
  font-size: 18px;
}
</style>
