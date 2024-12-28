package com.seebon.rpa.entity.po.declare;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;
import java.util.Date;

/**
 * TODO
 *
 * @author zjf
 * @describe 申报汇总表
 * @date 2023-07-05 15:36
 */

@Data
@ApiModel("申报汇总表")
@Table(name = "employee_declare_change_count")
public class EmployeeDeclareChangeCount{

    @ApiModelProperty("id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    @ApiModelProperty("地区id")
    private Integer addrId;

    @Column
    @ApiModelProperty("地区名字")
    private String addrName;

    @Column
    @ApiModelProperty("业务类型")
    private Integer businessType;

    @Column
    @ApiModelProperty("客户id")
    private Integer clientId;

    @Column
    @ApiModelProperty("客户名字")
    private String clientName;

    @Column
    @ApiModelProperty("申报开始时间")
    private String declareStartTime;

    @Column
    @ApiModelProperty("申报结束时间")
    private String declareEndTime;

    @Column
    @ApiModelProperty("当日待申报人数")
    private Integer todayPendDeclare;

    @Column
    @ApiModelProperty("待申报超过3日")
    private Integer pendDeclareThree;

    @Column
    @ApiModelProperty("待申报超过5日")
    private Integer pendDeclareFive;

    @Column
    @ApiModelProperty("当日申报中人数")
    private Integer todayDeclareProgress;

    @Column
    @ApiModelProperty("申报中超1日")
    private Integer progressDeclareOne;

    @Column
    @ApiModelProperty("当日完成申报数据")
    private Integer todayCompleteDeclare;

    @Column
    @ApiModelProperty("本月完成申报数据")
    private Integer monthCompleteDeclare;

    @Column
    @ApiModelProperty("本月申报失败数据")
    private Integer monthFailDeclare;

    @Column
    @ApiModelProperty("生成报表时间")
    private String reportDate;

    @Column
    @ApiModelProperty("创建时间")
    private Date createTime;

    @Column
    @ApiModelProperty("更新时间")
    private Date updateTime;
}
