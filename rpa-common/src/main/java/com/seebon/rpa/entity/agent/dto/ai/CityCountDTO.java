package com.seebon.rpa.entity.agent.dto.ai;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * TODO
 *
 * @author zjf
 * @describe
 * @date 2023-10-16 13:56
 */
@Data
public class CityCountDTO{

    @ApiModelProperty("城市")
    private String city;

    @ApiModelProperty("城市数据")
    private Integer cityCount;
}
