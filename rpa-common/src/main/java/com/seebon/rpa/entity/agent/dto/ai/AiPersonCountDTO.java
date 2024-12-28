package com.seebon.rpa.entity.agent.dto.ai;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Set;

/**
 * TODO
 *
 * @author zjf
 * @describe
 * @date 2023-09-25 14:30
 */
@Data
public class AiPersonCountDTO{

    @ApiModelProperty("客户")
    private String customerName;

    @ApiModelProperty("总记录数")
    private long countRecord;

    @ApiModelProperty("增员记录数")
    private long addRecord;

    @ApiModelProperty("减员记录数")
    private long reduceRecord;

    @ApiModelProperty("补缴记录数")
    private long mendRecord;

    @ApiModelProperty("解析的城市集合")
    private Set<AiCityCountDTO> cityList;

}
