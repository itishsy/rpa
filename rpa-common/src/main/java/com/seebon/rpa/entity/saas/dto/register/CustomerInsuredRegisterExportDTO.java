package com.seebon.rpa.entity.saas.dto.register;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CustomerInsuredRegisterExportDTO implements Serializable {

    @ApiModelProperty("参保时间，yyyy-MM")
    private String dataMonth;

    @ApiModelProperty("参保城市id")
    private Integer addrId;

    @ApiModelProperty("申报单位id（查询条件）")
    private Integer orgId;

    @ApiModelProperty("排序类型")
    private String sort;

    @ApiModelProperty("排序字段")
    private String sidx;

    @ApiModelProperty("申报单位ids（列表选择）")
    private List<Integer> orgIds;

    @ApiModelProperty("导出数据类型，1：在户名单，2：费用明细，3：在户名单原始表，4：费用明细原始表")
    private List<Integer> dataTypes;

}
