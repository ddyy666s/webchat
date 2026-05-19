const path = require('path');

const ROOT = __dirname;

module.exports = {
  apps: [
    {
      name: 'webchat-stack',
      cwd: ROOT,
      script: '/bin/bash',
      args: '-c "docker compose up --abort-on-container-exit --remove-orphans"',
      autorestart: true,
      restart_delay: 10000,
      max_restarts: 5,
      watch: false,
      kill_timeout: 30000,
      log_date_format: 'YYYY-MM-DD HH:mm:ss',
      merge_logs: true,
    },
    {
      name: 'webrtc-sfu',
      cwd: path.join(ROOT, 'webrtc-sfu'),
      script: 'server.js',
      interpreter: 'node',
      autorestart: true,
      restart_delay: 5000,
      max_restarts: 10,
      watch: false,
      kill_timeout: 10000,
      log_date_format: 'YYYY-MM-DD HH:mm:ss',
      merge_logs: true,
    },
  ]
};
