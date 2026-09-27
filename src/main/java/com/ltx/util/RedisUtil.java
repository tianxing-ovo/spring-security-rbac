package com.ltx.util;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Redis工具类
 *
 * @author tianxing
 */
@Component
public class RedisUtil {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * String-set(设置键值对)
     *
     * @param key     键
     * @param value   值
     * @param timeout 过期时间
     * @param unit    时间单位
     */
    public void set(@NonNull String key, @NonNull String value, long timeout, @NonNull TimeUnit unit) {
        stringRedisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /**
     * 删除指定的key
     *
     * @param key 键
     */
    public void delete(@NonNull String key) {
        stringRedisTemplate.delete(key);
    }

    /**
     * 获取指定key的值
     *
     * @param key 键
     * @return 值
     */
    public String get(@NonNull String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 设置key的过期时间
     *
     * @param key     键
     * @param timeout 过期时间
     * @param unit    时间单位
     */
    public void expire(@NonNull String key, long timeout, @NonNull TimeUnit unit) {
        stringRedisTemplate.expire(key, timeout, unit);
    }
}
