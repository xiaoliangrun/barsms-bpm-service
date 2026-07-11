package com.cpic.barsms.bpm.common.redis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedisDistributedLockTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    @SuppressWarnings("rawtypes")
    private ValueOperations valueOperations;

    @InjectMocks
    private RedisDistributedLock redisDistributedLock;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        // 仅 tryLock / executeWithLock 路径用到 opsForValue，unlock 路径用不到，故用 lenient
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    @SuppressWarnings("unchecked")
    void tryLock_acquiresOnFirstSetIfAbsentSuccess() {
        when(valueOperations.setIfAbsent(any(String.class), any(String.class), anyLong(), eq(TimeUnit.SECONDS)))
                .thenReturn(Boolean.TRUE);

        String lockValue = redisDistributedLock.tryLock("biz:1", 1000L);

        assertNotNull(lockValue);
        verify(valueOperations, times(1)).setIfAbsent(eq("batch:lock:biz:1"), eq(lockValue), anyLong(), eq(TimeUnit.SECONDS));
    }

    @Test
    @SuppressWarnings("unchecked")
    void tryLock_returnsNullWhenTimeoutExceeded() {
        // 始终抢锁失败，触发超时退出
        when(valueOperations.setIfAbsent(any(String.class), any(String.class), anyLong(), eq(TimeUnit.SECONDS)))
                .thenReturn(Boolean.FALSE);

        String lockValue = redisDistributedLock.tryLock("biz:2", 120L);

        assertNull(lockValue);
    }

    @Test
    @SuppressWarnings("unchecked")
    void unlock_returnsFalseWhenLockValueNull() {
        assertFalse(redisDistributedLock.unlock("biz:3", null));
        verifyNoInteractions(valueOperations);
    }

    @Test
    @SuppressWarnings("unchecked")
    void unlock_successWhenScriptReturnsOne() {
        when(redisTemplate.execute(any(RedisScript.class), any(List.class), any()))
                .thenReturn(1L);

        assertTrue(redisDistributedLock.unlock("biz:4", "some-uuid"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void unlock_returnsFalseWhenScriptReturnsZero() {
        when(redisTemplate.execute(any(RedisScript.class), any(List.class), any()))
                .thenReturn(0L);

        assertFalse(redisDistributedLock.unlock("biz:5", "mismatched-uuid"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void executeWithLock_runsCallbackAndReleasesLock() {
        when(valueOperations.setIfAbsent(any(String.class), any(String.class), anyLong(), eq(TimeUnit.SECONDS)))
                .thenReturn(Boolean.TRUE);
        when(redisTemplate.execute(any(RedisScript.class), any(List.class), any()))
                .thenReturn(1L);

        String result = redisDistributedLock.executeWithLock("biz:6", 1000L, () -> "done");

        assertEquals("done", result);
        // 确认释放锁的 Lua 脚本被执行
        verify(redisTemplate).execute(any(RedisScript.class), any(List.class), any());
    }

    @Test
    @SuppressWarnings("unchecked")
    void executeWithLock_returnsNullWhenLockNotAcquired() {
        when(valueOperations.setIfAbsent(any(String.class), any(String.class), anyLong(), eq(TimeUnit.SECONDS)))
                .thenReturn(Boolean.FALSE);

        String result = redisDistributedLock.executeWithLock("biz:7", 120L, () -> "should-not-run");

        assertNull(result);
    }
}
