package com.seebon.rpa.service;

import com.seebon.rpa.entity.auth.po.SysMenu;
import com.seebon.rpa.entity.auth.vo.UserVO;

import java.util.List;

public interface UserService {

    int create(String clientId, String username, String password, String name);

    int updatePassword(String username, String password);

    int updateStatus(String username, int status);

    UserVO findUser(String username);

    List<String> findRoles(String username);

    List<String> findAuthorities(String clientId, String username);

    List<SysMenu> getMenu();

    int logout();
}
