package com.seebon.rpa.repository.mysql;

import com.seebon.rpa.entity.robot.RobotCustomer;
import com.seebon.rpa.entity.robot.RobotExecution;
import com.seebon.rpa.entity.robot.RobotTask;
import com.seebon.rpa.entity.robot.dto.design.*;
import com.seebon.rpa.entity.vo.*;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface RobotExecutionDao extends Mapper<RobotExecution> {
    /**
     * 判断execution是否存在
     *
     * @param executionCode
     * @return
     */
    RobotExecution existExecution(@Param("executionCode") String executionCode);

    /**
     * 修改execution
     *
     * @param executionVO
     * @return
     */
    int updateExecution(RobotClientExecutionVO executionVO);

    /**
     * 添加execution
     *
     * @param executionVO
     * @return
     */
    int insertExecution(RobotClientExecutionVO executionVO);

    /**
     * 添加executionDetail
     *
     * @param executionDetailVO
     * @return
     */
    int insertExecutionDetail(RobotClientExecutionDetailVO executionDetailVO);

    /**
     * 删除已存在的执行明细
     *
     * @param executionCode
     * @return
     */
    int deleteExecutionDetail(@Param("executionCode") String executionCode);

    /**
     * 查看执行操作
     * @param appCode
     * @return
     */
    List<ClientExecutionVO> appExecutions(@Param("appCode") String appCode);

    /**
     * executionsDetail执行明细列表
     *
     * @param executionCode
     * @return
     */
    List<RobotClientExecutionDetailVO> executionsDetails(@Param("executionCode") String executionCode);

    List<RobotCustomer> selectCustomer();

    /**
     * 执行计划列表
     */
    RobotClientAppVo selectTask(@Param("appCode") String appCode,@Param("clientId") Integer clientId);

    RobotClientAppVo selectExec(@Param("appCode") String appCode,@Param("clientId") Integer clientId,@Param("machineCode") String machineCode);

    /**
     * 应用概况
     * @param appCode
     * @param clientId
     * @return
     */
    RobotClientAppVo selectAppBasic(@Param("appCode") String appCode, @Param("clientId") Integer clientId);

    /**
     * 编辑页面
     */
    RobotTask selectTaskEdit(Integer id);

    String selectLatelyTime(@Param("taskCodes") List<String> taskCode);

    /**
     * 执行查询列表
     * @param appCode
     * @return
     */
    RobotClientAppVo selectExecutionList(@Param("appCode") String appCode, @Param("startTime")Date startTime,
                                         @Param("endTime") Date endTime,@Param("clientId") Integer clientId,
                                         @Param("machineCode") String machineCode);

    List<RobotClientAppVo> selectClientAppExcel(@Param("keyword") String keyword,@Param("status") List<Integer> status);

    /**
     * 最新执行时间
     * @param appCode
     * @param clientId
     * @return
     */
    Date selectLastExecTime(@Param("appCode") String appCode,@Param("clientId") Integer clientId);

    Integer selectDataNumber(@Param("taskCode") String taskCode);

    Integer selectDataNumberByMachineCode(@Param("machineCode") String machineCode);


    List<Map<String,Object>> getRobotExecErrorList(@Param("rangeTime") Integer rangeTime);

    /*根据appCode查询最新的执行时间*/
    Date selectLastExecutionTimeByAppCode(@Param("appCode") String appCode);

    /**
     *查询客户详情信息
     * @param executionCodes
     * @return
     */
    List<OperationRequestVo> selectDetailByExecutionCode(@Param("executionCodes") List<String> executionCodes);

}
