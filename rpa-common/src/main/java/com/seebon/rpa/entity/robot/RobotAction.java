package com.seebon.rpa.entity.robot;

import com.seebon.rpa.entity.Identity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

@ApiModel("操作指令")
@Data
@Table(name = "robot_action")
public class RobotAction extends Identity {
    /**
     * 指令编码
     */
    @Column
    @ApiModelProperty(value = "指令编码")
    private String actionCode;

    /**
     * 指令名称
     */
    @Column
    @ApiModelProperty(value = "指令名称")
    private String actionName;

    /**
     * 操作目标对象
     */
    @Column
    @ApiModelProperty(value = "操作目标对象")
    private String targetType;

    /**
     * 类别
     */
    @Column
    @ApiModelProperty(value = "类别")
    private String groupName;

    /**
     * 说明
     */
    @Column
    @ApiModelProperty(value = "说明")
    private String comment;

    /**
     * 显示顺序
     */
    @Column
    @ApiModelProperty(value = "显示顺序")
    private Integer displayOrder;
}
