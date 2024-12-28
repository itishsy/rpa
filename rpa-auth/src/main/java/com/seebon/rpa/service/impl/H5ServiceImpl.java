package com.seebon.rpa.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.seebon.rpa.repository.redis.RedisDao;
import com.seebon.rpa.service.H5Service;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@Slf4j
@Service
public class H5ServiceImpl implements H5Service {

    @Value("${h5.page.url:http://192.168.0.68:8899}")
    private String h5PageUrl;

    @Autowired
    private RedisDao redisDao;

    @Override
    public void toH5Page(HttpServletResponse response, String shortUrl) {
        String pageParams = redisDao.getStringValue(shortUrl);

        if (StringUtils.isBlank(pageParams)) {
            Map<String,Object> resultMap = Maps.newHashMap();
            resultMap.put("code", 404);
            resultMap.put("message", "链接已失效或不存在");
            resultMap.put("data", null);
            try {
                response.setCharacterEncoding("UTF-8");
                response.setContentType("application/json;charset=UTF-8");
                PrintWriter writer = response.getWriter();
                writer.write(new ObjectMapper().writeValueAsString(resultMap));
                writer.flush();
                writer.close();
            }catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                String h5Url = h5PageUrl.concat("?").concat(pageParams);
                response.sendRedirect(h5Url);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void toH5AuthPage(HttpServletResponse response, String code, String state) {
        try {
            String h5Url = h5PageUrl.concat(String.format("/collectPhone.html?code=%s&state=", code, state));
            response.sendRedirect(h5Url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
