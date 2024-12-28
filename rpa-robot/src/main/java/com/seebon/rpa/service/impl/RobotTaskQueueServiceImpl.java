package com.seebon.rpa.service.impl;

import com.google.common.collect.Lists;
import com.seebon.rpa.entity.robot.RobotExecution;
import com.seebon.rpa.entity.robot.RobotTaskArgs;
import com.seebon.rpa.entity.robot.RobotTaskQueue;
import com.seebon.rpa.entity.robot.vo.RobotTaskQueueVO;
import com.seebon.rpa.remote.RpaDesignService;
import com.seebon.rpa.repository.mapper.RobotTaskQueueMapper;
import com.seebon.rpa.service.RobotExecutionService;
import com.seebon.rpa.service.RobotTaskQueueService;
import com.seebon.rpa.utils.Convert;
import com.seebon.rpa.utils.FileStorage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RobotTaskQueueServiceImpl implements RobotTaskQueueService {
    @Autowired
    private RobotTaskQueueMapper taskQueueMapper;
    @Autowired
    private RpaDesignService designService;
    @Autowired
    private RobotExecutionService executionService;

    @Override
    public Integer syncTaskQueue() {
        Example example = new Example(RobotTaskQueue.class);
        example.createCriteria().andEqualTo("sync", 0).andIsNotNull("praEndTime");
        List<RobotTaskQueue> list = taskQueueMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(list)) {
            return 0;
        }
        //回调执行状态
        int count = designService.callbackTaskQueue(list);
        if (count <= 0) {
            return 0;
        }
        for (RobotTaskQueue queue : list) {
            RobotTaskQueue update = new RobotTaskQueue();
            update.setId(queue.getId());
            update.setSync(1);
            update.setUpdateTime(new Date());
            taskQueueMapper.updateByPrimaryKeySelective(update);
        }
        return list.size();
    }

    @Override
    public void saveTaskQueue(RobotTaskQueueVO queueVO) {
        queueVO.setId(null);
        queueVO.setStatus(1);
        queueVO.setSync(0);
        queueVO.setMachineCode(FileStorage.loadMachineCodeFromDisk());
        queueVO.setPraStartTime(new Date());
        queueVO.setPraEndTime(null);
        queueVO.setPraTime(null);
        queueVO.setComment(null);
        taskQueueMapper.insertSelective(queueVO);
    }

    @Override
    public void updateTaskQueue(RobotTaskQueueVO queueVO) {
        List<RobotExecution> list = executionService.getByExecutionCode(queueVO.getExecutionCode());
        RobotTaskQueue taskQueue = new RobotTaskQueue();
        taskQueue.setId(queueVO.getId());
        taskQueue.setStatus(4);//运行标识  1-执行中  2-待执行 3-执行中断 4-执行成功
        taskQueue.setPraEndTime(new Date());
        taskQueue.setUpdateTime(new Date());
        if (CollectionUtils.isNotEmpty(list)) {
            taskQueue.setStatus(3);
            taskQueue.setComment(Convert.getErrorMsg(list));
        } else {
            taskQueue.setComment(queueVO.getComment());
            if (StringUtils.isNotBlank(queueVO.getComment()) && Lists.newArrayList("任务未同步到盒子,请等待.", "任务参数未同步到盒子,请等待.", "流程未同步到盒子,请等待.").contains(queueVO.getComment())) {
                taskQueue.setStatus(3);
            }
        }
        taskQueueMapper.updateByPrimaryKeySelective(taskQueue);
    }
}