package com.seebon.rpa.repository.mysql;

import com.seebon.rpa.entity.robot.RobotFlow;
import com.seebon.rpa.entity.robot.RobotFlowStep;
import com.seebon.rpa.entity.robot.dto.design.RobotClientExecutionDetailVO;
import com.seebon.rpa.entity.robot.dto.design.RobotExecutionFileInfo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;
import java.util.Map;

public interface RobotFlowStepDao extends Mapper<RobotFlowStep>, MySqlMapper<RobotFlowStep> {

    void deleteByFlowCode(@Param("flowCode") String flowCode);

    void deleteByFlowCodeAndGroupCode(@Param("flowCode") String flowCode, @Param("groupCode") String groupCode);

    void deleteByFlowCodeNotGroupCode(@Param("flowCode") String flowCode);

    void increaseStepNumber(@Param("flowCode") String flowCode, @Param("stepNumber") Integer stepNumber);

    List<RobotClientExecutionDetailVO> selectByFlowCode(@Param("flowCode") String flowCode, @Param("stepCode") String stepCode);

    List<RobotExecutionFileInfo> selectByExecutionCode(Map<String, Object> params);

    List<RobotFlowStep> flowStepList(@Param("appCode") String appCode);


    List<RobotFlow> findSubFlowStep(@Param("appCode") String appCode, @Param("actionArgs") String actionArgs);

    RobotFlowStep selectByStepCode(@Param("stepCode") String stepCode);

    List<RobotFlowStep> selectByGroupCode(@Param("flowCode") String flowCode, @Param("groupCode") String groupCode);

    /**
     * 删除所有步骤
     *
     * @param appCode
     * @return
     */
    int deleteAllStep(@Param("appCode") String appCode);

    /**
     * 查询是否有重复的步骤名称
     */
    List<RobotFlowStep> selectRepeatStep(@Param("flowCode") String code, @Param("name") String name);

    /**
     * 根据flowCode查询flowStep
     */
    RobotFlowStep getByTargetArgs(@Param("flowCode") String flowCode, @Param("actionCode") String actionCode,
                                  @Param("targetArgs") String targetArgs);

    /**
     * 根据flowCode查询flowStep
     */
    RobotFlowStep getByActionArgs(@Param("flowCode") String flowCode, @Param("actionCode") String actionCode,
                                  @Param("actionArgs") String actionArgs);
}
