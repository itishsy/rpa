package com.seebon.rpa.entity.saas.po.social;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@ApiModel("导入临时表")
@Data
@Table(name = "tmp_social_cost_cust_import")
public class TmpSocialCostCustImport {
    @ApiModelProperty("主键id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ApiModelProperty("客户ID")
    @Column
    private Integer custId;

    @ApiModelProperty("入职时间")
    @Column
    private Date entryTime;


    @ApiModelProperty("身份证")
    @Column
    private String idCard;

    @ApiModelProperty("导入批次号")
    @Column
    private String importNum;


}
