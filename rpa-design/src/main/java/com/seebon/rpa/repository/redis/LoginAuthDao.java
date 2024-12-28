package com.seebon.rpa.repository.redis;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Repository;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

@Repository
public class LoginAuthDao extends RedisBase {
    private final static String LOGIN_AUTH_INFO_KEY = "LOGIN_AUTH_INFO_%s_%s";
    /**
     * key
     */
    public void setLoginProcessInfo(String declareSystem, String accountNumber, String orgName,Integer status,String msg){
        String redisKey = String.format(LOGIN_AUTH_INFO_KEY, declareSystem, accountNumber + "-" + orgName);
        HashMap<String , Object> infoMap = Maps.newHashMap();
        infoMap.put("status",status);
        infoMap.put("msg",msg);
        try {
            set(redisKey, JSONObject.toJSONString(infoMap).getBytes("utf8"), 10 * 60L);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public Map<String,Object> getLoginProcessInfo(String declareSystem, String accountNumber, String orgName) {
        String redisKey = String.format(LOGIN_AUTH_INFO_KEY, declareSystem, accountNumber + "-" + orgName);
        String string = "";
        try {
            if (getObject(redisKey) != null) {
                string = new String((byte[])getObject(redisKey),"utf8");
            } else {
                return null;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return JSONObject.parseObject(string);
    }


}
