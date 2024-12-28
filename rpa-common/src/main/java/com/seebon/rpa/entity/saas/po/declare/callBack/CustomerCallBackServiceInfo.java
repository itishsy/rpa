package com.seebon.rpa.entity.saas.po.declare.callBack;

import com.seebon.rpa.entity.Identity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

@ApiModel("客户申报数据状态变更通知回调接口信息表")
@Data
@Table(name = "customer_call_back_service_info")
public class CustomerCallBackServiceInfo extends Identity {

    @ApiModelProperty("客户id,对应rpa库表customer_base表id值")
    @Column
    private Integer custId;

    @ApiModelProperty("回调接口地址")
    @Column
    private String serviceUrl;

    @ApiModelProperty("请求方式，post 或者 get")
    @Column
    private String sendType;

    @ApiModelProperty("接口状态字段")
    @Column
    private String statusField;

    @ApiModelProperty("异常信息字段")
    @Column
    private String msgField;

    @ApiModelProperty("请求成功编码")
    @Column
    private String successCode;

    @ApiModelProperty("请求失败编码")
    @Column
    private String failCode;

    @ApiModelProperty("长久静态token")
    @Column
    private String token;

    @ApiModelProperty("最大回调次数")
    @Column
    private Integer maxCount;

    @ApiModelProperty("描述")
    @Column
    private String comment;

}
