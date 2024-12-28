package com.seebon.rpa.entity.robot;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Table(name = "robot_task_notice")
public class RobotTaskNotice implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ApiModelProperty(value = "执行编码")
    @Column(name = "execution_code")
    private String executionCode;

    @ApiModelProperty(value = "客户id")
    @Column(name = "client_id")
    private Integer clientId;

    @ApiModelProperty(value = "机器标识码")
    @Column(name = "machine_code")
    private String machineCode;

    @ApiModelProperty(value = "任务编码")
    @Column(name = "task_code")
    private String taskCode;

    @ApiModelProperty(value = "单位名称")
    @Column(name = "company_name")
    private String companyName;

    @ApiModelProperty(value = "申报账户")
    @Column(name = "account_number")
    private String accountNumber;

    @ApiModelProperty(value = "缴费时间")
    @Column(name = "pay_time")
    private Date payTime;

    @ApiModelProperty(value = "停用时间")
    @Column(name = "stop_time")
    private Date stopTime;

    @ApiModelProperty(value = "恢复时间")
    @Column(name = "regain_time")
    private Date regainTime;

    @ApiModelProperty(value = "恢复状态：1-已恢复，0-未恢复")
    @Column(name = "regain_status")
    private Integer regainStatus;

    @ApiModelProperty(value = "异常信息")
    private String error;

    @ApiModelProperty(value = "创建时间")
    @Column(name = "create_time")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @Column(name = "update_time")
    private Date updateTime;
}