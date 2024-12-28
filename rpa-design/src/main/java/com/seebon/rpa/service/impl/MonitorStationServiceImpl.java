package com.seebon.rpa.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.seebon.rpa.entity.robot.RobotApp;
import com.seebon.rpa.entity.robot.dto.design.MonitorCountVo;
import com.seebon.rpa.entity.robot.dto.design.RobotAppDesignVO;
import com.seebon.rpa.entity.robot.dto.design.TaskCountVo;
import com.seebon.rpa.entity.saas.po.declare.PolicyAddr;
import com.seebon.rpa.remote.RpaSaasAgentService;
import com.seebon.rpa.remote.RpaSaasService;
import com.seebon.rpa.repository.mongodb.RobotExecutionRepository;
import com.seebon.rpa.repository.mysql.MonitorStationDao;
import com.seebon.rpa.repository.mysql.RobotAppDao;
import com.seebon.rpa.service.MonitorStationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * TODO
 *
 * @author zjf
 * @describe 监控台实现类
 * @date 2023-07-24 16:28
 */
@Service
@Slf4j
public class MonitorStationServiceImpl implements MonitorStationService{
    private final String key="MONITOR_COUNT";

    @Autowired
    private MonitorStationDao dao;
    @Autowired
    private RpaSaasService rpaSaasService;
    @Autowired
    private RpaSaasAgentService rpaSaasAgentService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RobotAppDao robotAppDao;
    @Autowired
    private RobotExecutionRepository robotExecutionRepository;

    @Override
    public MonitorCountVo getMonitorCount(){
        //开通城市指标统计
        Map<String,Integer> cityMap=getCityCount();
        //盒子指标的统计
        Map<String,Integer> boxMap=dao.getBoxCount();
        //申报账号的统计指标
        Map<String,Integer> accountMap=rpaSaasService.getDeclareCount();
        //获取服务员工数
        Integer onJobEmp=rpaSaasService.getRegisteredPerson();
        //获取应用统计指标
        Map<String,Integer> appMap=dao.getAppCount();
        //获取服务数据统计指标
        Map<String,Integer> serviceMap=rpaSaasAgentService.getMonitorCountByDeclareStatus();
        //获取盒子总运行时间
        Long countTime=dao.getBoxExecutionTimeCount();
        BigDecimal time = new BigDecimal((double)countTime/1000/3600); //毫秒换算小时
        double hour = time.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
        cityMap.putAll(boxMap);
        cityMap.putAll(accountMap);
        cityMap.putAll(appMap);
        cityMap.putAll(serviceMap);
        MonitorCountVo monitorCountVo= JSON.parseObject(JSON.toJSONString(cityMap), MonitorCountVo.class);
        monitorCountVo.setServicePerson(onJobEmp);
        monitorCountVo.setTotalRunTime(hour);
        monitorCountVo.setDeclareRunCount(monitorCountVo.getBoxExecutionCount());
        monitorCountVo.setDeclareNoRunCount(monitorCountVo.getDeclareAccountCount()-monitorCountVo.getBoxExecutionCount());
        //设置城市，盒子，申报账号，应用，服务数据的指标比较
        if(ObjectUtil.isNotNull(redisTemplate.opsForValue().get(key))){
            String value=redisTemplate.opsForValue().get(key).toString();
            MonitorCountVo countVo=JSON.parseObject(value,MonitorCountVo.class);
            log.info("获取前一天缓存的数据{}",countVo);
            monitorCountVo.setCityIndex(monitorCountVo.getRightPassCityCount()-countVo.getRightPassCityCount());
            monitorCountVo.setBoxIndex(monitorCountVo.getBoxCount()-countVo.getBoxCount());
            monitorCountVo.setAccountIndex(monitorCountVo.getDeclareAccountCount()-countVo.getDeclareAccountCount());
            monitorCountVo.setAppIndex(monitorCountVo.getAppCount()-countVo.getAppCount());
            monitorCountVo.setServiceDataIndex(monitorCountVo.getServiceCount()-countVo.getServiceCount());
        }else{
             log.info("redis暂未缓存前一天数据");
        }
        return monitorCountVo;
    }

    @Override
    public Map getCity(){
        Map map=new HashMap();
        List<PolicyAddr> cityList=rpaSaasService.getCityList();
        //上线城市
        Example example=new Example(RobotApp.class);
        example.createCriteria().andEqualTo("runStatus",1);
        List<RobotApp> robotApps=robotAppDao.selectByExample(example);
        Set set=dateChange(robotApps);
        List<PolicyAddr> collect=cityList.stream().filter(e->set.contains(e.getAddrId())).collect(Collectors.toList());
        map.put("addrOnlineList",collect);
        //下线城市
        Example offlineExample=new Example(RobotApp.class);
        offlineExample.createCriteria().andEqualTo("runStatus",0);
        List<RobotApp> offlineRobotApps=robotAppDao.selectByExample(offlineExample);
        Set offlineSet=dateChange(offlineRobotApps);
        List<PolicyAddr> offlineCollect=cityList.stream().filter(e->offlineSet.contains(e.getAddrId())).collect(Collectors.toList());
        map.put("addrOfflineList",offlineCollect);
        //待上线数
        List<RobotAppDesignVO> robotAppList=robotAppDao.getWaitOnline();
        robotAppList.forEach(e->{
            JSONObject appArgs = JSONObject.parseObject(e.getAppArgs());
            String addrId=appArgs.getString("addrId");
            e.setAddrId(addrId);
        });
        List<RobotAppDesignVO> uniqueList=robotAppList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(()->new TreeSet<>(Comparator.comparing(RobotAppDesignVO::getAddrId))),ArrayList::new));
        List<RobotAppDesignVO> robotAppDesignVOS=robotAppDao.getOnlineOrOffline();
        robotAppDesignVOS.forEach(e->{
            JSONObject appArgs = JSONObject.parseObject(e.getAppArgs());
            String addrId=appArgs.getString("addrId");
            e.setAddrId(addrId);
        });
        List<String> addrList  = robotAppDesignVOS.stream().map(o -> o.getAddrId()).collect(Collectors.toList());
        List<RobotAppDesignVO> data = uniqueList.stream().filter(a ->!addrList.contains(a.getAddrId())).collect(Collectors.toList());
        List<String> addrs  = data.stream().map(o -> o.getAddrId()).collect(Collectors.toList());
        List<PolicyAddr> launchedCollect=cityList.stream().filter(e->addrs.contains(e.getAddrId().toString())).collect(Collectors.toList());
        map.put("addrLaunchedList",launchedCollect);
        return map;

    }

    @Override
    public TaskCountVo getTaskCount(){
        LocalDateTime now=LocalDateTime.now();
        String date=now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        TaskCountVo taskCountVo=new TaskCountVo();
        Long todayTaskCount=dao.getTodayTaskCount();
        taskCountVo.setTodayTaskCount(todayTaskCount);
        Long todayTaskExecuteCount=robotExecutionRepository.selectTaskExecutionCount(2);
        Long todayTaskExecuteNumber=robotExecutionRepository.selectTaskExecutionCount(null);
        taskCountVo.setTodayTaskExecuteCount(todayTaskExecuteCount);
        taskCountVo.setTodayTaskExecuteNumber(todayTaskExecuteNumber);
        Long todayExecuteError=dao.getWarnTypeCount(date);
        Long warnCount=dao.getWarnNumber(date);
        taskCountVo.setTodayExecuteError(todayExecuteError);
        taskCountVo.setWarnCount(warnCount);
        return taskCountVo;
    }

    public Map getCityCount(){
        Map map=new HashMap();
        //查询左菜单开通城市
        Example example=new Example(RobotApp.class);
        example.createCriteria().andEqualTo("robotCode","declare").andEqualTo("onlineStatus",2).orEqualTo("onlineStatus",3);
        List<RobotApp> robotApps=robotAppDao.selectByExample(example);
        Set set=dateChange(robotApps);
        map.put("leftPassCityCount", set.size());
        //查询左菜单下线城市
        List<Integer> list= Arrays.asList(2,3);
        Example offlineExample=new Example(RobotApp.class);
        offlineExample.createCriteria().andEqualTo("robotCode","declare").andEqualTo("runStatus",0).andIn("onlineStatus", list);
        List<RobotApp> offlineRobotApps=robotAppDao.selectByExample(offlineExample);
        Set offlineSet=dateChange(offlineRobotApps);
        map.put("leftOfflineCityCount", offlineSet.size());
        //查询左菜单待上线城市
        Example liveExample=new Example(RobotApp.class);
        liveExample.createCriteria().andEqualTo("robotCode","declare").andEqualTo("onlineStatus",0).orEqualTo("onlineStatus",1);
        List<RobotApp> liveRobotApps=robotAppDao.selectByExample(liveExample);
        Set liveSet=dateChange(liveRobotApps);
        map.put("leftLiveCityCount", liveSet.size());
        //查询右菜单总城市
        Example countExample=new Example(RobotApp.class);
        countExample.createCriteria().andEqualTo("robotCode","declare");
        List<RobotApp> countRobotApps=robotAppDao.selectByExample(countExample);
        Set countSet=dateChange(countRobotApps);
        map.put("rightPassCityCount", countSet.size());
        //右菜单下线城市
        Example rightOfflineExample=new Example(RobotApp.class);
        rightOfflineExample.createCriteria().andEqualTo("robotCode","declare").andEqualTo("runStatus",0);
        List<RobotApp> rightOfflineRobotApps=robotAppDao.selectByExample(rightOfflineExample);
        Set rightOffineSet=dateChange(rightOfflineRobotApps);
        map.put("rightOfflineCityCount", rightOffineSet.size());
        //右菜单上线城市
        Example rightLiveExample=new Example(RobotApp.class);
        rightLiveExample.createCriteria().andEqualTo("robotCode","declare").andEqualTo("runStatus",1);
        List<RobotApp> rightLiveRobotApps=robotAppDao.selectByExample(rightLiveExample);
        Set rightLiveSet=dateChange(rightLiveRobotApps);
        map.put("rightLiveCityCount", rightLiveSet.size());
        return map;
    }

    public Set dateChange(List<RobotApp> list){
        Set set=new HashSet();
        list.forEach(e->{
            JSONObject appArgs = JSONObject.parseObject(e.getAppArgs());
            //只统计社保的应用
            if(appArgs!=null){
                Integer addrId=appArgs.getInteger("addrId");
                set.add(addrId);
            }
        });
        return set;
    }

    public static void main(String[] args){
        Integer a=100;
        System.out.println(a.doubleValue()/1000);
    }
}
