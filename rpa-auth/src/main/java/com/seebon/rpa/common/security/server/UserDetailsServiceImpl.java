package com.seebon.rpa.common.security.server;

import com.seebon.rpa.entity.auth.vo.UserVO;
import com.seebon.rpa.repository.mysql.SysRoleDao;
import com.seebon.rpa.repository.mysql.SysUserDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("userDetailsService")
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private SysUserDao userDao;

    @Autowired
    private SysRoleDao roleDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("userDetailsService.loadUserByUsername: " + username);
        UserVO user = userDao.findUser(username);
        if(null == user){
            throw new RuntimeException("用户不存在或被禁用！");
//            throw new UsernameNotFoundException("用户不存在或被禁用！");
        }
        List<String> roles = roleDao.findCodeByUser(username);
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for(String roleCode : roles){
            authorities.add(new SimpleGrantedAuthority(roleCode));
        }
        return User.withUsername(username)
                .password(user.getPassword())
                .authorities(authorities)
                .build();
    }
}
