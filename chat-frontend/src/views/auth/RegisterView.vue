<template>
  <div class="register-container">
    <AnimatedBackground />
    <div class="register-card">
      <h1 class="title">注册账号</h1>
      <p class="subtitle">加入我们，开启聊天之旅~</p>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="0">
        <el-form-item prop="username"><el-input v-model="form.username" placeholder="请输入用户名（4-20位）" :prefix-icon="User" size="large" /></el-form-item>
        <el-form-item prop="nickname"><el-input v-model="form.nickname" placeholder="请输入昵称" :prefix-icon="User" size="large" /></el-form-item>
        <el-form-item prop="password"><el-input v-model="form.password" type="password" placeholder="请输入密码（6-20位）" :prefix-icon="Lock" size="large" show-password /></el-form-item>
        <el-form-item prop="confirmPassword"><el-input v-model="form.confirmPassword" type="password" placeholder="请再次输入密码" :prefix-icon="Lock" size="large" show-password /></el-form-item>
        <el-form-item><el-button type="primary" size="large" :loading="loading" @click="handleRegister" style="width:100%">注册</el-button></el-form-item>
        <div class="login-link"><span>已有账号？</span><el-link type="primary" @click="goToLogin">立即登录</el-link></div>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
/** 注册页面组件 @component */
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { registerApi } from '@/api/user'
import AnimatedBackground from '@/components/common/AnimatedBackground.vue'

const router = useRouter(); const formRef = ref(); const loading = ref(false)
const form = reactive({ username: '', nickname: '', password: '', confirmPassword: '' })

const validateConfirm = (_rule: any, value: string, callback: any) => { callback(value !== form.password ? new Error('两次输入的密码不一致') : undefined) }

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }, { min: 4, max: 20, message: '长度在4-20个字符', trigger: 'blur' }],
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }, { min: 2, max: 20, message: '长度在2-20个字符', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }, { min: 6, max: 20, message: '长度在6-20个字符', trigger: 'blur' }],
  confirmPassword: [{ required: true, message: '请再次输入密码', trigger: 'blur' }, { validator: validateConfirm, trigger: 'blur' }]
}

const handleRegister = async () => {
  const valid = await formRef.value?.validate(); if (!valid) return
  loading.value = true
  try { await registerApi({ username: form.username, password: form.password, nickname: form.nickname }); ElMessage.success('注册成功，请登录'); router.push('/login') }
  catch (error) { console.error(error) }
  finally { loading.value = false }
}

const goToLogin = () => { router.push('/login') }
</script>

<style scoped>
.register-container { display: flex; justify-content: center; align-items: center; min-height: 100vh; padding: 20px; position: relative; overflow: hidden; }
.register-card { width: 460px; background-color: rgba(255,255,255,0.95); backdrop-filter: blur(20px); padding: 44px 40px; border-radius: 24px; position: relative; z-index: 1; box-shadow: 0 20px 60px rgba(0,0,0,0.3); animation: cardIn 0.6s ease; overflow: hidden; }
@keyframes cardIn { from { opacity: 0; transform: translateY(30px) scale(0.95); } to { opacity: 1; transform: translateY(0) scale(1); } }
.title { font-size: 26px; color: var(--color-primary); font-weight: 700; letter-spacing: -0.5px; position: relative; display: flex; align-items: center; padding-left: 34px; margin-bottom: 4px; }
.title::before,.title::after { position: absolute; content: ""; border-radius: 50%; left: 0; }
.title::before { width: 20px; height: 20px; background: linear-gradient(135deg, var(--color-primary), var(--color-primary-light)); }
.title::after { width: 20px; height: 20px; animation: pulse 2s ease-in-out infinite; background: var(--color-primary-light); opacity: 0.4; }
@keyframes pulse { 0% { transform: scale(0.8); opacity: 0.5; } 50% { transform: scale(1.6); opacity: 0; } 100% { transform: scale(0.8); opacity: 0; } }
.subtitle { font-size: 14px; color: var(--text-secondary); margin-bottom: 28px; padding-left: 34px; }
.register-card :deep(.el-form-item) { margin-bottom: 20px; }
.register-card :deep(.el-button--primary) { border: none; outline: none; background: linear-gradient(135deg, var(--color-primary), var(--color-primary-dark)); padding: 14px; border-radius: 14px; color: #fff; font-size: 16px; width: 100%; height: auto; font-weight: 600; letter-spacing: 1px; }
.register-card :deep(.el-button--primary:hover) { transform: translateY(-2px); box-shadow: 0 8px 25px rgba(108,92,231,0.3); }
.register-card :deep(.el-input__wrapper) { border-radius: 14px; box-shadow: 0 0 0 1px var(--border-color) inset; padding: 4px 12px; }
.register-card :deep(.el-input__wrapper.is-focus) { box-shadow: 0 0 0 2px var(--color-primary-light) inset; }
.register-card :deep(.el-input__prefix) { margin-right: 8px; }
.register-card :deep(.el-input__prefix-inner) { color: var(--text-secondary); }
.login-link { text-align: center; margin-top: 20px; font-size: 14px; color: var(--text-regular); }
</style>
