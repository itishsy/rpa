package com.seebon.rpa.actions.impl.flow;

import cn.hutool.core.bean.BeanUtil;
import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.runtime.RobotInterruptException;
import com.seebon.rpa.entity.robot.RobotFlow;
import com.seebon.rpa.entity.robot.dto.RobotTaskStatusChangeDTO;
import com.seebon.rpa.repository.mapper.RobotFlowMapper;
import com.seebon.rpa.runner.SyncOutService;
import com.seebon.rpa.service.RobotCommonService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import tk.mybatis.mapper.entity.Example;

@Slf4j
@RobotAction(name = "停止执行任务", order = 30, comment = "停止执行任务并且暂停当前账户自动任务")
public class StopRunTask extends AbstractAction {

    @ActionArgs(value = "停止原因", required = true, comment = "错误原因将推送至客户邮箱")
    private String stopReason;

    @Autowired
    private RobotCommonService robotCommonService;
    @Autowired
    private SyncOutService syncOutService;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private RobotFlowMapper flowMapper;

    @Override
    public void run() {
        String appCode = ctx.get("appCode");
        String taskCode = ctx.get("taskCode");
        RobotTaskStatusChangeDTO robotTaskStatusChangeDTO = new RobotTaskStatusChangeDTO();
        robotTaskStatusChangeDTO.setAppCode(appCode);
        robotTaskStatusChangeDTO.setComment(stopReason);
        robotTaskStatusChangeDTO.setStatus(0);
        robotTaskStatusChangeDTO.setTaskCode(taskCode);
        robotCommonService.stopAutoTask(BeanUtil.beanToMap(robotTaskStatusChangeDTO));
        String sql = "UPDATE robot_task SET sync_time = null ,sync = 0 , status = 0,comment=? WHERE app_code = ? and task_code = ?";
        jdbcTemplate.update(sql, stopReason, appCode, taskCode);
        String tagCode = this.getTagCode();
        syncOutService.loginSuccess(tagCode, 2, stopReason);
        throw new RobotInterruptException(stopReason);
    }

    private String getTagCode() {
        log.info("流程编码：flowCode=" + flowCode);
        if (StringUtils.isBlank(flowCode)) {
            return null;
        }
        Example e = new Example(RobotFlow.class);
        e.createCriteria().andEqualTo("flowCode", flowCode);
        RobotFlow flow = flowMapper.selectOneByExample(e);
        if (flow == null) {
            return null;
        }
        return flow.getTagCode();
    }
}
