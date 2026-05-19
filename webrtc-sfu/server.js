/**
 * SFU 服务器入口
 * @module server
 */
require('dotenv').config();
const express = require('express');
const http = require('http');
const mediasoup = require('mediasoup');
const cors = require('cors');
const { Server } = require('socket.io');
const { PORT, RTC_MIN_PORT, RTC_MAX_PORT } = require('./config');
const { setWorker } = require('./state');
const { setIo, cleanupPeer, scheduleCleanup } = require('./roomManager');
const { registerSocketHandlers } = require('./socketHandler');
const { registerRoomEvents } = require('./roomEvents');
const { registerMediaEvents } = require('./mediaEvents');

const app = express();
const httpServer = http.createServer(app);
const io = new Server(httpServer, { cors: { origin: '*' } });

app.use(cors({ origin: '*' }));
app.use(express.json());
setIo(io);
registerSocketHandlers(io);

app.get('/healthz', (_req, res) => {
  const { rooms, onlineUsers } = require('./state');
  res.json({ ok: true, rooms: rooms.size, online: onlineUsers.size });
});

io.on('connection', (socket) => {
  const user = socket.user;
  const { userSockets, onlineUsers } = require('./state');
  if (!userSockets.has(user.id)) userSockets.set(user.id, new Set());
  userSockets.get(user.id).add(socket.id);
  onlineUsers.set(user.id, user);

  registerRoomEvents(socket, user);
  registerMediaEvents(socket);

  socket.on('disconnect', () => {
    const socks = userSockets.get(user.id);
    if (socks) { socks.delete(socket.id); if (socks.size === 0) { userSockets.delete(user.id); onlineUsers.delete(user.id); } }
    const dp = require('./state').peers.get(socket.id);
    cleanupPeer(socket.id, { notifyPeerLeft: true, leftReason: '掉线了', removeParticipant: false, skipEmptyRoomCleanup: true });
    if (dp) scheduleCleanup(dp.roomId, dp.user);
  });
});

async function runMediasoup() {
  const w = await mediasoup.createWorker({ logLevel: 'warn', rtcMinPort: RTC_MIN_PORT, rtcMaxPort: RTC_MAX_PORT });
  w.on('died', () => { console.error('mediasoup worker died'); process.exit(1); });
  setWorker(w);
}

(async () => {
  await runMediasoup();
  httpServer.listen(PORT, () => { console.log(`WebRTC SFU listening on :${PORT}`); });
})();
