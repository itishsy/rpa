package com.seebon.rpa.entity.robot.dto.design;

import com.seebon.rpa.entity.robot.RobotAppConfigForm;
import com.seebon.rpa.entity.robot.vo.design.RobotAppConfigGroupVO2;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@ApiModel("信息配置vo")
@Data
@Accessors(chain=true)
public class RobotAppConfigVO {
    @ApiModelProperty(value="应用配置维护表单")
    private RobotAppConfigForm robotAppConfigForm;
    @ApiModelProperty(value="应用配置分组VO2")
    private List<RobotAppConfigGroupVO2> robotAppConfigGroupVO2;
}
