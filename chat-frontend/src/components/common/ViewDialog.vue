<template>
  <el-dialog v-model="visible" :title="title" :width="width" @close="handleClose" top="6vh" class="beautiful-view-dialog">
    <slot />
  </el-dialog>
</template>

<script setup lang="ts">
/** 通用视图对话框组件 @component */
import { ref, watch } from 'vue'

/** 组件属性：显示状态、标题、宽度 */
const props = defineProps<{
  modelValue: boolean
  title?: string
  width?: string
}>()

/** 组件事件：更新显示状态、关闭 */
const emit = defineEmits(['update:modelValue', 'close'])

/** 对话框可见性 */
const visible = ref(false)

/** 同步外部 modelValue 到内部 visible */
watch(() => props.modelValue, (val) => { visible.value = val }, { immediate: true })
/** 内部 visible 变化同步到外部 */
watch(visible, (val) => { if (!val) emit('update:modelValue', false) })

/** 关闭对话框 @returns void */
const handleClose = () => {
  visible.value = false
  emit('close')
}
</script>
