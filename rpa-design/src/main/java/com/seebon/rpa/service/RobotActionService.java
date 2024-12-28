package com.seebon.rpa.service;


import com.seebon.rpa.entity.robot.RobotAction;
import com.seebon.rpa.entity.robot.vo.RobotAppArgsVO;

import java.util.List;

public interface RobotActionService {

    /**
     * 所有操作指令
     * @return
     */
    List<RobotAction> findAll();


    /**
     * 查找操作目标
     * @return
     */
    String findTargetType(String actionCode);

    /**
     * 查找动态字段
     * @param argsCode
     * @return
     */
    List<RobotAppArgsVO> findActionArgs(String argsCode);

    /**
     * 查找动态字段
     * @param argsCode
     * @return
     */
    List<RobotAppArgsVO> findTargetArgs(String argsCode);

}
