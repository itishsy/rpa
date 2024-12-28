package com.seebon.rpa.entity.agent.vo.declare.employee;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class ImportAccfundsVO {

    /*公积金参保状态*/
    @ApiModelProperty("公积金参保状态")
    private String accfundStatus;

    @ApiModelProperty("公积金参保城市")
    private String accfundCity;

    @ApiModelProperty("单位参保比列")
    private BigDecimal compRatio;

    @ApiModelProperty("个人参保比列")
    private BigDecimal empRatio;

    @ApiModelProperty("公积金起始月")
    private Date accfundInsuredDate;

    @ApiModelProperty("公积金基数")
    private BigDecimal accfundEmpTbBase;
}
