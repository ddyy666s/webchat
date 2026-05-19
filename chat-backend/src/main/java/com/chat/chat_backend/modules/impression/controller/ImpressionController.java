package com.chat.chat_backend.modules.impression.controller;

import com.chat.chat_backend.common.result.Result;
import com.chat.chat_backend.modules.impression.dto.request.AddImpressionRequest;
import com.chat.chat_backend.modules.impression.dto.response.ImpressionVO;
import com.chat.chat_backend.modules.impression.service.ImpressionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户印象/评价控制器
 * @author chat-backend
 * @since 2026-05-12
 */
@RestController
@RequestMapping("/impression")
@RequiredArgsConstructor
@Tag(name = "Impression", description = "用户印象/评价相关接口")
public class ImpressionController {

    private final ImpressionService impressionService;

    @PostMapping
    @Operation(summary = "添加评价")
    public Result<Void> addImpression(HttpServletRequest request, @Valid @RequestBody AddImpressionRequest req) {
        Long userId = (Long) request.getAttribute("userId");
        impressionService.addImpression(userId, req);
        return Result.success("评价成功", null);
    }

    @GetMapping("/to-me")
    @Operation(summary = "获取对我的评价")
    public Result<List<ImpressionVO>> getImpressionsToMe(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<ImpressionVO> result = impressionService.getImpressionsToMe(userId);
        return Result.success(result);
    }

    @GetMapping("/by-me")
    @Operation(summary = "获取我给出的评价")
    public Result<List<ImpressionVO>> getImpressionsByMe(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<ImpressionVO> result = impressionService.getImpressionsByMe(userId);
        return Result.success(result);
    }

    @DeleteMapping("/{impressionId}")
    @Operation(summary = "删除评价")
    public Result<Void> deleteImpression(HttpServletRequest request, @PathVariable Long impressionId) {
        Long userId = (Long) request.getAttribute("userId");
        impressionService.deleteImpression(userId, impressionId);
        return Result.success("删除成功", null);
    }
}
