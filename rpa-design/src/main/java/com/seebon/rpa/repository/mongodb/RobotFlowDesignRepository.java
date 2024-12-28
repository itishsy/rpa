package com.seebon.rpa.repository.mongodb;


import com.seebon.rpa.entity.robot.dto.design.RobotFlowDesign;
import com.seebon.rpa.entity.robot.dto.design.RobotFlowDesignVO;

import java.util.List;

public interface RobotFlowDesignRepository {

    String save(RobotFlowDesignVO flowDesign);

    void batchSave(List<RobotFlowDesign> flowDesigns);

    RobotFlowDesign findByFlowCode(String flowCode);

    List<RobotFlowDesign> listByFlowCodes(List<String> flowCodes);

    List<RobotFlowDesign> listByTemplateType(Integer templateType);
}