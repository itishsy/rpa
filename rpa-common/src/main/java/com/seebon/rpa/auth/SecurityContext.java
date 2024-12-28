package com.seebon.rpa.auth;

import com.seebon.rpa.SpringBeanHolder;
import com.seebon.rpa.entity.system.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

import java.util.List;
import java.util.Map;

public class SecurityContext {

    public static Session currentUser() {
        String token = currentToken();
        JwtTokenConverter tokenConverter = SpringBeanHolder.getBean(JwtTokenConverter.class);
        Map<String, Object> map = tokenConverter.decode(token);
        Integer userType = null;
        Integer mainUser = null;
        if (map.containsKey("userType") && map.get("userType")!=null) {
            userType = Integer.parseInt(map.get("userType")+"");
        }
        if (map.containsKey("mainUser") && map.get("mainUser")!=null) {
            mainUser = Integer.parseInt(map.get("mainUser")+"");
        }
        Integer custId = null;
        Object custId1 = map.get("custId");
        if (custId1 != null) {
            custId = Integer.parseInt(custId1 + "");
        }
        User user = new User(Long.parseLong(map.get("userId") + ""),
                map.get("user_name") + "",
                map.get("name") + "",
                custId,
                map.get("custName")+"",
                (List<String>) map.get("authorities"),
                userType,
                mainUser);
        return new Session(map.get("jti") + "", user);
    }


    public static Long currentUserId() {
        String token = currentToken();
        JwtTokenConverter tokenConverter = SpringBeanHolder.getBean(JwtTokenConverter.class);
        Map<String, Object> map = tokenConverter.decode(token);
        return Long.parseLong(map.get("userId") + "");
    }

    public static String currentToken(){
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication instanceof OAuth2Authentication) {
                OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) authentication.getDetails();
                return details.getTokenValue();
            }
        } catch (Exception e) {
            throw new RuntimeException("获取用户异常", e);
        }
        throw new RuntimeException("获取认证异常");
    }
}
