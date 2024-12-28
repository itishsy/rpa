package com.seebon.rpa.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.seebon.rpa.auth.SecurityContext;
import com.seebon.rpa.auth.Session;
import com.seebon.rpa.entity.robot.RobotClientApp;
import com.seebon.rpa.entity.robot.RobotClientPriority;
import com.seebon.rpa.repository.mysql.RobotClientAppDao;
import com.seebon.rpa.repository.mysql.RobotClientPriorityDao;
import com.seebon.rpa.service.RobotClientPriorityService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RobotClientPriorityImpl implements RobotClientPriorityService {
    @Autowired
    private RobotClientPriorityDao clientPriorityDao;
    @Autowired
    private RobotClientAppDao clientAppDao;

    @Override
    public List<RobotClientPriority> list() {
        Session session = SecurityContext.currentUser();
        Example example = new Example(RobotClientPriority.class);
        example.createCriteria().andEqualTo("clientId", session.getCustId());
        return clientPriorityDao.selectByExample(example);
    }

    @Override
    public List<RobotClientApp> listApp() {
        Session session = SecurityContext.currentUser();
        Example example = new Example(RobotClientApp.class);
        example.createCriteria().andEqualTo("clientId", session.getCustId()).andEqualTo("status", 1);
        List<RobotClientApp> list = clientAppDao.selectByExample(example);
        if (CollectionUtils.isEmpty(list)) {
            return list;
        }
        List<RobotClientApp> result = Lists.newArrayList();
        Map<String, List<RobotClientApp>> groupMap = list.stream().collect(Collectors.groupingBy(vo -> vo.getAppCode()));
        for (String appCode : groupMap.keySet()) {
            List<RobotClientApp> value = groupMap.get(appCode);
            if (CollectionUtils.isEmpty(value)) {
                continue;
            }
            result.add(value.get(0));
        }
        return result;
    }

    @Override
    public List<RobotClientPriority> list(String machineCode) {
        Session session = SecurityContext.currentUser();
        Map<String, Object> params = Maps.newHashMap();
        params.put("machineCode", machineCode);
        params.put("clientId", session.getCustId());
        return clientPriorityDao.selectByParams(params);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int save(List<RobotClientPriority> priorities) {
        Session session = SecurityContext.currentUser();
        //先删除
        clientPriorityDao.deleteByClientId(session.getCustId());
        if (CollectionUtils.isEmpty(priorities)) {
            return 1;
        }
        for (RobotClientPriority priority : priorities) {
            priority.setClientId(session.getCustId());
            priority.setSyncMachineCode(null);
            priority.setCreateTime(new Date());
            priority.setUpdateTime(new Date());
        }
        clientPriorityDao.insertList(priorities);
        return 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void callbackPriority(String machineCode) {
        if (StringUtils.isBlank(machineCode)) {
            return;
        }
        Session session = SecurityContext.currentUser();
        Map<String, Object> params = Maps.newHashMap();
        params.put("machineCode", machineCode);
        params.put("clientId", session.getCustId());
        List<RobotClientPriority> list = clientPriorityDao.selectByParams(params);
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        for (RobotClientPriority priority : list) {
            RobotClientPriority update = new RobotClientPriority();
            update.setId(priority.getId());
            update.setSyncTime(new Date());
            update.setSyncMachineCode(this.getSyncMachineCode(priority, machineCode));
            clientPriorityDao.updateByPrimaryKeySelective(update);
        }
    }

    private String getSyncMachineCode(RobotClientPriority priority, String machineCode) {
        if (StringUtils.isBlank(priority.getSyncMachineCode())) {
            return machineCode;
        }
        return priority.getSyncMachineCode() + "," + machineCode;
    }
}
