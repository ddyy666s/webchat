<template>
  <el-dialog v-model="visible" :title="title" :width="width" :class="dialogClass" :top="top" :close-on-click-modal="closeOnClickModal" :show-close="showClose" :before-close="handleBeforeClose" @closed="emit('closed')" :destroy-on-close="destroyOnClose">
    <template v-if="$slots.header" #header><slot name="header" /></template>
    <slot />
    <template v-if="$slots.footer" #footer><slot name="footer" /></template>
  </el-dialog>
</template>

<script setup lang="ts">
/** 通用对话框基座组件，封装 Element Plus Dialog 的统一外观和行为 @component */
import { computed } from 'vue'

const props = defineProps<{ modelValue: boolean; title?: string; width?: string; top?: string; closeOnClickModal?: boolean; showClose?: boolean; destroyOnClose?: boolean; type?: 'default' | 'beautiful' }>()
const emit = defineEmits(['update:modelValue', 'closed', 'close'])

const visible = computed({ get: () => props.modelValue, set: (val) => emit('update:modelValue', val) })
const dialogClass = computed(() => { const cls: string[] = []; if (props.type !== 'beautiful') cls.push('beautiful-dialog'); return cls.join(' ') })
const handleBeforeClose = (done: () => void) => { emit('close'); done() }
</script>
