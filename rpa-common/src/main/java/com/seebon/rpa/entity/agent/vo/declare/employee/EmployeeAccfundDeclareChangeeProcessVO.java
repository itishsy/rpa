package com.seebon.rpa.entity.agent.vo.declare.employee;

import com.seebon.rpa.entity.agent.po.declare.employee.EmployeeAccfundDeclareChangeeProcess;
import com.seebon.rpa.entity.vo.RobotExecutionVo;
import lombok.Data;

import java.util.Date;

/**
 * @Author hao
 * @Date 2022/11/23 19:03
 * @Version 1.0
 **/
@Data
public class EmployeeAccfundDeclareChangeeProcessVO extends EmployeeAccfundDeclareChangeeProcess {

    private String OperatorName;

    private String processTypeStr;

    private RobotExecutionVo robotExecutionVos;

    private Integer custId;

    private Date minCheckTime;

    private Date minOfferTime;

    private Long avgCheckTime;
}
