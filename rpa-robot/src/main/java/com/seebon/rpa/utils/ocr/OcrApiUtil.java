package com.seebon.rpa.utils.ocr;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.seebon.rpa.context.RobotContext;
import com.seebon.rpa.utils.python.PythonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class OcrApiUtil {

    private static final String URL = "http://api.ttshitu.com/predict";
    private static final String USERNAME = "itishsy";
    private static final String PASSWORD = "123456";

    /**
     * 图像识别
     * //一、图片文字类型(默认    3 数英混合)：
     * //1 : 纯数字
     * //1001：纯数字2
     * //2 : 纯英文
     * //1002：纯英文2
     * //3 : 数英混合
     * //1003：数英混合2
     * //4 : 闪动GIF
     * //7 : 无感学习(独家)
     * //11 : 计算题
     * //1005:  快速计算题
     * //16 : 汉字
     * //32 : 通用文字识别(证件、单据)
     * //66:  问答题
     * //49 :recaptcha图片识别 参考 https://shimo.im/docs/RPGcTpxdVgkkdQdY
     * //二、图片旋转角度类型：
     * //29 :  旋转类型
     * <p>
     * //三、图片坐标点选类型：
     * //19 :  1个坐标
     * //20 :  3个坐标
     * //21 :  3 ~ 5个坐标
     * //22 :  5 ~ 8个坐标
     * //27 :  1 ~ 4个坐标
     * //48 : 轨迹类型
     * //四、缺口识别
     * //18：缺口识别
     * //五、拼图识别
     * //53：拼图识别
     *
     * @throws IOException
     */
    public static String doOCR(String image, String typeid) {
        Map<String, String> data = new HashMap<>();
        data.put("username", USERNAME);
        data.put("password", PASSWORD);
        data.put("typeid", typeid);
        data.put("remark", "验证码识别");
        data.put("image", image);
        try {

            String resultString = Jsoup.connect(URL)
                    .requestBody(JSON.toJSONString(data))
                    .header("Content-Type", "application/json")
                    .ignoreContentType(true).timeout(120000).post().text();
            JSONObject jsonObject = JSONObject.parseObject(resultString);
            if (jsonObject.getBoolean("success")) {
                String result = jsonObject.getJSONObject("data").getString("result");
                System.out.println("识别成功结果为:" + result);
                return result;
            } else {
                System.out.println("识别失败原因为:" + jsonObject.getString("message"));
                return null;
            }
        } catch (Exception e) {
            log.error("【Exception】", e);
            return null;
        }
    }

    public static String doOCR20(String image) {
        Map<String, String> data = new HashMap<>();
        data.put("username", USERNAME);
        data.put("password", PASSWORD);
        data.put("typeid", "20");
        data.put("remark", "验证码识别");
        data.put("image", image);
        try {

            String resultString = Jsoup.connect(URL)
                    .requestBody(JSON.toJSONString(data))
                    .header("Content-Type", "application/json")
                    .ignoreContentType(true).timeout(120000).post().text();
            JSONObject jsonObject = JSONObject.parseObject(resultString);
            if (jsonObject.getBoolean("success")) {
                String result = jsonObject.getJSONObject("db").getString("result");
                System.out.println("识别成功结果为:" + result);
                return result;
            } else {
                System.out.println("识别失败原因为:" + jsonObject.getString("message"));
                return null;
            }
        } catch (Exception e) {
            log.error("【Exception】", e);
            return null;
        }
    }

    public static String autonomousOCR(File file, int timeout) {
        List<String> commands = Lists.newArrayList();
        commands.add(RobotContext.pythonPath);
        commands.add(RobotContext.workPath + "\\python\\OcrIdentify.py");
        commands.add(RobotContext.workPath + "\\python\\model");
        commands.add(file.getPath());
        commands.add("0");
        commands.add("en");
        List<String> texts = PythonUtil.runCommand("OCR文字识别", null, timeout, commands);
        if (CollectionUtils.isNotEmpty(texts)) {
            String orcResult = texts.stream().collect(Collectors.joining(""));
            return orcResult.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll(" ", "").replaceAll("'", "").replaceAll("\"", "").trim();
        }
        return null;
    }
}
