package com.seebon.rpa.entity.robot;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * 机器人客户端状态追踪
 */
@Data
@ApiModel(value = "机器人状态追踪")
@Table(name = "robot_client_track")
public class RobotClientTrack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer id;

    @Column
    @ApiModelProperty(value = "客户ID")
    private Integer clientId;

    @Column
    @ApiModelProperty(value = "机器标识码")
    private String machineCode;

    @Column
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @Column
    @ApiModelProperty(value = "结束时间")
    private Date endTime;

    @Column
    @ApiModelProperty(value = "状态")
    private Integer status;

}
