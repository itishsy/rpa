package com.seebon.rpa.schedule.RobotWarnTask;

import com.alibaba.fastjson.JSON;
import com.seebon.rpa.entity.robot.dto.design.RobotCountVO;
import com.seebon.rpa.service.RobotAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * TODO
 *
 * @author zjf
 * @describe 统计应用指标
 * @date 2024-01-09 13:54
 */
@Component
public class RobotAppCountTask{
    private final String key = "ROBOT_APP_COUNT";
    @Autowired
    private RobotAppService robotAppService;
    @Autowired
    private RedisTemplate redisTemplate;

    @Scheduled(cron = "0 0 23 * * ?")
    public void runTask() {
        RobotCountVO robotCountVO = robotAppService.getStatusCount();
        redisTemplate.opsForValue().set(key, JSON.toJSONString(robotCountVO));
    }
}
