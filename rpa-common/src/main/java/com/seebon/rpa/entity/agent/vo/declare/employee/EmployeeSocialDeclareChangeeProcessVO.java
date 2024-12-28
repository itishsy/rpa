package com.seebon.rpa.entity.agent.vo.declare.employee;

import com.seebon.rpa.entity.agent.po.declare.employee.EmployeeSocialDeclareChangeeProcess;
import com.seebon.rpa.entity.vo.RobotExecutionVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Author hao
 * @Date 2022/11/23 19:00
 * @Version 1.0
 **/
@Data
public class EmployeeSocialDeclareChangeeProcessVO extends EmployeeSocialDeclareChangeeProcess {

    private String OperatorName;

    private String processTypeStr;

    private String tplTypeName;

    private RobotExecutionVo robotExecutionVos;

    private Integer custId;

    private Date minCheckTime;

    private Date minOfferTime;

    private Long avgCheckTime;
}
