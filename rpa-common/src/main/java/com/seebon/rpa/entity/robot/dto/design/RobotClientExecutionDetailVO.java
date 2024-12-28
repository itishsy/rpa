package com.seebon.rpa.entity.robot.dto.design;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import java.util.Date;
import java.util.List;

@ApiModel("机器人执行明细")
@Data
public class RobotClientExecutionDetailVO {

    private Integer id;

    /**
     * 执行编码
     */
    @Column
    @ApiModelProperty(value = "执行编码")
    private String executionCode;

    /**
     * 流程编码
     */
    @Column
    @ApiModelProperty(value = "流程编码")
    private String flowCode;
    /**
     * 步骤编码
     */
    @Column
    @ApiModelProperty(value = "步骤编码")
    private String stepCode;

    @Column
    @ApiModelProperty(value = "流程类型 1--主流程  2--子流程")
    private Integer flowType;
    /**
     * 步骤名称
     */
    @Column
    @ApiModelProperty(value = "步骤名称")
    private String stepName;
    /**
     * 开始时间
     */
    @Column
    @ApiModelProperty(value = "开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    /**
     * 结束时间
     */
    @Column
    @ApiModelProperty(value = "结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;
    /**
     * 状态
     */
    @Column
    @ApiModelProperty(value = "状态")
    private Integer status;
    /**
     * 异常信息
     */
    @Column
    @ApiModelProperty(value = "异常信息")
    private String error;

    /**
     * 步骤截图文件
     */
    @ApiModelProperty(value = "步骤截图文件")
    private List<RobotExecutionFileInfo> robotExecutionFileInfos;

    @ApiModelProperty(value = "子流程步骤")
    private List<RobotClientExecutionDetailVO> detailVOS;
}
