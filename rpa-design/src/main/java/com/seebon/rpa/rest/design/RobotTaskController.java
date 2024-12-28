package com.seebon.rpa.rest.design;

import com.seebon.rpa.annotation.MyLog;
import com.seebon.rpa.entity.agent.dto.CustomerOrgBusinessDTO;
import com.seebon.rpa.entity.robot.RobotCommand;
import com.seebon.rpa.entity.robot.RobotTaskArgs;
import com.seebon.rpa.entity.robot.RobotTaskNotice;
import com.seebon.rpa.entity.robot.dto.TaskInfoDTO;
import com.seebon.rpa.entity.robot.dto.design.RobotTaskDataDTO;
import com.seebon.rpa.entity.robot.dto.design.RobotTaskParamsDTO;
import com.seebon.rpa.entity.robot.vo.RobotAppFormVO;
import com.seebon.rpa.entity.robot.vo.RobotClientAppVO;
import com.seebon.rpa.entity.robot.vo.RobotCommandVO;
import com.seebon.rpa.entity.robot.vo.RobotTaskQueueVO;
import com.seebon.rpa.service.RobotTaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@Api(value = "客户机器人任务", tags = "RPA应用设计")
@RestController
@RequestMapping("/task")
public class RobotTaskController {
    @Autowired
    private RobotTaskService robotTaskService;



    @ApiOperation("客户下所有机器人任务")
    @PostMapping("/listRobotTask")
    public List<RobotClientAppVO> listRobotTask() {
        return robotTaskService.listTaskByCustId();
    }

    @ApiOperation("配置机器人页面")
    @RequestMapping("/showArgs")
    public List<RobotAppFormVO> showArgs(@RequestParam("appCode") String appCode) {
        return robotTaskService.queryFormVO(appCode);
    }

    @ApiOperation("添加任务")
    @PostMapping("/saveTask")
    @MyLog(moduleName = "客户机器人任务", pageName = "客户机器人任务-添加任务", operation = "添加任务")
    public Integer saveTask(@RequestBody List<RobotTaskArgs> robotTaskArgs, @RequestParam(value = "appCode") String appCode,
                            @RequestParam(value = "machineCode") String machineCode) {
        return robotTaskService.addTask(robotTaskArgs, appCode,machineCode);
    }

    @ApiOperation("校验配置信息")
    @PostMapping("/checkConfig")
    public void check(@RequestBody List<RobotTaskArgs> robotTaskArgs, @RequestParam(value = "appCode") String appCode,@RequestParam(value = "addrId") Integer addrId) {
        robotTaskService.check(robotTaskArgs, appCode,addrId);
    }

    @ApiOperation("修改配置信息时校验")
    @PostMapping("/checkEditConfig")
    public void checkEdit(@RequestBody List<RobotTaskArgs> robotTaskArgs, @RequestParam(value = "appCode") String appCode) {
        robotTaskService.checkEdit(robotTaskArgs, appCode);
    }

    @ApiOperation("配置信息回显")
    @PostMapping("/echoConfig")
    public List<RobotTaskArgs> echoConfig(@RequestParam("taskCode") String taskCode) {
        return robotTaskService.echoTaskArgs(taskCode);
    }

    @ApiOperation("编辑配置信息")
    @PostMapping("/editArgs")
    @MyLog(moduleName = "客户机器人任务", pageName = "客户机器人任务-编辑配置信息", operation = "编辑配置信息")
    public Integer editArgs(@RequestBody List<RobotTaskArgs> robotTaskArgs, @RequestParam("taskCode") String taskCode,
                            @RequestParam("originalCompanyName") String originalCompanyName,@RequestParam("originalAccountNumber") String originalAccountNumber) {
        return robotTaskService.editArgs(robotTaskArgs, taskCode,originalCompanyName,originalAccountNumber);
    }

    @ApiOperation("暂停或启动任务")
    @PostMapping("/enableOrStop")
    @MyLog(moduleName = "客户机器人任务", pageName = "客户机器人任务-暂停或启动任务", operation = "暂停或启动任务")
    public Integer enableOrStop(@ApiParam(name = "appCode", value = "应用编号", required = true) @RequestParam("appCode") String appCode,
                                @ApiParam(name = "status", value = "状态1：启用，0：停用", required = true) @RequestParam("status") Integer status,
                                @ApiParam(name = "taskCode", value = "任务编号", required = true) @RequestParam("taskCode") String taskCode,
                                @ApiParam(name = "comment", value = "停用原因", required = true) @RequestParam("comment") String comment
                                ) {
        return robotTaskService.updateStatus(appCode, status, taskCode, comment);
    }

    @ApiOperation("启动任务。自动任务触发。")
    @PostMapping("/startTask")
    @MyLog(moduleName = "客户机器人任务", pageName = "客户机器人任务-启动任务", operation = "启动任务")
    public String startTask(@RequestBody RobotCommand robotCommand) {
        return robotTaskService.startTask(robotCommand);
    }

    @ApiOperation("获取手动任务执行结果")
    @PostMapping("/getRobotCommandExeList")
    public List<RobotCommandVO> getRobotCommandExeList() {
        return robotTaskService.getRobotCommandExeList();
    }

    @PostMapping("/checkTaskRunning")
    public Boolean checkTaskRunning(@RequestBody List<TaskInfoDTO> taskInfoList) {
        return robotTaskService.checkTaskRunning(taskInfoList);
    }

    @PostMapping("/addTaskCommand/{clientId}/{addrName}/{accountNumber}/{businessType}")
    public void addTaskCommand(@PathVariable("clientId") Integer clientId,
                                 @PathVariable("addrName") String addrName,
                                 @PathVariable("accountNumber") String accountNumber,
                                 @PathVariable("businessType") Integer businessType
                                 ) {
        robotTaskService.addTaskCommand(clientId, addrName, accountNumber, businessType);
    }

    @ApiOperation("测试流程")
    @MyLog(moduleName = "流程管理", pageName = "流程管理-测试流程", operation = "测试流程")
    @PostMapping("/runTestCommand")
    public Map<String, Object> runTestCommand(@RequestParam("taskCode") String taskCode, @RequestParam("flowCode") String flowCode, @RequestParam("stepCode") String stepCode) {
        return robotTaskService.runTestCommand(taskCode, flowCode, stepCode);
    }

    @ApiOperation("测试流程-状态")
    @MyLog(moduleName = "流程管理", pageName = "流程管理-测试流程-状态", operation = "测试流程-状态")
    @PostMapping("/getTestTaskStatus")
    public Integer getTestTaskStatus(@RequestParam("taskCode") String taskCode) {
        return robotTaskService.getTestTaskStatus(taskCode);
    }


    @ApiOperation("在户人数-获取客户申报单位业务类型信息")
    @PostMapping("/getCustOrgBusiness/{clientId}/{addrName}/{orgName}")
    public List<CustomerOrgBusinessDTO> getCustOrgBusiness(
            @ApiParam(name = "clientId", value = "客户id", required = true)@PathVariable("clientId")Integer clientId,
            @ApiParam(name = "addrName", value = "参保城市", required = true)@PathVariable("addrName")String addrName,
            @ApiParam(name = "orgName", value = "申报单位", required = true)@PathVariable("orgName")String orgName){
        return robotTaskService.getCustOrgBusiness(clientId, addrName, orgName);
    }


    @ApiOperation("启动任务，在户人数手动触发")
    @PostMapping("/runTask/{clientId}/{addrName}/{orgName}")
    @MyLog(moduleName = "在户人数", pageName = "在户人数-启动任务", operation = "启动任务")
    public String runTask(@ApiParam(name = "clientId", value = "客户id", required = true)@PathVariable("clientId")Integer clientId,
                          @ApiParam(name = "addrName", value = "参保城市", required = true)@PathVariable("addrName")String addrName,
                          @ApiParam(name = "orgName", value = "申报单位", required = true)@PathVariable("orgName")String orgName,
                          @ApiParam(name = "businessTypes", value = "业务类型", required = true)@RequestParam("businessTypes") List<Integer> businessTypes) {
        return robotTaskService.runTask(clientId, addrName, orgName, businessTypes);
    }

    @ApiOperation("获取任务通知弹窗列表")
    @PostMapping("/listTaskNotice")
    public List<RobotTaskNotice> listTaskNotice() {
        return robotTaskService.listTaskNotice();
    }

    @ApiOperation("恢复任务")
    @PostMapping("/regainTask/{appCode}/{taskCode}")
    @MyLog(moduleName = "申报配置", pageName = "申报配置-恢复任务", operation = "恢复任务")
    public void regainTask(@ApiParam(name = "appCode", value = "应用编码", required = true) @PathVariable("appCode") String appCode,
                           @ApiParam(name = "taskCode", value = "任务编码", required = true) @PathVariable("taskCode") String taskCode) {
        robotTaskService.regainTask(appCode, taskCode);
    }

    @ApiOperation("延期")
    @PostMapping("/delayTask/{taskCode}/{payDate}")
    @MyLog(moduleName = "申报配置", pageName = "申报配置-延期", operation = "延期")
    public void delayTask(@ApiParam(name = "taskCode", value = "任务编码", required = true) @PathVariable("taskCode") String taskCode,
                          @ApiParam(name = "payDate", value = "缴费时间", required = true) @PathVariable("payDate") String payDate) {
        robotTaskService.delayTask(taskCode, payDate);
    }

    @ApiOperation("申报配置-批量启用停用-统计申报账户")
    @PostMapping("/getTaskList")
    public RobotTaskDataDTO getTaskList(@RequestBody RobotTaskParamsDTO dto) {
        return robotTaskService.getTaskList(dto);
    }

    @ApiOperation("申报配置-批量启用停用申报账户")
    @PostMapping("/batchEnableOrStop")
    @MyLog(moduleName = "申报配置", pageName = "申报配置", operation = "批量启用停用申报账户")
    public Integer batchEnableOrStop(@RequestBody RobotTaskDataDTO dto) {
        return robotTaskService.batchEnableOrStop(dto);
    }
}
