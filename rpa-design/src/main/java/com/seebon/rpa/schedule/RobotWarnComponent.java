package com.seebon.rpa.schedule;

import com.google.common.collect.Lists;
import com.seebon.common.utils.SmsUtils;
import com.seebon.rpa.RpaDesignApplication;
import com.seebon.rpa.entity.robot.po.robotWarn.RobotWarnConfigBase;
import com.seebon.rpa.entity.robot.po.robotWarn.RobotWarnMsgList;
import com.seebon.rpa.repository.mysql.RobotWarnConfigBaseDao;
import com.seebon.rpa.repository.mysql.RobotWarnMsgListDao;
import com.seebon.rpa.schedule.RobotWarnTask.RobotExecWarnTask;
import com.seebon.rpa.schedule.RobotWarnTask.RobotHeartbeatWarnTask;
import com.seebon.rpa.schedule.RobotWarnTask.RobotServiceWarnTask;
import com.seebon.rpa.schedule.RobotWarnTask.RobotUpgradeWarnTask;
import com.seebon.rpa.utils.EmailUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

/**
 * @author ZhenShijun
 * @dateTime 2022-12-01 11:32:49
 */
@Slf4j
@Component
public class RobotWarnComponent {

    @Autowired
    private RobotWarnConfigBaseDao robotWarnConfigBaseDao;

    @Autowired
    private RobotWarnMsgListDao robotWarnMsgListDao;

    protected static String taskNamePrefix = "rpaWarnTask-no-";

    public static final int MAX_THREAD_NUM = 4;

    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    @Autowired
    private RobotHeartbeatWarnTask robotHeartbeatWarnTask;

    @Autowired
    private RobotExecWarnTask robotExecWarnTask;

    @Autowired
    private RobotServiceWarnTask robotServiceWarnTask;

    @Autowired
    private RobotUpgradeWarnTask robotUpgradeWarnTask;

    @PostConstruct
    private void initAccountWarnTask() {
        new Thread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                System.out.println(123);
                Example example = new Example(RobotWarnConfigBase.class);
                example.createCriteria().andEqualTo("status", 1);
                List<RobotWarnConfigBase> robotWarnConfigBases = robotWarnConfigBaseDao.selectByExample(example);
                if (CollectionUtils.isNotEmpty(robotWarnConfigBases)) {
                    //启动
                    robotWarnConfigBases.stream().forEach(item -> {
                        start(item);
                    });
                }
            }
        }).start();
    }

    /**
     * 根据预警配置信息启动预警作业
     *
     * @param configBase
     */
    public void start(RobotWarnConfigBase configBase) {
        if (null == this.threadPoolTaskScheduler) {
            this.threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
            this.threadPoolTaskScheduler.setThreadNamePrefix(taskNamePrefix);
            this.threadPoolTaskScheduler.initialize();
            this.threadPoolTaskScheduler.setPoolSize(MAX_THREAD_NUM);
        }
        // Integer status = configBase.getStatus(); // 预警状态，1：开启，0：关闭
        Integer baseId = configBase.getId(); // 预警配置id
        String cron = configBase.getCron(); // 预警频率
        log.info("============开始启动【" + configBase.getTypeName() + "】预警作业==================");
        if (null != cron && cron.trim().length() > 0) {
            ScheduledFuture future = this.threadPoolTaskScheduler.schedule(() -> {
                run(configBase);
            }, new CronTrigger(cron));
            RpaDesignApplication.map.put(taskNamePrefix + baseId, future);
        }
        log.info("============【" + configBase.getTypeName() + "】预警作业启动成功==================");
    }

    /**
     * 根据预警配置id终止预警作业
     *
     * @param baseId
     */
    public void stop(Integer baseId) {
        ScheduledFuture scheduledFuture = RpaDesignApplication.map.get(taskNamePrefix + baseId);
        if (scheduledFuture == null) {
            return;
        }
        scheduledFuture.cancel(true);
        // 查看任务是否在正常执行之前结束,正常true
        boolean cancelled = scheduledFuture.isCancelled();
        while (!cancelled) {
            scheduledFuture.cancel(true);
        }
        RpaDesignApplication.map.remove(taskNamePrefix + baseId);
    }

    /**
     * 重启预警作业
     *
     * @param configBase
     */
    public void restart(RobotWarnConfigBase configBase) {
        stop(configBase.getId());
        start(configBase);
    }


    private void run(RobotWarnConfigBase configBase) {
        configBase = robotWarnConfigBaseDao.selectByPrimaryKey(configBase.getId());
        if (configBase.getStatus() == null || configBase.getStatus() == 0) {
            return;
        }
        String type = configBase.getType();
        if ("1028001".equals(type)) { // 机器人心跳异常
            robotHeartbeatWarnTask.runTask(configBase);
        } else if ("1028002".equals(type)) { // 机器人执行异常
            robotExecWarnTask.runTask(configBase);
        } else if ("1028003".equals(type)) { // 机器人接口服务异常
            robotServiceWarnTask.runTask(configBase);
        } else if ("1028004".equals(type)) { // 机器人盒子升级失败
            robotUpgradeWarnTask.runTask(configBase);
        }
    }

    /**
     * 没10分钟检索是否存在发送失败或者未发送的短信跟邮件预警消息，如有则继续发送
     */
    @Scheduled(cron = "0 */10 * * * ?")
    public void sendMsg() {
        Example example = new Example(RobotWarnMsgList.class);
        example.createCriteria().andIn("sendStatus", Lists.newArrayList(0, 2));
        example.setOrderByClause("create_time");
        List<RobotWarnMsgList> robotWarnMsgLists = robotWarnMsgListDao.selectByExample(example);
        if (CollectionUtils.isNotEmpty(robotWarnMsgLists)) {
            robotWarnMsgLists.stream().forEach(item -> {
                sendMsg(item);
            });
        }
    }

    /**
     * 发送预警信息
     *
     * @param msg
     */
    private void sendMsg(RobotWarnMsgList msg) {
        Integer sendWay = msg.getSendWay();
        RobotWarnMsgList item = new RobotWarnMsgList();
        item.setId(msg.getId());
        try {
            item.setSendTime(new Date());
            if (sendWay == 1) { // 短信
                if (StringUtils.isNotBlank(msg.getPhoneNumber())) {
                    log.info("开始发送短信给{" + msg.getPhoneNumber() + "}......");
                    SmsUtils.send(msg.getPhoneNumber(), msg.getSmsContent());
                    item.setSendStatus(1);
                    log.info("成功结束发短信给{" + msg.getPhoneNumber() + "}......");
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
                    item.setSendStatus(1);
                    log.info("成功结束发邮件给{" + msg.getEmail() + "}......");
                } else {
                    log.info("接收邮件的邮箱地址为空，不执行邮件发送");
                }
            }
        } catch (Exception e) {
            log.error("消息发送失败：{}", e.getMessage());
            e.printStackTrace();
            item.setSendStatus(2);
        } finally {
            robotWarnMsgListDao.updateByPrimaryKeySelective(item);
        }
    }

}
