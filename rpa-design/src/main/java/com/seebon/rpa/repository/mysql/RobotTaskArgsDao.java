package com.seebon.rpa.repository.mysql;

import com.seebon.rpa.entity.robot.RobotTaskArgs;
import com.seebon.rpa.entity.robot.dto.RobotTaskConfigDTO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

public interface RobotTaskArgsDao extends Mapper<RobotTaskArgs>, MySqlMapper<RobotTaskArgs> {
    RobotTaskArgs selectByAppAndArgsKey(@Param("appCode") String appCode, @Param("argsKey") String argsKey,
                                        @Param("argsValue") String argsValue,
                                        @Param("clientId") Integer clientId, @Param("id") Integer id);

    RobotTaskArgs selectAccountNumber(@Param("taskCode") String taskCode,
                                      @Param("numbers") List<String> numbers);

    List<RobotTaskConfigDTO> getRobotTaskConfigInfo(@Param("taskCode") String taskCode);

}
