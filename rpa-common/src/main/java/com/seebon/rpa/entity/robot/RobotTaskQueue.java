package com.seebon.rpa.entity.robot;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel(value = "客户机器人任务执行队列")
@Table(name = "robot_task_queue")
public class RobotTaskQueue implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "主键Id")
    private Integer id;

    @Column
    @ApiModelProperty(value = "客户ID")
    private Integer clientId;

    @Column
    @ApiModelProperty(value = "执行编码")
    private String executionCode;

    @Column
    @ApiModelProperty(value = "执行盒子编码")
    private String machineCode;

    @Column
    @ApiModelProperty(value = "分配盒子编码")
    private String fixMachineCode;

    @Column
    @ApiModelProperty(value = "应用编码")
    private String appCode;

    @Column
    @ApiModelProperty(value = "任务编码")
    private String taskCode;

    @Column
    @ApiModelProperty(value = "申报系统 字典 10007,10008")
    private String declareSystem;

    @Column
    @ApiModelProperty(value = "申报账户")
    private String declareAccount;

    @Column
    @ApiModelProperty(value = "申报单位")
    private String companyName;

    @Column
    @ApiModelProperty(value = "执行事项，1：增减员（增员、减员、调基、补缴），6：缴费，7：在册名单，8：费用明细，9：政策通知 10：基数申报 11：登录 12：查审核结果")
    private Integer queueItem;

    @Column
    @ApiModelProperty(value = "指定盒子规则Id(表：robot_client_rule的id)")
    private Integer ruleId;

    @Column
    @ApiModelProperty(value = "执行优先级")
    private Integer sort;

    @Column
    @ApiModelProperty(value = "排序规则：1-H5认证 2-配置优先城市 3-ukey认证 4-账号密码认证")
    private Integer sortRule;

    @Column
    @ApiModelProperty(value = "运行标识  1-执行中  2-待执行 3-执行中断 4-执行成功")
    private Integer status;

    @Column
    @ApiModelProperty(value = "通知手机号码")
    private String phoneNumber;

    @Column
    @ApiModelProperty(value = "登录状态 -1-待登录 0-开始登录 1-登录成功 2-登录失败")
    private Integer loginStatus;

    @Column
    @ApiModelProperty(value = "登录方式")
    private String loginType;

    @Column
    @ApiModelProperty(value = "登录时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date loginTime;

    @Column
    @ApiModelProperty(value = "类型：1-固定盒子，2-可调度盒子")
    private Integer type;

    @Column
    @ApiModelProperty(value = "业务类型 1：社保，2：公积金")
    private Integer businessType;

    @Column
    @ApiModelProperty(value = "任务来源：1-自动任务 2-手动任务")
    private Integer source;

    @Column
    @ApiModelProperty(value = "同步标识  0-未同步  1-已同步")
    private Integer sync;

    @Column
    @ApiModelProperty(value = "调试备注")
    private String comment;

    @Column
    @ApiModelProperty(value = "预计时长(单位：分)")
    private Long preTime;

    @Column
    @ApiModelProperty(value = "预计开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date preStartTime;

    @Column
    @ApiModelProperty(value = "预计结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date preEndTime;

    @Column
    @ApiModelProperty(value = "实际时长(单位：分)")
    private Long praTime;

    @Column
    @ApiModelProperty(value = "实际开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date praStartTime;

    @Column
    @ApiModelProperty(value = "实际结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date praEndTime;

    @Column
    @ApiModelProperty(value = "创建人id")
    private Integer createId;

    @Column
    @ApiModelProperty(value = "创建人名称")
    private String createName;

    @Column
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @Column
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}
