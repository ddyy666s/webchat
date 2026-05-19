<template>
  <div class="impression-list">
    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <el-tab-pane label="对我的评价" name="to-me">
        <Loading :visible="loadingToMe" text="加载评价中..." />
        <template v-if="!loadingToMe">
          <template v-if="paginatedToMe.length > 0"><ImpressionItem v-for="imp in paginatedToMe" :key="imp.id" :impression="imp" type="to-me" /></template>
          <Empty v-else title="暂无评价" description="还没有人给你留下评价" />
          <div v-if="impressionsToMe.length > 0 && hasMoreToMe" class="load-more-wrapper"><el-button :loading="loadingMoreToMe" type="primary" text @click="loadMoreToMe">{{ loadingMoreToMe ? '加载中...' : '加载更多' }}</el-button></div>
          <div v-if="!hasMoreToMe && impressionsToMe.length > 0" class="no-more">没有更多了</div>
        </template>
      </el-tab-pane>
      <el-tab-pane label="我给出的评价" name="by-me">
        <Loading :visible="loadingByMe" text="加载评价中..." />
        <template v-if="!loadingByMe">
          <template v-if="paginatedByMe.length > 0"><ImpressionItem v-for="imp in paginatedByMe" :key="imp.id" :impression="imp" type="by-me" @delete="handleDelete" /></template>
          <Empty v-else title="暂无评价" description="还没有给好友留下评价"><el-button type="primary" size="small" @click="$emit('add')">去评价好友</el-button></Empty>
          <div v-if="impressionsByMe.length > 0 && hasMoreByMe" class="load-more-wrapper"><el-button :loading="loadingMoreByMe" type="primary" text @click="loadMoreByMe">{{ loadingMoreByMe ? '加载中...' : '加载更多' }}</el-button></div>
          <div v-if="!hasMoreByMe && impressionsByMe.length > 0" class="no-more">没有更多了</div>
        </template>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
/** 评价列表组件，支持分页和 Tab 切换 @component */
import { ref, computed, watch } from 'vue'
import type { ImpressionVO } from '@/api/impression'
import Empty from '@/components/common/Empty.vue'
import Loading from '@/components/common/Loading.vue'
import ImpressionItem from './ImpressionItem.vue'

const props = defineProps<{ impressionsToMe: ImpressionVO[]; impressionsByMe: ImpressionVO[]; loadingToMe: boolean; loadingByMe: boolean }>()
const emit = defineEmits<{ delete: [id: number]; add: []; tabChange: [tab: string] }>()

const activeTab = ref('to-me'); const pageSize = 10; const currentPageToMe = ref(1); const currentPageByMe = ref(1)
const hasMoreToMe = computed(() => currentPageToMe.value * pageSize < props.impressionsToMe.length)
const hasMoreByMe = computed(() => currentPageByMe.value * pageSize < props.impressionsByMe.length)
const paginatedToMe = computed(() => props.impressionsToMe.slice(0, currentPageToMe.value * pageSize))
const paginatedByMe = computed(() => props.impressionsByMe.slice(0, currentPageByMe.value * pageSize))
const loadingMoreToMe = ref(false); const loadingMoreByMe = ref(false)

const loadMoreToMe = () => { if (loadingMoreToMe.value || !hasMoreToMe.value) return; loadingMoreToMe.value = true; currentPageToMe.value++; loadingMoreToMe.value = false }
const loadMoreByMe = () => { if (loadingMoreByMe.value || !hasMoreByMe.value) return; loadingMoreByMe.value = true; currentPageByMe.value++; loadingMoreByMe.value = false }
const handleTabChange = (tab: string) => { emit('tabChange', tab) }
const handleDelete = (id: number) => { emit('delete', id) }
watch(() => props.impressionsToMe, () => { currentPageToMe.value = 1 }, { deep: true })
watch(() => props.impressionsByMe, () => { currentPageByMe.value = 1 }, { deep: true })
</script>

<style scoped>
.impression-list { height: 100%; }
.load-more-wrapper { text-align: center; padding: 16px 0; }
.no-more { text-align: center; padding: 16px 0; font-size: 13px; color: var(--text-secondary); font-weight: 500; }
.impression-list :deep(.el-tabs__item) { font-size: 14px; font-weight: 500; }
</style>
