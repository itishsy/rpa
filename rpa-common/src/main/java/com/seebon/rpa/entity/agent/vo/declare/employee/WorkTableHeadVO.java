package com.seebon.rpa.entity.agent.vo.declare.employee;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class WorkTableHeadVO {

    @ApiModelProperty("在职人数")
    private Integer numberOfEmp;

    @ApiModelProperty("年度离职")
    private Integer leaveOfYear;

    @ApiModelProperty("本月待增")
    private Integer reduceOfMonth;

    @ApiModelProperty("本月待减")
    private Integer increaseOfMonth;

    @ApiModelProperty("城市总数")
    private Integer allCityCounts;

    @ApiModelProperty("城市总数")
    private Integer allCompanyCounts;

    private Boolean isNewCustomer = true;
}
