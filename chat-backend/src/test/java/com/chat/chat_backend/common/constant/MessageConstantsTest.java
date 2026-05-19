package com.chat.chat_backend.common.constant;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MessageConstantsTest {

    @Test
    void messageTypeConstants_shouldHaveCorrectValues() {
        assertEquals(1, MessageConstants.MSG_TYPE_TEXT);
        assertEquals(2, MessageConstants.MSG_TYPE_IMAGE);
        assertEquals(3, MessageConstants.MSG_TYPE_FILE);
        assertEquals(4, MessageConstants.MSG_TYPE_VOICE);
    }

    @Test
    void messageStatusConstants_shouldHaveCorrectValues() {
        assertEquals(0, MessageConstants.MSG_UNREAD);
        assertEquals(1, MessageConstants.MSG_READ);
    }

    @Test
    void messageLimitConstants_shouldHaveCorrectValues() {
        assertEquals(500, MessageConstants.MAX_CONTENT_LENGTH);
        assertEquals(20, MessageConstants.PAGE_SIZE);
        assertEquals(2, MessageConstants.RECALL_TIME_LIMIT_MINUTES);
    }

    @Test
    void downloadConstants_shouldHaveCorrectValues() {
        assertEquals(500, MessageConstants.MAX_DOWNLOAD_SIZE);
        assertEquals(100, MessageConstants.DEFAULT_DOWNLOAD_SIZE);
        assertEquals("chat_", MessageConstants.DOWNLOAD_FILE_PREFIX);
        assertEquals(".txt", MessageConstants.DOWNLOAD_FILE_EXTENSION);
    }
}
