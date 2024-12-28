package com.seebon.rpa.entity.robot;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Table(name = "robot_account_status_history")
public class RobotAccountStatusHistory implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键Id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ApiModelProperty(value = "任务编码")
    private String taskCode;

    @ApiModelProperty(value = "状态  0-暂停  1-启动")
    private Integer status;

    @ApiModelProperty(value = "原因备注")
    private String remark;

    @ApiModelProperty(value = "创建人Id")
    private Integer createId;

    @ApiModelProperty(value = "创建人名称")
    private String createName;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;


}