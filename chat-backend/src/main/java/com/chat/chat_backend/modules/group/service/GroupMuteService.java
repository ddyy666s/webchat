package com.chat.chat_backend.modules.group.service;

import java.util.List;

public interface GroupMuteService {

    boolean isMuted(Long groupId, Long userId);

    void muteMember(Long userId, Long groupId, Long memberId, Integer minutes);

    void unmuteMember(Long userId, Long groupId, Long memberId);

    void batchMute(Long userId, Long groupId, List<Long> memberIds, Integer minutes);
}
