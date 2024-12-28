package com.seebon.rpa.service;

import com.seebon.rpa.entity.agent.dto.CustomerOrgBusinessDTO;
import com.seebon.rpa.entity.robot.RobotCommand;
import com.seebon.rpa.entity.robot.RobotTaskArgs;
import com.seebon.rpa.entity.robot.RobotTaskNotice;
import com.seebon.rpa.entity.robot.dto.TaskInfoDTO;
import com.seebon.rpa.entity.robot.dto.design.RobotTaskDataDTO;
import com.seebon.rpa.entity.robot.dto.design.RobotTaskParamsDTO;
import com.seebon.rpa.entity.robot.vo.*;

import java.util.List;
import java.util.Map;

public interface RobotTaskService {

    List<RobotClientAppVO> listTaskByCustId();

    List<RobotClientVO> listTaskTableByCustId(String keyword, String hasNotice);

    List<RobotAppFormVO> queryFormVO(String appCode);

    /**
     * 暂停或启动任务
     *
     * @param status 0-暂停  1-启动
     */
    Integer updateStatus(String appCode, Integer status, String taskCode, String comment);

    Integer addTask(List<RobotTaskArgs> robotTaskArgs, String appCode, String machineCode);

    /**
     * 校验配置信息
     */
    void check(List<RobotTaskArgs> robotTaskArgs, String appCode,Integer addrId);

    /**
     * 修改配置信息时校验
     */
    void checkEdit(List<RobotTaskArgs> robotTaskArgs, String appCode);

    List<RobotTaskArgs> echoTaskArgs(String taskCode);

    Integer editArgs(List<RobotTaskArgs> robotTaskArgs, String taskCode,String originalCompanyName,String originalAccountNumber);

    String startTask(RobotCommand robotCommand);

    Integer selectCustAccount();

    List<RobotCommandVO> getRobotCommandExeList();

    Boolean checkTaskRunning(List<TaskInfoDTO> taskInfoList);

    void addTaskCommand(Integer clientId, String addrName, String accountNumber, Integer businessType);

    Map<String,Object> runTestCommand(String taskCode, String flowCode, String stepCode);

    Integer getTestTaskStatus(String taskCode);

    List<CustomerOrgBusinessDTO> getCustOrgBusiness(Integer clientId, String addrName, String orgName);

    String runTask(Integer clientId, String addrName, String orgName, List<Integer> businessTypes);

    List<RobotTaskNotice> listTaskNotice();

    void regainTask(String appCode, String taskCode);

    void delayTask(String taskCode, String payDate);

    RobotTaskDataDTO getTaskList(RobotTaskParamsDTO dto);

    Integer batchEnableOrStop(RobotTaskDataDTO dto);
}
