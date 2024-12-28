package com.seebon.rpa.entity.agent.dto.openApi;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author ZhenShijun
 * @dateTime 2022-10-18 20:16:03
 */
@ApiModel("接口录入数据申报状态返回信息")
@Data
public class EmployeeDeclareResutDTO {

    @ApiModelProperty("批次号")
    private String batchNumber;

    @ApiModelProperty("参保地名称")
    private String addrName;

    @ApiModelProperty("业务类型")
    private Integer businessType;

    @ApiModelProperty("申报类型")
    private Integer declareType;

    @ApiModelProperty("员工姓名")
    private String name;

    @ApiModelProperty("员工证件号码")
    private String idCard;

    @ApiModelProperty("报盘信息")
    private Map<String, String> declareInfo;

    @ApiModelProperty("申报状态")
    private String status = "待申报";

    @ApiModelProperty("入库状态 1：已入库，0：未入库")
    private Integer warehousingStatus = 0;

    @ApiModelProperty("失败信息")
    private String errorMsg;

    @ApiModelProperty("失败类型，0：基础校验失败，1：申报失败")
    private Integer failType;

    @ApiModelProperty("备注说明")
    private String explain;

    @ApiModelProperty("险种申报明细信息")
    private List<Map<String, String>> itemDetails;

    @ApiModelProperty("补缴申报明细信息")
    private Map<String, Object> suppItemDetails;

}
