package com.seebon.rpa.entity.robot.dto.design;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;

/**
 * TODO
 *
 * @author zjf
 * @describe 运维监控分页vo
 * @date 2023/4/12 17:26
 */
@ApiModel("运维监控分页vo")
@Data
public class OperationMonitorVo{
    @Column
    @ApiModelProperty("上报时间")
    private String reportTime;

    @Column
    @ApiModelProperty("客户名称")
    private String clientName;

    @Column
    @ApiModelProperty("客户Id")
    private Integer clientId;

    @Column
    @ApiModelProperty("设备编号")
    private String machineCode;

    @Column
    @ApiModelProperty("RPA执行异常")
    private Integer rpaFailCount;

    @Column
    @ApiModelProperty("网站功能异常")
    private Integer siteFailCount;

    @Column
    @ApiModelProperty("登录失败")
    private Integer loginFailCount;

    @Column
    @ApiModelProperty("机器人心跳异常")
    private Integer robotHeartBeatCount;

    @Column
    @ApiModelProperty("盒子升级失败")
    private Integer robotUpgradeFailCount;

    @Column
    @ApiModelProperty("证书需升级")
    private Integer certWaitUpgradeCount;
}
