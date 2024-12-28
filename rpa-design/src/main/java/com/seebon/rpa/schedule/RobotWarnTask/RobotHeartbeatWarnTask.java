package com.seebon.rpa.schedule.RobotWarnTask;

import com.google.common.collect.Lists;
import com.seebon.rpa.entity.robot.po.robotWarn.RobotWarnConfigBase;
import com.seebon.rpa.repository.mysql.RobotClientDao;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 机器人心跳异常预警实现类
 *
 * @author ZhenShijun
 * @dateTime 2022-12-01 15:55:36
 */
@Slf4j
@Component
public class RobotHeartbeatWarnTask extends RobotWarnTaskBase {

    @Autowired
    private RobotClientDao robotClientDao;

    @Override
    public void runTask(RobotWarnConfigBase configBase) {
        int rangeTime = 10;
        if (configBase.getRangeTime() != null) {
            rangeTime = configBase.getRangeTime();
        }
        List<Map<String, Object>> heartbeatList = robotClientDao.getHeartbeatErrorList(rangeTime);
        if (CollectionUtils.isNotEmpty(heartbeatList)) {
            String tableHtml = buildTable(heartbeatList, Lists.newArrayList("序号", "客户", "机器人ip地址", "最近接收心跳时间"),
                    Lists.newArrayList("xh", "custName", "ip", "lastHeartbeatTime"));
            buildAndSendMsg(configBase, tableHtml);
        }
    }
}
