package com.seebon.rpa.entity.saas.dto.declare;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @author ZhenShijun
 * @dateTime 2023-01-30 16:31:55
 */
@ApiModel("实缴字段配置DTO")
@Data
public class PaidInFieldDTO {

    @ApiModelProperty("本地实缴字段")
    private Integer id;

    @ApiModelProperty("本地实缴字段")
    private String localPaidInField;

    @ApiModelProperty("网站实缴字段")
    private String webPaidInField;

    @ApiModelProperty("业务类型（1--社保；2--公积金）")
    private Integer businessType;

    @ApiModelProperty("参保城市id")
    private String address;

    @ApiModelProperty("参保城市名称")
    private String addressName;

    @ApiModelProperty("状态（1--启用；2--停用）")
    private Integer status;

    @ApiModelProperty("本地实缴字段名称")
    private String fieldName;

    @ApiModelProperty("本地实缴字段对应实体类属性名")
    private String entityFieldName;

    @ApiModelProperty("是否展示")
    private Integer requireShow;

}
