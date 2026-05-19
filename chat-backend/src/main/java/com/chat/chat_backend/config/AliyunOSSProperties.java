package com.chat.chat_backend.config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 阿里云OSS配置属性类，绑定"aliyun.oss"前缀的配置项
 * @author chat-backend
 * @since 2026-05-12
 */
@Data
@Component
@ConfigurationProperties(prefix = "aliyun.oss")
@Schema(description = "阿里云OSS配置属性")
public class AliyunOSSProperties {
    @Schema(description = "OSS访问端点URL")
    private String endpoint;

    @Schema(description = "OSS存储空间名称")
    private String bucketName;

    @Schema(description = "OSS地域ID（如cn-beijing）")
    private String region;

    @Schema(description = "阿里云访问密钥ID")
    private String accessKeyId;

    @Schema(description = "阿里云访问密钥密码")
    private String accessKeySecret;
}
