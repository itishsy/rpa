package com.seebon.rpa.schedule.RobotWarnTask;

import com.alibaba.fastjson.JSON;
import com.seebon.rpa.entity.robot.dto.design.MonitorCountVo;
import com.seebon.rpa.service.MonitorStationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 监控台定时生成当日统计数据
 *
 * @author zjf
 * @dateTime 2023-07-26
 */
@Component
public class MonitorCountWarnTask {
    private final String key = "MONITOR_COUNT";
    @Autowired
    private MonitorStationService monitorStationService;
    @Autowired
    private RedisTemplate redisTemplate;

    @Scheduled(cron = "0 0 23 * * ?")
    public void runTask() {
        MonitorCountVo monitorCount = monitorStationService.getMonitorCount();
        redisTemplate.opsForValue().set(key, JSON.toJSONString(monitorCount));
    }
}
