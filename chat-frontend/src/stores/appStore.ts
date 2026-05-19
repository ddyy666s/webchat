/** 应用级状态管理 @module appStore */
import { defineStore } from 'pinia'
import { ref } from 'vue'

/** 应用全局状态 store */
export const useAppStore = defineStore('app', () => {
  /** 侧边栏是否折叠 */
  const sidebarCollapsed = ref(false)
  /** 当前主题（dark/light） */
  const theme = ref('light')
  /** 全局加载状态 */
  const globalLoading = ref(false)

  /** 切换侧边栏折叠状态 */
  const toggleSidebar = () => { sidebarCollapsed.value = !sidebarCollapsed.value }

  /** 设置主题 @param newTheme 主题名称 */
  const setTheme = (newTheme: string) => {
    theme.value = newTheme
    localStorage.setItem('chat_theme', newTheme)
    document.documentElement.setAttribute('data-theme', newTheme)
  }

  /** 初始化主题（从本地存储恢复） */
  const initTheme = () => { setTheme(localStorage.getItem('chat_theme') || 'light') }

  /** 设置全局加载状态 @param loading 是否加载中 */
  const setGlobalLoading = (loading: boolean) => { globalLoading.value = loading }

  return { sidebarCollapsed, theme, globalLoading, toggleSidebar, setTheme, initTheme, setGlobalLoading }
})
