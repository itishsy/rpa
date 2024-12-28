package com.seebon.rpa.entity.robot.vo.design;

import com.seebon.rpa.entity.robot.RobotArgsDefine;
import com.seebon.rpa.entity.robot.RobotDataDict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel(value = "机器人操作指令参数定义和值")
@Data
public class RobotFlowStepArgsVO extends RobotArgsDefine {

    /**
     * 字段值
     */
    @ApiModelProperty(value = "字段值")
    private String fieldValue;

    @ApiModelProperty(value = "下拉动态选项")
    private List<RobotDataDict> robotDataDicts;


}
