package com.seebon.rpa.runner;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.seebon.rpa.context.RobotContext;
import com.seebon.rpa.entity.robot.*;
import com.seebon.rpa.entity.robot.dto.AppVersionDTO;
import com.seebon.rpa.entity.robot.dto.RobotAppDTO;
import com.seebon.rpa.entity.robot.vo.RobotAppCaVO;
import com.seebon.rpa.entity.robot.vo.RobotTaskQueueVO;
import com.seebon.rpa.remote.RpaDesignService;
import com.seebon.rpa.repository.mapper.*;
import com.seebon.rpa.service.*;
import com.seebon.rpa.utils.FileStorage;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 拉取数据同步
 */
@Slf4j
@Component
public class SyncInService {
    @Autowired
    private RobotAppMapper appMapper;
    @Autowired
    private RobotFlowMapper flowMapper;
    @Autowired
    private RobotFlowStepMapper flowStepMapper;
    @Autowired
    private RobotArgsDefineMapper argsDefineMapper;
    @Autowired
    private RobotAppConditionMapper conditionMapper;
    @Autowired
    private RobotAppFormMapper formMapper;
    @Autowired
    private RobotAppGroupMapper groupMapper;
    @Autowired
    private RobotAppFormGroupMapper formGroupMapper;
    @Autowired
    private RobotAppScheduleMapper scheduleMapper;
    @Autowired
    private RobotTaskScheduleMapper taskScheduleMapper;
    @Autowired
    private RobotAppGeneralPlanMapper generalPlanMapper;
    @Autowired
    private RobotTaskMapper taskMapper;
    @Autowired
    private RobotTaskArgsMapper taskArgsMapper;
    @Autowired
    private RobotAppEnvMapper appEnvMapper;
    @Autowired
    private RobotFlowTemplateMapper flowTemplateMapper;
    @Autowired
    private RobotFlowTemplateStepMapper flowTemplateStepMapper;
    @Autowired
    private RobotFlowStepFlowMapper flowStepFlowDao;
    @Autowired
    private RobotFlowStepArgsMapper flowStepArgsDao;
    @Autowired
    private RobotClientMapper clientMapper;
    @Autowired
    private RobotTaskSessionKeepService taskSessionKeepService;
    @Autowired
    private RobotTaskQueueService taskQueueService;
    @Autowired
    private RobotAppCaService appCaService;
    @Autowired
    private RobotConfigService configService;

    @Autowired
    private RobotContext ctx;
    @Autowired
    private RpaDesignService designService;
    @Autowired
    private RobotRunService runService;
    @Autowired(required = false)
    private HikariDataSource dataSource;
    @Autowired
    private RobotRunner robotRunner;

    public Integer syncAppData() {
        if (this.isDevEnv("true")) {
            return 0;
        }
        if (ctx.isRun()) {
            log.info("盒子正在执行其他流程,等待下次同步");
            return 0;
        }
        Integer count = 0;
        List<AppVersionDTO> appVersions = designService.allApps();
        List<RobotApp> apps = appMapper.selectAll();
        Map<String, String> appMap = Maps.newHashMap();
        for (RobotApp app : apps) {
            appMap.put(app.getAppCode(), app.getVersion());
        }
        for (AppVersionDTO appVersion : appVersions) {
            String appCode = appVersion.getAppCode();
            String version = appVersion.getVersion();
            if (!appMap.containsKey(appCode) || !appMap.get(appCode).equals(version)) {
                RobotAppDTO appDTO = designService.findApp(appCode);
                if (appDTO != null) {
                    this.upset(appDTO);
                    count++;
                }
            }
        }
        return count;
    }

    public Integer syncAppTaskData() {
        if (this.isDevEnv("true")) {
            return 0;
        }
        String machineCode = FileStorage.loadMachineCodeFromDisk();
        RobotAppDTO robotAppDTO = designService.listTask(machineCode);
        if (robotAppDTO == null || CollectionUtils.isEmpty(robotAppDTO.getRobotTaskList())) {
            return 0;
        }
        //先回调
        designService.callbackTask(machineCode);
        //更新数据
        this.upsetTask(robotAppDTO);
        //执行任务条数
        return robotAppDTO.getRobotTaskList().size();
    }

    public Integer syncTaskSessionKeep() {
        if (this.isDevEnv("true")) {
            return 0;
        }
        String machineCode = FileStorage.loadMachineCodeFromDisk();
        List<RobotTaskSessionKeep> list = designService.listTaskSessionKeep(machineCode);
        if (CollectionUtils.isEmpty(list)) {
            return 0;
        }
        //先回调
        designService.callbackTaskSessionKeep(machineCode);
        //更新数据
        taskSessionKeepService.saveSessionKeep(list);
        //执行任务条数
        return list.size();
    }

    public Integer syncAppCa() {
        if (this.isDevEnv("true")) {
            return 0;
        }
        String machineCode = FileStorage.loadMachineCodeFromDisk();
        List<RobotAppCaVO> list = designService.listAppCa(machineCode);
        if (CollectionUtils.isEmpty(list)) {
            return 0;
        }
        //先回调
        designService.callbackAppCa(machineCode);
        //更新数据
        appCaService.saveAppCa(list);
        //执行任务条数
        return list.size();
    }

    public Integer syncClient() {
        if (this.isDevEnv("true")) {
            return 0;
        }
        String machineCode = FileStorage.loadMachineCodeFromDisk();
        RobotClient client = designService.getClient(machineCode);
        if (client == null) {
            return 0;
        }
        //先回调
        designService.callbackClient(machineCode);
        //更新数据
        clientMapper.deleteByPrimaryKey(client.getId());
        clientMapper.insert(client);
        //执行任务条数
        return 1;
    }

    public Integer syncConfig() {
        if (this.isDevEnv("true")) {
            return 0;
        }
        String machineCode = FileStorage.loadMachineCodeFromDisk();
        List<RobotConfig> list = designService.listConfig(machineCode);
        if (CollectionUtils.isEmpty(list)) {
            return 0;
        }
        //先回调
        designService.callbackConfig(machineCode);
        //更新数据
        configService.saveConfig(list);
        //执行任务条数
        return list.size();
    }

    public Integer syncCommandData() {
        List<RobotCommand> commands = designService.findCommand(this.getMachineCode());
        if (CollectionUtils.isEmpty(commands)) {
            return 0;
        }
        for (RobotCommand command : commands) {
            //先回调
            designService.callbackCommand(command);
            //测试流程
            if ("runTestFlow".equals(command.getCommand())) {
                if (StringUtils.isBlank(command.getArgs())) {
                    log.info("指令参数为空.");
                    return 0;
                }
                Map<String, String> args = JSON.parseObject(command.getArgs(), Map.class);
                String flowCode = args.get("flowCode");
                String stepCode = args.get("stepCode");
                if (StringUtils.isBlank(flowCode)) {
                    log.info("执行流程flowCode不存在.");
                    return 0;
                }
                RobotFlowStep flowStep = new RobotFlowStep();
                flowStep.setFlowCode(flowCode);
                flowStep.setStepCode(stepCode);
                List<RobotFlowStep> list = designService.listFlowStep(flowStep);
                runService.startTestFlowStep(command.getTaskCode(), flowCode, command.getUuid(), list);
            }
        }
        return commands.size();
    }

    public Integer syncTaskQueueData() {
        RobotTaskQueueVO queueVO = designService.findTaskQueue(this.getMachineCode());
        if (queueVO == null) {
            return 0;
        }
        //保存队列数据
        taskQueueService.saveTaskQueue(queueVO);
        //执行任务
        robotRunner.startTask(queueVO);
        return 1;
    }

    private void upset(RobotAppDTO appDTO) {
        appMapper.clean(appDTO.getAppCode());

        List<RobotFlowTemplate> flowTemplates = appDTO.getRobotFlowTemplate();
        if (CollectionUtils.isNotEmpty(flowTemplates)) {
            for (RobotFlowTemplate flowTemplate : flowTemplates) {
                appMapper.cleanFlowTemplate(flowTemplate.getTemplateFlowCode());
                flowStepArgsDao.deleteByFlowCode(flowTemplate.getFlowCode());
            }
        }

        //应用
        RobotApp app = new RobotApp();
        BeanUtils.copyProperties(appDTO, app);
        app.setCreateTime(new Date());
        appMapper.insertSelective(app);

        //流程
        if (CollectionUtils.isNotEmpty(appDTO.getRobotFlows())) {
            flowMapper.insertList(appDTO.getRobotFlows());
        }
        //流程步骤
        if (CollectionUtils.isNotEmpty(appDTO.getRobotFlowSteps())) {
            flowStepMapper.insertList(appDTO.getRobotFlowSteps());
        }
        //应用配置分组条件
        if (CollectionUtils.isNotEmpty(appDTO.getConfigConditions())) {
            conditionMapper.insertList(appDTO.getConfigConditions());
        }
        //机器人参数定义
        if (CollectionUtils.isNotEmpty(appDTO.getRobotArgsDefines())) {
            argsDefineMapper.insertList(appDTO.getRobotArgsDefines());
        }
        //应用配置维护表单
        if (CollectionUtils.isNotEmpty(appDTO.getConfigForms())) {
            formMapper.insertList(appDTO.getConfigForms());
        }
        //应用配置分组
        if (CollectionUtils.isNotEmpty(appDTO.getConfigGroups())) {
            groupMapper.insertList(appDTO.getConfigGroups());
        }
        //表单与组中间表
        if (CollectionUtils.isNotEmpty(appDTO.getFormGroupList())) {
            formGroupMapper.insertList(appDTO.getFormGroupList());
        }
        //应用执行计划
        if (CollectionUtils.isNotEmpty(appDTO.getScheduleList())) {
            scheduleMapper.insertList(appDTO.getScheduleList());
        }
        //任务执行计划
        if (CollectionUtils.isNotEmpty(appDTO.getTaskScheduleList())) {
            taskScheduleMapper.insertList(appDTO.getTaskScheduleList());
        }
        //通用计划表
        if (CollectionUtils.isNotEmpty(appDTO.getGeneralPlanList())) {
            generalPlanMapper.insertList(appDTO.getGeneralPlanList());
        }
        //应用环境
        if (CollectionUtils.isNotEmpty(appDTO.getRobotAppEnv())) {
            appEnvMapper.clean(appDTO.getAppCode());
            appEnvMapper.insertList(appDTO.getRobotAppEnv());
        }
        Set<String> flowCodes = appDTO.getRobotFlows().stream().map(f -> f.getFlowCode()).collect(Collectors.toSet());
        for (String flowCode : flowCodes) {
            flowTemplateMapper.clean(flowCode);
        }

        //机器人流程模板关系
        if (CollectionUtils.isNotEmpty(flowTemplates)) {
            flowTemplateMapper.insertList(flowTemplates);
        }
        //机器人流程模板步骤关系
        if (CollectionUtils.isNotEmpty(appDTO.getRobotFlowTemplateStep())) {
            flowTemplateStepMapper.insertList(appDTO.getRobotFlowTemplateStep());
        }

        //机器人流程步骤流程
        List<RobotFlowStepFlow> flowStepFlows = appDTO.getRobotFlowStepFlow();
        if (CollectionUtils.isNotEmpty(flowStepFlows)) {
            for (RobotFlowStepFlow stepFlow : flowStepFlows) {
                flowStepFlowDao.deleteByPrimaryKey(stepFlow.getId());
            }
            flowStepFlowDao.insertList(flowStepFlows);
        }

        //机器人流程模板步骤关系
        List<RobotFlowStepArgs> flowStepArgs = appDTO.getRobotFlowStepArgs();
        if (CollectionUtils.isNotEmpty(flowStepArgs)) {
            flowStepArgsDao.insertList(flowStepArgs);
        }
    }

    private void upsetTask(RobotAppDTO appDTO) {
        String machineCode = FileStorage.loadMachineCodeFromDisk();
        //客户机器人任务
        if (CollectionUtils.isNotEmpty(appDTO.getRobotTaskList())) {
            //先删除
            appDTO.getRobotTaskList().stream().forEach(task -> {
                taskMapper.cleanByTaskCode(task.getTaskCode());
                task.setMachineCode(machineCode);
                task.setSyncMachineCode(null);
            });
            List<List<RobotTask>> groupList = Lists.partition(appDTO.getRobotTaskList(), 1000);
            for (List<RobotTask> list : groupList) {
                taskMapper.insertList(list);
            }
        }
        //客户机器人任务参数
        if (CollectionUtils.isNotEmpty(appDTO.getRobotTaskArgsList())) {
            List<List<RobotTaskArgs>> groupList = Lists.partition(appDTO.getRobotTaskArgsList(), 1000);
            for (List<RobotTaskArgs> list : groupList) {
                taskArgsMapper.insertList(list);
            }
        }
        //任务执行计划
        if (CollectionUtils.isNotEmpty(appDTO.getTaskScheduleList())) {
            List<List<RobotTaskSchedule>> groupList = Lists.partition(appDTO.getTaskScheduleList(), 1000);
            for (List<RobotTaskSchedule> list : groupList) {
                taskScheduleMapper.insertList(list);
            }
        }
    }

    private String getMachineCode() {
        String machineCode = FileStorage.loadMachineCodeFromDisk();
        if (StringUtils.isBlank(machineCode)) {
            throw new RuntimeException("机器识别码不存在,请检查.");
        }
        log.info("machineCode=" + machineCode.toUpperCase());
        return machineCode.toUpperCase();
    }

    public boolean isDevEnv(String flag) {
        if (dataSource != null && dataSource.getJdbcUrl().contains("jdbc:mysql")) {
            if (StringUtils.isNotBlank(flag)) {
                log.info("已配置开发者模式,直连mysql.取消同步");
            }
            return true;
        }
        return false;
    }
}
