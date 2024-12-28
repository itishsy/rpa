package com.seebon.rpa.schedule.RobotWarnTask;

import com.seebon.rpa.entity.robot.dto.design.OperationRequestVo;
import com.seebon.rpa.entity.robot.enums.WarnStatus;
import com.seebon.rpa.entity.robot.po.design.RobotOperationMessageConfig;
import com.seebon.rpa.remote.RpaSaasAgentService;
import com.seebon.rpa.service.RobotWarnMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 机器人接口服务异常预警实现类
 *
 * @author zjf
 * @dateTime 2023-05-25 18:00:24
 */
@Component
public class RobotNewServiceWarnTask {
    @Autowired
    private RobotWarnMessageService robotWarnMessageService;
    @Autowired
    private RpaSaasAgentService rpaSaasAgentService;

    @Scheduled(cron = "0 */5 * * * ?")
    public void runTask() {
        try {
            int i = rpaSaasAgentService.httpTest();
        } catch (Exception e) {
            //构建短信与邮件内容
            List<OperationRequestVo> list = new ArrayList<>();
            RobotOperationMessageConfig robotOperationMessageConfig = robotWarnMessageService.buildContent(list, WarnStatus.API_EXCEPTION.getCode());
            //发送短信与邮件
            robotWarnMessageService.sendMsg(robotOperationMessageConfig);
        }
    }
}
