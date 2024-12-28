package com.seebon.rpa.entity.robot.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel("驾驶仓-经办人任务排名")
@Data
public class SysUserSubmitDataStatisticsDTO implements Serializable {

    @ApiModelProperty("经办人id")
    private Integer userId;

    @ApiModelProperty("经办人姓名")
    private String userName;

    @ApiModelProperty("负责城市")
    private String addrNames;

    @ApiModelProperty("月数")
    private Integer monthTotal;

    @ApiModelProperty("日数")
    private Integer dayTotal;

}
