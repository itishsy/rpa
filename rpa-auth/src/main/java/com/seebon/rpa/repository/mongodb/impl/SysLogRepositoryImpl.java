package com.seebon.rpa.repository.mongodb.impl;

import com.seebon.rpa.entity.SysLog;
import com.seebon.rpa.repository.mongodb.SysLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author ZhenShijun
 * @dateTime 2021-04-08 10:30:07
 */
@Repository
@Slf4j
public class SysLogRepositoryImpl implements SysLogRepository {

    private static final String MONGODB_NAME = "sys_operation_log";

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void insetList(List<SysLog> logs) {
        if (CollectionUtils.isNotEmpty(logs)) {
            mongoTemplate.insert(logs, MONGODB_NAME);
        }
    }

    @Override
    public void inset(SysLog sysLog) {
        if (sysLog != null) {
            mongoTemplate.insert(sysLog);
        }
    }
}
