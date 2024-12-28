package com.seebon.rpa.schedule.RobotWarnTask;

import com.seebon.rpa.entity.robot.po.robotWarn.RobotWarnConfigBase;
import com.seebon.rpa.remote.RpaSaasService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 机器人接口服务异常预警实现类
 *
 * @author ZhenShijun
 * @dateTime 2022-12-01 16:00:24
 */
@Slf4j
@Component
public class RobotServiceWarnTask extends RobotWarnTaskBase {
    @Autowired
    private RpaSaasService rpaSaasService;

    @Override
    public void runTask(RobotWarnConfigBase configBase) {
        try {
            int i = rpaSaasService.httpTest();
        } catch (Exception e) {
            buildAndSendMsg(configBase, null);
        }
    }
}
