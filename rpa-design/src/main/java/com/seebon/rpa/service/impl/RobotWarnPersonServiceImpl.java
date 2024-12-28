package com.seebon.rpa.service.impl;

import com.seebon.rpa.entity.robot.po.design.RobotWarnPersonType;
import com.seebon.rpa.repository.mysql.RobotWarnPersonDao;
import com.seebon.rpa.service.RobotWarnPersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * TODO
 *
 * @author s
 * @describe
 * @date 2023/4/21 15:26
 */
@Service
public class RobotWarnPersonServiceImpl implements RobotWarnPersonService{
    @Autowired
    private RobotWarnPersonDao robotWarnPersonDao;
    @Override
    public List<RobotWarnPersonType> getPersonType(){
        List<RobotWarnPersonType> robotWarnPersonTypes=robotWarnPersonDao.selectAll();
        return robotWarnPersonTypes;
    }
}
