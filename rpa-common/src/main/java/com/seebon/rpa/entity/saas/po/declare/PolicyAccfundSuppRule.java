package com.seebon.rpa.entity.saas.po.declare;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.seebon.rpa.entity.Identity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author ZhenShijun
 * @dateTime 2022-04-21 18:06:04
 */
@ApiModel("参保地公积金补缴规则")
@Data
@Table(name = "policy_accfund_supp_rule")
public class PolicyAccfundSuppRule extends Identity {

    @Column
    @ApiModelProperty("参保地id")
    private Integer addrId;

    @Column
    @ApiModelProperty("可允许补缴类型code 字典值")
    private String allowSuppTypeCode;

    @Column
    @ApiModelProperty("可允许补缴类型名称")
    private String allowSuppTypeName;

    @Column
    @ApiModelProperty("往前补缴月数")
    private Integer allowMonths;

    @Column
    @ApiModelProperty("最早可补缴年月")
    @JsonFormat(pattern = "yyyy-MM", timezone = "GMT+8")
    private Date earliestAllowYearMonth;

    @Column
    @ApiModelProperty("是否允许跨月补缴 1:允许，0：否")
    private Integer acrossMonth;

}
