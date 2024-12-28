package com.seebon.rpa.entity.robot.dto.design;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel("机器人应用流程VO")
public class RobotFlowAddVO {

    @ApiModelProperty(value = "应用code")
    private String appCode;

    @ApiModelProperty(value = "流程名称")
    private String flowName;

    @ApiModelProperty(value = "类型（1主流程，2子流程）")
    private Integer flowType;

    @ApiModelProperty(value = "是否模板(0：否,1：是)")
    private Integer templateType;

    @ApiModelProperty(value = "服务项目1：增员，2：减员，3：调基，5：补缴，6：缴费，7：名册名单，8：费用明细 9：政策通知  10：基数申报 ")
    private Integer serviceItem;

    @ApiModelProperty(value = "申报系统 字典 10007,10008")
    private String declareSystem;

    @ApiModelProperty(value = "运行载体 字典 10019")
    private String runSupport;

    @ApiModelProperty(value = "模板流程编码")
    private String templateFlowCode;

    @ApiModelProperty(value = "标签编码")
    private String tagCode;
}

