package com.seebon.rpa.service.impl;

import com.alibaba.fastjson.JSON;
import com.seebon.rpa.entity.robot.RobotExecutionMonitor;
import com.seebon.rpa.entity.robot.vo.RobotTaskQueueVO;
import com.seebon.rpa.remote.RpaDesignService;
import com.seebon.rpa.repository.mapper.RobotExecutionMonitorMapper;
import com.seebon.rpa.service.RobotExecutionMonitorService;
import com.seebon.rpa.utils.FileStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class RobotExecutionMonitorServiceImpl implements RobotExecutionMonitorService {
    @Autowired
    private RobotExecutionMonitorMapper executionMonitorMapper;
    @Autowired
    private RpaDesignService designService;

    @Override
    public void addMonitor(RobotTaskQueueVO queueVO, Date startTime) {
        try {
            RobotExecutionMonitor monitor = executionMonitorMapper.selectByExecutionCode(queueVO.getExecutionCode());
            if (monitor == null) {
                log.info("任务:executionCode=" + queueVO.getExecutionCode() + " 执行完成，监控数据为空.");
                return;
            }
            if (monitor.getDeclareNum() == 0 && monitor.getSuccessNum() == 0) {
                log.info("任务:executionCode=" + queueVO.getExecutionCode() + "执行完成，更新监控数据：" + JSON.toJSONString(monitor));
                return;
            }
            RobotExecutionMonitor executionMonitor = new RobotExecutionMonitor();
            executionMonitor.setExecutionCode(queueVO.getExecutionCode());
            executionMonitor.setClientId(queueVO.getClientId());
            executionMonitor.setTaskCode(queueVO.getTaskCode());
            executionMonitor.setMachineCode(FileStorage.loadMachineCodeFromDisk());
            executionMonitor.setStartTime(startTime);
            executionMonitor.setEndTime(new Date());
            executionMonitor.setDeclareNum(monitor.getDeclareNum());
            executionMonitor.setSuccessNum(monitor.getSuccessNum());
            executionMonitor.setZyNum(monitor.getZyNum());
            executionMonitor.setJyNum(monitor.getJyNum());
            executionMonitor.setTjNum(monitor.getTjNum());
            executionMonitor.setBjNum(monitor.getBjNum());
            executionMonitorMapper.insertSelective(executionMonitor);
        } catch (Exception e) {
            log.error("更新监控数据异常." + e.getMessage(), e);
        }
    }

    @Override
    public Integer syncMonitorData() {
        List<RobotExecutionMonitor> monitorList = executionMonitorMapper.selectUnSyncMonitor();
        int result = designService.addMonitor(monitorList);
        if (result > 0) {
            for (RobotExecutionMonitor monitor : monitorList) {
                RobotExecutionMonitor update = new RobotExecutionMonitor();
                update.setId(monitor.getId());
                update.setSyncTime(new Date());
                executionMonitorMapper.updateByPrimaryKeySelective(update);
            }
        }
        return monitorList.size();
    }
}
