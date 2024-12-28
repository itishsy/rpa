package com.seebon.rpa.entity.agent.po.declare.employee;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 人员社保账号表
 */
@ApiModel(value = "com-seebon-rpa-entity-po-declare-employee-EmployeeSocialAccount")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "employee_social_account")
public class EmployeeSocialAccount implements Serializable {
    /**
     * 自动增长ID
     */
    @Id
    @ApiModelProperty(value = "自动增长ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 人员id
     */
    @Column
    @ApiModelProperty(value = "人员id")
    private Integer employeeId;

    /**
     * 身份证号
     */
    @Column
    @ApiModelProperty(value = "身份证号")
    private String idCard;

    /**
     * 投保地地址id
     */
    @Column
    @ApiModelProperty(value = "投保地地址id")
    private Integer addrId;

    /**
     * 社保账号
     */
    @Column
    @ApiModelProperty(value = "社保账号")
    private String socialnum;

    private static final long serialVersionUID = 1L;

    public static final String COL_ID = "id";

    public static final String COL_EMPLOYEE_ID = "employee_id";

    public static final String COL_ID_CARD = "id_card";

    public static final String COL_ADDR_ID = "addr_id";

    public static final String COL_SOCIALNUM = "socialnum";
}