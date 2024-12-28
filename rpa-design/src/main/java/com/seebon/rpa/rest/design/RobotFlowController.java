package com.seebon.rpa.rest.design;

import com.seebon.rpa.annotation.MyLog;
import com.seebon.rpa.entity.robot.*;
import com.seebon.rpa.entity.robot.dto.design.RobotFlowAddVO;
import com.seebon.rpa.entity.robot.dto.design.RobotFlowVO;
import com.seebon.rpa.entity.robot.vo.RobotFlowStatusHistoryVO;
import com.seebon.rpa.service.RobotFlowService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Api(value = "流程配置", tags = "RPA应用设计")
@RestController
@RequestMapping("/flow")
public class RobotFlowController {
    @Autowired
    private RobotFlowService flowService;

    @ApiOperation(value = "流程配置-列表")
    @PostMapping("/list")
    public List<RobotFlowVO> flowList(@RequestBody RobotFlowVO flowVO) {
        return flowService.flowList(flowVO);
    }

    @ApiOperation(value = "统计流程状态数量-列表")
    @PostMapping("/totalStatus")
    public List<RobotFlowVO> totalStatus(@RequestBody RobotFlowVO flowVO) {
        return flowService.totalStatus(flowVO);
    }

    @ApiOperation(value = "流程状态历史-列表")
    @PostMapping("/statusHistory")
    public List<RobotFlowStatusHistoryVO> statusHistory(@RequestBody RobotFlowVO flowVO) {
        return flowService.statusHistory(flowVO);
    }

    @ApiOperation(value = "流程配置-模板列表")
    @PostMapping("/listTemplate")
    public List<RobotFlowVO> listTemplate(RobotFlowVO flowVO) {
        return flowService.listTemplate(flowVO.getTemplateType(), flowVO.getFlowType());
    }

    @ApiOperation(value = "流程配置-所有流程")
    @PostMapping("/allFlow")
    public List<RobotFlow> allFlow() {
        return flowService.allFlow();
    }

    @ApiOperation(value = "关联/复制新增选择流程名称的接口")
    @PostMapping(value = "/showOneAppFlow")
    public List<RobotFlowVO> showOneAppFlow(@RequestParam(value = "appCode", required = false) String appCode) {
        return flowService.showFlowByAppCode(appCode);
    }

    @ApiOperation(value = "流程配置-自定义新增")
    @PostMapping("/add")
    @MyLog(moduleName = "流程配置", pageName = "流程配置-自定义新增流程", operation = "自定义新增流程")
    public int add(RobotFlowAddVO addVO) {
        return flowService.add(addVO);
    }

    @ApiOperation(value = "流程配置-复制新增")
    @PostMapping("/copyAdd")
    @MyLog(moduleName = "流程配置", pageName = "流程配置-复制新增流程", operation = "复制新增流程")
    public int copyAdd(@RequestParam(value = "flowCode") String flowCode, @RequestParam(value = "flowName") String flowName,
                       @RequestParam(value = "appCode") String appCode, @RequestParam(value = "templateFlowCode") String templateFlowCode,
                       @RequestParam(value = "tagCode") String tagCode,
                       @RequestParam(value = "serviceItem") Integer serviceItem,
                       @RequestParam(value = "declareSystem") String declareSystem,
                       @RequestParam(value = "runSupport") String runSupport) {
        return flowService.copyAdd(flowCode, flowName, appCode, templateFlowCode, tagCode, serviceItem,declareSystem,runSupport);
    }

    @ApiOperation(value = "流程配置-关联新增")
    @PostMapping("/relateAdd")
    @MyLog(moduleName = "流程配置", pageName = "流程配置-关联新增流程", operation = "关联新增流程")
    public int glAdd(@RequestParam(value = "flowCode") String flowCode, @RequestParam(value = "flowName") String flowName,
                     @RequestParam(value = "appCode") String appCode, @RequestParam(value = "tagCode") String tagCode,
                     @RequestParam(value = "serviceItem") Integer serviceItem, @RequestParam(value = "declareSystem") String declareSystem,
                     @RequestParam(value = "runSupport") String runSupport) {
        return flowService.glAdd(flowCode, flowName, appCode, tagCode, serviceItem, declareSystem, runSupport);
    }

    @ApiOperation(value = "编辑流程")
    @PostMapping(value = "/editFlow")
    @MyLog(moduleName = "流程配置", pageName = "流程配置-编辑流程", operation = "编辑流程")
    public int editFlow(@RequestBody RobotFlow robotFlow) {
        return flowService.editFlow(robotFlow);
    }

    @ApiOperation(value = "编辑流程状态")
    @PostMapping(value = "/editFlowStatus")
    @MyLog(moduleName = "流程配置", pageName = "流程配置-编辑流程状态", operation = "编辑流程状态")
    public int editFlowStatus(@RequestBody RobotFlowVO robotFlow) {
        return flowService.editFlowStatus(robotFlow);
    }

    @ApiOperation(value = "编辑流程排序")
    @PostMapping(value = "/editFlowOrder")
    @MyLog(moduleName = "流程配置", pageName = "流程配置-编辑流程排序", operation = "编辑流程排序")
    public int editFlowOrder(@RequestBody RobotFlowVO robotFlow) {
        return flowService.editFlowOrder(robotFlow);
    }

    @ApiOperation(value = "流程配置-上移/下移")
    @PostMapping("/update")
    @MyLog(moduleName = "流程配置", pageName = "流程配置-上移/下移流程", operation = "上移/下移流程")
    public int update(@RequestParam(value = "appCode") String appCode, @RequestParam(value = "flowName") String flowName, @RequestParam(value = "flag") int flag) {
        return flowService.updateOrder(appCode, flowName, flag);
    }

    @ApiOperation(value = "流程配置-删除")
    @PostMapping("/delete")
    @MyLog(moduleName = "流程配置", pageName = "流程配置-删除流程", operation = "删除流程")
    public int delete(@RequestParam(value = "flowCode") String flowCode) {
        return flowService.delete(flowCode);
    }

    @ApiOperation("编辑流程计划")
    @PostMapping(value = "/editFlowSchedule/{appCode}")
    @MyLog(moduleName = "流程配置", pageName = "流程配置-编辑流程计划", operation = "编辑流程计划")
    public int edit(@RequestBody RobotAppSchedule robotAppSchedule, @PathVariable(value = "appCode") String appCode) {
        return flowService.editSchedule(robotAppSchedule, appCode);
    }

    @ApiOperation("流程计划列表")
    @PostMapping("/flowScheduleList")
    public List<RobotFlowVO> flowTask(String appCode) {
        return flowService.listFlowSchedule(appCode);
    }

    @ApiOperation("添加计划")
    @PostMapping("/addFlowSchedule")
    @MyLog(moduleName = "流程配置", pageName = "流程配置-添加流程计划", operation = "添加流程计划")
    public int addTask(@RequestBody RobotAppSchedule robotAppSchedule, @RequestParam(value = "appCode") String appCode) {
        return flowService.addFlowSchedule(robotAppSchedule, appCode);
    }

    @ApiOperation("流程计划上移下移")
    @PostMapping("/upOrDownMove")
    @MyLog(moduleName = "流程配置", pageName = "流程配置-流程计划上移下移", operation = "流程计划上移下移")
    public int upOrDownMove(@RequestParam(value = "appCode") String appCode, @RequestParam("flowCode") String flowCode, @RequestParam("flag") int flag) {
        return flowService.upOrDownMove(appCode, flowCode, flag);
    }

    @ApiOperation("执行计划编辑回显")
    @PostMapping("/editEcho")
    public RobotFlowVO editEcho(String flowCode) {
        return flowService.editEcho(flowCode);
    }

    @ApiOperation("移除流程计划")
    @PostMapping(value = "/removeSchedule")
    @MyLog(moduleName = "流程配置", pageName = "流程配置-移除流程计划", operation = "移除流程计划")
    public int remove(String flowCode) {
        return flowService.removeSchedule(flowCode);
    }

    @ApiOperation("设置通用计划")
    @PostMapping("/updateGeneralPlan")
    @MyLog(moduleName = "流程配置", pageName = "流程配置-设置通用计划", operation = "设置通用计划")
    public int updateGeneralPlan(RobotGeneralPlan plan) {
        return flowService.updateGeneralPlan(plan);
    }

    @ApiOperation("通用计划回显")
    @PostMapping("/generalPlanEcho")
    public RobotGeneralPlan generalPlanEcho(String appCode) {
        return flowService.listGeneralPlanByAppCode(appCode);
    }

    @ApiOperation(value = "查询主流程")
    @PostMapping("/getScheduleFlow")
    public List<RobotFlowVO> getScheduleFlow(@RequestParam("appCode") String appCode) {
        return flowService.listScheduleFlow(appCode);
    }


    @ApiOperation("任务执行计划列表")
    @GetMapping("/taskScheduleList")
    public List<RobotFlowVO> taskScheduleList(@RequestParam(value = "taskCode") String taskCode, @RequestParam(value = "appCode") String appCode) {
        return flowService.taskScheduleList(taskCode, appCode);
    }

    @ApiOperation("添加任务执行计划")
    @PostMapping("/addTaskSchedule")
    @MyLog(moduleName = "流程配置", pageName = "流程配置-添加任务执行计划", operation = "添加任务执行计划")
    public int addTaskSchedule(@RequestBody RobotTaskSchedule taskSchedule, @RequestParam(value = "appCode") String appCode) {
        return flowService.addTaskSchedule(taskSchedule, appCode);
    }

    @ApiOperation("编辑任务执行计划")
    @PostMapping(value = "/editTaskSchedule/{appCode}")
    @MyLog(moduleName = "流程配置", pageName = "流程配置-编辑任务执行计划", operation = "编辑任务执行计划")
    public int editTaskSchedule(@RequestBody RobotTaskSchedule taskSchedule, @PathVariable(value = "appCode") String appCode) {
        return flowService.editTaskSchedule(taskSchedule, appCode);
    }

    @ApiOperation("移除任务执行计划")
    @PostMapping(value = "/removeTaskSchedule")
    @MyLog(moduleName = "流程配置", pageName = "流程配置-移除任务执行计划", operation = "移除任务执行计划")
    public int removeTaskSchedule(@RequestParam(value = "taskCode") String taskCode, @RequestParam(value = "flowCode") String flowCode) {
        return flowService.removeTaskSchedule(taskCode,flowCode);
    }

    @ApiOperation("回显任务执行计划")
    @PostMapping("/getTaskSchedule")
    public RobotFlowVO getTaskSchedule(@RequestParam(value = "taskCode") String taskCode, @RequestParam(value = "flowCode") String flowCode) {
        return flowService.getTaskSchedule(taskCode, flowCode);
    }

    @ApiOperation("任务执行计划上移下移")
    @PostMapping("/task/upOrDownMove")
    @MyLog(moduleName = "流程配置", pageName = "流程配置-任务执行计划上移下移", operation = "任务执行计划上移下移")
    public int upOrDownMoveTask(@RequestParam(value = "appCode") String appCode, @RequestParam(value = "taskCode") String taskCode,
                                @RequestParam("flowCode") String flowCode, @RequestParam("flag") int flag) {
        return flowService.upOrDownMoveTask(appCode, taskCode, flowCode, flag);
    }

    @ApiOperation("查询服务列表")
    @PostMapping("/getServiceList")
    public List<RobotServiceItem> upOrDownMoveTask(@RequestParam(value = "appCode") String appCode) {
        return flowService.selectServiceList(appCode);
    }
}
