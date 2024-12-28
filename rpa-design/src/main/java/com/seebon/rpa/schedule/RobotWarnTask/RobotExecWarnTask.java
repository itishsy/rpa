package com.seebon.rpa.schedule.RobotWarnTask;

import com.google.common.collect.Lists;
import com.seebon.rpa.entity.robot.dto.robotWarn.RobotWarnMsgDTO;
import com.seebon.rpa.entity.robot.po.robotWarn.RobotWarnConfigBase;
import com.seebon.rpa.entity.robot.po.robotWarn.RobotWarnMsgList;
import com.seebon.rpa.entity.robot.po.robotWarn.RobotWarnRecipient;
import com.seebon.rpa.repository.mysql.RobotExecutionDao;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

/**
 * 机器人执行异常预警实现类
 *
 * @author ZhenShijun
 * @dateTime 2022-12-01 15:58:28
 */
@Slf4j
@Component
public class RobotExecWarnTask extends RobotWarnTaskBase {
    @Autowired
    private RobotExecutionDao robotExecutionDao;

    @Override
    public void runTask(RobotWarnConfigBase configBase) {
        int rangeTime = 10;
        if (configBase.getRangeTime() != null) {
            rangeTime = configBase.getRangeTime();
        }
        List<Map<String, Object>> robotExecErrorList = robotExecutionDao.getRobotExecErrorList(rangeTime);
        if (CollectionUtils.isNotEmpty(robotExecErrorList)) {
            String tableHtml = buildTable(robotExecErrorList, Lists.newArrayList("序号", "应用名称", "客户名称", "任务名称", "流程名称"),
                    Lists.newArrayList("xh", "appName", "custName", "taskName", "flowName"));
            buildAndSendMsg(configBase, tableHtml);
        }
    }

    public void sendExecErrorMsg(RobotWarnMsgDTO msgDTO) {
        Example example = new Example(RobotWarnConfigBase.class);
        example.createCriteria().andEqualTo("type", "1028002");
        RobotWarnConfigBase robotWarnConfigBase = robotWarnConfigBaseDao.selectOneByExample(example);
        List<RobotWarnRecipient> recipients = getRecipients(robotWarnConfigBase.getId());
        Integer sendSms = robotWarnConfigBase.getSendSms();
        Integer sendEmail = robotWarnConfigBase.getSendEmail();
        if (CollectionUtils.isNotEmpty(recipients)) {
            for (RobotWarnRecipient recipient : recipients) {
                if (sendSms == 1 && StringUtils.isNotBlank(recipient.getPhoneNumber()) && StringUtils.isNotBlank(msgDTO.getSmsContent())) {
                    RobotWarnMsgList robotWarnMsgListEntity = createRobotWarnMsgListEntity(robotWarnConfigBase);
                    robotWarnMsgListEntity.setSendWay(1);
                    robotWarnMsgListEntity.setEmpName(recipient.getEmpName());
                    robotWarnMsgListEntity.setPhoneNumber(recipient.getPhoneNumber());
                    robotWarnMsgListEntity.setSmsContent(msgDTO.getSmsContent());
                    sendMsg(robotWarnMsgListEntity);
                }
                if (sendEmail == 1 && StringUtils.isNotBlank(recipient.getEmail()) && StringUtils.isNotBlank(msgDTO.getEmailContent())) {
                    RobotWarnMsgList robotWarnMsgListEntity = createRobotWarnMsgListEntity(robotWarnConfigBase);
                    robotWarnMsgListEntity.setSendWay(2);
                    robotWarnMsgListEntity.setEmpName(recipient.getEmpName());
                    robotWarnMsgListEntity.setEmail(recipient.getEmail());
                    robotWarnMsgListEntity.setEmailTheme(robotWarnConfigBase.getEmailTheme());
                    robotWarnMsgListEntity.setEmailContent(msgDTO.getEmailContent());
                    sendMsg(robotWarnMsgListEntity);
                }
            }
        } else {
            log.info(String.format("%s未配置消息接收人信息，不执行消息推送。。。", robotWarnConfigBase.getTypeName()));
        }
    }
}
