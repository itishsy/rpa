package com.seebon.rpa.entity.robot;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@ApiModel("机器人客户端日志")
@Data
@Table(name = "robot_client_log")
public class RobotClientLog implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "主键Id")
    private Long id;

    @Column
    @ApiModelProperty(value = "机器标识码")
    private String machineCode;

    @Column
    @ApiModelProperty(value = "维护人")
    private String userName;

    @Column
    @ApiModelProperty(value = "设备状态 1-正常 2-已废弃 3-需升级")
    private Integer status;

    @Column
    @ApiModelProperty(value = "备注")
    private String remark;

    @Column
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
}