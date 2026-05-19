<template>
  <ConfirmDialog v-model="visible" :title="title" :message="message" :type="type || 'info'"
    :confirm-text="confirmText" :cancel-text="cancelText" @confirm="emitConfirm" @cancel="handleCancel">
    <el-input v-model="inputVal" :placeholder="placeholder" class="prompt-input"
      @keyup.enter="handleConfirm" />
    <div v-if="inputError" class="prompt-error">{{ inputError }}</div>
  </ConfirmDialog>
</template>

<script setup lang="ts">
/** 输入提示对话框组件，复用 ConfirmDialog，支持表单验证 @component */
import { ref, watch } from 'vue'
import ConfirmDialog from './ConfirmDialog.vue'

const props = defineProps<{
  modelValue: boolean
  title?: string
  message: string
  type?: 'info' | 'warning' | 'danger'
  confirmText?: string
  cancelText?: string
  width?: string
  placeholder?: string
  inputValue?: string
  inputPattern?: RegExp
  inputErrorMessage?: string
}>()

const emit = defineEmits(['update:modelValue', 'confirm', 'cancel'])

const visible = ref(false)
const inputVal = ref(props.inputValue || '')
const inputError = ref('')

watch(() => props.modelValue, (open) => {
  visible.value = open
  if (open) inputVal.value = props.inputValue || ''
})

watch(visible, (val) => {
  if (!val) emit('update:modelValue', false)
})

const handleConfirm = () => {
  if (props.inputPattern && !props.inputPattern.test(inputVal.value)) {
    inputError.value = props.inputErrorMessage || '输入格式不正确'
    return
  }
  inputError.value = ''
  emit('confirm', inputVal.value)
  visible.value = false
}

const handleCancel = () => {
  emit('cancel')
  visible.value = false
}

const emitConfirm = () => {
  emit('confirm', inputVal.value)
}
</script>

<style scoped>
.prompt-input { width: 90%; margin-top: 4px; }
.prompt-input :deep(.el-input__wrapper) { border-radius: 12px !important; height: 44px; }
.prompt-error { color: var(--color-danger); font-size: 13px; margin-top: -8px; }
</style>