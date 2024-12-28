package com.seebon.rpa.rest.remote;

import com.google.common.collect.Maps;
import com.seebon.rpa.entity.IgGridDefaultPage;
import com.seebon.rpa.entity.robot.*;
import com.seebon.rpa.entity.robot.dto.AppVersionDTO;
import com.seebon.rpa.entity.robot.dto.ChangeLoginStatusDTO;
import com.seebon.rpa.entity.robot.dto.DeclareAccountBaseDTO;
import com.seebon.rpa.entity.robot.dto.RobotAppDTO;
import com.seebon.rpa.entity.robot.dto.design.RobotClientAppVo;
import com.seebon.rpa.entity.robot.dto.design.RobotClientExecutionVO;
import com.seebon.rpa.entity.robot.vo.RobotAppVO;
import com.seebon.rpa.entity.robot.vo.RobotFlowVO;
import com.seebon.rpa.entity.saas.vo.CustomerInsuredRegisterExVO;
import com.seebon.rpa.remote.RpaSaasService;
import com.seebon.rpa.service.RobotAppService;
import com.seebon.rpa.service.RobotClientAppService;
import com.seebon.rpa.service.RobotLoginAuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@Api(value = "机器人应用数据同步", tags = "通讯中心")
@RestController
@RequestMapping("/client/app")
public class RobotClientAppController {
    @Autowired
    private RobotClientAppService clientAppService;
    @Autowired
    private RobotAppService robotAppService;
    @Autowired
    private RpaSaasService rpaSaasService;
    @Autowired
    private RobotLoginAuthService loginAuthService;

    @ApiOperation(value = "应用数据-保存任务")
    @PostMapping("/task/save")
    public int saveTask(@RequestBody RobotTask clientTask) {
        return clientAppService.saveTask(clientTask);
    }

    @ApiOperation(value = "应用数据-保存执行明细")
    @PostMapping("/execution/save")
    public int addExecution(@RequestBody RobotClientExecutionVO executionVO) {
        return clientAppService.addExecution(executionVO);
    }

    @ApiOperation(value = "应用数据-同步盒子获取最新应用")
    @PostMapping("/latest/{code}")
    public RobotAppDTO latestVersion(@PathVariable(value = "code") String appCode) {
        return robotAppService.findLatest(appCode);
    }

    @ApiOperation(value = "获取客户应用")
    @PostMapping("/all/{machineCode}")
    public List<AppVersionDTO> clientApp(@PathVariable("machineCode") String machineCode) {
        return clientAppService.findClientApps(machineCode);
    }

    @ApiOperation(value = "获取客户应用")
    @PostMapping("/allClientApp")
    public List<AppVersionDTO> allClientApp() {
        return clientAppService.findClientApps(null);
    }

    @ApiOperation("获取机器人申报业务类型")
    @PostMapping("/getRobotDeclareType")
    public RobotAppVO getRobotDeclareType(@RequestParam("appCode") String appCode) {
        return robotAppService.getRobotDeclareType(appCode);
    }

    @ApiOperation("调saas服务获取待申报人数")
    @PostMapping("/getAwaitDeclareNumber/{businessType}/{accountNumber}/{addrId}")
    public Integer getAwaitDeclareNumber(@PathVariable("businessType") String businessType, @PathVariable("accountNumber") String accountNumber,
                                         @PathVariable("addrId") Integer addrId) {
        return rpaSaasService.awaitDeclareNumber(businessType, accountNumber, addrId);
    }

    @PostMapping("/getBusinessType/{appCode}")
    public String getDictName(@PathVariable(value = "appCode") String appCode) {
        return robotAppService.getDictName(appCode);
    }

    @PostMapping("/saveDeclareAccount")
    public Integer saveDeclareAccount(@RequestBody DeclareAccountBaseDTO declareAccountBaseDTO) {
        return robotAppService.saveDeclareAccount(declareAccountBaseDTO);
    }

    @ApiOperation(value = "获取Usb")
    @PostMapping("/listUsb/{machineCode}")
    public List<RobotClientUsb> listUsb(@PathVariable(value = "machineCode") String machineCode) {
        return robotAppService.listUsb(machineCode);
    }

    @ApiOperation(value = "回调Usb")
    @PostMapping("/callbackUsb/{machineCode}")
    public void callbackUsb(@PathVariable(value = "machineCode") String machineCode) {
        robotAppService.callbackUsb(machineCode);
    }

    @ApiOperation(value = "保存Usb")
    @PostMapping("/saveUsb")
    public int saveUsb(@RequestBody RobotClientUsb clientUsb) {
        return robotAppService.saveUsb(clientUsb);
    }

    @ApiOperation(value = "清空Usb")
    @PostMapping("/clearUsb/{machineCode}")
    public void clearUsb(@PathVariable(value = "machineCode") String machineCode) {
        robotAppService.clearUsb(machineCode);
    }

    @ApiOperation(value = "文件上传，提供给后台使用")
    @PostMapping("/fileUploadForServer")
    public String fileUploadForServer(@RequestBody Map<String, Object> params) {
        robotAppService.fileUploadForServer(params);
        return "";
    }

    @ApiOperation(value = "客户应用列表-列表查询")
    @PostMapping("/clientAppList/{taskCode}")
    public List<RobotClientAppVo> clientAppList(@PathVariable(value = "taskCode") String taskCode) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("taskCode", taskCode);
        IgGridDefaultPage<RobotClientAppVo> page = clientAppService.appList(map);
        return page.getRows();
    }

    @ApiOperation(value = "检查是否需要拿在册人数")
    @PostMapping("/checkRegister")
    public Integer checkRegister(@RequestBody CustomerInsuredRegisterExVO registerVO) {
        return clientAppService.checkRegister(registerVO);
    }

    @ApiOperation(value = "获取执行步骤")
    @PostMapping("/listFlowStep")
    public List<RobotFlowStep> listFlowStep(@RequestBody RobotFlowStep flowStep) {
        return clientAppService.listFlowStep(flowStep);
    }

    @ApiOperation(value = "获取流程")
    @PostMapping("/getByFlowCode/{flowCode}")
    public RobotFlowVO getByFlowCode(@PathVariable(value = "flowCode") String flowCode) {
        return clientAppService.getByFlowCode(flowCode);
    }

    @ApiOperation(value = "停用申报账户")
    @PostMapping("/stopAccount")
    public String stopAccount(@RequestBody RobotTaskNotice taskNotice) {
       return clientAppService.stopAccount(taskNotice);
    }

    @PostMapping("/getRobotClient/{machineCode}")
    public RobotClient getRobotClient(@PathVariable("machineCode") String machineCode) {
        return clientAppService.getRobotClientByMachineCode(machineCode);
    }

    @PostMapping("/getRobotClientApp/{appCode}/{machineCode}")
    public RobotClientApp getRobotClientApp(@PathVariable("appCode") String appCode, @PathVariable("machineCode") String machineCode) {
        return clientAppService.getRobotClientApp(appCode, machineCode);
    }

    @PostMapping("/loginSuccess")
    public void loginSuccess(@RequestBody ChangeLoginStatusDTO statusDTO) {
        clientAppService.loginSuccess(statusDTO);
    }
}
