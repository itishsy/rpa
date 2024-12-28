package com.seebon.rpa.repository.mysql;

import com.seebon.rpa.entity.robot.RobotTaskSchedule;
import com.seebon.rpa.entity.robot.dto.RobotTaskScheduleDTO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;
import java.util.Map;

public interface RobotTaskScheduleDao extends Mapper<RobotTaskSchedule>, MySqlMapper<RobotTaskSchedule> {

    Integer updateByExecOrder(@Param("execOrder") Integer execOrder, @Param("taskCode") String taskCode, @Param("flowCodes") List<String> flowCodes);

    List<RobotTaskScheduleDTO> selectSchedules(@Param("appCode")String appCode, @Param("taskCode")String taskCode, @Param("notNull")Integer notNull);

    int updateStatus(Map<String,Object> params);

    List<RobotTaskScheduleDTO> selectCommonSchedules(@Param("appCode")String appCode, @Param("taskCode")String taskCode, @Param("prvtFlowCodes")List<String> prvtFlowCodes, @Param("notNull")Integer notNull);

}
