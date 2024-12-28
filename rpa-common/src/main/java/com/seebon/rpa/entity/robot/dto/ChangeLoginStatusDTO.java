package com.seebon.rpa.entity.robot.dto;

import com.seebon.rpa.entity.robot.RobotLoginAuth;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

@Data
public class ChangeLoginStatusDTO extends RobotLoginAuth {

    @ApiModelProperty(value = "执行编码")
    private String executionCode;

    @ApiModelProperty(value = "任务编码")
    private String taskCode;

    @ApiModelProperty(value = "登录状态 0未登录 1维持中 2已失效")
    private Integer loginStatus;

    @ApiModelProperty("备注")
    private String comment;
}
