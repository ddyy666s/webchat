<template>
  <div class="empty-chat">
    <div class="empty-bg">
      <div class="empty-orb empty-orb-1"></div>
      <div class="empty-orb empty-orb-2"></div>
      <div class="empty-orb empty-orb-3"></div>
    </div>
    <div class="empty-content">
      <div class="empty-icon">
        <svg viewBox="0 0 120 120" fill="none" xmlns="http://www.w3.org/2000/svg">
          <circle cx="60" cy="60" r="55" stroke="url(#chat-grad)" stroke-width="2" opacity="0.3" />
          <circle cx="60" cy="60" r="45" stroke="url(#chat-grad)" stroke-width="1.5" opacity="0.2" />
          <circle cx="42" cy="48" r="6" fill="url(#chat-grad)" opacity="0.6" />
          <circle cx="78" cy="48" r="6" fill="url(#chat-grad)" opacity="0.6" />
          <path d="M42 66 Q60 84 78 66" stroke="url(#chat-grad)" stroke-width="2.5" stroke-linecap="round" fill="none" opacity="0.5" />
          <defs><linearGradient id="chat-grad" x1="0" y1="0" x2="120" y2="120"><stop offset="0%" stop-color="#6c5ce7" /><stop offset="50%" stop-color="#a29bfe" /><stop offset="100%" stop-color="#fd79a8" /></linearGradient></defs>
        </svg>
        <div class="pulse-ring"></div>
      </div>
      <h3 class="empty-title">开始聊天</h3>
      <p class="empty-desc">从左侧选择一个好友或群聊，开启你们的对话吧</p>
    </div>
  </div>
</template>

<style scoped>
.empty-chat { height: 100%; display: flex; align-items: center; justify-content: center; position: relative; overflow: hidden; }
.empty-bg { position: absolute; inset: 0; pointer-events: none; }
.empty-orb { position: absolute; border-radius: 50%; filter: blur(60px); opacity: 0.15; animation: emptyOrb 15s ease-in-out infinite; }
.empty-orb-1 { width: 300px; height: 300px; background: #6c5ce7; top: -10%; left: -5%; animation-delay: 0s; }
.empty-orb-2 { width: 250px; height: 250px; background: #a29bfe; bottom: -10%; right: -5%; animation-delay: -5s; }
.empty-orb-3 { width: 200px; height: 200px; background: #fd79a8; top: 40%; left: 50%; animation-delay: -10s; }
@keyframes emptyOrb { 0%, 100% { transform: translate(0, 0) scale(1); } 25% { transform: translate(40px, -30px) scale(1.1); } 50% { transform: translate(-20px, 20px) scale(0.9); } 75% { transform: translate(30px, 30px) scale(1.05); } }
.empty-content { position: relative; z-index: 1; text-align: center; animation: fadeInUp 0.8s ease; }
.empty-icon { position: relative; width: 120px; height: 120px; margin: 0 auto 24px; }
.empty-icon svg { width: 100%; height: 100%; animation: pulse 3s ease-in-out infinite; }
.pulse-ring { position: absolute; top: 50%; left: 50%; width: 120px; height: 120px; transform: translate(-50%, -50%); border-radius: 50%; border: 2px solid rgba(108, 92, 231, 0.15); animation: pulseRing 3s ease-in-out infinite; }
@keyframes pulseRing { 0% { transform: translate(-50%, -50%) scale(1); opacity: 1; } 50% { transform: translate(-50%, -50%) scale(1.3); opacity: 0; } 100% { transform: translate(-50%, -50%) scale(1.3); opacity: 0; } }
@keyframes pulse { 0%, 100% { transform: scale(1); } 50% { transform: scale(1.05); } }
.empty-title { font-size: 22px; font-weight: 700; color: var(--text-primary); margin: 0 0 10px; background: linear-gradient(135deg, #6c5ce7, #a29bfe); -webkit-background-clip: text; -webkit-text-fill-color: transparent; background-clip: text; }
.empty-desc { font-size: 14px; color: var(--text-secondary); margin: 0; line-height: 1.6; }
@keyframes fadeInUp { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }
</style>
