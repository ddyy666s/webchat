import { ref, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { uploadVoiceApi } from '@/api/message'

export function useRecording(emit: (event: string, ...args: any[]) => void) {
  const isRecording = ref(false)
  const recordDuration = ref(0)
  let mediaRecorder: MediaRecorder | null = null
  let audioChunks: Blob[] = []
  let recordingTimer: number | null = null
  let startTime: number = 0
  let mediaStream: MediaStream | null = null

  const getSupportedMimeType = () => {
    const types = ['audio/webm;codecs=opus', 'audio/webm', 'audio/ogg;codecs=opus', 'audio/mp4', 'audio/mpeg']
    return types.find(t => MediaRecorder.isTypeSupported(t)) || ''
  }

  const startRecord = async () => {
    try {
      mediaStream = await navigator.mediaDevices.getUserMedia({ audio: true })
      const mimeType = getSupportedMimeType()
      const options: any = mimeType ? { mimeType } : {}
      mediaRecorder = new MediaRecorder(mediaStream, options)
      audioChunks = []; startTime = Date.now(); recordDuration.value = 0
      recordingTimer = setInterval(() => {
        recordDuration.value = Math.floor((Date.now() - startTime) / 1000)
        if (recordDuration.value >= 60) stopRecord()
      }, 200) as unknown as number
      mediaRecorder.ondataavailable = (e) => { audioChunks.push(e.data) }
      mediaRecorder.onstop = async () => {
        const duration = Math.floor((Date.now() - startTime) / 1000)
        if (duration < 1) { ElMessage.warning('录音时间太短'); mediaStream?.getTracks().forEach(t => t.stop()); mediaStream = null; return }
        const mimeType = getSupportedMimeType() || 'audio/webm'
        const ext = mimeType.includes('mp4') ? 'mp4' : mimeType.includes('ogg') ? 'ogg' : 'webm'
        const blob = new Blob(audioChunks, { type: mimeType })
        const file = new File([blob], `voice_${Date.now()}.${ext}`, { type: mimeType })
        try { const url = await uploadVoiceApi(file); emit('sendVoice', url, duration) }
        catch { /* error already shown by response interceptor */ }
        mediaStream?.getTracks().forEach(t => t.stop()); mediaStream = null
      }
      mediaRecorder.start(100); isRecording.value = true
    } catch { ElMessage.error('无法获取麦克风权限') }
  }

  const stopRecord = () => {
    if (mediaRecorder && isRecording.value && mediaRecorder.state !== 'inactive') {
      mediaRecorder.stop(); isRecording.value = false
      if (recordingTimer) clearInterval(recordingTimer)
    }
  }

  const cancelRecord = () => {
    if (mediaRecorder && isRecording.value && mediaRecorder.state !== 'inactive') {
      mediaRecorder.stop(); isRecording.value = false
      if (recordingTimer) clearInterval(recordingTimer)
      ElMessage.info('已取消录音')
      mediaStream?.getTracks().forEach(t => t.stop()); mediaStream = null
    }
  }

  onUnmounted(() => {
    if (mediaRecorder && isRecording.value) mediaRecorder.stop()
    if (mediaStream) mediaStream.getTracks().forEach(t => t.stop())
  })

  return { isRecording, recordDuration, startRecord, stopRecord, cancelRecord }
}
