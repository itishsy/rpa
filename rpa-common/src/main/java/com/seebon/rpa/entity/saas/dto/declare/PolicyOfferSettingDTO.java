package com.seebon.rpa.entity.saas.dto.declare;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author ZhenShijun
 * @dateTime 2022-09-27 18:33:14
 */
@ApiModel("报盘规则信息DTO")
@Data
public class PolicyOfferSettingDTO {

    @ApiModelProperty("uuid")
    private String uuid;

    @ApiModelProperty("参保城市名称")
    private String addrName;

    @ApiModelProperty("业务类型")
    private Integer businessType;

    @ApiModelProperty("业务类型名称")
    private String businessTypeName;

    @ApiModelProperty("申报类型")
    private Integer declareType;

    @ApiModelProperty("申报类型名称")
    private String declareTypeName;

    @ApiModelProperty(value="版本号")
    private String versions;

    @ApiModelProperty(value="版本说明")
    private String explainV;

    @ApiModelProperty("申报注意事项")
    private String declareItem;

    @ApiModelProperty("自动解析户籍省市区 1：是，0：否")
    private Integer autoParse;

    @ApiModelProperty("行政区划返回格式：1：国标.名称，2：国标-名称，3：国标_名称，4：国标 名称，5国标，6：名称.国标，7：名称-国标，:8：名称_国标，9：名称 国标,10:国标名称,11:名称国标,12:国标--名称,13:名称--国标")
    private Integer divisionsFormat;

    @ApiModelProperty("字段规则信息")
    private List<PolicyOfferColumnSettingDTO> policyFields;

}
