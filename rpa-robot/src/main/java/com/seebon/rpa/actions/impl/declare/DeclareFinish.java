package com.seebon.rpa.actions.impl.declare;

import com.seebon.common.utils.UUIDGenerator;
import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.NoneTarget;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.constant.RobotConstant;
import com.seebon.rpa.context.runtime.RuntimeSkipTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

@RobotAction(name = "申报完成",
        order = 30,
        targetType = NoneTarget.class,
        comment = "申报完成")
public class DeclareFinish extends AbstractAction {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run() {
        String batchCode = ctx.getVariable(RobotConstant.INST_ID);
        String sql = "update robot_execution_data set robotExecStatus =1,declareStatus =2 WHERE batch_code=? and robotExecStatus = 0";
        jdbcTemplate.update(sql, batchCode);
        // 跳过到下一个步骤
        throw new RuntimeSkipTo(UUIDGenerator.uuidStringWithoutLine());
    }
}
