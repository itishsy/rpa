package com.seebon.rpa.repository.mapper;

import com.seebon.rpa.entity.robot.RobotFlowTemplateStep;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

public interface RobotFlowTemplateStepMapper extends Mapper<RobotFlowTemplateStep>, MySqlMapper<RobotFlowTemplateStep> {

    /**
     * 根据flowCode查询RobotFlowTemplateStep
     *
     * @param templateFlowCode 流程编码
     * @return flowStep
     */
    RobotFlowTemplateStep selectByTemplateFlowCode(@Param("templateFlowCode") String templateFlowCode, @Param("templateStepCode") String templateStepCode,
                                                   @Param("mainFlowCode") String mainFlowCode);
}
