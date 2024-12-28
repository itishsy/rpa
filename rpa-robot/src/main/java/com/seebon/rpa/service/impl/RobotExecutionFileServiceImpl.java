package com.seebon.rpa.service.impl;

import cn.hutool.core.io.FileUtil;
import com.google.common.collect.Maps;
import com.seebon.common.utils.JsonUtils;
import com.seebon.rpa.context.RobotContext;
import com.seebon.rpa.context.constant.RobotConstant;
import com.seebon.rpa.entity.robot.RobotExecutionFile;
import com.seebon.rpa.remote.RpaDesignService;
import com.seebon.rpa.repository.mapper.RobotExecutionFileMapper;
import com.seebon.rpa.runner.SyncInService;
import com.seebon.rpa.service.RobotExecutionFileService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class RobotExecutionFileServiceImpl implements RobotExecutionFileService {
    @Autowired
    private RobotExecutionFileMapper executionFileMapper;
    @Autowired
    private RpaDesignService rpaDesignService;
    @Autowired
    private RobotContext ctx;
    @Autowired
    private SyncInService syncInService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveRobotExecutionFile(String flowCode, String stepCode) {
        try {
            String executionCode = ctx.getVariable(RobotConstant.EXECUTION_CODE_KEY);
            String dataPath = ctx.getVariable("dataPath");//文件目录
            String instId = ctx.getVariable("instId");
            if (StringUtils.isBlank(executionCode) || StringUtils.isBlank(dataPath) || StringUtils.isBlank(instId)) {
                log.info("executionCode、dataPath、instId 为空");
                return;
            }
            List<File> fileList = FileUtil.loopFiles(dataPath);
            for (File file : fileList) {
                if (!file.getName().contains(instId)) {
                    continue;
                }
                Example example = new Example(RobotExecutionFile.class);
                example.createCriteria().andEqualTo("filePath", file.getPath());
                int count = executionFileMapper.selectCountByExample(example);
                if (count != 0) {
                    continue;
                }
                RobotExecutionFile executionFile = new RobotExecutionFile();
                executionFile.setExecutionCode(executionCode);
                executionFile.setTaskCode(ctx.getVariable("taskCode"));
                executionFile.setFlowCode(flowCode);
                executionFile.setStepCode(stepCode);
                executionFile.setFileName(file.getName());
                executionFile.setFilePath(file.getPath());
                executionFile.setStatus(0);
                executionFile.setCreateTime(new Date());
                executionFile.setUpdateTime(new Date());
                executionFileMapper.insertSelective(executionFile);
            }
        } catch (Exception e) {
            log.error("保存执行文件记录异常。" + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer syncExecutionFile() {
        if (syncInService.isDevEnv(null)) {
            return 0;
        }
        Example example = new Example(RobotExecutionFile.class);
        example.createCriteria().andEqualTo("status", 0);
        List<RobotExecutionFile> files = executionFileMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(files)) {
            return 0;
        }
        for (RobotExecutionFile executionFile : files) {
            RobotExecutionFile update = new RobotExecutionFile();
            update.setId(executionFile.getId());
            update.setStatus(1);
            update.setUpdateTime(new Date());
            update.setUploadTime(new Date());
            try {
                File file = new File(executionFile.getFilePath());
                if (!file.exists()) {
                    log.info("文件" + executionFile.getFilePath() + " 不存在,删除记录。");
                    executionFileMapper.deleteByPrimaryKey(executionFile.getId());
                    continue;
                }
                byte[] byteArray = FileUtil.readBytes(file);
                String byteString = new String(byteArray, "ISO-8859-1");

                Map<String, Object> params = Maps.newHashMap();
                params.put("fileName", executionFile.getFileName());
                params.put("byteString", byteString);
                params.put("executionCode", executionFile.getExecutionCode());
                params.put("taskCode", executionFile.getTaskCode());
                params.put("flowCode", executionFile.getFlowCode());
                params.put("stepCode", executionFile.getStepCode());

                rpaDesignService.fileUploadForServer(JsonUtils.toJSon(params));
            } catch (Exception e) {
                log.error("定时文件上传异常." + e.getMessage(), e);
                update.setStatus(2);
                update.setError(e.getMessage());
                update.setUploadTime(null);
            }
            executionFileMapper.updateByPrimaryKeySelective(update);
        }
        return files.size();
    }
}
