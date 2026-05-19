/**
 * 房间相关 Socket.io 事件处理器
 * @module roomEvents
 */
const { toId, getOrCreateRoom, createInvite, membersSnapshot, getRoom, ensureRouter, emitRoomMembers, cleanupPeer, endRoom } = require('./roomManager');
const { peers, rooms } = require('./state');

/** 注册房间相关事件 @param {import('socket.io').Socket} socket @param {object} user */
function registerRoomEvents(socket, user) {
  socket.on('createRoomInvite', ({ targetUserId, roomId, roomName, callType = 'video' }, cb = () => {}) => {
    try {
      const room = getOrCreateRoom(toId(roomId), user, roomName || `${user.nickname}的视频通话`, 'direct');
      const invite = createInvite(room, user, targetUserId, callType);
      if (!invite) return cb({ error: 'targetUserId is invalid' });
      return cb({ inviteId: invite.id, roomId: room.id, roomName: room.name, members: membersSnapshot(room) });
    } catch (err) { return cb({ error: err.message }); }
  });

  socket.on('createGroupRoom', ({ roomId, roomName, participantIds = [], callType = 'video' }, cb = () => {}) => {
    try {
      const rid = toId(roomId);
      if (!rid) return cb({ error: 'roomId is required' });
      const room = getOrCreateRoom(rid, user, roomName || '群视频通话', 'group');
      const ids = new Set((Array.isArray(participantIds) ? participantIds : []).map(toId).filter(Boolean));
      ids.add(user.id);
      for (const pid of ids) { room.participants.add(pid); if (pid !== user.id) createInvite(room, user, pid, callType); }
      return cb({ roomId: room.id, roomName: room.name, members: membersSnapshot(room) });
    } catch (err) { return cb({ error: err.message }); }
  });

  socket.on('respondRoomInvite', ({ inviteId, accept }, cb = () => {}) => {
    try {
      const invitations = require('./state').invitations;
      const inv = invitations.get(toId(inviteId));
      if (!inv || inv.status !== 'pending') return cb({ error: 'invite not found or already handled' });
      if (inv.toUserId !== user.id) return cb({ error: 'invite does not belong to current user' });
      invitations.delete(inv.id);
      inv.status = accept ? 'accepted' : 'rejected';
      const { emitToUser, cleanupEmpty } = require('./roomManager');
      emitToUser(inv.fromUser.id, 'inviteResponded', { inviteId: inv.id, accepted: Boolean(accept), roomId: inv.roomId, by: user });
      if (!accept) { cleanupEmpty(inv.roomId); return cb({ ok: true, accepted: false }); }
      const room = rooms.get(inv.roomId);
      if (!room) return cb({ error: 'room no longer exists' });
      room.participants.add(user.id);
      require('./roomManager').ensureNames(room).set(user.id, user.nickname || user.username);
      emitRoomMembers(inv.roomId);
      return cb({ ok: true, accepted: true, roomId: inv.roomId, roomName: room.name, roomType: room.type });
    } catch (err) { return cb({ error: err.message }); }
  });

  socket.on('joinRoom', async ({ roomId }, cb = () => {}) => {
    try {
      const rid = toId(roomId);
      const room = rooms.get(rid);
      if (!room) return cb({ error: 'room not found' });
      if (!room.participants.has(user.id)) return cb({ error: 'you are not invited to this room' });
      const existing = peers.get(socket.id);
      if (existing && existing.roomId !== rid) cleanupPeer(socket.id, { notifyPeerLeft: true, leftReason: '切换了通话' });
      const router = await ensureRouter(room);
      room.peers.add(socket.id);
      require('./roomManager').ensureNames(room).set(user.id, user.nickname || user.username);
      peers.set(socket.id, { user, socket, roomId: rid, transports: new Map(), producers: new Map(), consumers: new Map() });
      const existingProducers = [];
      for (const pid of room.peers) {
        if (pid === socket.id) continue;
        const op = peers.get(pid);
        if (!op) continue;
        for (const prod of op.producers.values()) existingProducers.push({ producerId: prod.id, kind: prod.kind, user: op.user });
      }
      emitRoomMembers(rid);
      return cb({ rtpCapabilities: router.rtpCapabilities, existingProducers, isCreator: room.creatorUserId === user.id, members: membersSnapshot(room), roomName: room.name, roomType: room.type });
    } catch (err) { return cb({ error: err.message }); }
  });

  socket.on('leaveRoom', (_d, cb = () => {}) => {
    const peer = peers.get(socket.id);
    if (!peer) return cb({ error: 'peer not joined' });
    const rid = peer.roomId;
    cleanupPeer(socket.id, { notifyPeerLeft: true });
    return cb({ ok: true, roomId: rid });
  });

  socket.on('endRoom', (_d, cb = () => {}) => {
    const peer = peers.get(socket.id);
    if (!peer) return cb({ error: 'peer not joined' });
    endRoom(peer.roomId, `${user.nickname || user.username} 结束了通话`, user.id);
    return cb({ ok: true, roomId: peer.roomId });
  });
}

module.exports = { registerRoomEvents };
