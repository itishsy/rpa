package com.seebon.rpa.entity.robot.dto.design;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@ApiModel("机器人执行明细")
@Data
public class RobotClientExecutionVO {

    /**
     * 任务编码
     */
    @ApiModelProperty(value = "任务编码")
    private String taskCode;

    @ApiModelProperty(value = "执行编码")
    private String executionCode;


    /**
     * 流程编码
     */
    @ApiModelProperty(value = "流程编码")
    private String flowCode;
    /**
     * 开始时间
     */
    @ApiModelProperty(value = "开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    /**
     * 结束时间
     */
    @ApiModelProperty(value = "结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;
    /**
     * 状态
     */
    @ApiModelProperty(value = "状态")
    private Integer status;
    /**
     * 异常信息
     */
    @ApiModelProperty(value = "异常信息")
    private String error;

    /**
     * 执行明细
     */
    @ApiModelProperty(value = "执行明细")
    private List<RobotClientExecutionDetailVO> executionDetailVOS;

}
