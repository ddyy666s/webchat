/** 文件下载工具模块 @module download */

/** 下载聊天记录 @param friendId 好友用户ID @param friendName 好友名称（用于文件名） */
export const downloadChatRecord = async (friendId: number, friendName: string) => {
  const token = localStorage.getItem('chat_token')?.replace(/"/g, '')
  try {
    const response = await fetch(`/api/message/download/${friendId}`, { headers: { 'Authorization': `Bearer ${token}` } })
    const blob = await response.blob(); const url = URL.createObjectURL(blob)
    const a = document.createElement('a'); a.href = url; a.download = `chat_${friendName}_${Date.now()}.txt`
    document.body.appendChild(a); a.click(); document.body.removeChild(a); URL.revokeObjectURL(url)
  } catch (error) { console.error('下载失败', error) }
}

/** 导出 JSON 文件 @param data 要导出的数据 @param filename 文件名 */
export const downloadJson = (data: any, filename: string) => {
  const blob = new Blob([JSON.stringify(data, null, 2)], { type: 'application/json' })
  const url = URL.createObjectURL(blob); const a = document.createElement('a')
  a.href = url; a.download = filename; a.click(); URL.revokeObjectURL(url)
}
