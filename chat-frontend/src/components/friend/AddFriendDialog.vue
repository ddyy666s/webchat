<template>
  <BaseDialog v-model="visible" title="添加好友" width="500px" top="8vh">
    <div class="search-box">
      <el-input v-model="keyword" placeholder="请输入用户名或昵称搜索" clearable @keyup.enter="handleSearch" class="search-input" />
      <el-button class="search-btn" @click="handleSearch" :loading="loading"><el-icon :size="16"><Search /></el-icon><span>搜索</span></el-button>
    </div>
    <div v-if="results.length > 0" class="search-results"><SearchResultItem v-for="user in results" :key="user.userId" :user="user" @add="handleAdd" /></div>
    <Empty v-else-if="searched && results.length === 0" description="未找到相关用户" />
    <template #footer><div class="dialog-footer"><el-button class="btn-cancel" @click="visible = false">取消</el-button></div></template>
  </BaseDialog>
</template>

<script setup lang="ts">
/** 添加好友对话框组件，支持搜索用户和发送好友申请 @component */
import { ref, watch } from 'vue'
import BaseDialog from '@/components/common/BaseDialog.vue'
import { notify } from '@/utils/notify'
import { Search } from '@element-plus/icons-vue'
import { searchUsersApi, sendFriendRequestApi } from '@/api/friend'
import Empty from '@/components/common/Empty.vue'
import SearchResultItem from './SearchResultItem.vue'

const props = defineProps<{ modelValue: boolean }>()
const emit = defineEmits(['update:modelValue', 'success'])

const visible = ref(false); const keyword = ref(''); const results = ref<any[]>([]); const loading = ref(false); const searched = ref(false)

watch(() => props.modelValue, (val) => { visible.value = val; if (!val) { keyword.value = ''; results.value = []; searched.value = false } })
watch(visible, (val) => { emit('update:modelValue', val) })

const handleSearch = async () => {
  if (!keyword.value.trim()) { notify.warning('请输入搜索内容'); return }
  searched.value = true; loading.value = true
  try { results.value = await searchUsersApi(keyword.value) }
  catch (error) { console.error(error); notify.error('搜索失败') }
  finally { loading.value = false }
}

const handleAdd = async (userId: number) => {
  try { await sendFriendRequestApi(userId); notify.success('好友申请已发送'); visible.value = false; emit('success') }
  catch (error: any) { notify.error(error?.message || '发送失败') }
}
</script>

<style scoped>
.search-box { display: flex; gap: 10px; align-items: center; }
.search-input { flex: 1; }
.search-input :deep(.el-input__wrapper) { border-radius: 12px !important; height: 44px; }
.search-btn { height: 44px; padding: 0 22px !important; border-radius: 12px !important; font-size: 14px !important; font-weight: 600 !important; gap: 6px; border: 2px solid var(--color-primary) !important; background: linear-gradient(135deg, var(--color-primary), var(--color-primary-dark)) !important; color: white !important; transition: all 0.25s !important; flex-shrink: 0; }
.search-btn:hover { transform: translateY(-2px); box-shadow: 0 6px 20px rgba(108,92,231,0.3) !important; border-color: var(--color-primary-light) !important; }
.search-btn:active { transform: translateY(0) scale(0.97); }
.search-btn.is-loading { opacity: 0.8; }
.search-results { margin-top: 18px; max-height: 320px; overflow-y: auto; }
.dialog-footer { display: flex; justify-content: center; }
.btn-cancel { min-width: 120px; height: 44px; border-radius: 14px !important; font-weight: 600 !important; font-size: 15px !important; }
</style>
