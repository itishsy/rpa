package com.seebon.rpa.entity.agent.dto.ai;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * TODO
 *
 * @author zjf
 * @describe
 * @date 2023-08-31 10:14
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AiRequestDTO{
    @ApiModelProperty("字段名")
    private String columnName;

    @ApiModelProperty("字段类型")
    private String columnType;

    @ApiModelProperty("字段类型名字")
    private String columnTypeName;

    @ApiModelProperty("是否必填。1：不是；2：是")
    private Boolean isRequire;

    @ApiModelProperty("日期格式")
    private String columnFormat;

    @ApiModelProperty("下拉值")
    private List<String> optionalValueList;

    @ApiModelProperty("条件字段")
    private String conclusion;

    private String matterType;

    @ApiModelProperty("默认值")
    private String defaultValue;

    @ApiModelProperty("字段编码")
    private String fieldCode;

    @ApiModelProperty("行政区划返回格式：1：国标.名称，2：国标-名称，3：国标_名称，4：国标 名称，5国标，6：名称.国标，7：名称-国标，:8：名称_国标，9：名称 国标,10:国标名称,11:名称国标,12:国标--名称,13:名称--国标")
    private String divisionsFormat;

    @ApiModelProperty("字段备注信息")
    private String comment;
}
