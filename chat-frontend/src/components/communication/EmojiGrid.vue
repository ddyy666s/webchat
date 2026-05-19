<template>
  <div class="emoji-grid">
    <div v-for="emoji in emojis" :key="emoji.id" class="emoji-item" @click="$emit('select', emoji)">
      <img :src="emoji.url" />
      <span>{{ emoji.name }}</span>
      <el-button v-if="showDelete" class="delete-btn" size="small" text @click.stop="$emit('delete', emoji.id)">
        删除
      </el-button>
    </div>
    <Empty v-if="emojis.length === 0" :description="emptyText" />
  </div>
</template>

<script setup lang="ts">
/** 表情网格展示组件 @component */
import Empty from '@/components/common/Empty.vue'

/** 组件属性：表情列表、是否显示删除按钮、空状态文本 */
defineProps<{
  emojis: any[]
  showDelete?: boolean
  emptyText?: string
}>()

/** 组件事件：选择表情、删除表情 */
defineEmits<{
  (e: 'select', emoji: any): void
  (e: 'delete', id: number): void
}>()
</script>

<style scoped>
.emoji-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 12px;
}

.emoji-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  padding: 10px;
  border-radius: 14px;
  position: relative;
  transition: all 0.2s;
}

.emoji-item:hover {
  background: #f3f0ff;
  transform: translateY(-2px);
}

.emoji-item:active {
  transform: scale(0.95);
}

.emoji-item img {
  width: 48px;
  height: 48px;
  object-fit: contain;
  border-radius: 8px;
}

.delete-btn {
  position: absolute;
  top: 0;
  right: 0;
  opacity: 0;
  transition: opacity 0.2s;
}

.emoji-item:hover .delete-btn {
  opacity: 1;
}
</style>
