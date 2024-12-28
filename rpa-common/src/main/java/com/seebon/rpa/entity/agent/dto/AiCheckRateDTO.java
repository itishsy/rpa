package com.seebon.rpa.entity.agent.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@ApiModel("AI检验进度信息")
@Data
public class AiCheckRateDTO implements Serializable {

    @ApiModelProperty("总记录数")
    private Integer records;

    @ApiModelProperty("已完成校验数")
    private Integer finished;

    @ApiModelProperty("待校验数")
    private Integer unfinished;

    @ApiModelProperty("校验进度")
    private BigDecimal rate;

}
