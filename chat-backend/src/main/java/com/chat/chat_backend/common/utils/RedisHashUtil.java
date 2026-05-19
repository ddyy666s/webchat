package com.chat.chat_backend.common.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Redis Hash操作工具类，提供Hash数据结构的常用操作
 * @author chat-backend
 * @since 2026-05-12
 */
@Component
@RequiredArgsConstructor
public class RedisHashUtil {

    /** Redis模板，用于执行哈希操作 */
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 向Hash中存入字段-值对
     * @param key Redis键
     * @param hashKey Hash字段键
     * @param value 存储的值
     */
    public void hashPut(String key, Object hashKey, Object value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    /**
     * 获取指定Hash字段的值
     * @param key Redis键
     * @param hashKey Hash字段键
     * @return 字段值，不存在时返回null
     */
    public Object hashGet(String key, Object hashKey) {
        return redisTemplate.opsForHash().get(key, hashKey);
    }

    /**
     * 对Hash中的某个字段执行增量操作
     * @param key Redis键
     * @param hashKey Hash字段键
     * @param delta 增量值
     * @return 增加后的新值
     */
    public Long hashIncrement(String key, Object hashKey, long delta) {
        return redisTemplate.opsForHash().increment(key, hashKey, delta);
    }

    /**
     * 删除Hash中的一个或多个字段
     * @param key Redis键
     * @param hashKeys 待删除的Hash字段键
     */
    public void hashDelete(String key, Object... hashKeys) {
        redisTemplate.opsForHash().delete(key, hashKeys);
    }
}
