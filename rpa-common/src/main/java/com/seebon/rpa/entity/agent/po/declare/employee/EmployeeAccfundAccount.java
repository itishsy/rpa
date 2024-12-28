package com.seebon.rpa.entity.agent.po.declare.employee;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 人员公积金账号表
 */
@ApiModel(value = "com-seebon-rpa-entity-po-declare-employee-EmployeeAccfundAccount")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "employee_accfund_account")
public class EmployeeAccfundAccount implements Serializable {
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
     * 汇缴地id
     */
    @Column
    @ApiModelProperty(value = "汇缴地id")
    private Integer addrId;

    /**
     * 公积金账号
     */
    @Column
    @ApiModelProperty(value = "公积金账号")
    private String accfundnum;

    /**
     * 个人电脑号
     */
    @Column
    @ApiModelProperty(value = "个人电脑号")
    private String computerNumber;

    /**
     * 个人编号
     */
    @Column
    @ApiModelProperty(value = "个人编号")
    private String personalNumber;

    private static final long serialVersionUID = 1L;

    public static final String COL_ID = "id";

    public static final String COL_EMPLOYEE_ID = "employee_id";

    public static final String COL_ID_CARD = "id_card";

    public static final String COL_ADDR_ID = "addr_id";

    public static final String COL_ACCFUNDNUM = "accfundnum";

    public static final String COL_COMPUTER_NUMBER = "computer_number";

    public static final String COL_PERSONAL_NUMBER = "personal_number";
}