package com.seebon.rpa.entity.agent.po.declare.employee;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 人员客户信息
 */
@ApiModel(value = "人员客户信息")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "employee_customer_site")
public class EmployeeCustomerSite implements Serializable {
    @Id
    @ApiModelProperty(value = "主键id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 人员id
     */
    @Column
    @ApiModelProperty(value = "人员id")
    private Integer employeeId;

    /**
     * 人员身份证号
     */
    @Column
    @ApiModelProperty(value = "人员身份证号")
    private String idCard;

    /**
     * 客户id
     */
    @Column
    @ApiModelProperty(value = "客户id")
    private Integer customerId;

    /**
     * 合同开始日期
     */
    @Column
    @ApiModelProperty(value = "合同开始日期")
    private Date contractStartDate;

    /**
     * 合同结束日期
     */
    @Column
    @ApiModelProperty(value = "合同结束日期")
    private Date contractEndDate;

    /**
     * 入职日期
     */
    @Column
    @ApiModelProperty(value = "入职日期")
    private Date entryDate;

    /**
     * 离职日期
     */
    @Column
    @ApiModelProperty(value = "离职日期")
    private Date leaveDate;

    /**
     * 离职原因
     */
    @Column
    @ApiModelProperty(value = "离职原因")
    private String leaveComment;

    /**
     * 创建人id
     */
    @Column
    @ApiModelProperty(value = "创建人id")
    private Integer createId;

    /**
     * 创建时间
     */
    @Column
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 更新人id
     */
    @Column
    @ApiModelProperty(value = "更新人id")
    private Integer updateId;

    /**
     * 更新时间
     */
    @Column
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    /**
     * 备注
     */
    @Column
    @ApiModelProperty(value = "备注")
    private String comment;
}