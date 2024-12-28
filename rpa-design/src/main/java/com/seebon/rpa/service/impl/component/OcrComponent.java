package com.seebon.rpa.service.impl.component;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.seebon.rpa.BusinessException;
import com.seebon.rpa.entity.robot.vo.OcrReqVO;
import com.seebon.rpa.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.mime.content.FileBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import sun.misc.BASE64Encoder;

import java.io.File;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Slf4j
@Component
public class OcrComponent {

    @Value("${ocr.url}")
    private String ocrUrl;

    @Value("${ocr.urlNew}")
    private String ocrUrlNew;

    @Value("${ocr.selfbaidu.url:http://192.168.150.119:12656/ocr}")
    private String selfBaiduOcrUrl;

    @Value("${ocr.baidu.api_key:Q8hBRnUVsdZXtcjZZ7ZloIYq}")
    private String baiduApiKey;

    @Value("${ocr.baidu.secret_key:nxNV2qRGVeHkNr9kmVjoeSXPNyHjIja1}")
    private String baiduSecretKey;

    @Value("${ocr.selfbaidu.requestUuid:f71bc5112d8b42968ae1815696b9079a}")
    private String selfBaiduRequestUuid;

    static final OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder().build();
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public String ocrText(OcrReqVO reqVO) {
        String base64 = this.toBase64(reqVO.getBytes());

        Map<String, Object> data = Maps.newHashMap();
        data.put("key", Lists.newArrayList("image"));
        data.put("value", Lists.newArrayList(base64));
        try {
            String json = HttpUtils.post(ocrUrl, JSON.toJSONString(data), Maps.newHashMap());
            if (StringUtils.isBlank(json)) {
                return null;
            }
            JSONObject jsonObj = JSON.parseObject(json, JSONObject.class);
            JSONArray jsonArray = jsonObj.getJSONArray("value");
            return jsonArray.getString(0);
        } catch (Exception e) {
            log.error("ocr识别异常." + e.getMessage(), e);
            throw new BusinessException("ocr识别异常." + e.getMessage());
        }
    }

    /**
     * 百度自有ocr识别
     */
    public String ocrTextBySelfBaidu(OcrReqVO reqVO) {
        File file = FileUtil.writeBytes(reqVO.getBytes(), new File(UUID.randomUUID().toString()));
        try {
            Map<String, Object> data = Maps.newHashMap();
            Map<String, String> headerMap = Maps.newHashMap();
            headerMap.put("X-Request-UUID",selfBaiduRequestUuid);
            headerMap.put("isReqBody",Boolean.TRUE.toString());
            data.put("file", new FileBody(file));
            try {
                String json = HttpUtils.post(selfBaiduOcrUrl, data, headerMap);
                log.info(json);
                if (StringUtils.isBlank(json)) {
                    return null;
                }
                JSONObject jsonObj = JSON.parseObject(json, JSONObject.class);
                if (!"成功".equals(jsonObj.getString("msg"))) {
                    throw new BusinessException(String.format("ocr识别失败:%s",json));
                }
                JSONArray jsonArray = jsonObj.getJSONArray("data");
                return jsonArray.getString(0);
            } catch (Exception e) {
                log.error("ocr识别异常." + e.getMessage(), e);
                throw new BusinessException("ocr识别异常." + e.getMessage());
            }
        } finally {
            file.delete();
        }
    }

    /**
     * 使用百度OCR接口进行文字识别。
     * <p>
     * 该方法通过百度OCR API 将给定的图片（经过Base64编码）发送到百度OCR服务进行文字识别，并返回识别结果。
     *
     * @param reqVO 包含需要识别的图片数据的请求对象。图片数据应为字节数组格式，通过 {@link #toBase64(byte[])} 方法进行Base64编码。
     * @return OCR 识别结果的 JSON 字符串，表示识别出的文字信息。
     * @throws BusinessException 如果在调用百度OCR服务时发生异常，将抛出业务异常。
     * @see #toBase64(byte[]) 用于将图片数据编码为Base64格式。
     * @see <a href="https://ai.baidu.com/ai-doc/OCR/1k3h7y3db">百度OCR API 文档</a>
     */
    public String ocrTextByBaidu(OcrReqVO reqVO) {
//        Map<String, Object> data = Maps.newHashMap();
//        data.put("image", base64);
//        Map<String, String> headers = Maps.newHashMap();
//        headers.put("contentType","x-www-form-urlencoded");
//        try {
//            return HttpUtils.post("https://aip.baidubce.com/rest/2.0/ocr/v1/accurate_basic?access_token=" + getBaiduAccessToken(),
//                    data, headers);
//        } catch (Exception e) {
//            log.error("ocr识别异常." + e.getMessage(), e);
//            throw new BusinessException("ocr识别异常." + e.getMessage());
//        }
        try {
            String base64 = this.toBase64(reqVO.getBytes());
            MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
            RequestBody body = RequestBody.create(mediaType, "image=" + base64);
            Request request = new Request.Builder()
                    .url("https://aip.baidubce.com/rest/2.0/ocr/v1/accurate_basic?access_token=" + getBaiduAccessToken())
                    .method("POST", body)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .addHeader("Accept", "application/json")
                    .build();
            Response response = HTTP_CLIENT.newCall(request).execute();
            ResponseBody responseBody = response.body();
            if (responseBody != null) {
                return responseBody.string();
            }
            return "";
        } catch (Exception e) {
            log.error("百度ocr识别异常." + e.getMessage(), e);
            throw new BusinessException("百度ocr识别异常." + e.getMessage());
        }
    }

    private String getBaiduAccessToken() {
        String baiduToken = redisTemplate.opsForValue().get("baiduToken");
        if (StringUtils.isNotBlank(baiduToken)) {
            return baiduToken;
        }
        String json = HttpUtils.post("https://aip.baidubce.com/oauth/2.0/token?grant_type=client_credentials&client_id=" + baiduApiKey + "&client_secret=" + baiduSecretKey);
        JSONObject jsonObj = JSON.parseObject(json, JSONObject.class);
        redisTemplate.opsForValue().set("baiduToken", jsonObj.getString("access_token"), jsonObj.getInteger("expires_in"), TimeUnit.SECONDS);
        return jsonObj.getString("access_token");
    }

    public String ocrCaptcha(OcrReqVO reqVO) {
        try {
            String text = this.ocrText(reqVO);
            log.info("识别验证码：text=" + text);
            if (StringUtils.isBlank(text)) {
                return "";
            }
            String[] texts = text.split("\\[\\('");
            if (texts.length <= 1) {
                return "";
            }
            return this.getCaptcha(texts);
        } catch (Exception e) {
            log.error("ocr验证码识别异常" + e.getMessage(), e);
        }
        return "";
    }

    private String getCaptcha(String[] texts) {
        StringBuilder result = new StringBuilder("");
        for (String text : texts) {
            String[] values = text.split("\\), \\[\\[");
            if (values.length <= 1) {
                continue;
            }
            String[] captchas = values[0].split(",");
            if (captchas.length <= 1) {
                continue;
            }
            String captcha = captchas[0].replace("'", "").replace(".", "").trim();
            if (StringUtils.isBlank(captcha)) {
                continue;
            }
            result.append(captcha);
        }
        return result.toString();
    }

    private String toBase64(byte[] bytes) {
        return new BASE64Encoder().encode(bytes);
    }

    public String ocrCaptchaNew(OcrReqVO reqVO) {
        try {
            String base64 = this.toBase64(reqVO.getBytes());
            System.out.println("====== 验证码识别开始 ======");
            Map<String, String> header = Maps.newHashMap();
            header.put("contentType", "application/json; charset=UTF-8");
            String post = HttpUtils.post(ocrUrlNew, base64, header);
            if (StringUtils.isNotBlank(post)) {
                JSONObject jsonObject = JSONObject.parseObject(post);
                String orcResult = jsonObject.getString("code").toLowerCase();
                System.out.println(String.format("识别的验证码为：%s", orcResult));
                return orcResult;
            }
        } catch (Exception e) {
            log.error("ocr验证码识别异常" + e.getMessage(), e);
        }
        return "";
    }
}
