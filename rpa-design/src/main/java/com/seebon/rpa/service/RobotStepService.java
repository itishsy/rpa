package com.seebon.rpa.service;

import com.seebon.rpa.entity.robot.RobotArgsDefine;
import com.seebon.rpa.entity.robot.RobotFlowStep;
import com.seebon.rpa.entity.robot.dto.design.RobotStepAppDescribeVO;
import com.seebon.rpa.entity.robot.vo.RobotFlowStepVO;

import java.util.List;

public interface RobotStepService {

    /**
     * 查询机器人流程的所有步骤
     * @param flowCode 流程编码
     * @param stepCode 步骤编码，步骤为代码块时传入。
     * @param templateFlowCode 流程引用的模板。
     * @return
     */
    List<RobotFlowStepVO> list(String flowCode, String stepCode, String templateFlowCode);

    /**
     * 机器人流程步骤保存(新增/修改)
     *
     * @param steps
     * @return
     */
    boolean save(List<RobotFlowStep> steps, String flowCode, String stepCode, String templateFlowCode);

    /**
     * 根据flowCode查询有多少个步骤
     *
     * @param flowCode
     * @return
     */
    int countByCode(String flowCode);

    /**
     * 添加步骤
     *
     * @param robotFlowStep
     * @return
     */
    boolean add(RobotFlowStep robotFlowStep);

    /**
     * 参数定义
     *
     * @param actionCode
     * @return
     */
    List<RobotArgsDefine> findArgs(String type, String actionCode);

    /**
     * 机器人流程配置字段
     */
    RobotStepAppDescribeVO listDescribe(Integer id);

}
