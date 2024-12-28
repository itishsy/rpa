package com.seebon.rpa.entity.robot;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * 机器人执行数据
 */
@Data
@Table(name = "robot_execution_data")
@ApiModel(value = "机器人执行记录")
public class RobotExecutionData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "client_id")
    @ApiModelProperty(value = "客户ID")
    private Integer clientId;

    @Column(name = "machine_code")
    @ApiModelProperty(value = "机器标识码")
    private String machineCode;

    @Column(name = "uuid")
    @ApiModelProperty(value = "唯一编码")
    private String uuid;

    @Column(name = "execution_code")
    @ApiModelProperty(value = "执行编码")
    private String executionCode;

    @Column(name = "batch_code")
    @ApiModelProperty(value = "批次码")
    private String batchCode;

    @Column(name = "addrName")
    @ApiModelProperty(value = "申报地")
    private String addrName;

    @Column(name = "businessType")
    @ApiModelProperty(value = "业务类型 1：社保，2：公积金")
    private Integer businessType;

    @Column(name = "declareType")
    @ApiModelProperty(value = "申报类型 1：增员，2：减员，3：调基，5：补缴")
    private Integer declareType;

    @Column(name = "dataType")
    @ApiModelProperty(value = "数据类型 1：基本，2：补充")
    private Integer dataType;

    @Column(name = "flagType")
    @ApiModelProperty(value = "标记类型 1：新流程，2：旧流程")
    private Integer flagType;

    @Column(name = "operationType")
    @ApiModelProperty(value = "申报数据操作类型 （参考表 policy_declare_operation_type_dict）")
    private Integer operationType;

    @Column(name = "nodeComment")
    @ApiModelProperty(value = "节点结果备注")
    private String nodeComment;

    @Column(name = "accountNumber")
    @ApiModelProperty(value = "申报账户")
    private String accountNumber;

    @Column(name = "name")
    @ApiModelProperty(value = "人员姓名")
    private String name;

    @Column(name = "idCard")
    @ApiModelProperty(value = "身份证号")
    private String idCard;

    @Column(name = "declareMonth")
    @ApiModelProperty(value = "申报年月，增员、减员、调基传申报月份，补缴传实际补缴的月份区间，用逗号隔开，例：2022-07,2022-09")
    private String declareMonth;

    @Column(name = "extend")
    @ApiModelProperty(value = "扩展字段")
    private String extend;

    @Column(name = "tplTypeCode")
    @ApiModelProperty(value = "系统类型，10007001：全险系统，10007002：养老系统，10007003：医疗系统，10007004：单工伤，10008001：公积金系统")
    private String tplTypeCode;

    @Column(name = "declareStatus")
    @ApiModelProperty(value = "申报状态，1：待申报，2：申报中，4：申报成功，5：申报失败")
    private Integer declareStatus;

    @Column(name = "robotExecStatus")
    @ApiModelProperty(value = "机器人执行状态，0：未执行，1：已执行，2：执行成功")
    private Integer robotExecStatus;

    @Column(name = "failType")
    @ApiModelProperty(value = "失败类型：1-全部失败,2-部分失败")
    private Integer failType;

    @Column(name = "failReason")
    @ApiModelProperty(value = "申报失败原因；如果部分失败，则需要列出具体的失败险种和成功的险种，没有列出的险种不做回盘([{\\\"itemName\\\":\\\"养老\\\",\\\"status\\\":5,\\\"failReason\\\":\\\"失败原因\\\"}])")
    private String failReason;

    @Column(name = "sync")
    @ApiModelProperty(value = "同步状态（0--未同步；1--已同步）")
    private Integer sync;

    @Column(name = "syncFailReason")
    @ApiModelProperty(value = "同步失败原因")
    private String syncFailReason;

    @Column(name = "syncTime")
    @ApiModelProperty(value = "同步时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date syncTime;

    @Column(name = "createTime")
    @ApiModelProperty(value = "创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}
