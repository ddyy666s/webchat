package com.chat.chat_backend;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public abstract class BaseServiceTest {

    protected void setPrivateField(Object target, String fieldName, Object value) {
        ReflectionTestUtils.setField(target, fieldName, value);
    }

    @SuppressWarnings("unchecked")
    protected <T> T getPrivateField(Object target, String fieldName) {
        return (T) ReflectionTestUtils.getField(target, fieldName);
    }
}
