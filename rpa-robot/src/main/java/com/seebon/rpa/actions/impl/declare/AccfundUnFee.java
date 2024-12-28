package com.seebon.rpa.actions.impl.declare;

import com.alibaba.fastjson.JSON;
import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.NoneTarget;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.constant.RobotConstant;
import com.seebon.rpa.entity.robot.RobotExecutionData;
import com.seebon.rpa.entity.robot.RobotTaskNotice;
import com.seebon.rpa.remote.RpaDesignService;
import com.seebon.rpa.utils.Convert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

@Slf4j
@RobotAction(name = "公积金未缴费", order = 20, targetType = NoneTarget.class, comment = "公积金未缴费停用24小时")
public class AccfundUnFee extends AbstractAction {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private RpaDesignService rpaDesignService;

    @Override
    public void run() {
        List<RobotExecutionData> declareList = this.getDeclareList();
        if (CollectionUtils.isNotEmpty(declareList)) {
            for (RobotExecutionData data : declareList) {
                String sql = "update robot_execution_data set robotExecStatus =2,declareStatus =2,failType=1,failReason='未缴费无法申报' WHERE id=?";
                jdbcTemplate.update(sql, data.getId());
            }
        }
        RobotTaskNotice taskNotice = new RobotTaskNotice();
        taskNotice.setExecutionCode(ctx.get("executionCode"));
        taskNotice.setTaskCode(ctx.get("taskCode"));
        try {
            log.info("未缴费-停用账户返回值:{}", JSON.toJSONString(taskNotice));
            String resp = rpaDesignService.stopAccount(taskNotice);
            log.info("未缴费-停用账户返回值:{}", resp);
        } catch (Exception e) {
            log.error("未缴费-停用账户异常." + e.getMessage(), e);
            throw new RuntimeException("未缴费-停用账户异常." + e.getMessage());
        }
    }

    private List<RobotExecutionData> getDeclareList() {
        String addrName = ctx.get("addrName");
        Integer businessType = Convert.getBusinessType(ctx.get("businessType"));
        Integer declareType = ctx.get("declareType");
        String accountNumber = ctx.getAccountNumber();
        String tplTypeCode = ctx.get("tplTypeCode");
        String batchCode = ctx.get(RobotConstant.INST_ID);

        String sql = "select id,name,idCard,declareMonth,failReason,declareType from robot_execution_data WHERE robotExecStatus = 0 and declareStatus = 1 " +
                "and addrName=? and businessType=? and declareType=? and accountNumber=? and tplTypeCode=? and batch_code=?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(RobotExecutionData.class),
                addrName, businessType, declareType, accountNumber, tplTypeCode, batchCode);
    }
}
