package com.seebon.rpa.entity.saas.po.ai;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * TODO
 *
 * @author zjf
 * @describe
 * @date 2023-08-25 11:22
 */
@Data
@Table(name = "ai_person_detail")
@ApiModel("初始化申报账号表")
public class AIPersonDetail{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer id;

    @ApiModelProperty("客户id")
    @Column
    private Integer customerId;

    @ApiModelProperty("客户名称")
    @Column
    private String customerName;

    @ApiModelProperty("参保主体")
    @Column
    private String insuredName;

    @ApiModelProperty("业务类型")
    @Column
    private Integer businessType;

    @ApiModelProperty("业务名称")
    @Column
    private String businessName;

    @ApiModelProperty("申报单位")
    @Column
    private String declareUnit;

    @ApiModelProperty("申报账号")
    @Column
    private String declareAccount;

    @ApiModelProperty("备注")
    @Column
    private String remark;

    @ApiModelProperty("创建时间")
    @Column
    private Date createTime;

    @ApiModelProperty("更新时间")
    @Column
    private Date updateTime;

}
