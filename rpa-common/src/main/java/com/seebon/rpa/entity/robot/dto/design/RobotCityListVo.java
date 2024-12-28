package com.seebon.rpa.entity.robot.dto.design;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author LY
 * @date 2023/7/24 15:32
 */
@Data
public class RobotCityListVo {

    private Long id;

    @ApiModelProperty(value = "机器人编码")
    private String robotCode;

    @ApiModelProperty(value = "应用编码")
    private String appCode;

    @ApiModelProperty(value = "应用名称")
    private String appName;

    @ApiModelProperty(value = "应用城市")
    private String addrName;

    @ApiModelProperty(value = "应用参数")
    private String appArgs;

    @ApiModelProperty(value = "版本号")
    private String version;

    @ApiModelProperty(value = "版本说明")
    private String comment;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "上线阶段")
    private Integer onlineStatus;

    @ApiModelProperty(value = "上下架状态")
    private Integer runStatus;

    @ApiModelProperty(value = "网站链接")
    private String websiteLinks;

    @ApiModelProperty(value = "发布时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date releaseTime;

    private String latestDeclarationTime;

    private String lastCostTime;

    private Long execTotalCount;

    /**
     * 单位 秒
     */
    private Long execTotalTime;

    private Integer configuredBoxCount;

    private Integer configuredCustomCount;

    private Integer configuredPrincipalCount;

    private String remark;

    private Integer configuredAdd;

    private Integer configuredDel;

    private Integer configuredRepair;

    private Integer configuredAdjust;


}
