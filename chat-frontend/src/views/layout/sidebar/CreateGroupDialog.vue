<template>
  <BaseDialog v-model="visible" title="创建群聊" width="500px" @close="handleClose">
    <el-form :model="form" label-width="80px">
      <el-form-item label="群名称" required>
        <el-input v-model="form.name" placeholder="请输入群名称" maxlength="30" show-word-limit />
      </el-form-item>
      <el-form-item label="群公告">
        <el-input v-model="form.notice" type="textarea" :rows="2" placeholder="选填" maxlength="100" />
      </el-form-item>
      <el-form-item label="邀请好友">
        <el-select v-model="form.memberIds" multiple filterable placeholder="选择要邀请的好友" style="width: 100%">
          <el-option v-for="friend in friendList" :key="friend.userId" :label="friend.remark || friend.nickname"
            :value="friend.userId" />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" :loading="loading" @click="handleSubmit">创建</el-button>
    </template>
  </BaseDialog>
</template>

<script setup lang="ts">
/** 创建群聊对话框组件 @component */
import { ref, computed } from 'vue'
import BaseDialog from '@/components/common/BaseDialog.vue'
import { notify } from '@/utils/notify'
import type { FriendVO } from '@/api/friend'

/** 组件属性：显示状态、好友列表 */
const props = defineProps<{
  modelValue: boolean
  friendList: FriendVO[]
}>()

/** 组件事件：更新显示状态、提交创建 */
const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  submit: [data: { name: string; notice?: string; memberIds: number[] }]
}>()

/** 对话框可见性（双向绑定） */
const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

/** 创建按钮加载状态 */
const loading = ref(false)
/** 表单数据 */
const form = ref({
  name: '',
  notice: '',
  memberIds: [] as number[]
})

/** 关闭对话框并重置表单 @returns void */
const handleClose = () => {
  form.value = { name: '', notice: '', memberIds: [] }
  visible.value = false
}

/** 提交创建群聊 @returns Promise<void> */
const handleSubmit = async () => {
  if (!form.value.name.trim()) {
    notify.warning('请输入群名称')
    return
  }

  loading.value = true
  emit('submit', {
    name: form.value.name,
    notice: form.value.notice || undefined,
    memberIds: form.value.memberIds
  })
  handleClose()
  loading.value = false
}
</script>
