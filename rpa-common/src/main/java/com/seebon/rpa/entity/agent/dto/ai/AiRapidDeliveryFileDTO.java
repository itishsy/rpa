package com.seebon.rpa.entity.agent.dto.ai;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AiRapidDeliveryFileDTO{

    @ApiModelProperty("批次号")
    private String batchNo;

    @ApiModelProperty("uuid")
    private String rapidDeliveryUuid;

    @ApiModelProperty("文件路径")
    private String fileUrl;

    @ApiModelProperty("文件名称")
    private String fileName;

}
