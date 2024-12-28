package com.seebon.rpa.utils;

import com.seebon.rpa.entity.saas.BaseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author created By charles
 * @Description:
 * @Date 2020/8/5 17:28
 * @Modifide By:
 */
@Slf4j
public class RestRequestHelper {
    /**
     * 发送表单参数的post请求
     *
     * @param url      请求url
     * @param param    参数
     * @param respType 返回类型
     * @return T
     */
    public static <T> T post(String url, MediaType mediaType, Object param, Class<T> respType) {
        log.info("request=" + url + ",param=" + param);
        T t = getRestInstance().postForEntity(url, getHttpEntity(param, mediaType), respType).getBody();
        log.info("response=" + t);

        return t;
    }

    /**
     * 发送表单参数的异步post请求
     *
     * @param url      请求url
     * @param callback 回调接口
     * @param respType 返回类型
     */
    public static <T> void asyncPostForm(String url, Map<String, List<Object>> param,
                                         Class<T> respType, ListenableFutureCallback<ResponseEntity<T>> callback) {
        log.info(url);
        getAsyncRestInstance().postForEntity(url, getHttpEntity(param, MediaType.APPLICATION_JSON), respType).addCallback(callback);
    }

    /**
     * 发送表单有参数get请求
     *
     * @param url      请求url
     * @param param    参数对象
     * @param respType 返回类型
     * @return T
     */
    public static <T> T get(String url, Class<T> respType, Map<String, String> param) {
        log.info("request=" + url + ",param=" + param);
        T t = getRestInstance().getForEntity(url, respType, param).getBody();
        log.info("response=" + t);
        return t;
    }

    /**
     * @Description: 发送表单无参数的get请求
     * @Param: [url, param, respType]
     * @return: T
     */
    public static <T> T get(String url, Class<T> respType) {
        log.info("request=" + url);
        T t = getRestInstance().getForObject(url, respType);
        log.info("response=" + t);
        return t;
    }

    public static byte[] getInputStream(String url) {
        RestTemplate restTemplate = getRestInstance();
        HttpHeaders headers = new HttpHeaders();
        List list = new ArrayList();
        list.add(MediaType.ALL);
        headers.setAccept(list);
        ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<byte[]>(headers), byte[].class);
        return response.getBody();
    }

    public static <T> BaseVO<T> exchange(String url) {
        log.info("request=" + url);
        BaseVO baseVO = getRestInstance().exchange(url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<BaseVO<T>>() {
                }).getBody();
        log.info("resp=" + baseVO);
        return baseVO;
    }

    public static <T> T exchange(String url, HttpMethod method, Class<T> clz) {
        log.info("request=" + url);
        T t = getRestInstance().exchange(url,
                method,
                null,
                clz).getBody();
        log.info("resp=" + t);
        return t;
    }

    /**
     * 获取HttpEntity实例对象
     *
     * @param param     参数对象
     * @param mediaType 请求格式
     * @return HttpEntity
     */
    private static <P> HttpEntity<P> getHttpEntity(P param, MediaType mediaType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        return new HttpEntity<>(param, headers);
    }


    private static RestTemplate restInit() {
        //设置连接超时和读取超时时间
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5 * 1000);
        factory.setReadTimeout(2 * 60 * 1000);
        factory.setBufferRequestBody(false);
        //factory.setChunkSize(1000);
        RestTemplate restTemplate = new RestTemplate(factory);
        FormHttpMessageConverter fastConverter = new FormHttpMessageConverter();
        TextMappingJackson2HttpMessageConverter wmc = new TextMappingJackson2HttpMessageConverter();
        ByteArrayHttpMessageConverter bac = new ByteArrayHttpMessageConverter();

        restTemplate.getMessageConverters().add(fastConverter);
        restTemplate.getMessageConverters().add(wmc);
        restTemplate.getMessageConverters().add(bac);
        return restTemplate;
    }


    private static AsyncRestTemplate asyncRestInit() {
        return new AsyncRestTemplate();
    }

    public static RestTemplate getRestInstance() {
        return RestSingle.INSTANCE;
    }

    private static AsyncRestTemplate getAsyncRestInstance() {
        return AsyncRestSingle.INSTANCE;
    }


    private static class RestSingle {
        private static final RestTemplate INSTANCE = restInit();
    }

    private static class AsyncRestSingle {
        private static final AsyncRestTemplate INSTANCE = asyncRestInit();
    }

}
