package com.seebon.rpa.actions.impl.declare;

import com.seebon.common.utils.UUIDGenerator;
import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.NoneTarget;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.constant.RobotConstant;
import com.seebon.rpa.context.enums.QuitFlow;
import com.seebon.rpa.context.runtime.RuntimeSkipTo;
import com.seebon.rpa.entity.robot.RobotExecutionData;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

@RobotAction(name = "核验完成", order = 30, targetType = NoneTarget.class, comment = "核验完成")
public class VerifyFinish extends AbstractAction {
    @ActionArgs(value = "是否退出流程", dict = QuitFlow.class)
    private String quitFlow;

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private DeclareVerify declareVerify;

    @Override
    public void run() {
        List<RobotExecutionData> checkList = declareVerify.getCheckList();
        if (CollectionUtils.isEmpty(checkList)) {
            return;
        }
        for (RobotExecutionData data : checkList) {
            this.updateDeclareing(data.getId());
        }
        if (StringUtils.isNotBlank(quitFlow) && QuitFlow.FALSE.equals(QuitFlow.valueOf(quitFlow))) {
            throw new RuntimeSkipTo(RobotConstant.NEXT_STEP);
        }
        throw new RuntimeSkipTo(UUIDGenerator.uuidStringWithoutLine());
    }

    private void updateDeclareing(Integer id) {
        String sql = "update robot_execution_data set robotExecStatus=2,declareStatus=2 where robotExecStatus=1 and id=?";
        jdbcTemplate.update(sql, id);
    }
}
