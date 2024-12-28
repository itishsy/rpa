package com.seebon.rpa.entity.agent.dto.ai;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * TODO
 *
 * @author zjf
 * @describe
 * @date 2023-10-16 13:42
 */
@Data
public class AiFinishCountDTO{
    @ApiModelProperty("批次")
    private String batchNo;

    @ApiModelProperty("总记录数")
    private Integer countRecord;

    @ApiModelProperty("增员数据")
    private Integer addRecord;

    @ApiModelProperty("减员数据")
    private Integer reduceRecord;

    @ApiModelProperty("补缴数据")
    private Integer mendRecord;

    @ApiModelProperty("成功数")
    private Integer successCount;

    @ApiModelProperty("失败数")
    private Integer failCount;

    @ApiModelProperty("城市集合")
    private List<CityCountDTO> cityCountDTOList;


}
