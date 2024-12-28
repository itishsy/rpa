package com.seebon.rpa.runner;

import cn.hutool.core.date.DateUtil;
import com.seebon.rpa.entity.robot.RobotExecution;
import com.seebon.rpa.entity.robot.RobotTaskQueue;
import com.seebon.rpa.entity.robot.RobotTaskSessionKeep;
import com.seebon.rpa.repository.mapper.RobotTaskQueueMapper;
import com.seebon.rpa.repository.mapper.RobotTaskSessionKeepMapper;
import com.seebon.rpa.service.RobotAppCaService;
import com.seebon.rpa.service.RobotExecutionService;
import com.seebon.rpa.utils.Convert;
import com.seebon.rpa.utils.UsbIpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 启动时把异常或者人为中断的数据重新同步到sass
 *
 * @author: xfz
 */
@Slf4j
@Component
public class RobotSyncQueue {
    @Autowired
    private RobotTaskQueueMapper taskQueueMapper;
    @Autowired
    private RobotExecutionService executionService;
    @Autowired
    private SyncInService syncInService;
    @Autowired
    private RobotTaskSessionKeepMapper taskSessionKeepMapper;
    @Autowired
    private RobotAppCaService appCaService;

    @PostConstruct
    public void initRobot() {
        if (syncInService.isDevEnv(null)) {
            return;
        }
        try {
            Example example = new Example(RobotTaskQueue.class);
            example.createCriteria().andEqualTo("sync", 0).andIsNull("praEndTime");
            List<RobotTaskQueue> queueList = taskQueueMapper.selectByExample(example);
            if (CollectionUtils.isEmpty(queueList)) {
                log.info("没有需要同步的数据");
                return;
            }
            Date nowDate = new Date();
            for (RobotTaskQueue queue : queueList) {
                List<RobotExecution> executionList = executionService.getByExecutionCode(queue.getExecutionCode());
                RobotTaskQueue update = new RobotTaskQueue();
                update.setId(queue.getId());
                update.setStatus(3);//运行标识  1-执行中  2-待执行 3-执行中断 4-执行成功
                update.setPraEndTime(nowDate);
                update.setUpdateTime(nowDate);
                if (CollectionUtils.isNotEmpty(executionList)) {
                    update.setComment("设备重启：" + Convert.getErrorMsg(executionList));
                } else {
                    update.setComment("设备重启");
                }
                taskQueueMapper.updateByPrimaryKeySelective(update);
            }
        } catch (Exception e) {
            log.error("设备重启,同步数据异常." + e.getMessage(), e);
        }
    }

    @PostConstruct
    public void unInstallAppCa() {
        if (syncInService.isDevEnv(null)) {
            return;
        }
        try {
            log.info("设备重启，开始卸载所有证书.");
            //appCaService.unInstallAppCa();
            log.info("设备重启，完成卸载所有证书.");
        } catch (Exception e) {
            log.error("设备重启,卸载所有证书异常." + e.getMessage(), e);
        }
    }

    @PostConstruct
    public void detachUsbIp() {
        if (syncInService.isDevEnv(null)) {
            return;
        }
        try {
            String result = UsbIpUtil.detach();
            log.info("设备重启 卸载usbKey result=" + result);
        } catch (Exception e) {
            log.error("设备重启 卸载usbKey异常." + e.getMessage(), e);
        }
    }

    @PostConstruct
    public void retryLogin() {
        if (syncInService.isDevEnv(null)) {
            return;
        }
        try {
            log.info("设备重启，重新设置为已登录，校验会话维持状态.");
            Example example = new Example(RobotTaskQueue.class);
            example.createCriteria().andGreaterThanOrEqualTo("createTime", DateUtil.formatDate(DateUtil.offsetDay(new Date(), -2)) + " 00:00:00");
            List<RobotTaskQueue> queueList = taskQueueMapper.selectByExample(example);
            if (CollectionUtils.isEmpty(queueList)) {
                log.info("没有任务数据.");
                return;
            }
            List<String> taskCodeList = queueList.stream().map(vo -> vo.getTaskCode()).distinct().collect(Collectors.toList());
            example = new Example(RobotTaskSessionKeep.class);
            example.createCriteria().andIn("taskCode", taskCodeList);
            List<RobotTaskSessionKeep> list = taskSessionKeepMapper.selectByExample(example);
            if (CollectionUtils.isEmpty(list)) {
                return;
            }
            List<RobotTaskSessionKeep> sharePortList = list.stream().filter(vo -> vo.getSharePort() != null).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(sharePortList)) {
                Map<Integer, List<RobotTaskSessionKeep>> sharePortMap = sharePortList.stream().collect(Collectors.groupingBy(RobotTaskSessionKeep::getSharePort));
                for (Integer sharePort : sharePortMap.keySet()) {
                    Integer keepId = this.getKeepId(sharePortMap.get(sharePort));
                    if (keepId == null) {
                        continue;
                    }
                    taskSessionKeepMapper.retryLogin(keepId);
                }
            }
            List<RobotTaskSessionKeep> keepList = list.stream().filter(vo -> vo.getLoginStatus() == 0 && vo.getSharePort() == null).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(keepList)) {
                for (RobotTaskSessionKeep keep : keepList) {
                    taskSessionKeepMapper.retryLogin(keep.getId());
                }
            }
        } catch (Exception e) {
            log.error("设备重启,重新设置为已登录，校验会话维持状态异常." + e.getMessage(), e);
        }
    }

    private Integer getKeepId(List<RobotTaskSessionKeep> keepList) {
        List<RobotTaskSessionKeep> sessionkeepList = keepList.stream().filter(vo -> vo.getStatus() == 3 && vo.getLoginStatus() == 1).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(sessionkeepList)) {
            return null;
        }
        List<RobotTaskSessionKeep> startKeepList = sessionkeepList.stream().filter(vo -> vo.getStartKeepTime() != null).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(startKeepList)) {
            return startKeepList.get(0).getId();
        }
        return keepList.get(0).getId();
    }
}
