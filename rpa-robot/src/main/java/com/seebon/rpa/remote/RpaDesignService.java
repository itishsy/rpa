package com.seebon.rpa.remote;

import com.seebon.rpa.entity.robot.*;
import com.seebon.rpa.entity.robot.device.response.UsbKeyResponse;
import com.seebon.rpa.entity.robot.dto.AppVersionDTO;
import com.seebon.rpa.entity.robot.dto.ChangeLoginStatusDTO;
import com.seebon.rpa.entity.robot.dto.RobotAppDTO;
import com.seebon.rpa.entity.robot.dto.RobotExecutionDTO;
import com.seebon.rpa.entity.robot.vo.*;
import com.seebon.rpa.entity.sms.vo.GetSmsCodeVO;
import com.seebon.rpa.http.HttpService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@HttpService("${rpa.server}")
public interface RpaDesignService {

    @PostMapping("/robot/client/app/latest/{code}")
    RobotAppDTO findApp(@PathVariable(value = "code") String appCode);

    @PostMapping("/robot/client/app/allClientApp")
    List<AppVersionDTO> allApps();

    @PostMapping("/robot/client/app/saveUsb")
    int saveUsb(@RequestBody RobotClientUsb clientUsb);

    @PostMapping("/robot/client/app/clearUsb/{machineCode}")
    void clearUsb(@PathVariable("machineCode") String machineCode);

    @PostMapping("/robot/client/app/fileUploadForServer")
    String fileUploadForServer(@RequestBody String params);

    @PostMapping("/robot/client/task/listTask/{machineCode}")
    RobotAppDTO listTask(@PathVariable("machineCode") String machineCode);

    @PostMapping("/robot/client/task/callbackTask/{machineCode}")
    void callbackTask(@PathVariable("machineCode") String machineCode);

    @PostMapping("/robot/client/task/listTaskSessionKeep/{machineCode}")
    List<RobotTaskSessionKeep> listTaskSessionKeep(@PathVariable("machineCode") String machineCode);

    @PostMapping("/robot/client/task/callbackTaskSessionKeep/{machineCode}")
    void callbackTaskSessionKeep(@PathVariable("machineCode") String machineCode);

    @PostMapping("/robot/client/task/listAppCa/{machineCode}")
    List<RobotAppCaVO> listAppCa(@PathVariable("machineCode") String machineCode);

    @PostMapping("/robot/client/task/callbackAppCa/{machineCode}")
    void callbackAppCa(@PathVariable("machineCode") String machineCode);

    @PostMapping("/robot/client/task/getClient/{machineCode}")
    RobotClient getClient(@PathVariable("machineCode") String machineCode);

    @PostMapping("/robot/client/task/callbackClient/{machineCode}")
    void callbackClient(@PathVariable("machineCode") String machineCode);

    @PostMapping("/robot/client/task/listConfig/{machineCode}")
    List<RobotConfig> listConfig(@PathVariable("machineCode") String machineCode);

    @PostMapping("/robot/client/task/callbackConfig/{machineCode}")
    void callbackConfig(@PathVariable("machineCode") String machineCode);

    @PostMapping("/robot/client/task/updateTaskSessionKeep")
    void updateTaskSessionKeep(@RequestBody RobotTaskSessionKeep keep);

    @PostMapping("/robot/client/task/callbackTaskStatus")
    void callbackTaskStatus(@RequestBody RobotTaskVO taskVO);

    @PostMapping("/robot/client/task/callbackTaskQueue")
    int callbackTaskQueue(@RequestBody List<RobotTaskQueue> list);

    @PostMapping("/robot/client/task/findCommand/{machineCode}")
    List<RobotCommand> findCommand(@PathVariable("machineCode") String machineCode);

    @PostMapping("/robot/client/task/findTaskQueue/{machineCode}")
    RobotTaskQueueVO findTaskQueue(@PathVariable("machineCode") String machineCode);

    @PostMapping("/robot/client/task/callbackCommand")
    void callbackCommand(@RequestBody RobotCommand command);

    @PostMapping("/robot/client/task/getSmsCode")
    String getSmsCode(@RequestBody GetSmsCodeVO smsCodeVO);

    @PostMapping("/robot/client/execution/add")
    int addExecution(@RequestBody RobotExecutionDTO executionDTO);

    @PostMapping("/robot/client/execution/addMonitor")
    int addMonitor(@RequestBody List<RobotExecutionMonitor> monitorList);

    @PostMapping("/robot/client/execution/saveExecutionData")
    int saveExecutionData(@RequestBody List<RobotExecutionData> executionDataList);

    @PostMapping("/robot/client/execution/saveExecutionVoucher")
    int saveExecutionVoucher(@RequestBody RobotExecutionVoucher voucher);

    @PostMapping("/robot/client/ocrText")
    String ocrText(@RequestBody OcrReqVO reqVO);

    @PostMapping("/robot/client/ocrTextBySelfBaidu")
    String ocrTextBySelfBaidu(@RequestBody OcrReqVO reqVO);

    @PostMapping("/robot/client/ocrTextByBaidu")
    String ocrTextByBaidu(@RequestBody OcrReqVO reqVO);

    @PostMapping("/robot/client/ocrCaptcha")
    String ocrCaptcha(@RequestBody OcrReqVO reqVO);

    @PostMapping("/robot/client/ocrCaptchaNew")
    String ocrCaptchaNew(@RequestBody OcrReqVO reqVO);

    @PostMapping("/robot/client/app/listFlowStep")
    List<RobotFlowStep> listFlowStep(@RequestBody RobotFlowStep flowStep);

    @PostMapping("/robot/client/app/getByFlowCode/{flowCode}")
    RobotFlowVO getByFlowCode(@PathVariable(value = "flowCode") String flowCode);

    @PostMapping("/robot/offerRule/list")
    List<RobotOfferRuleVO> listOfferRule(@RequestBody RobotOfferRuleVO ruleVO);

    @PostMapping("/robot/client/app/stopAccount")
    String stopAccount(@RequestBody RobotTaskNotice taskNotice);

    @PostMapping("/robot/client/app/loginSuccess")
    Boolean loginSuccess(@RequestBody ChangeLoginStatusDTO statusDTO);

    @PostMapping("/robot/client/getUsbKey")
    UsbKeyResponse getUsbKey(@RequestBody RobotUsbKey usbKey);
}
