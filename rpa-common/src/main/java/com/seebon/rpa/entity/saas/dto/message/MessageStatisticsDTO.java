package com.seebon.rpa.entity.saas.dto.message;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class MessageStatisticsDTO implements Serializable {

    @ApiModelProperty("消息类型")
    private String messageType;

    @ApiModelProperty("消息类型名称")
    private String messageTypeName;

    @ApiModelProperty("消息数")
    private Integer countNumber;

}
