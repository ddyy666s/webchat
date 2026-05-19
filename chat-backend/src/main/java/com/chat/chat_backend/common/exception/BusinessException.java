package com.chat.chat_backend.common.exception;

import com.chat.chat_backend.common.result.ResultCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 自定义业务异常，携带错误码和错误消息
 *
 * @author chat-backend
 * @since 2026-05-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BusinessException extends RuntimeException {

    private Integer code;
    private String message;

    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BusinessException(String message) {
        super(message);
        this.code = 500;
        this.message = message;
    }
}
