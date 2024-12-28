package com.seebon.rpa.service.impl;

import com.seebon.rpa.entity.robot.RobotConfig;
import com.seebon.rpa.repository.mapper.RobotConfigMapper;
import com.seebon.rpa.service.RobotConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class RobotConfigServiceImpl implements RobotConfigService {
    @Autowired
    private RobotConfigMapper configMapper;

    @Override
    public void saveConfig(List<RobotConfig> list) {
        list.stream().forEach(ca -> {
            ca.setSyncTime(null);
            ca.setSyncMachineCode(null);
            configMapper.deleteByPrimaryKey(ca.getId());
        });
        list.stream().forEach(ca -> {
            RobotConfig appCa = new RobotConfig();
            BeanUtils.copyProperties(ca, appCa);
            if (appCa.getDisabled() != null && appCa.getDisabled() == 1) {
                configMapper.insert(appCa);
            } else {
                configMapper.deleteByPrimaryKey(appCa.getId());
            }
        });
    }

    @Override
    public List<RobotConfig> listConfig() {
        return configMapper.selectAll();
    }
}
