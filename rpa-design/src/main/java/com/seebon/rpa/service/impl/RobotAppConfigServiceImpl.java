package com.seebon.rpa.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Sets;
import com.seebon.common.utils.DateUtils;
import com.seebon.rpa.BusinessException;
import com.seebon.rpa.auth.SecurityContext;
import com.seebon.rpa.auth.Session;
import com.seebon.rpa.entity.agent.enums.BusinessTypeEnum;
import com.seebon.rpa.entity.robot.*;
import com.seebon.rpa.entity.robot.dto.design.RobotAppConfigConditionVO2;
import com.seebon.rpa.entity.robot.dto.design.RobotAppConfigConditionVO5;
import com.seebon.rpa.entity.robot.dto.design.RobotAppConfigGroupVO;
import com.seebon.rpa.entity.robot.dto.design.RobotCopyArgsVo;
import com.seebon.rpa.entity.robot.vo.design.RobotAppConfigGroupItemVO;
import com.seebon.rpa.entity.robot.vo.design.RobotAppFormVO;
import com.seebon.rpa.entity.robot.vo.design.RobotAppGroupVO;
import com.seebon.rpa.entity.robot.vo.design.RobotArgsDefineVO;
import com.seebon.rpa.entity.saas.dto.AddrBusinessChangeProcessDTO;
import com.seebon.rpa.remote.RpaSaasService;
import com.seebon.rpa.repository.mysql.*;
import com.seebon.rpa.service.RobotAppConfigService;
import com.seebon.rpa.utils.BeanCopyUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RobotAppConfigServiceImpl implements RobotAppConfigService {

    @Autowired
    private RobotAppConfigGroupDao robotAppConfigGroupDao;
    @Autowired
    private RobotAppDao robotAppDao;

    @Autowired
    private RobotInputTypeDao robotInputTypeDao;
    @Autowired
    private RobotAppFormGroupDao formGroupDao;
    @Autowired
    private RobotArgsDefineDao argsDefineDao;
    @Autowired
    private RobotAppConfigFormDao configFormDao;
    @Autowired
    private RobotAppConfigConditionDao conditionDao;
    @Autowired
    private RpaSaasService rpaSaasService;

    /**
     * 根据机器人应用类型查表单信息
     * @param configInput
     * @return
     */
    @Override
    public List<RobotAppConfigForm> getInputInfoByType(RobotAppConfigForm configInput) {
        String type = configInput.getRobotType();
        if (StringUtils.isBlank(type)){
            throw new BusinessException("机器人应用类型为空");
        }
        Example example = new Example(RobotAppConfigForm.class);
        example.createCriteria().andEqualTo("robotType", type);
        return configFormDao.selectByExample(example);
    }

    /**
     * 根据分组明细id逻辑删除分组明细
     *
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Integer logicDeleteGroupItemById(Integer argsDefineId) {
        Example example = new Example(RobotAppConfigCondition.class);
        example.createCriteria().andEqualTo("relateConditionArgsId",argsDefineId);
        List<RobotAppConfigCondition> robotAppConfigConditions = conditionDao.selectByExample(example);

        if (CollectionUtils.isNotEmpty(robotAppConfigConditions)){
            Integer integer = robotAppConfigConditions.stream()
                    .map(RobotAppConfigCondition::getArgsDefineId).distinct()
                    .findFirst().orElseThrow(BusinessException::new);
            RobotArgsDefine robotArgsDefine = argsDefineDao.selectByPrimaryKey(integer);
            throw new BusinessException("当前字段是{"+robotArgsDefine.getFieldName()+"字段}的联动条件，不允许删除");
        }
        RobotArgsDefine robotArgsDefine = new RobotArgsDefine();
        robotArgsDefine.setId(argsDefineId);
        //逻辑删除
        robotArgsDefine.setIsDelete(1);
        //更新robotApp
        RobotArgsDefine selectByPrimaryKey = argsDefineDao.selectByPrimaryKey(argsDefineId);
        robotAppDao.updateStatus(selectByPrimaryKey.getArgsCode());
        int count = argsDefineDao.updateByPrimaryKeySelective(robotArgsDefine);

        if (robotArgsDefine == null) {
            throw new BusinessException("未找到需要调整顺序的配置项信息，请核查！");
        }

        Example addExample = new Example(RobotArgsDefine.class);
        addExample.createCriteria()
                .andEqualTo("isDelete",0).andEqualTo("argsCode",robotArgsDefine.getArgsCode()).andEqualTo("formGroupId",
                selectByPrimaryKey.getFormGroupId()).andNotEqualTo("id", argsDefineId);
        List<RobotArgsDefine> robotArgsDefines = argsDefineDao.selectByExample(addExample);

        Optional<RobotArgsDefine> first = robotArgsDefines.stream().filter(it -> it.getDisplayOrder() == null).findFirst();
        if (first.isPresent()) {
            robotArgsDefines.sort(Comparator.comparing(RobotArgsDefine::getId));
            for (int i = 1; i<=robotArgsDefines.size(); i++) {
                robotArgsDefines.get(i-1).setDisplayOrder(i);
            }
        } else {
            robotArgsDefines.sort(Comparator.comparing(RobotArgsDefine::getDisplayOrder).thenComparing(RobotArgsDefine::getId));
        }
        for (int i=1; i<=robotArgsDefines.size(); i++) {
            RobotArgsDefine item = robotArgsDefines.get(i-1);
            item.setDisplayOrder(i);
            argsDefineDao.updateByPrimaryKeySelective(item);
        }

        saveAddrBusinessProcess(selectByPrimaryKey.getArgsCode(), String.format("删除%s类型字段：%s-%s，%s%s", getFieldTypeName(selectByPrimaryKey.getFieldType()),
                selectByPrimaryKey.getFieldName(),selectByPrimaryKey.getFieldKey(),
                selectByPrimaryKey.getRequired()==0?"必填":"非必填",
                StringUtils.isNotBlank(selectByPrimaryKey.getDropDownOption())?String.format(",下拉选项为：%s", selectByPrimaryKey.getDropDownOption()):""), null);

        return count;
    }

    @Override
    public void updateInputInfo(RobotAppConfigForm configInput) {
        robotAppConfigGroupDao.updateInputInfo(configInput);
    }

    @Override
    public List<RobotAppFormVO> getRobotConfigInfoById(String appCode) {
        Example example = new Example(RobotAppFormGroup.class);
        example.createCriteria().andEqualTo("appCode",appCode);
        List<RobotAppFormGroup> formGroups = formGroupDao.selectByExample(example);
        List<RobotAppFormVO> robotAppFormVOList = new ArrayList<>();

        List<Integer> formIds = formGroups.stream().map(RobotAppFormGroup::getFormId).distinct().collect(Collectors.toList());
        for (Integer formId : formIds) {
            RobotAppConfigForm robotAppConfigForm = configFormDao.selectByPrimaryKey(formId);
            RobotAppFormVO robotAppFormVO = new RobotAppFormVO();

            Optional.ofNullable(robotAppConfigForm).ifPresent(s -> BeanUtils.copyProperties(s,robotAppFormVO));
            Example example1 = new Example(RobotAppFormGroup.class);
            example1.createCriteria().andEqualTo("formId",formId).andEqualTo("appCode",appCode);
            List<RobotAppFormGroup> formGroupList = formGroupDao.selectByExample(example1);
            List<RobotAppFormGroup> robotAppFormGroups = formGroupList.stream().filter(s -> s.getIsDelete().equals(0))
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(robotAppFormGroups)){
                robotAppFormVO.setSort(robotAppFormGroups.get(0).getId());
            }

            List<Integer> groupIds = robotAppFormGroups.stream()
                    .map(RobotAppFormGroup::getGroupId)
                    .collect(Collectors.toList());
            List<RobotAppGroupVO> robotAppGroupVOS = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(groupIds)){
                List<RobotAppConfigGroup> robotAppConfigGroups = robotAppConfigGroupDao.selectGroupByIds(groupIds);
                robotAppGroupVOS = BeanCopyUtils.copyListProperties(robotAppConfigGroups, RobotAppGroupVO::new);
            }

            for (RobotAppGroupVO robotAppGroupVO : robotAppGroupVOS) {
                Example example2 = new Example(RobotAppFormGroup.class);
                example2.createCriteria()
                        .andEqualTo("isDelete",0)
                        .andEqualTo("appCode",appCode)
                        .andEqualTo("formId",formId)
                        .andEqualTo("groupId",robotAppGroupVO.getId());
                RobotAppFormGroup formGroup = formGroupDao.selectOneByExample(example2);
                robotAppGroupVO.setSort(formGroup.getId());
                Example example3 = new Example(RobotArgsDefine.class);
                example3.createCriteria().andEqualTo("scope","app").andEqualTo("formGroupId",formGroup.getId()).andEqualTo("isDelete",0);
                List<RobotArgsDefine> robotArgsDefines = argsDefineDao.selectByExample(example3);
                /*List<RobotArgsDefine> collect = robotArgsDefines.stream().filter(s -> s.getIsDelete().equals(0))
                        .collect(Collectors.toList());*/

                List<RobotArgsDefineVO> list = new ArrayList<>();Optional<RobotArgsDefine> first = robotArgsDefines.stream().filter(it -> it.getDisplayOrder() == null).findFirst();
                if (first.isPresent()) {
                    robotArgsDefines.sort(Comparator.comparing(RobotArgsDefine::getId));
                    for (int i = 1; i<=robotArgsDefines.size(); i++) {
                        robotArgsDefines.get(i-1).setDisplayOrder(i);
                    }
                } else {
                    robotArgsDefines.sort(Comparator.comparing(RobotArgsDefine::getDisplayOrder).thenComparing(RobotArgsDefine::getId));
                }
                for (RobotArgsDefine robotArgsDefine : robotArgsDefines) {
                    RobotArgsDefineVO robotArgsDefineVO = new RobotArgsDefineVO();
                    BeanUtils.copyProperties(robotArgsDefine,robotArgsDefineVO);
                    Example example4 = new Example(RobotAppConfigCondition.class);
                    example4.createCriteria().andEqualTo("argsDefineId",robotArgsDefine.getId());
                    List<RobotAppConfigCondition> conditions = conditionDao.selectByExample(example4);
                    robotArgsDefineVO.setConditionList(conditions);
                    list.add(robotArgsDefineVO);
                }
                robotAppGroupVO.setArgsDefineVOS(list);
            }
            List<RobotAppGroupVO> groupVOS = robotAppGroupVOS.stream().sorted(Comparator.comparingInt(RobotAppGroupVO::getSort))
                    .collect(Collectors.toList());
            robotAppFormVO.setRobotAppGroupVOS(groupVOS);

            robotAppFormVOList.add(robotAppFormVO);
        }
        List<RobotAppFormVO> collect = robotAppFormVOList.stream().filter(s -> s.getSort() != null)
                .collect(Collectors.toList());
        return collect.stream().sorted(Comparator.comparingInt(RobotAppFormVO::getSort))
                .collect(Collectors.toList());
    }

    public List<RobotAppConfigForm> getInputBaseInfo(RobotAppConfigForm configInput) {
        return robotAppConfigGroupDao.getInputInfo(configInput,null);
    }

    @Override
    public RobotAppConfigConditionVO5 getConditionByGroupItemId(Integer id) throws BusinessException{
        if (null == id){
            throw new BusinessException("参数错误");
        }
        Example example = new Example(RobotAppConfigCondition.class);
        example.createCriteria().andEqualTo("argsDefineId",id);
        List<RobotAppConfigCondition> robotAppConfigConditions = conditionDao.selectByExample(example);
        RobotArgsDefine robotArgsDefine = argsDefineDao.selectByPrimaryKey(id);
        RobotAppConfigConditionVO5 configConditionVO5 = new RobotAppConfigConditionVO5();
        configConditionVO5.setFieldName(robotArgsDefine.getFieldName());
        configConditionVO5.setShowOrHidden(robotArgsDefine.getShowOrHidden());
        configConditionVO5.setRobotAppConfigConditions(robotAppConfigConditions);
        configConditionVO5.setRelation(robotAppConfigConditions.get(0).getRelation());
        return configConditionVO5;
    }

    @Override
    public Integer deleteGroupItemById(String id) {

        return robotAppConfigGroupDao.deleteGroupItemById(id);
    }

    /**
     * 根据分组id 逻辑删除 分组，分组明细，明细条件
     *
     * @param groupName
     * @param groupId 分组id
     * @return
     */
    @Override
    @Transactional
    public void  logicDeleteGroupInfo(String groupName, String groupId, String appCode) throws BusinessException{
        /*try {
            if (StringUtils.isBlank(groupId)){
                throw new BusinessException("groupId为空");
            }
            //如果该表单下没有其它分组信息则物理删除表单
            RobotAppConfigGroup coonfigGroup = new RobotAppConfigGroup();
            coonfigGroup.setId(Integer.valueOf(groupId));
            List<RobotAppConfigGroup> baseInfo = robotAppConfigGroupDao.getGroupBaseInfo(coonfigGroup);

            String inputId = baseInfo.get(0).getInputId();
            String type = baseInfo.get(0).getType();

            Integer count = robotAppConfigGroupDao.countGroup(inputId);
            if (count <= 1){
                robotAppConfigGroupDao.deleteInputById(inputId);
            }

            //分组内存在字段，则分组不可删除。点击弹出提示：存在字段信息，此分组不能删除。否则分组可删除。
            RobotArgsDefine item = new RobotArgsDefine();
            item.setGroupId(Integer.valueOf(groupId));
            int num = robotAppConfigGroupDao.countGroupItemInfo(item);
            if (num != 0){
                throw new BusinessException("存在字段信息，此分组不能删除");
            }

            //逻辑删除分组
            coonfigGroup.setStatus("1");
            coonfigGroup.setGroupName("");
            coonfigGroup.setInputId("");
            robotAppConfigGroupDao.editGroupInfo(coonfigGroup);//修改状态 0:启用 1:禁用

            //如果该分组名称已经不存在于同类型任何表单下，则
            int num2 = robotAppConfigGroupDao.countGroupNameByTypeAndAppCode(type, groupName,null);
            if(num2 == 0){
                //物理删分组和应用类型关系表
                robotAppConfigGroupDao.deleteGroupTypeByGroupNameAndType(groupName,type);
            }

            //更新版本状态
            List<String> list = Lists.newArrayList();
            list.add(appCode);
            robotAppConfigGroupDao.updateRobotAppStatusById(list);
        }catch (Exception e){
            log.error("deleteGroupInfo()->"+e.getMessage(), e);
            throw new BusinessException("删除失败");
        }*/
    }

    /**
     * 根据分组id删除 分组，分组明细，明细条件
     * @param
     * @return
     */
    @Override
    @Transactional
    public Integer deleteGroupInfo(RobotAppFormGroup group) throws BusinessException{
        Example example2 = new Example(RobotAppFormGroup.class);
        example2.createCriteria()
                .andEqualTo("isDelete",0)
                .andEqualTo("appCode",group.getAppCode())
                .andEqualTo("formId",group.getFormId())
                .andEqualTo("groupId",group.getGroupId());
        RobotAppFormGroup formGroup = formGroupDao.selectOneByExample(example2);
        Example example = new Example(RobotArgsDefine.class);
        example.createCriteria()
                .andEqualTo("scope","app")
                .andEqualTo("isDelete",0)
                .andEqualTo("formGroupId",formGroup.getId());
        List<RobotArgsDefine> robotArgsDefines = argsDefineDao.selectByExample(example);
        if (CollectionUtils.isNotEmpty(robotArgsDefines)){
            throw new BusinessException("存在字段信息，此分组不能删除");
        }
        //逻辑删除
        formGroup.setIsDelete(1);
        //更新robotApp
        robotAppDao.updateStatus(group.getAppCode());
        return formGroupDao.updateByPrimaryKeySelective(formGroup);
    }

    /**
     * 维护分组
     * @param configGroup
     * @throws BusinessException
     */
    @Override
    @Transactional
    public void editGroupInfo(RobotAppConfigGroupVO configGroup) throws BusinessException{
       Example example = new Example(RobotAppConfigGroup.class);
       example.createCriteria().andEqualTo("robotType",configGroup.getRobotType());
        List<RobotAppConfigGroup> robotAppConfigGroups = robotAppConfigGroupDao.selectByExample(example);
        List<RobotAppConfigGroup> configGroups = robotAppConfigGroups.stream().filter(s -> configGroup.getGroupName()
                .equals(s.getGroupName()) && !configGroup.getGroupId().equals(s.getId())).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(configGroups)){
            throw new BusinessException("分组名称"+configGroup.getGroupName()+"重复，请更正后再确定保存");
        }
        RobotAppConfigGroup robotAppConfigGroup = new RobotAppConfigGroup();
        robotAppConfigGroup.setId(configGroup.getGroupId());
        robotAppConfigGroup.setRobotType(configGroup.getRobotType());
        robotAppConfigGroup.setGroupName(configGroup.getGroupName());
        robotAppConfigGroupDao.updateByPrimaryKey(robotAppConfigGroup);
        //更新robotApp
        Example formGroupExam = new Example(RobotAppFormGroup.class);
        formGroupExam.createCriteria().andEqualTo("groupId",configGroup.getGroupId());
        List<RobotAppFormGroup> robotAppFormGroups = formGroupDao.selectByExample(formGroupExam);
        List<String> appCodes = robotAppFormGroups.stream().map(RobotAppFormGroup::getAppCode)
                .collect(Collectors.toList());
        for (String appCode : appCodes) {
            robotAppDao.updateStatus(appCode);
        }
    }

    /**
     * 添加/编辑分组时校验
     * @param configGroup
     * @throws BusinessException
     */
    private void checkEditOrAddGroupInfo(RobotAppConfigGroup configGroup) throws BusinessException{
        /*try {
            if (null == configGroup){
                throw new BusinessException("参数错误");
            }
            String groupName = configGroup.getGroupName();
            String inputId = configGroup.getInputId();
            if (StringUtils.isBlank(groupName) || StringUtils.isBlank(inputId)){
                throw new BusinessException("参数错误");
            }

            //同一表单下，“有效”的分组名称不能重复 （添加入库前的新分组名经过这里，查到已经存在就代表重复，但编辑经过这里应该是>1）
            List<RobotAppConfigGroup> groupBaseInfo = robotAppConfigGroupDao.getGroup(configGroup);
            if (null != groupBaseInfo && groupBaseInfo.size() !=0){
                String inputId2 = groupBaseInfo.get(0).getInputId();
                RobotAppConfigForm configInput = new RobotAppConfigForm();
                configInput.setInputId(inputId2);

                List<RobotAppConfigForm> inputBaseInfo = getInputBaseInfo(configInput);
                if (null != inputBaseInfo && inputBaseInfo.size()>0){
                    HashSet hashSet = Sets.newHashSet();
                    for (RobotAppConfigForm input : inputBaseInfo) {
                        String inputName = input.getInputName();
                        if (StringUtils.isNotBlank(inputName)){
                            hashSet.add(inputName);
                        }
                    }
                    throw new BusinessException("当前分组已存在表单"+hashSet+"下，请勿重复添加。");
                }
            }

        } catch (Exception e) {
            log.error("checkEditOrAddGroupInfo()->"+e.getMessage(),e);
            throw new BusinessException(e.getMessage());
        }*/
    }



    /**
     * list根据某个字段去重
     * @param orderList
     * @return
     */
    public List<RobotAppConfigGroup> removeDuplicateOrder(List<RobotAppConfigGroup> orderList) {
        Set<RobotAppConfigGroup> set = new TreeSet<RobotAppConfigGroup>(new Comparator<RobotAppConfigGroup>() {
            @Override
            public int compare(RobotAppConfigGroup a, RobotAppConfigGroup b) {
                // 字符串则按照asicc码升序排列
                return a.getGroupName().compareTo(b.getGroupName());
            }
        });

        set.addAll(orderList);
        return new ArrayList<RobotAppConfigGroup>(set);
    }

    /**
     * 查分组基本信息
     * @param configGroup
     * @return
     */
    public List<RobotAppConfigGroup> getGroupBaseInfo(RobotAppConfigGroup configGroup) {
        return robotAppConfigGroupDao.getGroupBaseInfo(configGroup);
    }


    @Override
    public List<RobotAppConfigGroup> getGroupInfo(String robotType) {
        Example example = new Example(RobotAppConfigGroup.class);
        example.createCriteria().andEqualTo("robotType",robotType);
        return robotAppConfigGroupDao.selectByExample(example);
    }

    @Override
    @Transactional
    public Integer addInput(RobotAppConfigForm configForm) throws BusinessException {

        Example example = new Example(RobotAppConfigForm.class);
        example.createCriteria().andEqualTo("formCode",configForm.getFormCode())
                .andEqualTo("robotType",configForm.getRobotType());
        RobotAppConfigForm robotAppConfigForm = configFormDao.selectOneByExample(example);
        //如果formCode存在，则更新
        if (ObjectUtils.isNotEmpty(robotAppConfigForm)){
            RobotAppConfigForm robotAppConfigForm2 = new RobotAppConfigForm();
            robotAppConfigForm2.setFormCode(robotAppConfigForm.getFormCode());
            robotAppConfigForm2.setId(robotAppConfigForm.getId());
            robotAppConfigForm2.setFormName(configForm.getFormName());
            robotAppConfigForm2.setRobotType(robotAppConfigForm.getRobotType());
            return configFormDao.updateByPrimaryKey(robotAppConfigForm2);
        }else {
            //新增
            Example example1 = new Example(RobotAppConfigForm.class);
            example1.createCriteria().andEqualTo("robotType",configForm.getRobotType())
                    .andEqualTo("formName",configForm.getFormName());
            RobotAppConfigForm appConfigForm = configFormDao.selectOneByExample(example1);
            if (ObjectUtils.isNotEmpty(appConfigForm)){
                throw new BusinessException("表单名称已存在");
            }
            return configFormDao.insert(configForm);
        }

    }

    /**
     * 添加分组
     * @param configGroup
     * @return
     * @throws BusinessException
     */
    @Override
    @Transactional
    public void addGroup(RobotAppConfigGroupVO configGroup) throws BusinessException {
        if (configGroup.getFormId() != null && configGroup.getGroupId() != null) {
            Example formAndGroupExa = new Example(RobotAppFormGroup.class);
            formAndGroupExa.createCriteria().andEqualTo("appCode", configGroup.getAppCode())
                    .andEqualTo("formId", configGroup.getFormId())
                    .andEqualTo("groupId", configGroup.getGroupId())
                    .andEqualTo("isDelete", 0);
            List<RobotAppFormGroup> appFormGroups = formGroupDao.selectByExample(formAndGroupExa);
            if (CollectionUtils.isNotEmpty(appFormGroups)) {
                throw new BusinessException("该表单下已存在此分组，请勿重复添加！");
            }
        }
        Example example = new Example(RobotAppConfigGroup.class);
        example.createCriteria().andEqualTo("groupName", configGroup.getGroupName()).andEqualTo("robotType", configGroup.getRobotType());
        RobotAppConfigGroup appConfigGroup = robotAppConfigGroupDao.selectOneByExample(example);
        //如果分组名称存在，则只需添加到robot_app_form_group中间表
        if (ObjectUtils.isNotEmpty(appConfigGroup)) {
            RobotAppFormGroup formGroup = new RobotAppFormGroup();
            formGroup.setAppCode(configGroup.getAppCode());
            formGroup.setFormId(configGroup.getFormId());
            formGroup.setGroupId(appConfigGroup.getId());
            formGroup.setIsDelete(0);
            formGroupDao.insert(formGroup);
            //更新机器人应用状态
            robotAppDao.updateStatus(configGroup.getAppCode());
        } else {
            //否则先添加robot_app_config_group表，再添加添加到robot_app_form_group中间表
            RobotAppConfigGroup group = new RobotAppConfigGroup();
            group.setGroupName(configGroup.getGroupName());
            group.setRobotType(configGroup.getRobotType());
            robotAppConfigGroupDao.insertGroupWithIdentity(group);
            RobotAppFormGroup formGroup = new RobotAppFormGroup();
            formGroup.setAppCode(configGroup.getAppCode());
            formGroup.setFormId(configGroup.getFormId());
            formGroup.setGroupId(group.getId());
            formGroup.setIsDelete(0);
            formGroupDao.insert(formGroup);
            //更新机器人应用状态
            robotAppDao.updateStatus(configGroup.getAppCode());
        }
    }

    /**
     * 根据任意字段获取分组明细信息
     * @param argsDefine
     * @return
     */
    public List<RobotArgsDefine> getGroupItemInfo(RobotArgsDefine argsDefine){
        argsDefine.setShowOrHidden(1);
        int count = robotAppConfigGroupDao.countGroupItemInfo(argsDefine);
        if (count == 0){
            return null;
        }
        return robotAppConfigGroupDao.getGroupItemInfo(argsDefine);
    }

    @Override
    @Transactional
    public Integer addGroupItem(RobotAppConfigGroupItemVO configGroupItem) throws BusinessException {
        Example addExample = new Example(RobotArgsDefine.class);
        addExample.createCriteria()
               .andEqualTo("isDelete",0).andEqualTo("argsCode",configGroupItem.getAppCode())
               .andEqualTo("fieldKey",configGroupItem.getFieldKey());
        Integer count=argsDefineDao.selectCountByExample(addExample);
        if(count>0){
            throw new BusinessException("字段key【"+configGroupItem.getFieldKey()+"】不能重复");
        }

        Example example = new Example(RobotAppFormGroup.class);
        example.createCriteria()
                .andEqualTo("isDelete",0)
                .andEqualTo("appCode",configGroupItem.getAppCode())
                .andEqualTo("formId",configGroupItem.getFormId())
                .andEqualTo("groupId",configGroupItem.getGroupId());
        RobotAppFormGroup formGroup = formGroupDao.selectOneByExample(example);
        RobotArgsDefine robotArgsDefine = new RobotArgsDefine();
        BeanUtils.copyProperties(configGroupItem,robotArgsDefine);
        robotArgsDefine.setFormGroupId(formGroup.getId());
        robotArgsDefine.setArgsCode(configGroupItem.getAppCode());
        robotArgsDefine.setScope("app");
        robotArgsDefine.setIsDelete(0);
        if (StringUtils.isBlank(robotArgsDefine.getDropDownOption())
                && !"password".equals(robotArgsDefine.getFieldType()) && isPasswordKey(robotArgsDefine.getFieldKey())) {
            robotArgsDefine.setFieldType("password");
        }
        //更新robotApp
        robotAppDao.updateStatus(configGroupItem.getAppCode());
        int i = argsDefineDao.insertSelective(robotArgsDefine);
        saveAddrBusinessProcess(configGroupItem.getAppCode(), String.format("新增%s类型字段：%s-%s，%s%s", getFieldTypeName(configGroupItem.getFieldType()),
                configGroupItem.getFieldName(),configGroupItem.getFieldKey(),
                configGroupItem.getRequired()==0?"必填":"非必填",
                StringUtils.isNotBlank(configGroupItem.getDropDownOption())?String.format(",下拉选项为：%s", configGroupItem.getDropDownOption()):""), null);
        return i;
    }

    private String getFieldTypeName(String fieldType) {
        if ("text".equals(fieldType)) {
            return "文本框";
        } else if ("password".equals(fieldType)) {
            return "密码框";
        } else if ("number".equals(fieldType)) {
            return "数值框";
        }else if ("date".equals(fieldType)) {
            return "日期选择框";
        } else if ("singleDropList".equals(fieldType)) {
            return "下拉单选框";
        } else if ("multipleDropList".equals(fieldType)) {
            return "下拉多选框";
        } else if ("single".equals(fieldType)) {
            return "单选框";
        } else if ("multiple".equals(fieldType)) {
            return "复选框";
        } else if ("photoUpload".equals(fieldType)) {
            return "图片上传";
        } else if ("fileUpload".equals(fieldType)) {
            return "附件上传";
        }
        return "";
    }

    private void saveAddrBusinessProcess(String appCode, String newChedule, String oldChedule) {
        Example example = new Example(RobotApp.class);
        example.createCriteria().andEqualTo("appCode", appCode);
        RobotApp robotApp = robotAppDao.selectOneByExample(example);
        if (robotApp == null) {
            return;
        }
        String appArgs = robotApp.getAppArgs();
        if (StringUtils.isNotBlank(appArgs)) {
            try {
                JSONObject jsonObject = JSONObject.parseObject(appArgs);
                String addrName = jsonObject.getString("addrName");
                String addrId = jsonObject.getString("addrId");
                String businessType = jsonObject.getString("businessType");

                Integer busType = "1001001".equals(businessType)?1:2;

                Session session = SecurityContext.currentUser();

                AddrBusinessChangeProcessDTO dto = new AddrBusinessChangeProcessDTO();
                dto.setAddrId(Integer.valueOf(addrId));
                dto.setAddrName(addrName);
                dto.setBusinessType(busType);
                dto.setBusinessTypeName(BusinessTypeEnum.getNameByCode(busType));
                dto.setChangeType("修改信息配置");
                dto.setChangeReason(null);
                dto.setCreateId((int)session.getId());
                dto.setCreateName(session.getName());
                dto.setCreateTime(DateUtils.dateToStr(new Date(), "yyyy-MM-dd HH:mm:ss"));
                dto.setOriginalValue(oldChedule);
                dto.setNewValue(newChedule);
                rpaSaasService.saveAddrBusinessProcess(dto);
            }catch (Exception e) {
                log.info("保存地区业务变动数据异常：{}", e.getMessage());
            }
        }
    }

    private boolean isPasswordKey(String fieldKey) {
        Set<String> pwKeys = Sets.newHashSet("password", "pw", "pword");
        boolean result = false;
        for (String key : pwKeys) {
            result = fieldKey.toLowerCase().endsWith(key);
            if (result) {
                break;
            }
        }
        return result;
    }

    @Override
    public Integer editGroupItem(RobotAppConfigGroupItemVO configGroupItem) {
        Example addExample = new Example(RobotArgsDefine.class);
        addExample.createCriteria()
                  .andEqualTo("isDelete",0).andEqualTo("argsCode",configGroupItem.getAppCode())
                  .andEqualTo("fieldKey",configGroupItem.getFieldKey()).andNotEqualTo("id",configGroupItem.getId());
        Integer count=argsDefineDao.selectCountByExample(addExample);
        if(count>0){
            throw new BusinessException("字段key【"+configGroupItem.getFieldKey()+"】不能重复");
        }
        RobotArgsDefine robotArgsDefine = new RobotArgsDefine();
        BeanUtils.copyProperties(configGroupItem,robotArgsDefine);
        RobotArgsDefine selectByPrimaryKey = argsDefineDao.selectByPrimaryKey(robotArgsDefine.getId());
        //更新robotApp
        robotAppDao.updateStatus(selectByPrimaryKey.getArgsCode());
        if (StringUtils.isBlank(robotArgsDefine.getDropDownOption())
                && !"password".equals(robotArgsDefine.getFieldType()) && isPasswordKey(robotArgsDefine.getFieldKey())) {
            robotArgsDefine.setFieldType("password");
        }
        int i = argsDefineDao.updateByPrimaryKeySelective(robotArgsDefine);

        String newVal = String.format("%s类型字段：%s-%s，%s%s", getFieldTypeName(configGroupItem.getFieldType()),
                configGroupItem.getFieldName(),configGroupItem.getFieldKey(),
                configGroupItem.getRequired()==0?"必填":"非必填",
                StringUtils.isNotBlank(configGroupItem.getDropDownOption())?String.format(",下拉选项为：%s", configGroupItem.getDropDownOption()):"");

        String oldVal = String.format("%s类型字段：%s-%s，%s%s", getFieldTypeName(selectByPrimaryKey.getFieldType()),
                selectByPrimaryKey.getFieldName(),selectByPrimaryKey.getFieldKey(),
                selectByPrimaryKey.getRequired()==0?"必填":"非必填",
                StringUtils.isNotBlank(selectByPrimaryKey.getDropDownOption())?String.format(",下拉选项为：%s", selectByPrimaryKey.getDropDownOption()):"");

        if (!newVal.equals(oldVal)) {
            saveAddrBusinessProcess(configGroupItem.getAppCode(), String.format("修改为%s", newVal),String.format("原为%s", oldVal));
        }
        return i;
    }

    @Override
    public Integer editGroupItemOrder(RobotAppConfigGroupItemVO configGroupItem) {
        RobotArgsDefine robotArgsDefine = argsDefineDao.selectByPrimaryKey(configGroupItem.getId());
        if (robotArgsDefine == null) {
            throw new BusinessException("未找到需要调整顺序的配置项信息，请核查！");
        }

        Example addExample = new Example(RobotArgsDefine.class);
        addExample.createCriteria()
                .andEqualTo("isDelete",0).andEqualTo("argsCode",robotArgsDefine.getArgsCode()).andEqualTo("formGroupId",
                robotArgsDefine.getFormGroupId());
        List<RobotArgsDefine> robotArgsDefines = argsDefineDao.selectByExample(addExample);

        Optional<RobotArgsDefine> first = robotArgsDefines.stream().filter(it -> it.getDisplayOrder() == null).findFirst();
        if (first.isPresent()) {
            robotArgsDefines.sort(Comparator.comparing(RobotArgsDefine::getId));
            for (int i = 1; i<=robotArgsDefines.size(); i++) {
                robotArgsDefines.get(i-1).setDisplayOrder(i);
            }
        } else {
            robotArgsDefines.sort(Comparator.comparing(RobotArgsDefine::getDisplayOrder).thenComparing(RobotArgsDefine::getId));
        }
        Integer displayOrder = configGroupItem.getDisplayOrder();
        int order = 1;
        for (int i=1; i<=robotArgsDefines.size(); i++) {
            RobotArgsDefine item = robotArgsDefines.get(i-1);
            if (item.getId().equals(configGroupItem.getId())) {
                item.setDisplayOrder(displayOrder);
                argsDefineDao.updateByPrimaryKeySelective(item);
            } else {
                if (order == displayOrder) {
                    order++;
                }
                item.setDisplayOrder(order);
                argsDefineDao.updateByPrimaryKeySelective(item);
                order++;
            }
        }
        return 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer changeForm(RobotAppConfigGroupVO groupVO,Integer formId) {
        Example example = new Example(RobotAppFormGroup.class);
        example.createCriteria()
                .andEqualTo("isDelete",0)
                .andEqualTo("appCode",groupVO.getAppCode())
                .andEqualTo("formId",groupVO.getFormId())
                .andEqualTo("groupId",groupVO.getGroupId());
        RobotAppFormGroup formGroup = formGroupDao.selectOneByExample(example);
        if (ObjectUtils.isEmpty(formGroup)){
            throw new BusinessException("分组不存在，请检查。");
        }
        Example example2 = new Example(RobotAppFormGroup.class);
        example2.createCriteria()
                .andEqualTo("isDelete",0)
                .andEqualTo("appCode",groupVO.getAppCode())
                .andEqualTo("formId",formId)
                .andEqualTo("groupId",groupVO.getGroupId());
        RobotAppFormGroup formGroup1 = formGroupDao.selectOneByExample(example2);
        if (ObjectUtils.isNotEmpty(formGroup1)){
            RobotAppConfigForm robotAppConfigForm = configFormDao.selectByPrimaryKey(formId);
            RobotAppConfigGroup group = robotAppConfigGroupDao.selectByPrimaryKey(groupVO.getGroupId());
            throw new BusinessException(robotAppConfigForm.getFormName()+"下已存在"+group.getGroupName());
        }

        //将要换到此应用此表单下所有数据的id
        example2.clear();
        example2.createCriteria().andEqualTo("appCode",groupVO.getAppCode())
                .andEqualTo("formId",formId)
                .andEqualTo("isDelete",0);
        List<RobotAppFormGroup> robotAppFormGroups = formGroupDao.selectByExample(example2);
        if (CollectionUtils.isNotEmpty(robotAppFormGroups)){
            List<Integer> collect = robotAppFormGroups.stream().map(RobotAppFormGroup::getId)
                    .collect(Collectors.toList());
            //查出该表单下的所有组的所有字段
            Example example5 = new Example(RobotArgsDefine.class);
            example5.createCriteria()
                    .andEqualTo("scope","app")
                    .andEqualTo("isDelete",0)
                    .andIn("formGroupId",collect);
            List<RobotArgsDefine> robotArgsDefines = argsDefineDao.selectByExample(example5);
            List<String> fieldKeys = robotArgsDefines.stream().map(RobotArgsDefine::getFieldKey)
                    .collect(Collectors.toList());
            //如果要换的那个组下的fieldKey有和fieldKeys重复的，则抛异常
            example5.clear();
            example5.createCriteria()
                    .andEqualTo("scope","app")
                    .andEqualTo("formGroupId",formGroup.getId())
                    .andEqualTo("isDelete",0);
            List<RobotArgsDefine> argsDefines = argsDefineDao.selectByExample(example5);
            List<String> list = argsDefines.stream().map(RobotArgsDefine::getFieldKey)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(fieldKeys) && CollectionUtils.isNotEmpty(list)){
                for (String s : list) {
                    if (fieldKeys.stream().anyMatch( fieldKey -> fieldKey.equals(s))){
                        throw new BusinessException("fieldKey："+s+"重复，请检查。");
                    }
                }
            }
        }


        RobotAppFormGroup robotAppFormGroup = new RobotAppFormGroup();
        robotAppFormGroup.setId(formGroup.getId());
        robotAppFormGroup.setIsDelete(1);
        formGroupDao.updateByPrimaryKeySelective(robotAppFormGroup);
        RobotAppFormGroup appFormGroup = new RobotAppFormGroup();
        appFormGroup.setFormId(formId);
        appFormGroup.setGroupId(formGroup.getGroupId());
        appFormGroup.setAppCode(groupVO.getAppCode());
        appFormGroup.setIsDelete(0);
        formGroupDao.addFormGroupWithIdentity(appFormGroup);
        Example example1 = new Example(RobotArgsDefine.class);
        example1.createCriteria()
                .andEqualTo("scope","app")
                .andEqualTo("isDelete",0)
                .andEqualTo("formGroupId",formGroup.getId());
        RobotArgsDefine robotArgsDefine = new RobotArgsDefine();
        robotArgsDefine.setFormGroupId(appFormGroup.getId());
        //更新robotApp
        robotAppDao.updateStatus(groupVO.getAppCode());
        return argsDefineDao.updateByExampleSelective(robotArgsDefine,example1);
    }

    /**
     * 添加分组明细相关校验
     * @param configGroupItem
     * @return
     */
    private void addGroupItemCheck(RobotArgsDefine configGroupItem) throws BusinessException{
        try {
            if (null == configGroupItem){
                return;
            }
            Integer groupId = configGroupItem.getFormGroupId();
            String columnName = configGroupItem.getFieldName();
            String columnKey = configGroupItem.getFieldKey();

            //同一分组下，有效的字段名称不能重复
            RobotArgsDefine item = new RobotArgsDefine();
            item.setFieldName(columnName);
            item.setFormGroupId(groupId);
            List<RobotArgsDefine> groupItemInfo = getGroupItemInfo(item);
            if (null != groupItemInfo && groupItemInfo.size() >0){
                throw new BusinessException("存在同一分组下,字段名称重复");
            }

            //同一表单下，字段名称，字段key不能重复；否则确定保存时，该字段下红色字体提示：字段已存在，请勿重复设置

            //1.通过groupid定位表单
            RobotAppConfigGroup group = new RobotAppConfigGroup();
            group.setId(groupId);
            List<RobotAppConfigGroup> groupBaseInfo = robotAppConfigGroupDao.getGroupBaseInfo(group);


        } catch (Exception e) {
            log.error("addGroupItemCheck()->添加分组明细相关校验不通过",e);
            throw new BusinessException(e.getMessage());
        }

    }

    @Override
    public Integer checkGroupItem(RobotArgsDefine robotArgsDefine) {

        return robotAppConfigGroupDao.checkGroupItem(robotArgsDefine);
    }

    @Override
    @Transactional
    public Integer addCondition(RobotAppConfigConditionVO2 conditionList) throws BusinessException{
        List<RobotAppConfigCondition> conditions = conditionList.getConfigConditions();
        for (int i = 0; i < conditions.size(); i++) {
            for (int j = i+1; j < conditions.size(); j++) {
                if (conditions.get(i).getConditionLeft().equals(conditions.get(j).getConditionLeft())
                && conditions.get(i).getConditionRight().equals(conditions.get(j).getConditionRight())){
                    throw new BusinessException(conditions.get(j).getConditionLeft()+"的条件值重复，请检查");
                }
            }
        }
        Example example = new Example(RobotAppConfigCondition.class);
        example.createCriteria().andEqualTo("argsDefineId",conditionList.getArgsDefineId());
        conditionDao.deleteByExample(example);

        conditions.forEach(s -> {
            s.setArgsDefineId(conditionList.getArgsDefineId());
            s.setRelation(conditionList.getRelation());
        });
        conditionDao.insertList(conditions);
        StringBuffer stringBuffer = new StringBuffer();
        for (RobotAppConfigCondition condition : conditions) {
            stringBuffer.append(condition.getConditionLeft()).append(condition.getLogicalOperator())
                    .append(condition.getConditionRight()).append(conditionList.getRelation());
        }
        String s = stringBuffer.toString();
        int length = conditionList.getRelation().length();
        String condition = s.substring(0, s.length() - length);
        String type = conditionList.getShowOrHidden() == 0 ? "字段显示" : "字段隐藏";
        String s1 = condition + "，" + type;
        RobotArgsDefine robotArgsDefine = new RobotArgsDefine();
        robotArgsDefine.setId(conditionList.getArgsDefineId());
        robotArgsDefine.setCond(s1);
        robotArgsDefine.setShowOrHidden(conditionList.getShowOrHidden());
        //更新robotApp
        RobotArgsDefine selectByPrimaryKey = argsDefineDao.selectByPrimaryKey(conditionList.getArgsDefineId());
        robotAppDao.updateStatus(selectByPrimaryKey.getArgsCode());
        int i = argsDefineDao.updateByPrimaryKeySelective(robotArgsDefine);

        saveAddrBusinessProcess(selectByPrimaryKey.getArgsCode(),
                String.format("修改%s类型字段%s-%s条件，条件内容为：%s",
                        getFieldTypeName(selectByPrimaryKey.getFieldType()),
                        selectByPrimaryKey.getFieldName(),
                        selectByPrimaryKey.getFieldKey(),
                        s1),
                String.format("%s类型字段%s-%s原来条件为：%s",
                        getFieldTypeName(selectByPrimaryKey.getFieldType()),
                        selectByPrimaryKey.getFieldName(),
                        selectByPrimaryKey.getFieldKey(),
                        StringUtils.isNotBlank(selectByPrimaryKey.getCond())?selectByPrimaryKey.getCond():"无"));

        return i;
    }

    /*private void addConditionCheck(List<RobotAppConfigCondition> conditionList) throws BusinessException{
        //同一个条件字段，允许添加多条，但匹配的目标值不能相同。否则保存时下方红字提示出现错误：[某条件字段]的条件值重复，请检查

        for (int i = 0; i < conditionList.size(); i++) {
            String conditionLeft1 = conditionList.get(i).getConditionLeft();
            String conditionRight1 = conditionList.get(i).getConditionRight().trim();
            //条件值长度不能超过100
            if (conditionRight1.toCharArray().length>100){
                throw new BusinessException("条件值长度不能超过100");
            }

            StringBuilder builder1 = new StringBuilder();
            builder1.append(conditionLeft1).append(conditionRight1);
            if (i+1 == conditionList.size()){
                continue;
            }
            for (int j = i+1; j <conditionList.size(); j++) {
                String conditionLeft2 = conditionList.get(j).getConditionLeft();
                String conditionRight2 = conditionList.get(j).getConditionRight().trim();
                StringBuilder builder2 = new StringBuilder();
                builder2.append(conditionLeft2).append(conditionRight2);
                if (builder1.toString().equals(builder2.toString())){
                    throw new BusinessException(conditionLeft1+"和"+conditionLeft2+"条件值重复");
                }
            }
        }

        //字段间是否存在互为条件。存在则提示错误：[xx字段]与[yy字段]互为联动条件，不允许保存
        Set<String> set = Sets.newHashSet();
        String column = conditionList.();
        for (RobotAppConfigCondition condition : conditionList) {

            if (StringUtils.isNotBlank(column)){
                set.add(column);
            }
        }
        if (null !=set && set.size()>0){
            Integer groupItemId = conditionList.get(0).getArgsDefineId();
            //查同分组下所有分组明细所有条件下的所有字段
            RobotArgsDefine groupItem = new RobotArgsDefine();
            groupItem.setId(groupItemId);
            List<RobotArgsDefine> groupItemInfo = robotAppConfigGroupDao.getGroupItemInfo(groupItem);
            Integer groupId = groupItemInfo.get(0).getFormGroupId();
            String columnName = groupItemInfo.get(0).getFieldName();
            List<String> columnList = robotAppConfigGroupDao.getAllConditionColumnByGroupId(groupId);
            //拿本次添加去重后的字段匹配以上所有字段
            if (null != columnList && columnList.size()>0){
                for (String column : columnList) {
                    if (StringUtils.isBlank(column)){
                        continue;
                    }

                    for(String newCol : set){
                        if (StringUtils.isBlank(newCol)){
                            continue;
                        }

                        if (column.equals(newCol)){
                            throw new BusinessException(columnName+"与"+column+"互为联动条件，不允许保存");
                        }
                    }
                }
            }
        }
    }*/

    @Override
    @Transactional
    public void copyArgs(RobotCopyArgsVo robotCopyArgsVo){
        String appCode=robotCopyArgsVo.getAppCode();
        List<RobotApp> robotApps=robotAppDao.selectAll();
        String appCodeStr=null;
        for(RobotApp robotApp: robotApps){
            JSONObject appArgs=JSONObject.parseObject(robotApp.getAppArgs());
            String addrIds=appArgs.getString("addrId");
            String businessTypes=appArgs.getString("businessType");
            if(robotCopyArgsVo.getAddrId().equals(addrIds)&&robotCopyArgsVo.getBusinessType().equals(businessTypes)){
                appCodeStr=robotApp.getAppCode();
                break;
            }
        }
        Example argExample=new Example(RobotArgsDefine.class);
        argExample.createCriteria().andEqualTo("isDelete",0).andEqualTo("argsCode",appCodeStr);
        List<RobotArgsDefine> robotArgsDefines=argsDefineDao.selectByExample(argExample);
        if(CollectionUtils.isEmpty(robotArgsDefines)){
            String appStr=robotCopyArgsVo.getAddrName()+robotCopyArgsVo.getBusinessName();
            throw new BusinessException("{"+appStr+"}不存在参数信息，请检查");
        }
        //先删除原来的参数信息
        Example deleteArgsExample=new Example(RobotArgsDefine.class);
        deleteArgsExample.createCriteria().andEqualTo("argsCode",appCode).andEqualTo("isDelete", 0);
        argsDefineDao.deleteByExample(deleteArgsExample);

        //删除中间表信息
        Example deleteFormGroupExample=new Example(RobotAppFormGroup.class);
        deleteFormGroupExample.createCriteria().andEqualTo("appCode",appCode).andEqualTo("isDelete", 0);
        formGroupDao.deleteByExample(deleteFormGroupExample);
        //插入中间表及参数表
        Example zjExample=new Example(RobotAppFormGroup.class);
        zjExample.createCriteria().andEqualTo("appCode",appCodeStr).andEqualTo("isDelete",0);
        zjExample.setOrderByClause("id asc");
        List<RobotAppFormGroup> robotAppFormGroups=formGroupDao.selectByExample(zjExample);
        for(RobotAppFormGroup group: robotAppFormGroups){
            RobotAppFormGroup robotAppFormGroup=new RobotAppFormGroup();
            robotAppFormGroup.setFormId(group.getFormId());
            robotAppFormGroup.setGroupId(group.getGroupId());
            robotAppFormGroup.setIsDelete(0);
            robotAppFormGroup.setAppCode(appCode);
            formGroupDao.insert(robotAppFormGroup);
            Example csExample=new Example(RobotArgsDefine.class);
            csExample.createCriteria().andEqualTo("formGroupId",group.getId()).andEqualTo("isDelete",0);
            csExample.setOrderByClause("form_group_id asc");
            List<RobotArgsDefine> robotArgsDefinesList=argsDefineDao.selectByExample(csExample);
            for(RobotArgsDefine args:robotArgsDefinesList){
                RobotArgsDefine robotArgsDefine=new RobotArgsDefine();
                BeanUtils.copyProperties(args,robotArgsDefine);
                robotArgsDefine.setId(null);
                robotArgsDefine.setArgsCode(appCode);
                robotArgsDefine.setFormGroupId(robotAppFormGroup.getId());
                argsDefineDao.insert(robotArgsDefine);
            }
        }
        robotAppDao.updateStatus(robotCopyArgsVo.getAppCode());
    }

    @Override
    public List<RobotAppFormVO> getGeneralArgs(Integer businessType){
        List<RobotAppFormVO> robotAppFormVOList=new ArrayList<>();
        if(businessType==1){
            robotAppFormVOList.add(setSocialAccountInfo());
            robotAppFormVOList.add(setSocialLoginInfo());
        }else{
            robotAppFormVOList.add(setAccfundAccountInfo());
            robotAppFormVOList.add(setAccfundLoginInfo());
        }
        return robotAppFormVOList;
    }

    @Override
    public void addGeneralArgs(List<RobotAppFormVO> robotCopyArgsVo,String appCode){
        //删除中间表
        Example zjExample=new Example(RobotAppFormGroup.class);
        zjExample.createCriteria().andEqualTo("appCode",appCode).andEqualTo("isDelete", 0);
        formGroupDao.deleteByExample(zjExample);
        //删除参数表
        Example deleteArgsExample=new Example(RobotArgsDefine.class);
        deleteArgsExample.createCriteria().andEqualTo("argsCode",appCode).andEqualTo("isDelete", 0);
        argsDefineDao.deleteByExample(deleteArgsExample);
        for(RobotAppFormVO formVO:robotCopyArgsVo){
            for(RobotAppGroupVO groupVO:formVO.getRobotAppGroupVOS()){
                RobotAppFormGroup robotAppFormGroup=new RobotAppFormGroup();
                robotAppFormGroup.setGroupId(groupVO.getId());
                robotAppFormGroup.setAppCode(appCode);
                robotAppFormGroup.setFormId(formVO.getId());
                robotAppFormGroup.setIsDelete(0);
                formGroupDao.insert(robotAppFormGroup);
                for(RobotArgsDefineVO robotArgsDefineVO:groupVO.getArgsDefineVOS()){
                    robotArgsDefineVO.setIsDelete(0);
                    robotArgsDefineVO.setArgsCode(appCode);
                    robotArgsDefineVO.setFormGroupId(robotAppFormGroup.getId());
                    argsDefineDao.insert(robotArgsDefineVO);
                }
            }
        }
    }

    @Override
    public List<RobotArgsDefine> getFiledNameList(String keyWord){
        Example example=new Example(RobotArgsDefine.class);
        example.createCriteria().andEqualTo("isDelete", 0).andLike("fieldName", "%".concat(keyWord).concat("%"));
        List<RobotArgsDefine> robotArgsDefines=argsDefineDao.selectByExample(example);
        return robotArgsDefines;
    }

    /**
     * 组装社保申报账户信息表单
     * @return RobotAppFormVO
     */
    public RobotAppFormVO setSocialAccountInfo(){
        String[]groupStr={"申报账户信息","经办人信息"};
        List<RobotArgsDefineVO> robotArgsDefineVOList=new ArrayList<>();
        List<RobotArgsDefineVO> robotArgsDefineHandleList=new ArrayList<>();
        //申报账号信息
        RobotArgsDefineVO argsAccount=new RobotArgsDefineVO();
        argsAccount.setScope("app");
        argsAccount.setFieldKey("companyName");
        argsAccount.setFieldName("单位名称");
        argsAccount.setFieldType("text");
        argsAccount.setDisplayOrder(1);
        argsAccount.setGroupName("申报账户信息");
        argsAccount.setRequired(1);
        robotArgsDefineVOList.add(argsAccount);
        RobotArgsDefineVO argsSocial=new RobotArgsDefineVO();
        argsSocial.setScope("app");
        argsSocial.setFieldKey("socialNumber");
        argsSocial.setFieldName("单位社保号");
        argsSocial.setFieldType("text");
        argsSocial.setDisplayOrder(2);
        argsSocial.setGroupName("申报账户信息");
        argsSocial.setRequired(1);
        robotArgsDefineVOList.add(argsSocial);
        RobotArgsDefineVO argsNs=new RobotArgsDefineVO();
        argsNs.setScope("app");
        argsNs.setFieldKey("companyNumber");
        argsNs.setFieldName("纳税人识别号");
        argsNs.setFieldType("text");
        argsNs.setDisplayOrder(3);
        argsNs.setGroupName("申报账户信息");
        argsNs.setRequired(1);
        robotArgsDefineVOList.add(argsNs);

        //经办人信息
        RobotArgsDefineVO argsHandle=new RobotArgsDefineVO();
        argsHandle.setScope("app");
        argsHandle.setFieldKey("mobile");
        argsHandle.setFieldName("经办人手机号");
        argsHandle.setFieldType("text");
        argsHandle.setDisplayOrder(3);
        argsHandle.setGroupName("经办人信息");
        argsHandle.setRequired(1);
        robotArgsDefineHandleList.add(argsHandle);

        RobotArgsDefineVO argsHandleEmail=new RobotArgsDefineVO();
        argsHandleEmail.setScope("app");
        argsHandleEmail.setFieldKey("email");
        argsHandleEmail.setFieldName("经办人邮箱");
        argsHandleEmail.setFieldType("text");
        argsHandleEmail.setDisplayOrder(2);
        argsHandleEmail.setGroupName("经办人信息");
        argsHandleEmail.setRequired(1);
        robotArgsDefineHandleList.add(argsHandleEmail);
        RobotAppFormVO formVo=getFromData(Arrays.asList(groupStr),"申报账户信息",robotArgsDefineVOList,robotArgsDefineHandleList);
        return formVo;


    }

    /**
     * 组装公积金申报账户信息表单
     * @return RobotAppFormVO
     */
    public RobotAppFormVO setAccfundAccountInfo(){
        String[]groupStr={"申报账户信息"};
        List<RobotArgsDefineVO> robotArgsDefineVOList=new ArrayList<>();
        //申报账号信息
        RobotArgsDefineVO argsAccount=new RobotArgsDefineVO();
        argsAccount.setScope("app");
        argsAccount.setFieldKey("companyName");
        argsAccount.setFieldName("单位名称");
        argsAccount.setFieldType("text");
        argsAccount.setDisplayOrder(1);
        argsAccount.setGroupName("申报账户信息");
        argsAccount.setRequired(1);
        robotArgsDefineVOList.add(argsAccount);
        RobotArgsDefineVO argsSocial=new RobotArgsDefineVO();
        argsSocial.setScope("app");
        argsSocial.setFieldKey("accfundNumber");
        argsSocial.setFieldName("单位公积金号");
        argsSocial.setFieldType("text");
        argsSocial.setDisplayOrder(2);
        argsSocial.setGroupName("申报账户信息");
        argsSocial.setRequired(1);
        robotArgsDefineVOList.add(argsSocial);
        RobotArgsDefineVO argsNs=new RobotArgsDefineVO();
        argsNs.setScope("app");
        argsNs.setFieldKey("companyNumber");
        argsNs.setFieldName("纳税人识别号");
        argsNs.setFieldType("text");
        argsNs.setDisplayOrder(3);
        argsNs.setGroupName("申报账户信息");
        argsNs.setRequired(1);
        robotArgsDefineVOList.add(argsNs);
        RobotAppFormVO formVo=getFromData(Arrays.asList(groupStr),"申报账户信息",robotArgsDefineVOList,null);
        return formVo;


    }
    /**
     * 组装社保登录配置表单
     * @return RobotAppFormVO
     */
    public RobotAppFormVO setSocialLoginInfo(){
        String[]groupStr={"登录信息"};
        List<RobotArgsDefineVO> robotArgsDefineVOList=new ArrayList<>();
        RobotArgsDefineVO argsAccount=new RobotArgsDefineVO();
        argsAccount.setScope("app");
        argsAccount.setFieldKey("username");
        argsAccount.setFieldName("登录账号");
        argsAccount.setFieldType("text");
        argsAccount.setDisplayOrder(1);
        argsAccount.setGroupName("登录信息");
        argsAccount.setRequired(1);
        robotArgsDefineVOList.add(argsAccount);
        RobotArgsDefineVO argsSocial=new RobotArgsDefineVO();
        argsSocial.setScope("app");
        argsSocial.setFieldKey("password");
        argsSocial.setFieldName("登录密码");
        argsSocial.setFieldType("text");
        argsSocial.setDisplayOrder(2);
        argsSocial.setGroupName("登录信息");
        argsSocial.setRequired(1);
        robotArgsDefineVOList.add(argsSocial);
        RobotAppFormVO formVO=getFromData(Arrays.asList(groupStr),"登录配置",robotArgsDefineVOList,null);
        return formVO;
    }

    /**
     * 组装公积金登录配置表单
     * @return RobotAppFormVO
     */
    public RobotAppFormVO setAccfundLoginInfo(){
        String[]groupStr={"登录信息"};
        List<RobotArgsDefineVO> robotArgsDefineVOList=new ArrayList<>();
        RobotArgsDefineVO argsAccount=new RobotArgsDefineVO();
        argsAccount.setScope("app");
        argsAccount.setFieldKey("password");
        argsAccount.setFieldName("CA证书密码");
        argsAccount.setFieldType("text");
        argsAccount.setDisplayOrder(1);
        argsAccount.setGroupName("登录信息");
        argsAccount.setRequired(1);
        robotArgsDefineVOList.add(argsAccount);
        RobotArgsDefineVO argsSocial=new RobotArgsDefineVO();
        argsSocial.setScope("app");
        argsSocial.setFieldKey("usbPort");
        argsSocial.setFieldName("USB端口号");
        argsSocial.setFieldType("singleDropList");
        argsSocial.setDisplayOrder(2);
        argsSocial.setGroupName("登录信息");
        argsSocial.setDropDownOption("1,2,3,4,5,6,7,8");
        argsSocial.setRequired(1);
        robotArgsDefineVOList.add(argsSocial);
        RobotAppFormVO formVO=getFromData(Arrays.asList(groupStr),"登录配置",robotArgsDefineVOList,null);
        return formVO;
    }

    public RobotAppFormVO getFromData(List<String> groupNameList,String formName,List<RobotArgsDefineVO> argsDefineVOS,List<RobotArgsDefineVO>robotArgsDefineHandleList ){
        RobotAppFormVO formVO=new RobotAppFormVO();
        List<RobotAppGroupVO> robotAppGroupVOS=new ArrayList<>();
        //查询分组信息
        Example groupExample=new Example(RobotAppConfigGroup.class);
        groupExample.createCriteria().andIn("groupName",groupNameList).andEqualTo("robotType", "申报机器人");
        groupExample.setOrderByClause("group_name asc");
        List<RobotAppConfigGroup> robotAppConfigList=robotAppConfigGroupDao.selectByExample(groupExample);
        for(RobotAppConfigGroup group:robotAppConfigList){
            RobotAppGroupVO vo=new RobotAppGroupVO();
            BeanUtils.copyProperties(group, vo);
            robotAppGroupVOS.add(vo);
        }
        if(robotAppGroupVOS.size()>1){
            robotAppGroupVOS.get(0).setArgsDefineVOS(argsDefineVOS);
            robotAppGroupVOS.get(1).setArgsDefineVOS(robotArgsDefineHandleList);
        }else{
            robotAppGroupVOS.get(0).setArgsDefineVOS(argsDefineVOS);
        }
        formVO.setRobotAppGroupVOS(robotAppGroupVOS);
        //查询表单信息
        Example formExample=new Example(RobotAppConfigForm.class);
        formExample.createCriteria().andEqualTo("formName",formName).andEqualTo("robotType", "申报机器人");
        RobotAppConfigForm robotAppConfigForm=configFormDao.selectOneByExample(formExample);
        BeanUtils.copyProperties(robotAppConfigForm, formVO);
        return formVO;
    }
}
