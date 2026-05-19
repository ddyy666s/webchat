/**
 * 共享状态管理（所有 Map 实例）
 * @module state
 */
const rooms = new Map();
const peers = new Map();
const invitations = new Map();
const userSockets = new Map();
const onlineUsers = new Map();
const participantCleanupTimers = new Map();
let worker = null;

module.exports = { rooms, peers, invitations, userSockets, onlineUsers, participantCleanupTimers, getWorker: () => worker, setWorker: (w) => { worker = w; } };
