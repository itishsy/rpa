package com.seebon.rpa.entity.saas.vo.social;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel("导出采集表")
@Data
public class ExportTableVO {

    @ApiModelProperty("客户ID")
    private Integer custId;

    @ApiModelProperty("身份证")
    private String idCard;

    @ApiModelProperty("姓名")
    private String name;

    private List<Integer> pinkunIds;

    private List<Integer> shiyeIds;

    @ApiModelProperty("身份证(多选)")
    private List<String> idCards;
}
