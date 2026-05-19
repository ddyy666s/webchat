/** Element Plus 消息通知封装 @module notify */
import { ElMessage } from 'element-plus'

/** 消息通知工具对象 */
export const notify = {
  /** 成功通知 @param msg 提示文本 */
  success(msg: string) { return ElMessage.success(msg) },
  /** 警告通知 @param msg 提示文本 */
  warning(msg: string) { return ElMessage.warning(msg) },
  /** 错误通知 @param msg 提示文本 */
  error(msg: string) { return ElMessage.error(msg) },
  /** 信息通知 @param msg 提示文本 */
  info(msg: string) { return ElMessage.info(msg) }
}
