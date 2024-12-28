package com.seebon.rpa.entity.robot.dto.monitor;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@ApiModel("今日执行查询条件信息")
@Data
public class TodayExecQueryDTO implements Serializable {

    @ApiModelProperty("参保城市id")
    private Integer addrId;

    @ApiModelProperty("客户id")
    private Integer custId;

    @ApiModelProperty("申报账户（任务编码）")
    private String taskCode;

    @ApiModelProperty("上线阶段")
    private Integer state;

    @ApiModelProperty("开发人员")
    private String devUserName;

    @ApiModelProperty("测试人员")
    private String testUserName;

    @ApiModelProperty("运维人员")
    private String ywUserName;

    private List<String> appArgs;

}
