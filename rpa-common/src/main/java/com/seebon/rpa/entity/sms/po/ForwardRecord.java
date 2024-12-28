package com.seebon.rpa.entity.sms.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@ApiModel("短信转发记录")
@Data
@Table(name = "sms_forward_record")
public class ForwardRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column
    @ApiModelProperty("主键Id")
    private Integer id;

    @Column
    @ApiModelProperty("接收人手机号码")
    private String phone;

    @Column
    @ApiModelProperty("短信内容")
    private String content;

    @Column
    @ApiModelProperty("状态(0-发送失败，1-发送成功)")
    private Integer status;

    @Column
    @ApiModelProperty("失败原因")
    private String errorMsg;

    @Column
    @ApiModelProperty("创建时间")
    private Date createTime;

    @Column
    @ApiModelProperty("更新时间")
    private Date updateTime;
}