package com.seebon.rpa.schedule;


import com.google.common.collect.Maps;
import com.seebon.common.utils.DateUtil;
import com.seebon.rpa.entity.robot.RobotExecutionFile;
import com.seebon.rpa.entity.robot.dto.design.RobotExecutionDetailMo;
import com.seebon.rpa.entity.robot.dto.design.RobotExecutionFileInfo;
import com.seebon.rpa.entity.robot.dto.design.RobotExecutionMo;
import com.seebon.rpa.entity.robot.dto.design.RobotTaskQueueGenerate;
import com.seebon.rpa.remote.RpaSaasService;
import com.seebon.rpa.repository.mongodb.RobotExecutionRepository;
import com.seebon.rpa.repository.mongodb.RobotTaskQueueGenerateRepository;
import com.seebon.rpa.repository.mysql.RobotExecutionFileDao;
import com.seebon.rpa.repository.mysql.RobotExecutionFileInfoDao;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 日常清除数据
 * http://192.168.0.87:8080/browse/RPA-5791
 * 执行明细数据太大，需要定时清理
 */
@Slf4j
@Component
public class RobotCleanDataTask {

    @Autowired
    private RobotExecutionFileDao robotExecutionFileDao;

    @Autowired
    private RobotExecutionFileInfoDao robotExecutionFileInfoDao;

    @Autowired
    private RobotExecutionRepository robotExecutionRepository;

    @Autowired
    private ScheduledRefreshScopeProperties scheduledRefreshScopeProperties;

    @Autowired
    private RobotTaskQueueGenerateRepository queueGenerateRepository;

    @Autowired
    private RpaSaasService rpaSaasService;

    final static Integer loopMax = 1000;
    final static Integer batchSize = 5000;

    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanTask(){

        if (!scheduledRefreshScopeProperties.getScheduleCleanDataTaskOpen()) {
            log.info("定时任务未开启，不执行清除数据任务");
            return;
        }
        // 执行删除任务
        run();
    }

    public void run(){
        /**
         * 清除任务生成日志记录数据
         */
        try {
            int i = 0;
            while (i < loopMax) {
                // 查询小于
                List<RobotTaskQueueGenerate> byEndTimeLimit = queueGenerateRepository.findOnlyIdByEndTimeLimit(DateUtil.addDays(new Date(), -7),batchSize);
                if (CollectionUtils.isNotEmpty(byEndTimeLimit)) {
                    List<String> ids = byEndTimeLimit.parallelStream().map(RobotTaskQueueGenerate::getId).collect(Collectors.toList());
                    queueGenerateRepository.deleteDetailByIds(ids);
                }
                if (CollectionUtils.isEmpty(byEndTimeLimit) || byEndTimeLimit.size() != batchSize) {
                    break;
                }
                i++;
            }
        } catch (Exception e) {
            log.error("清除任务生成日志记录数据异常:",e);
        }

        /**
         * 执行时间数据（mongo）及附件（fdfs)，保留3个月，
         */
        Date retainDate = DateUtil.addMonths(new Date(), -3);
//        Date retainDate = DateUtil.parseDate("2024-05-14 23:50:10","yyyy-MM-dd HH:mm:ss");

        HashMap<String , Object> params = Maps.newHashMap();
        params.put("ltUpdateDate",retainDate);
        params.put("limit",batchSize);

        /* 客户机器人执行文件记录 */
        // 清除执行文件，涉及mysql robot_execution_file
        log.info("--------------开始清除日志数据----------");
        int i = 0;
        while (i < loopMax) {
            List<RobotExecutionFile> robotExecutionFiles = robotExecutionFileDao.selectList(params);
            if (CollectionUtils.isEmpty(robotExecutionFiles)) {
                break;
            }
            List<Integer> idList = robotExecutionFiles.parallelStream().map(RobotExecutionFile::getId).collect(Collectors.toList());
            Example example = new Example(RobotExecutionFile.class);
            System.out.println(robotExecutionFiles);
            example.createCriteria().andIn("id",idList);
            robotExecutionFileDao.deleteByExample(example);
            i ++ ;
        }

        /* 客户机器人执行文件明细记录 及 文件数据 */
        // 清除执行文件信息，涉及mysql robot_execution_file_info，针对fdfs文件服务器文件删除操作
        i = 0;
        params = Maps.newHashMap();
        params.put("ltCreateDate",retainDate);
        params.put("limit", batchSize);
        while (i < loopMax) {
            List<RobotExecutionFileInfo> robotExecutionFiles = robotExecutionFileInfoDao.selectList(params);
            if (CollectionUtils.isEmpty(robotExecutionFiles)) {
                break;
            }
            robotExecutionFiles.forEach(fileInfo -> {
                if(fileInfo.getFileId() != null && StringUtils.isNotEmpty(fileInfo.getFilePath())) {
                    // 调用删除文件
                    rpaSaasService.fileDelete(fileInfo.getFileId(),fileInfo.getFilePath());
                    robotExecutionFileInfoDao.deleteById(fileInfo.getId());
                }
            });
            i ++ ;
        }

        // 基于id转时间提高执行效率;
        //清理执行日志 涉及mongodb robot_execution
        i = 0;
        while (i < loopMax) {
            List<RobotExecutionMo> byEndTimeLimit = robotExecutionRepository.findByEndTimeLimit(retainDate, batchSize);
            if (CollectionUtils.isNotEmpty(byEndTimeLimit)) {
                List<String> ids = byEndTimeLimit.parallelStream().map(RobotExecutionMo::getId).collect(Collectors.toList());
                robotExecutionRepository.deleteByIds(ids);
            }
            if (CollectionUtils.isEmpty(byEndTimeLimit) || byEndTimeLimit.size() != batchSize) {
                break;
            }
            i++;
        }
        //清理执行日志明细 涉及mongodb robot_execution_detail
        i = 0;
        while (i < loopMax) {
            List<RobotExecutionDetailMo> byEndTimeLimit = robotExecutionRepository.findDetailByEndTimeLimit(retainDate, batchSize);
            if (CollectionUtils.isNotEmpty(byEndTimeLimit)) {
                List<String> ids = byEndTimeLimit.parallelStream().map(RobotExecutionDetailMo::getId).collect(Collectors.toList());
                robotExecutionRepository.deleteDetailByIds(ids);
            }
            if (CollectionUtils.isEmpty(byEndTimeLimit) || byEndTimeLimit.size() != batchSize) {
                break;
            }
            i++;
        }
        log.info("--------------结束清除日志数据----------");

    }


}
