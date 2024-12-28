package com.seebon.rpa.repository.mapper;

import com.seebon.rpa.entity.robot.RobotTaskSessionKeep;
import com.seebon.rpa.entity.robot.vo.RobotTaskSessionKeepVO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

public interface RobotTaskSessionKeepMapper extends Mapper<RobotTaskSessionKeep>, MySqlMapper<RobotTaskSessionKeep> {

    List<RobotTaskSessionKeepVO> selectLoginStatus();

    RobotTaskSessionKeep selectTaskCode(@Param("appCode") String appCode, @Param("taskCode") String taskCode, @Param("declareSystem") String declareSystem);

    RobotTaskSessionKeep selectSharePortKeep(@Param("sharePort") Integer sharePort);

    void cleanByTaskCode(@Param("appCode") String appCode, @Param("taskCode") String taskCode, @Param("declareSystem") String declareSystem);

    void updateStatus(@Param("id") Integer id, @Param("status") Integer status, @Param("comment") String comment);

    void updateStatusSharePort(@Param("sharePort") Integer sharePort, @Param("status") Integer status, @Param("comment") String comment);

    void startKeep(@Param("id") Integer id, @Param("status") Integer status);

    void startKeepSharePort(@Param("id") Integer id, @Param("status") Integer status, @Param("sharePort") Integer sharePort);

    void keepFail(@Param("id") Integer id, @Param("status") Integer status, @Param("fileId") Integer fileId);

    void keepFailSharePort(@Param("sharePort") Integer sharePort, @Param("status") Integer status, @Param("fileId") Integer fileId);

    void keepSuccess(@Param("id") Integer id, @Param("status") Integer status);

    void keepSuccessSharePort(@Param("id") Integer id, @Param("status") Integer status, @Param("sharePort") Integer sharePort);

    void retryLogin(@Param("id") Integer id);
}
