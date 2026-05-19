/** Vue Router 路由实例与全局守卫 @module router */
import { createRouter, createWebHistory } from 'vue-router'
import { routes } from './routes'
import { useUserStore } from '@/stores/userStore'
import { ElMessage } from 'element-plus'

/** 路由实例 */
const router = createRouter({ history: createWebHistory(), routes })

/** 全局前置守卫：鉴权与页面标题设置 */
router.beforeEach((to, _from, next) => {
  const userStore = useUserStore()
  const requiresAuth = to.meta.requiresAuth !== false
  if (requiresAuth && !userStore.isLoggedIn()) { ElMessage.warning('请先登录'); next('/login'); return }
  if (to.meta.requiresAdmin && !userStore.isAdmin()) { ElMessage.error('无权限访问'); next('/'); return }
  document.title = `${to.meta.title || '在线聊天系统'}`
  next()
})

export default router
