package com.seebon.rpa.entity.agent.dto.ai;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * TODO
 *
 * @author zjf
 * @describe 批量作废人员数据dto
 * @date 2023-10-11 17:32
 */
@Data
public class DeleteAiPersonDTO{
    @ApiModelProperty("随机id")
    private String uuid;

    @ApiModelProperty("地区")
    private String address;

    @ApiModelProperty("事务类型")
    private Integer workType;
}
