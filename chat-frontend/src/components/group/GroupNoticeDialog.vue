<template>
  <ViewDialog v-model="showView" title="群公告" width="440px">
    <p>{{ notice || '暂无群公告' }}</p>
  </ViewDialog>

  <BaseDialog v-model="showEdit" title="编辑群公告" width="440px">
    <el-input v-model="editContent" type="textarea" :rows="4" placeholder="请输入群公告" maxlength="200" show-word-limit />
    <template #footer>
      <el-button @click="showEdit = false">取消</el-button>
      <el-button type="primary" :loading="loading" @click="handleSave">保存</el-button>
    </template>
  </BaseDialog>
</template>

<script setup lang="ts">
/** 群公告查看/编辑对话框 @component */
import { ref, watch } from 'vue'
import BaseDialog from '@/components/common/BaseDialog.vue'
import ViewDialog from '@/components/common/ViewDialog.vue'

/** 组件属性：显示状态、公告内容、是否可编辑 */
const props = defineProps<{
  modelValue: boolean
  notice: string | null
  canEdit: boolean
}>()

/** 组件事件：更新显示状态、保存公告 */
const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void
  (e: 'save', content: string): Promise<void>
}>()

/** 查看模式对话框可见 */
const showView = ref(false)
/** 编辑模式对话框可见 */
const showEdit = ref(false)
/** 编辑框内容 */
const editContent = ref('')
/** 保存加载状态 */
const loading = ref(false)

/** 根据 modelValue 和 canEdit 切换查看/编辑模式 */
watch(() => props.modelValue, (val) => {
  if (props.canEdit) {
    showEdit.value = val
    editContent.value = props.notice || ''
  } else {
    showView.value = val
  }
})

/** 编辑对话框关闭时同步外部状态 */
watch(showEdit, (val) => {
  if (!val) emit('update:modelValue', false)
})

/** 保存公告 @returns void */
const handleSave = () => {
  loading.value = true
  emit('save', editContent.value)
  showEdit.value = false
  loading.value = false
}
</script>
