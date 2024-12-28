package com.seebon.rpa;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.seebon.rpa.entity.robot.vo.OcrReqVO;
import com.seebon.rpa.utils.HttpUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpHeaders;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class OcrUtil {
    //生产
    private static final String authorization = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJ4dWZ1emhvdSIsInNjb3BlIjpbInJ3Il0sIm5hbWUiOiLlvpDlr4zlkagiLCJjdXN0SWQiOjEsImV4cCI6MTc5NTEwMDkzNywiY3VzdE5hbWUiOiLlub_lt57ku5XpgqbkurrlipvotYTmupDmnInpmZDlhazlj7giLCJ1c2VySWQiOjE2LCJhdXRob3JpdGllcyI6WyJhZG1pbiJdLCJqdGkiOiJmZjIyNDcxMC01MjgxLTQ5OGEtYTQ5MS1lYWExMDM0OTg5MDEiLCJjbGllbnRfaWQiOiJycGEifQ.MeHafZs4dAR259YSxMnlya0Eu8V6VwrCuxg_GeRfRmg";

    public static void main(String[] args) throws Exception {
        String filePath = "D:\\test\\v5.png";
        //ocrText2(filePath, "http://192.168.0.102:9898/ocr/file/json");
        //ocrText2(filePath, "http://192.168.0.102:9797/ocr/file/json");

        ocrText2(filePath, "https://rpa.seebon.com/api/ocr/file/json");
        ocrText2(filePath, "https://rpa.seebon.com/api/ocr/file/json2");

    }

    public static void selectZcryList() {
        String url = "https://sbwb.guangdong.chinatax.gov.cn/sbf/api/ssfmgr/back/sbjydjgt/selectZcryList?appId=1440000016&requestId=9gi2lcgxwaQjaQWt2lq803gTgfcEIVm8&token=DB2407AEEE5DFC2FF159A006033C31B9";

        Map<String, Object> data = Maps.newHashMap();
        data.put("xmList", Lists.newArrayList());
        data.put("grsbhList", Lists.newArrayList());
        data.put("sfzjhmList", Lists.newArrayList("441623199809064625", "440111198807290348"));
        data.put("pageIndex", "1");
        data.put("pageSize", "10");
        data.put("sfzjlx", "201");

        Map<String, String> headers = Maps.newHashMap();
        headers.put("Accept", "application/json, text/plain, */*");
        headers.put("Accept-Encoding", "gzip, deflate, br");
        headers.put("Accept-Language", "zh-CN,zh;q=0.9");
        headers.put("Connection", "keep-alive");
        /*  headers.put("Content-Length", "107");*/
        headers.put("Content-Type", "application/json;charset=UTF-8");
        headers.put("Host", "sbwb.guangdong.chinatax.gov.cn");
        headers.put("Origin", "https://sbwb.guangdong.chinatax.gov.cn");
        headers.put("Referer", "https://sbwb.guangdong.chinatax.gov.cn/sbf/index/zcrycxJs");
        headers.put("Sec-Ch-Ua", "\"Not.A/Brand\";v=\"8\", \"Chromium\";v=\"114\", \"Google Chrome\";v=\"114\"");
        headers.put("Sec-Ch-Ua-Mobile", "?0");
        headers.put("Sec-Ch-Ua-Platform", "Windows");
        headers.put("Sec-Fetch-Dest", "empty");
        headers.put("Sec-Fetch-Mode", "cors");
        headers.put("Sec-Fetch-Site", "same-origin");
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36");
        //Cookie:yfx_c_g_u_id_10003701=_ck23092109291912595304504754308; yfx_f_l_v_t_10003701=f_t_1695259759257__r_t_1695259759257__v_t_1695259759257__r_c_0; yfx_mr_10003701=%3A%3Amarket_type_free_search%3A%3A%3A%3Abaidu%3A%3A%3A%3A%3A%3A%3A%3Awww.baidu.com%3A%3A%3A%3Apmf_from_free_search; yfx_mr_f_10003701=%3A%3Amarket_type_free_search%3A%3A%3A%3Abaidu%3A%3A%3A%3A%3A%3A%3A%3Awww.baidu.com%3A%3A%3A%3Apmf_from_free_search; yfx_key_10003701=; yfx_c_g_u_id_10000433=_ck23092815491919257003403367133; yfx_f_l_v_t_10000433=f_t_1695887359927__r_t_1695887359927__v_t_1695887359927__r_c_0; yfx_c_g_u_id_10003702=_ck23101110402716659227909105259; yfx_f_l_v_t_10003702=f_t_1696992027667__r_t_1696992027667__v_t_1696992027667__r_c_0; Hm_lvt_a83fd5cd47f51f4a9b53ddc2b0b56cde=1696992028; DZSWJ_TGC=05C06F3F376E4FB68A2A12B8E9749A6A; x_host_key=18b270b002f-53f7d5e27abdac445a0e55c84b41285e97369b86; TGC=TGT-5490035-rsGhQEni9W9Gro6fnedRT1U3bdJ5dng9B9CwFagcPfm0LxubmE-gddzswj; tokenId=05C06F3F376E4FB68A2A12B8E9749A6A; userType=0; random=V8wLBaJJgGkIdmoY29AugXrObnkfw64x

        String json = HttpUtils.post(url, JSON.toJSONString(data), headers);
        System.out.println(json);
    }

    public static void fileUpload() throws Exception {
        CloseableHttpClient client = HttpClients.createSystem();
        HttpPost httpPost = new HttpPost("https://rpa.seebon.com/api/policy/sys/file/fileUpload?allowDelete=0");
        httpPost.addHeader(HttpHeaders.AUTHORIZATION, authorization);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create().setMode(HttpMultipartMode.RFC6532);
        File file = new File((String) "D:\\\\seebon\\\\rpa\\cache\\gz\\9a49e82557e4484d96d0ac6671c3fd47_110368033683_sb_details_10014423000053599562.xlsx");
        builder.addBinaryBody("file", file, ContentType.MULTIPART_FORM_DATA, file.getName());
        HttpEntity entity = builder.build();
        httpPost.setEntity(entity);
        HttpEntity entity2 = ((CloseableHttpResponse) client.execute(httpPost)).getEntity();
        System.out.println(EntityUtils.toString(entity2, "utf-8"));
    }

    public static void ocrText() {
        String filePath = "D:\\seebon\\rpa\\python\\images\\2c416622c800475c82239d3a1a283596.png";
        //生产
        String url = "http://192.168.0.92:9527/robot/client/ocrText";
        //本地
        //String url = "http://172.172.4.142:9527/robot/client/ocrText";
        OcrReqVO reqVO = new OcrReqVO();
        reqVO.setBytes(FileUtil.readBytes(filePath));
        Map<String, String> headers = Maps.newHashMap();
        headers.put("Authorization", authorization);
        String text = HttpUtils.post(url, JSON.toJSONString(reqVO), headers);
        System.out.println("OCR图片识别 返回：" + text);
    }

    public static void ocrText2(String filePath, String api_url) {
        File file = new File(filePath);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(api_url);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addBinaryBody("image", file, ContentType.MULTIPART_FORM_DATA, file.getName());
        HttpEntity multipart = builder.build();
        httpPost.setEntity(multipart);
        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                String responseBody = EntityUtils.toString(responseEntity);
                System.out.println("api_url=" + api_url + ", resp=" + responseBody);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
