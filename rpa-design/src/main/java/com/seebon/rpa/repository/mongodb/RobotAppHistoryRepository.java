package com.seebon.rpa.repository.mongodb;

import com.mongodb.client.result.UpdateResult;
import com.seebon.rpa.entity.robot.dto.history.RobotAppHistory;

import java.util.List;

public interface RobotAppHistoryRepository {

    void add(RobotAppHistory appHistory);

    List<RobotAppHistory> findHistory(String appCode);

    UpdateResult update(String appCode);

    UpdateResult updateByVersion(String appCode, String version);

    RobotAppHistory selectUseVersion(String appCode, String version);

    //根据版本号查询最新版本
    RobotAppHistory latestVersion(String appCode);

}