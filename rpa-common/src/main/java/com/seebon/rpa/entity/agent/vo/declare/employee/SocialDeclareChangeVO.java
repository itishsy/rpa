package com.seebon.rpa.entity.agent.vo.declare.employee;

import lombok.Data;


@Data
public class SocialDeclareChangeVO extends EmployeeSocialDeclareChangeVO {
    //社保账号
    private String socialnum;
    //户口类型
    private String residenceType;
    //社保参保状态
    private String declareStatusName;
    //参保业务类型名字
    private String changeTypeName;
}
