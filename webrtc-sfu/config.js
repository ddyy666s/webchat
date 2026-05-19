/**
 * SFU 配置常量
 * @module config
 */
require('dotenv').config();

const PORT = Number(process.env.PORT || 3000);
const JWT_SECRET = process.env.JWT_SECRET || 'your-secret-key-please-change-it-in-production-2026';
const ANNOUNCED_IP = process.env.ANNOUNCED_IP || '127.0.0.1';
const RTC_MIN_PORT = Number(process.env.RTC_MIN_PORT || 2000);
const RTC_MAX_PORT = Number(process.env.RTC_MAX_PORT || 2100);
const REJOIN_GRACE_MS = Number(process.env.REJOIN_GRACE_MS || 30000);

const mediaCodecs = [
  { kind: 'audio', mimeType: 'audio/opus', clockRate: 48000, channels: 2 },
  { kind: 'video', mimeType: 'video/VP8', clockRate: 90000, parameters: { 'x-google-start-bitrate': 1000 } }
];

module.exports = { PORT, JWT_SECRET, ANNOUNCED_IP, RTC_MIN_PORT, RTC_MAX_PORT, REJOIN_GRACE_MS, mediaCodecs };
