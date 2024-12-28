package com.seebon.rpa.entity.agent.dto.declare.employee;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.seebon.rpa.entity.agent.dto.declare.RobotLoginNoticListDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * Hey man，take your time！
 *
 * @Author: 李松洋
 * @Date: Create in 15:16 2023/10/24
 */
@ApiModel("消息通知")
@Data
public class FailMessageDTO {
    @ApiModelProperty("消息id")
    private Integer id;
    @ApiModelProperty("错误类型")
    private String failType;
    @ApiModelProperty("错误原因")
    private String failReason;
    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    @ApiModelProperty("浮窗")
    private RobotLoginNoticListDto robotLoginNoticListDTO;
    @ApiModelProperty("业务类型")
    private Integer typeOfBusiness;

    public FailMessageDTO() {

    }
}
