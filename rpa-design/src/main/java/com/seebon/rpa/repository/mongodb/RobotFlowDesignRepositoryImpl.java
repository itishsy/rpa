package com.seebon.rpa.repository.mongodb;

import com.seebon.common.utils.DateUtil;
import com.seebon.rpa.auth.SecurityContext;
import com.seebon.rpa.auth.Session;
import com.seebon.rpa.entity.robot.RobotFlowStep;
import com.seebon.rpa.entity.robot.dto.design.RobotFlowDesign;
import com.seebon.rpa.entity.robot.dto.design.RobotFlowDesignVO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class RobotFlowDesignRepositoryImpl implements RobotFlowDesignRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public String save(RobotFlowDesignVO flowDesign) {
        RobotFlowDesign design = new RobotFlowDesign();
        BeanUtils.copyProperties(flowDesign, design);
        Session session = SecurityContext.currentUser();
        design.setUpdateId((int) session.getId());
        design.setUpdateTime(DateUtil.getNowTime());
        if (StringUtils.isBlank(flowDesign.getId())) {
            design.setCreateId((int) session.getId());
            design.setCreateTime(DateUtil.getNowTime());
        }
        if (CollectionUtils.isNotEmpty(flowDesign.getSteps())) {
            List<RobotFlowStep> steps = flowDesign.getSteps().stream().map(stepVO -> {
                RobotFlowStep step = new RobotFlowStep();
                BeanUtils.copyProperties(stepVO, step);
                return step;
            }).collect(Collectors.toList());
            design.setSteps(steps);
        }
        mongoTemplate.save(design);
        return design.getId();
    }

    @Override
    public void batchSave(List<RobotFlowDesign> flowDesigns) {
        for (RobotFlowDesign design : flowDesigns) {
            mongoTemplate.save(design);
        }
    }

    @Override
    public RobotFlowDesign findByFlowCode(String flowCode) {
        Query query = new Query(Criteria.where("flowCode").is(flowCode));
        return mongoTemplate.findOne(query, RobotFlowDesign.class);
    }

    @Override
    public List<RobotFlowDesign> listByFlowCodes(List<String> flowCodes) {
        Query query = new Query(Criteria.where("flowCode").in(flowCodes));
        return mongoTemplate.find(query, RobotFlowDesign.class);
    }

    @Override
    public List<RobotFlowDesign> listByTemplateType(Integer templateType) {
        Query query = new Query(Criteria.where("templateType").is(templateType));
        return mongoTemplate.find(query, RobotFlowDesign.class);
    }
}
