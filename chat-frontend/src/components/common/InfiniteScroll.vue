<template>
  <div ref="containerRef" class="infinite-scroll-container" @scroll="handleScroll">
    <div class="infinite-scroll-content"><slot /></div>
    <div v-if="loading && hasMore" class="infinite-scroll-loading"><el-icon class="is-loading"><Loading /></el-icon><span>加载中...</span></div>
    <div v-if="!hasMore && !loading && listLength > 0" class="infinite-scroll-no-more">没有更多了</div>
    <div v-if="error" class="infinite-scroll-error"><span>{{ errorMessage }}</span><el-button type="primary" link @click="retry">重试</el-button></div>
  </div>
</template>

<script setup lang="ts">
/** 无限滚动加载组件 @component */
import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue'
import { Loading } from '@element-plus/icons-vue'

const props = withDefaults(defineProps<{ loading?: boolean; hasMore?: boolean; error?: boolean; errorMessage?: string; distance?: number; immediateCheck?: boolean }>(), { loading: false, hasMore: true, error: false, errorMessage: '加载失败', distance: 100, immediateCheck: true })
const emit = defineEmits<{ (e: 'load'): void; (e: 'retry'): void }>()

const containerRef = ref<HTMLElement>(); const listLength = ref(0)
let timer: number | null = null

const checkScroll = () => {
  if (!containerRef.value || props.loading || !props.hasMore) return
  const { scrollTop, scrollHeight, clientHeight } = containerRef.value
  if (scrollHeight - scrollTop - clientHeight < props.distance) emit('load')
}
const handleScroll = () => { if (timer) return; timer = window.setTimeout(() => { checkScroll(); timer = null }, 100) }
const retry = () => { emit('retry') }
const updateListLength = () => { if (containerRef.value) { const contentEl = containerRef.value.querySelector('.infinite-scroll-content'); if (contentEl) listLength.value = contentEl.children.length } }

watch(() => props.loading, (newVal, oldVal) => { if (!newVal && oldVal) nextTick(() => { updateListLength(); checkScroll() }) })

onMounted(() => { if (props.immediateCheck) nextTick(() => { checkScroll() }) })
onUnmounted(() => { if (timer) clearTimeout(timer) })
</script>

<style scoped>
.infinite-scroll-container { height: 100%; overflow-y: auto; }
.infinite-scroll-loading,.infinite-scroll-no-more,.infinite-scroll-error { display: flex; align-items: center; justify-content: center; gap: 8px; padding: 16px; font-size: 13px; color: #909399; }
.infinite-scroll-loading .el-icon { font-size: 16px; }
.infinite-scroll-error { color: #f56c6c; }
.is-loading { animation: rotate 1.5s linear infinite; }
@keyframes rotate { from { transform: rotate(0deg); } to { transform: rotate(360deg); } }
</style>
