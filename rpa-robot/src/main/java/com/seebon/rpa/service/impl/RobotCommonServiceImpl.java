package com.seebon.rpa.service.impl;

import cn.hutool.core.io.IoUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.seebon.rpa.actions.target.impl.ResponseTarget;
import com.seebon.rpa.entity.agent.dto.openApi.DeclareCounterOfferDTO;
import com.seebon.rpa.entity.agent.dto.openApi.DeclareOfferExportParamsDTO;
import com.seebon.rpa.service.RobotCommonService;
import com.seebon.rpa.utils.Convert;
import com.seebon.rpa.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class RobotCommonServiceImpl implements RobotCommonService {
    @Value("${rpa.token}")
    private String token;
    @Value("${rpa.server}")
    public String rpaServer;
    @Autowired
    private ResponseTarget responseTarget;

    @Override
    public String checkOfferExportUrl(Map<String, Object> params) {
        return this.post("/agent/employeeDeclare/checkOfferExport", params);
    }

    @Override
    public String declareTempInfoUrl(Map<String, Object> params) {
        return this.post("/agent/employeeDeclare/getDeclareTempInfo", params);
    }

    @Override
    public String savePaidInPersonDataUrl(Map<String, Object> params) {
        return this.post("/agent/paidIn/savePaidInPersonData", params);
    }

    @Override
    public String getDeclareDataUrl(Map<String, Object> params) {
        return this.post("/agent/employeeDeclare/getDeclareData", params);
    }

    @Override
    public String declareCounterOffer(List<DeclareCounterOfferDTO> declareOfferList) {
        return this.post("/agent/employeeDeclare/declareCounterOffer", declareOfferList);
    }

    @Override
    public String saveRegister(Map<String, Object> params) {
        return this.post("/agent/cust/insured/register", params);
    }

    @Override
    public String saveEmpAccount(Map<String, Object> params) {
        return this.post("/agent/employeeDeclare/saveEmpAccount", params);
    }

    @Override
    public String getEmployeeFiles(Map<String, Object> params) {
        return this.post("/agent/employeeDeclare/getEmployeeFiles", params);
    }

    @Override
    public String getLoginNoticeTemp(String appCode, String tplTypeCode, String validateType) {
        return this.post("/policy/sys/robot/loginNoticeTemplate/getloginNoticeTemp/" + appCode + "/" + tplTypeCode + "/" + validateType, Maps.newHashMap());
    }

    @Override
    public String addNotice(Map<String, Object> params) {
        return this.post("/policy/sys/robot/noticeList/addNotice", params);
    }

    @Override
    public String getResendSignal(String execBatchCode) {
        return this.post("/policy/sys/robot/noticeList/getResendSignal/" + execBatchCode, Maps.newHashMap());
    }

    @Override
    public String loginSuccess(String execBatchCode) {
        return this.post("/policy/sys/robot/noticeList/loginSuccess/" + execBatchCode, Maps.newHashMap());
    }

    @Override
    public String getSmsCode(String execBatchCode) {
        String res = this.post("/policy/sys/robot/noticeList/getSmsCode/" + execBatchCode, Maps.newHashMap());
        JSONObject resObj = JSON.parseObject(res);
        return resObj.getString("data");
    }

    @Override
    public void downloadFile(String fileId, String filePath) throws Exception {
        CloseableHttpResponse response = this.getResp("/policy/sys/file/download/" + fileId);
        Convert.respToFile(response, filePath);
    }

    @Override
    public CloseableHttpResponse downloadFile(String fileId) throws Exception {
        return this.getResp("/policy/sys/file/download/" + fileId);
    }

    @Override
    public String fileUpload(File file) {
        //上传图片
        if (!file.exists()) {
            throw new RuntimeException("文件不存在");
        }
        responseTarget.setMethod("postFile");
        responseTarget.setUrl(rpaServer + "/policy/sys/file/fileUpload?allowDelete=0");
        Map<String, Object> body = Maps.newHashMap();
        body.put("file", file.getAbsolutePath());
        responseTarget.setParams(body);
        CloseableHttpResponse response = (CloseableHttpResponse) responseTarget.fetchTarget();
        String result = null;
        try {
            result = IoUtil.readUtf8(response.getEntity().getContent());
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
        JSONObject jsonObject = JSON.parseObject(result);
        return JSON.parseObject(jsonObject.getJSONArray("data").get(0).toString()).getString("fileID");
    }

    @Override
    public String post(String url, Object params) {
        Map<String, String> headers = Maps.newHashMap();
        headers.put("Content-Type", "application/json");
        headers.put(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        return HttpUtils.post(rpaServer + url, JSON.toJSONString(params), headers);
    }

    @Override
    public String postLocal(String url, Object params) {
        Map<String, String> headers = Maps.newHashMap();
        headers.put("Content-Type", "application/json");
        headers.put(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        return HttpUtils.post(url, JSON.toJSONString(params), headers);
    }

    @Override
    public CloseableHttpResponse offerDownloadUrl(Map<String, Object> params) throws IOException {
        return this.postResp("/agent/employeeDeclare/declareOfferExport", params);
    }

    @Override
    public CloseableHttpResponse postResp(String url, Map<String, Object> params) throws IOException {
        CloseableHttpClient client = HttpClients.createSystem();
        String bodyJson = JSON.toJSONString(params);

        HttpPost httpPost = new HttpPost(rpaServer + url);
        httpPost.addHeader("Content-Type", "application/json");
        httpPost.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);

        StringEntity se = new StringEntity(bodyJson, "UTF-8");
        se.setContentEncoding(new BasicHeader("Content-Type", "application/json"));
        httpPost.setEntity(se);
        return client.execute(httpPost);
    }

    public CloseableHttpResponse getResp(String url) throws IOException {
        RequestConfig requestConfig = Convert.getRequestConfig(5);
        CloseableHttpClient client = HttpClients.createSystem();
        HttpGet httpGet = new HttpGet(rpaServer + url);
        httpGet.addHeader("Content-Type", "application/json");
        httpGet.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        httpGet.setConfig(requestConfig);
        return client.execute(httpGet);
    }


    @Override
    public String obtainedFeeDetail(Map<String, Object> params) {
        return this.post("/agent/paidIn/checkPaidIn", params);
    }

    @Override
    public String stopAutoTask(Map<String, Object> params) {
        return this.post("/robot/app/client/robotUpdateTaskStatus", params);
    }

    @Override
    public String getByKeyAndName(String dataKey, String dictName) {
        return this.post("/policy/sys/dict/getByKeyAndName?dataKey=" + dataKey + "&dictName=" + dictName, Maps.newHashMap());
    }

    @Override
    public String declareTagCheck(DeclareOfferExportParamsDTO dto) {
        return this.post("/agent/employeeDeclare/findValidateRule", dto);
    }

    @Override
    public String upsetTaskTrigger(Map<String, Object> params) {
        return this.post("/robot/client/task/upsetTaskTrigger", params);
    }
}
