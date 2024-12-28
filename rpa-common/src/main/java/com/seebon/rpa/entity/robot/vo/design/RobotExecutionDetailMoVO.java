package com.seebon.rpa.entity.robot.vo.design;

import com.seebon.rpa.entity.robot.dto.design.RobotExecutionDetailMo;
import com.seebon.rpa.entity.robot.dto.design.RobotExecutionFileInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 客户端控制台-执行明细
 */
@Data
@ApiModel(value = "机器人执行明细")
public class RobotExecutionDetailMoVO extends RobotExecutionDetailMo {

    @ApiModelProperty(value = "截图文件")
    private List<RobotExecutionFileInfo> files;
}
