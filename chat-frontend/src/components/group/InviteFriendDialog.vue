<template>
  <BaseDialog v-model="visible" title="邀请好友" width="440px">
    <el-select v-model="selectedId" placeholder="选择好友" filterable style="width: 100%">
      <el-option v-for="friend in friends" :key="friend.userId" :label="friend.remark || friend.nickname"
        :value="friend.userId" />
    </el-select>
    <Empty v-if="friends.length === 0" description="暂无好友可邀请" />
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" :loading="loading" @click="handleInvite">邀请</el-button>
    </template>
  </BaseDialog>
</template>

<script setup lang="ts">
/** 邀请好友加入群聊对话框 @component */
import { ref, watch } from 'vue'
import BaseDialog from '@/components/common/BaseDialog.vue'
import { notify } from '@/utils/notify'
import Empty from '@/components/common/Empty.vue'

/** 组件属性：显示状态、好友列表 */
const props = defineProps<{
  modelValue: boolean
  friends: any[]
}>()

/** 组件事件：更新显示状态、邀请好友 */
const emit = defineEmits(['update:modelValue', 'invite'])

/** 对话框可见性 */
const visible = ref(false)
/** 选中的好友 ID */
const selectedId = ref<number | null>(null)
/** 邀请按钮加载状态 */
const loading = ref(false)

/** 同步外部 modelValue */
watch(() => props.modelValue, (val) => {
  visible.value = val
  if (!val) {
    selectedId.value = null
  }
})

/** 内部可见性同步到外部 */
watch(visible, (val) => {
  emit('update:modelValue', val)
})

/** 执行邀请 @returns Promise<void> */
const handleInvite = async () => {
  if (!selectedId.value) {
    notify.warning('请选择好友')
    return
  }
  loading.value = true
  emit('invite', selectedId.value)
  visible.value = false
  loading.value = false
}
</script>
