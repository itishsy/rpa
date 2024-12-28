package com.seebon.rpa.common.security.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 处理权限不足
 */
public class AccessDenied implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException)
            throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("code", 401);
        map.put("message", "权限不足");
        map.put("data", accessDeniedException.getMessage());
        ObjectMapper mapper = new ObjectMapper();
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(mapper.writeValueAsString(map));
    }
}
