package com.seebon.rpa;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.binary.Base64;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class OcrUtil119 {
    private static final String imagePath =  "D:\\seebon\\rpa\\python\\images\\2c416622c800475c82239d3a1a283596.png";
    //private static final String url = "http://192.168.150.119:9100/predict/chinese_ocr_db_crnn_mobile";
    private static final String url = "http://192.168.150.119:9101/predict/chinese_ocr_db_crnn_server";

    public static void main(String[] args) {
        ocr();
    }

    private static void ocr() {
        TimeInterval timer = DateUtil.timer();
        // 读取待识别图片，并将图片转换为Base64编码
        String imageBase64 = Base64.encodeBase64String(FileUtil.readBytes(imagePath));
        // 发送POST请求
        JSONArray resut = ocrPost(url, imageBase64);
        System.out.println(resut.toString());
        System.out.println("耗时：" + timer.intervalSecond() + " 秒");
    }

    private static JSONArray ocrPost(String url, String base64) {
        JSONArray imageList = new JSONArray();
        imageList.add(base64);
        JSONObject requestData = new JSONObject();
        requestData.put("images", imageList);

        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setDoOutput(true);
            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.write(requestData.toString().getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
            outputStream.close();

            String resp = IoUtil.readUtf8(connection.getInputStream());
            JSONObject respJson = JSON.parseObject(resp);
            return respJson.getJSONArray("results");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //释放链接
            connection.disconnect();
        }
        return null;
    }
}
