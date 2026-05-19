/** WebSocket 回调注册模块 @module websocketCallbacks */

/** WebSocket 消息回调类型 */
export type MessageCallback = (data: any) => void

/** 消息回调列表 */
export const messageCallbacks: MessageCallback[] = []
/** 在线状态回调列表 */
export const statusCallbacks: MessageCallback[] = []
/** 群消息回调列表 */
export const groupMessageCallbacks: MessageCallback[] = []
/** 通话信令回调列表 */
export const callSignalCallbacks: MessageCallback[] = []
/** 系统通知回调列表 */
export const notificationCallbacks: MessageCallback[] = []
/** 好友请求回调列表 */
export const friendRequestCallbacks: MessageCallback[] = []
/** 好友请求处理结果回调列表 */
export const friendRequestHandledCallbacks: MessageCallback[] = []

/** 注册消息回调 @param callback 回调函数 */
export const onMessage = (callback: MessageCallback) => { messageCallbacks.push(callback) }
/** 注册在线状态回调 @param callback 回调函数 */
export const onStatus = (callback: MessageCallback) => { statusCallbacks.push(callback) }
/** 注册群消息回调 @param callback 回调函数 */
export const onGroupMessage = (callback: MessageCallback) => { groupMessageCallbacks.push(callback) }
/** 注册通话信令回调 @param callback 回调函数 */
export const onCallSignal = (callback: MessageCallback) => { callSignalCallbacks.push(callback) }
/** 注册系统通知回调 @param callback 回调函数 */
export const onNotification = (callback: MessageCallback) => { notificationCallbacks.push(callback) }
/** 注册好友请求回调 @param callback 回调函数 */
export const onFriendRequest = (callback: MessageCallback) => { friendRequestCallbacks.push(callback) }
/** 注册好友请求处理结果回调 @param callback 回调函数 */
export const onFriendRequestHandled = (callback: MessageCallback) => { friendRequestHandledCallbacks.push(callback) }
