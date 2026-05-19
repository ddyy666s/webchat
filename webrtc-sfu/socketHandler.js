/**
 * Socket.io 认证中间件
 * @module socketHandler
 */
const jwt = require('jsonwebtoken');
const { JWT_SECRET } = require('./config');
const { toId } = require('./roomManager');

/** 注册认证中间件 @param {import('socket.io').Server} io */
function registerSocketHandlers(io) {
  io.use((socket, next) => {
    try {
      const raw = socket.handshake.auth?.token;
      if (!raw) return next(new Error('UNAUTHORIZED: missing token'));
      const token = String(raw).replace(/^Bearer\s+/i, '').replace(/"/g, '');
      const payload = jwt.verify(token, JWT_SECRET);
      const uid = toId(payload.userId || payload.sub);
      if (!uid) return next(new Error('UNAUTHORIZED: missing user id'));
      socket.user = { id: uid, username: payload.username || `user-${uid}`, nickname: payload.nickname || payload.username || `user-${uid}`, role: payload.role || 'user' };
      return next();
    } catch (err) { return next(new Error(`UNAUTHORIZED: ${err.message}`)); }
  });
}

module.exports = { registerSocketHandlers };
