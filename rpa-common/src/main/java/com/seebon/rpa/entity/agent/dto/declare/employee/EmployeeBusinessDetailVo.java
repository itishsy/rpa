package com.seebon.rpa.entity.agent.dto.declare.employee;

import lombok.Data;

/**
 * TODO
 *
 * @author zjf
 * @describe 首页公积金社保数据展示
 * @date 2023-08-09 21:50
 */
@Data
public class EmployeeBusinessDetailVo{

    private Integer id;

    private Integer addrId;

    private Integer status;

    private Integer employeeFileStoreId;

    private String itemId;

    private String itemCode;

    private String itemName;

    private String idCard;

    private String addrName;

    private String changeType;

    private String strInsuredDate;

    private Integer businessType;

    private String employeeName;

    private String uuid;

    private String createTime;

    private String compAccount;

    private String insuredDate;

    private String cardType;

    private Integer suppDeclareStatus;

    private String failCause;

    private String suppFailCause;

    private Integer robotExecStatus;

    private Integer declareStatus;


}
