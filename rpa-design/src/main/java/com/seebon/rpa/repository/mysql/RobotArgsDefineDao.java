package com.seebon.rpa.repository.mysql;

import com.seebon.rpa.entity.robot.vo.RobotAppArgsVO;
import com.seebon.rpa.entity.robot.vo.RobotArgsDefineVO;
import com.seebon.rpa.entity.robot.RobotArgsDefine;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

public interface RobotArgsDefineDao extends Mapper<RobotArgsDefine>, MySqlMapper<RobotArgsDefine> {

    /**
     * 根据appcode查找操作指令参数
     *
     * @param appCode
     * @return
     */
    List<RobotArgsDefineVO> findAppArgsList(@Param("appCode") String appCode);

    int updateExecOrder(@Param("groupName") String groupName, @Param("displayOrder") Integer displayOrder);

    List<RobotArgsDefine> selectArgs(@Param("fieldKey") String fieldKey, @Param("fieldName") String fieldName, @Param("appCode") String appCode);

    List<RobotArgsDefine> selectArgsById(@Param("fieldKey") String fieldKey, @Param("fieldName") String fieldName, @Param("appCode") String appCode, @Param("id") Integer id);

    List<RobotAppArgsVO> selectRobotArgsDefine(@Param("scope") String scope, @Param("dataCode") String dataCode);

    List<RobotAppArgsVO>    selectTarget(@Param("dataCode") String dataCode);

    List<RobotAppArgsVO> selectTargetArg(@Param("targetType") String targetType, @Param("dataCode") String dataCode);
}
