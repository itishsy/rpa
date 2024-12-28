package com.seebon.rpa.entity.robot.dto;

import lombok.Data;

import java.util.List;

/**
 * 任务流程状态变更
 */
@Data
public class RobotTaskStatusChangeDTO {

    /**
     * 流程appCode
     */
    private String appCode;

    /**
     * 任务编号
     */
    private String taskCode;

    /**
     * 状态 0停用 1启用
     */
    private Integer status;

    /**
     * 原因，停用必填
     */
    private String comment;

}
