package com.seebon.rpa.entity.robot;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Table(name = "robot_flow_status_history")
public class RobotFlowStatusHistory implements Serializable {
    @ApiModelProperty(value = "主键Id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ApiModelProperty(value = "应用编码")
    @Column(name = "app_code")
    private String appCode;

    @ApiModelProperty(value = "流程编码")
    @Column(name = "flow_code")
    private String flowCode;

    @ApiModelProperty(value = "动作名称")
    @Column(name = "action_name")
    private String actionName;

    @ApiModelProperty(value = "应用状态 0：调研 1：配置 2：调试通过 3：等待数据 4：验证有效  5：正常运行   6：修复问题")
    @Column(name = "app_status")
    private Integer appStatus;

    @ApiModelProperty(value = "流程状态 0：配置 1：调试通过 2：等待数据 3：验证有效 4：修复问题")
    @Column(name = "flow_status")
    private Integer flowStatus;

    @ApiModelProperty(value = "上线标记 1：上线、2：下线、3：待上线")
    @Column(name = "on_live")
    private Integer onLive;

    @ApiModelProperty(value = "数据类型：1：应用状态，2：流程状态")
    private Integer type;

    @ApiModelProperty(value = "原因备注")
    private String remark;

    @ApiModelProperty(value = "创建人Id")
    @Column(name = "create_id")
    private Integer createId;

    @ApiModelProperty(value = "创建人名称")
    @Column(name = "create_name")
    private String createName;

    @ApiModelProperty(value = "创建时间")
    @Column(name = "create_time")
    private Date createTime;

    private static final long serialVersionUID = 1L;
}