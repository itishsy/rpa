package com.seebon.rpa.entity.saas.po.declare;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author hao
 * @Date 2023/1/5 11:35
 * @Version 1.0
 **/
@Data
@Table(name = "policy_paid_in_situation")
@ApiModel("实缴汇总表")
public class PolicyPaidInSituation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    @ApiModelProperty("客户id")
    private Integer custId;

    @Column
    @ApiModelProperty("组织名称")
    private String orgName;

    @Column
    @ApiModelProperty("单位社保号")
    private String accountNumber;

    @Column
    @ApiModelProperty("地区")
    private String addressName;

    @Column
    @ApiModelProperty("业务类型；1--社保；2--公积金")
    private Integer businessType;

    @Column
    @ApiModelProperty("系统类型")
    private String prefix;


    @Column
    @ApiModelProperty("费款所属期")
    private String periodOfExpense;

    @Column
    @ApiModelProperty("缴纳金额")
    private BigDecimal totalAmount;

    @Column
    @ApiModelProperty("文件id")
    private Integer fileId;


    @Column
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd")
    @ApiModelProperty("缴款时间")
    private Date periodTime;

}
