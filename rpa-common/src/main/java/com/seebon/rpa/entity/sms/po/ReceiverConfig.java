package com.seebon.rpa.entity.sms.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@ApiModel("短信接收器配置")
@Data
@Table(name = "sms_receiver_config")
public class ReceiverConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column
    @ApiModelProperty("主键Id")
    private Integer id;

    @Column
    @ApiModelProperty("客户ID")
    private Integer clientId;

    @Column
    @ApiModelProperty("接收器ip")
    private String receiverIp;

    @Column
    @ApiModelProperty("接收器手机号")
    private String receiverPhone;

    @Column
    @ApiModelProperty("创建时间")
    private Date createTime;

    @Column
    @ApiModelProperty("更新时间")
    private Date updateTime;
}