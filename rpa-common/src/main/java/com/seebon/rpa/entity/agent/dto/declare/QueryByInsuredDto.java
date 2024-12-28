package com.seebon.rpa.entity.agent.dto.declare;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @author lsh
 * @dateTime 2022-04-21
 */
@ApiModel("投保动态参数查询Dto")
@Data
public class QueryByInsuredDto {

    /*姓名*/
    @ApiModelProperty("姓名")
    private String name;

    /*证件号码*/
    @ApiModelProperty("证件号码")
    private String idCard;

    @ApiModelProperty("参保地id")
    private Integer addrId;

    @ApiModelProperty("业务类型名称 社保/公积金")
    private Integer bussinssTypeName;

    @ApiModelProperty("申报类型名称  增员/减员/调基/变更/补缴")
    private Integer changeTypeName;


}
