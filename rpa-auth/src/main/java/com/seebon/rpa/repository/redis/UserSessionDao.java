package com.seebon.rpa.repository.redis;

import com.seebon.rpa.entity.auth.po.SysMenu;
import com.seebon.rpa.entity.auth.po.SysResource;
import com.seebon.rpa.repository.mysql.SysMenuDao;
import com.seebon.rpa.repository.mysql.SysResourceDao;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
public class UserSessionDao extends RedisBase {
    @Autowired
    private SysMenuDao sysMenuDao;

    @Autowired
    private SysResourceDao sysResourceDao;

    private static final int SESSION_TIMEOUT = 30 * 24 * 60 * 60;
    private static final String USER_SESSION = "session:%s:%s:token";
    private static final String USER_MENU = "session:%s:%s:menu";
    private static final String USER_RESOURCE = "session:%s:%s:resource";
    private static final String USER_EXPIRESIN = "session:%s:%s:expiresIn";
    private static final String USER_INSTALLER = "installer";

    public void create(String username, String sessionId, Map<String, Object> details) {
        create(username,sessionId,details,SESSION_TIMEOUT);
    }

    public void create(String username, String sessionId, Map<String, Object> details, int timeout) {
        String expiresInKey = String.format(USER_EXPIRESIN, username, sessionId);
        set(expiresInKey, (long)timeout, timeout);

        String key = String.format(USER_SESSION, username, sessionId);
        set(key, details, timeout);
        //登录用户的menu和resources权限
        String menuKey = String.format(USER_MENU, username, sessionId);
        long userId = (long) details.get("userId");
        set(menuKey,sysMenuDao.getMenu(userId), timeout);
        String resourceKey = String.format(USER_RESOURCE, username, sessionId);
        List<String> roles = (List<String>) details.get("authorities");
        List<SysResource> resources=new ArrayList<>();
        for (String role : roles) {
            resources.addAll(sysResourceDao.findByRole(role));
        }
        set(resourceKey,resources, timeout);
    }

    public void pushInstaller(String token){
        queuePush(USER_INSTALLER,token);
    }
    public String popInstaller(){
        return queuePop(USER_INSTALLER);
    }

    public void refresh(String username, String sessionId){
        long timeOut = SESSION_TIMEOUT;
        String expiresInKey = String.format(USER_EXPIRESIN, username, sessionId);
        if(hasKey(expiresInKey)) {
            timeOut = (Long)getObject(expiresInKey);
            expire(expiresInKey, timeOut);
        } else {
            set(expiresInKey, timeOut, timeOut);
        }

        String key = String.format(USER_SESSION, username, sessionId);
        if(hasKey(key)) {
            expire(key, timeOut);
        }
        String menuKey = String.format(USER_MENU, username, sessionId);
        if(hasKey(menuKey)) {
            expire(menuKey, timeOut);
        }
        String resourceKey = String.format(USER_RESOURCE, username, sessionId);
        if(hasKey(resourceKey)) {
            expire(resourceKey, timeOut);
        }
    }

    public void refresh(String username, String sessionId, long timeOut){
        String expiresInKey = String.format(USER_EXPIRESIN, username, sessionId);
        set(expiresInKey, timeOut, timeOut);

        String key = String.format(USER_SESSION, username, sessionId);
        if(hasKey(key)) {
            expire(key, timeOut);
        }
        String menuKey = String.format(USER_MENU, username, sessionId);
        if(hasKey(menuKey)) {
            expire(menuKey, timeOut);
        }
        String resourceKey = String.format(USER_RESOURCE, username, sessionId);
        if(hasKey(resourceKey)) {
            expire(resourceKey, timeOut);
        }
    }

    public void remove(String username,String sessionId){
        String expiresInKey = String.format(USER_EXPIRESIN, username, sessionId);
        if(hasKey(expiresInKey)) {
            remove(expiresInKey);
        }
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


    public boolean exists(String username, String sessionId){
        String key = String.format(USER_SESSION, username, sessionId);
        return hasKey(key);
    }


    public List<SysMenu> findMenus(String username, String sessionId){
        String menuKey = String.format(USER_MENU, username, sessionId);
        List<SysMenu> menus = (List<SysMenu>)getObject(menuKey);
        return menus;
    }

    public List<SysResource> findResources(String username, String sessionId){
        String resourceKey = String.format(USER_RESOURCE, username, sessionId);
        List<SysResource> resources = (List<SysResource>)getObject(resourceKey);
        return resources;
    }

    public void removeUser(String username) {
        Set<String> listKey = getListKey(String.format("session:%s", username));
        if (CollectionUtils.isNotEmpty(listKey)) {
            listKey.stream().forEach(key -> {
                remove(key);
            });
        }

    }
}
