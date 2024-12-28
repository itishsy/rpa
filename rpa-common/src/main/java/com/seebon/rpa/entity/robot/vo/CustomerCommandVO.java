package com.seebon.rpa.entity.robot.vo;

import com.seebon.rpa.entity.robot.CustomerCommand;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("客户指令")
@Data
public class CustomerCommandVO extends CustomerCommand {

    @ApiModelProperty("申报单位")
    private String orgName;

    @ApiModelProperty("单位社保号或公积金号")
    private String accountNumber;

    @ApiModelProperty("执行状态名称")
    private String statusName;
}
