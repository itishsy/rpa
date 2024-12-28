package com.seebon.rpa.entity.agent.vo.declare.employee;

import com.seebon.rpa.entity.agent.po.declare.employee.EmployeeAccfundDeclareChange;
import com.seebon.rpa.entity.agent.po.declare.employee.EmployeeAccfundDeclareChangeSuppDetail;
import lombok.Data;

import java.util.List;

@Data
public class EmployeeAccfundDeclareChangeVO extends EmployeeAccfundDeclareChange {
    private String residenceType;
    private String accfundnum;
    private String accfundStatus;
    private String changeTypeName;
    private String declareStatusName;
    private List<EmployeeAccfundDeclareChangeSuppDetail> details;
}
