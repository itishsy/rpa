package com.seebon.rpa.repository.mysql;

import com.seebon.rpa.entity.robot.RobotFlowTemplateStep;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

public interface RobotFlowTemplateStepDao extends Mapper<RobotFlowTemplateStep>, MySqlMapper<RobotFlowTemplateStep> {

    /**
     * 根据appCode查询RobotFlowTemplateStep
     *
     * @param appCode 流程编码
     * @return flowStep
     */
    List<RobotFlowTemplateStep> selectByAppCode(@Param("appCode") String appCode);

    /**
     * 根据flowCode查询RobotFlowTemplateStep
     *
     * @param templateFlowCode 流程编码
     * @return flowStep
     */
    RobotFlowTemplateStep selectByTemplateFlowCode(@Param("templateFlowCode") String templateFlowCode, @Param("templateStepCode") String templateStepCode, @Param("mainFlowCode") String mainFlowCode);
}
