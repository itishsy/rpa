package com.seebon.rpa.entity.agent.dto.openApi;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author ZhenShijun
 * @dateTime 2022-10-17 10:01:10
 */
@ApiModel("员工申报信息DTO")
@Data
public class EmployeeDeclareBaseDTO {

    @ApiModelProperty("参保地名称")
    private String addrName;

    @ApiModelProperty("业务类型")
    private Integer businessType;

    @ApiModelProperty("申报类型")
    private Integer declareType;

    @ApiModelProperty("员工申报信息")
    private List<Map<String, String>> declareInfo;

    @ApiModelProperty("ai增减员id")
    private String aiId;

    @ApiModelProperty("ai增减员批次号")
    private String batchNumber;

}
