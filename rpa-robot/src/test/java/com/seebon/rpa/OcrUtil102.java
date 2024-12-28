package com.seebon.rpa;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.seebon.rpa.utils.HttpUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.IOUtils;
import sun.misc.BASE64Encoder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class OcrUtil102 {
    private static final String url = "http://192.168.0.102:9998/ocr/prediction";

    private static final String filePath = "D:\\authcode.jpg";

    public static void main(String[] args) {
        /*String dd = "账户状态不为封存,不能跨单位启封;正常状态的公积金账户_不允许进行启封;姓名[黄瑞菁],证件号码[440104197905061313]存在正常缴存状态的账户,请检查";
        if (!dd.contains("没有此公积金账户") && !dd.contains("没有此公积金账户") && !dd.contains("正常状态的公积金账户_不允许进行启封") || dd.contains("账户状态不为封存,不能跨单位启封")) {
            System.out.println("=============");
        }*/
        ocrCaptchaTest();
    }

    public static void quertList() {
        //String url = "https://ybj.jszwfw.gov.cn/hsa-pss-cw/pss/web/insuRoster/exportList";
        //String url = "https://ybj.jszwfw.gov.cn/hsa-pss-cw/pss/web/insuRoster/quertList";
        String url = "https://ybj.jszwfw.gov.cn/hsa-pss-cw/api/auditSituation/queryUserEmpBizLogPage";
        Map<String, Object> params = Maps.newLinkedHashMap();
        params.put("certno", "");
        params.put("acpName", "");
        params.put("empBizType", "4");
        params.put("oprtTime", "");
        params.put("startTime", "2024-01-01");
        params.put("endTime", "2024-02-02");
        params.put("insutype", "");
        params.put("pageSizeDW", 83);
        params.put("pageNum", 1);
        params.put("pageSize", 10);
        params.put("chkStas", "5");

        Map<String, String> headers = Maps.newHashMap();
        headers.put("AccessToken", "b730a20fa404476d9701ff296643791f");
        headers.put("PoolareaNo", "320200");
        headers.put("content-type", "application/json");
        headers.put("Accept", "application/json");
        headers.put("Connection", "keep-alive");
        headers.put("Cookie", "headerShow=false; sid=7398fd27-ff04-4ec0-a282-463bde1f58fe; service-mall-accesstoken=b730a20fa404476d9701ff296643791f; service-mall-refreshtoken=0a2bf54cba5042fe8ee60ef0011a8e98; service-mall-authCode=9afc40b2d8e846daa57c88e225cde187; headerStatus=0; poolareaNo=320200; SESSION_FLAG=1; service-mall-userInfo=%7B%22accountId%22%3A%2216607939120000001%22%2C%22empId%22%3A%2216607939120000001%22%2C%22account%22%3A%22a913202026891727922%22%2C%22unitAccountId%22%3A%2216607939120000002%22%2C%22accountType%22%3A%22UNIT%22%2C%22userSourceType%22%3Anull%2C%22tokenType%22%3A%22loginUnit%22%2C%22seq%22%3A1706839332643%2C%22agentInfoDTO%22%3Anull%2C%22unitInfoDTO%22%3A%7B%22empId%22%3A%2216607939120000001%22%2C%22empUact%22%3A%22a913202026891727922%22%2C%22empType%22%3A%22%22%2C%22empName%22%3A%22%E5%B9%BF%E5%B7%9E%E4%BB%95%E9%82%A6%E4%BA%BA%E5%8A%9B%E8%B5%84%E6%BA%90%E6%9C%89%E9%99%90%E5%85%AC%E5%8F%B8%E6%97%A0%E9%94%A1%E5%88%86%E5%85%AC%E5%8F%B8%EF%BC%88%E6%B4%BE%E9%81%A3%EF%BC%89%22%2C%22uscc%22%3A%22913202026891727922%22%2C%22orgcode%22%3Anull%2C%22bizreglic%22%3Anull%2C%22taxRegNo%22%3Anull%2C%22legrepName%22%3A%22%E6%9B%B9%E6%89%BF%E5%86%9B%22%2C%22legrepTel%22%3A%2215920315275%22%2C%22legrepCertno%22%3A%2234082219860118093X%22%2C%22legrepCertType%22%3A%2201%22%2C%22empAddr%22%3A%22%E5%B9%BF%E5%B7%9E%E4%BB%95%E9%82%A6%E4%BA%BA%E5%8A%9B%E8%B5%84%E6%BA%90%E6%9C%89%E9%99%90%E5%85%AC%E5%8F%B8%E6%97%A0%E9%94%A1%E5%88%86%E5%85%AC%E5%8F%B8%22%2C%22roles%22%3A%5B%5D%2C%22type%22%3A%22unit%22%7D%2C%22loginType%22%3A%22loginUnit%22%2C%22isCaLogin%22%3Anull%2C%22type%22%3A%22unit%22%2C%22loginTel%22%3A%22a913202026891727922%22%7D; SERVERID=7c3e35df998791a32923d9f71e5d110b|1706839379|1706839325");
        headers.put("Host", "ybj.jszwfw.gov.cn");
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36");
        String json = HttpUtils.post(url, JSON.toJSONString(params), headers);
        System.out.println("json=" + json);
    }

    public static String prediction() {
        try {
            File file = new File(filePath);
            InputStream inputStream = new FileInputStream(file);
            String image = ImageToBase64(inputStream);

            Map<String, Object> data = Maps.newHashMap();
            data.put("key", Lists.newArrayList("image"));
            data.put("value", Lists.newArrayList(image));

            String json = HttpUtils.post(url, JSON.toJSONString(data), Maps.newHashMap());
            System.out.println("json=" + json);
            return json;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String ocrCaptchaTest() {
        try {
            String json = prediction();
            JSONObject jsonObj = JSON.parseObject(json, JSONObject.class);
            JSONArray jsonArray = jsonObj.getJSONArray("value");
            ocrCaptcha(jsonArray.getString(0));
            System.out.println(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String ocrCaptcha(String ocrText) {
        try {
            String text = ocrText;
            if (StringUtils.isBlank(text)) {
                return "";
            }
            String[] texts = text.split("\\[\\('");
            if (texts.length <= 1) {
                return "";
            }
            String dd = getCaptcha(texts);
            System.out.println("getCaptcha=" + dd);
            return texts[0].replace("'", "").replace(".", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String getCaptcha(String[] texts) {
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

    public static void run(String ocrText, String matchText) {
        Map<String, Integer> pointMap = Maps.newHashMap();
        int yPoint = 0;
        int xPoint = 0;
        if (StringUtils.isNotBlank(ocrText) && StringUtils.isNotBlank(matchText)) {
            List<String> textArr = Lists.newArrayList(ocrText.split("\\[\\('"));
            List<String> matchTexts = Arrays.stream(matchText.split(",")).map(item -> "'".concat(item).concat("'")).collect(Collectors.toList());
            Optional<String> first = textArr.stream().filter(it -> {
                String[] split = it.split("\\), \\[\\[");
                boolean isMatch = false;
                for (String mt : matchTexts) {
                    isMatch = mt.equals("'" + (split[0].split("', ")[0]) + "'");
                    if (isMatch) break;
                }
                return isMatch;
            }).findFirst();
            if (first.isPresent()) {
                String text = first.get();
                String[] split = text.split("\\), \\[\\[");
                xPoint = Double.valueOf(split[1].split("\\]")[0].split(",")[0]).intValue();
                yPoint = Double.valueOf(split[1].split("\\]")[0].split(",")[1]).intValue();
            }
        }
        pointMap.put("xPoint", xPoint);
        pointMap.put("yPoint", yPoint);
        System.out.println(xPoint + "=====" + yPoint);
    }

    private static String ImageToBase64(InputStream in) {
        byte[] bytes = null;
        try {
            bytes = IOUtils.toByteArray(in);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new BASE64Encoder().encode(bytes);
    }
}
