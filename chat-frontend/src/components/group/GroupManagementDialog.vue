<template>
  <BaseDialog v-model="visible" title="群管理" width="640px" show-close>
    <div style="margin-bottom:12px;display:flex;align-items:center;gap:8px">
      <el-button v-if="canManage" size="small" @click="toggleSelectAll">全选/取消</el-button>
      <el-button v-if="selectedIds.length > 0" size="small" type="warning" @click="openBatchMute">批量禁言({{ selectedIds.length }})</el-button>
    </div>
    <el-table :data="members" style="width:100%" max-height="400" @selection-change="onSelectionChange" class="group-manage-table">
      <el-table-column v-if="canManage" type="selection" width="40" />
      <el-table-column label="成员" min-width="160">
        <template #default="{ row }">
          <div style="display:flex;align-items:center;gap:8px">
            <el-avatar :size="32" :src="row.avatar || ''">{{ row.nickname?.charAt(0) }}</el-avatar>
            <span style="font-weight:600;color:var(--text-primary)">{{ row.nickname }}</span>
            <el-tag v-if="row.muted" size="small" type="danger">禁言中</el-tag>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="身份" width="80">
        <template #default="{ row }">
          <el-tag v-if="row.role === 2" type="warning" size="small">群主</el-tag>
          <el-tag v-else-if="row.role === 1" type="success" size="small">管理员</el-tag>
          <span v-else style="color:var(--text-regular)">成员</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="240">
        <template #default="{ row }">
          <el-button v-if="isOwner && row.role !== 2 && row.userId !== currentUserId" size="small" type="primary" @click="toggleAdmin(row)">{{ row.role === 1 ? '取消管理' : '设为管理' }}</el-button>
          <el-button v-if="canKick(row)" size="small" type="danger" @click="handleKick(row)">移除</el-button>
          <el-button v-if="canManageMute(row)" size="small" @click="toggleMute(row)">{{ row.muted ? '取消禁言' : '禁言' }}</el-button>
        </template>
      </el-table-column>
    </el-table>
    <ConfirmDialog v-model="showKickDialog" title="移除成员" :message="`确定移除成员 ${kickTarget?.nickname || ''}？`" type="danger" confirm-text="移除" cancel-text="取消" @confirm="confirmKick" />
    <PromptDialog v-model="showPromptMute" title="禁言" message="禁言时长(分钟)：" confirm-text="确定" cancel-text="取消" input-value="60" :input-pattern="/^\d+$/" input-error-message="请输入数字" @confirm="onMuteConfirm" />
    <PromptDialog v-model="showPromptBatch" title="批量禁言" message="禁言时长(分钟)：" confirm-text="确定" cancel-text="取消" input-value="60" :input-pattern="/^\d+$/" input-error-message="请输入数字" @confirm="onBatchMuteConfirm" />
  </BaseDialog>
</template>

<script setup lang="ts">
/** 群管理对话框组件，支持查看成员、设置管理员、禁言、移除等操作 @component */
import { ref, watch } from 'vue'
import BaseDialog from '@/components/common/BaseDialog.vue'
import { notify } from '@/utils/notify'
import { setGroupAdminApi, removeGroupAdminApi, removeGroupMemberApi, muteGroupMemberApi, unmuteGroupMemberApi, batchMuteGroupApi, type GroupMemberVO } from '@/api/group'
import ConfirmDialog from '@/components/common/ConfirmDialog.vue'
import PromptDialog from '@/components/common/PromptDialog.vue'

const props = defineProps<{ modelValue: boolean; groupId: number; members: GroupMemberVO[]; currentUserId: number; isOwner: boolean; canManage: boolean }>()
const emit = defineEmits(['update:modelValue', 'refresh'])

const visible = ref(false); const selectedIds = ref<number[]>([]); const showKickDialog = ref(false); const kickTarget = ref<GroupMemberVO | null>(null)
watch(() => props.modelValue, (val) => { visible.value = val }, { immediate: true })

function canKick(row: GroupMemberVO) {
  if (!props.canManage || row.userId === props.currentUserId) return false
  if (row.role === 2) return false; if (row.role === 1 && !props.isOwner) return false; return true
}
function canManageMute(row: GroupMemberVO) {
  if (!props.canManage || row.userId === props.currentUserId) return false
  if (row.role === 2) return false; if (row.role === 1 && !props.isOwner) return false; return true
}
function isSelectable(r: GroupMemberVO) {
  if (r.role === 2 || r.userId === props.currentUserId) return false
  if (r.role === 1 && !props.isOwner) return false; return true
}
const onSelectionChange = (rows: GroupMemberVO[]) => { selectedIds.value = rows.filter(isSelectable).map(r => r.userId) }
let allSelected = false
const toggleSelectAll = () => { allSelected = !allSelected; selectedIds.value = allSelected ? props.members.filter(isSelectable).map(r => r.userId) : [] }

const showPromptMute = ref(false); const pendingMuteRow = ref<GroupMemberVO | null>(null); const muteMinutesResult = ref(0)
const openMutePrompt = (row: GroupMemberVO) => { pendingMuteRow.value = row; muteMinutesResult.value = 0; showPromptMute.value = true }
const onMuteConfirm = async (minutes: string) => {
  muteMinutesResult.value = parseInt(minutes, 10); showPromptMute.value = false
  if (!props.groupId || !pendingMuteRow.value) return
  try { await muteGroupMemberApi(props.groupId, pendingMuteRow.value.userId, muteMinutesResult.value); notify.success(`已禁言 ${muteMinutesResult.value} 分钟`); pendingMuteRow.value = null; emit('refresh') }
  catch (e: any) { notify.error(e?.response?.data?.message || '操作失败') }
}

const toggleAdmin = async (row: GroupMemberVO) => {
  if (!props.groupId) return
  try {
    if (row.role === 1) { await removeGroupAdminApi(props.groupId, row.userId); notify.success('已取消管理员') }
    else { await setGroupAdminApi(props.groupId, row.userId); notify.success('已设置管理员') }
    emit('refresh')
  } catch (e: any) { notify.error(e?.response?.data?.message || '操作失败') }
}

const handleKick = (row: GroupMemberVO) => { kickTarget.value = row; showKickDialog.value = true }
const confirmKick = async () => {
  if (!props.groupId || !kickTarget.value) return
  try { await removeGroupMemberApi(props.groupId, kickTarget.value.userId); notify.success('已移除'); showKickDialog.value = false; emit('refresh') }
  catch (e: any) { notify.error(e?.response?.data?.message || '移除失败') }
}

const toggleMute = async (row: GroupMemberVO) => {
  if (!props.groupId) return
  if (row.muted) {
    try { await unmuteGroupMemberApi(props.groupId, row.userId); notify.success('已取消禁言'); emit('refresh') }
    catch (e: any) { notify.error(e?.response?.data?.message || '操作失败') }
  } else { openMutePrompt(row) }
}

const showPromptBatch = ref(false)
const openBatchMute = () => { muteMinutesResult.value = 0; showPromptBatch.value = true }
const onBatchMuteConfirm = async (minutes: string) => {
  showPromptBatch.value = false; if (!props.groupId || selectedIds.value.length === 0) return
  const m = parseInt(minutes, 10)
  try { await batchMuteGroupApi(props.groupId, selectedIds.value, m); notify.success(`已批量禁言 ${selectedIds.value.length} 人`); selectedIds.value = []; emit('refresh') }
  catch (e: any) { notify.error(e?.response?.data?.message || '批量禁言失败') }
}
</script>

<style scoped>
.group-manage-table :deep(.el-table__body td) { padding: 10px 0; }
.group-manage-table :deep(.el-button) { font-weight: 600; }
</style>
