package com.seebon.rpa.entity.robot.dto.design;

import com.seebon.rpa.entity.robot.RobotAppConfigCondition;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author hao
 * @Date 2022/10/25 15:29
 * @Version 1.0
 **/
@Data
public class RobotAppConfigConditionVO2 {

    private String appCode;

    private Integer argsDefineId;

    private String condition;

    @ApiModelProperty(value = "关系")
    private String relation;

    @ApiModelProperty(value = "状态 0:显性 1:隐性")
    private Integer showOrHidden;

    @ApiModelProperty(value = "字段名")
    private String column;

    private List<RobotAppConfigCondition> configConditions;
}
