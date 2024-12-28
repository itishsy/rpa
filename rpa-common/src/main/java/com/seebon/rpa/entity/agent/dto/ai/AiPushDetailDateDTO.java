package com.seebon.rpa.entity.agent.dto.ai;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AiPushDetailDateDTO{

    @ApiModelProperty("业务编号")
    private String businessUuid;

    @ApiModelProperty("批次号")
    private String batchNo;

    @ApiModelProperty("处理状态")
    private int status;

    @ApiModelProperty("说明内容")
    private String message;

    @ApiModelProperty("解析是否结束")
    private Boolean isFinish;

    @ApiModelProperty("明细信息")
    private List<AiRapidDeliveryDetailDTO> detailList;


}
