package com.seebon.rpa.entity.robot;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * RobotLoginAuth 实体类映射数据库表 robot_login_auth
 */
@Data
@ApiModel(value = "客户机器人集中登录信息")
@Table(name = "robot_login_auth")
public class RobotLoginAuth implements Serializable {

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
    @ApiModelProperty(value = "机器标识码（指定盒子编码或执行盒子编码）")
    private String machineCode;

    @Column
    @ApiModelProperty(value = "任务编码")
    private String taskCode;

    @Column
    @ApiModelProperty(value = "申报账户")
    private String declareAccount;

    @Column
    @ApiModelProperty(value = "申报系统")
    private String declareSystem;

    @Column
    @ApiModelProperty(value = "执行事项，1：增减员（增员、减员、调基、补缴），6：缴费，7：在册名单，8：费用明细，9：政策通知 10：基数申报 11：登录 12：查审核结果")
    private Integer queueItem;

    @Column
    @ApiModelProperty(value = "业务类型  1社保 2公积金")
    private Integer businessType;

    @Column
    @ApiModelProperty(value = "登录方式")
    private String loginType;

    @Column
    @ApiModelProperty(value = "通知号码")
    private String phoneNumber;

    @Column
    @ApiModelProperty(value = "登录时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date loginTime;

    @Column
    @ApiModelProperty(value = "处理状态 0待登录 1已登录")
    private Integer processStatus;

    @Column
    @ApiModelProperty(value = "登录状态 0未登录 1维持中 2已失效")
    private Integer loginStatus;

    @Column
    @ApiModelProperty(value = "操作来源   1:定时任务  2:手动任务 ")
    private Integer source;

    @Column
    @ApiModelProperty(value = "创建时间 / 记录生成时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @Column
    @ApiModelProperty(value = "创建人")
    private Integer createId;

    @Column
    @ApiModelProperty(value = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    @Column
    @ApiModelProperty(value = "更新人")
    private Integer updateId;

    @Column
    @ApiModelProperty("备注")
    private String comment;

    @Column
    @ApiModelProperty("有效状态  0有效 1无效（不显示）")
    private Integer isDelete;

}
