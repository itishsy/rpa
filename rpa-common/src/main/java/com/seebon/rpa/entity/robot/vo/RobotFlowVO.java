package com.seebon.rpa.entity.robot.vo;

import com.seebon.rpa.entity.robot.RobotFlow;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;

/**
 * @author ZhenShijun
 * @dateTime 2022-11-28 11:05:24
 */
@Data
public class RobotFlowVO extends RobotFlow {

    /*执行周期*/
    private String execCycle;


    /*执行时区*/
    private String execAreaTime;

    /*执行方式 1：连续时间区间，2：固定时间，3：实时*/
    private Integer execStyle;

    /*计划类型 1：通用计划，2：自定义计划*/
    private Integer taskType;

    /*模板流程code*/
    private String templateFlowCode;

    /*状态，1：启用，0：停用 */
    private Integer scheduleStatus;

    /*应用名称 */
    private String appName;

    /*申报系统 */
    private String declareSystemName;
}
