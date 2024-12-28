package com.seebon.rpa.actions.impl.flow;

import com.seebon.common.utils.UUIDGenerator;
import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.enums.ActionResult;
import com.seebon.rpa.context.runtime.RobotInterruptException;
import com.seebon.rpa.context.runtime.RuntimeSkipTo;
import com.seebon.rpa.entity.robot.RobotFlow;
import com.seebon.rpa.repository.mapper.RobotFlowMapper;
import com.seebon.rpa.runner.SyncOutService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

@Slf4j
@RobotAction(name = "退出流程", order = 30, comment = "退出当前流程")
public class Stop extends AbstractAction {

    @ActionArgs(value = "结束类型", required = true, dict = ActionResult.class)
    private String stopType;

    @ActionArgs(value = "结束信息", required = true)
    private String stopInfo;

    @Autowired
    private SyncOutService syncOutService;
    @Autowired
    private RobotFlowMapper flowMapper;

    @Override
    public void run() {
        String tagCode = this.getTagCode();
        switch (ActionResult.valueOf(stopType)) {
            case success:
                syncOutService.loginSuccess(tagCode, 1, stopInfo);
                throw new RuntimeSkipTo(UUIDGenerator.uuidStringWithoutLine());
            case failed:
                syncOutService.loginSuccess(tagCode, 2, stopInfo);
                throw new RobotInterruptException(stopInfo);
            default:
                break;
        }
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
