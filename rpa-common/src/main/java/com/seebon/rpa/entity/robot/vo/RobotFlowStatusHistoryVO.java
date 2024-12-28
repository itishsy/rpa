package com.seebon.rpa.entity.robot.vo;

import com.seebon.rpa.entity.robot.RobotFlowStatusHistory;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class RobotFlowStatusHistoryVO extends RobotFlowStatusHistory {

    @ApiModelProperty(value = "应用状态 0：调研 1：配置 2：调试通过 3：等待数据 4：验证有效  5：正常运行   6：修复问题")
    private String appStatusName;

    @ApiModelProperty(value = "流程状态 0：配置 1：调试通过 2：等待数据 3：验证有效 4：修复问题")
    private String flowStatusName;

    @ApiModelProperty(value = "上线标记 1：上线、2：下线、3：待上线")
    private String onLiveName;
}