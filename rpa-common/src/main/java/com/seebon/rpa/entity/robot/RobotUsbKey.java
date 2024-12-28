package com.seebon.rpa.entity.robot;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel(value = "USB设备")
@Table(name = "robot_usb_key")
public class RobotUsbKey implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    @ApiModelProperty(value = "客户ID")
    private Integer clientId;

    @Column
    @ApiModelProperty(value = "usbKeyId")
    private Integer usbKeyId;

    @Column
    @ApiModelProperty(value = "应用编码")
    private String appCode;

    @Column
    @ApiModelProperty(value = "任务编码")
    private String taskCode;

    @Column
    @ApiModelProperty(value = "申报账户")
    private String declareAccount;

    @Column
    @ApiModelProperty(value = "申报单位")
    private String companyName;

    @Column
    @ApiModelProperty(value = "申报系统 字典 10007,10008")
    private String declareSystem;

    @Column
    @ApiModelProperty(value = "备注")
    private String remark;

    @Column
    @ApiModelProperty(value = "启用状态：0-禁用，1-启用")
    private Integer disabled;

    @Column
    @ApiModelProperty(value = "证书有效开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date certEffeStartTime;

    @Column
    @ApiModelProperty(value = "证书有效结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date certEffeEndTime;

    @Column
    @ApiModelProperty(value = "创建者Id")
    private Integer createId;

    @Column
    @ApiModelProperty(value = "创建者名称")
    private String createName;

    @Column
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @Column
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}
