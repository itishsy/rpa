package com.seebon.rpa.service;

import com.seebon.rpa.entity.IgGridDefaultPage;
import com.seebon.rpa.entity.robot.dto.robotWarn.RobotWarnMsgDTO;
import com.seebon.rpa.entity.robot.dto.robotWarn.RobotWarnMsgForwardDTO;
import com.seebon.rpa.entity.robot.po.robotWarn.RobotWarnMsgList;
import com.seebon.rpa.entity.robot.vo.robotWarn.RobotWarnConfigBaseVO;

import java.util.Map;

/**
 * @author ZhenShijun
 * @dateTime 2022-12-02 10:31:35
 */
public interface RobotWarnService {
    
    void updateStatus(Integer id, Integer status);


    int addOrUpdate(RobotWarnConfigBaseVO vo);

    IgGridDefaultPage<RobotWarnConfigBaseVO> getByPage(Map<String, Object> params);

    void sendExecErrorMsg(RobotWarnMsgDTO msgDTO);

    IgGridDefaultPage<RobotWarnMsgList> getMsgPage(Map<String, Object> params);

    void forwardMsg(RobotWarnMsgForwardDTO dto);
}
