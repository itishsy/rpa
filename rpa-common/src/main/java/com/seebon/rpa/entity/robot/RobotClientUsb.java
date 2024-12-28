package com.seebon.rpa.entity.robot;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel(value = "机器人usb")
@Table(name = "robot_client_usb")
public class RobotClientUsb implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @ApiModelProperty(value = "机器标识码")
    private String machineCode;

    @Column
    @ApiModelProperty(value = "序号")
    private String sort;

    @Column
    @ApiModelProperty(value = "编码")
    private String orginal;

    @Column
    @ApiModelProperty(value = "同步标识  0-未同步  1-已同步")
    private Integer sync;

    @Column
    @ApiModelProperty(value = "备注")
    private String remark;

    @Column
    @ApiModelProperty(value = "同步时间")
    private Date syncTime;

    @Column
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @Column
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
}