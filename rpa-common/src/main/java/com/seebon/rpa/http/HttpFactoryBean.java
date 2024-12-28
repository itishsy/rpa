package com.seebon.rpa.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.google.common.reflect.AbstractInvocationHandler;
import com.google.common.reflect.Reflection;
import com.seebon.rpa.SpringBeanHolder;
import com.seebon.rpa.response.ResponseResult;
import com.seebon.rpa.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;

/**
 * 处理http请求FactoryBean
 *
 * @author huangshouyi
 * @since 2022-06-01
 */
@Slf4j
public class HttpFactoryBean implements FactoryBean<Object> {

    private Class<?> objectType;
    private String baseUrl;

    @Override
    public Object getObject() throws Exception {
        return Reflection.newProxy(objectType, new AbstractInvocationHandler() {
            @Override
            protected Object handleInvocation(Object proxy, Method method, Object[] args) throws Throwable {
                Annotation[] annotations = method.getDeclaredAnnotations();
                Boolean isPost = true;
                String url = baseUrl;
                Class<?> clazz = method.getReturnType();
                for (Annotation annotation : annotations) {
                    if (annotation instanceof PostMapping) {
                        PostMapping postMapping = (PostMapping) annotation;
                        url += postMapping.value()[0];
                        break;
                    }
                    if (annotation instanceof GetMapping) {
                        GetMapping getMapping = (GetMapping) annotation;
                        isPost = false;
                        url += getMapping.value()[0];
                        break;
                    }
                    if (annotation instanceof RequestMapping) {
                        RequestMapping requestMapping = (RequestMapping) annotation;
                        RequestMethod[] methods = requestMapping.method();
                        if (methods.length > 0) {
                            isPost = methods[0].equals(RequestMethod.POST);
                        }
                        url += requestMapping.value()[0];
                        break;
                    }
                }

                try {
                    Object objParams = null;
                    String isReqBody = "false";
                    if (args.length > 0) {
                        Map<String, Object> params = new HashMap<>();
                        Parameter[] parameters = method.getParameters();
                        for (int index=0; index<parameters.length; index++) {
                            if (null != parameters[index].getAnnotation(RequestBody.class)) {
                                isReqBody = "true";
                                break;
                            }
                        }
                        if ("true".equals(isReqBody)) {
                            for (int index=0; index<parameters.length; index++) {
                                if (null != parameters[index].getAnnotation(RequestBody.class)) {
                                    objParams = args[index];
                                } else {
                                    String key = parameters[index].getName();
                                    Object val = args[index];
                                    PathVariable pathVariable = parameters[index].getDeclaredAnnotation(PathVariable.class);
                                    if (pathVariable != null) {
                                        url = url.replace("{" + pathVariable.value() + "}", val.toString());
                                    }
                                }
                            }
                        } else {
                            for (int i = 0; i < args.length; i++) {
                                String key = parameters[i].getName();
                                Object val = args[i];
                                PathVariable pathVariable = parameters[i].getDeclaredAnnotation(PathVariable.class);
                                if (pathVariable != null) {
                                    url = url.replace("{" + pathVariable.value() + "}", val.toString());
                                } else {
                                    params.put(key, val);
                                }
                            }
                            if (params.size() > 0) {
                                objParams = params;
                            }
                        }
                    }

                    Map<String, String> headers = null;
                    try {
                        HttpRequestConfig requestConfig = SpringBeanHolder.getBean(HttpRequestConfig.class);
                        headers = requestConfig.getHeaders();
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    if (headers == null) {
                        headers = Maps.newHashMap();
                    }
                    headers.put("isReqBody", isReqBody);
                    String responseBody = isPost ? HttpUtils.post(url, objParams, headers) : HttpUtils.get(url, objParams, headers);
                    if (url.endsWith("oauth/token")) {
                        return JSON.parseObject(responseBody, clazz);
                    }

                    ResponseResult result = JSON.parseObject(responseBody, ResponseResult.class);
                    if (result.getCode() == ResponseResult.SUCCESS) {
                        Object object = result.getData();
                        if (object instanceof JSONArray) {
                            ParameterizedType type = (ParameterizedType) method.getAnnotatedReturnType().getType();
                            clazz = (Class<?>) type.getActualTypeArguments()[0];
                            return JSONObject.parseArray(JSON.toJSONString(object), clazz);
                        }
                        if (clazz.isPrimitive() || clazz.getName().startsWith("java.lang")) {
                            return object;
                        } else {
                            return JSON.toJavaObject((JSON) object, clazz);
                        }
                    } else {
                        Map<String, Object> map = new HashMap<>();
                        map.put("url", url);
                        map.put("result", responseBody);
                        map.put("error", result.getMessage());
                        throw new RuntimeException(JSONObject.toJSONString(map));
                    }
                } catch (Exception e) {
                    log.error("【Exception】", e);
                    Map parse = (Map) JSONObject.parse(e.getMessage());
                    throw new RuntimeException("" + parse.get("error"));
                }
            }
        });
    }

    @Override
    public Class<?> getObjectType() {
        return objectType;
    }

    public void setObjectType(Class<?> objectType) {
        this.objectType = objectType;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
