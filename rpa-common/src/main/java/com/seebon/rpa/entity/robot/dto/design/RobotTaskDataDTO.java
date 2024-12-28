package com.seebon.rpa.entity.robot.dto.design;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@ApiModel("申报配置-批量启停申报账户统计信息")
@Data
public class RobotTaskDataDTO implements Serializable {

    @ApiModelProperty("任务编号")
    private List<String> taskCodes;

    @ApiModelProperty("申报账户统计信息")
    private List<Map<String, Object>> taskData;

    @ApiModelProperty("状态,1：启用，0：停用")
    private Integer status;

    @ApiModelProperty("停用原因")
    private String comment;

}
