package com.seebon.rpa.utils;

import com.alibaba.fastjson.JSONObject;
import com.seebon.rpa.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import java.util.Map;

@Slf4j
public class WorkdayUtils {

    private static final String successCode = "1";

    private static String resUrl = "https://api.liangmlk.cn/k780/aapi.php?app=life.workday&date=";

    /**
     * 判断是否为工作日
     * @param date yyyy-MM-dd
     * @return
     */
    public static boolean isWorkDay(String date) {
        try {
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(30 * 1000)// 连接超时时间 30秒
                    .setSocketTimeout(300 * 1000)// 数据传输超时时间
                    .setConnectionRequestTimeout(30 * 1000)//请求超时时间 30秒
                    .build();
            SSLContextBuilder sslContextBuilder = SSLContexts.custom().loadTrustMaterial((chain, authType) -> true);
            SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContextBuilder.build(), NoopHostnameVerifier.INSTANCE);

            CloseableHttpClient client = HttpClients.custom().setSSLSocketFactory(sslSocketFactory).build();
            HttpGet httpGet = new HttpGet(resUrl + date);
            httpGet.addHeader("Content-Type", "application/json");
            httpGet.setConfig(requestConfig);
            CloseableHttpResponse execute = client.execute(httpGet);

            HttpEntity entity = ((CloseableHttpResponse) execute).getEntity();
            String result = EntityUtils.toString(entity, "utf-8");
            System.out.println(result);
            Map<String, Object> map = JSONObject.parseObject(result, Map.class);
            String success = (String)map.get("success");
            if (successCode.equals(success)) {
                Map<String, Object> res = (Map<String, Object>)map.get("result");
                String workmk = (String) res.get("workmk");
                return workmk!=null && workmk.equals("1");
            } else {
                throw new BusinessException("判断是否为工作日异常！");
            }
        } catch (Exception e) {
            log.error("判断是否为工作日异常：{}", e.getMessage(), e);
            throw new BusinessException("判断是否为工作日异常！");
        }
    }

    /**
     * 判断是否为工作日(https://date.appworlds.cn/work)
     * @param date yyyy-MM-dd
     * @return
     */
    private final static String resUrl_Appworlds = "https://date.appworlds.cn/work?";
    public static boolean isWorkDay_Appworlds(String date) {
        try {
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(30 * 1000)// 连接超时时间 30秒
                    .setSocketTimeout(300 * 1000)// 数据传输超时时间
                    .setConnectionRequestTimeout(30 * 1000)//请求超时时间 30秒
                    .build();
            SSLContextBuilder sslContextBuilder = SSLContexts.custom().loadTrustMaterial((chain, authType) -> true);
            SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContextBuilder.build(), NoopHostnameVerifier.INSTANCE);

            CloseableHttpClient client = HttpClients.custom().setSSLSocketFactory(sslSocketFactory).build();
            HttpGet httpGet = new HttpGet(resUrl_Appworlds + date);
            httpGet.addHeader("Content-Type", "application/json");
            httpGet.setConfig(requestConfig);
            CloseableHttpResponse execute = client.execute(httpGet);

            HttpEntity entity = execute.getEntity();
            String result = EntityUtils.toString(entity, "utf-8");
            System.out.println(result);
            JSONObject responseData = JSONObject.parseObject(result);

            Integer success = responseData.getInteger("code");
            if (success == 200) {
                return responseData.getJSONObject("data").getBoolean("work");
            } else {
                throw new BusinessException("判断是否为工作日异常！");
            }
        } catch (Exception e) {
            log.error("判断是否为工作日异常：{}", e.getMessage(), e);
            throw new BusinessException("判断是否为工作日异常！");
        }
    }

}
