package com.seebon.rpa.schedule;

import com.alibaba.fastjson.JSON;
import com.seebon.rpa.entity.robot.dto.design.OperationRequestVo;
import com.seebon.rpa.entity.robot.enums.WarnStatus;
import com.seebon.rpa.entity.robot.po.design.RobotOperationMessageConfig;
import com.seebon.rpa.repository.mysql.RobotClientDao;
import com.seebon.rpa.service.OperationMonitorService;
import com.seebon.rpa.service.RobotWarnMessageService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * TODO
 *
 * @author zjf
 * @describe 盒子升级失败定时任务
 * @date 2023/4/23 14:34
 */
@Component
public class RobotWarnFailTask {
    @Autowired
    private RobotClientDao robotClientDao;
    @Autowired
    private OperationMonitorService operationMonitorService;
    @Autowired
    private RobotWarnMessageService robotWarnMessageService;
    @Autowired
    private SimpleLogin simpleLogin;

    @Value("${task.login.userId}")
    private Integer userId;
    @Value("${task.login.userName}")
    private String userName;
    @Value("${task.login.custId}")
    private Integer custId;

    //每10分钟执行一次
    @Scheduled(cron = "0 */10 * * * ?")
    public void runTask() {
        simpleLogin.doLogin(userId, userName, custId);
        List<Map<String, Object>> upgradeErrorList = robotClientDao.getBoxErrorList();
        if (CollectionUtils.isNotEmpty(upgradeErrorList)) {
            //机器人盒子升级失败录入运维监控表
            List<OperationRequestVo> operationRequestVos = JSON.parseArray(JSON.toJSONString(upgradeErrorList), OperationRequestVo.class);
            operationRequestVos = operationMonitorService.setCustomerName(operationRequestVos);
            operationMonitorService.insertOperationDetail(operationRequestVos, WarnStatus.ROBOT_UPGRADE_FAIL.getCode());
            //构建短信与邮件内容
            RobotOperationMessageConfig robotOperationMessageConfig = robotWarnMessageService.buildContent(operationRequestVos, WarnStatus.ROBOT_UPGRADE_FAIL.getCode());
            //发送短信与邮件
            robotWarnMessageService.sendMsg(robotOperationMessageConfig);
        }
        simpleLogin.loginOut();
    }
}
