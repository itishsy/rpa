package com.seebon.rpa.entity.robot.vo;

import com.seebon.rpa.entity.robot.RobotLoginAuth;
import com.seebon.rpa.entity.robot.RobotTaskQueue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "客户机器人集中登录信息")
public class RobotLoginAuthVO extends RobotLoginAuth {


    @ApiModelProperty(value = "参保城市")
    private String addrName;

    @ApiModelProperty(value = "参保城市Id")
    private Integer addrId;

    @ApiModelProperty(value = "应用编码")
    private String appCode;

    @ApiModelProperty(value = "申报单位")
    private String companyName;

    @ApiModelProperty(value = "申报账户")
    private String accountNumber;

    @ApiModelProperty(value = "执行事项名")
    private String queueItemName;

    @ApiModelProperty(value = "业务类型 1：申报，2：公积金")
    private String businessTypeName;

    @ApiModelProperty(value = "申报系统名")
    private String declareSystemName;

    @ApiModelProperty(value = "应用参数值")
    private String appArgs;

    @ApiModelProperty(value = "客户名")
    private String customerName;

    @ApiModelProperty("队列信息")
    private RobotTaskQueue robotTaskQueue;

}
