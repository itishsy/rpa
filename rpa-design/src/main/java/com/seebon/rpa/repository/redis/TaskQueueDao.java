package com.seebon.rpa.repository.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

@Slf4j
@Repository
public class TaskQueueDao extends RedisBase {
    @Autowired
    private RedisLockRegistry redisLockRegistry;
    /**
     * key
     */
    private static final String LOCK_KEY = "TASK_QUEUE_FIRST_";
    /**
     * 加锁失效时间，单位：分钟
     */
    private static final long LOCK_EXPIRE = 1;

    public boolean tryLock(String type) {
        return tryLock(LOCK_KEY + type, LOCK_KEY + type, LOCK_EXPIRE);
    }

    public void unLock(String type) {
        remove(LOCK_KEY + type);
    }

    public Lock lock(String type) {
        return redisLockRegistry.obtain(LOCK_KEY + type);
    }

    public boolean tryLock(Lock lock) {
        try {
            if (lock.tryLock(TaskQueueDao.LOCK_EXPIRE, TimeUnit.MINUTES)) {
                return true;
            }
        } catch (Exception e) {
            log.error("获取队列锁异常，请稍后再试.", e);
        }
        return false;
    }
}
