package com.seebon.rpa.entity.robot.po.robotWarn;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

/**
 * @author ZhenShijun
 * @dateTime 2022-12-01 14:16:47
 */
@ApiModel("rpa机器人预警信息接收人信息")
@Table(name = "robot_warn_recipient")
@Data
public class RobotWarnRecipient {

    @ApiModelProperty("接收人id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer id;

    @ApiModelProperty("关联robot_warn_config_base_id 表id")
    @Column
    private Integer warnBaseId;

    @ApiModelProperty("预警消息接收人姓名")
    @Column
    private String empName;

    @ApiModelProperty("消息接收人手机号码")
    @Column
    private String phoneNumber;

    @ApiModelProperty("消息接收人电子邮箱")
    @Column
    private String email;

}
