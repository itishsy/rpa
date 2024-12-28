package com.seebon.rpa.service.impl;

import cn.hutool.core.lang.Dict;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.seebon.rpa.auth.SecurityContext;
import com.seebon.rpa.auth.Session;
import com.seebon.rpa.entity.robot.*;
import com.seebon.rpa.entity.robot.dto.*;
import com.seebon.rpa.entity.robot.vo.RobotAppCaVO;
import com.seebon.rpa.entity.robot.vo.RobotTaskSessionKeepVO;
import com.seebon.rpa.entity.robot.vo.RobotTaskVO;
import com.seebon.rpa.repository.mysql.*;
import com.seebon.rpa.service.RobotClientTaskService;
import com.seebon.rpa.service.RobotLoginAuthService;
import com.seebon.rpa.utils.Assert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author ZhenShijun
 * @dateTime 2022-11-25 19:08:08
 */
@Slf4j
@Service
public class RobotClientTaskServiceImpl implements RobotClientTaskService {
    @Autowired
    private RobotClientDao clientDao;
    @Autowired
    private RobotTaskDao taskDao;
    @Autowired
    private RobotAppDao robotAppDao;
    @Autowired
    private RobotClientAppDao clientAppDao;
    @Autowired
    private RobotTaskArgsDao taskArgsDao;
    @Autowired
    private RobotCommandDao commandDao;
    @Autowired
    private RobotTaskSessionKeepDao taskSessionKeepDao;
    @Autowired
    private RobotTaskScheduleDao taskScheduleDao;
    @Autowired
    private RobotLoginAuthService loginAuthService;
    @Autowired
    private RobotAppCaDao appCaDao;
    @Autowired
    private RobotConfigDao configDao;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int addOrUpdate(RobotTaskDTO taskDTO) {
        Example example = new Example(RobotTask.class);
        example.createCriteria().andEqualTo("taskCode", taskDTO.getTaskCode());
        RobotTask task = taskDao.selectOneByExample(example);
        int result = 0;
        if (task != null && task.getId() != null) {
            taskDTO.setId(task.getId());
            taskDTO.setSchedule(null);
            taskDTO.setExecCycle(null);
            taskDTO.setExecAreaTime(null);
            taskDTO.setExecStyle(null);
            taskDTO.setTaskType(null);
            taskDTO.setSyncTime(new Date());
            result = taskDao.updateByPrimaryKeySelective(taskDTO);

            Example e = new Example(RobotTaskArgs.class);
            e.createCriteria().andEqualTo("taskCode", taskDTO.getTaskCode());
            taskArgsDao.deleteByExample(e);
        } else {
            taskDTO.setClientId(SecurityContext.currentUser().getCustId());
            taskDTO.setSyncTime(new Date());
            result = taskDao.insert(taskDTO);
        }
        List<RobotTaskArgs> argsList = taskDTO.getArgsList();
        for (RobotTaskArgs args : argsList) {
            result += taskArgsDao.insert(args);
        }
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void callbackTaskStatus(RobotTaskVO taskVO) {
        //先设置为未运行
        taskDao.updateMachineRun(taskVO.getMachineCode());
        //更新运行状态
        if (StringUtils.isNotBlank(taskVO.getTaskCode())) {
            for (String taskCode : taskVO.getTaskCode().split(",")) {
                taskDao.updateTaskRun(taskCode, 1);
            }
        }
    }

    @Override
    public RobotTask findTask(String taskCode) {
        Example e = new Example(RobotTask.class);
        e.createCriteria().andEqualTo("taskCode", taskCode);
        return taskDao.selectOneByExample(e);
    }

    @Override
    public List<RobotCommand> findCommand(String machineCode) {
        if (StringUtils.isBlank(machineCode)) {
            return null;
        }
        Session session = SecurityContext.currentUser();
        List<RobotCommand> list = Lists.newArrayList();
        //deleteTask指令都获取
        Example example = new Example(RobotCommand.class);
        example.createCriteria().andEqualTo("clientId", session.getCustId()).andEqualTo("machineCode", machineCode).andEqualTo("status", 0)
                .andEqualTo("command", "deleteTask");
        List<RobotCommand> taskList = commandDao.selectByExample(example);
        if (CollectionUtils.isNotEmpty(taskList)) {
            list.addAll(taskList);
        }
        //运行指令只拿一个
        example = new Example(RobotCommand.class);
        example.orderBy("id").asc();
        example.createCriteria().andEqualTo("clientId", session.getCustId()).andEqualTo("machineCode", machineCode).andEqualTo("status", 0)
                .andNotEqualTo("command", "deleteTask");
        List<RobotCommand> runList = commandDao.selectByExample(example);
        if (CollectionUtils.isNotEmpty(runList)) {
            list.add(runList.get(0));
        }
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCommand(RobotCommand command) {
        commandDao.updateByUuid(command);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void callbackCommand(RobotCommand command) {
        //更新为-已同步
        RobotCommand update = new RobotCommand();
        update.setId(command.getId());
        update.setStatus(1);
        update.setTipStatus(0);
        update.setRemark(command.getRemark());
        update.setSyncTime(new Date());
        update.setUpdateTime(new Date());
        commandDao.updateByPrimaryKeySelective(update);

        //更新为-运行中
        if ("runTestFlow".equals(command.getCommand())) {
            taskDao.updateTaskRun(command.getTaskCode(), 1);
        }
    }

    @Override
    public RobotAppDTO listTask(String machineCode) {
        RobotAppDTO appDTO = new RobotAppDTO();
        if (StringUtils.isBlank(machineCode)) {
            return appDTO;
        }
        Session session = SecurityContext.currentUser();
        Map<String, Object> params = Maps.newHashMap();
        params.put("clientId", session.getCustId());
        params.put("machineCode", machineCode);
        List<RobotTask> taskList = taskDao.selectUnSyncTask(params);
        if (CollectionUtils.isEmpty(taskList)) {
            return appDTO;
        }
        List<String> taskCodes = taskList.stream().map(vo -> vo.getTaskCode()).distinct().collect(Collectors.toList());
        Example exampleEx = new Example(RobotTaskArgs.class);
        exampleEx.createCriteria().andIn("taskCode", taskCodes);
        List<RobotTaskArgs> taskArgsList = taskArgsDao.selectByExample(exampleEx);
        Example exampleSchedule = new Example(RobotTaskSchedule.class);
        exampleSchedule.createCriteria().andIn("taskCode", taskCodes);
        List<RobotTaskSchedule> taskScheduleList = taskScheduleDao.selectByExample(exampleSchedule);
        appDTO.setRobotTaskArgsList(taskArgsList);
        appDTO.setRobotTaskList(taskList);
        appDTO.setTaskScheduleList(taskScheduleList);
        return appDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void callbackTask(String machineCode) {
        Session session = SecurityContext.currentUser();
        Map<String, Object> params = Maps.newHashMap();
        params.put("clientId", session.getCustId());
        params.put("machineCode", machineCode);
        List<RobotTask> taskList = taskDao.selectUnSyncTask(params);
        if (CollectionUtils.isEmpty(taskList)) {
            return;
        }
        for (RobotTask task : taskList) {
            RobotTask update = new RobotTask();
            update.setId(task.getId());
            update.setSyncTime(new Date());
            update.setSync(1);
            update.setSyncFee(1);
            if (StringUtils.isBlank(task.getSyncMachineCode())) {
                update.setSyncMachineCode(machineCode);
            } else {
                update.setSyncMachineCode(task.getSyncMachineCode() + "," + machineCode);
            }
            taskDao.updateByPrimaryKeySelective(update);
        }
    }

    @Override
    public List<RobotTaskSessionKeepVO> listTaskSessionKeep(String machineCode) {
        Session session = SecurityContext.currentUser();
        return taskSessionKeepDao.selectByClientId(session.getCustId(), machineCode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void callbackTaskSessionKeep(String machineCode) {
        Session session = SecurityContext.currentUser();
        List<RobotTaskSessionKeepVO> list = taskSessionKeepDao.selectByClientId(session.getCustId(), machineCode);
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        for (RobotTaskSessionKeepVO keepVO : list) {
            RobotTaskSessionKeep update = new RobotTaskSessionKeep();
            update.setId(keepVO.getId());
            update.setSyncTime(new Date());
            update.setSyncMachineCode(this.getSyncMachineCode(keepVO, machineCode));
            taskSessionKeepDao.updateByPrimaryKeySelective(update);
        }
    }

    @Override
    public List<RobotAppCaVO> listAppCa(String machineCode) {
        return appCaDao.selectByMachineCode(machineCode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void callbackAppCa(String machineCode) {
        List<RobotAppCaVO> list = appCaDao.selectByMachineCode(machineCode);
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        for (RobotAppCaVO keepVO : list) {
            RobotAppCa update = new RobotAppCa();
            update.setId(keepVO.getId());
            update.setSyncTime(new Date());
            if (StringUtils.isBlank(keepVO.getSyncMachineCode())) {
                update.setSyncMachineCode(machineCode);
            } else {
                update.setSyncMachineCode(keepVO.getSyncMachineCode() + "," + machineCode);
            }
            appCaDao.updateByPrimaryKeySelective(update);
        }
    }

    @Override
    public RobotClient getClient(String machineCode) {
        Example example = new Example(RobotClient.class);
        example.createCriteria().andEqualTo("sync", 0).andEqualTo("machineCode", machineCode);
        RobotClient client = clientDao.selectOneByExample(example);
        if (client == null) {
            return null;
        }
        RobotClient result = new RobotClient();
        result.setId(client.getId());
        result.setMachineCode(client.getMachineCode());
        result.setMachineName(client.getMachineName());
        result.setMaxKeepNum(client.getMaxKeepNum());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void callbackClient(String machineCode) {
        Example example = new Example(RobotClient.class);
        example.createCriteria().andEqualTo("sync", 0).andEqualTo("machineCode", machineCode);
        RobotClient client = clientDao.selectOneByExample(example);
        if (client == null) {
            return;
        }
        RobotClient update = new RobotClient();
        update.setId(client.getId());
        update.setSync(1);
        clientDao.updateByPrimaryKeySelective(update);
    }

    @Override
    public List<RobotConfig> listConfig(String machineCode) {
        return configDao.selectByMachineCode(machineCode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void callbackConfig(String machineCode) {
        List<RobotConfig> list = configDao.selectByMachineCode(machineCode);
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        for (RobotConfig keepVO : list) {
            RobotConfig update = new RobotConfig();
            update.setId(keepVO.getId());
            update.setSyncTime(new Date());
            if (StringUtils.isBlank(keepVO.getSyncMachineCode())) {
                update.setSyncMachineCode(machineCode);
            } else {
                update.setSyncMachineCode(keepVO.getSyncMachineCode() + "," + machineCode);
            }
            configDao.updateByPrimaryKeySelective(update);
        }
    }

    private String getSyncMachineCode(RobotTaskSessionKeepVO keep, String machineCode) {
        if (StringUtils.isBlank(keep.getSyncMachineCode())) {
            return machineCode;
        }
        return keep.getSyncMachineCode() + "," + machineCode;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTaskSessionKeep(RobotTaskSessionKeep keep) {
        log.info("更新任务维持状态：{}", JSON.toJSONString(keep));
        RobotTaskSessionKeep update = new RobotTaskSessionKeep();
        update.setId(keep.getId());
        update.setMachineCode(keep.getMachineCode());
        if (keep.getStatus() != 3) {
            update.setMachineCode(null);
        }
        update.setStatus(keep.getStatus());
        update.setFileId(keep.getFileId());
        update.setStartKeepTime(keep.getStartKeepTime());
        update.setEndKeepTime(keep.getEndKeepTime());
        update.setSharePort(keep.getSharePort());
        update.setComment(keep.getComment());
        //共享端口号
        if (keep.getSharePort() != null) {
            taskSessionKeepDao.updateBySharePort(update);
        } else {
            taskSessionKeepDao.updateById(update);
        }
        //更新登录表的维持状态
        loginAuthService.updateLoginStatus(keep.getTaskCode(), keep.getDeclareSystem(), keep.getClientId(), keep.getStatus());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Dict startOrStop(StartRobotDTO dto) {
        Session session = SecurityContext.currentUser();
        Example example = new Example(RobotClientApp.class);
        example.createCriteria().andEqualTo("clientId", session.getCustId());
        List<RobotClientApp> clientApps = clientAppDao.selectByExample(example);
        Assert.isNotEmptyCollection(clientApps, "该客户未找到此申报账号");

        String appArgs = "addrName:" + dto.getAddrId() + ";businessType:" + dto.getBusinessType();
        String taskCode = clientAppDao.selectTaskCodeByClientId(session.getCustId(), dto.getAccountNumber(), appArgs);
        Assert.isNotBlank(taskCode, "该客户未找到此申报账号");

        taskDao.updateTaskStatus(dto.getStatus(), taskCode);
        return Dict.create().set("result", "操作成功");
    }

    @Override
    public List<GetRobotStatusRespDTO> getRobotStatus(GetRobotStatusDTO dto) {
        Example example = new Example(RobotClientApp.class);
        example.createCriteria().andIn("clientId", dto.getCustomerIds());
        List<RobotClientApp> clientApps = clientAppDao.selectByExample(example);
        Assert.isNotEmptyCollection(clientApps, "该客户未找到此申报账号");

        List<GetRobotStatusRespDTO> respDTOS = clientAppDao.selectByClientIds(dto.getTaskCodes(), dto.getCustomerIds());
        if (CollectionUtils.isEmpty(respDTOS)) {
            return Lists.newArrayList();
        }
        respDTOS.forEach(re -> {
            re.setPlatform(dto.getPlatform());
            if (StringUtils.isNotBlank(re.getAddrName())) {
                JSONObject jsonObj = JSONObject.parseObject(re.getAddrName());
                String businessTypeVal = jsonObj.getString("businessType");
                String addrName = jsonObj.getString("addrName");
                re.setAddrName(addrName);
                re.setBusinessType(businessTypeVal);
            }
        });
        return respDTOS;
    }

    @Override
    public GetTaskFlowRespDTO getTaskFlow(GetTaskFlowDTO dto) {
        return robotAppDao.getTaskFlow(dto);
    }

    @Override
    public Integer getTaskCountByCustomer(Integer clientId) {
        Example example = new Example(RobotTask.class);
        example.createCriteria().andEqualTo("clientId", clientId);
        return taskDao.selectCountByExample(example);
    }
}
