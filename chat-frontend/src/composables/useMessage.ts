/** Element Plus 消息弹窗封装 composable @module useMessage */
import { ElMessage, ElMessageBox } from 'element-plus'

/** 消息提示封装 hook @returns 各类消息提示方法 */
export const useMessage = () => {
  /** 成功提示 @param msg 提示文本 */
  const success = (msg: string) => ElMessage.success(msg)
  /** 错误提示 @param msg 提示文本 */
  const error = (msg: string) => ElMessage.error(msg)
  /** 警告提示 @param msg 提示文本 */
  const warning = (msg: string) => ElMessage.warning(msg)
  /** 信息提示 @param msg 提示文本 */
  const info = (msg: string) => ElMessage.info(msg)
  /** 确认对话框 @param message 确认内容 @param title 标题 @returns Promise */
  const confirm = (message: string, title?: string) => ElMessageBox.confirm(message, title || '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }).then(() => {})
  /** 提示对话框 @param message 提示内容 @param title 标题 @returns Promise */
  const alert = (message: string, title?: string) => ElMessageBox.alert(message, title || '提示', { confirmButtonText: '确定' }).then(() => {})
  /** 输入对话框 @param message 提示内容 @param defaultValue 默认值 @returns 用户输入的值 */
  const prompt = (message: string, defaultValue?: string) => ElMessageBox.prompt(message, '提示', { confirmButtonText: '确定', cancelButtonText: '取消', inputValue: defaultValue || '' }).then(({ value }) => value)

  return { success, error, warning, info, confirm, alert, prompt }
}
