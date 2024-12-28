package com.seebon.rpa.schedule.robotClientCheckHeartBeatTask;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.seebon.rpa.auth.SecurityContext;
import com.seebon.rpa.entity.robot.RobotClient;
import com.seebon.rpa.entity.robot.RobotClientTrack;
import com.seebon.rpa.entity.robot.dto.design.OperationRequestVo;
import com.seebon.rpa.entity.robot.enums.WarnStatus;
import com.seebon.rpa.entity.robot.po.design.RobotOperationMessageConfig;
import com.seebon.rpa.repository.mysql.RobotClientDao;
import com.seebon.rpa.repository.mysql.RobotClientTrackDao;
import com.seebon.rpa.repository.mysql.RobotTaskDao;
import com.seebon.rpa.schedule.SimpleLogin;
import com.seebon.rpa.service.OperationMonitorService;
import com.seebon.rpa.service.RobotWarnMessageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class RobotCheckHeartBeatTask {
    @Autowired
    private RobotClientDao robotClientDao;

    @Autowired
    private RobotClientTrackDao robotClientTrackDao;

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

    //每5分钟执行一次
    @Scheduled(cron = "0 */5 * * * ?")
    public void checkHeartBeat() {
        try {
            simpleLogin.doLogin(userId, userName, custId);
            DateTime dateTime = DateUtil.offsetSecond(new Date(), -600);
            List<Integer> list = robotClientDao.selectMoreThanTenMinutes(dateTime.getTime());
            if (CollectionUtils.isEmpty(list)) {
                log.info("不存在心跳时间超过10分钟的数据.");
                return;
            }

            //修改为离线状态
            robotClientDao.updateStatusByIds(8, list);

            //插入离线记录
            updateTrack(list);

            //修改为未执行
            robotClientDao.stopTaskByIds(0, list);

            //机器人心跳异常数据录入运维监控表
            List<OperationRequestVo> clientDetails = robotClientDao.getClientDetail(list);

            //设置客户名称
            clientDetails = operationMonitorService.setCustomerName(clientDetails);

            //异常信息录入
            operationMonitorService.insertOperationDetail(clientDetails, WarnStatus.ROBOT_HEART_EXCEPTION.getCode());

            //构建短信与邮件内容
            RobotOperationMessageConfig messageConfig = robotWarnMessageService.buildContent(clientDetails, WarnStatus.ROBOT_HEART_EXCEPTION.getCode());

            //发送短信与邮件
            robotWarnMessageService.sendMsg(messageConfig);
            simpleLogin.loginOut();
        } catch (Exception e) {
            log.error("心跳检测更新盒子状态异常." + e.getMessage(), e);
        }
    }

    public void updateTrack(List<Integer> list){
        for(Integer id : list) {
            RobotClient robotClient = robotClientDao.selectByPrimaryKey(id);

            RobotClientTrack clientTrack = robotClientTrackDao.selectLatest(robotClient.getMachineCode());
            if (clientTrack != null) {
                clientTrack.setEndTime(new Date());
                robotClientTrackDao.updateByPrimaryKey(clientTrack);
                if (clientTrack.getStatus() != 8){
                    RobotClientTrack track = new RobotClientTrack();
                    track.setClientId(robotClient.getClientId());
                    track.setMachineCode(robotClient.getMachineCode());
                    track.setStatus(8);
                    track.setCreateTime(new Date());
                    robotClientTrackDao.insertSelective(track);
                }
            }
            else {
                RobotClientTrack track = new RobotClientTrack();
                track.setClientId(robotClient.getClientId());
                track.setMachineCode(robotClient.getMachineCode());
                track.setStatus(8);
                track.setCreateTime(new Date());
                robotClientTrackDao.insertSelective(track);
            }
        }
    }
}
