package com.seebon.rpa.entity.sms.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@ApiModel("短信模板")
@Data
@Table(name = "sms_template")
public class SmsTemplate implements Serializable {
    @Id
    @Column
    @ApiModelProperty("主键Id")
    private Integer id;

    @Column
    @ApiModelProperty("模板名称")
    private String name;

    @Column
    @ApiModelProperty("匹配类型(startsWith、endsWith、contains)")
    private String matchType;

    @Column
    @ApiModelProperty("模板内容")
    private String content;

    @Column
    @ApiModelProperty("地区Id")
    private Integer addrId;

    @Column
    @ApiModelProperty("业务类型（社保：1001001，公积金：1001002）")
    private String businessType;

    @Column
    @ApiModelProperty("创建时间")
    private Date createTime;

    @Column
    @ApiModelProperty("更新时间")
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}