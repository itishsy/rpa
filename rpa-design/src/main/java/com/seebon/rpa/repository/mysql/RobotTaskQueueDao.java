package com.seebon.rpa.repository.mysql;

import com.seebon.rpa.entity.robot.RobotTaskQueue;
import com.seebon.rpa.entity.robot.vo.RobotTaskQueueVO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;
import java.util.Map;

public interface RobotTaskQueueDao extends Mapper<RobotTaskQueue>, MySqlMapper<RobotTaskQueue> {

    List<RobotTaskQueueVO> selectByParams(Map<String, Object> params);

    List<RobotTaskQueueVO> findByParams(Map<String, Object> params);

    List<RobotTaskQueueVO> selectQueueByClientId(@Param("clientId") Integer clientId, @Param("statusList") List<Integer> statusList);

    Long getAvgPraTime(Map<String, Object> params);

    List<RobotTaskQueueVO> getSortByClientId(@Param("clientId") Integer clientId);

    List<RobotTaskQueueVO> findQueueFirst(@Param("clientId") Integer clientId);

    void batchUpdateSort(List<RobotTaskQueue> list);

    void batchUpdatePreTime(List<RobotTaskQueue> list);

    void batchUpdatePraTime(List<RobotTaskQueue> list);

    void updateStatus(@Param("id") Integer id, @Param("status") Integer status, @Param("comment") String comment);

    void updateLoginStatus(@Param("executionCode") String executionCode, @Param("taskCode") String taskCode, @Param("loginStatus") Integer loginStatus);

    void updateStatusByMachineCode(@Param("machineCode") String machineCode, @Param("comment") String comment, @Param("status") Integer status);

    void updateById(@Param("id") Integer id, @Param("status") Integer status, @Param("loginStatus") Integer loginStatus, @Param("machineCode") String machineCode);
}
