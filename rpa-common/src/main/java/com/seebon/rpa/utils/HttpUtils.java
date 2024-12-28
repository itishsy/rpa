package com.seebon.rpa.utils;

import com.alibaba.fastjson.JSON;
import com.seebon.rpa.BusinessException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * httpclient请求工具类
 *
 * @author huangshouyi
 * @since 2022-06-01
 */
public class HttpUtils {
    private static PoolingHttpClientConnectionManager connectionManager;
    private static RequestConfig requestConfig;
    private static HttpClientBuilder httpBuilder;

    static {
        requestConfig = RequestConfig.custom().setConnectTimeout(30 * 1000)// 连接目标服务器超时时间
                .setSocketTimeout(5 * 60 * 1000)// 读取目标服务器数据超时时间
                .setConnectionRequestTimeout(30 * 1000)//从连接池获取连接的超时时间
                .build();

        // 设置连接池
        connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(200); //最大连接数
        connectionManager.setDefaultMaxPerRoute(200);//每主机最大连接数

        httpBuilder = HttpClients.custom();
        httpBuilder.setConnectionManager(connectionManager).setConnectionManagerShared(true);
    }

    private static String sendRequest(boolean isPost, String url, Object params, Map<String, String> headers) {
        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;
        HttpPost httpPost = new HttpPost(url);
        try {
            client = httpBuilder.build();
            if (isPost) {
                httpPost.setConfig(requestConfig);
                if (headers != null) {
                    for (String key : headers.keySet()) {
                        if ("isReqBody".equals(key) || "contentType".equals(key)) {
                            continue;
                        }
                        httpPost.setHeader(key, headers.get(key));
                    }
                }

                if (params != null) {
                    if (params instanceof Map && (headers==null || !headers.containsKey("isReqBody") || "false".equals(headers.get("isReqBody")))) {
                        List<NameValuePair> pairs = mapToPairs((Map) params);
                        httpPost.setEntity(new UrlEncodedFormEntity(pairs, "UTF-8"));
                    } else if(params instanceof Map && ((Map) params).get("file") instanceof FileBody) {
                        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                        //解决上传文件，文件名中文乱码问题
                        builder.setCharset(Charset.forName("utf-8"));
                        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);//设置浏览器兼容模式
                        // builder.addBinaryBody("uploadFile", file, ContentType.create("multipart/form-data"), "file");
                        builder.addPart("file", (FileBody) ((Map) params).get("file"));
                        httpPost.setEntity(builder.build());
                    } else {
                        String contentType = headers.get("contentType");
                        httpPost.setHeader("content-type", StringUtils.isBlank(contentType)?"application/json; charset=UTF-8":contentType);
                        String json = (params instanceof String) ? params.toString() : JSON.toJSONString(params);
                        StringEntity se = new StringEntity(json, "UTF-8");
                        se.setContentEncoding(new BasicHeader("Content-Type", StringUtils.isBlank(contentType)?"application/json; charset=UTF-8":contentType));
                        httpPost.setEntity(se);
                    }
                }
                response = client.execute(httpPost);
            } else {
                RequestBuilder builder = RequestBuilder.get(url).setConfig(requestConfig);
                if (headers != null) {
                    for (String key : headers.keySet()) {
                        builder.setHeader(key, headers.get(key));
                    }
                }
                if (params != null) {
                    if (params instanceof Map) {
                        List<NameValuePair> pairs = mapToPairs((Map) params);
                        builder.addParameters(pairs.toArray(new NameValuePair[pairs.size()]));
                    }
                }
                response = client.execute(builder.build());
            }
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                try {
                    HttpEntity entity = response.getEntity();
                    return EntityUtils.toString(entity, "utf-8");
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
            throw new BusinessException("{statusCode:" + statusCode + "}");
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("{error:\"" + e.getMessage() + "\"}");
        } finally {
            try {
                if (response != null) {
                    EntityUtils.consume(response.getEntity());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            IOUtils.closeQuietly(response);
            IOUtils.closeQuietly(client);
        }
    }

    public static String get(String url) {
        return get(url, null, null);
    }

    public static String get(String url, Object params, Map<String, String> headers) {
        return sendRequest(false, url, params, headers);
    }

    public static String post(String url) {
        return post(url, null, null);
    }

    public static String post(String url, Object params, Map<String, String> headers) {
        return sendRequest(true, url, params, headers);
    }

    private static List<NameValuePair> mapToPairs(Map<String, Object> map) {
        List<NameValuePair> pairs = null;
        if (null != map && map.size() > 0) {
            pairs = new ArrayList<>();
            for (String key : map.keySet()) {
                Object val = map.get(key);
                String valStr = null;
                if (val != null) {
                    valStr = (val instanceof String) ? (String) val : JSON.toJSONString(val);
                }
                pairs.add(new BasicNameValuePair(key, valStr));
            }
        }
        return pairs;
    }
}
