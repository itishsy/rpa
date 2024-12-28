package com.seebon.rpa.actions.target.impl;

import cn.hutool.http.HttpConfig;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.seebon.rpa.actions.target.AbstractTarget;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.ActionTarget;
import com.seebon.rpa.context.enums.HttpHeader;
import com.seebon.rpa.context.enums.HttpMethod;
import com.seebon.rpa.context.runtime.RobotRuntimeException;
import com.seebon.rpa.utils.Convert;
import com.seebon.rpa.utils.ELParser;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@ActionTarget
public class ResponseTarget extends AbstractTarget<Object> {

    @ActionArgs("URL")
    @Setter
    private String url;

    @ActionArgs(value = "请求方法", required = true, dict = HttpMethod.class)
    @Setter
    private String method;

    @ActionArgs(value = "网站请求头", dict = HttpHeader.class)
    private String header;

    @ActionArgs(value = "网站请求头keys")
    private String headerKeys;

    @ActionArgs(value = "请求参数")
    @Setter
    private Object params;

    @Value("${rpa.token}")
    private String token;

    @Override
    public Object fetchTarget() {
        RequestConfig requestConfig = Convert.getRequestConfig(this.getTimeout());
        SSLConnectionSocketFactory sslSocketFactory = Convert.newSSLSocketFactory();
        try {
            CloseableHttpClient client = HttpClients.custom().setSSLSocketFactory(sslSocketFactory).build();
            switch (HttpMethod.valueOf(method)) {
                case post: {
                    String reqStr = JSON.toJSONString(params, SerializerFeature.WriteMapNullValue);
                    HttpPost httpPost = new HttpPost(url);
                    httpPost.setConfig(requestConfig);
                    httpPost.addHeader("content-type", "application/json; charset=UTF-8");
                    if (StringUtils.isNotBlank(header)) {
                        if (HttpHeader.HeaderAndCookie.equals(HttpHeader.valueOf(header)) || HttpHeader.cookie.equals(HttpHeader.valueOf(header))) {
                            WebDriver driver = ctx.getDriver();
                            if (driver != null) {
                                WebDriver.Options manage = driver.manage();
                                String coStr = "";
                                for (Cookie cookie : manage.getCookies()) {
                                    coStr += cookie.getName() + "=" + cookie.getValue() + ";";
                                }
                                log.info("网站cookie=" + coStr);
                                httpPost.addHeader("Cookie", coStr);
                            }
                        }
                        if (HttpHeader.HeaderAndCookie.equals(HttpHeader.valueOf(header)) || HttpHeader.Header.equals(HttpHeader.valueOf(header))) {
                            for (String key : headerKeys.split(",")) {
                                String value = ctx.get(key);
                                log.info("网站请求头：" + key + "=" + value);
                                if (StringUtils.isBlank(value)) {
                                    throw new RuntimeException("网站请求头参数为空，拦截配置错误，请检查。");
                                }
                                httpPost.addHeader(key, value);
                            }
                        }
                    } else {
                        httpPost.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
                    }
                    if (StringUtils.isNotEmpty(reqStr)) {
                        if (params instanceof Map) {
                            Map<String, Object> ps = ELParser.parse(reqStr, ctx.getVariables(), LinkedHashMap.class);
                            reqStr = JSON.toJSONString(ps);
                        }
                        StringEntity se = new StringEntity(reqStr, "UTF-8");
                        se.setContentEncoding(new BasicHeader("Content-Type", "application/json; charset=UTF-8"));
                        httpPost.setEntity(se);
                    }
                    return client.execute(httpPost);
                }
                case postHttps: {
                    HttpConfig httpConfig = Convert.getHttpConfig(this.getTimeout());
                    HttpRequest httpRequest = HttpUtil.createPost(url);
                    httpRequest.header("Content-type", "application/json; charset=UTF-8");
                    httpRequest.setConfig(httpConfig);
                    if (StringUtils.isNotBlank(header)) {
                        if (HttpHeader.HeaderAndCookie.equals(HttpHeader.valueOf(header)) || HttpHeader.cookie.equals(HttpHeader.valueOf(header))) {
                            WebDriver driver = ctx.getDriver();
                            if (driver != null) {
                                WebDriver.Options manage = driver.manage();
                                String coStr = "";
                                for (Cookie cookie : manage.getCookies()) {
                                    coStr += cookie.getName() + "=" + cookie.getValue() + ";";
                                }
                                log.info("网站cookie=" + coStr);
                                httpRequest.cookie(coStr);
                            }
                        }
                        if (HttpHeader.HeaderAndCookie.equals(HttpHeader.valueOf(header)) || HttpHeader.Header.equals(HttpHeader.valueOf(header))) {
                            for (String key : headerKeys.split(",")) {
                                String value = ctx.get(key);
                                log.info("网站请求头：" + key + "=" + value);
                                if (StringUtils.isBlank(value)) {
                                    throw new RuntimeException("网站请求头参数为空，拦截配置错误，请检查。");
                                }
                                httpRequest.header(key, value);
                            }
                        }
                    } else {
                        httpRequest.header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
                    }
                    String reqStr = JSON.toJSONString(params);
                    if (StringUtils.isNotBlank(reqStr)) {
                        if (params instanceof Map) {
                            Map<String, Object> ps = ELParser.parse(reqStr, ctx.getVariables(), LinkedHashMap.class);
                            reqStr = JSON.toJSONString(ps);
                        }
                        httpRequest.body(reqStr);
                    }
                    return httpRequest.execute().body();
                }
                case postDeclare: {
                    String object = JSON.toJSONString(params);
                    HttpPost httpPost = new HttpPost(url);
                    httpPost.setConfig(requestConfig);
                    httpPost.addHeader("content-type", "application/json; charset=UTF-8");
                    httpPost.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
                    if (StringUtils.isNotBlank(header)) {
                        if (HttpHeader.cookie.equals(HttpHeader.valueOf(header))) {
                            //设置 cookies
                            WebDriver driver = ctx.getDriver();
                            if (driver != null) {
                                WebDriver.Options manage = driver.manage();
                                String coStr = "";
                                for (Cookie cookie : manage.getCookies()) {
                                    coStr += cookie.getName() + "=" + cookie.getValue() + ";";
                                }
                                httpPost.addHeader("Cookie", coStr);
                            }
                        }
                    }
                    if (StringUtils.isNotEmpty(object)) {
                        String seStr = object;
                        if (params instanceof Map) {
                            Map<String, Object> ps = ELParser.parse(object, ctx.getVariables(), Map.class);
                            seStr = JSON.toJSONString(ps);
                        }
                        StringEntity se = new StringEntity(seStr, "UTF-8");
                        se.setContentEncoding(new BasicHeader("Content-Type", "application/json; charset=UTF-8"));
                        httpPost.setEntity(se);
                    }
                    return client.execute(httpPost);
                }
                case postFile: {
                    HttpPost httpPost = new HttpPost(url);
                    httpPost.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
                    MultipartEntityBuilder builder = MultipartEntityBuilder.create().setMode(HttpMultipartMode.RFC6532);
                    Map<String, Object> objectMap = null;
                    if (params instanceof String) {
                        objectMap = JSONObject.parseObject(((String) params).replaceAll("\\\\", "/"));
                    } else {
                        objectMap = (Map) params;
                    }

                    for (String key : objectMap.keySet()) {
                        Object value = objectMap.get(key);
                        if ("file".equals(key)) {
                            File file = new File((String) value);
                            builder.addBinaryBody(key, file, ContentType.MULTIPART_FORM_DATA, file.getName());
                        } else if (value instanceof Integer) {
                            builder.addTextBody(key, value + "");
                        } else {
                            value = ELParser.parse((String) value, ctx.getVariables(), String.class);
                            builder.addTextBody(key, (String) value);
                        }
                    }
                    HttpEntity entity = builder.build();
                    httpPost.setEntity(entity);
                    return client.execute(httpPost);
                }
                case getBrowser: {
                    WebDriver driver = ctx.getWebDriver();
                    driver.get(url);
                    WebElement webElement = driver.findElement(By.tagName("body"));
                    return webElement.getText();
                }
                case empFile: {
                    Map<String, Object> paramsMap = (Map<String, Object>) params;
                    List<Map<String, Object>> empList = (List<Map<String, Object>>) paramsMap.get("dataSet");
                    String idCardKey = (String) paramsMap.get("idCardKey");
                    String fileType = (String) paramsMap.get("fileType");
                    String addrName = (String) paramsMap.get("addrName");
                    String fileKey = (String) paramsMap.get("fileKey");
                    String fileFolder = (String) paramsMap.get("fileFolder");
                    String fileSuffix = (String) paramsMap.get("fileSuffix");
                    List<String> idCards = empList.stream().map(it -> (String) it.get(idCardKey)).collect(Collectors.toList());

                    Map<String, Object> httpParams = Maps.newHashMap();
                    httpParams.put("addrName", addrName);
                    httpParams.put("fileType", fileType);
                    httpParams.put("idCards", idCards);

                    String object = JSON.toJSONString(httpParams);
                    HttpPost httpPost = new HttpPost(url);
                    httpPost.addHeader("Content-Type", "application/json");
                    httpPost.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
                    if (StringUtils.isNotEmpty(object)) {
                        StringEntity se = new StringEntity(object, "UTF-8");
                        se.setContentEncoding(new BasicHeader("Content-Type", "application/json"));
                        httpPost.setEntity(se);
                    }
                    CloseableHttpResponse response = client.execute(httpPost);
                    HttpEntity entity = ((CloseableHttpResponse) response).getEntity();
                    String resStr = EntityUtils.toString(entity, "utf-8");
                    Map<String, Object> map = JSONObject.parseObject(resStr, Map.class);

                    Map<String, Object> data = (Map<String, Object>) map.get("data");
                    if (!data.isEmpty()) {
                        Integer index = 0;
                        for (Map.Entry<String, Object> entry : data.entrySet()) {
                            List<String> fileUrls = saveLocalAfterDownloadFile((List<String>) entry.getValue(), fileFolder, index, fileSuffix);
                            empList.stream().filter(item -> ((String) item.get(idCardKey)).equals(entry.getKey())).forEach(item -> {
                                item.put(fileKey, fileUrls);
                            });
                            index++;
                        }
                    }
                    StringEntity strEntity = new StringEntity(JSONObject.toJSONString(empList), ContentType.APPLICATION_JSON);
                    response.setEntity(strEntity);
                    return response;
                }
                case formPost:
                    String reqStr = JSON.toJSONString(params, SerializerFeature.WriteMapNullValue);
                    HttpPost httpPost = new HttpPost(url);
                    httpPost.setConfig(requestConfig);
                    if (StringUtils.isNotBlank(header)) {
                        if (HttpHeader.HeaderAndCookie.equals(HttpHeader.valueOf(header)) || HttpHeader.cookie.equals(HttpHeader.valueOf(header))) {
                            WebDriver driver = ctx.getDriver();
                            if (driver != null) {
                                WebDriver.Options manage = driver.manage();
                                String coStr = "";
                                for (Cookie cookie : manage.getCookies()) {
                                    coStr += cookie.getName() + "=" + cookie.getValue() + ";";
                                }
                                log.info("网站cookie=" + coStr);
                                httpPost.addHeader("Cookie", coStr);
                            }
                        }
                        if (HttpHeader.HeaderAndCookie.equals(HttpHeader.valueOf(header)) || HttpHeader.Header.equals(HttpHeader.valueOf(header))) {
                            for (String key : headerKeys.split(",")) {
                                String value = ctx.get(key);
                                log.info("网站请求头：" + key + "=" + value);
                                if (StringUtils.isBlank(value)) {
                                    throw new RuntimeException("网站请求头参数为空，拦截配置错误，请检查。");
                                }
                                httpPost.addHeader(key, value);
                            }
                        }
                    } else {
                        httpPost.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
                    }
                    if (StringUtils.isNotEmpty(reqStr)) {
                        if (params instanceof Map) {
                            Map<String, Object> ps = ELParser.parse(reqStr, ctx.getVariables(), LinkedHashMap.class);
                            reqStr = JSON.toJSONString(ps);
                        }

                        Map<String, Object> paramsMap = JSONObject.parseObject(reqStr, Map.class);
                        List<NameValuePair> formParams = paramsMap.entrySet().stream().map(item -> new BasicNameValuePair(item.getKey(), String.valueOf(item.getValue())))
                                .collect(Collectors.toList());
                        httpPost.setEntity(new UrlEncodedFormEntity(formParams, "UTF-8"));
                    }
                    return client.execute(httpPost);

                case get:
                default:
                    HttpGet httpGet = new HttpGet(url);
                    httpGet.addHeader("Content-Type", "application/json");
                    if (StringUtils.isNotBlank(header)) {
                        if (HttpHeader.HeaderAndCookie.equals(HttpHeader.valueOf(header)) || HttpHeader.cookie.equals(HttpHeader.valueOf(header))) {
                            WebDriver driver = ctx.getDriver();
                            if (driver != null) {
                                WebDriver.Options manage = driver.manage();
                                String coStr = "";
                                for (Cookie cookie : manage.getCookies()) {
                                    coStr += cookie.getName() + "=" + cookie.getValue() + ";";
                                }
                                log.info("网站cookie=" + coStr);
                                httpGet.addHeader("Cookie", coStr);
                            }
                        }
                        if (HttpHeader.HeaderAndCookie.equals(HttpHeader.valueOf(header)) || HttpHeader.Header.equals(HttpHeader.valueOf(header))) {
                            for (String key : headerKeys.split(",")) {
                                String value = ctx.get(key);
                                log.info("网站请求头：" + key + "=" + value);
                                if (StringUtils.isBlank(value)) {
                                    throw new RuntimeException("网站请求头参数为空，拦截配置错误，请检查。");
                                }
                                httpGet.addHeader(key, value);
                            }
                        }
                    } else {
                        httpGet.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
                    }
                    httpGet.setConfig(requestConfig);
                    return client.execute(httpGet);
            }
        } catch (Exception e) {
            log.error("【Exception】", e);
            throw new RobotRuntimeException("HTTP请求失败");
        }
    }

    private List<String> saveLocalAfterDownloadFile(List<String> fileUrls, String fileFolder, Integer index, String fileSuffix) {
        List<String> files = Lists.newArrayList();
        int i = 0;
        for (String urlStr : fileUrls) {
            FileOutputStream fos = null;
            String filePath = "";
            try {
                if (StringUtils.isBlank(fileSuffix)) {
                    fileSuffix = urlStr.substring(urlStr.lastIndexOf(".") + 1, urlStr.length());
                }
                filePath = ELParser.parse(fileFolder, ctx.getVariables(), String.class) + "_file_" + index + "_" + i + "." + fileSuffix;
                filePath = filePath.replace("\\\\", "\\");
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(3000);
                conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

                InputStream inputStream = conn.getInputStream();
                byte[] buffer = new byte[1024];
                int rLen = 0;
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                while ((rLen = inputStream.read(buffer)) != -1) {
                    bos.write(buffer, 0, rLen);
                }
                bos.close();
                byte[] bytes = bos.toByteArray();

                InputStream in = new ByteArrayInputStream(bytes);
                File file = new File(filePath);
                fos = new FileOutputStream(file);
                int len = 0;
                byte[] buf = new byte[1024];

                while ((len = in.read(buf)) != -1) {
                    fos.write(buf, 0, len);
                }
                inputStream.close();
                fos.flush();
                files.add(filePath);
            } catch (Exception e) {
                return Lists.newArrayList();
            } finally {
                i++;
                if (null != fos) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return files;
    }

    private int getTimeout() {
        if (ctx.getAction() == null) {
            return 30;
        }
        return ctx.getAction().getTimeout();
    }

    private void log() {

    }
}
