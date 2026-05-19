<template>
  <el-card class="section-card">
    <template #header>
      <span>聊天记录审计</span>
    </template>

    <el-form :inline="true" class="filter-form">
      <el-form-item label="发送者ID">
        <el-input v-model="filter.fromUserId" placeholder="发送者ID" clearable />
      </el-form-item>
      <el-form-item label="接收者ID">
        <el-input v-model="filter.toUserId" placeholder="接收者ID" clearable />
      </el-form-item>
      <el-form-item label="开始时间">
        <el-date-picker v-model="filter.startTime" type="date" placeholder="开始日期" />
      </el-form-item>
      <el-form-item label="结束时间">
        <el-date-picker v-model="filter.endTime" type="date" placeholder="结束日期" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="messages" border stripe>
      <el-table-column prop="id" label="消息ID" width="100" />
      <el-table-column prop="fromUserNickname" label="发送者" width="120" />
      <el-table-column prop="fromUserId" label="发送者ID" width="100" />
      <el-table-column prop="toUserNickname" label="接收者" width="120" />
      <el-table-column prop="toUserId" label="接收者ID" width="100" />
      <el-table-column prop="content" label="内容" min-width="200" show-overflow-tooltip />
      <el-table-column prop="sendTime" label="发送时间" width="180" />
    </el-table>

    <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :total="total"
      layout="total, sizes, prev, pager, next" @current-change="loadMessages" @size-change="loadMessages" />
  </el-card>
</template>

<script setup lang="ts">
/** 消息审计页面组件，支持按条件搜索和分页查看聊天记录 @component */
import { ref } from 'vue'
import { getAdminMessagesApi } from '@/api/admin'
import { formatDate } from '@/utils/date'

/** 消息列表 */
const messages = ref<any[]>([])
/** 当前页码 */
const currentPage = ref(1)
/** 每页条数 */
const pageSize = ref(20)
/** 总记录数 */
const total = ref(0)

/** 搜索过滤条件 */
const filter = ref({
  fromUserId: '',
  toUserId: '',
  startTime: '',
  endTime: ''
})

/** 加载消息列表 @returns Promise<void> */
const loadMessages = async () => {
  const params: any = {
    page: currentPage.value,
    size: pageSize.value
  }
  if (filter.value.fromUserId) params.fromUserId = filter.value.fromUserId
  if (filter.value.toUserId) params.toUserId = filter.value.toUserId
  if (filter.value.startTime) params.startTime = formatDate(filter.value.startTime, 'YYYY-MM-DD')
  if (filter.value.endTime) params.endTime = formatDate(filter.value.endTime, 'YYYY-MM-DD')

  const res = await getAdminMessagesApi(params)
  messages.value = res.records
  total.value = res.total
}

/** 搜索 @returns void */
const handleSearch = () => {
  currentPage.value = 1
  loadMessages()
}

/** 重置搜索条件 @returns void */
const handleReset = () => {
  filter.value = { fromUserId: '', toUserId: '', startTime: '', endTime: '' }
  currentPage.value = 1
  loadMessages()
}

loadMessages()
</script>

<style scoped>
.section-card {
  margin-bottom: 24px;
}

.filter-form {
  margin-bottom: 16px;
}
</style>
