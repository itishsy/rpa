package com.seebon.rpa;

import cn.hutool.http.HttpConfig;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.seebon.rpa.context.enums.HttpHeader;
import com.seebon.rpa.utils.Convert;
import com.seebon.rpa.utils.ELParser;
import com.seebon.rpa.utils.HttpUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.springframework.http.HttpHeaders;

import java.util.LinkedHashMap;
import java.util.Map;

public class Test {
    private static final String url = "https://sbwx.rst.shanxi.gov.cn:8007/shiyeonline/reports/crud";

    public static void main(String[] args) {
        quertList();
    }

    public static void quertList() {
        String reqJson = "{\"serviceid\":\"businessSpRecordList\",\"target\":\"\",\"sessionid\":\"A1FA5A0280ED9A8B17CF74CCBEC9C931\",\"loginname\":null,\"password\":null,\"params\":{\"aaz010\":\"\",\"target_name\":\"\",\"aaa121\":\"\",\"sbrq\":\"\",\"typ\":\"1\",\"pagesize\":10,\"page\":1},\"datas\":[{\"aaz010\":\"\",\"target_name\":\"\",\"aaa121\":\"\",\"sbrq\":\"\",\"typ\":\"1\",\"pagesize\":10,\"page\":1}]}";

        Map<String, String> headers = Maps.newHashMap();
        headers.put("Cookie", "A1FA5A0280ED9A8B17CF74CCBEC9C931");
        headers.put("Content-Type", "multipart/form-data");
        String json = HttpUtils.post(url, reqJson, headers);
        System.out.println("json=" + json);

        HttpConfig httpConfig = Convert.getHttpConfig(3000);
        HttpRequest httpRequest = HttpUtil.createPost(url);
        httpRequest.header("Content-type", "multipart/form-data");
        httpRequest.setConfig(httpConfig);
        httpRequest.cookie("JSESSIONID=A1FA5A0280ED9A8B17CF74CCBEC9C931");
        httpRequest.header("Content-Type", "multipart/form-data");
        httpRequest.body(reqJson);
        String body = httpRequest.execute().body();
        System.out.println("body=" + body);
    }
}
