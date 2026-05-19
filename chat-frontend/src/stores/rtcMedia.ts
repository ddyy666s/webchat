/** 媒体操作模块 @module rtcMedia */
import { type Ref } from 'vue'

/** 创建媒体操作管理器 @param st 响应式状态 @param request Socket 请求函数 @param getProducers 获取生产者Map @returns 媒体操作方法 */
export function createMediaModule(
  st: {
    isMicMuted: Ref<boolean>; isCameraOff: Ref<boolean>
  },
  request: <T = any>(type: string, data?: Record<string, unknown>) => Promise<T>,
  getProducers: () => Map<string, any>
) {
  async function toggleMic() {
    const producer = [...getProducers().values()].find(item => item.kind === 'audio')
    if (!producer) return
    if (producer.paused) { producer.resume(); await request('resumeProducer', { producerId: producer.id }); st.isMicMuted.value = false }
    else { producer.pause(); await request('pauseProducer', { producerId: producer.id }); st.isMicMuted.value = true }
  }

  async function toggleCamera() {
    const producer = [...getProducers().values()].find(item => item.kind === 'video')
    if (!producer) return
    if (producer.paused) { producer.resume(); await request('resumeProducer', { producerId: producer.id }); st.isCameraOff.value = false }
    else { producer.pause(); await request('pauseProducer', { producerId: producer.id }); st.isCameraOff.value = true }
  }

  return { toggleMic, toggleCamera }
}
