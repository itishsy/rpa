package com.seebon.rpa.runner;

import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Maps;
import com.seebon.rpa.context.RobotContext;
import com.seebon.rpa.context.constant.RobotConstant;
import com.seebon.rpa.entity.agent.enums.TplTypeEnum;
import com.seebon.rpa.entity.robot.RobotTaskSessionKeep;
import com.seebon.rpa.entity.robot.dto.ChangeLoginStatusDTO;
import com.seebon.rpa.entity.robot.vo.RobotTaskQueueVO;
import com.seebon.rpa.entity.robot.vo.RobotTaskVO;
import com.seebon.rpa.remote.RpaDesignService;
import com.seebon.rpa.repository.mapper.RobotTaskSessionKeepMapper;
import com.seebon.rpa.service.*;
import com.seebon.rpa.utils.FileStorage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 回传数据同步
 */
@Slf4j
@Component
public class SyncOutService {
    @Autowired
    private RobotTaskQueueService taskQueueService;
    @Autowired
    private RpaDesignService designService;
    @Autowired
    private RobotCommonService commonService;
    @Autowired
    private RobotExecutionService executionService;
    @Autowired
    private RobotExecutionMonitorService executionMonitorService;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private RobotContext ctx;
    @Autowired
    private SyncInService syncInService;
    @Autowired
    private RobotTaskSessionKeepService taskSessionKeepService;
    @Autowired
    private RobotTaskSessionKeepMapper taskSessionKeepMapper;

    public Integer syncTaskStatusData() {
        RobotTaskVO taskVO = new RobotTaskVO();
        taskVO.setMachineCode(FileStorage.loadMachineCodeFromDisk());

        Map<String, RobotTaskQueueVO> taskRunMap = RobotConstant.taskRunMap;
        if (MapUtils.isEmpty(taskRunMap)) {
            taskVO.setRun(0);
        } else {
            taskVO.setRun(1);
            taskVO.setTaskCode(taskRunMap.keySet().stream().collect(Collectors.joining(",")));
        }
        //回调执行状态
        designService.callbackTaskStatus(taskVO);
        return RobotConstant.taskRunMap.size();
    }

    public Integer syncTaskQueueData() {
        return taskQueueService.syncTaskQueue();
    }

    public Integer syncExecution() {
        return executionService.syncExecution();
    }

    public Integer syncMonitorData() {
        return executionMonitorService.syncMonitorData();
    }

    public Integer syncEmpAccount() {
        List<Map<String, Object>> maps = jdbcTemplate.queryForList("SELECT * FROM robot_emp_account where accountList != '' and sync = 0");
        if (CollectionUtils.isEmpty(maps)) {
            return 0;
        }
        for (Map<String, Object> hashMap : maps) {
            this.sendSaveEmpAccount(hashMap);
        }
        String ids = maps.stream().map(map -> String.valueOf(map.get("id"))).collect(Collectors.joining(","));
        // 删除个人编号
        String sql = String.format("update robot_emp_account set sync = 1 where id in (%s)", ids);
        jdbcTemplate.update(sql);
        return maps.size();
    }

    private void sendSaveEmpAccount(Map<String, Object> paramsBody) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("addrId", Integer.parseInt(paramsBody.get("addrId").toString()));
        params.put("businessType", Integer.parseInt(paramsBody.get("businessType").toString()));
        params.put("accountList", JSONArray.parseArray(paramsBody.get("accountList").toString()));
        try {
            String respBody = commonService.saveEmpAccount(params);
            log.info("保存个人编号,respBody=" + respBody);
        } catch (Exception e) {
            log.error("保存个人编号异常." + e.getMessage(), e);
        }
    }

    public void loginSuccess(String tagCode, Integer loginStatus, String comment) {
        log.info("标签编码：tagCode=" + tagCode + ",loginStatus=" + loginStatus + ",comment=" + comment);
        if (StringUtils.isBlank(tagCode) || !"10018008".equals(tagCode) || syncInService.isDevEnv(null)) {
            return;
        }
        RobotTaskSessionKeep sessionKeep = taskSessionKeepService.getSessionKeep();
        if (sessionKeep != null) {
            if (sessionKeep.getStatus() == 3) {
                this.updateLoginStatus(sessionKeep);
                this.log(sessionKeep, "当前申报户已维持,不更新状态.");
                return;
            }
            if (sessionKeep.getSharePort() != null) {
                RobotTaskSessionKeep sharePortKeep = taskSessionKeepMapper.selectSharePortKeep(sessionKeep.getSharePort());
                if (sharePortKeep != null) {
                    this.updateLoginStatus(sharePortKeep);
                    this.log(sharePortKeep, ",共享端口号：" + sharePortKeep.getSharePort() + "已维持,不更新状态.");
                    return;
                }
            }
        }
        ChangeLoginStatusDTO statusDTO = new ChangeLoginStatusDTO();
        statusDTO.setExecutionCode(ctx.get("executionCode"));
        statusDTO.setTaskCode(ctx.get("taskCode"));
        statusDTO.setLoginStatus(loginStatus);
        statusDTO.setComment(comment);
        designService.loginSuccess(statusDTO);
        log.info("登录流程执行完成,回传参数=" + JSONArray.toJSONString(statusDTO));

        // 同步会话保持
        taskSessionKeepService.syncSessionKeep(loginStatus, comment);
    }

    private void log(RobotTaskSessionKeep keep, String msg) {
        log.info("appCode：" + keep.getAppCode() + ",taskCode：" + keep.getTaskCode() + ",tplTypeCode：" + TplTypeEnum.getNameByCode(keep.getDeclareSystem()) + "," + msg);
    }

    private void updateLoginStatus(RobotTaskSessionKeep keep) {
        if (keep.getLoginStatus() == 1) {
            return;
        }
        this.log(keep, "申报户已维持,数据登录状态未更新，重新更新为已登录.");
        taskSessionKeepMapper.retryLogin(keep.getId());
    }
}
