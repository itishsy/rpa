package com.seebon.rpa.entity.agent.dto.declare;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * Hey man，take your time！
 *
 * @Author: 李松洋
 * @Date: Create in 10:44 2023/10/25
 */
@Data
public class RobotLoginNoticListDto {
    @ApiModelProperty("失败类型")
    private String failType;
    @ApiModelProperty("失败状态")
    private String status;
    @ApiModelProperty("创建时间")
    private Date createTime;
    @ApiModelProperty("验证方式")
    private String MsgType;
    @ApiModelProperty("地区业务")
    private String appName;
    @ApiModelProperty("手机号码")
    private String recipientPhoneNumber;

}
