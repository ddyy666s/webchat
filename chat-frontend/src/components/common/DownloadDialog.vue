<template>
  <BaseDialog v-model="visible" title="下载聊天记录" width="440px">
    <div class="download-dialog">
      <div class="info"><p>当前共有 <strong>{{ totalMessages }}</strong> 条聊天记录</p></div>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="下载条数" prop="limit">
          <el-input-number v-model="form.limit" :min="1" :max="maxLimit" :step="10" controls-position="right" style="width:100%" />
          <div class="tip">最多可下载 {{ maxLimit }} 条</div>
        </el-form-item>
      </el-form>
    </div>
    <template #footer>
      <el-button class="btn-cancel" @click="handleClose">取消</el-button>
      <button class="download-btn" :disabled="loading" @click="handleDownload"><el-icon v-if="!loading" style="font-size:18px"><Download /></el-icon><span class="button-content">{{ loading ? '下载中...' : '下载' }}</span></button>
    </template>
  </BaseDialog>
</template>

<script setup lang="ts">
/** 聊天记录下载对话框组件 @component */
import { ref, watch } from 'vue'
import BaseDialog from '@/components/common/BaseDialog.vue'
import { notify } from '@/utils/notify'
import { Download } from '@element-plus/icons-vue'

const props = defineProps<{ modelValue: boolean; friendId: number; friendName: string; totalMessages: number; maxLimit?: number }>()
const emit = defineEmits(['update:modelValue', 'download'])
const visible = ref(false); const loading = ref(false); const formRef = ref()
const maxLimit = props.maxLimit || 500
const form = ref({ limit: Math.min(100, props.totalMessages || 100) })
const rules = { limit: [{ required: true, message: '请输入下载条数' }, { type: 'number', min: 1, max: maxLimit, message: `请输入1-${maxLimit}之间的数字` }] }

const handleClose = () => { visible.value = false; emit('update:modelValue', false) }
const handleDownload = async () => {
  const valid = await formRef.value?.validate(); if (!valid) return
  loading.value = true
  try { emit('download', form.value.limit); handleClose() }
  catch (error) { notify.error('下载失败') }
  finally { loading.value = false }
}
watch(() => props.modelValue, (val) => { visible.value = val; if (val) { form.value.limit = Math.min(100, props.totalMessages || 100); formRef.value?.clearValidate() } })
</script>

<style scoped>
.download-dialog { padding: 8px 0; }
.info { background: var(--bg-color); padding: 16px; border-radius: 14px; margin-bottom: 20px; text-align: center; }
.info p { font-size: 14px; color: var(--text-regular); }
.info strong { color: var(--color-primary); font-size: 22px; font-weight: 700; }
.tip { font-size: 12px; color: var(--text-secondary); margin-top: 8px; }
.btn-cancel { height: 44px; padding: 0 28px; border-radius: 12px !important; font-weight: 600 !important; font-size: 15px !important; border: 2px solid var(--border-color) !important; }
.btn-cancel:hover { border-color: var(--color-primary-light) !important; color: var(--color-primary) !important; background: #f3f0ff !important; }
.download-btn { position: relative; display: inline-flex; align-items: center; gap: 8px; height: 44px; padding: 0 28px; border-radius: 12px; background: linear-gradient(135deg, var(--color-primary), var(--color-primary-dark)); color: #fff; border: 2px solid var(--color-primary); cursor: pointer; font-size: 15px; font-weight: 600; margin-left: 12px; transition: all 0.3s; white-space: nowrap; }
.download-btn:hover { transform: translateY(-2px); box-shadow: 0 6px 20px rgba(108,92,231,0.3); border-color: var(--color-primary-light); }
.download-btn:active { transform: translateY(0) scale(0.97); }
.download-btn:disabled { opacity: 0.6; cursor: not-allowed; transform: none; box-shadow: none; }
</style>
