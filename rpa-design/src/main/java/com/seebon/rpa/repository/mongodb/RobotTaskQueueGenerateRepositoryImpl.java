package com.seebon.rpa.repository.mongodb;

import com.mongodb.BasicDBObject;
import com.seebon.rpa.entity.robot.dto.design.RobotTaskQueueGenerate;
import com.seebon.rpa.entity.robot.vo.RobotFlowVO;
import com.seebon.rpa.entity.robot.vo.RobotTaskGenerateQueueVO;
import com.seebon.rpa.entity.robot.vo.RobotTaskVO;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class RobotTaskQueueGenerateRepositoryImpl implements RobotTaskQueueGenerateRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 执行自定义查询字符串，并添加limit和orderby功能
     *
     * @param queryStr       查询字符串，例如 "{'aa':'bb'}"
     * @param limit          返回的最大文档数量
     * @param orderBy        排序字段，例如 "aa"
     * @param orderDirection 排序方向，例如 "asc" 或 "desc"
     * @return 查询结果列表
     */
    @Override
    public List<RobotTaskQueueGenerate> findByCustomQueryWithLimitAndOrder(String queryStr, Integer limit, String orderBy, String orderDirection) {
        // 将查询字符串转换成DBObject
        BasicDBObject queryObject = BasicDBObject.parse(queryStr);

        // 创建Query对象
        Query query = new Query(Criteria.where(queryObject.keySet().iterator().next()).is(queryObject.toMap().values().iterator().next()));

        // 如果limit参数不为空，则设置查询限制
        if (limit != null) {
            query.limit(limit);
        } else {
            query.limit(5);
        }

        // 如果orderby参数不为空，则设置排序
        if (StringUtils.isNotEmpty(orderBy) && StringUtils.isNotEmpty(orderDirection)) {
            Sort.Direction direction = Sort.Direction.fromString(orderDirection.toUpperCase());
            query.with(Sort.by(direction, orderBy));
        } else {
            query.with(Sort.by("_id","desc"));
        }

        // 执行查询并返回结果
        return mongoTemplate.find(query, RobotTaskQueueGenerate.class);
    }

    @Override
    public void save(RobotTaskVO task, List<RobotFlowVO> flows, String s,Object otherData) {
        save(task, flows,null, s, otherData);
    }

    @Override
    public void save(RobotTaskVO task, List<RobotFlowVO> flows,Integer serviceItem, String s,Object otherData) {
        RobotTaskQueueGenerate queueGenerate = new RobotTaskQueueGenerate();
        Date nowDate = new Date();
        if (task != null) {
            RobotTaskGenerateQueueVO robotTaskGenerateQueueVO = new RobotTaskGenerateQueueVO();
            BeanUtils.copyProperties(task,robotTaskGenerateQueueVO);
            queueGenerate.setTaskVO(robotTaskGenerateQueueVO);
            queueGenerate.setTaskCode(robotTaskGenerateQueueVO.getTaskCode());
        }
        queueGenerate.setFlowVOList(flows);
        queueGenerate.setMessage(s);
        queueGenerate.setOtherData(otherData);
        queueGenerate.setCreateTime(nowDate);
        queueGenerate.setTimeStamp(nowDate.getTime());
        queueGenerate.setQueueItem(serviceItem);
        mongoTemplate.save(queueGenerate);
    }

    @Override
    public void deleteByLtId(ObjectId objectId) {
        mongoTemplate.remove(Query.query(new Criteria("_id").lt(objectId)),RobotTaskQueueGenerate.class);
    }

    @Override
    public List<RobotTaskQueueGenerate> findOnlyIdByEndTimeLimit(Date beforeDate, Integer batchSize) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").lt(new ObjectId(beforeDate)));
        query.fields().include("_id");
        query.limit(batchSize);
        return mongoTemplate.find(query, RobotTaskQueueGenerate.class);
    }

    public void deleteDetailByIds(List<String> ids) {
        Query query = Query.query(Criteria.where("_id").in(ids));
        mongoTemplate.remove(query, RobotTaskQueueGenerate.class);
    }


}
