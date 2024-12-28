package com.seebon.rpa.entity.agent.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel("运营后台首页服务员工-服务数据统计信息DTO")
@Data
public class DeclareStatisticsDTO implements Serializable {

    @ApiModelProperty("服务员工总数")
    private Integer serviceEmpCount = 0;

    @ApiModelProperty("当前在册")
    private Integer registerCount = 0;

    @ApiModelProperty("今日增加")
    private Integer todayRegisterAdd = 0;

    @ApiModelProperty("服务数据总数")
    private Integer serviceCount = 0;

    @ApiModelProperty("申报成功")
    private Integer declareSuccess = 0;

    @ApiModelProperty("申报失败")
    private Integer declareFail = 0;

    @ApiModelProperty("今日申报")
    private Integer todayDeclare = 0;


}
