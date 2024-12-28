package com.seebon.rpa.repository.mysql;

import com.seebon.rpa.entity.agent.dto.CustOrgTaskDTO;
import com.seebon.rpa.entity.robot.RobotTask;
import com.seebon.rpa.entity.robot.dto.RobotStatisticsDTO;
import com.seebon.rpa.entity.robot.dto.RobotTaskInfoDTO;
import com.seebon.rpa.entity.robot.dto.RobotTaskStatusChangeSendDTO;
import com.seebon.rpa.entity.robot.dto.design.RobotExecutionMo;
import com.seebon.rpa.entity.robot.dto.monitor.TodayExecDataDTO;
import com.seebon.rpa.entity.robot.dto.monitor.TodayExecQueryDTO;
import com.seebon.rpa.entity.robot.vo.RobotClientAppVO;
import com.seebon.rpa.entity.robot.vo.RobotTaskVO;
import com.seebon.rpa.entity.robot.vo.RobotVO;
import com.seebon.rpa.entity.saas.vo.system.SysUserDeclareAccountVO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface RobotTaskDao extends Mapper<RobotTask>, MySqlMapper<RobotTask> {

    Integer cleanAreaTime(@Param("id") Integer id);

    Integer updateTaskStatus(@Param("status") Integer status,@Param("taskCode") String taskCode);

    Integer updateMachineRun(@Param("machineCode") String machineCode);

    Integer updateTaskRun(@Param("taskCode") String taskCode, @Param("run") Integer run);

    List<RobotClientAppVO> selectByClientId(@Param("clientId") Integer clientId,@Param("userDeclareAccountList") List<SysUserDeclareAccountVO> userDeclareAccountList);

    int cleanSyncTime(@Param("taskCode")String taskCode);

    int cleanSync(@Param("appCode")String appCode);

    int updateComment(@Param("taskCode") String taskCode, @Param("comment") String comment);

    int updateStatus(@Param("appCode") String appCode, @Param("taskCode") String taskCode, @Param("status") Integer status, @Param("comment") String comment);

    Integer selectCountCityByCustId(@Param("clientId")Integer clientId );

    List<RobotClientAppVO> selectTableTaskVO(Map<String, Object> params);

    List<RobotVO> selectRobotVO();

    RobotExecutionMo selectByTaskCode(@Param("taskCode") String taskCode, @Param("flowCode") String flowCode);

    Map<String,Object> selectAppInfo(@Param("clientId")Integer clientId, @Param("machineCode")String machineCode, @Param("appCode")String appCode);

    List<RobotTaskInfoDTO> getRobotTasks(Map<String,Object> params);

    List<RobotTaskInfoDTO> getRobotTaskList(@Param("clientId")Integer clientId, @Param("appCode")String appCode);

    String getAccountNumberByTaksCode(@Param("taskCode")String taskCode);

    List<TodayExecDataDTO> getRobotTaskExecList(TodayExecQueryDTO dto);

    int updateTask(RobotTask robotTask);

    Integer selectRunningCount(Map<String, Object> params);

    RobotTask selectRobotTaskByParams(Map<String,Object> params);

    int updateTaskData(RobotTask robotTask);

    RobotStatisticsDTO selectStatistics();

    List<CustOrgTaskDTO> getRobotTaskByParams(Map<String,Object> params);

    int selectH5LoginCount(@Param("taskCodes") List<String> taskCodes);

    Integer selectTaskCount(Map<String,Object> params);

    RobotTaskStatusChangeSendDTO getRobotTaskInfo(HashMap<String, Object> paramsMap);

    List<RobotTaskVO> selectAllTask(Map<String,Object> params);

    List<RobotTask> selectUnSyncTask(Map<String,Object> params);
}
