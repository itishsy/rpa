package com.seebon.rpa.repository.mysql;

import com.seebon.rpa.entity.robot.RobotAppCa;
import com.seebon.rpa.entity.robot.RobotTaskSessionKeep;
import com.seebon.rpa.entity.robot.vo.RobotAppCaVO;
import com.seebon.rpa.entity.robot.vo.RobotTaskSessionKeepVO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;
import java.util.Map;

public interface RobotAppCaDao extends Mapper<RobotAppCa>, MySqlMapper<RobotAppCa> {

    List<RobotAppCaVO> selectByParams(Map<String, Object> params);

    List<RobotAppCaVO> selectByMachineCode(@Param("machineCode") String machineCode);

    void againSync(@Param("id") Integer id);
}