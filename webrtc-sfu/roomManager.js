/**
 * 房间管理模块 — 房间 CRUD、广播、清理
 * @module roomManager
 */
const { randomUUID } = require('crypto');
const { rooms, peers, invitations, participantCleanupTimers } = require('./state');
const { mediaCodecs, REJOIN_GRACE_MS } = require('./config');

/** @param {*} v @returns {string} */
function toId(v) { return String(v || '').trim(); }

function getOrCreateRoom(roomId, creatorUser, roomName, roomType) {
  const id = roomId || randomUUID().split('-')[0];
  let r = rooms.get(id);
  if (!r) {
    r = { id, name: roomName || '视频通话', type: roomType || 'direct', router: null, creatorUserId: creatorUser.id, participants: new Set([creatorUser.id]), participantNames: new Map([[creatorUser.id, creatorUser.nickname || creatorUser.username]]), peers: new Set() };
    rooms.set(id, r);
  }
  if (!r.creatorUserId) r.creatorUserId = creatorUser.id;
  if (roomName) r.name = roomName;
  ensureNames(r).set(creatorUser.id, creatorUser.nickname || creatorUser.username);
  r.participants.add(creatorUser.id);
  return r;
}

function ensureNames(r) { if (!r.participantNames) r.participantNames = new Map(); return r.participantNames; }
function getRoom(rid) { return rooms.get(rid); }

function destroyRoom(rid) {
  const r = rooms.get(rid);
  if (!r) return;
  for (const k of [...participantCleanupTimers.keys()]) { if (k.startsWith(`${rid}:`)) { clearTimeout(participantCleanupTimers.get(k)); participantCleanupTimers.delete(k); } }
  r.router?.close();
  rooms.delete(rid);
}

function cleanupEmpty(rid) {
  const r = rooms.get(rid);
  if (!r || r.peers.size > 0) return;
  if ([...invitations.values()].some(i => i.roomId === rid && i.status === 'pending')) return;
  destroyRoom(rid);
}

function membersSnapshot(r) {
  const names = ensureNames(r);
  const joined = new Set();
  for (const pid of r.peers) { const p = peers.get(pid); if (p) joined.add(p.user.id); }
  return [...r.participants].map(uid => ({ id: uid, username: names.get(uid) || `user-${uid}`, nickname: names.get(uid) || `user-${uid}`, isCreator: r.creatorUserId === uid, isJoined: joined.has(uid) }));
}

function emitRoomMembers(rid) { const r = rooms.get(rid); if (r) emitToRoomPeers(rid, 'roomMembersUpdate', { roomId: rid, members: membersSnapshot(r) }); }

async function ensureRouter(r) { if (!r.router) r.router = await getWorker().createRouter({ mediaCodecs }); return r.router; }

function endRoom(rid, reason, endedByUserId) {
  const r = rooms.get(rid);
  if (!r) return;
  for (const uid of r.participants) { if (uid !== endedByUserId) emitToUser(uid, 'roomEnded', { roomId: rid, reason, endedByUserId }); }
  for (const inv of [...invitations.values()]) { if (inv.roomId === rid) invitations.delete(inv.id); }
  for (const pid of [...r.peers]) cleanupPeer(pid, { removeParticipant: true, skipEmptyRoomCleanup: true });
  destroyRoom(rid);
}

function createInvite(room, fromUser, targetUserId, callType) {
  const norm = toId(targetUserId);
  if (!norm || norm === fromUser.id) return null;
  room.participants.add(norm);
  const invite = { id: randomUUID(), roomId: room.id, roomName: room.name, roomType: room.type, fromUser, toUserId: norm, callType, status: 'pending' };
  invitations.set(invite.id, invite);
  emitToUser(norm, 'incomingInvite', { inviteId: invite.id, roomId: room.id, roomName: room.name, roomType: room.type, callType, fromUser });
  return invite;
}

let io = null;
function setIo(inst) { io = inst; }
function getWorker() { return require('./state').getWorker(); }

function emitToUser(uid, event, payload) {
  const socks = require('./state').userSockets.get(toId(uid));
  if (!socks) return;
  for (const sid of socks) io.sockets.sockets.get(sid)?.emit(event, payload);
}

function emitToRoomPeers(rid, event, payload, exclude) {
  const r = rooms.get(rid);
  if (!r) return;
  for (const pid of r.peers) { if (pid !== exclude) peers.get(pid)?.socket.emit(event, payload); }
}

function cleanupPeer(sid, opts = {}) {
  const peer = peers.get(sid);
  if (!peer) return;
  const { notifyPeerLeft, leftReason, removeParticipant, skipEmptyRoomCleanup } = { notifyPeerLeft: false, leftReason: '离开了通话', removeParticipant: true, skipEmptyRoomCleanup: false, ...opts };
  const r = rooms.get(peer.roomId);
  if (r) {
    r.peers.delete(sid);
    if (removeParticipant) { r.participants.delete(peer.user.id); ensureNames(r).delete(peer.user.id); if (r.creatorUserId === peer.user.id) r.creatorUserId = [...r.participants][0] || ''; }
  }
  for (const c of peer.consumers.values()) c.close();
  for (const p of peer.producers.values()) p.close();
  for (const t of peer.transports.values()) t.close();
  if (r && notifyPeerLeft) emitToRoomPeers(peer.roomId, 'peerLeft', { roomId: peer.roomId, user: peer.user, reason: leftReason });
  if (r) emitRoomMembers(peer.roomId);
  if (r && !skipEmptyRoomCleanup) cleanupEmpty(peer.roomId);
  peers.delete(sid);
}

function scheduleCleanup(rid, user) {
  const key = `${rid}:${user.id}`;
  clearTimeout(participantCleanupTimers.get(key));
  const t = setTimeout(() => {
    participantCleanupTimers.delete(key);
    const r = rooms.get(rid);
    if (!r) return;
    if ([...r.peers].some(pid => peers.get(pid)?.user.id === user.id)) return;
    r.participants.delete(user.id); ensureNames(r).delete(user.id); emitRoomMembers(rid); cleanupEmpty(rid);
  }, REJOIN_GRACE_MS);
  t.unref?.();
  participantCleanupTimers.set(key, t);
}

async function createWebRtcTransport(router) {
  return router.createWebRtcTransport({ listenIps: [{ ip: '0.0.0.0', announcedIp: require('./config').ANNOUNCED_IP }], enableUdp: true, enableTcp: true, preferUdp: true });
}

module.exports = { getOrCreateRoom, getRoom, destroyRoom, cleanupEmpty, membersSnapshot, emitRoomMembers, ensureRouter, endRoom, createInvite, ensureNames, emitToUser, emitToRoomPeers, cleanupPeer, scheduleCleanup, setIo, toId, createWebRtcTransport };
