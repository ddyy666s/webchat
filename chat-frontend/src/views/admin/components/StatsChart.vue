<template>
  <el-card class="chart-card">
    <template #header>
      <span>数据统计图表</span>
    </template>
    <div ref="chartRef" class="chart-container"></div>
  </el-card>
</template>

<script setup lang="ts">
/** 数据统计图表组件（ECharts 饼图），30 秒自动刷新 @component */
import { ref, onMounted, onBeforeUnmount, watch } from 'vue'
import * as echarts from 'echarts'
import type { StatisticsVO } from '@/api/admin'

/** 组件属性：统计数据 */
const props = defineProps<{
  stats: StatisticsVO
}>()

/** 组件事件：触发刷新 */
const emit = defineEmits<{
  (e: 'refresh'): void
}>()

/** 图表容器引用 */
const chartRef = ref<HTMLElement>()
/** ECharts 实例 */
let chartInstance: echarts.ECharts | null = null
/** 自动刷新定时器 ID */
let refreshTimer: number | null = null

/** 生成饼图数据 @returns 饼图数据数组 */
const getChartData = () => {
  return [
    { name: '总用户数', value: props.stats.totalUsers || 0 },
    { name: '今日活跃', value: props.stats.todayActiveUsers || 0 },
    { name: '今日消息', value: props.stats.todayMessages || 0 },
    { name: '在线人数', value: props.stats.onlineUsers || 0 }
  ]
}

/** 初始化图表 @returns void */
const initChart = () => {
  if (!chartRef.value) return

  chartInstance = echarts.init(chartRef.value)

  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left',
      data: getChartData().map(item => item.name)
    },
    series: [
      {
        name: '平台数据统计',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: true,
          formatter: '{b}: {d}%',
          position: 'outside'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 16,
            fontWeight: 'bold'
          }
        },
        data: getChartData()
      }
    ]
  }

  chartInstance.setOption(option)
}

/** 更新图表数据 @returns void */
const updateChart = () => {
  if (chartInstance) {
    chartInstance.setOption({
      legend: {
        data: getChartData().map(item => item.name)
      },
      series: [{
        data: getChartData()
      }]
    })
  }
}

/** 窗口大小变化时自适应 */
const handleResize = () => {
  chartInstance?.resize()
}

onMounted(() => {
  initChart()
  window.addEventListener('resize', handleResize)
  refreshTimer = setInterval(() => emit('refresh'), 30000) as unknown as number
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  if (refreshTimer) clearInterval(refreshTimer)
  chartInstance?.dispose()
})

/** 监听 stats 变化，更新图表 */
watch(() => props.stats, () => {
  updateChart()
}, { deep: true })
</script>

<style scoped>
.chart-card {
  margin-top: 0;
  border-radius: 16px !important;
  border: none !important;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.04) !important;
}

.chart-card :deep(.el-card__header) {
  padding: 16px 24px;
  border-bottom: 1px solid var(--border-color-lighter);
  font-weight: 600;
  font-size: 16px;
}

.chart-card :deep(.el-card__body) {
  padding: 20px;
}

.chart-container {
  width: 100%;
  height: 400px;
}
</style>
