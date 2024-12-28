package com.seebon.rpa.entity.robot.dto.design;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author ZhenShijun
 * @dateTime 2022-04-11 18:31:25
 */
@ApiModel("申报账户基本信息VO")
@Data
public class DeclareAccountBaseVo  {


    @ApiModelProperty("地区id")
    private Integer addressId;

    @ApiModelProperty("申报账号总数")
    private Integer accountCount;

    @ApiModelProperty("组织")
    private String orgName;

    @ApiModelProperty("组织")
    private String addressName;



}
