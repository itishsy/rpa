package com.seebon.rpa.repository.mysql;

import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * TODO
 *
 * @author zjf
 * @describe
 * @date 2023-07-24 16:29
 */
@Component
public interface MonitorStationDao{
     Map<String,Integer> getCityCount();

     Map<String,Integer> getBoxCount();

     Map<String,Integer> getAccountCount();

     Map<String,Integer> getAppCount();

     Long getBoxExecutionTimeCount();

     Long getTodayTaskCount();

     Long getWarnTypeCount(String today);

     Long getWarnNumber(String today);


}
