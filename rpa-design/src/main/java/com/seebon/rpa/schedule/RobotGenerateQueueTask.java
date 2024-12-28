package com.seebon.rpa.schedule;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import com.seebon.rpa.entity.robot.RobotDataPreset;
import com.seebon.rpa.repository.mysql.RobotDataPresetDao;
import com.seebon.rpa.service.impl.component.RobotTaskQueueComponent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 生成调度队列
 */
@Slf4j
@Component
public class RobotGenerateQueueTask {

    @Autowired
    private RobotTaskQueueComponent queueComponent;
    @Autowired
    private ScheduledRefreshScopeProperties scheduledRefreshScopeProperties;
    @Autowired
    private RobotDataPresetDao robotDataPresetDao;
    @Autowired
    private SimpleLogin simpleLogin;
    @Value("${task.login.userId}")
    private Integer userId;
    @Value("${task.login.userName}")
    private String userName;
    @Value("${task.login.custId}")
    private Integer custId;


    private Date nextRunDate; // 下一个运行时间

    @Scheduled(cron = "*/1 * * * * ?")
    public void run1(){
        String cron = "0 */10 * * * ?";
        List<RobotDataPreset> dataPreset = robotDataPresetDao.findDataPreset(1002);
        if (CollectionUtils.isNotEmpty(dataPreset)) {
            cron = dataPreset.stream().filter(x-> StringUtils.isNotEmpty(x.getContext()))
                    .map(RobotDataPreset::getContext)
                    .findFirst()
                    .orElse(cron);
        }
        CronSequenceGenerator cronSequenceGenerator = new CronSequenceGenerator(cron);
        Date nextDate = cronSequenceGenerator.next(new Date());
        if (nextRunDate == null) {
            nextRunDate = nextDate;
        }
        Date curNextDate = nextRunDate;
        nextRunDate = cronSequenceGenerator.next(new Date());
        if (curNextDate.compareTo(new Date()) <= 0) {
            this.run();
        }
    }

    public void run() {
        if (!scheduledRefreshScopeProperties.getScheduleGenerateQueueTaskOpen()) {
            log.info("定时任务未开启，不执行自动任务队列生成");
            return;
        }
        simpleLogin.doLogin(userId, userName, custId);
        try {
            Date nowDate = new Date();
            TimeInterval timer = DateUtil.timer();
            log.info("开始-执行自动任务队列.");
            queueComponent.generateQueue(nowDate);
            log.info("完成-执行自动任务队列,耗时：" + timer.intervalSecond() + " 秒.");
        } finally {
            simpleLogin.loginOut();
        }
    }
}
