package com.seebon.rpa.common.security.server;

import com.seebon.rpa.entity.auth.vo.UserVO;
import com.seebon.rpa.repository.mysql.SysUserDao;
import com.seebon.rpa.repository.redis.UserSessionDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class JwtTokenEnhancer implements TokenEnhancer {

    /*@Autowired
    private ResourceService resourceService;
*/
    @Autowired
    private SysUserDao userDao;

    @Autowired
    private UserSessionDao userSessionDao;

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {

        Map<String, Object> additionalInfo = new LinkedHashMap(accessToken.getAdditionalInformation());
        String tokenId = accessToken.getValue();
        User user = (User) authentication.getUserAuthentication().getPrincipal();
        String username = user.getUsername();
        List<String> authorities = new ArrayList<>();
        for (GrantedAuthority authority : user.getAuthorities()) {
            authorities.add(authority.getAuthority());
        }
        additionalInfo.put("authorities", authorities);
        UserVO userVO = userDao.findUser(username);
        additionalInfo.put("userId", userVO.getId());
        additionalInfo.put("name", userVO.getName());
        additionalInfo.put("custId", userVO.getCustId());
        additionalInfo.put("custName", userVO.getCustName());
        additionalInfo.put("userType", userVO.getUserType());
        additionalInfo.put("mainUser", userVO.getMainUser());
        additionalInfo.put("jti", tokenId);
        Map<String, String> details = (Map<String, String>) authentication.getUserAuthentication().getDetails();
        String clientId = details.get("client_id");
        if (clientId.equals("rpa_console")) {
            //客户端的超时时间为token时间
            userSessionDao.create(username, tokenId, additionalInfo,accessToken.getExpiresIn());
            userSessionDao.pushInstaller(username);
        } else {
            Integer expiresIn = userVO.getExpiresIn();
            if (expiresIn != null || (userVO.getUserType()==2 && userVO.getUserCategory() != 2)) {
                if (expiresIn == null) {
                    expiresIn = 2; // 客户用户默认2小时session维持
                }
                userSessionDao.create(username, tokenId, additionalInfo, expiresIn * 60 * 60);
                //((DefaultOAuth2AccessToken) accessToken).setExpiration(new Date(System.currentTimeMillis() + expiresIn * 60 * 60 * 1000));
            } else {
                //SaaS的超时时间为Session时间
                userSessionDao.create(username, tokenId, additionalInfo);
            }

        }
        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);

        return accessToken;
    }

/*

    public OAuth2AccessToken enhance1(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        try {
            Map<String, Object> additionalInfo = accessToken.getAdditionalInformation();
            String username = null;
            if (null == authentication.getUserAuthentication()) {
                //client模式
                Collection<? extends GrantedAuthority> authorities = authentication.getOAuth2Request().getAuthorities();
                List<SysResource> resources = new ArrayList<>();
                for (GrantedAuthority authority : authorities) {
                    resources.addAll(resourceService.findByRole(authority.getAuthority()));
                }
                additionalInfo.put("resources", resources);
            } else {
                //password模式
                User user = (User) authentication.getUserAuthentication().getPrincipal();
                username = user.getUsername();
                List<String> authorities = new ArrayList<>();
                for (GrantedAuthority authority : user.getAuthorities()) {
                    authorities.add(authority.getAuthority());
                }
                //userId 、custId 添加到additionalInfo
                User sysUser = userDao.findUser(username);
                additionalInfo.put("authorities", authorities);
                additionalInfo.put("userId", sysUser.getId());
                additionalInfo.put("name", sysUser.getName());
                additionalInfo.put("custId", sysUser.getCustId());
            }
//            ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
            //添加用户session
            userSessionDao.create(username, accessToken.getValue(), accessToken.getAdditionalInformation());
            return accessToken;
        } catch (Exception e) {
            return accessToken;
        }
    }
*/

}
