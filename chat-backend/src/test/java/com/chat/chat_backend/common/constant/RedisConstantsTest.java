package com.chat.chat_backend.common.constant;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RedisConstantsTest {

    @Test
    void keyConstants_shouldHaveCorrectValues() {
        assertEquals("online:users", RedisConstants.ONLINE_USERS);
        assertEquals("unread:", RedisConstants.UNREAD_COUNT);
        assertEquals("blacklist:token:", RedisConstants.TOKEN_BLACKLIST);
    }

    @Test
    void expireConstants_shouldHaveCorrectValues() {
        assertEquals(1800L, RedisConstants.ONLINE_EXPIRE_SECONDS);
        assertEquals(86400L, RedisConstants.TOKEN_BLACKLIST_EXPIRE_SECONDS);
    }
}
