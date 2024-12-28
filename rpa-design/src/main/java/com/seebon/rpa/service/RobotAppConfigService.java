package com.seebon.rpa.service;


import com.seebon.rpa.entity.robot.*;
import com.seebon.rpa.entity.robot.dto.design.RobotAppConfigConditionVO2;
import com.seebon.rpa.entity.robot.dto.design.RobotAppConfigConditionVO5;
import com.seebon.rpa.entity.robot.dto.design.RobotAppConfigGroupVO;
import com.seebon.rpa.entity.robot.dto.design.RobotCopyArgsVo;
import com.seebon.rpa.entity.robot.vo.design.RobotAppConfigGroupItemVO;
import com.seebon.rpa.entity.robot.vo.design.RobotAppFormVO;

import java.util.List;

public interface RobotAppConfigService {

    List<RobotAppConfigGroup> getGroupInfo(String robotType);

    Integer addInput(RobotAppConfigForm configInput);

    void addGroup(RobotAppConfigGroupVO configGroup);

    Integer addGroupItem(RobotAppConfigGroupItemVO configGroupItem);

    Integer checkGroupItem(RobotArgsDefine robotArgsDefine);

    Integer addCondition(RobotAppConfigConditionVO2 configConditions);

    List<RobotArgsDefine> getGroupItemInfo(RobotArgsDefine configGroupItem);

    void editGroupInfo(RobotAppConfigGroupVO configGroup);

    Integer deleteGroupInfo(RobotAppFormGroup group);

    Integer deleteGroupItemById(String id);

    List<RobotAppFormVO> getRobotConfigInfoById(String createRobotAppId);

    RobotAppConfigConditionVO5 getConditionByGroupItemId(Integer id);

    void updateInputInfo(RobotAppConfigForm configInput);

    void logicDeleteGroupInfo(String groupName, String groupId, String appCode);

    Integer logicDeleteGroupItemById(Integer argsDefineId);

    List<RobotAppConfigForm> getInputInfoByType(RobotAppConfigForm configInput);

    Integer editGroupItem(RobotAppConfigGroupItemVO configGroupItem);

    Integer changeForm(RobotAppConfigGroupVO groupVO,Integer formId);

    Integer editGroupItemOrder(RobotAppConfigGroupItemVO configGroupItem);

    void copyArgs(RobotCopyArgsVo robotCopyArgsVo);

    List<RobotAppFormVO> getGeneralArgs(Integer businessType);

    void addGeneralArgs(List<RobotAppFormVO> robotCopyArgsVo,String appCode);

    List<RobotArgsDefine> getFiledNameList(String keyWord);
}
