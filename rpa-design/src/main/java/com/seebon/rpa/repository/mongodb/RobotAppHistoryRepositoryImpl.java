package com.seebon.rpa.repository.mongodb;

import com.mongodb.client.result.UpdateResult;
import com.seebon.rpa.entity.robot.RobotAppConfigCondition;
import com.seebon.rpa.entity.robot.RobotFlowTemplate;
import com.seebon.rpa.entity.robot.dto.history.RobotAppHistory;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@Repository
public class RobotAppHistoryRepositoryImpl implements RobotAppHistoryRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void add(RobotAppHistory appHistory) {
        if (appHistory != null) {
            // 流程数过多，压缩存储
            if (null != appHistory.getFlowStepList() && appHistory.getFlowStepList().size() > 3000) {
                RobotAppHistory compressAppHistory = new RobotAppHistory();
                compressAppHistory.setAppCode(appHistory.getAppCode());
                compressAppHistory.setStatus(appHistory.getStatus());
                compressAppHistory.setVersion(appHistory.getVersion());
                compressAppHistory.setReleaseTime(appHistory.getReleaseTime());
                compressAppHistory.setCompressedData(compressObject(appHistory));
                mongoTemplate.insert(compressAppHistory);
            } else {
                mongoTemplate.insert(appHistory);
            }
        }
    }

    @Override
    public List<RobotAppHistory> findHistory(String appCode) {
        //根据id字段来排序,如果想根据多个字段进行排序,可以在str字符串数组中添加字段
        Query query = new Query();
        Criteria criteria = Criteria.where("appCode").is(appCode);
        query.addCriteria(criteria);
        String[] str = {"releaseTime"};
        query.with(Sort.by(Sort.Direction.DESC, str));//DESC降序,ASC是升序
        List<RobotAppHistory> list = mongoTemplate.find(query, RobotAppHistory.class);
        return returnAppHistoryList(list);
    }

    //修改历史版本状态
    @Override
    public UpdateResult update(String appCode) {
        Query query = new Query(Criteria.where("appCode").is(appCode).and("status").is(1));
        Update update = Update.update("status", 2);
        return mongoTemplate.updateMulti(query, update, RobotAppHistory.class);
    }

    //当状态为未发布历史表状态更新为失效
    @Override
    public UpdateResult updateByVersion(String appCode, String version) {
        Query query = new Query(Criteria.where("appCode").is(appCode).and("version").is(version));
        Update update = Update.update("status", 3);
        return mongoTemplate.updateMulti(query, update, RobotAppHistory.class);
    }

    //使用此版本
    @Override
    public RobotAppHistory selectUseVersion(String appCode, String version) {
        Query query = new Query();
        Criteria criteria = Criteria.where("version").is(version).and("appCode").is(appCode);
        query.addCriteria(criteria);
        return returnAppHistory(mongoTemplate.findOne(query, RobotAppHistory.class)) ;
    }

    //根据版本号查询最新版本
    @Override
    public RobotAppHistory latestVersion(String appCode) {
        Query query = new Query();
        Criteria criteria = Criteria.where("appCode").is(appCode).and("status").is(1);
        query.addCriteria(criteria);
        return returnAppHistory(mongoTemplate.findOne(query, RobotAppHistory.class));
    }

    private List<RobotAppHistory>  returnAppHistoryList(List<RobotAppHistory> appHistorys){
        List<RobotAppHistory> appHistories = new ArrayList<>();
        for(RobotAppHistory appHistory : appHistorys){
            appHistories.add(returnAppHistory(appHistory));
        }
        return appHistories;
    }

    private RobotAppHistory returnAppHistory(RobotAppHistory appHistory){
        if(null == appHistory || null == appHistory.getCompressedData() || appHistory.getCompressedData().length == 0){
            return appHistory;
        } else {
            return decompressObject(appHistory.getCompressedData());
        }
    }

    public static byte[] compressObject(RobotAppHistory appHistory) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(gzipOutputStream);
            objectOutputStream.writeObject(appHistory);
            objectOutputStream.close();
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        RobotAppHistory appHistory = new RobotAppHistory();
        RobotAppConfigCondition configCondition = new RobotAppConfigCondition();
        configCondition.setConditionLeft("324234");
        List<RobotAppConfigCondition> configConditions = new ArrayList<>();
        configConditions.add(configCondition);
        appHistory.setAppCode("23123123");
        appHistory.setAppName("dfsdfadfa");
        RobotFlowTemplate flowTemplate = new RobotFlowTemplate();
        flowTemplate.setFlowCode("23423432");
        List<RobotFlowTemplate> flowTemplates = new ArrayList<>();
        flowTemplates.add(flowTemplate);
        appHistory.setConfigConditions(configConditions);
        appHistory.setFlowTemplate(flowTemplates);
        byte[] bytes = compressObject(appHistory);
        System.out.println(bytes.length);
        RobotAppHistory appHistory1 = decompressObject(bytes);
        System.out.println(appHistory1.getFlowTemplate().get(0).getFlowCode());
    }

    public static RobotAppHistory decompressObject(byte[] compressedData) {
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(compressedData);
            GZIPInputStream gzipInputStream = new GZIPInputStream(byteArrayInputStream);
            ObjectInputStream objectInputStream = new ObjectInputStream(gzipInputStream);
            Object object = objectInputStream.readObject();
            objectInputStream.close();
            return (RobotAppHistory)object;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
