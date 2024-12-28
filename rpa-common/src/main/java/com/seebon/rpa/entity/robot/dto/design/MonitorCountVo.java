package com.seebon.rpa.entity.robot.dto.design;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * TODO
 *
 * @author zjf
 * @describe
 * @date 2023-07-24 15:49
 */
@ApiModel("监控台数据统计vo")
@Data
public class MonitorCountVo{

    @ApiModelProperty(value = "左菜单-当前已开通城市")
    private Integer leftPassCityCount;

    @ApiModelProperty(value = "左菜单-下线城市")
    private Integer leftOfflineCityCount;

    @ApiModelProperty(value = "左菜单-待上线城市")
    private Integer leftLiveCityCount;

    @ApiModelProperty(value = "右菜单-开通城市")
    private Integer rightPassCityCount;

    @ApiModelProperty(value = "右菜单-下线城市")
    private Integer rightOfflineCityCount;

    @ApiModelProperty(value = "右菜单-上线城市")
    private Integer rightLiveCityCount;

    @ApiModelProperty(value = "盒子总数")
    private Integer boxCount;

    @ApiModelProperty(value = "盒子执行中")
    private Integer boxExecutionCount;

    @ApiModelProperty(value = "盒子休眠中")
    private Integer boxSleepCount;

    @ApiModelProperty(value = "申报账号总数")
    private Integer declareAccountCount;

    @ApiModelProperty(value = "申报运行中")
    private Integer declareRunCount;

    @ApiModelProperty(value = "申报未运行")
    private Integer declareNoRunCount;

    @ApiModelProperty(value = "服务员工总数")
    private Integer servicePerson;

    @ApiModelProperty(value = "应用总数")
    private Integer appCount;

    @ApiModelProperty(value = "应用执行中")
    private Integer appExecutionCount;

    @ApiModelProperty(value = "服务数据")
    private Integer serviceCount;

    @ApiModelProperty(value = "服务成功")
    private Integer serviceSuccessCount;

    @ApiModelProperty(value = "服务失败")
    private Integer serviceFailCount;

    @ApiModelProperty(value = "总运行时间")
    private double totalRunTime;

    @ApiModelProperty(value = "节省工时")
    private String saveHour;

    @ApiModelProperty(value = "城市指标-返回正数上升，负数下降，0等于")
    private Integer cityIndex;

    @ApiModelProperty(value = "盒子指标-返回正数上升，负数下降，0等于")
    private Integer boxIndex;

    @ApiModelProperty(value = "申报账号指标-返回正数上升，负数下降，0等于")
    private Integer accountIndex;

    @ApiModelProperty(value = "应用指标-返回正数上升，负数下降，0等于")
    private Integer appIndex;

    @ApiModelProperty(value = "服务数据指标-返回正数上升，负数下降，0等于")
    private Integer serviceDataIndex;


}
