/** 路由配置表 @module routes */
import type { RouteRecordRaw } from 'vue-router'

/** 路由配置列表 */
export const routes: RouteRecordRaw[] = [
  { path: '/login', name: 'Login', component: () => import('@/views/auth/LoginView.vue'), meta: { requiresAuth: false, title: '登录' } },
  { path: '/register', name: 'Register', component: () => import('@/views/auth/RegisterView.vue'), meta: { requiresAuth: false, title: '注册' } },
  { path: '/', name: 'Main', component: () => import('@/views/layout/MainLayout.vue'), meta: { requiresAuth: true, title: '聊天' } },
  { path: '/profile', name: 'Profile', component: () => import('@/views/profile/ProfileView.vue'), meta: { requiresAuth: true, title: '个人资料' } },
  { path: '/admin', name: 'Admin', component: () => import('@/views/admin/AdminView.vue'), meta: { requiresAuth: true, requiresAdmin: true, title: '管理后台' } }
]
