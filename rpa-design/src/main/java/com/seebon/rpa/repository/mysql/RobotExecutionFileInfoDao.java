package com.seebon.rpa.repository.mysql;

import com.seebon.rpa.entity.robot.RobotExecutionFile;
import com.seebon.rpa.entity.robot.dto.design.RobotExecutionFileInfo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.HashMap;
import java.util.List;

/**
 * TODO
 *
 * @author zjf
 * @describe
 * @date 2023-06-27 17:25
 */
public interface RobotExecutionFileInfoDao  extends Mapper<RobotExecutionFileInfo>{

    List<RobotExecutionFileInfo> selectList(HashMap<String, Object> params);

    void deleteById(@Param("id") Integer id);
}
