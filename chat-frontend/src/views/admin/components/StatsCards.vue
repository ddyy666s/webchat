<template>
  <div class="stats-cards">
    <el-card class="stat-card">
      <div class="stat-value">{{ stats.totalUsers }}</div>
      <div class="stat-label">总用户数</div>
    </el-card>
    <el-card class="stat-card">
      <div class="stat-value">{{ stats.todayActiveUsers }}</div>
      <div class="stat-label">今日活跃</div>
    </el-card>
    <el-card class="stat-card">
      <div class="stat-value">{{ stats.todayMessages }}</div>
      <div class="stat-label">今日消息</div>
    </el-card>
    <el-card class="stat-card">
      <div class="stat-value">{{ stats.onlineUsers }}</div>
      <div class="stat-label">在线人数</div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
/** 数据统计卡片组件 @component */
import type { StatisticsVO } from '@/api/admin'

/** 组件属性：统计数据 */
defineProps<{
  stats: StatisticsVO
}>()
</script>

<style scoped>
.stats-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  text-align: center;
  border-radius: 16px !important;
  border: none !important;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.04) !important;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 30px rgba(108, 92, 231, 0.1) !important;
}

.stat-card :deep(.el-card__body) {
  padding: 24px 20px;
}

.stat-value {
  font-size: 34px;
  font-weight: 700;
  background: linear-gradient(135deg, var(--color-primary), var(--color-primary-light));
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}

.stat-label {
  font-size: 14px;
  color: var(--text-secondary);
  margin-top: 8px;
  font-weight: 500;
}
</style>
