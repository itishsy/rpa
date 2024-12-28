package com.seebon.rpa.repository.mongodb;

import com.seebon.rpa.entity.robot.dto.design.RobotTaskQueueGenerate;
import com.seebon.rpa.entity.robot.vo.RobotFlowVO;
import com.seebon.rpa.entity.robot.vo.RobotTaskVO;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;

public interface RobotTaskQueueGenerateRepository {

    List<RobotTaskQueueGenerate> findByCustomQueryWithLimitAndOrder(String queryStr, Integer limit, String orderBy, String orderDirection);

    void save(RobotTaskVO task, List<RobotFlowVO> flows, String s,Object otherData);

    void save(RobotTaskVO task, List<RobotFlowVO> flows,Integer serviceItem, String s,Object otherData);

    void deleteByLtId(ObjectId objectId);

    List<RobotTaskQueueGenerate> findOnlyIdByEndTimeLimit(Date beforeDate, Integer batchSize);

    void deleteDetailByIds(List<String> ids);
}
