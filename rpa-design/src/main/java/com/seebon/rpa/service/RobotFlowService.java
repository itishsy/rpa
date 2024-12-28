package com.seebon.rpa.service;

import com.seebon.rpa.entity.robot.*;
import com.seebon.rpa.entity.robot.dto.design.RobotFlowAddVO;
import com.seebon.rpa.entity.robot.dto.design.RobotFlowVO;
import com.seebon.rpa.entity.robot.vo.RobotFlowStatusHistoryVO;

import java.util.List;

public interface RobotFlowService {

    /**
     * 查询机器人流程列表
     *
     * @param flowVO
     * @return
     */
    List<RobotFlowVO> flowList(RobotFlowVO flowVO);

    /**
     * 统计流程状态数量
     *
     * @param flowVO
     * @return
     */
    List<RobotFlowVO> totalStatus(RobotFlowVO flowVO);

    /**
     * 流程状态历史
     *
     * @param flowVO
     * @return
     */
    List<RobotFlowStatusHistoryVO> statusHistory(RobotFlowVO flowVO);

    /**
     * 模板列表
     *
     * @return
     */
    List<RobotFlowVO> listTemplate(Integer templateType, String flowType);

    /**
     * 机器人流程-自定义新增
     *
     * @param flowVO
     * @return
     */
    int add(RobotFlowAddVO addVO);

    /**
     * 机器人流程-复制新增
     *
     * @param flowName
     * @return
     */
    int copyAdd(String flowCode, String flowName, String appCode, String templateFlowCode, String tagCode, Integer serviceItem, String declareSystem, String runSupport);

    /**
     * 机器人流程-关联新增
     *
     * @param flowCode
     * @return
     */
    int glAdd(String flowCode, String flowName, String appCode, String tagCode, Integer serviceItem, String declareSystem, String runSupport);

    int editFlow(RobotFlow robotFlow);

    int editFlowStatus(RobotFlowVO robotFlow);

    int editFlowOrder(RobotFlowVO robotFlow);

    /**
     * 更新机器人流程执行顺序(上移/下移)
     *
     * @param appCode
     * @param flowName
     * @param flag
     * @return
     */
    int updateOrder(String appCode, String flowName, int flag);


    /**
     * 删除机器人流程
     *
     * @param flowCode
     * @return
     */
    int delete(String flowCode);

    List<RobotFlow> allFlow();

    /**
     * 关联/复制新增选择流程名称的方法
     *
     * @param appCode
     * @return
     */
    List<RobotFlowVO> showFlowByAppCode(String appCode);

    /**
     * 机器人流程编辑
     */
    int editSchedule(RobotAppSchedule robotAppSchedule, String appCode);

    /**
     * 流程计划列表
     *
     * @param appCode
     * @return
     */
    List<RobotFlowVO> listFlowSchedule(String appCode);

    int addFlowSchedule(RobotAppSchedule robotAppSchedule, String appCode);

    /**
     * 流程计划上移下移
     *
     * @param appCode
     * @param flowCode
     * @param flag
     * @return
     */
    int upOrDownMove(String appCode, String flowCode, Integer flag);

    /**
     * 流程计划编辑页面回显
     *
     * @param flowCode
     * @return
     */
    RobotFlowVO editEcho(String flowCode);

    /**
     * 移除流程计划
     */
    int removeSchedule(String flowCode);

    /**
     * 设置通用计划
     */
    int updateGeneralPlan(RobotGeneralPlan robotGeneralPlan);

    /**
     * 通用计划回显
     */
    RobotGeneralPlan listGeneralPlanByAppCode(String appCode);


    List<RobotFlowVO> listScheduleFlow(String appCode);


    /**
     * 任务流程计划列表
     */
    List<RobotFlowVO> taskScheduleList(String taskCode, String appCode);

    /**
     * 添加任务执行计划
     */
    int addTaskSchedule(RobotTaskSchedule taskSchedule, String appCode);

    /**
     * 机器人流程编辑
     */
    int editTaskSchedule(RobotTaskSchedule taskSchedule, String appCode);

    /**
     * 移除任务执行计划
     */
    int removeTaskSchedule(String taskCode, String flowCode);

    /**
     * 流程计划编辑页面回显
     */
    RobotFlowVO getTaskSchedule(String taskCode, String flowCode);

    /**
     * 任务执行计划上移下移
     */
    int upOrDownMoveTask(String appCode, String taskCode,String flowCode, Integer flag);

    /**
     * 查询服务列表
     */
    List<RobotServiceItem> selectServiceList(String appCode);
}
