package com.seebon.rpa.repository.mapper;

import com.seebon.rpa.entity.robot.RobotTask;
import com.seebon.rpa.entity.robot.RobotTaskArgs;
import com.seebon.rpa.entity.robot.dto.RobotTaskDTO;
import com.seebon.rpa.entity.robot.vo.RobotTaskVO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

public interface RobotTaskMapper extends Mapper<RobotTask>, MySqlMapper<RobotTask> {

    RobotTaskDTO findByCode(@Param("taskCode") String taskCode);

    List<RobotTaskArgs> findArgs(@Param("taskCode") String taskCode);

    void cleanByTaskCode(@Param("taskCode") String taskCode);

    void cleanByAppCode(@Param("appCode") String appCode);

    List<RobotTaskVO> selectAllTask();
}
