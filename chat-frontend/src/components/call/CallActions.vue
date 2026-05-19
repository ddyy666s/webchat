<template>
  <div class="call-actions">
    <el-button v-if="showAccept" type="success" @click="onAccept" :icon="Phone">
      接听
    </el-button>
    <el-button type="danger" @click="onHangup" :icon="Close">
      挂断
    </el-button>
  </div>
</template>

<script setup lang="ts">
/** 通话操作按钮组件，提供接听和挂断功能 @component */
import { ref, computed } from 'vue'
import { Phone, Close } from '@element-plus/icons-vue'

/** 组件属性：通话连接状态与是否为呼叫方 */
const props = defineProps<{
  isConnected: boolean
  isCaller: boolean
}>()

/** 组件事件：接听和挂断 */
const emit = defineEmits<{
  (e: 'accept'): void
  (e: 'hangup'): void
}>()

/** 是否已接听标记 */
const accepted = ref(false)

/** 是否显示接听按钮：未被接听且未连接且不是呼叫方 */
const showAccept = computed(() => !accepted.value && !props.isConnected && !props.isCaller)

/** 接听通话处理 @param void @returns void */
const onAccept = () => {
  accepted.value = true
  emit('accept')
}
/** 挂断通话处理 @param void @returns void */
const onHangup = () => emit('hangup')
</script>

<style scoped>
.call-actions {
  display: flex;
  justify-content: center;
  gap: 20px;
}
</style>
