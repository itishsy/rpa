package com.seebon.rpa.entity.robot.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class CheckDeclareDataVO {

    @ApiModelProperty(value = "客户ID")
    private List<Integer> clientIds;

    @ApiModelProperty(value = "社保申报账户")
    private List<String> socialAccountList;

    @ApiModelProperty(value = "公积金申报账户")
    private List<String> accfundAccountList;
}
