<template>
  <div class="notification-manage">
    <el-card>
      <template #header>
        <span>发布系统通知</span>
      </template>

      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="通知标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入通知标题" maxlength="100" show-word-limit />
        </el-form-item>

        <el-form-item label="通知内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="6" placeholder="请输入通知内容"
            maxlength="2000" show-word-limit />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSend" :loading="sending">发送通知</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card style="margin-top: 20px;">
      <template #header>
        <span>已发送通知</span>
      </template>

      <el-table :data="sentNotifications" v-loading="loading" empty-text="暂无已发送通知">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="title" label="标题" min-width="200" />
        <el-table-column prop="content" label="内容" min-width="300" show-overflow-tooltip />
        <el-table-column prop="createdAt" label="发送时间" width="180" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
/** 系统通知管理页面组件 @component */
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { sendNotificationApi, getAdminNotificationsApi, type SystemNotification } from '@/api/notification'

/** 表单引用 */
const formRef = ref()
/** 发送中状态 */
const sending = ref(false)
/** 加载中状态 */
const loading = ref(false)
/** 已发送的通知列表 */
const sentNotifications = ref<SystemNotification[]>([])

/** 表单数据 */
const form = reactive({
  title: '',
  content: ''
})

/** 表单校验规则 */
const rules = {
  title: [{ required: true, message: '请输入通知标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入通知内容', trigger: 'blur' }]
}

/** 发送通知 @returns Promise<void> */
const handleSend = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  sending.value = true
  try {
    await sendNotificationApi(form.title, form.content)
    ElMessage.success('通知已发送')
    resetForm()
    loadSentNotifications()
  } catch {
    ElMessage.error('发送失败')
  } finally {
    sending.value = false
  }
}

/** 重置表单 @returns void */
const resetForm = () => {
  form.title = ''
  form.content = ''
  formRef.value?.resetFields()
}

/** 加载已发送通知列表 @returns Promise<void> */
const loadSentNotifications = async () => {
  loading.value = true
  try {
    const res = await getAdminNotificationsApi()
    sentNotifications.value = res || []
  } catch {
    sentNotifications.value = []
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadSentNotifications()
})
</script>
