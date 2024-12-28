package com.seebon.rpa.service.impl;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.seebon.common.utils.DateUtils;
import com.seebon.common.utils.JsonUtils;
import com.seebon.common.utils.UUIDGenerator;
import com.seebon.rpa.BusinessException;
import com.seebon.rpa.auth.SecurityContext;
import com.seebon.rpa.auth.Session;
import com.seebon.rpa.entity.IgGridDefaultPage;
import com.seebon.rpa.entity.agent.enums.BusinessTypeEnum;
import com.seebon.rpa.entity.auth.po.SysUser;
import com.seebon.rpa.entity.robot.*;
import com.seebon.rpa.entity.robot.dto.DeclareAccountBaseDTO;
import com.seebon.rpa.entity.robot.dto.RobotAppDTO;
import com.seebon.rpa.entity.robot.dto.design.*;
import com.seebon.rpa.entity.robot.dto.history.RobotAppHistory;
import com.seebon.rpa.entity.robot.enums.*;
import com.seebon.rpa.entity.robot.vo.RobotAppAddVO;
import com.seebon.rpa.entity.robot.vo.RobotAppVO;
import com.seebon.rpa.entity.robot.vo.RobotClientVO;
import com.seebon.rpa.entity.robot.vo.RobotFlowStatusHistoryVO;
import com.seebon.rpa.entity.saas.DevUserAddr;
import com.seebon.rpa.entity.saas.dto.AddrBusinessChangeProcessDTO;
import com.seebon.rpa.entity.saas.po.declare.PolicyAddr;
import com.seebon.rpa.entity.system.User;
import com.seebon.rpa.remote.RpaAuthService;
import com.seebon.rpa.remote.RpaDesignProdService;
import com.seebon.rpa.remote.RpaSaasService;
import com.seebon.rpa.repository.mongodb.RobotAppHistoryRepository;
import com.seebon.rpa.repository.mongodb.RobotFlowDesignRepository;
import com.seebon.rpa.repository.mysql.*;
import com.seebon.rpa.service.CustomerCommandService;
import com.seebon.rpa.service.RobotAppService;
import com.seebon.rpa.utils.ConvertUtl;
import com.seebon.rpa.utils.RespUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletResponse;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RobotAppServiceImpl implements RobotAppService {
    private final String key = "ROBOT_APP_COUNT";
    HashMap<Integer, Integer > appStatusMap  = new HashMap<Integer, Integer>(){{
        put(RobotAppTypeEnum.SURVEY.getCode(),RobotStageEnum.CONFIGURATION.getCode());
        put(RobotAppTypeEnum.CONFIGURATION.getCode(),RobotStageEnum.CONFIGURATION.getCode());
        put(RobotAppTypeEnum.TEST.getCode(),RobotStageEnum.CONFIGURATION.getCode());
        put(RobotAppTypeEnum.WAIT.getCode(),RobotStageEnum.TEST.getCode());
        put(RobotAppTypeEnum.CHECK.getCode(),RobotStageEnum.TEST.getCode());
        put(RobotAppTypeEnum.NORMAL.getCode(),RobotStageEnum.OPERATION.getCode());
        put(RobotAppTypeEnum.REPAIR.getCode(),RobotStageEnum.OPERATION.getCode());
    }};
    @Autowired
    private RobotAppDao robotAppDao;
    @Autowired
    private RobotArgsDefineDao robotArgsDefineDao;
    @Autowired
    private RobotAppConfigConditionDao configConditionDao;
    @Autowired
    private RobotAppConfigFormDao configFormDao;
    @Autowired
    private RobotAppConfigGroupDao configGroupDao;
    @Autowired
    private RobotAppFormGroupDao formGroupDao;
    @Autowired
    private RobotFlowDao robotFlowDao;
    @Autowired
    private RobotFlowStepDao robotFlowStepDao;
    @Autowired
    private RobotAppHistoryRepository appHistoryRepository;
    @Autowired
    private RobotDao robotDao;
    @Autowired
    private RobotClientDao robotClientDao;
    @Autowired
    private RobotAppScheduleDao scheduleDao;
    @Autowired
    private RobotTaskScheduleDao taskScheduleDao;
    @Autowired
    private RobotGeneralPlanDao generalPlanDao;
    @Autowired
    private RobotTaskDao robotTaskDao;
    @Autowired
    private RobotTaskArgsDao robotTaskArgsDao;
    @Autowired
    private RpaSaasService rpaSaasService;
    @Autowired
    private RobotClientAppDao robotClientAppDao;
    @Autowired
    private RobotDataDictDao robotDataDictDao;
    @Autowired
    private RobotExecutionDetailDao robotExecutionDetailDao;
    @Autowired
    private RobotClientUsbDao clientUsbDao;
    @Autowired
    private RobotAppEnvDao robotAppEnvDao;
    @Autowired
    private RobotFlowStepFlowDao flowStepFlowDao;
    @Autowired
    private RobotFlowTemplateDao flowTemplateDao;
    @Autowired
    private RobotFlowTemplateStepDao flowTemplateStepDao;
    @Autowired
    private RobotFlowStepArgsDao flowStepArgsDao;
    @Autowired
    private RobotFlowDesignRepository flowDesignRepository;
    @Autowired
    private RpaDesignProdService rpaDesignProdService;
    @Autowired
    private RpaAuthService rpaAuthService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RobotClientTaskDao clientTaskDao;
    @Autowired
    private RobotFlowStatusHistoryDao statusHistoryDao;
    @Autowired
    private RobotServiceItemDao robotServiceItemDao;
    @Autowired
    private RobotAppExplainDao robotAppExplainDao;
    @Autowired
    private CustomerCommandService customerCommandService;

    @Override
    public IgGridDefaultPage<RobotAppVO> listPage(Map<String, Object> map) {
        if (!map.containsKey("isPage")) {
            throw new BusinessException("非查询参数");
        }
        //分页查询流程设计信息
        Object keyword = map.get("keyword");
        if (ObjectUtils.isNotEmpty(keyword)) {
            if ("已发布".equals(keyword)) {
                map.put("status", 1);
            }
            if ("未发布".equals(keyword)) {
                map.put("status", 0);
            }
            if ("历史版本".equals(keyword)) {
                map.put("status", 2);
            }
        }
        Integer start = (Integer) map.get("start");
        Integer size = (Integer) map.get("size");
        PageHelper.offsetPage(start, size);
        Page<RobotAppVO> page = (Page<RobotAppVO>) robotAppDao.selectRobotApp(map);
        return new IgGridDefaultPage<RobotAppVO>(page.getResult(), (int) page.getTotal());
    }

    @Override
    public int add(String addrName, String businessType) {
        Example example = new Example(RobotApp.class);
        example.createCriteria().andEqualTo("appName", addrName + businessType);
        List<RobotApp> robotApps = robotAppDao.selectByExample(example);
        //存在则提示
        if (robotApps.size() > 0) {
            throw new BusinessException("已存在相同条件的记录");
        }
        RobotApp robotApp = new RobotApp();
        robotApp.setAppCode(UUIDGenerator.uuidStringWithoutLine());//appCode
        robotApp.setVersion("v1.0.1");//版本号
        robotApp.setAppName(addrName + businessType);
        robotApp.setStatus(1);//状态为未发布
        return robotAppDao.insertSelective(robotApp);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int syncInRobotAppInfos(RobotAppHistory robotAppInfos) {
        String appCode = robotAppInfos.getAppCode();
        List<RobotFlow> flowList = robotAppInfos.getFlowList();
        List<RobotFlowStep> flowStepList = robotAppInfos.getFlowStepList();
        //如果app应用下没有任何流程，或app下的流程没有任何步骤，不允许同步。
        if (CollectionUtils.isEmpty(flowList) || CollectionUtils.isEmpty(flowStepList)) {
            throw new BusinessException("此APP下没有任何流程步骤，同步失败");
        }
        //如果app应用下没有进行信息配置，不允许同步
        List<RobotAppFormGroup> robotAppFormGroups = robotAppInfos.getFormGroupList();
        if (CollectionUtils.isEmpty(robotAppFormGroups)) {
            throw new BusinessException("此APP下没有进行信息配置，同步失败");
        }
        //如果app应用下没有进行信息配置，不允许同步
        List<RobotArgsDefine> robotArgsDefines = robotAppInfos.getAppArgsList();
        if (CollectionUtils.isEmpty(robotArgsDefines)) {
            throw new BusinessException("此APP下没有进行信息配置，同步失败");
        }
        Example example = new Example(RobotApp.class);
        example.createCriteria().andEqualTo("appCode", appCode);
        RobotApp robotApp = robotAppDao.selectOneByExample(example);
        if (robotApp == null) { //新建的流程应用
            RobotApp robotCreate = new RobotApp();
            BeanUtils.copyProperties(robotAppInfos, robotCreate);
            robotCreate.setId(null);
            robotCreate.setCreateTime(new Date());
            robotCreate.setStatus(0);
            if (StringUtils.isBlank(robotCreate.getVersion())) {
                robotCreate.setVersion("V0.0.1");
                robotCreate.setComment("V0.0.1");
            }
            robotCreate.setReleaseTime(null);
            robotAppDao.insert(robotCreate);
        } else {
            robotApp.setStatus(0);
            robotApp.setComment(robotAppInfos.getComment());
            robotApp.setReleaseTime(null);
            robotApp.setUpdateTime(new Date());
            if (StringUtils.isBlank(robotApp.getVersion())) {
                robotApp.setVersion("V0.0.1");
                robotApp.setComment("V0.0.1");
            }
            robotAppDao.updateByPrimaryKeySelective(robotApp);

            //删除旧的机器人参数定义和应用配置分组条件
            Example exampleArgs = new Example(RobotArgsDefine.class);
            exampleArgs.createCriteria().andEqualTo("argsCode", appCode).andEqualTo("scope", "app");
            List<RobotArgsDefine> appArgsList = robotArgsDefineDao.selectByExample(exampleArgs);
            if (CollectionUtils.isNotEmpty(appArgsList)) {
                List<Integer> ids = appArgsList.stream().map(it -> it.getId()).collect(Collectors.toList());
                Example example1 = new Example(RobotAppConfigCondition.class);
                example1.createCriteria().andIn("argsDefineId", ids);
                configConditionDao.deleteByExample(example1);
                robotArgsDefineDao.deleteByExample(exampleArgs);
            }

            //删除旧的表单与组中间表
            Example formGroupExam = new Example(RobotAppFormGroup.class);
            formGroupExam.createCriteria().andEqualTo("appCode", appCode);
            formGroupDao.deleteByExample(formGroupExam);

            //删除旧流程
            Example e2 = new Example(RobotFlow.class);
            e2.createCriteria().andEqualTo("appCode", appCode);
            List<RobotFlow> robotFlows = robotFlowDao.selectByExample(e2);
            robotFlowDao.deleteByExample(e2);

            //删除旧流程步骤
            List<String> oldFlowCodes = robotFlows.stream().map(it -> it.getFlowCode()).collect(Collectors.toList());
            Example e3 = new Example(RobotFlowStep.class);
            e3.createCriteria().andIn("flowCode", oldFlowCodes);
            robotFlowStepDao.deleteByExample(e3);
        }

        List<RobotAppConfigCondition> configConditions = robotAppInfos.getConfigConditions();
        robotAppFormGroups.stream().forEach(it -> {
            List<RobotArgsDefine> subArgsDefines = robotArgsDefines.stream().filter(item ->
                    item.getFormGroupId().equals(it.getId())).collect(Collectors.toList());
            it.setId(null);
            formGroupDao.insert(it);
            subArgsDefines.stream().forEach(robotArgsDefine -> {
                List<RobotAppConfigCondition> filters = configConditions.stream().filter(item1 ->
                        item1.getArgsDefineId().equals(robotArgsDefine.getId())).collect(Collectors.toList());
                robotArgsDefine.setFormGroupId(it.getId());
                robotArgsDefine.setId(null);
                robotArgsDefineDao.insert(robotArgsDefine);
                if (CollectionUtils.isNotEmpty(filters)) {
                    filters.stream().forEach(item1 -> {
                        item1.setArgsDefineId(robotArgsDefine.getId());
                    });
                    configConditionDao.insertList(filters);
                }
            });
        });
        flowList.stream().forEach(it -> {
            it.setId(null);
            it.setCreateTime(new Date());
        });
        robotFlowDao.insertList(flowList);
        flowStepList.stream().forEach(it -> {
            it.setId(null);
            it.setCreateTime(new Date());
        });
        robotFlowStepDao.insertList(flowStepList);

        //机器人流程模板关系
        List<RobotFlowTemplate> flowTemplates = robotAppInfos.getFlowTemplate();
        if (CollectionUtils.isNotEmpty(flowTemplates)) {
            for (RobotFlowTemplate step : flowTemplates) {
                Example flowTemplateExample = new Example(RobotFlowTemplate.class);
                flowTemplateExample.createCriteria().andEqualTo("flowCode", step.getFlowCode());
                flowTemplateDao.deleteByExample(flowTemplateExample);
                step.setUpdateTime(new Date());
            }
            flowTemplateDao.insertList(flowTemplates);
        }
        //机器人流程模板步骤关系
        List<RobotFlowTemplateStep> flowTemplateSteps = robotAppInfos.getFlowTemplateStep();
        if (CollectionUtils.isNotEmpty(flowTemplateSteps)) {
            for (RobotFlowTemplateStep step : flowTemplateSteps) {
                flowTemplateStepDao.deleteByPrimaryKey(step.getId());
                step.setUpdateTime(new Date());
            }
            flowTemplateStepDao.insertList(flowTemplateSteps);
        }
        //机器人流程模板步骤关系
        List<RobotFlowStepArgs> flowStepArgs = robotAppInfos.getFlowStepArgs();
        if (CollectionUtils.isNotEmpty(flowStepArgs)) {
            for (RobotFlowStepArgs step : flowStepArgs) {
//                flowStepArgsDao.deleteByPrimaryKey(step.getId());
                flowStepArgsDao.deleteByFlowCode(step.getFlowCode());
                step.setUpdateTime(new Date());
            }
            flowStepArgsDao.insertList(flowStepArgs);
        }
        //机器人流程步骤流程
        List<RobotFlowStepFlow> flowStepFlows = robotAppInfos.getFlowStepFlow();
        if (CollectionUtils.isNotEmpty(flowStepFlows)) {
            for (RobotFlowStepFlow step : flowStepFlows) {
                flowStepFlowDao.deleteByPrimaryKey(step.getId());
                step.setUpdateTime(new Date());
            }
            flowStepFlowDao.insertList(flowStepFlows);
        }
        //流程设计
        List<RobotFlowDesign> designList = robotAppInfos.getDesignList();
        if (CollectionUtils.isNotEmpty(designList)) {
            flowDesignRepository.batchSave(designList);
        }
        return 1;
    }

    @Override
    public int syncOut(String appCode, String comment) {
        Session session = SecurityContext.currentUser();
        //状态（1 已发布，0 未发布 , 2 历史版本）
        //根据appCode查询具体选定发布的流程
        Example example = new Example(RobotApp.class);
        example.createCriteria().andEqualTo("appCode", appCode);
        RobotApp robotApp = robotAppDao.selectOneByExample(example);
        //查询flow
        Example exampleFlow = new Example(RobotFlow.class);
        exampleFlow.createCriteria().andEqualTo("appCode", appCode);
        List<RobotFlow> flowLists = robotFlowDao.selectByExample(exampleFlow);
        //查询step
        List<RobotFlowStep> robotFlowSteps = robotFlowStepDao.flowStepList(appCode);
        //如果app应用下没有任何流程，或app下的流程没有任何步骤，不允许发布。
        if (CollectionUtils.isEmpty(flowLists) || CollectionUtils.isEmpty(robotFlowSteps)) {
            throw new BusinessException("此APP下没有任何流程步骤，不能同步");
        }
        //流程编码
        List<String> flowCodes = flowLists.stream().map(RobotFlow::getFlowCode).collect(Collectors.toList());

        //如果app应用下没有进行信息配置，不允许发布
        Example formGroupExample = new Example(RobotAppFormGroup.class);
        formGroupExample.createCriteria().andEqualTo("appCode", appCode);
        List<RobotAppFormGroup> formGroupList = formGroupDao.selectByExample(formGroupExample);
        if (CollectionUtils.isEmpty(formGroupList)) {
            throw new BusinessException("此APP下没有进行信息配置，不能同步");
        }
        List<Integer> formGroupIds = formGroupList.stream().map(RobotAppFormGroup::getId).collect(Collectors.toList());
        Example argsExam = new Example(RobotArgsDefine.class);
        argsExam.createCriteria().andIn("formGroupId", formGroupIds).andEqualTo("isDelete", 0);
        List<RobotArgsDefine> robotArgsDefines = robotArgsDefineDao.selectByExample(argsExam);
        if (CollectionUtils.isEmpty(robotArgsDefines)) {
            throw new BusinessException("此APP下没有进行信息配置，不能同步");
        }
        //根据app查询操作指令参数
        Example exampleArgs = new Example(RobotArgsDefine.class);
        exampleArgs.createCriteria().andEqualTo("argsCode", appCode).andEqualTo("scope", "app");
        List<RobotArgsDefine> appArgsList = robotArgsDefineDao.selectByExample(exampleArgs);
        //根据appCode查询信息配置表单与分组中间表
        Example formGroupExam = new Example(RobotAppFormGroup.class);
        formGroupExam.createCriteria().andEqualTo("appCode", appCode);
        List<RobotAppFormGroup> robotAppFormGroups = formGroupDao.selectByExample(formGroupExam);
        //查询表单表
        List<RobotAppConfigForm> robotAppConfigForms = configFormDao.selectAll();
        //查询分组表
        List<RobotAppConfigGroup> robotAppConfigGroups = configGroupDao.selectAll();
        //数据存入历史表
        RobotAppHistory robotAppHistory = new RobotAppHistory();
        BeanUtils.copyProperties(robotApp, robotAppHistory);
        robotAppHistory.setComment(comment);
        robotAppHistory.setAppArgsList(appArgsList);
        robotAppHistory.setFlowList(flowLists);
        robotAppHistory.setFlowStepList(robotFlowSteps);
        robotAppHistory.setCreateId(session.getCustId());
        robotAppHistory.setClientId(session.getCustId());
        //查询信息配置条件表
        List<Integer> argsIds = appArgsList.stream().map(RobotArgsDefine::getId).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(argsIds)) {
            Example conditionExam = new Example(RobotAppConfigCondition.class);
            conditionExam.createCriteria().andIn("argsDefineId", argsIds);
            List<RobotAppConfigCondition> robotAppConfigConditions = configConditionDao.selectByExample(conditionExam);
            robotAppHistory.setConfigConditions(robotAppConfigConditions);
        }
        robotAppHistory.setConfigForms(robotAppConfigForms);
        robotAppHistory.setConfigGroups(robotAppConfigGroups);
        robotAppHistory.setFormGroupList(robotAppFormGroups);
        //机器人流程模板关系
        List<RobotFlowTemplate> flowTemplates = flowTemplateDao.selectByAppCode(appCode);
        if (CollectionUtils.isNotEmpty(flowTemplates)) {
            robotAppHistory.setFlowTemplate(flowTemplates);
        }
        //机器人流程模板步骤关系
        List<RobotFlowTemplateStep> flowTemplateSteps = flowTemplateStepDao.selectByAppCode(appCode);
        if (CollectionUtils.isNotEmpty(flowTemplateSteps)) {
            robotAppHistory.setFlowTemplateStep(flowTemplateSteps);
        }
        //机器人流程模板步骤关系
        List<RobotFlowStepArgs> flowStepArgs = flowStepArgsDao.selectByAppCode(appCode);
        if (CollectionUtils.isNotEmpty(flowStepArgs)) {
            robotAppHistory.setFlowStepArgs(flowStepArgs);
        }
        //机器人流程步骤流程
        List<RobotFlowStepFlow> flowStepFlows = this.getStepFlowList(flowCodes);
        if (CollectionUtils.isNotEmpty(flowStepFlows)) {
            robotAppHistory.setFlowStepFlow(flowStepFlows);
        }
        //流程设计
        List<RobotFlowDesign> designList = flowDesignRepository.listByFlowCodes(flowCodes);
        if (CollectionUtils.isNotEmpty(designList)) {
            robotAppHistory.setDesignList(designList);
        }
        return rpaDesignProdService.syncIn(robotAppHistory);
    }

    @Override
    public IgGridDefaultPage<RobotAppDesignVO> getAppData(Map<String,Object> map){
        Integer page =(Integer)map.get("page");
        Integer size =(Integer)map.get("size");
        Integer start=(Integer)map.get("start");
        String keyWord=(String)map.get("keyWord");
        List<Integer> businessList=(List)map.get("businessList");
        List<Integer> onlineList=(List)map.get("onlineList");
        List<PolicyAddr> policyAddrList=rpaSaasService.getPopularAddr();
        Map<String, String> addressMap = policyAddrList.stream().collect(Collectors.toMap(PolicyAddr::getAddrName, PolicyAddr::getProvinceName));
        List<RobotAppDesignVO> appList=robotAppDao.getAppList(map);
        DevUserAddr devUserAddr = new DevUserAddr();
        List<DevUserAddr> userAddrs = rpaSaasService.listUserAddr(devUserAddr);
        Map<String,DevUserAddr> userAddrMap=userAddrs.stream().collect(Collectors.toMap(k->k.getBusinessType()+"-"+k.getAddrName(),k->k));
        List<RobotServiceItem> robotServiceItems=robotServiceItemDao.selectAll();
        Map<String,List<RobotServiceItem>> robotServiceMap=robotServiceItems.stream().collect(Collectors.groupingBy(RobotServiceItem::getAppCode));
        appList.forEach(e->{
            List<String> serviceItemList=new ArrayList<>();
            JSONObject appArgs = JSONObject.parseObject(e.getAppArgs());
            String addrId=appArgs.getString("addrId");
            String addrName=appArgs.getString("addrName");
            Integer businessType= (appArgs.getString("businessType") == null || appArgs.getString("businessType").equals("1001001"))?1:2;
            e.setAddrId(addrId);
            e.setAddrName(addrName);
            e.setBusinessType(businessType);
            e.setProvince(addressMap.get(addrName));
            String businessName=businessType==1?"社保":"公积金";
            DevUserAddr userAddr=userAddrMap.get(businessName+"-"+addrName);
            if(userAddr!=null){
                e.setXqUserName(userAddr.getXqUserName());
                e.setDevUserName(userAddr.getDevUserName());
                e.setTestUserName(userAddr.getTestUserName());
                e.setYwUserName(userAddr.getYwUserName());
            }
            e.setOnlineStatus(appStatusMap.get(e.getAppStatus()));
            if(e.getRunStatus()==null){
                e.setRunStatus(2);
            }
            if(StringUtils.isNotBlank(e.getServiceItem())){
                if(e.getServiceItem().contains(",")){
                    String [] str=e.getServiceItem().split(",");
                    e.setServiceItemList(Arrays.asList(str));
                }else{
                    serviceItemList.add(e.getServiceItem());
                    e.setServiceItemList(serviceItemList);
                }
            }
            List<RobotServiceItem> robotServiceItemList=robotServiceMap.get(e.getAppCode());
            if(CollectionUtils.isEmpty(robotServiceItemList)){
                Integer [] serviceItems={1,2,3,5,6,7,8,9};
                List<Integer> serviceList=Arrays.asList(serviceItems);
                List<RobotFlow> robotFlows=robotFlowDao.getServiceItemList(e.getAppCode());
                List<Integer> serviceInt = robotFlows.stream().map(RobotFlow::getServiceItem).collect(Collectors.toList());
                List<Integer> serviceData=serviceList.stream().filter(s->!serviceInt.contains(s)).collect(Collectors.toList());
                List<RobotServiceItem> items=new ArrayList<>();
                robotFlows.forEach(t->{
                    RobotServiceItem robotServiceItem=new RobotServiceItem();
                    robotServiceItem.setAppCode(t.getAppCode());
                    robotServiceItem.setServiceItem(t.getServiceItem());
                    if(e.getStatus()!=0){
                        robotServiceItem.setServiceStatus(1);
                    }else{
                        robotServiceItem.setServiceStatus(2);
                    }
                    items.add(robotServiceItem);
                });
                serviceData.forEach(t->{
                    RobotServiceItem robotServiceItem=new RobotServiceItem();
                    robotServiceItem.setAppCode(e.getAppCode());
                    robotServiceItem.setServiceItem(t);
                    robotServiceItem.setServiceRemark(null);
                    robotServiceItem.setServiceStatus(2);
                    items.add(robotServiceItem);
                });
                e.setServiceVOList(items);
            }else{
                e.setServiceVOList(robotServiceItemList);
            }
        });
        if(CollectionUtils.isNotEmpty(businessList)){
            appList=appList.stream().filter(e->businessList.contains(e.getBusinessType())).collect(Collectors.toList());
        }
        if(CollectionUtils.isNotEmpty(onlineList)){
            appList=appList.stream().filter(e->onlineList.contains(e.getRunStatus())).collect(Collectors.toList());
        }
        if(StringUtils.isNotBlank(keyWord)){
            appList=appList.stream().filter(e->(StringUtils.isNotBlank(e.getXqUserName())&&e.getXqUserName().contains(keyWord))||(StringUtils.isNotBlank(e.getTestUserName())&&e.getTestUserName().contains(keyWord))||(StringUtils.isNotBlank(e.getDevUserName())&&e.getDevUserName().contains(keyWord))||(StringUtils.isNotBlank(e.getYwUserName())&&e.getYwUserName().contains(keyWord))||(StringUtils.isNotBlank(e.getProvince())&&e.getProvince().contains(keyWord))||(StringUtils.isNotBlank(e.getAddrName())&&e.getAddrName().contains(keyWord))||(StringUtils.isNotBlank(e.getAppName())&&e.getAppName().contains(keyWord))).collect(Collectors.toList());
        }
        int count=page*size;
        if(count>appList.size()){
            count=appList.size();
        }
        List<RobotAppDesignVO> pageList=appList.subList(start,count);
        int dataCount=appList.size();
        if(dataCount!=appList.size()){
            dataCount=appList.size();
        }
        return new IgGridDefaultPage<RobotAppDesignVO>(pageList,dataCount);
    }

    @Override
    public RobotCountVO getAppCount(){
        RobotCountVO statusCount=getStatusCount();
        RobotCountVO robotCountVO=robotAppDao.getAppCount();
        robotCountVO.setOnlineCount(statusCount.getOnlineCount());
        robotCountVO.setOfflineCount(statusCount.getOfflineCount());
        robotCountVO.setLaunchedCount(statusCount.getLaunchedCount());
        robotCountVO.setCityCount(statusCount.getOnlineCount()+statusCount.getOfflineCount()+statusCount.getLaunchedCount());
        if(ObjectUtil.isNotNull(redisTemplate.opsForValue().get(key))){
            String value=redisTemplate.opsForValue().get(key).toString();
            RobotCountVO countVo=JSON.parseObject(value,RobotCountVO.class);
            log.info("获取前一天缓存的数据{}",countVo);
            robotCountVO.setOnlineIndex(robotCountVO.getOnlineCount()-countVo.getOnlineCount());
            robotCountVO.setOfflineIndex(robotCountVO.getOfflineCount()-countVo.getOfflineCount());
            robotCountVO.setLaunchedIndex(robotCountVO.getLaunchedCount()-countVo.getLaunchedCount());
        }else{
            log.info("redis暂未缓存前一天数据");
        }
        return robotCountVO;
    }

    public RobotCountVO getStatusCount(){
        RobotCountVO robotCountVO=new RobotCountVO();
        //上线数
        Example example=new Example(RobotApp.class);
        example.createCriteria().andEqualTo("runStatus",1);
        List<RobotApp> robotApps=robotAppDao.selectByExample(example);
        Set set=dateChange(robotApps);
        robotCountVO.setOnlineCount(set.size());
        //下线数
        Example offlineExample=new Example(RobotApp.class);
        offlineExample.createCriteria().andEqualTo("runStatus",0);
        List<RobotApp> offlineRobotApps=robotAppDao.selectByExample(offlineExample);
        Set offlineSet=dateChange(offlineRobotApps);
        robotCountVO.setOfflineCount(offlineSet.size());
        //待上线数
        List<RobotAppDesignVO> robotAppList=robotAppDao.getWaitOnline();
        robotAppList.forEach(e->{
            JSONObject appArgs = JSONObject.parseObject(e.getAppArgs());
            String addrId=appArgs.getString("addrId");
            e.setAddrId(addrId);
        });
        List<RobotAppDesignVO> uniqueList=robotAppList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(()->new TreeSet<>(Comparator.comparing(RobotAppDesignVO::getAddrId))),ArrayList::new));
        List<RobotAppDesignVO> robotAppDesignVOS=robotAppDao.getOnlineOrOffline();
        robotAppDesignVOS.forEach(e->{
            JSONObject appArgs = JSONObject.parseObject(e.getAppArgs());
            String addrId=appArgs.getString("addrId");
            e.setAddrId(addrId);
        });
        List<String> addrList  = robotAppDesignVOS.stream().map(o -> o.getAddrId()).collect(Collectors.toList());
        List<RobotAppDesignVO> data = uniqueList.stream().filter(a ->!addrList.contains(a.getAddrId())).collect(Collectors.toList());
        robotCountVO.setLaunchedCount(data.size());
        return  robotCountVO;
    }

    @Override
    @Transactional
    public void offlineStatus(String appCode,String comment,Integer appStatus){
        Session session = SecurityContext.currentUser();
        Example example=new Example(RobotTask.class);
        example.createCriteria().andEqualTo("appCode", appCode);
        List<RobotTask> robotTasks=robotTaskDao.selectByExample(example);
        robotTasks.forEach(e->{
            clientTaskDao.cleanSyncTime(e.getTaskCode());
        });
        RobotTask robotTask = new RobotTask();
        robotTask.setAppCode(appCode);
        robotTask.setStatus(0);
        robotTask.setComment(comment);
        robotTask.setEditName(session.getName());
        robotTask.setUpdateTime(new Date());
        robotTaskDao.updateTaskData(robotTask);

        Example example1 = new Example(RobotApp.class);
        example1.createCriteria().andEqualTo("appCode", appCode);
        RobotApp robotApp = robotAppDao.selectOneByExample(example1);

        //更新应用表
        robotAppDao.updateOfflineStatus(appStatus,comment,appCode);
        //插入历史操作表
        RobotFlowStatusHistory history = new RobotFlowStatusHistory();
        history.setAppCode(appCode);
        history.setActionName("下架");
        history.setOnLive(OnLiveEnum.offline.getStatus());
        history.setAppStatus(appStatus);
        history.setType(1);
        history.setCreateId((int) session.getId());
        history.setCreateName(session.getName());
        history.setCreateTime(new Date());
        history.setRemark(comment);
        statusHistoryDao.insertSelective(history);

        String originalValue = String.format("状态：%s，版本：%s", robotApp.getRunStatus() != null && robotApp.getRunStatus() == 1 ? "上线" : "下线", robotApp.getVersion());
        String newValue = String.format("状态：下线，版本：%s", robotApp.getVersion());
        // 保存地区业务变更记录
        saveAddrBusinessProcess(robotApp,"调整RPA流程", comment, originalValue, newValue, comment);
    }

    @Override
    public void updateAppStatus(String appCode,Integer appStatus,String remark){
        Session session = SecurityContext.currentUser();
        //更新应用表
        Example example=new Example(RobotApp.class);
        example.createCriteria().andEqualTo("appCode", appCode);

        RobotApp robotApp1 = robotAppDao.selectOneByExample(example);

        RobotApp robotApp=new RobotApp();
        robotApp.setAppStatus(appStatus);
        robotApp.setRemark(remark);
        robotAppDao.updateByExampleSelective(robotApp,example);
        //插入历史操作表
        RobotFlowStatusHistory history = new RobotFlowStatusHistory();
        history.setAppCode(appCode);
        history.setActionName("调整状态");
        history.setAppStatus(appStatus);
        history.setType(1);
        history.setRemark(remark);
        history.setCreateId((int) session.getId());
        history.setCreateName(session.getName());
        history.setCreateTime(new Date());
        statusHistoryDao.insertSelective(history);


        // 插入地区业务变更记录
        String originalValue = String.format("应用状态：%s，版本：%s", getAppStatusText(robotApp1.getAppStatus()), robotApp1.getVersion());
        String newValue = String.format("应用状态：%s，版本：%s", getAppStatusText(robotApp.getAppStatus()), robotApp1.getVersion());

        // 保存地区业务变更记录
        saveAddrBusinessProcess(robotApp1, "调整应用状态", remark, originalValue, newValue, remark);
    }

    private String getAppStatusText(Integer appStatus) {
        List<String> appStatusList = Lists.newArrayList("调研", "配置", "调试通过", "等待数据", "验证有效", "正常运行", "修复问题");
        if (appStatus!=null && appStatus < appStatusList.size()) {
            return appStatusList.get(appStatus);
        }
        return "";
    }

    @Override
    public List<RobotFlowStatusHistoryVO> selectAppHistoryStatus(String appCode){
        Example example=new Example(RobotFlowStatusHistory.class);
        example.orderBy("id").desc();
        example.createCriteria().andEqualTo("appCode",appCode).andEqualTo("type",1);
        List<RobotFlowStatusHistory> list=statusHistoryDao.selectByExample(example);
        if(CollectionUtils.isEmpty(list)){
            return Lists.newArrayList();
        }
        return list.stream().map(vo->{
            RobotFlowStatusHistoryVO historyVO=new RobotFlowStatusHistoryVO();
            BeanUtils.copyProperties(vo,historyVO);
            historyVO.setAppStatusName(RobotAppTypeEnum.getNameByCode(vo.getAppStatus()));
            historyVO.setOnLiveName(OnLiveEnum.getNameByCode(vo.getOnLive()));
            return historyVO;
        }).collect(Collectors.toList());
    }

    @Override
    public void downExcel(Map<String,Object> map,HttpServletResponse response){
        map.put("page", 1);
        map.put("size", 10000);
        IgGridDefaultPage<RobotAppDesignVO> appData=this.getAppData(map);
        List<RobotAppDesignVO> designVOList=appData.getRows();
        List<RobotAppExcelDTO> excelVos = designVOList.stream().map(e -> {
            RobotAppExcelDTO robotAppExcelDTO = new RobotAppExcelDTO();
            BeanUtils.copyProperties(e, robotAppExcelDTO);
            if(e.getRunStatus()!=null){
                robotAppExcelDTO.setRunStatusStr(AppLiveEnum.getNameByCode(e.getRunStatus()));
            }
            if(e.getOnlineStatus()!=null){
                robotAppExcelDTO.setOnlineStatusStr(RobotStageEnum.getNameByCode(e.getOnlineStatus()));
            }
            if(e.getAppStatus()!=null){
                robotAppExcelDTO.setAppStatusStr(RobotAppTypeEnum.getNameByCode(e.getAppStatus()));
            }
            robotAppExcelDTO.setAppTypeStr(e.getRobotName());
            List<RobotServiceItem> serviceVOList=e.getServiceVOList();
            for(RobotServiceItem robotServiceItem: serviceVOList){
                //增员
                if(robotServiceItem.getServiceItem().equals(ServiceItemTypeEnum.ADD.getCode())){
                    if(robotServiceItem.getServiceStatus()==1){
                        robotAppExcelDTO.setAddStr("√");
                    }
                    if(robotServiceItem.getServiceStatus()==0){
                        if(StringUtils.isNotBlank(robotServiceItem.getServiceRemark())){
                            robotAppExcelDTO.setAddStr("不实现"+":"+robotServiceItem.getServiceRemark());
                        }else{
                            robotAppExcelDTO.setAddStr("不实现");
                        }
                    }
                    if(robotServiceItem.getServiceStatus()==2){
                        if(StringUtils.isNotBlank(robotServiceItem.getServiceRemark())){
                            robotAppExcelDTO.setAddStr("未实现"+":"+robotServiceItem.getServiceRemark());
                        }else{
                            robotAppExcelDTO.setAddStr("未实现");
                        }
                    }
                }
                //补缴
                if(robotServiceItem.getServiceItem().equals(ServiceItemTypeEnum.SUPPLEMENT.getCode())){
                    if(robotServiceItem.getServiceStatus()==1){
                        robotAppExcelDTO.setBjStr("√");
                    }
                    if(robotServiceItem.getServiceStatus()==0){
                        if(StringUtils.isNotBlank(robotServiceItem.getServiceRemark())){
                            robotAppExcelDTO.setBjStr("不实现"+":"+robotServiceItem.getServiceRemark());
                        }else{
                            robotAppExcelDTO.setBjStr("不实现");
                        }
                    }
                    if(robotServiceItem.getServiceStatus()==2){
                        if(StringUtils.isNotBlank(robotServiceItem.getServiceRemark())){
                            robotAppExcelDTO.setBjStr("未实现"+":"+robotServiceItem.getServiceRemark());
                        }else{
                            robotAppExcelDTO.setBjStr("未实现");
                        }
                    }
                }
                //减员
                if(robotServiceItem.getServiceItem().equals(ServiceItemTypeEnum.REDUCE.getCode())){
                    if(robotServiceItem.getServiceStatus()==1){
                        robotAppExcelDTO.setReduceStr("√");
                    }
                    if(robotServiceItem.getServiceStatus()==0){
                        if(StringUtils.isNotBlank(robotServiceItem.getServiceRemark())){
                            robotAppExcelDTO.setReduceStr("不实现"+":"+robotServiceItem.getServiceRemark());
                        }else{
                            robotAppExcelDTO.setReduceStr("不实现");
                        }
                    }
                    if(robotServiceItem.getServiceStatus()==2){
                        if(StringUtils.isNotBlank(robotServiceItem.getServiceRemark())){
                            robotAppExcelDTO.setReduceStr("未实现"+":"+robotServiceItem.getServiceRemark());
                        }else{
                            robotAppExcelDTO.setReduceStr("未实现");
                        }
                    }
                }
                //调基
                if(robotServiceItem.getServiceItem().equals(ServiceItemTypeEnum.TRANSFER.getCode())){
                    if(robotServiceItem.getServiceStatus()==1){
                        robotAppExcelDTO.setTjStr("√");
                    }
                    if(robotServiceItem.getServiceStatus()==0){
                        if(StringUtils.isNotBlank(robotServiceItem.getServiceRemark())){
                            robotAppExcelDTO.setTjStr("不实现"+":"+robotServiceItem.getServiceRemark());
                        }else{
                            robotAppExcelDTO.setTjStr("不实现");
                        }
                    }
                    if(robotServiceItem.getServiceStatus()==2){
                        if(StringUtils.isNotBlank(robotServiceItem.getServiceRemark())){
                            robotAppExcelDTO.setTjStr("未实现"+":"+robotServiceItem.getServiceRemark());
                        }else{
                            robotAppExcelDTO.setTjStr("未实现");
                        }
                    }
                }
                //缴费
                if(robotServiceItem.getServiceItem().equals(ServiceItemTypeEnum.PAY.getCode())){
                    if(robotServiceItem.getServiceStatus()==1){
                        robotAppExcelDTO.setJfStr("√");
                    }
                    if(robotServiceItem.getServiceStatus()==0){
                        if(StringUtils.isNotBlank(robotServiceItem.getServiceRemark())){
                            robotAppExcelDTO.setJfStr("不实现"+":"+robotServiceItem.getServiceRemark());
                        }else{
                            robotAppExcelDTO.setJfStr("不实现");
                        }
                    }
                    if(robotServiceItem.getServiceStatus()==2){
                        if(StringUtils.isNotBlank(robotServiceItem.getServiceRemark())){
                            robotAppExcelDTO.setJfStr("未实现"+":"+robotServiceItem.getServiceRemark());
                        }else{
                            robotAppExcelDTO.setJfStr("未实现");
                        }
                    }
                }
                //在册
                if(robotServiceItem.getServiceItem().equals(ServiceItemTypeEnum.REGISTER.getCode())){
                    if(robotServiceItem.getServiceStatus()==1){
                        robotAppExcelDTO.setZcStr("√");
                    }
                    if(robotServiceItem.getServiceStatus()==0){
                        if(StringUtils.isNotBlank(robotServiceItem.getServiceRemark())){
                            robotAppExcelDTO.setZcStr("不实现"+":"+robotServiceItem.getServiceRemark());
                        }else{
                            robotAppExcelDTO.setZcStr("不实现");
                        }
                    }
                    if(robotServiceItem.getServiceStatus()==2){
                        if(StringUtils.isNotBlank(robotServiceItem.getServiceRemark())){
                            robotAppExcelDTO.setZcStr("未实现"+":"+robotServiceItem.getServiceRemark());
                        }else{
                            robotAppExcelDTO.setZcStr("未实现");
                        }
                    }
                }
                //费用明细
                if(robotServiceItem.getServiceItem().equals(ServiceItemTypeEnum.COST.getCode())){
                    if(robotServiceItem.getServiceStatus()==1){
                        robotAppExcelDTO.setFyStr("√");
                    }
                    if(robotServiceItem.getServiceStatus()==0){
                        if(StringUtils.isNotBlank(robotServiceItem.getServiceRemark())){
                            robotAppExcelDTO.setFyStr("不实现"+":"+robotServiceItem.getServiceRemark());
                        }else{
                            robotAppExcelDTO.setFyStr("不实现");
                        }
                    }
                    if(robotServiceItem.getServiceStatus()==2){
                        if(StringUtils.isNotBlank(robotServiceItem.getServiceRemark())){
                            robotAppExcelDTO.setFyStr("未实现"+":"+robotServiceItem.getServiceRemark());
                        }else{
                            robotAppExcelDTO.setFyStr("未实现");
                        }
                    }
                }
                //政策通知
                if(robotServiceItem.getServiceItem().equals(ServiceItemTypeEnum.POLICY_NOTICE.getCode())){
                    if(robotServiceItem.getServiceStatus()==1){
                        robotAppExcelDTO.setFyStr("√");
                    }
                    if(robotServiceItem.getServiceStatus()==0){
                        if(StringUtils.isNotBlank(robotServiceItem.getServiceRemark())){
                            robotAppExcelDTO.setFyStr("不实现"+":"+robotServiceItem.getServiceRemark());
                        }else{
                            robotAppExcelDTO.setFyStr("不实现");
                        }
                    }
                    if(robotServiceItem.getServiceStatus()==2){
                        if(StringUtils.isNotBlank(robotServiceItem.getServiceRemark())){
                            robotAppExcelDTO.setFyStr("未实现"+":"+robotServiceItem.getServiceRemark());
                        }else{
                            robotAppExcelDTO.setFyStr("未实现");
                        }
                    }
                }
            }
            robotAppExcelDTO.setStatusStr(ReleaseTypeEnum.getNameByCode(e.getStatus()));
            return robotAppExcelDTO;
        }).collect(Collectors.toList());
        RespUtils.write(response, "rpa应用设计导出", RobotAppExcelDTO.class, excelVos);

    }

    @Override
    public void updateApp(RobotAppAddVO robotAppAddVO){
        List<RobotServiceItem> serviceItemList =robotAppAddVO.getRobotServiceItemList();
        HashMap<Integer,String> dataMap=serviceItemList.stream().collect(HashMap::new,(map,k)->map.put(k.getServiceItem(),k.getServiceRemark()),HashMap::putAll);
        if ("declare".equals(robotAppAddVO.getRobotName())) {
            Example example = new Example(RobotApp.class);
            example.createCriteria().andEqualTo("appName", robotAppAddVO.getAppName()).andNotEqualTo("id", robotAppAddVO.getId());
            List<RobotApp> robotApps = robotAppDao.selectByExample(example);
            if (CollectionUtils.isNotEmpty(robotApps)) {
                throw new BusinessException("该地区已存在相同业务类型，不允许重复添加");
            }
        }

        Example itemExample=new Example(RobotServiceItem.class);
        itemExample.createCriteria().andEqualTo("appCode",robotAppAddVO.getAppCode());
        List<RobotServiceItem> robotServiceItems=robotServiceItemDao.selectByExample(itemExample);
        if(CollectionUtils.isNotEmpty(robotServiceItems)){
            robotServiceItemDao.deleteByExample(itemExample);
            List<RobotServiceItem> robotServiceItemList=robotAppAddVO.getRobotServiceItemList();
            robotServiceItemList.forEach(e->{
                e.setAppCode(robotAppAddVO.getAppCode());
            });
            robotServiceItemDao.insertList(robotServiceItemList);
        }else{
            Integer [] serviceItems={1,2,3,5,6,7,8};
            List<Integer> serviceList=Arrays.asList(serviceItems);
            List<RobotFlow> robotFlows=robotFlowDao.getServiceItemList(robotAppAddVO.getAppCode());
            List<Integer> serviceInt = robotFlows.stream().map(RobotFlow::getServiceItem).collect(Collectors.toList());
            List<Integer> serviceData=serviceList.stream().filter(s->!serviceInt.contains(s)).collect(Collectors.toList());
            robotFlows.forEach(e->{
                RobotServiceItem robotServiceItem=new RobotServiceItem();
                robotServiceItem.setAppCode(e.getAppCode());
                robotServiceItem.setServiceItem(e.getServiceItem());
                if(e.getStatus()!=null&&e.getStatus()!=0){
                    robotServiceItem.setServiceStatus(1);
                }else{
                    robotServiceItem.setServiceStatus(2);
                }
                robotServiceItem.setServiceRemark(dataMap.get(robotServiceItem.getServiceItem()));
                robotServiceItemDao.insertSelective(robotServiceItem);
            });
            serviceData.forEach(e->{
                RobotServiceItem robotServiceItem=new RobotServiceItem();
                robotServiceItem.setAppCode(robotAppAddVO.getAppCode());
                robotServiceItem.setServiceItem(e);
                robotServiceItem.setServiceRemark(dataMap.get(robotServiceItem.getServiceItem()));
                robotServiceItem.setServiceStatus(2);
                robotServiceItemDao.insertSelective(robotServiceItem);
            });


        }
        robotAppDao.updateApp(robotAppAddVO);

    }


    public Set dateChange(List<RobotApp> list){
        Set set=new HashSet();
        list.forEach(e->{
            JSONObject appArgs = JSONObject.parseObject(e.getAppArgs());
            //只统计社保的应用
            if(appArgs!=null){
                Integer addrId=appArgs.getInteger("addrId");
                set.add(addrId);
            }
        });
        return set;
    }

    private void saveAppInfo(RobotApp robotApp) {

        String appCode = robotApp.getAppCode();

        //查询flow
        Example exampleFlow = new Example(RobotFlow.class);
        exampleFlow.createCriteria().andEqualTo("appCode", appCode);
        List<RobotFlow> flowLists = robotFlowDao.selectByExample(exampleFlow);

        //查询step
        List<RobotFlowStep> robotFlowSteps = robotFlowStepDao.flowStepList(appCode);
        if (CollectionUtils.isEmpty(flowLists) || CollectionUtils.isEmpty(robotFlowSteps)) {
            throw new BusinessException("此APP下没有任何流程步骤，不能发布");
        }

        //查询信息配置
        Example formGroupExample = new Example(RobotAppFormGroup.class);
        formGroupExample.createCriteria().andEqualTo("appCode", appCode);
        List<RobotAppFormGroup> formGroupList = formGroupDao.selectByExample(formGroupExample);

        //查询应用执行计划
        List<String> flowCodes = flowLists.stream().map(f -> f.getFlowCode()).collect(Collectors.toList());
        Example scheduleExam = new Example(RobotAppSchedule.class);
        scheduleExam.createCriteria().andIn("flowCode", flowCodes);
        List<RobotAppSchedule> schedules = scheduleDao.selectByExample(scheduleExam);

        //查询任务执行计划
        Example taskScheduleExam = new Example(RobotTaskSchedule.class);
        taskScheduleExam.createCriteria().andIn("flowCode", flowCodes);
        List<RobotTaskSchedule> taskSchedules = taskScheduleDao.selectByExample(taskScheduleExam);

        //查询通用计划
        Example generalExam = new Example(RobotGeneralPlan.class);
        generalExam.createCriteria().andEqualTo("appCode", appCode);
        List<RobotGeneralPlan> generalPlans = generalPlanDao.selectByExample(generalExam);

        //查询信息配置参数
        Example exampleArgs = new Example(RobotArgsDefine.class);
        exampleArgs.createCriteria().andEqualTo("argsCode", appCode).andEqualTo("scope", "app").andEqualTo("isDelete", 0);
        List<RobotArgsDefine> appArgsList = robotArgsDefineDao.selectByExample(exampleArgs);


        //数据存入历史表
        RobotAppHistory robotAppHistory = new RobotAppHistory();
        BeanUtils.copyProperties(robotApp, robotAppHistory);
        User user = SecurityContext.currentUser();
        robotAppHistory.setReleaseTime(new Date());//更新发布时间
        robotAppHistory.setStatus(2);
        robotAppHistory.setVersion(robotApp.getVersion().concat("-bak"));
        robotAppHistory.setCreateId((int) user.getId());
        robotAppHistory.setClientId(user.getCustId());
        robotAppHistory.setComment("引用历史版本，备份现有的应用信息");
        robotAppHistory.setChangeReason("引用历史版本，备份现有的应用信息");
        robotAppHistory.setAppArgsList(appArgsList);
        robotAppHistory.setFlowList(flowLists);
        robotAppHistory.setFlowStepList(robotFlowSteps);
        robotAppHistory.setFormGroupList(formGroupList);
        robotAppHistory.setSchedules(schedules);
        robotAppHistory.setTaskSchedules(taskSchedules);
        robotAppHistory.setGeneralPlans(generalPlans);

        //查询信息配置条件
        Example conditionExam = new Example(RobotAppConfigCondition.class);
        conditionExam.createCriteria().andIn("argsDefineId", appArgsList.stream().map(d -> d.getId()).collect(Collectors.toList()));
        List<RobotAppConfigCondition> configConditions = configConditionDao.selectByExample(conditionExam);

        //查询表单表
        List<RobotAppConfigForm> configForms = configFormDao.selectAll();

        //查询分组表
        List<RobotAppConfigGroup> configGroups = configGroupDao.selectAll();

        robotAppHistory.setConfigConditions(configConditions);
        robotAppHistory.setConfigForms(configForms);
        robotAppHistory.setConfigGroups(configGroups);

        //查询任务表
        Example taskExam = new Example(RobotTask.class);
        taskExam.createCriteria().andEqualTo("appCode", appCode);
        List<RobotTask> robotTasks = robotTaskDao.selectByExample(taskExam);
        if (CollectionUtils.isNotEmpty(robotTasks)) {
            //查询任务参数表
            Example taskArgsExam = new Example(RobotTaskArgs.class);
            taskArgsExam.createCriteria().andIn("taskCode", robotTasks.stream().map(t -> t.getTaskCode()).collect(Collectors.toList()));
            List<RobotTaskArgs> robotTaskArgs = robotTaskArgsDao.selectByExample(taskArgsExam);

            robotAppHistory.setRobotTasks(robotTasks);
            robotAppHistory.setRobotTaskArgs(robotTaskArgs);
        }

        //查询应用环境
        Example appEnvExam = new Example(RobotAppEnv.class);
        appEnvExam.createCriteria().andEqualTo("appCode", appCode);
        List<RobotAppEnv> appEnvs = robotAppEnvDao.selectByExample(appEnvExam);
        if (CollectionUtils.isNotEmpty(appEnvs)) {
            robotAppHistory.setAppEnv(appEnvs);
        }

        //机器人流程模板关系
        List<RobotFlowTemplate> flowTemplates = flowTemplateDao.selectByAppCode(appCode);
        if (CollectionUtils.isNotEmpty(flowTemplates)) {
            robotAppHistory.setFlowTemplate(flowTemplates);
            //有用到模板
            List<String> templateFlowCodes = flowTemplates.stream().map(t -> t.getTemplateFlowCode()).distinct().collect(Collectors.toList());

            //模板流程
            exampleFlow = new Example(RobotFlow.class);
            if(CollectionUtils.isNotEmpty(templateFlowCodes)){
                exampleFlow.createCriteria().andIn("flowCode", templateFlowCodes);
            }
            List<RobotFlow> templateFlowList = robotFlowDao.selectByExample(exampleFlow);

            //模板流程步骤
            exampleFlow = new Example(RobotFlowStep.class);
            if(CollectionUtils.isNotEmpty(templateFlowCodes)){
                exampleFlow.createCriteria().andIn("flowCode", templateFlowCodes);
            }
            List<RobotFlowStep> robotFlowStepList = robotFlowStepDao.selectByExample(exampleFlow);

            robotAppHistory.getFlowList().addAll(templateFlowList);
            robotAppHistory.getFlowStepList().addAll(robotFlowStepList);
        }

        //机器人流程模板步骤关系
        List<RobotFlowTemplateStep> flowTemplateSteps = flowTemplateStepDao.selectByAppCode(appCode);
        if (CollectionUtils.isNotEmpty(flowTemplateSteps)) {
            robotAppHistory.setFlowTemplateStep(flowTemplateSteps);
        }

        //机器人流程步骤流程
        List<RobotFlowStepFlow> flowStepFlows = this.getStepFlowList(flowCodes);
        if (CollectionUtils.isNotEmpty(flowStepFlows)) {
            robotAppHistory.getFlowStepFlow().addAll(flowStepFlows);
        }

        //机器人流程模板步骤关系
        List<RobotFlowStepArgs> flowStepArgs = flowStepArgsDao.selectByAppCode(appCode);
        if (CollectionUtils.isNotEmpty(flowStepArgs)) {
            robotAppHistory.setFlowStepArgs(flowStepArgs);
        }
        appHistoryRepository.add(robotAppHistory);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int release(RobotAppVO1 robotAppVO) {
        String appCode = robotAppVO.getAppCode();
        String comment = robotAppVO.getComment();
        String rule = robotAppVO.getRule();
        String changeReason = robotAppVO.getChangeReason();
        if (StringUtils.isBlank(appCode) || StringUtils.isBlank(comment) || StringUtils.isBlank(rule) || StringUtils.isBlank(changeReason)) {
            throw new BusinessException("appCode/comment/rule/changeReason必传");
        }
        Session session = SecurityContext.currentUser();

        //查询app
        Example example = new Example(RobotApp.class);
        example.createCriteria().andEqualTo("appCode", appCode);
        RobotApp robotApp = robotAppDao.selectOneByExample(example);

        //查询flow
        Example exampleFlow = new Example(RobotFlow.class);
        exampleFlow.createCriteria().andEqualTo("appCode", appCode);
        List<RobotFlow> flowLists = robotFlowDao.selectByExample(exampleFlow);

        //查询step
        List<RobotFlowStep> robotFlowSteps = robotFlowStepDao.flowStepList(appCode);
        if (CollectionUtils.isEmpty(flowLists) || CollectionUtils.isEmpty(robotFlowSteps)) {
            throw new BusinessException("此APP下没有任何流程步骤，不能发布");
        }

        //查询信息配置
        Example formGroupExample = new Example(RobotAppFormGroup.class);
        formGroupExample.createCriteria().andEqualTo("appCode", appCode);
        List<RobotAppFormGroup> formGroupList = formGroupDao.selectByExample(formGroupExample);
        if (CollectionUtils.isEmpty(formGroupList)) {
            throw new BusinessException("此APP下没有进行信息配置，不能发布");
        }

        //查询应用执行计划
        List<String> flowCodes = flowLists.stream().map(f -> f.getFlowCode()).collect(Collectors.toList());
        Example scheduleExam = new Example(RobotAppSchedule.class);
        scheduleExam.createCriteria().andIn("flowCode", flowCodes);
        List<RobotAppSchedule> schedules = scheduleDao.selectByExample(scheduleExam);
        if (CollectionUtils.isEmpty(schedules)) {
            throw new BusinessException("此APP下没有进行执行计划配置，不能发布");
        }

        //查询任务执行计划
        Example taskScheduleExam = new Example(RobotTaskSchedule.class);
        taskScheduleExam.createCriteria().andIn("flowCode", flowCodes);
        List<RobotTaskSchedule> taskSchedules = taskScheduleDao.selectByExample(taskScheduleExam);

        //查询通用计划
        Example generalExam = new Example(RobotGeneralPlan.class);
        generalExam.createCriteria().andEqualTo("appCode", appCode);
        List<RobotGeneralPlan> generalPlans = generalPlanDao.selectByExample(generalExam);
        if (CollectionUtils.isEmpty(generalPlans)) {
            throw new BusinessException("此APP下没有设置通用计划，不能发布");
        }

        //查询信息配置参数
        Example exampleArgs = new Example(RobotArgsDefine.class);
        exampleArgs.createCriteria().andEqualTo("argsCode", appCode).andEqualTo("scope", "app").andEqualTo("isDelete", 0);
        List<RobotArgsDefine> appArgsList = robotArgsDefineDao.selectByExample(exampleArgs);
        if (CollectionUtils.isEmpty(appArgsList)) {
            throw new BusinessException("此APP下没有进行信息配置，不能发布");
        }


        String oldVersion = robotApp.getVersion();
        Integer oldStatus = robotApp.getRunStatus();

        //更新版本状态
        robotApp.setVersion(ConvertUtl.getVersionName(oldVersion, rule));
        robotApp.setReleaseTime(new Date());//更新发布时间
        robotApp.setStatus(1);//状态（1 已发布，0 未发布 , 2 历史版本）
        robotApp.setComment(comment);//版本说明
        int count = robotAppDao.updateByExample(robotApp, example);

        //修改历史表版本的状态为2
        appHistoryRepository.update(appCode);

        //发布成功 appcode下的配置状态更新
        //robotFlowDao.updateFlowStatus(appCode);

        //任务更新为未同步状态
        robotTaskDao.cleanSync(appCode);

        //数据存入历史表
        RobotAppHistory robotAppHistory = new RobotAppHistory();
        BeanUtils.copyProperties(robotApp, robotAppHistory);
        User user = SecurityContext.currentUser();
        robotAppHistory.setCreateId((int) user.getId());
        robotAppHistory.setClientId(session.getCustId());
        robotAppHistory.setRule(rule);
        robotAppHistory.setChangeReason(changeReason);
        robotAppHistory.setAppArgsList(appArgsList);
        robotAppHistory.setFlowList(flowLists);
        robotAppHistory.setFlowStepList(robotFlowSteps);
        robotAppHistory.setFormGroupList(formGroupList);
        robotAppHistory.setSchedules(schedules);
        robotAppHistory.setTaskSchedules(taskSchedules);
        robotAppHistory.setGeneralPlans(generalPlans);

        //查询信息配置条件
        Example conditionExam = new Example(RobotAppConfigCondition.class);
        conditionExam.createCriteria().andIn("argsDefineId", appArgsList.stream().map(d -> d.getId()).collect(Collectors.toList()));
        List<RobotAppConfigCondition> configConditions = configConditionDao.selectByExample(conditionExam);

        //查询表单表
        List<RobotAppConfigForm> configForms = configFormDao.selectAll();

        //查询分组表
        List<RobotAppConfigGroup> configGroups = configGroupDao.selectAll();

        robotAppHistory.setConfigConditions(configConditions);
        robotAppHistory.setConfigForms(configForms);
        robotAppHistory.setConfigGroups(configGroups);

        //查询任务表
        Example taskExam = new Example(RobotTask.class);
        taskExam.createCriteria().andEqualTo("appCode", appCode);
        List<RobotTask> robotTasks = robotTaskDao.selectByExample(taskExam);
        if (CollectionUtils.isNotEmpty(robotTasks)) {
            //查询任务参数表
            Example taskArgsExam = new Example(RobotTaskArgs.class);
            taskArgsExam.createCriteria().andIn("taskCode", robotTasks.stream().map(t -> t.getTaskCode()).collect(Collectors.toList()));
            List<RobotTaskArgs> robotTaskArgs = robotTaskArgsDao.selectByExample(taskArgsExam);

            robotAppHistory.setRobotTasks(robotTasks);
            robotAppHistory.setRobotTaskArgs(robotTaskArgs);
        }

        //查询应用环境
        Example appEnvExam = new Example(RobotAppEnv.class);
        appEnvExam.createCriteria().andEqualTo("appCode", appCode);
        List<RobotAppEnv> appEnvs = robotAppEnvDao.selectByExample(appEnvExam);
        if (CollectionUtils.isNotEmpty(appEnvs)) {
            robotAppHistory.setAppEnv(appEnvs);
        }

        //机器人流程模板关系
        List<RobotFlowTemplate> flowTemplates = flowTemplateDao.selectByAppCode(appCode);
        if (CollectionUtils.isNotEmpty(flowTemplates)) {
            robotAppHistory.setFlowTemplate(flowTemplates);

            //有用到模板
            List<String> templateFlowCodes = flowTemplates.stream().map(t -> t.getTemplateFlowCode()).distinct().collect(Collectors.toList());
//            List<RobotFlowStepFlow> flowStepFlows = this.getStepFlowList(templateFlowCodes);
//            Set<String> templateFlowCodeList = Sets.newHashSet();
//            for (RobotFlowStepFlow st : flowStepFlows) {
//                templateFlowCodeList.add(st.getFlowCode());
//                templateFlowCodeList.add(st.getChildFlowCode());
//            }
//            robotAppHistory.setFlowStepFlow(flowStepFlows);

            //模板流程
            exampleFlow = new Example(RobotFlow.class);
            if(CollectionUtils.isNotEmpty(templateFlowCodes)){
                exampleFlow.createCriteria().andIn("flowCode", templateFlowCodes);
            }
            List<RobotFlow> templateFlowList = robotFlowDao.selectByExample(exampleFlow);

            //模板流程步骤
            exampleFlow = new Example(RobotFlowStep.class);
            if(CollectionUtils.isNotEmpty(templateFlowCodes)){
                exampleFlow.createCriteria().andIn("flowCode", templateFlowCodes);
            }
            List<RobotFlowStep> robotFlowStepList = robotFlowStepDao.selectByExample(exampleFlow);

            robotAppHistory.getFlowList().addAll(templateFlowList);
            robotAppHistory.getFlowStepList().addAll(robotFlowStepList);
        }

        //机器人流程模板步骤关系
        List<RobotFlowTemplateStep> flowTemplateSteps = flowTemplateStepDao.selectByAppCode(appCode);
        if (CollectionUtils.isNotEmpty(flowTemplateSteps)) {
            robotAppHistory.setFlowTemplateStep(flowTemplateSteps);
        }

        //机器人流程步骤流程
        List<RobotFlowStepFlow> flowStepFlows = this.getStepFlowList(flowCodes);
        if (CollectionUtils.isNotEmpty(flowStepFlows)) {
            robotAppHistory.getFlowStepFlow().addAll(flowStepFlows);
        }

        //机器人流程模板步骤关系
        List<RobotFlowStepArgs> flowStepArgs = flowStepArgsDao.selectByAppCode(appCode);
        if (CollectionUtils.isNotEmpty(flowStepArgs)) {
            robotAppHistory.setFlowStepArgs(flowStepArgs);
        }
        appHistoryRepository.add(robotAppHistory);

        //更新应用状态
        // 注释代码：发布后不要更新状态为正常运行
//        Example updateExample=new Example(RobotApp.class);
//        updateExample.createCriteria().andEqualTo("appCode",appCode);
//        RobotApp robot=new RobotApp();
//        robot.setAppStatus(RobotAppTypeEnum.NORMAL.getCode());
//        robot.setRunStatus(1);
//        robot.setUpdateTime(new Date());
//        robot.setUpdateId((int) user.getId());
//        robot.setRemark(null);
//        robot.setAppCode(appCode);
//        robotAppDao.updateOnlineMess(robot);
        //插入历史操作表
        RobotFlowStatusHistory history = new RobotFlowStatusHistory();
        history.setAppCode(appCode);
        history.setActionName("上线");
        history.setAppStatus(RobotAppTypeEnum.NORMAL.getCode());
        history.setOnLive(OnLiveEnum.live.getStatus());
        history.setType(1);
        history.setCreateId((int) session.getId());
        history.setCreateName(session.getName());
        history.setCreateTime(new Date());
        statusHistoryDao.insertSelective(history);

        // 插入地区业务变更记录
        String originalValue = String.format("状态：%s，版本：%s", robotApp.getRunStatus() != null && robotApp.getRunStatus() == 1 ? "上线" : "下线", oldVersion);
        String newValue = String.format("状态：上线，版本：%s", robotApp.getVersion());
        saveAddrBusinessProcess(robotApp, "调整RPA流程", changeReason, originalValue, newValue, comment);

        Dict dict = Dict.create().set("appCode", appCode);
        customerCommandService.addCustomerCommand("app", JSON.toJSONString(dict), "发布");
        return count;
    }

    private void saveAddrBusinessProcess(RobotApp robotApp, String changeType, String changeReason, String originalValue, String newValue, String comment) {
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
                dto.setChangeType(changeType);
                dto.setChangeReason(changeReason);
                dto.setCreateId((int)session.getId());
                dto.setCreateName(session.getName());
                dto.setCreateTime(DateUtils.dateToStr(new Date(), "yyyy-MM-dd HH:mm:ss"));
                dto.setOriginalValue(originalValue);
                dto.setNewValue(newValue);
                /*dto.setOriginalValue(String.format("状态：%s,版本：%s", oldStatus==1?"上线":"下线", oldVersion));
                dto.setNewValue(String.format("状态：%s,版本：%s", status==1?"上线":"下线", version));*/
                dto.setComment(comment);
                rpaSaasService.saveAddrBusinessProcess(dto);
            }catch (Exception e) {
                log.info("保存地区业务变动数据异常：{}", e.getMessage());
            }
        }
    }

    /**
     * 机器人流程步骤流程
     */
    private List<RobotFlowStepFlow> getStepFlowList(List<String> flowCodes) {
        List<RobotFlowStepFlow> flowStepFlows = Lists.newArrayList();
        this.getFlowStepFlow(flowStepFlows, flowCodes);
        return flowStepFlows;
    }

    /**
     * 机器人流程步骤流程
     */
    private List<RobotFlowStepFlow> getFlowStepFlow(List<RobotFlowStepFlow> all, List<String> flowCodes) {
        List<RobotFlowStepFlow> stepFlows = flowStepFlowDao.selectByFlowCodes(flowCodes);
        if (CollectionUtils.isNotEmpty(stepFlows)) {
            all.addAll(stepFlows);
            return this.getFlowStepFlow(all, stepFlows.stream().map(f -> f.getChildFlowCode()).distinct().collect(Collectors.toList()));
        }
        return Lists.newArrayList();
    }

    @Override
    public List<RobotAppHistoryVO> findHistory(String appCode) {
        List<RobotAppHistory> history = appHistoryRepository.findHistory(appCode);
        List<RobotAppHistoryVO> listory = new ArrayList<>();
        Example example = new Example(RobotApp.class);
        example.createCriteria().andEqualTo("appCode", appCode);
        RobotApp app = robotAppDao.selectOneByExample(example);
        for (RobotAppHistory robotAppHistory : history) {
            RobotAppHistoryVO robotAppHistoryVO = new RobotAppHistoryVO();
            robotAppHistoryVO.setRunStatus(app.getStatus());
            SysUser user = rpaAuthService.getSysUser(robotAppHistory.getCreateId());
            if (user != null) {
                robotAppHistoryVO.setReleaseName(user.getName());
            }
            BeanUtils.copyProperties(robotAppHistory, robotAppHistoryVO);
            listory.add(robotAppHistoryVO);
        }
        return listory;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int activate(String appCode, String version) {
        //根据appCode查询历史版本页面
        Example example = new Example(RobotApp.class);
        example.createCriteria().andEqualTo("appCode", appCode);
        RobotApp robotApp = robotAppDao.selectOneByExample(example);
        if (robotApp.getStatus() == 1) {
            //修改历史表版本的状态为2
            List<RobotAppHistory> historys = appHistoryRepository.findHistory(appCode);
            if (historys.size() >= 1) {//如果历史表中有数据
                appHistoryRepository.update(appCode);
            }
        }
        //当前版本的状态为未发布
        if (robotApp.getStatus() == 0) {
            appHistoryRepository.updateByVersion(appCode, robotApp.getVersion());//作废

            // 需要缓存一份为发布的本地已修改的应用信息
            saveAppInfo(robotApp);
        }
        //先删除所有流程步骤
        robotFlowStepDao.deleteAllStep(appCode);
        Example exampleFlow1 = new Example(RobotFlow.class);
        exampleFlow1.createCriteria().andEqualTo("appCode", appCode);
        robotFlowDao.deleteByExample(exampleFlow1);

        Example exampleAppFormGroup = new Example(RobotAppFormGroup.class);
        exampleAppFormGroup.createCriteria().andEqualTo("appCode", appCode);
        formGroupDao.deleteByExample(exampleAppFormGroup);

        Example exampleAppArgs = new Example(RobotArgsDefine.class);
        exampleAppArgs.createCriteria().andEqualTo("argsCode", appCode);
        robotArgsDefineDao.deleteByExample(exampleAppArgs);

        robotAppDao.deleteByPrimaryKey(robotApp.getId());
//        robotAppDao.delete(robotApp);
        //把使用此版本的流程和步骤添加进数据库
        //根据版本号查询使用哪一个版本
        RobotAppHistory robotAppHistories = appHistoryRepository.selectUseVersion(appCode, version);
        List<RobotFlow> flowList = robotAppHistories.getFlowList();//flow
        for (RobotFlow robotFlow : flowList) {
            robotFlow.setId(null);
            robotFlow.setUpdateTime(new Date());
            robotFlowDao.insert(robotFlow);
        }
        List<RobotFlowStep> flowStepList = robotAppHistories.getFlowStepList();//step
        for (RobotFlowStep step : flowStepList) {
            step.setId(null);
            step.setUpdateTime(new Date());
            robotFlowStepDao.insert(step);
        }

        List<RobotAppFormGroup> formGroupList = robotAppHistories.getFormGroupList();
        if (CollectionUtils.isNotEmpty(formGroupList)) {
            for (RobotAppFormGroup it : formGroupList) {
                formGroupDao.insert(it);
            }

        }

        List<RobotArgsDefine> appArgsLists = robotAppHistories.getAppArgsList();//robotAppArgsDefine
        for (RobotArgsDefine argsDefine : appArgsLists) {
            robotArgsDefineDao.insert(argsDefine);
        }
        //新版本号
        int i = Integer.valueOf(robotApp.getVersion().substring(5, robotApp.getVersion().length()));//截取旧版本号
        String newVersion = new DecimalFormat().format(i + 1); //新版本号
        RobotApp app = new RobotApp();
        BeanUtils.copyProperties(robotAppHistories, app);
        String headVersion = robotApp.getVersion().substring(0, 5);
        app.setVersion(headVersion + newVersion);//更新版本号
        app.setStatus(1);
        app.setAppName(robotApp.getAppName());
        app.setId(robotApp.getId());
        app.setAppStatus(robotApp.getAppStatus());
        app.setRunStatus(robotApp.getRunStatus());
        app.setAppRemark(robotApp.getAppRemark());
        robotAppDao.insert(app);
        //存入历史表中
        robotAppHistories.setVersion(app.getVersion());//更新版本号
        robotAppHistories.setComment("引用【版本号】进行发布");//发布内容
        robotAppHistories.setReleaseTime(new Date());//发布时间
        robotAppHistories.setStatus(1);
        robotAppHistories.setRule("补丁");
        appHistoryRepository.add(robotAppHistories);
        return 1;
    }

    @Override
    public RobotAppDTO findLatest(String appCode) {
        RobotAppHistory robotAppHistory = appHistoryRepository.latestVersion(appCode);
        if (robotAppHistory == null) {
            return null;
        }
        RobotAppDTO appDTO = new RobotAppDTO();
        BeanUtils.copyProperties(robotAppHistory, appDTO);
        appDTO.setClientId(SecurityContext.currentUser().getCustId());
        appDTO.setRobotArgsDefines(robotAppHistory.getAppArgsList());
        appDTO.setRobotFlows(robotAppHistory.getFlowList());
        appDTO.setRobotFlowSteps(robotAppHistory.getFlowStepList());
        appDTO.setConfigConditions(robotAppHistory.getConfigConditions());
        appDTO.setConfigForms(robotAppHistory.getConfigForms());
        appDTO.setConfigGroups(robotAppHistory.getConfigGroups());
        appDTO.setFormGroupList(robotAppHistory.getFormGroupList());
        appDTO.setScheduleList(robotAppHistory.getSchedules());
        appDTO.setTaskScheduleList(robotAppHistory.getTaskSchedules());
        appDTO.setGeneralPlanList(robotAppHistory.getGeneralPlans());
        appDTO.setRobotAppEnv(robotAppHistory.getAppEnv());
        appDTO.setRobotFlowTemplate(robotAppHistory.getFlowTemplate());
        appDTO.setRobotFlowTemplateStep(robotAppHistory.getFlowTemplateStep());
        appDTO.setRobotFlowStepArgs(robotAppHistory.getFlowStepArgs());
        appDTO.setRobotFlowStepFlow(robotAppHistory.getFlowStepFlow());

        Example example = new Example(RobotApp.class);
        example.createCriteria().andEqualTo("appCode", appCode);
        RobotApp robotApp = robotAppDao.selectOneByExample(example);
        if (robotApp != null) {
            appDTO.setRunStatus(robotApp.getRunStatus());
            appDTO.setSessionKeep(robotApp.getSessionKeep());
            appDTO.setOnlineStatus(robotApp.getOnlineStatus());
            appDTO.setWebsiteLinks(robotApp.getWebsiteLinks());
            appDTO.setRemark(robotApp.getRemark());
            appDTO.setAppRemark(robotApp.getAppRemark());
            appDTO.setAppStatus(robotApp.getAppStatus());
        }
        return appDTO;
    }

    @Override
    public List<RobotApp> allApp() {
        //这里必须是发布后的机器人，发布时会存到mongo，未发布的机器人在客户应用列表增加后，客户端无法同步到
        Example example = new Example(RobotApp.class);
        example.createCriteria().andEqualTo("status", 1);
        return robotAppDao.selectByExample(example);
    }

    @Override
    public List<RobotApp> list() {
        return robotAppDao.selectAll();
    }

    @Override
    public List<Robot> listRobot() {
        return robotDao.selectAll();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer addRobotApp(RobotAppAddVO robotAppAddVO) {
        if ("declare".equals(robotAppAddVO.getRobotName())) {
            Example example = new Example(RobotApp.class);
            example.createCriteria().andEqualTo("appName", robotAppAddVO.getAppName());
            List<RobotApp> robotApps = robotAppDao.selectByExample(example);
            if (CollectionUtils.isNotEmpty(robotApps)) {
                throw new BusinessException("该地区已存在相同业务类型，不允许重复添加");
            }
        }
        RobotApp robotApp = new RobotApp();
        robotApp.setAppName(robotAppAddVO.getAppName());
        robotApp.setAppArgs(robotAppAddVO.getAppArgs());
        robotApp.setRobotCode(robotAppAddVO.getRobotName());
        robotApp.setAppCode(UUIDGenerator.uuidStringWithoutLine());
        robotApp.setAppRemark(robotAppAddVO.getAppRemark());
        //插入历史操作表
        Session session = SecurityContext.currentUser();
        RobotFlowStatusHistory history = new RobotFlowStatusHistory();
        history.setAppCode(robotApp.getAppCode());
        history.setActionName("调研");
        history.setAppStatus(RobotAppTypeEnum.SURVEY.getCode());
        history.setOnLive(OnLiveEnum.launched.getStatus());
        history.setType(1);
        history.setCreateId((int) session.getId());
        history.setCreateName(session.getName());
        history.setCreateTime(new Date());
        statusHistoryDao.insertSelective(history);

        //插入流程服务表
        List<RobotServiceItem> robotServiceItemList=robotAppAddVO.getRobotServiceItemList();
        robotServiceItemList.forEach(e->{
            e.setAppCode(robotApp.getAppCode());
        });
        robotServiceItemDao.insertList(robotServiceItemList);
        return robotAppDao.addRobotApp(robotApp);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer addAppExplain(RobotAppExplain appExplain) {
        Session session = SecurityContext.currentUser();
        appExplain.setUpdateId((int) session.getId());
        appExplain.setUpdateTime(new Date());
        if (appExplain.getId() == null) {
            appExplain.setCreateId((int) session.getId());
            appExplain.setCreateTime(new Date());
            return robotAppExplainDao.insertSelective(appExplain);
        }
        return robotAppExplainDao.updateByPrimaryKeySelective(appExplain);
    }

    @Override
    public RobotAppExplain getAppExplain(String appCode) {
        Example example = new Example(RobotAppExplain.class);
        example.createCriteria().andEqualTo("appCode", appCode);
        return robotAppExplainDao.selectOneByExample(example);
    }

    @Override
    public String queryAppName(String appCode) {
        String appName = "";
        List<String> name2 = robotAppDao.selectAppName(appCode);
        if (CollectionUtils.isEmpty(name2)) {
            return "";
        }
        String name = name2.get(0);
        String shebao = name.substring(name.length() - 2);
        if ("社保".equals(shebao)) {
            appName = name.substring(0, name.length() - 2) + "-" + shebao;
        } else {
            appName = name.substring(0, name.length() - 3) + "-" + name.substring(name.length() - 3);
        }
        return appName;
    }

    @Override
    public String queryAppCode(String appName) {
        return robotAppDao.queryAppCode(appName);
    }

    @Override
    public List<RobotClientVO> getAppByClientId() {
        Session session = SecurityContext.currentUser();
        return robotClientDao.selectClientByClientId(session.getCustId());
    }

    @Override
    public List<RobotClientAppVo> listAppByCustId() {
        Session session = SecurityContext.currentUser();
        return robotClientAppDao.selectAppByClientId(session.getCustId(), "true");
    }

    @Override
    public Boolean hasActivateAppByCustId() {
        Session session = SecurityContext.currentUser();
        List<RobotClientAppVo> list = robotClientAppDao.selectAppByClientId(session.getCustId(), "false");
        if (CollectionUtils.isNotEmpty(list)) {
            return true;
        }
        return false;
    }

    @Override
    public RobotAppVO getRobotDeclareType(String appCode) {
        Example example = new Example(RobotApp.class);
        example.createCriteria().andEqualTo("appCode", appCode);
        RobotApp robotApp = robotAppDao.selectOneByExample(example);
        JSONObject appArgs = JSONObject.parseObject(robotApp.getAppArgs());
        String businessType = appArgs.getString("businessType");
        return rpaSaasService.getRobotDeclareType(businessType);
    }

    @Override
    public String getDictName(String appCode) {
        Example example = new Example(RobotApp.class);
        example.createCriteria().andEqualTo("appCode", appCode);
        RobotApp robotApp = robotAppDao.selectOneByExample(example);
        if (robotApp == null) {
            return null;
        }
        JSONObject appArgs = JSONObject.parseObject(robotApp.getAppArgs());
        String businessType = appArgs.getString("businessType");
        Example dictExam = new Example(RobotDataDict.class);
        dictExam.createCriteria().andEqualTo("dictCode", businessType);
        return robotDataDictDao.selectOneByExample(dictExam).getDictName();
    }

    @Override
    public Integer saveDeclareAccount(DeclareAccountBaseDTO dto) {
        Integer custId = SecurityContext.currentUser().getCustId();
        dto.setCustId(custId);
        return rpaSaasService.saveDeclareAccount(dto);
    }

    @Override
    public List<RobotClientUsb> listUsb(String machineCode) {
        Example example = new Example(RobotClientUsb.class);
        example.createCriteria().andEqualTo("machineCode", machineCode).andEqualTo("sync", 0);
        return clientUsbDao.selectByExample(example);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void callbackUsb(String machineCode) {
        List<RobotClientUsb> clientUsbs = this.listUsb(machineCode);
        if (CollectionUtils.isEmpty(clientUsbs)) {
            return;
        }
        for (RobotClientUsb usb : clientUsbs) {
            RobotClientUsb update = new RobotClientUsb();
            update.setId(usb.getId());
            update.setSync(1);
            update.setSyncTime(new Date());
            update.setUpdateTime(new Date());
            clientUsbDao.updateByPrimaryKeySelective(update);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int saveUsb(RobotClientUsb clientUsb) {
        Example example = new Example(RobotClientUsb.class);
        example.createCriteria().andEqualTo("machineCode", clientUsb.getMachineCode()).andEqualTo("sort", clientUsb.getSort());
        RobotClientUsb robotClientUsb = clientUsbDao.selectOneByExample(example);
        clientUsb.setUpdateTime(new Date());
        if (robotClientUsb != null) {
            clientUsb.setId(robotClientUsb.getId());
            return clientUsbDao.updateByPrimaryKeySelective(clientUsb);
        }
        clientUsb.setCreateTime(new Date());
        return clientUsbDao.insertSelective(clientUsb);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clearUsb(String machineCode) {
        if (StringUtils.isBlank(machineCode)) {
            return;
        }
        Example example = new Example(RobotClientUsb.class);
        example.createCriteria().andEqualTo("machineCode", machineCode);
        clientUsbDao.deleteByExample(example);
    }

    @Override
    public void fileUploadForServer(Map<String, Object> params) {
        String fileName = (String) params.get("fileName");
        String byteString = (String) params.get("byteString");

        Map<String, Object> fileInfo = rpaSaasService.fileUploadForServer(JsonUtils.toJSon(params));
        Integer fileId = (Integer) fileInfo.get("id");
        String clientFileName = (String) fileInfo.get("clientFileName");
        String serverFilePath = (String) fileInfo.get("serverFilePath");
        String fileUrl = (String) fileInfo.get("fileUrl");
        String flowCode = (String) params.get("flowCode");
        String stepCode = (String) params.get("stepCode");
        String executionCode = (String) params.get("executionCode");

        RobotExecutionFileInfo robotFileInfo = new RobotExecutionFileInfo();
        robotFileInfo.setFileId(fileId);
        robotFileInfo.setExecutionCode(executionCode);
        robotFileInfo.setStepCode(stepCode);
        robotFileInfo.setFlowCode(flowCode);
        robotFileInfo.setFileName(clientFileName);
        robotFileInfo.setFilePath(serverFilePath);
        robotFileInfo.setFileFullPath(fileUrl);
        robotFileInfo.setCreateTime(new Date());

        robotExecutionDetailDao.saveRobotFileInfo(robotFileInfo);
    }

    @Override
    public List<String> getMachineCode(Integer clientId, Integer addrId, String businessType, String accountNumber) {
        return robotAppDao.getMachineCode(clientId, addrId, businessType, accountNumber);
    }
}
