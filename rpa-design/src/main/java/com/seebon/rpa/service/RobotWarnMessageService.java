package com.seebon.rpa.service;

import com.seebon.rpa.entity.IgGridDefaultPage;
import com.seebon.rpa.entity.robot.dto.design.OperationRequestVo;
import com.seebon.rpa.entity.robot.po.design.RobotOperationMessageConfig;

import java.util.List;
import java.util.Map;

/**
 * TODO
 *
 * @author zjf
 * @describe 消息业务接口
 * @date 2023/4/21 9:46
 */
public interface RobotWarnMessageService{

    IgGridDefaultPage<RobotOperationMessageConfig> listPage(Map<String, Object> map);

    void insertOrUpdateMessage(RobotOperationMessageConfig robotOperationMessageConfig);

    void updateStatus(RobotOperationMessageConfig robotOperationMessageConfig);

    void sendMsg(RobotOperationMessageConfig robotOperationMessageConfig);

    RobotOperationMessageConfig selectByWarnType(String warnType);

    RobotOperationMessageConfig buildContent(List<OperationRequestVo> list, String warnType);

    String replaceContent(OperationRequestVo operationRequestVo,String content);

}
