package com.seebon.rpa.repository.redis;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Repository
public class RedisBase<T> {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    @Qualifier("kryoRedisTemplate")
    private RedisTemplate<String, T> redisTemplate;

    protected void set(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    protected void set(String key, T value) {
        redisTemplate.opsForValue().set(key, value);
    }

    protected void set(String key, String value,Long seconds) {
        if (seconds != null) {
            stringRedisTemplate.opsForValue().set(key, value, seconds, TimeUnit.SECONDS);
        } else {
            stringRedisTemplate.opsForValue().set(key, value);
        }
    }

    protected void set(String key, T value,long seconds) {
        redisTemplate.opsForValue().set(key, value, seconds, TimeUnit.SECONDS);
    }

    protected String getString(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    protected List<String> getListString(String pattern) {
        Set<String> keys = stringRedisTemplate.keys(pattern);
        if (CollectionUtils.isNotEmpty(keys)) {
            return stringRedisTemplate.opsForValue().multiGet(keys);
        }
        return null;
    }

    protected T getObject(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    protected List<T> getObjectList(Set<String> keys) {
        return redisTemplate.opsForValue().multiGet(keys);
    }

    protected void remove(String key) {
        if(!redisTemplate.delete(key)){
            stringRedisTemplate.delete(key);
        }
    }

    protected void batchRemove(Collection<String> keys) {
        if (redisTemplate.delete(keys)<=0) {
            stringRedisTemplate.delete(keys);
        }
    }

    protected Set<String> getListKey(String prefix) {
        Set<String> keys = redisTemplate.keys(prefix.concat("*"));
        return keys;
    }

    protected Boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key) || stringRedisTemplate.hasKey(key);
        }catch (Exception e){
            return false;
        }
    }

    protected Boolean expire(String key,long seconds){
        if(redisTemplate.expire(key, seconds, TimeUnit.SECONDS)){
            return true;
        }
        return stringRedisTemplate.expire(key, seconds, TimeUnit.SECONDS);
    }

    protected Long increment(String key, long l) {
        return redisTemplate.opsForValue().increment(key, l);
    }
    protected Long decrement(String key, long l) {
        return redisTemplate.opsForValue().increment(key, l);
    }

    protected Boolean expire(String key,long seconds, TimeUnit unit){
        if(redisTemplate.expire(key, seconds, unit)){
            return true;
        }
        return stringRedisTemplate.expire(key, seconds, unit);
    }

    public Boolean tryLock(String key, String value, long timeout) {
        return stringRedisTemplate.opsForValue().setIfAbsent(key, value, timeout, TimeUnit.MINUTES);
    }
}
