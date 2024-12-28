package com.seebon.rpa.entity.agent.dto.payFee;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * Hey man，take your time！
 *
 * @Author: 李松洋
 * @Date: Create in 14:26 2023/10/24
 */
@Data
public class CustomerPayFeeCommentDTO {
    @ApiModelProperty("失败类型")
    private String failType;
    @ApiModelProperty("失败原因")
    private String comment;
    @ApiModelProperty("创建时间")
    private Date createTime;
    @ApiModelProperty("id")
    private Integer id;
    @ApiModelProperty("业务类型")
    private Integer businessType;



}
