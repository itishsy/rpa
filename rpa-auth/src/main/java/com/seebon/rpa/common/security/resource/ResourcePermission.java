package com.seebon.rpa.common.security.resource;

import com.alibaba.nacos.client.utils.JSONUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.seebon.rpa.auth.SecurityContext;
import com.seebon.rpa.entity.auth.po.SysResource;
import com.seebon.rpa.auth.Session;
import com.seebon.rpa.repository.redis.UserSessionDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Slf4j
@Service
public class ResourcePermission {

    @Value("${server.servlet.context-path:}")
    private String cxt;

    @Autowired
    private IgnoreProperties ignoreProperties;

    @Autowired
    private UserSessionDao userSessionDao;


    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    public boolean hasPermission(HttpServletRequest request, Authentication authentication) {

        String requestUrl = request.getRequestURI();
        for (String au : ignoreProperties.getUrls()) {
            if (antPathMatcher.match((cxt + au), requestUrl)) {
                return true;
            }
        }

        if (authentication instanceof OAuth2Authentication) {
            try {
                Session session = SecurityContext.currentUser();
                if (!userSessionDao.exists(session.getUsername(), session.getSessionId())) {
                    return false;
                }

                List<SysResource> resources = userSessionDao.findResources(session.getUsername(), session.getSessionId());
                if (!CollectionUtils.isEmpty(resources)) {
                    for (int i=0; i<resources.size(); i++) {
                        String s = JSONUtils.serializeObject(resources.get(i));
                        SysResource resource = JSONUtils.deserializeObject(s, new TypeReference<SysResource>() {
                        });
                        if (antPathMatcher.match(resource.getUriPattern(), requestUrl)) {
                            userSessionDao.refresh(session.getUsername(), session.getSessionId());
                            return true;
                        }
                    }
                } else {
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

            /*try {
                OAuth2Authentication auth2Authentication = (OAuth2Authentication) authentication;
                Collection<GrantedAuthority> authorities = auth2Authentication.getAuthorities();
                if(authorities == null || authorities.size() ==0){
                    return false;
                }
                for (GrantedAuthority authority : authorities) {
                    List<SysResource> resources = resourceDao.findByRole(authority.getAuthority());
                    for(SysResource resource : resources) {
                        if (antPathMatcher.match(resource.getUriPattern(), requestUrl)) {
                            String session = SecurityContext.sessionId();
                            userSessionDao.refresh(auth2Authentication.getPrincipal()+"",session);
                            return true;
                        }
                    }
                }
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }*/
        }
        return false;
    }
}
