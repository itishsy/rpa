package com.seebon.rpa.entity.agent.vo.declare.employee;

import com.seebon.rpa.entity.agent.po.declare.employee.EmployeeAccfundDeclareChange;
import com.seebon.rpa.entity.agent.po.declare.employee.EmployeeAccfundDeclareChangeSuppDetail;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author hao
 * @Date 2022/6/29 18:31
 * @Version 1.0
 **/
@Data
public class AdjustAndStopTempFillVo extends EmployeeAccfundDeclareChange {
    private List<EmployeeAccfundDeclareChangeSuppDetail> acsupdetail;

    private Map<String, Object> accfundSuppMap = new HashMap<>();
}
