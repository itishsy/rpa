package com.seebon.rpa.repository.mysql;

import com.seebon.rpa.entity.robot.RobotLoginAuth;
import com.seebon.rpa.entity.robot.RobotTaskSessionKeep;
import com.seebon.rpa.entity.robot.vo.RobotLoginAuthVO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;
import java.util.Map;

public interface RobotLoginAuthDao extends Mapper<RobotLoginAuth>, MySqlMapper<RobotLoginAuth> {

    List<RobotLoginAuthVO> selectVOByParams(Map<String,Object> params);

    Integer selectCountByParams(Map<String,Object> params);

    void updateLoginStatus(@Param("clientId") Integer clientId, @Param("taskCode") String taskCode,
                           @Param("declareSystem") String declareSystem, @Param("loginStatus") Integer loginStatus);

    RobotTaskSessionKeep selectShareLoginAuth(@Param("clientId") Integer clientId, @Param("taskCode") String taskCode,
                                              @Param("declareSystem") String declareSystem);

}
