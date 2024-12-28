package com.seebon.rpa.entity.agent.vo.declare.employee;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 公积金当前状态详情
 */
@Data
public class AccfundStatusVO {
    @ApiModelProperty("公积金参保比列")
    private String accfundRatio;

    @ApiModelProperty("公积金基数")
    private String empTbBaseAccfund;

    @ApiModelProperty("公积金缴纳起始月")
    private String insuredAccfund;

}
