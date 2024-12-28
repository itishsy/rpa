package com.seebon.rpa.service;

import com.seebon.rpa.entity.IgGridDefaultPage;
import com.seebon.rpa.entity.robot.RobotAppEnv;
import com.seebon.rpa.entity.robot.dto.design.RobotAppEnvVO;

import java.util.Map;

public interface RobotAppEnvService {

    /**
     * 列表查询
     *
     * @param map
     * @return
     */
    IgGridDefaultPage<RobotAppEnv> listPage(Map<String, Object> map);

    /**
     * 添加机器人应用环境
     *
     * @return
     */
    Integer addRobotAppEnv(RobotAppEnv robotAppEnv);

    /**
     * 获取机器人应用环境
     *
     * @return
     */
    RobotAppEnv getById(Integer id);

    /**
     * 删除机器人应用环境
     *
     * @return
     */
    Integer deleteById(Integer id);

    Integer addRobotApps(RobotAppEnvVO robotAppEnvVO);
}
