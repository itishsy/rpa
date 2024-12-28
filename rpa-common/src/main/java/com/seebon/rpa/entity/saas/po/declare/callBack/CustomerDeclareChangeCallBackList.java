package com.seebon.rpa.entity.saas.po.declare.callBack;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@ApiModel("申报状态变更回调消息记录表")
@Data
@Table(name = "customer_declare_change_call_back_list")
public class CustomerDeclareChangeCallBackList implements Serializable {

    @ApiModelProperty("主键id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer id;

    @ApiModelProperty("客户id")
    @Column
    private Integer custId;

    @ApiModelProperty("状态变更的批次号，多个用逗号隔开")
    @Column
    private String changeBatchNumbers;

    @ApiModelProperty("回调状态，0：未回调，1：回调成功，2：回调失败")
    @Column
    private Integer sendStatus;

    @ApiModelProperty("回调失败信息")
    @Column
    private String errorMsg;

    @ApiModelProperty("回调次数")
    @Column
    private Integer sendCount;

    @ApiModelProperty("回调时间")
    @Column
    private Date sendTime;

    @ApiModelProperty("回调消息生成时间")
    @Column
    private Date createTime;

}
