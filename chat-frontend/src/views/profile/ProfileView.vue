<template>
  <div class="profile-container">
    <AnimatedBackground />
    <div class="profile-card">
      <div class="profile-header">
        <el-button :icon="ArrowLeft" @click="goBack">返回</el-button>
        <h2>个人资料</h2>
        <div></div>
      </div>
      <div class="avatar-section">
        <div class="avatar-wrapper" @click="triggerFileInput">
          <el-avatar :size="100" :src="form.avatar || ''" class="profile-avatar">{{ form.nickname?.charAt(0) || 'U' }}</el-avatar>
          <div class="avatar-overlay"><el-icon><Camera /></el-icon><span>更换头像</span></div>
        </div>
        <input ref="fileInput" type="file" accept="image/jpeg,image/png,image/gif" style="display:none" @change="handleAvatarChange" />
      </div>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="用户名"><el-input v-model="form.username" disabled /></el-form-item>
        <el-form-item label="昵称" prop="nickname"><el-input v-model="form.nickname" placeholder="请输入昵称" /></el-form-item>
        <el-form-item label="个性签名" prop="signature"><el-input v-model="form.signature" type="textarea" :rows="3" placeholder="请输入个性签名" maxlength="100" show-word-limit /></el-form-item>
        <el-form-item><el-button type="primary" :loading="saving" @click="handleSave">保存修改</el-button></el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
/** 个人资料页面组件，支持头像上传和昵称/签名修改 @component */
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Camera } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/userStore'
import { updateProfileApi, updateAvatarApi, type UserInfo } from '@/api/user'
import AnimatedBackground from '@/components/common/AnimatedBackground.vue'

const router = useRouter(); const userStore = useUserStore()
const formRef = ref(); const saving = ref(false); const fileInput = ref<HTMLInputElement>()
const form = reactive<UserInfo>({ id: 0, username: '', nickname: '', avatar: null, signature: null, role: 'user' })
const rules = { nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }, { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }] }

const goBack = () => { router.push('/') }
const triggerFileInput = () => { fileInput.value?.click() }

const handleAvatarChange = async (event: Event) => {
  const input = event.target as HTMLInputElement; const file = input.files?.[0]
  if (!file) return
  if (!file.type.startsWith('image/')) { ElMessage.error('请选择图片文件'); return }
  if (file.size > 2 * 1024 * 1024) { ElMessage.error('图片大小不能超过 2MB'); return }
  const loadingMsg = ElMessage.info('正在上传...')
  try {
    const avatarUrl = await updateAvatarApi(file)
    loadingMsg.close(); form.avatar = avatarUrl
    if (userStore.userInfo) { userStore.setUserInfo({ ...userStore.userInfo, avatar: avatarUrl }) }
    ElMessage.success('头像更新成功')
  } catch (error) { console.error(error); ElMessage.error('头像上传失败') }
}

const handleSave = async () => {
  const valid = await formRef.value?.validate(); if (!valid) return
  saving.value = true
  try {
    const res = await updateProfileApi({ nickname: form.nickname, signature: form.signature })
    userStore.setUserInfo(res); ElMessage.success('保存成功'); router.push('/')
  } catch (error) { console.error(error); ElMessage.error('保存失败') }
  finally { saving.value = false }
}

onMounted(() => {
  const userInfo = userStore.userInfo
  if (userInfo) { form.id = userInfo.id; form.username = userInfo.username; form.nickname = userInfo.nickname; form.avatar = userInfo.avatar; form.signature = userInfo.signature; form.role = userInfo.role }
})
</script>

<style scoped>
.profile-container { display: flex; justify-content: center; align-items: center; min-height: 100vh; padding: 20px; position: relative; overflow: hidden; }
.profile-card { width: 520px; background: rgba(255,255,255,0.95); backdrop-filter: blur(20px); border-radius: 24px; padding: 36px; position: relative; z-index: 1; box-shadow: 0 20px 60px rgba(0,0,0,0.3); animation: cardIn 0.5s ease; }
@keyframes cardIn { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }
.profile-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 28px; }
.profile-header h2 { margin: 0; font-size: 20px; font-weight: 600; }
.avatar-section { display: flex; justify-content: center; margin-bottom: 32px; }
.avatar-wrapper { position: relative; cursor: pointer; }
.profile-avatar { width: 110px; height: 110px; font-size: 44px; border: 3px solid var(--color-primary-light) !important; }
.avatar-overlay { position: absolute; top: 0; left: 0; width: 110px; height: 110px; background: rgba(108,92,231,0.8); border-radius: 50%; display: flex; flex-direction: column; align-items: center; justify-content: center; color: white; opacity: 0; transition: opacity 0.3s; cursor: pointer; backdrop-filter: blur(2px); }
.avatar-overlay .el-icon { font-size: 26px; margin-bottom: 4px; }
.avatar-overlay span { font-size: 12px; font-weight: 500; }
.avatar-wrapper:hover .avatar-overlay { opacity: 1; }
.profile-card :deep(.el-form-item__label) { font-weight: 600; color: var(--text-regular); }
.profile-card :deep(.el-button--primary) { border-radius: 12px !important; padding: 10px 32px !important; font-weight: 600 !important; }
</style>
