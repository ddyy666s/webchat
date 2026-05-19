<template>
  <div class="admin-header">
    <el-button :icon="ArrowLeft" @click="goBack">返回聊天</el-button>
    <h2>{{ title }}</h2>
    <div></div>
  </div>
</template>

<script setup lang="ts">
/** 管理后台头部组件 @component */
import { ArrowLeft } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'

/** 组件属性：页面标题 */
defineProps<{
  title: string
}>()

const router = useRouter()

/** 返回聊天页面 @returns void */
const goBack = () => {
  router.push('/')
}
</script>

<style scoped>
.admin-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0;
  padding: 0 24px;
  height: var(--header-height);
  background: var(--bg-color-white);
  border-bottom: 1px solid var(--border-color-lighter);
  flex-shrink: 0;
}

.admin-header h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
}

.admin-header .el-button {
  border-radius: 14px !important;
  font-weight: 600 !important;
  height: 44px;
  font-size: 15px !important;
  padding: 0 24px !important;
}
</style>
