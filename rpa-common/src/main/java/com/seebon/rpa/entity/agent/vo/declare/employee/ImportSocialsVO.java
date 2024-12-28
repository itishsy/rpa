package com.seebon.rpa.entity.agent.vo.declare.employee;

import com.seebon.rpa.entity.agent.po.declare.employee.EmployeeSocialDeclareChangeItemBase;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class ImportSocialsVO {
    /*社保参保状态*/
    @ApiModelProperty("社保参保状态")
    private String socialStatus;

    @ApiModelProperty("社保参保城市")
    private String socialCity;

    @ApiModelProperty("社保起始月")
    private Date socialInsuredDate;

    @ApiModelProperty("社保基数")
    private BigDecimal socialEmpTbBase;

    private List<EmployeeSocialDeclareChangeItemBase> bases;
}
