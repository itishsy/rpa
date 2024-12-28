package com.seebon.rpa.entity.agent.dto.ai;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class AiConstantColumnDTO{

    @ApiModelProperty("字段名")
    private String columnName;

    @ApiModelProperty("字段值")
    private String columnValue;

    @ApiModelProperty("字段类型")
    private String columnType;

    @ApiModelProperty("是否必填")
    private Boolean isRequire;

    @ApiModelProperty("字段格式")
    private String columnFormat;

    @ApiModelProperty("可选范围")
    private List<Object> optionalValueList;

    @ApiModelProperty("是否标红")
    private boolean isRed=false;

    @ApiModelProperty("默认值")
    private String defaultValue;

    @ApiModelProperty("字段编码")
    private String fieldCode;

    @ApiModelProperty("行政区划返回格式：1：国标.名称，2：国标-名称，3：国标_名称，4：国标 名称，5国标，6：名称.国标，7：名称-国标，:8：名称_国标，9：名称 国标,10:国标名称,11:名称国标,12:国标--名称,13:名称--国标")
    private String divisionsFormat;

    @ApiModelProperty("解析出来省市区map")
    private Map<String,Object> explainCity;

    @ApiModelProperty("字段备注信息")
    private String comment;

}
