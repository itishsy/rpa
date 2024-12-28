package com.seebon.rpa.entity.robot.dto.design;

import com.seebon.rpa.entity.robot.RobotServiceItem;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * TODO
 *
 * @author zjf
 * @describe RPA 应用设计vo
 * @date 2024-01-05 14:31
 */
@Data
public class RobotAppDesignVO{

    @ApiModelProperty(value = "id")
    private Integer id;

    @ApiModelProperty(value = "应用名称")
    private String appName;

    @ApiModelProperty(value = "应用编码")
    private String appCode;

    @ApiModelProperty(value = "应用参数")
    private String appArgs;

    @ApiModelProperty(value = "所处状态：0:配置 1:测试 2:运维")
    private Integer onlineStatus;

    @ApiModelProperty(value = "应用状态：0调研 1配置 2调试通过 3等待数据 4验证有效 5正常运行 6修复问题")
    private Integer appStatus;

    @ApiModelProperty("下架备注")
    private String remark;

    @ApiModelProperty(value = "服务项目，1：增员，2：减员，3：调基，5：补缴，6：缴费，7：名册名单，8：费用明细 9：政策通知  10：基数申报 (多个项目用逗号拼接)")
    private String serviceItem;

    @ApiModelProperty(value = "总流程数")
    private Integer flowCount;

    @ApiModelProperty(value = "修复问题流程数")
    private Integer problemCount;

    @ApiModelProperty(value = "版本号")
    private String version;

    @ApiModelProperty(value = "发布状态 1 已发布，0 未发布 , 2 有更新未发布")
    private Integer status;

    @ApiModelProperty(value = "发布时间")
    private Date releaseTime;

    @ApiModelProperty("开发人员")
    private String devUserName;

    @ApiModelProperty("测试人员")
    private String testUserName;

    @ApiModelProperty("运维人员")
    private String ywUserName;

    @ApiModelProperty("调研人员")
    private String xqUserName;

    @ApiModelProperty(value = "应用备注")
    private String appRemark;

    @ApiModelProperty(value = "应用类型 ,凭证 申报机器人")
    private String robotName;

    @ApiModelProperty(value = "运行状态 0 下架  1 上架 2 待上线")
    private Integer runStatus;

    @ApiModelProperty(value = "地区id")
    private String addrId;

    @ApiModelProperty(value = "地区名字")
    private String addrName;

    @ApiModelProperty(value = "业务类型 1社保 2公积金")
    private Integer businessType;

    @ApiModelProperty(value = "省")
    private String province;

    @ApiModelProperty(value = "启用账号数")
    private Integer startAccountCount;

    @ApiModelProperty(value = "停用账号数")
    private Integer stopAccountCount;

    @ApiModelProperty(value = "增员修复数")
    private Integer addCount;

    @ApiModelProperty(value = "减员修复数")
    private Integer reduceCount;

    @ApiModelProperty(value = "调基修复数")
    private Integer tjCount;

    @ApiModelProperty(value = "补缴修复数")
    private Integer bjCount;

    @ApiModelProperty(value = "缴费修复数")
    private Integer jfCount;

    @ApiModelProperty(value = "在册修复数")
    private Integer zcCount;

    @ApiModelProperty(value = "费用明细修复数")
    private Integer payCount;

    @ApiModelProperty(value = "政策通知修复数")
    private Integer zctzCount;

    private List<String> serviceItemList;

    private List<RobotServiceItem> serviceVOList;

}
