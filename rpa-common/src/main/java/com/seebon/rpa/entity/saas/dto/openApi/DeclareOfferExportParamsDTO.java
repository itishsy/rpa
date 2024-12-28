package com.seebon.rpa.entity.saas.dto.openApi;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author ZhenShijun
 * @dateTime 2022-10-24 14:57:02
 */
@ApiModel("报盘导出传参信息DTO")
@Data
public class DeclareOfferExportParamsDTO {

    @ApiModelProperty("参保地名称，例：广州")
    private String addrName;

    @ApiModelProperty("业务类型，1：社保，2：公积金")
    private Integer businessType;

    @ApiModelProperty("申报类型，1：增员，2：减员，3：调基，5：补缴")
    private Integer declareType;

    @ApiModelProperty("申报数据操作类型")
    private String operationType;

    @ApiModelProperty("申报账户")
    private String accountNumber;

    @ApiModelProperty("模板类型，10007001：全险系统，10007002：养老系统，10007003：医疗系统，10007004：单工伤，10008001：公积金系统")
    private String tplTypeCode;

    @ApiModelProperty("申报状态，多个状态需用英文逗号隔开")
    private String declareStatus;

    @ApiModelProperty("投保年月,格式：yyyy-mm")
    private String tbDate;

    @ApiModelProperty("限制条数")
    private Integer limit;

    @ApiModelProperty("申报状态")
    private List<String> status;

    @ApiModelProperty("社保险种")
    private List<String> itemCodes;

    @ApiModelProperty("机器人执行状态")
    private Integer robotExecStatus;

    @ApiModelProperty("字段名称")
    private List<String> fieldNames;
}
