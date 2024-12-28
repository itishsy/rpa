package com.seebon.rpa.entity.sms.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@ApiModel("短信转发配置")
@Data
@Table(name = "sms_forward_config")
public class ForwardConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column
    @ApiModelProperty("主键Id")
    private Integer id;

    @Column
    @ApiModelProperty("模板Id(表sms_template-id)")
    private Integer templateId;

    @Column
    @ApiModelProperty("任务code(robot_task-task_code)")
    private String taskCode;

    @Column
    @ApiModelProperty("转发手机号")
    private String forwardPhone;

    @Column
    @ApiModelProperty("接收器手机号")
    private String receiverPhone;

    @Column
    @ApiModelProperty("创建时间")
    private Date createTime;

    @Column
    @ApiModelProperty("更新时间")
    private Date updateTime;
}