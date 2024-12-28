package com.seebon.rpa.entity.sms.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@ApiModel("短信接收记录")
@Data
@Table(name = "sms_received_record")
public class ReceivedRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column
    @ApiModelProperty("主键Id")
    private Integer id;

    @Column
    @ApiModelProperty("接收器手机号")
    private String receiverPhone;

    @Column
    @ApiModelProperty("短信内容")
    private String content;

    @Column
    @ApiModelProperty("短信验证码")
    private String smsCode;

    @Column
    @ApiModelProperty("状态(0-未消费，1-已消费)")
    private Integer status;

    @Column
    @ApiModelProperty("创建时间")
    private Date createTime;

    @Column
    @ApiModelProperty("更新时间")
    private Date updateTime;
}