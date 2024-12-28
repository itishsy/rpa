package com.seebon.rpa.entity.robot.dto.design;

import com.seebon.rpa.entity.robot.vo.design.RobotOperationMonitorDetailVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel("运维监控办结参数DTO")
@Data
public class HandleMessageDTO {

    @ApiModelProperty("办结的记录")
    private List<RobotOperationMonitorDetailVO> detailVOList;

    @ApiModelProperty("原因归类")
    private String handleType;

    @ApiModelProperty("补充说明")
    private String handleRemark;

    @ApiModelProperty("关联链接")
    private String handleLink;

}
