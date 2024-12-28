package com.seebon.rpa.service;

import com.seebon.rpa.entity.robot.RobotDataDict;

import java.util.List;

public interface RobotDataDictService {

    /**
     * 查询所有Key
     * @return
     */
    List<String> findAllKey();

    /**
     * 根据dictKey查询
     * @param dictKey
     * @return
     */
    List<RobotDataDict> findList(String dictKey);


    /**
     * 根据parentKey查询
     * @param parentKey
     * @return
     */
    List<String> findKeys(String parentKey);

    /**
     * 根据dictCode查询
     * @param dictCode
     * @return
     */
    RobotDataDict getDictByCode(String dictCode);

}
