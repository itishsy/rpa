package com.seebon.rpa.repository.mysql;

import com.seebon.rpa.entity.robot.RobotTaskSessionKeep;
import com.seebon.rpa.entity.robot.vo.RobotTaskSessionKeepVO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;
import java.util.Map;

public interface RobotTaskSessionKeepDao extends Mapper<RobotTaskSessionKeep>, MySqlMapper<RobotTaskSessionKeep> {

    List<RobotTaskSessionKeepVO> selectByParams(Map<String, Object> params);

    List<RobotTaskSessionKeepVO> selectTask(RobotTaskSessionKeep keep);

    List<RobotTaskSessionKeepVO> selectByClientId(@Param("clientId") Integer clientId, @Param("machineCode") String machineCode);

    Integer selectMinPort(@Param("clientId") Integer clientId);

    void updateById(RobotTaskSessionKeep update);

    void updateBySharePort(RobotTaskSessionKeep update);

    void againSync(@Param("id") Integer id);
}
