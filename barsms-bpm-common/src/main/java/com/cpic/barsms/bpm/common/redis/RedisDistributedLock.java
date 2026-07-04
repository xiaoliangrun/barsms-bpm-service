package com.cpic.barsms.bpm.common.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Redis 分布式锁工具类
 */
@Slf4j
@Component
public class RedisDistributedLock {

    @Autowired
    private StringRedisTemplate redisTemplate;

    /** 锁前缀 */
    private static final String LOCK_PREFIX = "batch:lock:";

    /** 默认锁过期时间（秒） */
    private static final long DEFAULT_EXPIRE_SECONDS = 300;

    /** Lua 脚本：释放锁时校验 value 防止误删 */
    private static final String UNLOCK_SCRIPT =
            "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                    "return redis.call('del', KEYS[1]) " +
                    "else return 0 end";

    /**
     * 尝试获取锁
     *
     * @param lockKey  锁的 key
     * @param timeout  等待超时时间（毫秒）
     * @return 锁的 value（用于释放锁），获取失败返回 null
     */
    public String tryLock(String lockKey, long timeout) {
        String lockValue = UUID.randomUUID().toString();
        long startTime = System.currentTimeMillis();

        while (System.currentTimeMillis() - startTime < timeout) {
            Boolean success = redisTemplate.opsForValue()
                    .setIfAbsent(LOCK_PREFIX + lockKey, lockValue, DEFAULT_EXPIRE_SECONDS, TimeUnit.SECONDS);

            if (Boolean.TRUE.equals(success)) {
                log.debug("获取分布式锁成功: {}", lockKey);
                return lockValue;
            }

            try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        }

        log.warn("获取分布式锁失败: {}", lockKey);
        return null;
    }

    /**
     * 释放锁
     *
     * @param lockKey   锁的 key
     * @param lockValue 锁的 value（用于校验）
     * @return 是否释放成功
     */
    public boolean unlock(String lockKey, String lockValue) {
        if (lockValue == null) {
            return false;
        }

        try {
            DefaultRedisScript<Long> script = new DefaultRedisScript<>();
            script.setScriptText(UNLOCK_SCRIPT);
            script.setResultType(Long.class);

            Long result = redisTemplate.execute(script,
                    Collections.singletonList(LOCK_PREFIX + lockKey),
                    lockValue);

            boolean success = result != null && result == 1L;
            if (success) {
                log.debug("释放分布式锁成功: {}", lockKey);
            } else {
                log.warn("释放分布式锁失败，锁已被其他进程持有: {}", lockKey);
            }
            return success;
        } catch (Exception e) {
            log.error("释放分布式锁异常: {}", lockKey, e);
            return false;
        }
    }

    /**
     * 执行带锁的操作
     *
     * @param lockKey  锁的 key
     * @param timeout  等待超时时间（毫秒）
     * @param callback 回调函数
     * @return 回调函数的返回值，获取锁失败返回 null
     */
    public <T> T executeWithLock(String lockKey, long timeout, LockCallback<T> callback) {
        String lockValue = tryLock(lockKey, timeout);
        if (lockValue == null) {
            return null;
        }

        try {
            return callback.execute();
        } finally {
            unlock(lockKey, lockValue);
        }
    }

    /**
     * 锁执行回调接口
     */
    @FunctionalInterface
    public interface LockCallback<T> {
        T execute();
    }
}
