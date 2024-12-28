package com.seebon.rpa.rest.monitor;

import com.google.common.collect.Maps;
import com.seebon.rpa.annotation.MyLog;
import com.seebon.rpa.entity.IgGridDefaultPage;
import com.seebon.rpa.entity.robot.*;
import com.seebon.rpa.entity.robot.dto.*;
import com.seebon.rpa.entity.robot.dto.design.ClientExecutionVO;
import com.seebon.rpa.entity.robot.dto.design.RobotClientAppVo;
import com.seebon.rpa.entity.robot.dto.design.RobotExecutionMo;
import com.seebon.rpa.entity.robot.dto.design.RobotTaskDataDTO;
import com.seebon.rpa.entity.robot.vo.design.RobotExecutionDetailMoVO;
import com.seebon.rpa.entity.robot.vo.design.RobotTaskVO1;
import com.seebon.rpa.entity.saas.po.declare.customer.CustomerBase;
import com.seebon.rpa.entity.saas.vo.system.customer.CustomerBaseVO;
import com.seebon.rpa.service.RobotClientAppService;
import com.seebon.rpa.service.RobotTaskService;
import com.seebon.rpa.utils.BeanCopyUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@Api(value = "客户应用列表", tags = "客户应用列表")
@RestController
@RequestMapping("/app/client")
public class RobotExecutionController {
    @Autowired
    private RobotClientAppService clientAppService;
    @Autowired
    private RobotTaskService robotTaskService;

    @ApiOperation(value = "客户应用列表-列表查询")
    @PostMapping("/list")
    public IgGridDefaultPage<RobotClientAppVo> clientAppList(@RequestBody Map<String, Object> reqMap) {
        return clientAppService.appList(reqMap);
    }

    @ApiOperation("客户应用列表-导出")
    @PostMapping("/downloadClientApp")
    public void downloadClientApp(HttpServletResponse response, @RequestBody Map<String, Object> reqMap) throws IOException {
        clientAppService.exportClientApp(response, reqMap);
    }

    @ApiOperation(value = "客户应用列表-账户情况-机器与应用信息")
    @PostMapping("/getRobotClientInfo/{clientId}/{machineCode}/{appCode}")
    public RobotClientDTO getRobotClientInfo(@ApiParam(value = "客户id", name = "clientId", required = true) @PathVariable("clientId")Integer clientId,
                                             @ApiParam(value = "设备编号", name = "machineCode", required = true)@PathVariable("machineCode")String machineCode,
                                             @ApiParam(value = "应用编号", name = "appCode", required = true)@PathVariable("appCode")String appCode) {
        return clientAppService.getRobotClientInfo(clientId, machineCode, appCode);
    }

    @ApiOperation(value = "客户应用列表-账户情况-账户列表")
    @PostMapping("/getRobotTasks/{clientId}/{machineCode}/{appCode}")
    public List<RobotTaskInfoDTO> getRobotTasks(@ApiParam(value = "客户id", name = "clientId", required = true) @PathVariable("clientId")Integer clientId,
                                                @ApiParam(value = "设备编号", name = "machineCode", required = true)@PathVariable("machineCode")String machineCode,
                                                @ApiParam(value = "应用编号", name = "appCode", required = true)@PathVariable("appCode")String appCode,
                                                @ApiParam(value = "关键字", name = "searchText", required = false)@RequestParam("searchText")String searchText) {
        Map<String,Object> params = Maps.newHashMap();
        params.put("clientId", clientId);
        params.put("machineCode", machineCode);
        params.put("appCode", appCode);
        params.put("searchText", searchText);
        return clientAppService.getRobotTasks(params);
    }

    @ApiOperation(value = "客户应用列表-账户情况-登录信息")
    @PostMapping("/getRobotTaskConfigInfo/{taskCode}")
    public List<RobotTaskConfigDTO> getRobotTaskConfigInfo(@ApiParam(value = "任务编号", name = "taskCode", required = true)@PathVariable("taskCode")String taskCode) {
        return clientAppService.getRobotTaskConfigInfo(taskCode);
    }

    @ApiOperation(value = "客户应用列表-账户情况-启用停用账户")
    @PostMapping("/updateTaskStatus/{appCode}/{taskCode}/{status}")
    @MyLog(moduleName = "客户应用列表", pageName = "账户情况", operation = "启用停用账户")
    public int updateTaskStatus(@ApiParam(value = "应用编号", name = "appCode", required = true)@PathVariable("appCode")String appCode,
                                @ApiParam(value = "任务编号", name = "taskCode", required = true)@PathVariable("taskCode")String taskCode,
                                @ApiParam(value = "状态1：启用，0：停用", name = "status", required = true)@PathVariable("status")Integer status,
                                @ApiParam(value = "原因，停用必填", name = "comment", required = false)@RequestParam("comment")String comment) {
        return clientAppService.updateTaskStatus(appCode, taskCode, status, comment);
    }

    @ApiOperation(value = "启用停用账户(机器人调用)")
    @PostMapping("/robotUpdateTaskStatus")
    @MyLog(moduleName = "客户应用列表", pageName = "账户情况", operation = "启用停用账户")
    public int robotUpdateTaskStatus(@RequestBody RobotTaskStatusChangeDTO robotTaskStatusChangeDTO) {
        return clientAppService.robotUpdateTaskStatus(robotTaskStatusChangeDTO);
    }


    @ApiOperation("客户应用列表-批量启用停用申报账户")
    @PostMapping("/batchEnableOrStop")
    @MyLog(moduleName = "客户应用列表", pageName = "客户应用列表", operation = "批量启用停用申报账户")
    public Integer batchEnableOrStop(@RequestBody RobotTaskDataDTO dto) {
        return robotTaskService.batchEnableOrStop(dto);
    }

    @ApiOperation(value = "客户应用列表-账户情况-展示单独执行流程信息")
    @PostMapping("/showServiceItemFlow/{appCode}/{taskCode}/{accountNumber}")
    public List<RobotTaskServiceItemDTO> showServiceItemFlow(@ApiParam(value = "应用编号", name = "appCode", required = true)@PathVariable("appCode")String appCode,
                                                             @ApiParam(value = "任务编号", name = "taskCode", required = true)@PathVariable("taskCode")String taskCode,
                                                             @ApiParam(value = "单位申报号或公积金号", name = "accountNumber", required = true)@PathVariable("accountNumber")String accountNumber) {
        return clientAppService.showServiceItemFlow(appCode, taskCode, accountNumber);
    }

    @ApiOperation(value = "客户应用列表-账户情况-启用停用流程状态")
    @PostMapping("/updateTaskFlowStatus/{appCode}/{taskCode}/{status}")
    @MyLog(moduleName = "客户应用列表", pageName = "账户情况", operation = "启用停用流程状态")
    public int updateTaskFlowStatus(@ApiParam(value = "应用编号", name = "appCode", required = true)@PathVariable("appCode")String appCode,
            @ApiParam(value = "任务编号", name = "taskCode", required = true)@PathVariable("taskCode")String taskCode,
                                    @ApiParam(value = "状态1：启用，0：停用", name = "status", required = true)@PathVariable("status")Integer status,
                                    @ApiParam(value = "流程编号", name = "flowCodes", required = true)@RequestParam("flowCodes")List<String> flowCodes,
                                    @ApiParam(value = "原因，停用必填", name = "comment", required = true)@RequestParam("comment")String comment
                                    ) {
        return clientAppService.updateTaskFlowStatus(appCode, taskCode, status, flowCodes, comment);
    }

    @ApiOperation(value = "客户应用列表-账户情况-执行流程")
    @PostMapping("/runTask/{appCode}/{taskCode}/{machineCode}")
    @MyLog(moduleName = "客户应用列表", pageName = "账户情况", operation = "执行流程")
    public void runTask(@ApiParam(value = "应用编号", name = "appCode", required = true)@PathVariable("appCode")String appCode,
                        @ApiParam(value = "任务编号", name = "taskCode", required = true)@PathVariable("taskCode")String taskCode,
                        @ApiParam(value = "设备编号", name = "machineCode", required = true)@PathVariable("machineCode")String machineCode,
                        @ApiParam(value = "流程编号,单独执行需要传", name = "flowCodes", required = false)@RequestParam("flowCodes")List<String> flowCodes,
                        @ApiParam(value = "流程编号,单独执行需要传", name = "serviceItemName", required = false)@RequestParam("serviceItemName")String serviceItemName,
                        @ApiParam(value = "是否校验执行计划", name = "checkSchedule", required = false) @RequestParam(value = "checkSchedule",required = false) Boolean checkSchedule
    ) {
        clientAppService.runTask(appCode, taskCode, machineCode, flowCodes, serviceItemName, checkSchedule);
    }

    @ApiOperation(value = "客户应用列表-执行计划-获取计划列表")
    @PostMapping("/getRobotPlant/{clientId}/{machineCode}/{appCode}")
    public List<RobotTaskInfoDTO> getRobotPlant(@ApiParam(value = "客户id", name = "clientId", required = true) @PathVariable("clientId")Integer clientId,
                                            @ApiParam(value = "设备编号", name = "machineCode", required = true)@PathVariable("machineCode")String machineCode,
                                            @ApiParam(value = "应用编号", name = "appCode", required = true)@PathVariable("appCode")String appCode,
                                            @ApiParam(value = "关键字", name = "searchText", required = false)@RequestParam("searchText")String searchText) {
        Map<String,Object> params = Maps.newHashMap();
        params.put("clientId", clientId);
        params.put("machineCode", machineCode);
        params.put("appCode", appCode);
        params.put("searchText", searchText);
        return clientAppService.getRobotPlant(params);
    }

    @ApiOperation(value = "客户应用列表-执行计划-编辑流程计划")
    @PostMapping("/editRobotTaskSchedule")
    @MyLog(moduleName = "客户应用列表", pageName = "执行计划", operation = "编辑流程计划")
    public int editRobotTaskSchedule(@ApiParam(value = "流程计划信息", name = "dto", required = true)@RequestBody RobotTaskScheduleDTO dto) {
        return clientAppService.editRobotTaskSchedule(dto);
    }


    @ApiOperation("客户应用列表-执行查询列表")
    @PostMapping("/executionList")
    public List<RobotExecutionMo> executionList(@ApiParam(value = "执行列表查询参数信息", name = "dto", required = true)@RequestBody ExecutionQueryDTO dto) {
        return clientAppService.executions(dto);
    }

    @ApiOperation("客户应用列表-执行查询列表导出")
    @PostMapping("/executionExport")
    public void executionExport(HttpServletResponse response, @ApiParam(value = "执行列表查询参数信息", name = "dto", required = true) @RequestBody ExecutionQueryDTO dto) throws Exception {
        clientAppService.executionExport(response, dto);
    }

    @ApiOperation("客户应用列表-执行查询-获取应用")
    @PostMapping("/getRobotAppList")
    public List<RobotAppInfoDTO> getRobotAppList(@ApiParam(value = "客户id", name = "clientId", required = false)@RequestParam("clientId") Integer clientId) {
        return clientAppService.getRobotAppList(clientId);
    }

    @ApiOperation("客户应用列表-执行查询-获取申报账户")
    @PostMapping("/getRobotTaskList")
    public List<RobotTaskInfoDTO> getRobotTaskList(@ApiParam(value = "客户id", name = "clientId", required = false)@RequestParam("clientId") Integer clientId,
                                                   @ApiParam(value = "应用编号", name = "appCode", required = false)@RequestParam("appCode") String appCode) {
        return clientAppService.getRobotTaskList(clientId, appCode);
    }

    @ApiOperation("客户机器人应用-禁用启用")
    @PostMapping("/disableOrElse")
    @MyLog(moduleName = "客户应用列表", pageName = "客户应用列表-禁用启用", operation = "禁用启用")
    public Integer disableOrElse(@RequestParam("status") Integer status, @RequestParam("id") Integer id) {
        return clientAppService.disableOrEnable(status, id);
    }

    @ApiOperation("客户机器人应用-新增")
    @PostMapping("/addRobotClientApp")
    @MyLog(moduleName = "客户应用列表", pageName = "客户应用列表-新增", operation = "新增客户机器人应用")
    public Integer addRobotClientApp(@RequestBody RobotClientAppVo robotClientAppVO) {
        return clientAppService.addClientApp(robotClientAppVO);
    }

    @ApiOperation("客户机器人应用-编辑")
    @PostMapping("/editRobotClientApp")
    @MyLog(moduleName = "客户应用列表", pageName = "客户应用列表-编辑", operation = "编辑客户机器人应用")
    public Integer editRobotClientApp(@RequestBody RobotClientAppVo robotClientAppVO) {
        return clientAppService.editRobotClientApp(robotClientAppVO);
    }

    @ApiOperation("客户机器人应用-切换盒子")
    @PostMapping("/changeRobotClientApp")
    @MyLog(moduleName = "客户应用列表", pageName = "客户应用列表-切换盒子", operation = "切换客户应用盒子")
    public Integer changeRobotClientApp(@RequestBody RobotClientAppVo robotClientAppVO) {
        return clientAppService.changeRobotClientApp(robotClientAppVO);
    }

    @ApiOperation("客户机器人应用-获取盒子下任务")
    @PostMapping("/getClientAppTask")
    @MyLog(moduleName = "客户应用列表", pageName = "客户应用列表-获取盒子下任务", operation = "获取盒子下任务")
    public List<RobotTaskVO1> getClientAppTask(@RequestBody RobotClientAppVo robotClientAppVO) {
        return clientAppService.getClientAppTask(robotClientAppVO);
    }

    @ApiOperation("客户机器人应用-删除")
    @PostMapping("/deleteRobotClientApp")
    @MyLog(moduleName = "客户应用列表", pageName = "客户应用列表-删除", operation = "删除客户应用盒子")
    public Integer deleteRobotClientApp(Integer id) {
        return clientAppService.removeApp(id);
    }

    @ApiOperation(value = "客户机器人应用-查找明细")
    @PostMapping("/detail/{code}")
    public List<RobotClientApp> findAppDetail(@PathVariable("code") String appCode, String version) {
        return clientAppService.findAppDetail(appCode, version);
    }

    @ApiOperation(value = "客户机器人应用-所有任务")
    @PostMapping("/task/list")
    public List<RobotTask> allClientTask() {
        return clientAppService.listTask();
    }

    @PostMapping("/execution/list")
    @ApiOperation(value = "客户机器人应用-查看执行记录")
    public List<ClientExecutionVO> findExecutions(String appCode) {
        return clientAppService.appExecutions(appCode);
    }

    @PostMapping("/client/execution/detail")
    @ApiOperation(value = "客户机器人应用-查看执行明细")
    public List<RobotExecutionDetailMoVO> findExecutionDetails(@RequestParam("executionCode") String executionCode, @RequestParam("flowCode") String flowCode) {
        return clientAppService.appExecutionsDetail(executionCode, flowCode);
    }

    @ApiOperation("执行计划,客户应用列表")
    @PostMapping(value = "/listClientTask")
    public RobotClientAppVo listClientTask(@RequestParam("appCode") String appCode, @RequestParam("clientId") Integer clientId,
                                           @RequestParam("machineCode") String machineCode) {
        return clientAppService.queryClientTask(appCode, clientId, machineCode);
    }

    @ApiOperation("查询客户")
    @PostMapping(value = "/listCustomer")
    public List<CustomerBaseVO> listCustomer() {
        List<CustomerBase> customerBases = clientAppService.customerList();
        List<CustomerBaseVO> customerBaseVOS = BeanCopyUtils.copyListProperties(customerBases, CustomerBaseVO::new);
        customerBaseVOS.forEach(s -> s.setCustomerName(s.getName()));
        return customerBaseVOS;
    }

    @ApiOperation("编辑页面回显")
    @PostMapping(value = "/editEcho")
    public RobotTask echo(Integer id) {
        return clientAppService.editEcho(id);
    }

    @ApiOperation("编辑执行计划")
    @PostMapping(value = "/editTask")
    @MyLog(moduleName = "客户应用列表", pageName = "客户应用列表-执行计划-编辑执行计划", operation = "编辑执行计划")
    public int editTask(RobotTask robotTask) {
        return clientAppService.editTask(robotTask);
    }

    @ApiOperation("应用概况")
    @PostMapping(value = "/getAppSituation")
    public RobotClientAppVo getAppSituation(String appCode, @RequestParam(value = "clientId") Integer clientId, String machineCode) {
        return clientAppService.appBasic(appCode, clientId);
    }



    @ApiOperation("获取客户IP和端口号")
    @PostMapping("/getClientPortAndIp")
    public RobotClient getClientPortAndIp() {
        return clientAppService.getRobotClient();
    }

    @ApiOperation("查询应用的所有任务")
    @PostMapping("/listAllTask")
    public List<RobotTaskVO1> listAllTask(@RequestParam("appCode") String appCode) {
        return clientAppService.listAllTask(appCode);
    }

    @ApiOperation("启动任务")
    @PostMapping("/startTask")
    @MyLog(moduleName = "客户应用列表", pageName = "客户应用列表-启动任务", operation = "启动任务")
    public Integer startTask(@RequestParam("taskCode") String taskCode) {
        return clientAppService.startTask(taskCode);
    }

    @ApiOperation("平台方可启用/停用指定申报账户的机器人")
    @PostMapping("/stopOrEnableTask")
    @MyLog(moduleName = "客户应用列表", pageName = "客户应用列表-停用或启用指定申报账户的机器人", operation = "停用或启用指定申报账户的机器人")
    public Integer stopOrEnableTask(@RequestParam("argsValue") String argsValue, @RequestParam("status") Integer status) {
        return clientAppService.stopOrEnableTask(argsValue, status);
    }

    @ApiOperation("返回当前客户下所有的盒子")
    @GetMapping("/getMachineByClientId")
    public List<RobotClient> getMachineByClientId(@ApiParam(name = "clientId", value = "客户id", required = false)
                                                      @RequestParam("clientId") Integer clientId) {
        return clientAppService.getMachineByClientId(clientId);
    }

    @ApiOperation("下载申报文件")
    @GetMapping("/downLoadFile")
    public void downLoadFile(@RequestParam(value = "filePath") String filePath,
                                          @RequestParam(value = "fileName") String fileName,
                                          HttpServletResponse response) {
        clientAppService.downloadFile(fileName,filePath,response);
    }
    @ApiOperation("获取执行查询信息")
    @PostMapping("/getExecutionMessage")
    public RobotExecutionMo getExecutionMessage(@RequestParam(value = "robotExecCode") String robotExecCode) {
        return clientAppService.getExecutionMessage(robotExecCode);
    }

    @ApiOperation("获取执行查询信息")
    @PostMapping("/selectExecutionCountByParams")
    public Integer selectExecutionCountByParams(@RequestBody Map<String, Object> params) {
        return clientAppService.selectExecutionCountByParams(params);
    }

    @ApiOperation("查询账号状态历史")
    @PostMapping("/getAccountStatusHistory")
    public List<RobotAccountStatusHistory> getAccountStatusHistory(String taskCode) {
        return clientAppService.getAccountStatusHistory(taskCode);
    }
}
