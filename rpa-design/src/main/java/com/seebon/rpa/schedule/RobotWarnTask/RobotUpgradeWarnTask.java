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
 * 机器人盒子升级失败预警实现类
 *
 * @author ZhenShijun
 * @dateTime 2022-12-01 16:01:44
 */
@Slf4j
@Component
public class RobotUpgradeWarnTask extends RobotWarnTaskBase {
    @Autowired
    private RobotClientDao robotClientDao;

    @Override
    public void runTask(RobotWarnConfigBase configBase) {
        List<Map<String, Object>> upgradeErrorList = robotClientDao.getUpgradeErrorList();
        if (CollectionUtils.isNotEmpty(upgradeErrorList)) {
            String tableHtml = buildTable(upgradeErrorList, Lists.newArrayList("序号", "客户", "机器人ip地址", "失败原因"),
                    Lists.newArrayList("xh", "custName", "ip", "failCause"));
            buildAndSendMsg(configBase, tableHtml);
        }
    }
}
