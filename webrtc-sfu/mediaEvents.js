/**
 * 媒体相关 Socket.io 事件处理器
 * @module mediaEvents
 */
const { toId } = require('./roomManager');
const { peers, rooms } = require('./state');
const { createWebRtcTransport } = require('./roomManager');

/** 注册媒体相关事件 @param {import('socket.io').Socket} socket */
function registerMediaEvents(socket) {
  socket.on('createWebRtcTransport', async ({ direction } = {}, cb = () => {}) => {
    try {
      const peer = peers.get(socket.id);
      if (!peer) return cb({ error: 'peer not joined' });
      const room = rooms.get(peer.roomId);
      if (!room) return cb({ error: 'room not found' });
      const { ensureRouter } = require('./roomManager');
      const router = await ensureRouter(room);
      const transport = await createWebRtcTransport(router);
      transport.appData = { direction: direction === 'send' ? 'send' : 'recv' };
      peer.transports.set(transport.id, transport);
      return cb({ id: transport.id, iceParameters: transport.iceParameters, iceCandidates: transport.iceCandidates, dtlsParameters: transport.dtlsParameters });
    } catch (err) { return cb({ error: err.message }); }
  });

  socket.on('connectTransport', async ({ transportId, dtlsParameters }, cb = () => {}) => {
    try {
      const transport = peers.get(socket.id)?.transports.get(transportId);
      if (!transport) return cb({ error: 'transport not found' });
      await transport.connect({ dtlsParameters });
      return cb({ ok: true });
    } catch (err) { return cb({ error: err.message }); }
  });

  socket.on('produce', async ({ transportId, kind, rtpParameters }, cb = () => {}) => {
    try {
      const peer = peers.get(socket.id);
      const transport = peer?.transports.get(transportId);
      if (!transport) return cb({ error: 'transport not found' });
      const producer = await transport.produce({ kind, rtpParameters, appData: { user: peer.user } });
      peer.producers.set(producer.id, producer);
      producer.observer.on('close', () => { peer.producers.delete(producer.id); require('./roomManager').emitToRoomPeers(peer.roomId, 'producerClosed', { producerId: producer.id }, socket.id); });
      producer.on('transportclose', () => producer.close());
      require('./roomManager').emitToRoomPeers(peer.roomId, 'newProducer', { producerId: producer.id, kind: producer.kind, user: peer.user }, socket.id);
      return cb({ id: producer.id });
    } catch (err) { return cb({ error: err.message }); }
  });

  socket.on('pauseProducer', async ({ producerId }, cb = () => {}) => {
    try { const p = peers.get(socket.id)?.producers.get(toId(producerId)); if (!p) return cb({ error: 'producer not found' }); await p.pause(); return cb({ ok: true }); } catch (err) { return cb({ error: err.message }); }
  });

  socket.on('resumeProducer', async ({ producerId }, cb = () => {}) => {
    try { const p = peers.get(socket.id)?.producers.get(toId(producerId)); if (!p) return cb({ error: 'producer not found' }); await p.resume(); return cb({ ok: true }); } catch (err) { return cb({ error: err.message }); }
  });

  socket.on('consume', async ({ producerId, rtpCapabilities }, cb = () => {}) => {
    try {
      const peer = peers.get(socket.id);
      if (!peer) return cb({ error: 'peer not joined' });
      const room = rooms.get(peer.roomId);
      if (!room?.router) return cb({ error: 'room media router is not ready' });
      if (!room.router.canConsume({ producerId, rtpCapabilities })) return cb({ error: 'cannot consume' });
      let rt = [...peer.transports.values()].find(t => t.appData.direction === 'recv');
      if (!rt) { rt = await createWebRtcTransport(room.router); rt.appData = { direction: 'recv' }; peer.transports.set(rt.id, rt); }
      const consumer = await rt.consume({ producerId, rtpCapabilities, paused: true });
      peer.consumers.set(consumer.id, consumer);
      consumer.on('transportclose', () => peer.consumers.delete(consumer.id));
      consumer.on('producerclose', () => peer.consumers.delete(consumer.id));
      let producerUser = null;
      for (const rpid of room.peers) { const rp = peers.get(rpid); if (!rp) continue; const prod = rp.producers.get(producerId); if (prod) { producerUser = rp.user; break; } }
      return cb({ transportOptions: { id: rt.id, iceParameters: rt.iceParameters, iceCandidates: rt.iceCandidates, dtlsParameters: rt.dtlsParameters }, consumerOptions: { id: consumer.id, producerId, kind: consumer.kind, rtpParameters: consumer.rtpParameters }, user: producerUser });
    } catch (err) { return cb({ error: err.message }); }
  });

  socket.on('resume', async ({ consumerId }, cb = () => {}) => {
    try { const c = peers.get(socket.id)?.consumers.get(toId(consumerId)); if (!c) return cb({ error: 'consumer not found' }); await c.resume(); return cb({ ok: true }); } catch (err) { return cb({ error: err.message }); }
  });
}

module.exports = { registerMediaEvents };
