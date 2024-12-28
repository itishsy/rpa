package com.seebon.rpa.service.impl;

import com.seebon.rpa.auth.SecurityContext;
import com.seebon.rpa.auth.Session;
import com.seebon.rpa.entity.auth.po.SysMenu;
import com.seebon.rpa.entity.auth.vo.UserVO;
import com.seebon.rpa.repository.mysql.SysRoleDao;
import com.seebon.rpa.repository.mysql.SysUserDao;
import com.seebon.rpa.repository.redis.UserSessionDao;
import com.seebon.rpa.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    private SysUserDao userDao;
    @Autowired
    private UserSessionDao userSessionDao;
    @Autowired
    private SysRoleDao roleDao;

    @Override
    public int create(String clientId, String username, String password, String name) {
        return 0;
    }

    @Override
    public int updatePassword(String username, String password) {
        return 0;
    }

    @Override
    public int updateStatus(String username, int status) {
        return 0;
    }

    @Override
    public UserVO findUser(String username) {
        UserVO user = userDao.findUser(username);
        user.setRoles(this.findRoles(username));
        return user;
    }

    @Override
    public List<String> findRoles(String username) {
        return roleDao.findCodeByUser(username);
    }

    @Override
    public List<String> findAuthorities(String clientId, String username) {
        return null;
    }

    @Override
    public List<SysMenu> getMenu() {
        Session session = SecurityContext.currentUser();
        return userSessionDao.findMenus(session.getUsername(), session.getSessionId());
    }

    @Override
    public int logout() {
        try {
            // JWT无状态，失效的唯一途径就是等过期。logout只能清理Session
            Session user = SecurityContext.currentUser();
            userSessionDao.remove(user.getUsername(), user.getSessionId());
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
