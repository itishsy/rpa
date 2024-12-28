package com.seebon.rpa.repository.mongodb;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import com.seebon.common.utils.DateUtils;
import com.seebon.rpa.entity.robot.dto.ExecutionQueryDTO;
import com.seebon.rpa.entity.robot.dto.design.RobotExecutionDetailMo;
import com.seebon.rpa.entity.robot.dto.design.RobotExecutionMo;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.Column;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class RobotExecutionRepositoryImpl implements RobotExecutionRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    private static String colName = "robot_execution";

    @Override
    public void save(RobotExecutionMo execution) {
        mongoTemplate.save(execution);
    }

    @Override
    public void saveDetails(List<RobotExecutionDetailMo> executionDetails) {
        mongoTemplate.insertAll(executionDetails);
    }

    @Override
    public List<RobotExecutionMo> listRobotExecution(ExecutionQueryDTO dto) {
        Query query = new Query();

        Integer clientId = dto.getClientId();
        if (clientId != null) {
            query.addCriteria(Criteria.where("clientId").is(clientId));
        }

        String machineCode = dto.getMachineCode();
        if (StringUtils.isNotBlank(machineCode)) {
            query.addCriteria(Criteria.where("machineCode").is(machineCode));
        }

        String appCode = dto.getAppCode();
        if (StringUtils.isNotBlank(appCode)) {
            query.addCriteria(Criteria.where("appCode").is(appCode));
        }

        String executionCode = dto.getExecutionCode();
        if (StringUtils.isNotBlank(executionCode)) {
            query.addCriteria(Criteria.where("executionCode").is(executionCode));
        }

        String startTime = dto.getExecStartTime();
        String endTime = dto.getExecEndTime();
        Criteria criteria1 = Criteria.where("startTime");
        if (StringUtils.isNotBlank(startTime) || StringUtils.isNotBlank(endTime)) {
            if (StringUtils.isNotBlank(startTime)) {
                criteria1 = criteria1.gte(startTime + " 00:00:00");
            }
            if (StringUtils.isNotBlank(endTime)) {
                criteria1 = criteria1.lte(endTime + " 23:59:59");
            }
            query.addCriteria(criteria1);
        }

        String taskName = dto.getTaskName();
        if (StringUtils.isNotBlank(taskName)) {
            Criteria criteria = Criteria.where("taskName").regex(".*?"+taskName+".*");
            query.addCriteria(criteria);
        }
        String flowName = dto.getFlowName();
        if (StringUtils.isNotBlank(flowName)) {
            Criteria criteria = Criteria.where("flowName").regex(".*?"+flowName+".*");
            query.addCriteria(criteria);
        }

        String taskCode = dto.getTaskCode();
        if (StringUtils.isNotBlank(taskCode)) {
            query.addCriteria(Criteria.where("taskCode").is(taskCode));
        }

        List<String> flowCodes = dto.getFlowCodes();
        if (CollectionUtils.isNotEmpty(flowCodes)) {
            Criteria criteria = Criteria.where("flowCode").in(flowCodes);
            query.addCriteria(criteria);
        }

        List<Integer> statuses = dto.getStatuses();
        if (CollectionUtils.isNotEmpty(statuses)) {
            Criteria criteria = Criteria.where("status").in(statuses);
            query.addCriteria(criteria);
        }
        query.with(Sort.by(Sort.Direction.DESC, new String[]{"startTime"}));
        List<RobotExecutionMo> robotExecutionMos = mongoTemplate.find(query, RobotExecutionMo.class);
        if (CollectionUtils.isNotEmpty(robotExecutionMos)) {
            String execTimeStart = dto.getExecTimeStart();
            if (StringUtils.isNotBlank(execTimeStart)) {
                robotExecutionMos = robotExecutionMos.stream().filter(it -> {
                    String startTime1 = it.getStartTime();
                    String substring = startTime1.substring(11, 16);
                    return Integer.valueOf(substring.replace(":", ""))>=Integer.valueOf(execTimeStart.replace(":", ""));
                }).collect(Collectors.toList());
            }
            String execTimeEnd = dto.getExecTimeEnd();
            if (StringUtils.isNotBlank(execTimeEnd)) {
                robotExecutionMos = robotExecutionMos.stream().filter(it -> {
                    String startTime1 = it.getStartTime();
                    String substring = startTime1.substring(11, 16);
                    return Integer.valueOf(substring.replace(":", ""))<=Integer.valueOf(execTimeEnd.replace(":", ""));
                }).collect(Collectors.toList());
            }
        }
        return robotExecutionMos;
    }

    @Override
    public List<RobotExecutionDetailMo> listRobotExecutionDetail(String executionCode, String flowCode) {
        Query query = new Query(Criteria.where("executionCode").is(executionCode).and("flowCode").is(flowCode));
        return mongoTemplate.find(query, RobotExecutionDetailMo.class);
    }

    @Override
    public List<RobotExecutionDetailMo> listRobotExecutionDetail(String executionCode, String flowCode, Integer status) {
        Query query = new Query(Criteria.where("executionCode").is(executionCode).and("flowCode").is(flowCode).and("status").is(status));
        return mongoTemplate.find(query, RobotExecutionDetailMo.class);
    }

    @Override
    public List<RobotExecutionMo> listRobotExecutionError(){
        Date newDate = DateUtil.offset(new Date(), DateField.MINUTE, -10);
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Query query = new Query(Criteria.where("status").is(0).and("endTime").gte(sdf.format(newDate)));
        return mongoTemplate.find(query, RobotExecutionMo.class);
    }

    @Override
    public List<RobotExecutionMo> listRobotExecutionBySelect(List<String> executionCodeList){
        Query query = new Query(Criteria.where("executionCode").in(executionCodeList).and("status").is(0));
        return mongoTemplate.find(query, RobotExecutionMo.class);
    }

    @Override
    public List<RobotExecutionMo> listRobotExecutionByCode(String executionCode){
        Query query = new Query(Criteria.where("executionCode").is(executionCode));
        return mongoTemplate.find(query, RobotExecutionMo.class);
    }

    @Override
    public int getExecCount(String taskCode, List<String> flowCodes, String date) {
        Query query = new Query();
        if (StringUtils.isNotBlank(taskCode)) {
            query.addCriteria(Criteria.where("taskCode").is(taskCode));
        }
        if (CollectionUtils.isNotEmpty(flowCodes)) {
            query.addCriteria(Criteria.where("flowCode").in(flowCodes));
        }
        if (StringUtils.isNotBlank(date)) {
            query.addCriteria(Criteria.where("startTime").gte(date + " 00:00:00").lte(date + " 23:59:59"));
        }
        List<RobotExecutionMo> executionCode = mongoTemplate.findDistinct(query, "executionCode", colName, RobotExecutionMo.class);
        return executionCode.size();
    }

    @Override
    public RobotExecutionMo selectRecentlyOne(Integer clientId, String appCode, String taskCode, List<String> flowCodes) {
        Query query = new Query();
        if (clientId != null) {
            query.addCriteria(Criteria.where("clientId").is(clientId));
        }
        if (StringUtils.isNotBlank(appCode)) {
            query.addCriteria(Criteria.where("appCode").is(appCode));
        }
        if (StringUtils.isNotBlank(taskCode)) {
            query.addCriteria(Criteria.where("taskCode").is(taskCode));
        }
        if (CollectionUtils.isNotEmpty(flowCodes)) {
            query.addCriteria(Criteria.where("flowCode").in(flowCodes));
        }
        query.with(Sort.by(Sort.Direction.DESC, new String[]{"startTime"}));
        query.limit(1);
        return mongoTemplate.findOne(query, RobotExecutionMo.class, colName);
    }

    @Override
    public RobotExecutionMo selectExecutionOne(String executionCode, Integer clientId, String machineCode, String appCode, String taskCode, String flowCode) {
        Query query = new Query();
        query.addCriteria(Criteria.where("executionCode").is(executionCode));
        query.addCriteria(Criteria.where("clientId").is(clientId));
        query.addCriteria(Criteria.where("machineCode").is(machineCode));
        query.addCriteria(Criteria.where("appCode").is(appCode));
        query.addCriteria(Criteria.where("taskCode").is(taskCode));
        query.addCriteria(Criteria.where("flowCode").is(flowCode));
        return mongoTemplate.findOne(query, RobotExecutionMo.class, colName);
    }

    @Override
    public Long selectTaskExecutionCount(Integer status){
        Query query = new Query();
        String date=DateUtils.dateToStr(new Date(), "yyyy-MM-dd");
        query.addCriteria(Criteria.where("startTime").gte(date + " 00:00:00").lte(date + " 23:59:59"));
        if(status!=null){
            query.addCriteria(Criteria.where("status").is(status));
        }
        return mongoTemplate.count(query, colName);
    }

    @Override
    public Integer selectExecutionCountByParams(Map<String, Object> params) {
        Query query = new Query();
        Integer status = (Integer) params.get("status");
        if(status!=null){
            query.addCriteria(Criteria.where("status").is(status));
        }

        String minTime = (String) params.get("minTime");
        if (StringUtils.isNotBlank(minTime)) {
            query.addCriteria(Criteria.where("startTime").gte(minTime));
        }
        return (int)mongoTemplate.count(query, colName);
    }

    @Override
    public List<RobotExecutionDetailMo> listRobotExecutionDetail(List<String> executionCodeList) {
        Query query = new Query(Criteria.where("executionCode").in(executionCodeList).and("error").ne(null));
        return mongoTemplate.find(query, RobotExecutionDetailMo.class);
    }

    @Override
    public RobotExecutionMo selectOneById(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(id));
        return  mongoTemplate.findOne(query, RobotExecutionMo.class);
    }

    @Override
    public RobotExecutionDetailMo selectExecutionDetailOne(Map<String, Object> params) {
        Query query = new Query();
        String executionCode = (String)params.get("executionCode");
        if (StringUtils.isNotBlank(executionCode)) {
            query.addCriteria(Criteria.where("executionCode").is(executionCode));
        }
        String flowCode = (String)params.get("flowCode");
        if (StringUtils.isNotBlank(flowCode)) {
            query.addCriteria(Criteria.where("flowCode").is(flowCode));
        }
        Integer status = (Integer)params.get("status");
        if (status!=null) {
            query.addCriteria(Criteria.where("status").is(status));
        }
        List<RobotExecutionDetailMo> robotExecutionDetailMos = mongoTemplate.find(query, RobotExecutionDetailMo.class);
        if (CollectionUtils.isNotEmpty(robotExecutionDetailMos)) {
            return robotExecutionDetailMos.get(0);
        }
        return null;
    }

    @Override
    public List<RobotExecutionMo> findExecutionOne(Integer clientId, String machineCode, String appCode, String taskCode, List<String> flowCodes) {
        Query query = new Query();
        query.addCriteria(Criteria.where("clientId").is(clientId));
        query.addCriteria(Criteria.where("machineCode").is(machineCode));
        query.addCriteria(Criteria.where("appCode").is(appCode));
        query.addCriteria(Criteria.where("taskCode").is(taskCode));
        query.addCriteria(Criteria.where("flowCode").in(flowCodes));
        query.addCriteria(Criteria.where("status").is(1));
        query.with(Sort.by(Sort.Direction.DESC, new String[]{"startTime"}));
        query.limit(10);
        return mongoTemplate.find(query, RobotExecutionMo.class, colName);
    }

    public List<RobotExecutionMo> findByEndTimeLimit(Date beforeDate,Integer batchSize){
//        Query query = Query.query(Criteria.where("_id").lte(new ObjectId(beforeDate)));
        Query query = new Query();
        query.addCriteria(Criteria.where("syncTime").lte(DateUtil.format(beforeDate,"yyyy-MM-dd HH:mm:ss")));
//        query.fields().include("endTime");
        query.limit(batchSize);
        return mongoTemplate.find(query, RobotExecutionMo.class);
    }

    public void deleteByIds(List<String> ids) {
        Query query = Query.query(Criteria.where("_id").in(ids));
        mongoTemplate.remove(query,colName);
    }

    public List<RobotExecutionDetailMo> findDetailByEndTimeLimit(Date beforeDate,Integer batchSize){
//        Query query = Query.query(Criteria.where("_id").lte(new ObjectId(beforeDate)));
        Query query = new Query();
        query.addCriteria(Criteria.where("syncTime").lte(DateUtil.format(beforeDate,"yyyy-MM-dd HH:mm:ss")));
//        query.fields().include("endTime");
        query.limit(batchSize);
        return mongoTemplate.find(query, RobotExecutionDetailMo.class);
    }

    public void deleteDetailByIds(List<String> ids) {
        Query query = Query.query(Criteria.where("_id").in(ids));
        mongoTemplate.remove(query, RobotExecutionDetailMo.class);
    }
}
