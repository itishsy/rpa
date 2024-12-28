package com.seebon.rpa.actions.impl.flow;

import com.google.common.collect.Maps;
import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.NoneTarget;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.runtime.RobotConfigException;
import com.seebon.rpa.entity.robot.RobotFlow;
import com.seebon.rpa.entity.robot.enums.ServiceItemTypeEnum;
import com.seebon.rpa.repository.mapper.RobotFlowMapper;
import com.seebon.rpa.service.RobotCommonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Slf4j
@RobotAction(name = "定时执行流程", order = 55, targetType = NoneTarget.class, comment = "定时执行流程")
public class PlanFlow extends AbstractAction {

    @Autowired
    private RobotCommonService robotCommonService;

    @Autowired
    private RobotFlowMapper flowMapper;

    @ActionArgs(value = "流程名称", required = true)
    private String flowName;

    @ActionArgs(value = "下次执行时间(h)", required = true)
    private Integer nextHour;


    @Override
    public void run() {
        log.info("执行流程：" + flowName );
        Example e = new Example(RobotFlow.class);
        e.createCriteria().andEqualTo("appCode", ctx.get("appCode")).andEqualTo("flowName", flowName);
        RobotFlow flow = flowMapper.selectOneByExample(e);
        if(flow == null){
            throw new RobotConfigException("流程名称不存在：" + flowName);
        }
        if(nextHour == null || nextHour < 1){
            throw new RobotConfigException("下次执行时间配置有误" );
        }

        Map<String, Object> reqMap = Maps.newHashMap();
        reqMap.put("clientId", ctx.get("clientId"));
        reqMap.put("flowCode", flow.getFlowCode());
        reqMap.put("flowName", flowName);
        reqMap.put("taskCode", ctx.get("taskCode"));
        reqMap.put("nextHour", nextHour);
        reqMap.put("createId", 0);
        reqMap.put("createName", "执行器");
        reqMap.put("serviceItemName", ServiceItemTypeEnum.getNameByCode(flow.getServiceItem()));
        reqMap.put("generatePlan", LocalDateTime.now().plusHours(nextHour).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))); // 将当前时间加上小时
        String res = robotCommonService.upsetTaskTrigger(reqMap);
        log.info("调用后台添加定时执行任务。 结果：" + res);
    }
}
