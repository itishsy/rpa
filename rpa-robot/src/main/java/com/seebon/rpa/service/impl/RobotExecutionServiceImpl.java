package com.seebon.rpa.service.impl;

import cn.hutool.core.date.DateUtil;
import com.seebon.rpa.entity.robot.RobotExecution;
import com.seebon.rpa.entity.robot.RobotExecutionDetail;
import com.seebon.rpa.entity.robot.RobotFlow;
import com.seebon.rpa.entity.robot.dto.RobotExecutionDTO;
import com.seebon.rpa.entity.robot.vo.RobotTaskQueueVO;
import com.seebon.rpa.remote.RpaDesignService;
import com.seebon.rpa.repository.mapper.RobotExecutionDetailMapper;
import com.seebon.rpa.repository.mapper.RobotExecutionMapper;
import com.seebon.rpa.repository.mapper.RobotFlowMapper;
import com.seebon.rpa.runner.SyncInService;
import com.seebon.rpa.service.RobotExecutionService;
import com.seebon.rpa.utils.FileStorage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RobotExecutionServiceImpl implements RobotExecutionService {
    @Autowired
    private RobotExecutionMapper executionMapper;
    @Autowired
    private RobotExecutionDetailMapper executionDetailMapper;
    @Autowired
    private RpaDesignService designService;

    @Autowired
    private RobotFlowMapper robotFlowMapper;
    @Autowired
    private SyncInService syncInService;

    @Override
    public Integer addExecution(RobotTaskQueueVO queueVO, String flowCode) {
        RobotExecution addExecution = new RobotExecution();
        addExecution.setExecutionCode(queueVO.getExecutionCode());
        addExecution.setClientId(queueVO.getClientId());
        addExecution.setTaskCode(queueVO.getTaskCode());
        addExecution.setMachineCode(FileStorage.loadMachineCodeFromDisk());
        addExecution.setFlowCode(flowCode);
        addExecution.setStartTime(new Date());
        addExecution.setStatus(2);
        executionMapper.insertSelective(addExecution);
        return addExecution.getId();
    }

    @Override
    public Integer syncExecution() {
        if (syncInService.isDevEnv(null)) {
            return 0;
        }
        List<RobotExecutionDTO> executionList = executionMapper.selectUnSyncExecution();
        if (CollectionUtils.isEmpty(executionList)) {
            return 0;
        }
        for (RobotExecutionDTO executionDTO : executionList) {
            executionDTO.setMachineCode(FileStorage.loadMachineCodeFromDisk());
            int result = designService.addExecution(executionDTO);
            if (result > 0) {
                //更新已经同步过
                executionMapper.updateSyncStatus(executionDTO.getId());
                //没有明细记录
                if (CollectionUtils.isEmpty(executionDTO.getDetails())) {
                    continue;
                }
                String detailIds = executionDTO.getDetails().stream().map(de -> de.getId().toString()).collect(Collectors.joining(","));
                executionDetailMapper.deleteByIds(detailIds);
            }
        }

        //清理已经同步且同步时间已超过一天的数据
        this.clearExecution();

        return executionList.size();
    }


    @Override
    public Integer syncExecutionData() {
        if (syncInService.isDevEnv(null)) {
            return 0;
        }
        Example example = new Example(RobotExecution.class);
        example.createCriteria().andIsNull("syncTime");
        List<RobotExecution> executionList = executionMapper.selectByExampleAndRowBounds(example,new RowBounds(0, 5));
        if(executionList == null) {
            return 0;
        }
        for (RobotExecution execution : executionList){
            RobotExecutionDTO executionDTO = new RobotExecutionDTO();
            BeanUtils.copyProperties(execution, executionDTO);
            executionDTO.setMachineCode(FileStorage.loadMachineCodeFromDisk());
            Example example2 = new Example(RobotExecutionDetail.class);

            Example example3 = new Example(RobotFlow.class);
            example3.createCriteria().andEqualTo("flowCode", executionDTO.getFlowCode());
            RobotFlow robotFlow = robotFlowMapper.selectOneByExample(example3);
            boolean isRelationFlow = (robotFlow != null && robotFlow.getRelationFlowCode() !=null && StringUtils.isNotBlank(robotFlow.getRelationFlowCode()));

            if (isRelationFlow){
                example2.createCriteria().andEqualTo("executionCode", execution.getExecutionCode()).andEqualTo("flowCode", robotFlow.getRelationFlowCode());
            } else {
                example2.createCriteria().andEqualTo("executionCode", execution.getExecutionCode()).andEqualTo("flowCode", executionDTO.getFlowCode());
            }

            List<RobotExecutionDetail> executionDetails = executionDetailMapper.selectByExample(example2);
            log.info("同步执行明细,流程=" + executionDTO.getFlowCode() + "，执行步骤数=" + executionDetails.size() + ",引用流程=" + robotFlow.getRelationFlowCode());
            if(executionDetails.size() > 0) {
                if (isRelationFlow) {
                    for(RobotExecutionDetail detail : executionDetails){
                        detail.setFlowCode(executionDTO.getFlowCode());
                    }
                }
                executionDTO.setDetails(executionDetails);
            }

            int result = designService.addExecution(executionDTO);
            if (result > 0) {
                //更新已经同步过
                executionMapper.updateSyncStatus(executionDTO.getId());
                //没有明细记录
                if (CollectionUtils.isEmpty(executionDTO.getDetails())) {
                    continue;
                }
                String detailIds = executionDTO.getDetails().stream().map(de -> de.getId().toString()).collect(Collectors.joining(","));
                executionDetailMapper.deleteByIds(detailIds);
            }
        }

        //清理已经同步且同步时间已超过30天的数据
        this.clearExecution();

        return executionList.size();
    }

    private void clearExecution() {
        Date yesterday = DateUtil.offsetDay(new Date(), -30);
        List<Integer> idList = executionMapper.selectSyncExecution(DateUtil.formatDateTime(yesterday));
        if (CollectionUtils.isEmpty(idList)) {
            return;
        }
        log.info("清理执行记录-已经同步且同步时间已超过30天的数据:" + idList.size());
        String ids = idList.stream().map(id -> id.toString()).collect(Collectors.joining(","));
        executionMapper.deleteByIds(ids);
    }

    @Override
    public List<RobotExecution> getByExecutionCode(String executionCode) {
        Example example = new Example(RobotExecution.class);
        example.orderBy("id").desc();
        example.createCriteria().andEqualTo("status", 0).andEqualTo("executionCode", executionCode);
        List<RobotExecution> list = executionMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.stream().filter(e -> StringUtils.isNotBlank(e.getError())).collect(Collectors.toList());
    }
}
