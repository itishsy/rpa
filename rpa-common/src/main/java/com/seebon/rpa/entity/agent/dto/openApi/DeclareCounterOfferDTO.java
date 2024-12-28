package com.seebon.rpa.entity.agent.dto.openApi;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * @author ZhenShijun
 * @dateTime 2022-11-01 17:05:26
 */
@ApiModel("申报回盘信息")
@Document(collection = "employee_declare_counter_offer_info")
@Data
public class DeclareCounterOfferDTO {

    @ApiModelProperty("id")
    private String id;

    @ApiModelProperty("回盘数据的外部id")
    private Integer outId;

    @ApiModelProperty("申报地名称")
    private String addrName;

    @ApiModelProperty("业务类型 1：申报，2：公积金")
    private Integer businessType;

    @ApiModelProperty("申报类型 1：增员，2：减员，3：调基，5：补缴")
    private Integer declareType;

    @ApiModelProperty("申报账户")
    private String accountNumber;

    @ApiModelProperty("人员证件号码")
    private String idCard;

    @ApiModelProperty("申报年月，增员、减员、调基传申报月份，补缴传实际补缴的月份区间，用逗号隔开，例：2022-07,2022-09")
    private String declareMonth;

    @ApiModelProperty("系统类型，10007001：全险系统，10007002：养老系统，10007003：医疗系统，10007004：单工伤，10008001：公积金系统")
    private String tplTypeCode;

    @ApiModelProperty("申报状态，1：待申报，2：申报中，4：申报成功，5：申报失败")
    private Integer declareStatus;

    @ApiModelProperty("机器人执行状态，0：未执行，1：已执行，2：执行成功")
    private Integer robotExecStatus;

    @ApiModelProperty("失败类型，1：全部失败，2：部分失败")
    private Integer failType;

    @ApiModelProperty("数据类型，1：基本（默认），2：补充败")
    private Integer dataType;

    @ApiModelProperty("申报失败原因")
    private String failReason;

    @ApiModelProperty("回盘状态1：回盘成功，2：回盘失败")
    private Integer counterOfferStatus = 0;

    @ApiModelProperty("回盘失败原因")
    private String counterOfferFailReason;

    @ApiModelProperty("回盘批次号")
    private String batchNumber;

    @ApiModelProperty("回盘时间")
    private Date counterOfferTime;

    @ApiModelProperty("申报数据操作类型")
    private String operationType;

    @ApiModelProperty("节点结果备注")
    private String nodeComment;

    @ApiModelProperty("保持上次失败原因，1：是，0或null不维持")
    private Integer keepLastFailReason;

    @ApiModelProperty("机器人执行批次号")
    private String robotExecCode;
}
