package com.seebon.rpa.entity.robot.dto.design;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * TODO
 *
 * @author zjf
 * @describe
 * @date 2024-01-09 9:28
 */
@Data
public class RobotCountVO{

    @ApiModelProperty("城市数")
    private Integer cityCount;

    @ApiModelProperty("上线数")
    private Integer onlineCount;

    @ApiModelProperty("下线数")
    private Integer OfflineCount;

    @ApiModelProperty("待上线数")
    private Integer launchedCount;

    @ApiModelProperty("调研数")
    private Integer surveyCount;

    @ApiModelProperty("配置数")
    private Integer configurationCount;

    @ApiModelProperty("调试通过数")
    private Integer shakedownCount;

    @ApiModelProperty("等待数据数")
    private Integer waitCount;

    @ApiModelProperty("验证有效数")
    private Integer checkCount;

    @ApiModelProperty("正常数")
    private Integer normalCount;

    @ApiModelProperty("修复问题数")
    private Integer repairCount;

    @ApiModelProperty("配置阶段总数")
    private Integer configurationStageCount;

    @ApiModelProperty("测试阶段总数")
    private Integer testStageCount;

    @ApiModelProperty("运维阶段总数")
    private Integer operationStageCount;

    @ApiModelProperty("上线数据指标 大于0增加 小于0减少 等于0相等")
    private Integer onlineIndex;

    @ApiModelProperty("下线数据指标 大于0增加 小于0减少 等于0相等")
    private Integer offlineIndex;

    @ApiModelProperty("待上线数据指标 大于0增加 小于0减少 等于0相等")
    private Integer launchedIndex;


}
