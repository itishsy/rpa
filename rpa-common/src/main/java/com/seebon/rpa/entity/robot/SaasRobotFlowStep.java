package com.seebon.rpa.entity.robot;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.seebon.rpa.entity.robot.vo.RobotFlowStepArgsVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jdk.nashorn.internal.ir.annotations.Ignore;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;
import java.util.List;

/**
 * @author : 阿祥
 * @desc :
 * @date : 2023/10/23  11:19
 */
@ApiModel("sass-机器人步骤")
@Data
@Table(name = "saas_robot_flow_step")
public class SaasRobotFlowStep {
    public static final String SKIP_TO = "skipTo";
    public static final String FAILED_SKIP_TO = "failedSkipTo";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    @ApiModelProperty("步骤名称")
    private String stepName;

    @Column
    @ApiModelProperty("层级")
    private Integer level;

    @Column
    @ApiModelProperty("启用状态 1启用 0禁用")
    private Integer status = 1;

    @Column
    @ApiModelProperty("指令编码")
    private String actionCode;

    @Column
    @ApiModelProperty("行为参数值")
    private String actionArgs;

    @Column
    @ApiModelProperty("目标参数值")
    private String targetArgs;

    @Column
    @ApiModelProperty("默认执行后等待时间2s")
    private Integer waitAfter = 2;

    @Column
    @ApiModelProperty("执行序号")
    private Integer number;

    @Column
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @Column
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @Column
    @ApiModelProperty("创建人id")
    private Integer createId;

    @Column
    @ApiModelProperty("更新人id")
    private Integer updateId;

    @Column
    @ApiModelProperty("操作记录id")
    private Integer sysDesktopApplicationsId;

    @ApiModelProperty("行为参数")
    @Transient
    private List<RobotFlowStepArgsVO> actionArgsVOS;

    @ApiModelProperty("目标参数")
    @Transient
    private List<RobotFlowStepArgsVO> targetArgsVOS;
}
