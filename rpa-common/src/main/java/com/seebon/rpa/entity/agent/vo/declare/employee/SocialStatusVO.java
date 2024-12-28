package com.seebon.rpa.entity.agent.vo.declare.employee;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 社保当前状态信息
 */
@Data
public class SocialStatusVO {

    @ApiModelProperty("社保基数")
    private String empTbBaseSocial;

    @ApiModelProperty("社保缴纳起始月")
    private String insuredSocial;

    @ApiModelProperty("最终参保险种")
    private String itemUltimate;
}
