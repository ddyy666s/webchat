<template>
  <el-drawer v-model="visible" title="表情包" direction="btt" size="420px" @close="handleClose" class="beautiful-drawer">
    <div class="emoji-container">
      <div class="emoji-tabs">
        <el-button size="small" :type="activeTab === 'system' ? 'primary' : 'default'" @click="activeTab = 'system'">
          系统表情
        </el-button>
        <el-button size="small" :type="activeTab === 'user' ? 'primary' : 'default'" @click="activeTab = 'user'">
          我的表情
        </el-button>
        <el-button size="small" @click="$emit('upload')">上传表情</el-button>
      </div>

      <EmojiGrid v-if="activeTab === 'system'" :emojis="systemEmojis" :show-delete="false" empty-text="暂无系统表情"
        @select="$emit('select', $event)" />

      <EmojiGrid v-if="activeTab === 'user'" :emojis="userEmojis" :show-delete="true" empty-text="暂无自定义表情"
        @select="$emit('select', $event)" @delete="$emit('delete', $event)" />
    </div>
  </el-drawer>
</template>

<script setup lang="ts">
/** 表情选择器抽屉组件 @component */
import { ref, watch } from 'vue'
import EmojiGrid from './EmojiGrid.vue'

/** 组件属性：显示状态、系统/用户表情列表 */
const props = defineProps<{
  modelValue: boolean
  systemEmojis: any[]
  userEmojis: any[]
}>()

/** 组件事件：更新显示状态、选择/上传/删除表情 */
const emit = defineEmits(['update:modelValue', 'select', 'upload', 'delete'])

/** 抽屉可见性 */
const visible = ref(false)
/** 当前选中的表情 Tab */
const activeTab = ref('system')

/** 同步外部 modelValue 到内部 visible */
watch(() => props.modelValue, (val) => {
  visible.value = val
})

/** 内部 visible 变化同步到外部 */
watch(visible, (val) => {
  emit('update:modelValue', val)
})

/** 关闭抽屉 @returns void */
const handleClose = () => {
  visible.value = false
}
</script>

<style scoped>
.emoji-container {
  padding: 16px;
  max-height: 400px;
  overflow-y: auto;
}

.emoji-tabs {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.emoji-tabs .el-button {
  border-radius: 12px !important;
  font-weight: 600 !important;
}
</style>
