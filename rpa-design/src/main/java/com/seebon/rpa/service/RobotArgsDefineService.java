package com.seebon.rpa.service;


import com.seebon.rpa.entity.robot.RobotArgsDefine;
import com.seebon.rpa.entity.robot.vo.RobotAppArgsVO;

import java.util.List;

public interface RobotArgsDefineService {
    /**
     * 添加参数定义
     *
     * @param robotArgsDefine
     * @return
     */
    int saveAppArgs(RobotArgsDefine robotArgsDefine);

    /**
     * 查找参数定义
     *
     * @return
     */
    List<RobotArgsDefine> findArgs(String scope, String argsCode);

    /**
     * 根据fieldKey删除自定义信息
     *
     * @param fieldKey
     * @return
     */
    int deleteArgsDefine(String fieldKey, String appCode);

    /**
     * 编辑自定义信息
     *
     * @param robotAppArgsVO
     * @return
     */
    int updateArgsDefine(RobotAppArgsVO robotAppArgsVO, String fieldKey);

    List<RobotArgsDefine> listRobotArgsDefine(String appCode);
}
