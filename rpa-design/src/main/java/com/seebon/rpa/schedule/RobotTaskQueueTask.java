package com.seebon.rpa.schedule;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import com.seebon.rpa.service.RobotTaskQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class RobotTaskQueueTask {
    @Autowired
    private RobotTaskQueueService taskQueueService;

    //@Scheduled(cron = "0 */5 * * * ?")
    public void updatePreTime() {
        TimeInterval timer = DateUtil.timer();
        log.info("开始-执行自动更新预估时间.");
        taskQueueService.updatePreTime(null, null);
        log.info("完成-执行自动更新预估时间,耗时：" + timer.intervalSecond() + " 秒");
    }

    @Scheduled(cron = "0 */30 * * * ?")
    public void cleanTaskQueue() {
        TimeInterval timer = DateUtil.timer();
        log.info("开始-作废掉超过5小时未执行的任务.");
        taskQueueService.cleanTaskQueue();
        log.info("完成-作废掉超过5小时未执行的任务,耗时：" + timer.intervalSecond() + " 秒");
    }
}
