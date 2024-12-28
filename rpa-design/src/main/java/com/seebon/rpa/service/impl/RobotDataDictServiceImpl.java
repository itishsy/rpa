package com.seebon.rpa.service.impl;

import com.seebon.rpa.entity.robot.RobotDataDict;
import com.seebon.rpa.repository.mysql.RobotDataDictDao;
import com.seebon.rpa.service.RobotDataDictService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class RobotDataDictServiceImpl implements RobotDataDictService {

    @Autowired
    private RobotDataDictDao robotDataDictDao;

    @Override
    public List<String> findAllKey() {
        List<String> keys = new ArrayList<>();
        List<RobotDataDict> dictKeys = robotDataDictDao.selectAll();
        for(RobotDataDict dictKey : dictKeys){
            if(!keys.contains(dictKey.getDictKey())){
                keys.add(dictKey.getDictKey());
            }
        }
        return keys;
    }

    @Override
    public List<RobotDataDict> findList(String dictKey) {
        Example example = new Example(RobotDataDict.class);
        example.createCriteria()
                .andEqualTo("dictKey", dictKey);
        example.orderBy("displayOrder");
        return robotDataDictDao.selectByExample(example);
    }

    @Override
    public List<String> findKeys(String parentKey) {
        Example example = new Example(RobotDataDict.class);
        example.createCriteria()
                .andEqualTo("parentKey", parentKey);
        List<RobotDataDict> dictList = robotDataDictDao.selectByExample(example);
        List<String> keys = new ArrayList<>();
        for(RobotDataDict dict : dictList){
            keys.add(dict.getDictKey());
        }
        return keys;
    }

    @Override
    public RobotDataDict getDictByCode(String dictCode){
        Example example = new Example(RobotDataDict.class);
        example.createCriteria().andEqualTo("dictCode", dictCode);
        example.setOrderByClause("display_order asc");
        return robotDataDictDao.selectOneByExample(example);
    }
}
