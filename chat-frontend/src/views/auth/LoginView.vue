<template>
  <div class="login-container">
    <AnimatedBackground />
    <div class="login-card">
      <h1 class="title">在线聊天系统</h1>
      <p class="subtitle">欢迎回来，开始畅聊吧~</p>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="0">
        <el-form-item prop="username"><el-input v-model="form.username" placeholder="请输入用户名" :prefix-icon="User" size="large" /></el-form-item>
        <el-form-item prop="password"><el-input v-model="form.password" type="password" placeholder="请输入密码" :prefix-icon="Lock" size="large" show-password /></el-form-item>
        <el-form-item><el-button type="primary" size="large" :loading="loading" @click="handleLogin" style="width:100%">登录</el-button></el-form-item>
        <div class="register-link"><span>还没有账号？</span><el-link type="primary" @click="goToRegister">立即注册</el-link></div>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
/** 登录页面组件 @component */
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { loginApi } from '@/api/user'
import { useUserStore } from '@/stores/userStore'
import AnimatedBackground from '@/components/common/AnimatedBackground.vue'

const router = useRouter(); const userStore = useUserStore(); const formRef = ref(); const loading = ref(false)
const form = reactive({ username: '', password: '' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  const valid = await formRef.value?.validate(); if (!valid) return
  loading.value = true
  try {
    const res = await loginApi(form); userStore.setToken(res.token); userStore.setUserInfo(res.user)
    ElMessage.success('登录成功'); await router.push('/')
  } catch (error: any) { console.error('登录失败:', error); ElMessage.error(error?.message || '登录失败') }
  finally { loading.value = false }
}

const goToRegister = () => { router.push('/register') }
</script>

<style scoped>
.login-container { display: flex; justify-content: center; align-items: center; height: 100vh; position: relative; overflow: hidden; }
.login-card { width: 440px; background-color: rgba(255,255,255,0.95); backdrop-filter: blur(20px); padding: 48px 40px; border-radius: 24px; position: relative; z-index: 1; box-shadow: 0 20px 60px rgba(0,0,0,0.3); animation: cardIn 0.6s ease; overflow: hidden; }
@keyframes cardIn { from { opacity: 0; transform: translateY(30px) scale(0.95); } to { opacity: 1; transform: translateY(0) scale(1); } }
.title { font-size: 26px; color: var(--color-primary); font-weight: 700; letter-spacing: -0.5px; position: relative; display: flex; align-items: center; padding-left: 34px; margin-bottom: 4px; }
.title::before,.title::after { position: absolute; content: ""; border-radius: 50%; left: 0; background-color: var(--color-primary); }
.title::before { width: 20px; height: 20px; background: linear-gradient(135deg, var(--color-primary), var(--color-primary-light)); }
.title::after { width: 20px; height: 20px; animation: pulse 2s ease-in-out infinite; background: var(--color-primary-light); opacity: 0.4; }
@keyframes pulse { 0% { transform: scale(0.8); opacity: 0.5; } 50% { transform: scale(1.6); opacity: 0; } 100% { transform: scale(0.8); opacity: 0; } }
.subtitle { font-size: 14px; color: var(--text-secondary); margin-bottom: 32px; padding-left: 34px; }
.login-card :deep(.el-form-item) { margin-bottom: 22px; }
.login-card :deep(.el-button--primary) { border: none; outline: none; background: linear-gradient(135deg, var(--color-primary), var(--color-primary-dark)); padding: 14px; border-radius: 14px; color: #fff; font-size: 16px; width: 100%; height: auto; font-weight: 600; letter-spacing: 1px; }
.login-card :deep(.el-button--primary:hover) { transform: translateY(-2px); box-shadow: 0 8px 25px rgba(108,92,231,0.3); }
.login-card :deep(.el-input__wrapper) { border-radius: 14px; box-shadow: 0 0 0 1px var(--border-color) inset; padding: 4px 12px; }
.login-card :deep(.el-input__wrapper.is-focus) { box-shadow: 0 0 0 2px var(--color-primary-light) inset; }
.login-card :deep(.el-input__prefix) { margin-right: 8px; }
.login-card :deep(.el-input__prefix-inner) { color: var(--text-secondary); }
.register-link { text-align: center; margin-top: 20px; font-size: 14px; color: var(--text-regular); }
</style>
