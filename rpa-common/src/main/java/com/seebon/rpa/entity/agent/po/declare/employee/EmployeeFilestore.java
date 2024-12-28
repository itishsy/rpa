package com.seebon.rpa.entity.agent.po.declare.employee;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

/**
 * @author ZhenShijun
 * @dateTime 2022-12-27 15:19:07
 */
@ApiModel("员工附件信息")
@Table(name = "employee_filestore")
@Data
public class EmployeeFilestore {

    @ApiModelProperty("主键id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    @ApiModelProperty("人员证件号码")
    private String idCard;

    @Column
    @ApiModelProperty("人员姓名")
    private String employeeName;

    @Column
    @ApiModelProperty("参保地id")
    private Integer addrId;

    @Column
    @ApiModelProperty("参保地名称")
    private String addrName;

    @Column
    @ApiModelProperty("附件类型code")
    private String fileType;

    @Column
    @ApiModelProperty("附件类型")
    private String fileTypeName;

    @Column
    @ApiModelProperty("附件id")
    private Integer fileId;
}
