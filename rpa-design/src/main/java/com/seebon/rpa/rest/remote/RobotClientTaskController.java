package com.seebon.rpa.rest.remote;

import cn.hutool.core.lang.Dict;
import com.seebon.rpa.annotation.MyLog;
import com.seebon.rpa.entity.robot.*;
import com.seebon.rpa.entity.robot.dto.*;
import com.seebon.rpa.entity.robot.vo.*;
import com.seebon.rpa.entity.sms.vo.GetSmsCodeVO;
import com.seebon.rpa.remote.RpaSaasService;
import com.seebon.rpa.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Api(value = "机器人任务同步", tags = "通讯中心")
@RestController
@RequestMapping("/client/task")
public class RobotClientTaskController {
    @Autowired
    private RobotClientTaskService robotClientTaskService;
    @Autowired
    private RpaSaasService rpaSaasService;
    @Autowired
    private RobotAppService robotAppService;
    @Autowired
    private RobotTaskService robotTaskService;
    @Autowired
    private CustomerCommandService customerCommandService;
    @Autowired
    private RobotTaskQueueService taskQueueService;

    @ApiOperation(value = "保存任务")
    @PostMapping("/add")
    public int add(@RequestBody RobotTaskDTO taskDTO) {
        return robotClientTaskService.addOrUpdate(taskDTO);
    }

    @ApiOperation(value = "回调任务状态")
    @PostMapping("/callbackTaskStatus")
    public void callbackTaskStatus(@RequestBody RobotTaskVO taskVO) {
        robotClientTaskService.callbackTaskStatus(taskVO);
    }

    @ApiOperation(value = "回调任务队列")
    @PostMapping("/callbackTaskQueue")
    public Integer callbackTaskQueue(@RequestBody List<RobotTaskQueue> list) {
        return taskQueueService.callbackTaskQueue(list);
    }

    @ApiOperation(value = "查询任务")
    @PostMapping("/findTask/{code}")
    public RobotTask findTask(@PathVariable(value = "code") String taskCode) {
        return robotClientTaskService.findTask(taskCode);
    }

    @ApiOperation(value = "获取执行指令")
    @PostMapping("/findCommand/{machineCode}")
    public List<RobotCommand> findCommand(@PathVariable(value = "machineCode") String machineCode) {
        return robotClientTaskService.findCommand(machineCode);
    }

    @ApiOperation(value = "获取执行队列")
    @PostMapping("/findTaskQueue/{machineCode}")
    public RobotTaskQueueVO findTaskQueue(@PathVariable(value = "machineCode") String machineCode) {
        return taskQueueService.findTaskQueue(machineCode);
    }

    @ApiOperation(value = "回调执行指令")
    @PostMapping("/callbackCommand")
    public void callbackCommand(@RequestBody RobotCommand command) {
        robotClientTaskService.callbackCommand(command);
    }

    @ApiOperation(value = "获取客户指令")
    @PostMapping("/listCustomerCommand")
    public List<CustomerCommandVO> listCustomerCommand(@RequestBody CustomerCommandVO commandVO) {
        return customerCommandService.listCustomerCommand(commandVO);
    }

    @ApiOperation(value = "回调客户指令")
    @PostMapping("/callbackCustomerCommand")
    public void callbackCustomerCommand(@RequestBody List<CustomerCommandVO> commands) {
        customerCommandService.callbackCustomerCommand(commands);
    }

    @ApiOperation(value = "更新执行指令")
    @PostMapping("/updateCommand")
    public void updateCommand(@RequestBody RobotCommand command) {
        robotClientTaskService.updateCommand(command);
    }

    @ApiOperation(value = "获取执行任务")
    @PostMapping("/listTask/{machineCode}")
    public RobotAppDTO listTask(@PathVariable(value = "machineCode") String machineCode) {
        return robotClientTaskService.listTask(machineCode);
    }

    @ApiOperation(value = "回调执行任务")
    @PostMapping("/callbackTask/{machineCode}")
    public void callbackTask(@PathVariable(value = "machineCode") String machineCode) {
        robotClientTaskService.callbackTask(machineCode);
    }

    @ApiOperation(value = "获取执行任务长session维持数据")
    @PostMapping("/listTaskSessionKeep/{machineCode}")
    public List<RobotTaskSessionKeepVO> listTaskSessionKeep(@PathVariable(value = "machineCode") String machineCode) {
        return robotClientTaskService.listTaskSessionKeep(machineCode);
    }

    @ApiOperation(value = "回调执行任务长session维持数据")
    @PostMapping("/callbackTaskSessionKeep/{machineCode}")
    public void callbackTaskSessionKeep(@PathVariable(value = "machineCode") String machineCode) {
        robotClientTaskService.callbackTaskSessionKeep(machineCode);
    }

    @ApiOperation(value = "获取执行应用CA数据")
    @PostMapping("/listAppCa/{machineCode}")
    public List<RobotAppCaVO> listAppCa(@PathVariable(value = "machineCode") String machineCode) {
        return robotClientTaskService.listAppCa(machineCode);
    }

    @ApiOperation(value = "回调应用CA数据")
    @PostMapping("/callbackAppCa/{machineCode}")
    public void callbackAppCa(@PathVariable(value = "machineCode") String machineCode) {
        robotClientTaskService.callbackAppCa(machineCode);
    }

    @ApiOperation(value = "获取客户端")
    @PostMapping("/getClient/{machineCode}")
    public RobotClient getClient(@PathVariable(value = "machineCode") String machineCode) {
        return robotClientTaskService.getClient(machineCode);
    }

    @ApiOperation(value = "回调客户端数据")
    @PostMapping("/callbackClient/{machineCode}")
    public void callbackClient(@PathVariable(value = "machineCode") String machineCode) {
        robotClientTaskService.callbackClient(machineCode);
    }

    @ApiOperation(value = "获取配置")
    @PostMapping("/listConfig/{machineCode}")
    public List<RobotConfig> listConfig(@PathVariable(value = "machineCode") String machineCode) {
        return robotClientTaskService.listConfig(machineCode);
    }

    @ApiOperation(value = "回调配置数据")
    @PostMapping("/callbackConfig/{machineCode}")
    public void callbackConfig(@PathVariable(value = "machineCode") String machineCode) {
        robotClientTaskService.callbackConfig(machineCode);
    }

    @ApiOperation(value = "更新执行任务长session维持数据")
    @PostMapping("/updateTaskSessionKeep")
    public void updateTaskSessionKeep(@RequestBody RobotTaskSessionKeep keep) {
        robotClientTaskService.updateTaskSessionKeep(keep);
    }

    @ApiOperation(value = "启停机器人")
    @PostMapping("/startOrStop")
    public Dict startOrStop(@RequestBody StartRobotDTO dto) {
        return robotClientTaskService.startOrStop(dto);
    }

    @ApiOperation(value = "机器人执行状态查询")
    @PostMapping("/getRobotStatus")
    public List<GetRobotStatusRespDTO> getRobotStatus(@RequestBody GetRobotStatusDTO dto) {
        return robotClientTaskService.getRobotStatus(dto);
    }

    @ApiOperation("启动任务。自动任务触发。")
    @PostMapping("/startTask")
    @MyLog(moduleName = "客户机器人任务", pageName = "客户机器人任务-启动任务", operation = "启动任务")
    public String startTask(@RequestBody RobotCommand robotCommand) {
        return robotTaskService.startTask(robotCommand);
    }

    @ApiOperation("获取用户的应用的task数")
    @PostMapping("/getTaskByCustId")
    public Integer getTaskByCustId(@RequestParam("clientId") Integer clientId) {
        return robotClientTaskService.getTaskCountByCustomer(clientId);
    }
    @PostMapping("/getMachineCode/{clientId}/{addrId}/{businessType}/{accountNumber}")
    public List<String> getMachineCode(@PathVariable(value = "clientId") Integer clientId,@PathVariable(value = "addrId") Integer addrId,
                                 @PathVariable(value = "businessType") String businessType,
                                       @PathVariable(value = "accountNumber") String accountNumber) {
        return robotAppService.getMachineCode(clientId, addrId, businessType, accountNumber);
    }

    @ApiOperation("获取短信")
    @PostMapping("/getSmsCode")
    public String getSmsCode(@RequestBody GetSmsCodeVO smsCodeVO) {
        return rpaSaasService.getSmsCode(smsCodeVO);
    }

    @ApiOperation(value = "获取指定任务流程信息")
    @PostMapping("/getTaskFlow")
    public GetTaskFlowRespDTO getTaskFlow(@RequestBody GetTaskFlowDTO dto) {
        return robotClientTaskService.getTaskFlow(dto);
    }
}
