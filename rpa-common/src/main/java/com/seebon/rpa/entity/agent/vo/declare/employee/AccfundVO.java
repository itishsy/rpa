package com.seebon.rpa.entity.agent.vo.declare.employee;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class AccfundVO {
    /*公积金参保状态*/
    @ApiModelProperty("公积金参保状态")
    private String accfundStatus;


    @ApiModelProperty("参保地id")
    private Integer addrId;

    @ApiModelProperty("参保地名字")
    private String addrName;

    @ApiModelProperty("户口性质")
    private String residenceType;


    @ApiModelProperty("个人公积金账号")
    private String accfundnum;

    @ApiModelProperty("公积金缴纳详情")
    private AccfundStatusVO accfundStatusVOS;
}
