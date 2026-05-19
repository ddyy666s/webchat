/** 认证管理 composable @module useAuth */
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { loginApi, registerApi } from '@/api/user'
import { useUserStore } from '@/stores/userStore'

/** 认证相关操作 hook @returns 登录、注册、退出方法及加载状态 */
export const useAuth = () => {
  const router = useRouter()
  const userStore = useUserStore()
  /** 请求加载状态 */
  const loading = ref(false)

  /** 登录 @param username 用户名 @param password 密码 @returns 是否登录成功 */
  const login = async (username: string, password: string) => {
    loading.value = true
    try { const res = await loginApi({ username, password }); userStore.setToken(res.token); userStore.setUserInfo(res.user); ElMessage.success('登录成功'); router.push('/'); return true }
    catch (error) { return false }
    finally { loading.value = false }
  }

  /** 注册 @param username 用户名 @param password 密码 @param nickname 昵称 @returns 是否注册成功 */
  const register = async (username: string, password: string, nickname: string) => {
    loading.value = true
    try { await registerApi({ username, password, nickname }); ElMessage.success('注册成功，请登录'); router.push('/login'); return true }
    catch (error) { return false }
    finally { loading.value = false }
  }

  /** 退出登录（弹出确认框） */
  const logout = async () => {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' })
    userStore.logout(); router.push('/login')
  }

  return { loading, login, register, logout }
}
