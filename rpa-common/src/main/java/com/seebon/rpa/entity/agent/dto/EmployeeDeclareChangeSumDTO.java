package com.seebon.rpa.entity.agent.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * TODO
 *
 * @author zjf
 * @describe 申报数据统计
 * @date 2023-07-06 15:43
 */
@Data
public class EmployeeDeclareChangeSumDTO{


    @ApiModelProperty("当日待申报人数汇总")
    private Integer todayPendDeclareCount;


    @ApiModelProperty("待申报超过3日汇总")
    private Integer pendDeclareThreeCount;


    @ApiModelProperty("待申报超过5日汇总")
    private Integer pendDeclareFiveCount;


    @ApiModelProperty("当日申报中人数汇总")
    private Integer todayDeclareProgressCount;


    @ApiModelProperty("申报中超1日汇总")
    private Integer progressDeclareOneCount;


    @ApiModelProperty("当日完成申报数据汇总")
    private Integer todayCompleteDeclareCount;


    @ApiModelProperty("本月完成申报数据汇总")
    private Integer monthCompleteDeclareCount;


    @ApiModelProperty("本月申报失败数据汇总")
    private Integer monthFailDeclareCount;
}
