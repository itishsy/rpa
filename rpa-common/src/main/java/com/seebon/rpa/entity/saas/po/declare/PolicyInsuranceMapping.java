package com.seebon.rpa.entity.saas.po.declare;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

/**
 * @author ZhenShijun
 * @dateTime 2023-02-08 13:48:33
 */
@Data
@ApiModel("")
@Table(name = "policy_insurance_mapping")
public class PolicyInsuranceMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("id")
    protected Integer id;

    @ApiModelProperty("社保险种字典code")
    @Column
    private String dictCode;

    @ApiModelProperty("社保险种报盘字段code")
    @Column
    private String dataItemCode;

    @ApiModelProperty("社保险种名称")
    @Column
    private String insuranceName;

}
