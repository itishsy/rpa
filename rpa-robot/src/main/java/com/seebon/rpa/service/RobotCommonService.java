package com.seebon.rpa.service;

import com.seebon.rpa.entity.agent.dto.openApi.DeclareCounterOfferDTO;
import com.seebon.rpa.entity.agent.dto.openApi.DeclareOfferExportParamsDTO;
import org.apache.http.client.methods.CloseableHttpResponse;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface RobotCommonService {
    /**
     * 查询是否有数据需要申报
     *
     * @return
     */
    String checkOfferExportUrl(Map<String, Object> params);

    /**
     * 获取报盘信息字段
     *
     * @return
     */
    String declareTempInfoUrl(Map<String, Object> params);

    /**
     * 上传费用明细
     *
     * @return
     */
    String savePaidInPersonDataUrl(Map<String, Object> params);

    /**
     * 上传费用明细
     *
     * @return
     */
    String getDeclareDataUrl(Map<String, Object> params);

    /**
     * 申报回盘
     *
     * @return
     */
    String declareCounterOffer(List<DeclareCounterOfferDTO> declareOfferList);

    /**
     * 保存在册数据
     *
     * @return
     */
    String saveRegister(Map<String, Object> params);

    /**
     * 保存个人社保号、个人公积金号
     *
     * @return
     */
    String saveEmpAccount(Map<String, Object> params);

    /**
     * 获取员工附件
     *
     * @return
     */
    String getEmployeeFiles(Map<String, Object> params);

    /**
     * 获取扫码登录提醒模板
     *
     * @return
     */
    String getLoginNoticeTemp(String appCode, String tplTypeCode, String validateType);

    /**
     * 推送扫码登录验证通知
     *
     * @return
     */
    String addNotice(Map<String, Object> params);

    /**
     * 获取是否重新获取二维码的信号
     *
     * @param execBatchCode
     * @return
     */
    String getResendSignal(String execBatchCode);

    /**
     * 调用接口修改本次登录认证成功
     *
     * @param execBatchCode
     * @return
     */
    String loginSuccess(String execBatchCode);

    /**
     * 调用接口获取H5页面上传的短信验证码
     *
     * @param execBatchCode
     * @return
     */
    String getSmsCode(String execBatchCode);

    /**
     * 下载文件
     *
     * @param fileId
     * @return
     */
    void downloadFile(String fileId, String filePath) throws Exception;

    /**
     * 下载文件
     *
     * @param fileId
     * @return
     */
    CloseableHttpResponse downloadFile(String fileId) throws Exception;

    /**
     * 文件上传
     *
     * @param file
     * @return
     */
    String fileUpload(File file);

    /**
     * 接口请求
     *
     * @return
     */
    String post(String url, Object params);
    String postLocal(String url, Object params);

    /**
     * 下载报盘文件
     *
     * @return
     */
    CloseableHttpResponse offerDownloadUrl(Map<String, Object> params) throws IOException;

    /**
     * 接口请求
     *
     * @return
     */
    CloseableHttpResponse postResp(String url, Map<String, Object> params) throws IOException;

    /**
     * 查询已经抓取过的社保公积金费用  如果已经获取过 就不重复爬取
     *
     * @param params
     * @return 响应结果
     */
    String obtainedFeeDetail(Map<String, Object> params);

    /**
     * 停用自动任务
     */
    String stopAutoTask(Map<String, Object> params);

    /**
     * 获取字典值
     */
    String getByKeyAndName(String dataKey, String dictName);

    /**
     * 检查报盘数据，是否存在未打待申报标签
     *
     * @return
     */
    String declareTagCheck(DeclareOfferExportParamsDTO dto);

    /**
     * 添加定时执行任务
     *
     * @return
     */
    String upsetTaskTrigger(Map<String, Object> params);
}
