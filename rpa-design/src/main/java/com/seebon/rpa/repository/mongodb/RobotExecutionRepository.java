package com.seebon.rpa.repository.mongodb;

import com.seebon.rpa.entity.robot.dto.ExecutionQueryDTO;
import com.seebon.rpa.entity.robot.dto.design.RobotExecutionDetailMo;
import com.seebon.rpa.entity.robot.dto.design.RobotExecutionMo;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface RobotExecutionRepository {

    /**
     * 保存机器人执行记录
     *
     * @param execution
     */
    void save(RobotExecutionMo execution);

    /**
     * 保存机器人执行明细
     *
     * @param executionDetails
     */
    void saveDetails(List<RobotExecutionDetailMo> executionDetails);

    /**
     * 查询机器人执行记录
     *
     * @return
     */
    List<RobotExecutionMo> listRobotExecution(ExecutionQueryDTO dto);

    /**
     * 查询机器人执行明细
     *
     * @return
     */
    List<RobotExecutionDetailMo> listRobotExecutionDetail(String executionCode, String flowCode);

    List<RobotExecutionDetailMo> listRobotExecutionDetail(String executionCode, String flowCode, Integer status);


    /**
     * 查询10分钟内异常的数据
     *
     * @return
     */
    List<RobotExecutionMo> listRobotExecutionError();

    /**
     * 根据条件查询机器人执行记录
     *
     * @return
     */
    List<RobotExecutionMo> listRobotExecutionBySelect(List<String> executionCodeList);

    /**
     * 根据执行码查询机器人执行记录
     *
     * @return
     */
    List<RobotExecutionMo> listRobotExecutionByCode(String executionCode);

    int getExecCount(String taskCode, List<String> flowCodes, String date);

    RobotExecutionMo selectRecentlyOne(Integer clientId, String appCode, String taskCode, List<String> flowCodes);

    /**
     * 查询执行记录
     */
    RobotExecutionMo selectExecutionOne(String executionCode, Integer clientId, String machineCode, String appCode, String taskCode, String flowCode);

    /**
     * 读取所有账户下主流程执行的次数之和
     */
    Long selectTaskExecutionCount(Integer status);

    Integer selectExecutionCountByParams(Map<String, Object> params);

    /**
     * 执行记录明细
     */
    List<RobotExecutionDetailMo> listRobotExecutionDetail(List<String> executionCodeList);

    RobotExecutionMo selectOneById(String id);

    RobotExecutionDetailMo selectExecutionDetailOne(Map<String, Object> params);

    List<RobotExecutionMo> findExecutionOne(Integer clientId, String machineCode, String appCode, String taskCode, List<String> flowCode);

    List<RobotExecutionMo> findByEndTimeLimit(Date beforeDate,Integer batchSize);

    List<RobotExecutionDetailMo> findDetailByEndTimeLimit(Date beforeDate,Integer batchSize);

    void deleteByIds(List<String> ids);

    void deleteDetailByIds(List<String> ids);
}

