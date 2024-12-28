package com.seebon.rpa.entity.agent.dto.ai;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * TODO
 *
 * @author zjf
 * @describe
 * @date 2023-10-13 18:17
 */
@Data
public class AiCityCountDTO{

    @ApiModelProperty("城市")
    private String city;

    @ApiModelProperty("uuid")
    private String uuid;

    @ApiModelProperty("城市id")
    private Integer addrId;

    @ApiModelProperty("是否置灰")
    private boolean isGrey;

    @ApiModelProperty("批次号")
    private String batchNumber ;

    @ApiModelProperty("已经提交数")
    private Integer submitCount;

    @ApiModelProperty("未提交数")
    private Integer notSubmitCount;
}
