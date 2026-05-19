/** 侧边栏宽度可拖拽调整 composable @module useResizable */
import { ref, onMounted } from 'vue'

/** 侧边栏宽度拖拽调整 hook @param options 配置项 @returns 宽度状态与拖拽方法 */
export function useResizable(options: {
  minWidth?: number; maxWidth?: number; defaultWidth?: number; storageKey?: string
} = {}) {
  const { minWidth = 280, maxWidth = 800, defaultWidth = 420, storageKey = 'sidebar-width' } = options
  const sidebarWidth = ref(defaultWidth)
  const isResizing = ref(false)

  /** 从 localStorage 恢复保存的宽度 */
  const loadWidth = () => {
    const saved = localStorage.getItem(storageKey)
    if (saved) { const w = parseInt(saved, 10); if (w >= minWidth && w <= maxWidth) sidebarWidth.value = w }
  }

  /** 开始拖拽 @param e 鼠标事件 */
  const startResize = (e: MouseEvent) => {
    e.preventDefault(); isResizing.value = true; document.body.style.cursor = 'col-resize'; document.body.style.userSelect = 'none'
    const startX = e.clientX; const startWidth = sidebarWidth.value
    const onMouseMove = (ev: MouseEvent) => { sidebarWidth.value = Math.max(minWidth, Math.min(maxWidth, startWidth + ev.clientX - startX)) }
    const onMouseUp = () => {
      isResizing.value = false; document.body.style.cursor = ''; document.body.style.userSelect = ''
      localStorage.setItem(storageKey, String(sidebarWidth.value))
      document.removeEventListener('mousemove', onMouseMove); document.removeEventListener('mouseup', onMouseUp)
    }
    document.addEventListener('mousemove', onMouseMove); document.addEventListener('mouseup', onMouseUp)
  }

  onMounted(() => { loadWidth() })

  return { sidebarWidth, isResizing, startResize }
}
