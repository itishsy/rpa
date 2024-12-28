package com.seebon.rpa.entity.robot;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 客户机器人任务
 */
@Data
@ApiModel(value = "客户机器人任务")
@Table(name = "robot_client_task")
public class RobotClientTask {
    @Id
    @ApiModelProperty(value = "主键ID")
    private Integer id;

    @Column
    @ApiModelProperty(value = "客户ID")
    private Integer clientId;

    @Column
    @ApiModelProperty(value = "机器标识码")
    private String machineCode;

    @Column
    @ApiModelProperty(value = "任务编码")
    private String taskCode;

    @Column
    @ApiModelProperty(value = "同步状态（0-未同步；1-已同步）")
    private Integer status;

    @Column
    @ApiModelProperty(value = "同步时间")
    private Date syncTime;

    @Column
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
}
