package com.seebon.rpa.repository.redis;

import com.google.common.collect.Lists;
import com.seebon.rpa.entity.auth.SysResource;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
public class UserSessionDao extends RedisBase {

    private static final String USER_LOGIN_SESSION = "session:%s:%s";
    private static final int SESSION_TIMEOUT = 30 * 24 * 60 * 60;
    private static final String USER_SESSION = "session:%s:%s:token";
    private static final String USER_MENU = "session:%s:%s:menu";
    private static final String USER_RESOURCE = "session:%s:%s:resource";
    private static final String USER_INSTALLER = "installer";

    public Map<String, Object> getDetails(String username, String jti) {
        String key = String.format(USER_LOGIN_SESSION, username, jti);
        Map<String, Object> details = (Map<String, Object>)getObject(key);
        return details;
    }

    public void removeUser(String username) {

        Set<String> listKey = getListKey(String.format("session:%s", username));
        if (CollectionUtils.isNotEmpty(listKey)) {
            listKey.stream().forEach(key -> {
                remove(key);
            });
        }

    }

    public void remove(String username,String sessionId){
        String key = String.format(USER_SESSION, username, sessionId);
        if(hasKey(key)) {
            remove(key);
        }
        String menuKey = String.format(USER_MENU, username, sessionId);
        if(hasKey(menuKey)) {
            remove(menuKey);
        }
        String resourceKey = String.format(USER_RESOURCE, username, sessionId);
        if(hasKey(resourceKey)) {
            remove(resourceKey);
        }
    }

    public void create(String username, String sessionId, Map<String, Object> details) {
        create(username,sessionId,details,SESSION_TIMEOUT);
    }

    public void create(String username, String sessionId, Map<String, Object> details, int timeout) {
        String key = String.format(USER_SESSION, username, sessionId);
        set(key, details, timeout);
        //登录用户的menu和resources权限
        String menuKey = String.format(USER_MENU, username, sessionId);
        set(menuKey, Lists.newArrayList(), timeout);
        String resourceKey = String.format(USER_RESOURCE, username, sessionId);
        List<SysResource> resources=new ArrayList<>();
        SysResource sr = new SysResource();
        sr.setId(1);
        sr.setUriPattern("/**");
        resources.add(sr);
        set(resourceKey,resources, timeout);
    }
}
