package com.seebon.rpa.entity.agent.dto.openApi;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author: tanyong
 * @Description:
 * @Date: 2023/1/4 10:39
 * @Modified By:
 */
@ApiModel("撤回待申报的数据DTO")
@Data
public class EmployeeDeclareRecallDTO {
    @ApiModelProperty("批次号")
    private String batchNumber;

    @ApiModelProperty("人员证件号码")
    private List<String> idCards;
}
