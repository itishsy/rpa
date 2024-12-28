package com.seebon.rpa.entity.agent.dto.ai;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class AiRapidDeliveryPushDTO{

    @ApiModelProperty("业务编码")
    private String businessUuid;

    @ApiModelProperty("文件、批次集合")
    private List<AiRapidDeliveryFileDTO> fileDTOList;

    @ApiModelProperty("所需字段")
    private Map<String, Object> mustColumnMap;

}
