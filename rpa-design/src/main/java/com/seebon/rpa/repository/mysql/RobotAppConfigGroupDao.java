package com.seebon.rpa.repository.mysql;

import com.seebon.rpa.entity.robot.RobotAppConfigCondition;
import com.seebon.rpa.entity.robot.RobotAppConfigForm;
import com.seebon.rpa.entity.robot.RobotAppConfigGroup;
import com.seebon.rpa.entity.robot.RobotArgsDefine;
import com.seebon.rpa.entity.robot.dto.design.RobotAppConfigGroupVO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface RobotAppConfigGroupDao extends Mapper<RobotAppConfigGroup> {

    Integer addInput(RobotAppConfigForm configInput);

    int insertGroupWithIdentity(RobotAppConfigGroup group);

    List<RobotAppConfigGroup> selectGroupByIds(List<Integer> ids);

    Integer addGroup(RobotAppConfigGroup configGroup);

    Integer addGroupItem(RobotArgsDefine configGroupItem);

    Integer checkGroupItem(RobotArgsDefine robotArgsDefine);

    /**
     * 可以根据主键，分组明细id 删除条件以后可扩展
     * @param groupItemId
     */
    void deleteCondition(Integer groupItemId);

    Integer addConditionList(@Param("conditionList") List<RobotAppConfigCondition> conditionList);

    List<RobotAppConfigForm> getInputInfo(@Param("configInput") RobotAppConfigForm configInput , @Param("robotCodeList") Set robotCodeList);

    List<RobotAppConfigGroup> getGroupInfo(RobotAppConfigGroup configGroup);

    List<RobotArgsDefine> getGroupItemInfo(RobotArgsDefine robotArgsDefine);

    int countGroupItemInfo(RobotArgsDefine robotArgsDefine);

    Integer editGroupInfo(RobotAppConfigGroup configGroup);


    int deleteRobotAppConfigGroupByGroupId(String id);

    int deleteRobotAppConfigGroupItemByGroupId(String id);

    Integer deleteGroupItemById(@Param("id") String id);

    void updateGroupItemConditionById(RobotArgsDefine groupItem);

    void deleteRobotAppConfigConditionByGroupItemIds(List<Integer> list);

    List<RobotAppConfigCondition> getConditionByGroupItemId(Integer id);

    void updateRobotAppStatusById(@Param("appCode") List<String> appCode);

    void updateInputInfo(RobotAppConfigForm configInput);

    List<RobotAppConfigGroup> getGroupBaseInfo(RobotAppConfigGroup configGroup);

    List<RobotAppConfigGroup> getGroupInfoByInputId(HashSet<Integer> set);

    void updateGroupItem(RobotArgsDefine groupItem);

    Integer countInputInfo(RobotAppConfigForm input);

    List<RobotAppConfigForm> getInputInfoByType(RobotAppConfigForm configInput);

    void deleteInputById(String id);

    List<RobotAppConfigForm> getInputInfoByInputId(String inputId);

    Integer countGroup(String inputId);

    List<RobotAppConfigForm> getInputInfoById(String inputId);

    int checkColumnKey(@Param("inputId") String inputId,@Param("columnKey") String columnKey);

    void editGroupItem(RobotArgsDefine configGroupItem);

    List<RobotAppConfigGroup> getGroup(RobotAppConfigGroup configGroup);

    List<String> getAllConditionColumnByGroupId(Integer groupId);

    List<RobotAppConfigGroup> getGroupInfoByType(String type);

    List<Integer> getGroupIdByType(String type);

    void editGroupInfoByGroupIds(RobotAppConfigGroupVO configGroup, String oldGroupName, List<String> ids);

    int countGroupNameByType(String type,String groupName);

    String getGroupNameByid(Integer id);

    List<String> getGroupIdByGroupName(String groupName);

    List<String> getAllColumnKey(String inputId,String type,String appCode);

    int countColumnKeyByInputPrimary(@Param("type")String type ,
                                     @Param("newInputId") String newInputId,
                                     @Param("columnKeys") List<String> columnKeys,
                                     @Param("appCode") String appCode);

    String getInputPrimaryByInputId(String newInputId, String type);

    void editGroupInputIdByPrimarys(String newInputId, String type, String groupName,String appCode);

    List<String> getInputAppCodeByInputIdAndType(String newInputId, String type);

    void saveGroupAndType(String type, String groupName);

    void updateGroupType(String type, String oldGroupName, String groupName);

    void editGroupNameById(String groupName, Integer id);

    void deleteGroupTypeByGroupNameAndType(String groupName, String type);

    void editGroupNameByTypeAndNewGroupName(String type, String oldGroupName, String groupName, List<String> ids,String appCode);

    List<String> getInputAppCodeByTypeAndInputId(String type,String inputId);

    List<RobotAppConfigGroup> getGroupInfoByNotThis(RobotAppConfigGroupVO configGroup);

    int countGroupNameByTypeAndAppCode(String type, String groupName, String appCode);
}
