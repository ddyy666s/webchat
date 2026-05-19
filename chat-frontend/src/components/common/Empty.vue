<template>
  <div class="empty-container">
    <div class="empty-content">
      <div class="empty-icon" v-if="icon">
        <component :is="icon" :size="size" />
      </div>
      <div v-else class="empty-default-icon">
        <svg width="64" height="64" viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M32 4C16.5 4 4 16.5 4 32C4 47.5 16.5 60 32 60C47.5 60 60 47.5 60 32C60 16.5 47.5 4 32 4Z"
            fill="#E8ECF0" />
          <path d="M32 20V36M32 44H32.01" stroke="#909399" stroke-width="2" stroke-linecap="round" />
        </svg>
      </div>
      <div class="empty-title">{{ title }}</div>
      <div v-if="description" class="empty-description">{{ description }}</div>
      <div v-if="$slots.default" class="empty-action">
        <slot />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
/** 空状态占位组件 @component */
withDefaults(defineProps<{
  title?: string
  description?: string
  icon?: any
  size?: number
}>(), {
  title: '暂无数据',
  size: 48
})
</script>

<style scoped>
.empty-container {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
}

.empty-content {
  text-align: center;
  animation: fadeInUp 0.4s ease;
}

@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(12px); }
  to { opacity: 1; transform: translateY(0); }
}

.empty-default-icon {
  margin-bottom: 20px;
  opacity: 0.5;
  transition: transform 0.3s ease;
}

.empty-container:hover .empty-default-icon {
  transform: scale(1.1) rotate(-3deg);
}

.empty-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-secondary);
  margin-bottom: 8px;
}

.empty-description {
  font-size: 13px;
  color: var(--text-secondary);
  margin-top: 4px;
}

.empty-action {
  margin-top: 20px;
}
</style>
