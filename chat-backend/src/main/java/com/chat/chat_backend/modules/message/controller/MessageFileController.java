package com.chat.chat_backend.modules.message.controller;

import com.chat.chat_backend.common.result.Result;
import com.chat.chat_backend.common.utils.OssUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;

@Slf4j
@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
@Tag(name = "消息文件上传", description = "图片上传、语音上传、音频代理接口")
public class MessageFileController {

    private final OssUtil ossUtil;

    @PostMapping("/upload/image")
    @Operation(summary = "上传图片", description = "上传聊天图片到OSS或本地存储")
    public Result<String> uploadImage(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        Long userId = (Long) request.getAttribute("userId");
        try {
            String url = ossUtil.uploadFile(file, "chat/images/");
            log.info("图片上传成功: userId={}, url={}", userId, url);
            return Result.success(url);
        } catch (Exception e) {
            log.error("上传图片失败", e);
            return Result.error("上传失败");
        }
    }

    @PostMapping("/upload/voice")
    @Operation(summary = "上传语音", description = "上传语音消息到OSS或本地存储")
    public Result<String> uploadVoice(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        Long userId = (Long) request.getAttribute("userId");
        try {
            String url = ossUtil.uploadFile(file, "chat/voice/");
            log.info("语音上传成功: userId={}, url={}", userId, url);
            return Result.success(url);
        } catch (Exception e) {
            log.error("上传语音失败", e);
            return Result.error("上传失败");
        }
    }

    @GetMapping("/proxy-audio")
    @Operation(summary = "代理音频", description = "代理OSS音频文件以解决跨域问题")
    public void proxyAudio(@RequestParam String url, HttpServletResponse response) {
        try {
            URL ossUrl = URI.create(url).toURL();
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) ossUrl.openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            conn.connect();

            String contentType = conn.getContentType();
            if (contentType != null) response.setContentType(contentType);
            else response.setContentType("audio/webm");

            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Cache-Control", "public, max-age=86400");

            try (InputStream is = conn.getInputStream()) {
                is.transferTo(response.getOutputStream());
                response.flushBuffer();
            }
            conn.disconnect();
        } catch (Exception e) {
            log.error("代理音频失败: {}", url, e);
            response.setStatus(500);
        }
    }
}
