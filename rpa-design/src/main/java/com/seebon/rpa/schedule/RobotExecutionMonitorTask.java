package com.seebon.rpa.schedule;

import cn.hutool.core.util.StrUtil;
import com.seebon.rpa.entity.robot.RobotAccountStatusHistory;
import com.seebon.rpa.entity.robot.RobotExecutionMonitor;
import com.seebon.rpa.entity.robot.RobotTask;
import com.seebon.rpa.entity.robot.dto.design.RobotExecutionDetailMo;
import com.seebon.rpa.repository.mongodb.RobotExecutionRepository;
import com.seebon.rpa.repository.mysql.RobotAccountStatusHistoryDao;
import com.seebon.rpa.repository.mysql.RobotExecutionMonitorDao;
import com.seebon.rpa.repository.mysql.RobotTaskDao;
import com.seebon.rpa.service.RobotTaskQueueService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 每隔2分钟回盘一次
 */
@Slf4j
@Component
public class RobotExecutionMonitorTask {
    @Autowired
    private RobotExecutionMonitorDao executionMonitorDao;
    @Autowired
    private RobotTaskDao taskDao;
    @Autowired
    private RobotExecutionRepository robotExecutionRepository;
    @Autowired
    private RobotAccountStatusHistoryDao robotAccountStatusHistoryDao;
    @Autowired
    private RobotTaskQueueService taskQueueService;
    @Autowired
    private SimpleLogin simpleLogin;

    @Value("${task.login.userId}")
    private Integer userId;
    @Value("${task.login.userName}")
    private String userName;
    @Value("${task.login.custId}")
    private Integer custId;

    @Scheduled(cron = "0 */2 * * * ?")
    public void monitorHandle() {
        Example exampleTask = new Example(RobotTask.class);
        exampleTask.createCriteria().andEqualTo("status", 1);
        List<RobotTask> taskList = taskDao.selectByExample(exampleTask);
        if (CollectionUtils.isEmpty(taskList)) {
            return;
        }
        for (RobotTask task : taskList) {
            List<RobotExecutionMonitor> monitorList = executionMonitorDao.selectByTaskCode(task.getTaskCode(), task.getMachineCode());
            if (CollectionUtils.isEmpty(monitorList)) {
                continue;
            }
            //最近5条数据-都没有成功数
            List<RobotExecutionMonitor> successList = monitorList.stream().filter(mo -> mo.getSuccessNum() != 0).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(successList)) {
                continue;
            }
            if (monitorList.size() == 5 && task.getStatus() == 1) {
                List<String> executionCodes = monitorList.stream().map(mo -> mo.getExecutionCode()).distinct().collect(Collectors.toList());
                List<RobotExecutionDetailMo> detailList = robotExecutionRepository.listRobotExecutionDetail(executionCodes);
                String error = "";
                if (CollectionUtils.isNotEmpty(detailList)) {
                    error = detailList.stream().map(de -> de.getError()).distinct().collect(Collectors.joining(","));
                }
                this.update(task, 0, "连续5次操作失败，自动停用：" + error);
            }
        }
    }

    private void update(RobotTask task, Integer status, String comment) {
        //更新状态
        taskDao.updateStatus(task.getAppCode(), task.getTaskCode(), status, StrUtil.sub(comment, 1, 100));

        //中断任务队列
        taskQueueService.stopTask(task.getTaskCode(), comment);

        //历史记录
        RobotAccountStatusHistory accountStatusHistory = new RobotAccountStatusHistory();
        accountStatusHistory.setTaskCode(task.getTaskCode());
        accountStatusHistory.setStatus(status);
        if (StringUtils.isNotBlank(comment)) {
            accountStatusHistory.setRemark(StrUtil.sub(comment, 1, 100));
        }
        accountStatusHistory.setCreateId(userId);
        accountStatusHistory.setCreateName(userName);
        accountStatusHistory.setCreateTime(new Date());
        robotAccountStatusHistoryDao.insertSelective(accountStatusHistory);
    }
}
