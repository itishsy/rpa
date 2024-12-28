package com.seebon.rpa.entity.robot;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * 客户机器人任务参数
 */
@Data
@ApiModel(value = "客户机器人任务参数")
@Table(name = "robot_task_args")
public class RobotTaskArgs implements Serializable {

    @Id
    private Integer id;

    @Column
    @ApiModelProperty(value = "任务编码")
    private String taskCode;

    @Column
    @ApiModelProperty(value = "参数键")
    private String argsKey;

    @Column
    @ApiModelProperty(value = "参数值")
    private String argsValue;

    @Column
    @ApiModelProperty(value = "参数类型")
    private String argsType;

    @Column
    @ApiModelProperty(value = "表单配置的顺序")
    private Integer displayOrder;

    @Column
    @ApiModelProperty(value = "表单名")
    private String formName;

    @Column
    @ApiModelProperty("分组名")
    private String groupName;

    @Column
    @ApiModelProperty("同步时间")
    private Date syncTime;
}
