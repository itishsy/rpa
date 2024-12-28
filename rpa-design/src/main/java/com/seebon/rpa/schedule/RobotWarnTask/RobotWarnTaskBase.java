package com.seebon.rpa.schedule.RobotWarnTask;

import com.seebon.common.utils.SmsUtils;
import com.seebon.rpa.entity.robot.po.robotWarn.RobotWarnConfigBase;
import com.seebon.rpa.entity.robot.po.robotWarn.RobotWarnMsgList;
import com.seebon.rpa.entity.robot.po.robotWarn.RobotWarnRecipient;
import com.seebon.rpa.repository.mysql.RobotWarnConfigBaseDao;
import com.seebon.rpa.repository.mysql.RobotWarnMsgListDao;
import com.seebon.rpa.repository.mysql.RobotWarnRecipientDao;
import com.seebon.rpa.utils.EmailUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ZhenShijun
 * @dateTime 2022-12-01 15:52:46
 */
@Slf4j
@SuppressWarnings("all")
public abstract class RobotWarnTaskBase {
    public abstract void runTask(RobotWarnConfigBase configBase);

    protected static final String tableStart = "<table class='table table-hover' border-collapse='collapse' cellspacing='0' cellpadding='0' border='1'>" +
            "<thead><tr>";

    protected static final String tableEnd = "</tbody></table>";

    @Autowired
    protected RobotWarnMsgListDao robotWarnMsgListDao;
    @Autowired
    protected RobotWarnRecipientDao robotWarnRecipientDao;
    @Autowired
    protected RobotWarnConfigBaseDao robotWarnConfigBaseDao;

    protected final void buildAndSendMsg(RobotWarnConfigBase configBase, String tableHtml) {
        List<RobotWarnRecipient> recipients = this.getRecipients(configBase.getId());
        Integer sendSms = configBase.getSendSms();
        Integer sendEmail = configBase.getSendEmail();
        if (CollectionUtils.isNotEmpty(recipients)) {
            for (RobotWarnRecipient recipient : recipients) {
                if (sendSms == 1 && StringUtils.isNotBlank(recipient.getPhoneNumber())) {
                    RobotWarnMsgList robotWarnMsgListEntity = createRobotWarnMsgListEntity(configBase);
                    robotWarnMsgListEntity.setSendWay(1);
                    robotWarnMsgListEntity.setEmpName(recipient.getEmpName());
                    robotWarnMsgListEntity.setPhoneNumber(recipient.getPhoneNumber());
                    robotWarnMsgListEntity.setSmsContent(configBase.getSmsContent());
                    sendMsg(robotWarnMsgListEntity);
                }
                if (sendEmail == 1 && StringUtils.isNotBlank(recipient.getEmail())) {
                    RobotWarnMsgList robotWarnMsgListEntity = createRobotWarnMsgListEntity(configBase);
                    robotWarnMsgListEntity.setSendWay(2);
                    robotWarnMsgListEntity.setEmpName(recipient.getEmpName());
                    robotWarnMsgListEntity.setEmail(recipient.getEmail());
                    robotWarnMsgListEntity.setEmailTheme(configBase.getEmailTheme());
                    robotWarnMsgListEntity.setEmailContent(configBase.getEmailContent().concat(StringUtils.isNotBlank(tableHtml) ? "<br/><br/>".concat(tableHtml) : ""));
                    sendMsg(robotWarnMsgListEntity);
                }
            }
        } else {
            log.info(String.format("%s未配置消息接收人信息，不执行消息推送。。。", configBase.getTypeName()));
        }
        /*else {
            if (sendSms == 1) {
                RobotWarnMsgList robotWarnMsgListEntity = createRobotWarnMsgListEntity(configBase);
                robotWarnMsgListEntity.setSendWay(1);
                robotWarnMsgListEntity.setSmsContent(configBase.getSmsContent());
                sendMsg(robotWarnMsgListEntity);
            }
            if (sendEmail == 1) {
                RobotWarnMsgList robotWarnMsgListEntity = createRobotWarnMsgListEntity(configBase);
                robotWarnMsgListEntity.setSendWay(2);
                robotWarnMsgListEntity.setEmailTheme(configBase.getEmailTheme());
                robotWarnMsgListEntity.setEmailContent(configBase.getEmailContent().concat(StringUtils.isNotBlank(tableHtml)?"<br/><br/>".concat(tableHtml):""));
                sendMsg(robotWarnMsgListEntity);
            }
        }*/
    }

    /**
     * 发送预警信息
     *
     * @param msg
     */
    protected final void sendMsg(RobotWarnMsgList msg) {
        Integer sendWay = msg.getSendWay();
        try {
            msg.setSendTime(new Date());
            if (sendWay == 1) { // 短信
                if (StringUtils.isNotBlank(msg.getPhoneNumber())) {
                    log.info("开始发送短信给{" + msg.getPhoneNumber() + "}......");
                    SmsUtils.send(msg.getPhoneNumber(), msg.getSmsContent());
                    log.info("成功结束发短信给{" + msg.getPhoneNumber() + "}......");
                    msg.setSendStatus(1);
                } else {
                    log.info("接收短信的手机号码为空，不执行短信发送");
                }
            } else if (sendWay == 2) { // 邮件
                if (StringUtils.isNotBlank(msg.getEmail())) {
                    Map<String, Object> emailMap = new HashMap<String, Object>();
                    emailMap.put("subject", msg.getEmailTheme());
                    emailMap.put("tplName", "mailBaseTemplate");
                    emailMap.put("mail_content", msg.getEmailContent());
                    emailMap.put("receiverEmail", msg.getEmail());
                    log.info("开始发送邮件给{" + msg.getEmail() + "}......");
                    EmailUtil.sendEmailAttachRetiree(emailMap);
                    msg.setSendStatus(1);
                    log.info("成功结束发邮件给{" + msg.getEmail() + "}......");
                } else {
                    log.info("接收邮件的邮箱地址为空，不执行邮件发送");
                }
            }
        } catch (Exception e) {
            log.error("消息发送失败：{}", e.getMessage());
            e.printStackTrace();
            msg.setSendStatus(2);
        } finally {
            robotWarnMsgListDao.insert(msg);
        }
    }

    /**
     * 获取预警接收人信息
     *
     * @param configBaseId 预警配置id
     * @return
     */
    protected final List<RobotWarnRecipient> getRecipients(Integer configBaseId) {
        Example example = new Example(RobotWarnRecipient.class);
        example.createCriteria().andEqualTo("warnBaseId", configBaseId);
        return robotWarnRecipientDao.selectByExample(example);
    }

    protected final String buildTable(List<Map<String, Object>> dataSet, List<String> headerStrs, List<String> hesderKeys) {
        StringBuilder result = new StringBuilder(tableStart);
        for (String str : headerStrs) {
            if ("序号".equals(str)) {
                result.append("<th class='text-center xh'>");
            } else {
                result.append("<th class='text-center'>");
            }
            result.append(str);
            result.append("</th>");
        }
        result.append("</tr></thead><tbody>");
        int index = 1;
        for (Map<String, Object> item : dataSet) {
            result.append("<tr>");
            for (int i = 0; i < headerStrs.size(); i++) {
                String str = headerStrs.get(i);
                result.append("<td>");
                String val = item.get(hesderKeys.get(i)) == null ? "" : (String) item.get(hesderKeys.get(i));
                result.append("序号".equals(str) ? (index + "") : val);
                result.append("</td>");
            }
            result.append("</tr>");
            index++;
        }
        result.append(tableEnd);
        return result.toString();
    }

    protected final RobotWarnMsgList createRobotWarnMsgListEntity(RobotWarnConfigBase configBase) {
        RobotWarnMsgList msg = new RobotWarnMsgList();
        msg.setSendStatus(0);
        msg.setWarnBaseId(configBase.getId());
        msg.setWarnType(configBase.getType());
        msg.setWarnTypeName(configBase.getTypeName());
        msg.setCreateTime(new Date());
        return msg;
    }
}
