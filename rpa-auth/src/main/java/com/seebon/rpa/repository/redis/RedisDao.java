package com.seebon.rpa.repository.redis;

import org.springframework.stereotype.Repository;

@Repository
public class RedisDao extends RedisBase {

    public String getStringValue(String key){
        return getString(key);
    }

}
