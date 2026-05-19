<template>
  <BaseDialog v-model="visible" title="添加评价" width="440px" @close="handleClose">
    <el-form :model="form" label-width="80px">
      <el-form-item label="选择好友">
        <el-select v-model="form.toUserId" placeholder="请选择要评价的好友" filterable style="width: 100%" :loading="loading">
          <el-option v-for="friend in friendList" :key="friend.userId" :label="friend.remark || friend.nickname"
            :value="friend.userId" />
        </el-select>
      </el-form-item>
      <el-form-item label="评价内容">
        <el-input v-model="form.content" type="textarea" :rows="3" placeholder="请输入评价内容（最多100字）" maxlength="100"
          show-word-limit />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="handleSubmit">
        提交
      </el-button>
    </template>
  </BaseDialog>
</template>

<script setup lang="ts">
/** 添加好友评价对话框 @component */
import { ref, computed, watch } from 'vue'
import BaseDialog from '@/components/common/BaseDialog.vue'
import { notify } from '@/utils/notify'

/** 组件属性：显示状态、好友列表、加载状态、预选好友ID */
const props = defineProps<{
  modelValue: boolean
  friendList: any[]
  loading?: boolean
  preselectUserId?: number | null
}>()

/** 组件事件：更新显示状态、提交评价 */
const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  submit: [toUserId: number, content: string]
}>()

/** 对话框可见性（双向绑定），打开时自动预选好友 */
const visible = computed({
  get: () => props.modelValue,
  set: (val) => {
    emit('update:modelValue', val)
    if (!val) form.value = { toUserId: null, content: '' }
  }
})

/** 打开时预选好友 */
watch(visible, (val) => {
  if (val && props.preselectUserId && props.friendList.some(f => f.userId === props.preselectUserId)) {
    form.value.toUserId = props.preselectUserId
  }
})

/** 提交按钮加载状态 */
const submitting = ref(false)
/** 表单数据 */
const form = ref({
  toUserId: null as number | null,
  content: ''
})

/** 关闭对话框，重置表单 @returns void */
const handleClose = () => {
  form.value = { toUserId: null, content: '' }
  visible.value = false
}

/** 提交评价 @returns Promise<void> */
const handleSubmit = async () => {
  if (!form.value.toUserId) {
    notify.warning('请选择要评价的好友')
    return
  }
  if (!form.value.content.trim()) {
    notify.warning('请输入评价内容')
    return
  }

  submitting.value = true
  emit('submit', form.value.toUserId, form.value.content)
  handleClose()
  submitting.value = false
}
</script>
