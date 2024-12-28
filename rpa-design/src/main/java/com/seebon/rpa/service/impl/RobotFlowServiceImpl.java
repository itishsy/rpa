package com.seebon.rpa.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.seebon.common.utils.DateUtils;
import com.seebon.common.utils.UUIDGenerator;
import com.seebon.rpa.BusinessException;
import com.seebon.rpa.auth.SecurityContext;
import com.seebon.rpa.auth.Session;
import com.seebon.rpa.entity.agent.enums.BusinessTypeEnum;
import com.seebon.rpa.entity.auth.po.SysUser;
import com.seebon.rpa.entity.robot.*;
import com.seebon.rpa.entity.robot.dto.design.RobotFlowAddVO;
import com.seebon.rpa.entity.robot.dto.design.RobotFlowVO;
import com.seebon.rpa.entity.robot.enums.FlowTypeEnum;
import com.seebon.rpa.entity.robot.enums.OnLiveEnum;
import com.seebon.rpa.entity.robot.enums.RobotAppTypeEnum;
import com.seebon.rpa.entity.robot.vo.RobotAppVO;
import com.seebon.rpa.entity.robot.vo.RobotFlowStatusHistoryVO;
import com.seebon.rpa.entity.saas.dto.AddrBusinessChangeProcessDTO;
import com.seebon.rpa.remote.RpaAuthService;
import com.seebon.rpa.remote.RpaSaasService;
import com.seebon.rpa.repository.mysql.RobotServiceItemDao;
import com.seebon.rpa.repository.mysql.*;
import com.seebon.rpa.service.RobotFlowService;
import com.seebon.rpa.utils.Assert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
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
public class RobotFlowServiceImpl implements RobotFlowService {
    @Autowired
    private RobotFlowDao robotFlowDao;
    @Autowired
    private RobotAppDao robotAppDao;
    @Autowired
    private RobotFlowStepDao robotFlowStepDao;
    @Autowired
    private RobotAppScheduleDao robotAppScheduleDao;
    @Autowired
    private RobotTaskScheduleDao taskScheduleDao;
    @Autowired
    private RobotGeneralPlanDao generalPlanDao;
    @Autowired
    private RobotClientTaskDao clientTaskDao;
    @Autowired
    private RobotFlowTemplateDao flowTemplateDao;
    @Autowired
    private RobotTaskScheduleDao robotTaskScheduleDao;
    @Autowired
    private RobotTaskDao robotTaskDao;
    @Autowired
    private RobotFlowStatusHistoryDao statusHistoryDao;
    @Autowired
    private RpaAuthService rpaAuthService;
    @Autowired
    private RpaSaasService rpaSaasService;
    @Autowired
    private RobotServiceItemDao robotServiceItemDao;

    @Override
    public List<RobotFlowVO> flowList(RobotFlowVO flowVO) {
        List<RobotFlowVO> list = robotFlowDao.flowList(flowVO);
        for (RobotFlowVO flow : list) {
            if (StringUtils.isBlank(flow.getDeclareSystem())) {
                continue;
            }
            RobotAppVO appVO = rpaSaasService.getRobotDeclareType(flow.getDeclareSystem());
            if (appVO == null) {
                continue;
            }
            flow.setDeclareSystemName(appVO.getBusinessType());
        }
        return list;
    }

    @Override
    public List<RobotFlowVO> totalStatus(RobotFlowVO flowVO) {
        return robotFlowDao.totalStatus(flowVO);
    }

    @Override
    public List<RobotFlowStatusHistoryVO> statusHistory(RobotFlowVO flowVO) {
        Example example = new Example(RobotFlowStatusHistory.class);
        example.orderBy("id").desc();
        example.createCriteria().andEqualTo("appCode", flowVO.getAppCode()).andEqualTo("flowCode", flowVO.getFlowCode()).andEqualTo("type", flowVO.getHistoryType());
        List<RobotFlowStatusHistory> list = statusHistoryDao.selectByExample(example);
        if (CollectionUtils.isEmpty(list)) {
            return Lists.newArrayList();
        }
        return list.stream().map(vo -> {
            RobotFlowStatusHistoryVO historyVO = new RobotFlowStatusHistoryVO();
            BeanUtils.copyProperties(vo, historyVO);
            historyVO.setAppStatusName(RobotAppTypeEnum.getNameByCode(vo.getAppStatus()));
            historyVO.setFlowStatusName(FlowTypeEnum.getNameByCode(vo.getFlowStatus()));
            historyVO.setOnLiveName(OnLiveEnum.getNameByCode(vo.getOnLive()));
            return historyVO;
        }).collect(Collectors.toList());
    }

    @Override
    public List<RobotFlowVO> listTemplate(Integer templateType, String flowType) {
        return robotFlowDao.listTemplate(templateType, flowType);
    }

    @Override
    public int add(RobotFlowAddVO addVO) {
        if (StringUtils.isBlank(addVO.getFlowName())) {
            throw new BusinessException("流程名称不能为空");
        }
        if (addVO.getTemplateType() != null && 1 == addVO.getTemplateType()) {
            return this.addTemplateFlow(addVO);
        }
        return this.addFlow(addVO);
    }

    private int addTemplateFlow(RobotFlowAddVO addVO) {
        Session session = SecurityContext.currentUser();
        //查询是否有重复的流程名称
        Example example = new Example(RobotFlow.class);
        example.createCriteria().andEqualTo("templateType", addVO.getTemplateType()).andEqualTo("flowName", addVO.getFlowName());
        List<RobotFlow> robotFlows = robotFlowDao.selectByExample(example);
        if (robotFlows.size() != 0) {
            throw new BusinessException("流程模板名称不可重复");
        }
        //添加数据
        RobotFlow robotFlow = new RobotFlow();
        robotFlow.setFlowName(addVO.getFlowName());//流程名称
        robotFlow.setExecOrder(0);//执行顺序
        robotFlow.setFlowCode(UUIDGenerator.uuidStringWithoutLine());//flowCode
        robotFlow.setCreateTime(new Date());//创建时间
        robotFlow.setFlowType(addVO.getFlowType());//类型（1主流程，2子流程）
        robotFlow.setServiceItem(addVO.getServiceItem());
        robotFlow.setTagCode(addVO.getTagCode());
        robotFlow.setAddType("add");//自定义新增
        robotFlow.setOpenType(0);//开放类型  0-开放  1-私有
        robotFlow.setTemplateType(1);//是否模板(0：否,1：是)
        robotFlow.setCreateId((int) session.getId());
        robotFlow.setCreateTime(new Date());
        robotFlow.setUpdateId((int) session.getId());
        robotFlow.setUpdateTime(new Date());
        return robotFlowDao.insertSelective(robotFlow);
    }

    private int addFlow(RobotFlowAddVO addVO) {
        //查询是否有重复的流程名称
        Example example = new Example(RobotFlow.class);
        example.createCriteria().andEqualTo("appCode", addVO.getAppCode()).andEqualTo("flowName", addVO.getFlowName());
        List<RobotFlow> robotFlows = robotFlowDao.selectByExample(example);
        if (robotFlows.size() != 0) {
            throw new BusinessException("流程名称不可重复");
        }
        //查询此appcode执行顺序
        Example example2 = new Example(RobotFlow.class);
        example2.createCriteria().andEqualTo("appCode", addVO.getAppCode());
        List<RobotFlow> robotFlows1 = robotFlowDao.selectByExample(example2);
        //添加数据
        RobotFlow robotFlow = new RobotFlow();
        robotFlow.setFlowName(addVO.getFlowName());//流程名称
        robotFlow.setExecOrder(robotFlows1.size() + 1);//执行顺序
        robotFlow.setFlowCode(UUIDGenerator.uuidStringWithoutLine());//flowCode
        robotFlow.setAppCode(addVO.getAppCode());//appCode
        robotFlow.setCreateTime(new Date());//创建时间
        robotFlow.setUpdateTime(new Date());//更新
        robotFlow.setFlowType(addVO.getFlowType());//类型（1主流程，2子流程）
        robotFlow.setAddType("add");//自定义新增
        robotFlow.setOpenType(0);//开放类型  0-开放  1-私有
        robotFlow.setTemplateType(0);//是否模板(0：否,1：是)
        robotFlow.setTagCode(addVO.getTagCode());
        robotFlow.setServiceItem(addVO.getServiceItem());
        robotFlow.setDeclareSystem(addVO.getDeclareSystem());
        robotFlow.setRunSupport(addVO.getRunSupport());
        robotFlow.setStatus(FlowTypeEnum.SURVEY.getCode());
        robotFlowDao.insertSelective(robotFlow);
        //引用模板流程
        this.addFlowTemplate(robotFlow.getFlowCode(), addVO.getTemplateFlowCode());
        //保存状态
        this.addRobotFlowStatusHistory(robotFlow);
        //设置appcode状态为未发布
        return robotAppDao.updateStatus(addVO.getAppCode());
    }

    private void addFlowTemplate(String flowCode, String templateFlowCode) {
        //引用模板流程
        if (StringUtils.isBlank(templateFlowCode)) {
            return;
        }
        Session session = SecurityContext.currentUser();
        RobotFlowTemplate flowTemplate = new RobotFlowTemplate();
        flowTemplate.setTemplateFlowCode(templateFlowCode);
        flowTemplate.setFlowCode(flowCode);
        flowTemplate.setCreateId((int) session.getId());
        flowTemplate.setCreateTime(new Date());
        flowTemplate.setUpdateId((int) session.getId());
        flowTemplate.setUpdateTime(new Date());
        flowTemplateDao.insertSelective(flowTemplate);
    }

    private void addRobotFlowStatusHistory(RobotFlow robotFlow) {
        Session session = SecurityContext.currentUser();
        RobotFlowStatusHistory history = new RobotFlowStatusHistory();
        history.setAppCode(robotFlow.getAppCode());
        history.setFlowCode(robotFlow.getFlowCode());
        history.setActionName("保存流程");
        history.setAppStatus(RobotAppTypeEnum.CONFIGURATION.getCode());
        history.setFlowStatus(FlowTypeEnum.SURVEY.getCode());
        history.setType(2);
        history.setCreateId((int) session.getId());
        history.setCreateName(session.getName());
        history.setCreateTime(new Date());
        statusHistoryDao.insertSelective(history);
        robotAppDao.updateAppStatus(robotFlow.getAppCode(), history.getAppStatus(), null);
    }

    @Override
    public int copyAdd(String flowCode, String flowName, String appCode, String templateFlowCode, String tagCode, Integer serviceItem, String declareSystem, String runSupport) {
        //查询是否有重复的流程名称
        Example example = new Example(RobotFlow.class);
        example.createCriteria().andEqualTo("appCode", appCode).andEqualTo("flowName", flowName);
        List<RobotFlow> robotFlows = robotFlowDao.selectByExample(example);
        if (robotFlows.size() != 0) {
            throw new BusinessException("流程名称不可重复");
        }
        if (flowName == null || flowName.equals("")) {
            throw new BusinessException("流程名称不能为空");
        }
        //根据appcode查询所有flow--设置执行顺序
        Example exampleFlow = new Example(RobotFlow.class);
        exampleFlow.createCriteria().andEqualTo("appCode", appCode);
        List<RobotFlow> robotFlows1 = robotFlowDao.selectByExample(exampleFlow);
        //添加数据
        RobotFlow robotFlow1 = new RobotFlow();
        robotFlow1.setFlowCode(UUIDGenerator.uuidStringWithoutLine());//流程code
        robotFlow1.setAppCode(appCode);//appCode
        robotFlow1.setCreateTime(new Date());//创建时间
        robotFlow1.setUpdateTime(new Date());//更新时间
        robotFlow1.setExecOrder(robotFlows1.size() + 1);//执行顺序
        robotFlow1.setFlowName(flowName);//流程名称
        //复制新增
        robotFlow1.setAddType("copyAdd");
        robotFlow1.setTagCode(tagCode);
        robotFlow1.setServiceItem(serviceItem);
        robotFlow1.setDeclareSystem(declareSystem);
        robotFlow1.setRunSupport(runSupport);
        robotFlow1.setStatus(FlowTypeEnum.SURVEY.getCode());
        Example example1 = new Example(RobotFlow.class);
        example1.createCriteria().andEqualTo("flowCode", flowCode);
        RobotFlow robotFlow = robotFlowDao.selectOneByExample(example1);
        robotFlow.setOpenType(robotFlow.getOpenType());//开放类型  0-开放  1-私有
        robotFlow1.setFlowType(robotFlow.getFlowType());
        robotFlowDao.insertSelective(robotFlow1);
        //引用模板流程
        this.addFlowTemplate(robotFlow1.getFlowCode(), templateFlowCode);

        //复制步骤
        Example exampleStep = new Example(RobotFlowStep.class);
        exampleStep.createCriteria().andEqualTo("flowCode", flowCode);
        List<RobotFlowStep> robotFlowSteps = robotFlowStepDao.selectByExample(exampleStep);
        for (RobotFlowStep flowStep : robotFlowSteps) {
            flowStep.setId(null);
            flowStep.setFlowCode(robotFlow1.getFlowCode());//设置flowCode需与修改flow一致
            flowStep.setCreateTime(new Date());
            flowStep.setUpdateTime(new Date());
            flowStep.setStepCode(UUIDGenerator.uuidStringWithoutLine());//步骤编码
            robotFlowStepDao.insertSelective(flowStep);
        }
        //设置appcode状态为未发布
        return robotAppDao.updateStatus(appCode);
    }

    @Override
    public int glAdd(String flowCode,String flowName,  String appCode, String tagCode, Integer serviceItem, String declareSystem, String runSupport) {
        Assert.isNotBlank(flowName, "流程名称不能为空");
        Example example = new Example(RobotFlow.class);
        example.createCriteria().andEqualTo("appCode", appCode).andEqualTo("flowName", flowName);
        List<RobotFlow> robotFlows = robotFlowDao.selectByExample(example);
        if (CollectionUtils.isNotEmpty(robotFlows)) {
            throw new BusinessException("流程名称不可重复");
        }
        Example exampleFlow = new Example(RobotFlow.class);
        exampleFlow.createCriteria().andEqualTo("appCode", appCode);
        List<RobotFlow> robotFlows1 = robotFlowDao.selectByExample(exampleFlow);
        //添加数据
        RobotFlow robotFlow1 = new RobotFlow();
        robotFlow1.setAppCode(appCode);//appCode
        robotFlow1.setCreateTime(new Date());//创建时间
        robotFlow1.setExecOrder(robotFlows1.size() + 1);//执行顺序
        robotFlow1.setFlowName(flowName);//流程名称
        robotFlow1.setFlowCode(UUIDGenerator.uuidStringWithoutLine());//流程code
        robotFlow1.setRelationFlowCode(flowCode); //关联的哪条数据
        robotFlow1.setAddType("relateAdd");//关联新增
        robotFlow1.setTagCode(tagCode);
        robotFlow1.setServiceItem(serviceItem);
        robotFlow1.setDeclareSystem(declareSystem);
        robotFlow1.setRunSupport(runSupport);
        robotFlow1.setStatus(FlowTypeEnum.SURVEY.getCode());
        Example example1 = new Example(RobotFlow.class);
        example1.createCriteria().andEqualTo("flowCode", flowCode);
        RobotFlow robotFlow = robotFlowDao.selectOneByExample(example1);
        //与关联的那条数据的流程类型一致
        robotFlow1.setFlowType(robotFlow.getFlowType());
        robotFlowDao.insertSelective(robotFlow1);
        //设置appcode状态为未发布
        return robotAppDao.updateStatus(appCode);
    }

    @Override
    public int editFlow(RobotFlow robotFlow) {
        RobotFlow robotFlow1 = robotFlowDao.selectByPrimaryKey(robotFlow.getId());
        if (robotFlow1.getFlowType() == 1 && robotFlow.getFlowType() == 2) {
            Example scheduleExample = new Example(RobotAppSchedule.class);
            scheduleExample.createCriteria().andEqualTo("flowCode", robotFlow.getFlowCode());
            RobotAppSchedule schedule = robotAppScheduleDao.selectOneByExample(scheduleExample);
            if (Objects.nonNull(schedule)) {
                throw new BusinessException("此流程存在流程计划，不能变更为子流程");
            }
        }
        //修改robotApp状态为未发布
        robotAppDao.updateStatus(robotFlow.getAppCode());
        robotFlow1.setServiceItem(robotFlow.getServiceItem());
        robotFlow1.setFlowName(robotFlow.getFlowName());
        robotFlow1.setFlowType(robotFlow.getFlowType());
        robotFlow1.setTagCode(robotFlow.getTagCode());
        robotFlow1.setDeclareSystem(robotFlow.getDeclareSystem());
        robotFlow1.setRunSupport(robotFlow.getRunSupport());
        robotFlow1.setUpdateTime(new Date());
        return robotFlowDao.updateByPrimaryKey(robotFlow1);
    }

    @Override
    public int editFlowStatus(RobotFlowVO flowVO) {
        Session session = SecurityContext.currentUser();
        //1.2）流程状态含【配置】，应用状态更新为【配置】
        //1.3）流程状态无【配置】，含【修复问题】，应用状态更新为【修复问题】
        //1.4）流程状态无【配置、修复问题】，含【等待数据】，应用状态更新为【等待数据】
        //1.5）流程状态只有【调试通过、验证有效】，应用状态更新为【调试通过】
        //保存状态
        RobotFlowStatusHistory history = new RobotFlowStatusHistory();
        history.setAppCode(flowVO.getAppCode());
        history.setFlowCode(flowVO.getFlowCode());
        history.setFlowStatus(flowVO.getStatus());
        if (flowVO.getStatus().equals(FlowTypeEnum.SURVEY.getCode())) {
            history.setAppStatus(RobotAppTypeEnum.CONFIGURATION.getCode());
        } else if (flowVO.getStatus().equals(FlowTypeEnum.REPAIR.getCode())) {
            history.setAppStatus(RobotAppTypeEnum.REPAIR.getCode());
        } else if (flowVO.getStatus().equals(FlowTypeEnum.WAIT.getCode())) {
            history.setAppStatus(RobotAppTypeEnum.WAIT.getCode());
        } else if (flowVO.getStatus().equals(FlowTypeEnum.TEST.getCode()) ||
                flowVO.getStatus().equals(FlowTypeEnum.CHECK.getCode())) {
            history.setAppStatus(RobotAppTypeEnum.TEST.getCode());
        }
        robotAppDao.updateAppStatus(flowVO.getAppCode(), history.getAppStatus(), flowVO.getComment());

        history.setRemark(flowVO.getComment());
        history.setActionName("调整状态");
        history.setType(2);
        history.setCreateId((int) session.getId());
        history.setCreateName(session.getName());
        history.setCreateTime(new Date());
        statusHistoryDao.insertSelective(history);

        //修改robotApp状态为未发布
        robotAppDao.updateStatus(flowVO.getAppCode());
        RobotFlow update = new RobotFlow();
        update.setId(flowVO.getId());
        update.setStatus(flowVO.getStatus());
        update.setComment(flowVO.getComment());
        update.setUpdateId((int) session.getId());
        update.setUpdateTime(new Date());
        return robotFlowDao.updateByPrimaryKeySelective(update);
    }

    @Override
    public int editFlowOrder(RobotFlowVO flowVO) {
        Session session = SecurityContext.currentUser();

        Example example = new Example(RobotFlow.class);
        example.orderBy("execOrder").asc();
        example.createCriteria().andEqualTo("appCode", flowVO.getAppCode()).andNotEqualTo("id", flowVO.getId());
        List<RobotFlow> flowList = robotFlowDao.selectByExample(example);
        Integer order = 0;
        for (RobotFlow flow : flowList) {
            RobotFlow updateFlow = new RobotFlow();
            updateFlow.setId(flow.getId());
            updateFlow.setExecOrder((++order));
            updateFlow.setUpdateId((int) session.getId());
            updateFlow.setUpdateTime(new Date());
            robotFlowDao.updateByPrimaryKeySelective(updateFlow);
        }

        example = new Example(RobotFlow.class);
        example.orderBy("execOrder").asc();
        example.createCriteria().andEqualTo("appCode", flowVO.getAppCode());
        List<RobotFlow> flowLists = robotFlowDao.selectByExample(example);
        for (RobotFlow flow : flowLists) {
            if (flow.getId().intValue() == flowVO.getId().intValue()) {
                RobotFlow update = new RobotFlow();
                update.setId(flowVO.getId());
                update.setExecOrder(flowVO.getExecOrder());
                update.setUpdateId((int) session.getId());
                update.setUpdateTime(new Date());
                robotFlowDao.updateByPrimaryKeySelective(update);
            }
            if (flow.getId().intValue() != flowVO.getId().intValue() && flow.getExecOrder() >= flowVO.getExecOrder()) {
                RobotFlow updateFlow = new RobotFlow();
                updateFlow.setId(flow.getId());
                updateFlow.setExecOrder(flow.getExecOrder() + 1);
                updateFlow.setUpdateId((int) session.getId());
                updateFlow.setUpdateTime(new Date());
                robotFlowDao.updateByPrimaryKeySelective(updateFlow);
            }
        }
        return robotAppDao.updateStatus(flowVO.getAppCode());
    }

    @Override
    public int updateOrder(String appCode, String flowName, int flag) {
        //获取当前修改的流程配置
        Example example = new Example(RobotFlow.class);
        example.createCriteria().andEqualTo("flowName", flowName).andEqualTo("appCode", appCode);
        RobotFlow robotFlow = robotFlowDao.selectOneByExample(example);
        //上移
        if (flag == 1) {
            //获取上一条流程
            Example example1 = new Example(RobotFlow.class);
            example1.createCriteria().andEqualTo("execOrder", robotFlow.getExecOrder() - 1).andEqualTo("appCode", appCode);
            RobotFlow robotFlow1 = robotFlowDao.selectOneByExample(example1);
            robotFlow1.setExecOrder(robotFlow.getExecOrder());//交换执行顺序
            robotFlowDao.updateByExample(robotFlow1, example1);
            robotFlow.setExecOrder(robotFlow.getExecOrder() - 1);
            robotFlowDao.updateByExample(robotFlow, example);
        }
        //下移
        if (flag == 2) {
            //获取下一条流程
            Example example1 = new Example(RobotFlow.class);
            example1.createCriteria().andEqualTo("execOrder", robotFlow.getExecOrder() + 1).andEqualTo("appCode", robotFlow.getAppCode());
            RobotFlow robotFlow1 = robotFlowDao.selectOneByExample(example1);
            robotFlow1.setExecOrder(robotFlow.getExecOrder());//交换执行顺序
            robotFlowDao.updateByExample(robotFlow1, example1);
            robotFlow.setExecOrder(robotFlow.getExecOrder() + 1);
            robotFlowDao.updateByExample(robotFlow, example);
        }
        //设置appcode状态为未发布
        return robotAppDao.updateStatus(robotFlow.getAppCode());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(String flowCode) {
        Example scheduleExample = new Example(RobotAppSchedule.class);
        scheduleExample.createCriteria().andEqualTo("flowCode", flowCode);
        RobotAppSchedule schedule = robotAppScheduleDao.selectOneByExample(scheduleExample);
        if (Objects.nonNull(schedule)) {
            throw new BusinessException("此流程存在流程计划，不能删除");
        }

        //查询出被删除的那条flow
        Example example = new Example(RobotFlow.class);
        example.createCriteria().andEqualTo("flowCode", flowCode);
        RobotFlow robotFlow = robotFlowDao.selectOneByExample(example);

        List<RobotFlow> flowSteps = robotFlowStepDao.findSubFlowStep(robotFlow.getAppCode(), "{\"flowName\":\""+robotFlow.getFlowName()+"\"}" );
        if(flowSteps.size()>0){
            throw new BusinessException("此流程被其它流程中调用，不能删除。 引用流程：" + flowSteps.get(0).getFlowName());
        }

        //把关联这条流程的其他流程的relation_flow_code置为null
        robotFlowDao.updateRelationFlow(flowCode);
        //查询此appCode有多少条flow
        Example exampleFlow = new Example(RobotFlow.class);
        exampleFlow.createCriteria().andEqualTo("appCode", robotFlow.getAppCode());
        List<RobotFlow> robotApps = robotFlowDao.selectByExample(exampleFlow);
        //如果删除的flow不是最后一个
        if (robotFlow.getExecOrder() != robotApps.size()) {
            //重新设置执行顺序
            robotFlowDao.updateExecOrder(robotFlow.getAppCode(), robotFlow.getExecOrder());
        }
        //删除步骤
        Example exampleStep = new Example(RobotFlowStep.class);
        exampleStep.createCriteria().andEqualTo("flowCode", robotFlow.getFlowCode());
        robotFlowStepDao.deleteByExample(exampleStep);
        robotFlowDao.deleteByExample(example);
        //设置appcode状态为未发布
        return robotAppDao.updateStatus(robotFlow.getAppCode());
    }

    @Override
    public List<RobotFlow> allFlow() {
        return robotFlowDao.selectAll();
    }


    @Override
    public List<RobotFlowVO> showFlowByAppCode(String appCode) {
        List<RobotFlowVO> robotFlows = robotFlowDao.showRobotFlow();
        if (StringUtils.isNotBlank(appCode)) {
            //关联新增选择流程名称
            return robotFlows.stream().filter(s -> !s.getAppCode().equals(appCode)).collect(Collectors.toList());
        }
        return robotFlows;
    }

    @Override
    public int editSchedule(RobotAppSchedule robotAppSchedule, String appCode) {
        Session session = SecurityContext.currentUser();

        Example e = new Example(RobotAppSchedule.class);
        e.createCriteria().andEqualTo("flowCode", robotAppSchedule.getFlowCode());
        RobotAppSchedule oldSchedule = robotAppScheduleDao.selectOneByExample(e);

        RobotFlow robotFlow = robotFlowDao.selectOneByExample(e);
        String flowInfo = "";
        if (robotFlow!=null) {
            flowInfo = robotFlow.getFlowCode().concat("-").concat(robotFlow.getFlowName());
        }
        Example example = new Example(RobotGeneralPlan.class);
        example.createCriteria().andEqualTo("appCode", appCode);
        RobotGeneralPlan plan = generalPlanDao.selectOneByExample(example);

        //自定义计划变通用计划
        if (robotAppSchedule.getTaskType() == 1) {
            if (plan == null) {
                throw new BusinessException("此应用下没有设置通用计划，请先设置通用计划");
            }
            RobotAppSchedule schedule = new RobotAppSchedule();
            BeanUtils.copyProperties(robotAppSchedule, schedule);
            schedule.setId(robotAppSchedule.getId());
            schedule.setGeneralPlanId(plan.getId());
            schedule.setEditTime(new Date());
            schedule.setEditId((int) session.getId());
            robotAppScheduleDao.updateByPrimaryKeySelective(schedule);
            /*RobotGeneralPlan robotGeneralPlan = new RobotGeneralPlan();
            BeanUtils.copyProperties(robotAppSchedule, robotGeneralPlan);
            robotGeneralPlan.setAppCode(appCode);
            robotGeneralPlan.setEditTime(new Date());
            robotGeneralPlan.setId(plan.getId());*/

            //更新robotApp状态
            robotAppDao.updateStatus(appCode);
            int i = robotAppScheduleDao.updateByPrimaryKeySelective(schedule);
            if (oldSchedule.getTaskType()!=1) {
                saveAddrBusinessProcess(appCode, String.format("流程：%s，计划类型：通用计划，执行计划：%s", flowInfo, getGeneralPlanText(plan)),
                        String.format("计划类型：自定义计划，%s", getExecPlantText(oldSchedule)), "调整执行计划");
            }
            return i;
        } else {
            //自定义计划
            robotAppSchedule.setEditTime(new Date());
            robotAppSchedule.setEditId((int) session.getId());
            robotAppSchedule.setGeneralPlanId(null);
            //如果执行方式是实时，则清空exec_area_time
            if (robotAppSchedule.getExecStyle() == 3) {
                robotAppSchedule.setExecAreaTime(null);
            }
            //更新robotApp状态
            robotAppDao.updateStatus(appCode);
            int i = robotFlowDao.updateSchedule(robotAppSchedule);
            saveAddrBusinessProcess(appCode, String.format("流程：%s，计划类型：自定义计划，执行计划：%s", flowInfo, getExecPlantText(robotAppSchedule)),
                    oldSchedule.getTaskType()==1?("计划类型：通用计划，".concat(getGeneralPlanText(plan))):("计划类型：自定义计划，".concat(getExecPlantText(oldSchedule))),
                    "调整执行计划");
            return i;
        }
    }

    @Override
    public List<RobotFlowVO> listFlowSchedule(String appCode) {
        List<RobotFlowVO> robotFlowVOS = robotFlowDao.selectFlowSchedule(appCode);
        if (CollectionUtils.isNotEmpty(robotFlowVOS)) {

            Map<Integer, SysUser> userMap = Maps.newHashMap();

            for (RobotFlowVO robotFlowVO : robotFlowVOS) {

                Integer editId = robotFlowVO.getRobotAppSchedule().getEditId();
                if (editId != null) {
                    SysUser user = null;
                    if (userMap.containsKey(editId)) {
                        user = userMap.get(editId);
                    } else {
                        user = rpaAuthService.getSysUser(editId);
                        if (user != null) {
                            userMap.put(editId, user);
                        }
                    }
                    if (user != null) {
                        robotFlowVO.setCustomerName(user.getName());
                    }
                }

                RobotAppSchedule robotAppSchedule = robotFlowVO.getRobotAppSchedule();
                if (robotAppSchedule.getTaskType() == 1) {
                    RobotGeneralPlan plan = generalPlanDao.selectByPrimaryKey(robotAppSchedule.getGeneralPlanId());
                    StringBuffer stringBuffer = new StringBuffer();
                    String execCycle = plan.getExecCycle();
                    String[] split = execCycle.split("-");
                    stringBuffer.append("本月").append(split[0]).append("号").append("-").append(split[1]).append("号").append("：").append(StringUtils.isBlank(plan.getExecAreaTime()) ? "实时" : plan.getExecAreaTime());
                    String s = stringBuffer.toString();
                    robotAppSchedule.setSchedule(s);
                    robotAppSchedule.setExecStyle(plan.getExecStyle());
                    robotAppSchedule.setExecAreaTime(plan.getExecAreaTime());
                    robotAppSchedule.setExecCycle(plan.getExecCycle());
                    if (robotAppSchedule.getEditTime() == null) {
                        robotAppSchedule.setEditTime(plan.getEditTime());
                    }
//                    robotAppSchedule.setEditTime(plan.getEditTime());
                }
                if (robotAppSchedule.getTaskType() == 2) {
                    String execCycle = robotAppSchedule.getExecCycle();
                    String[] split = execCycle.split("-");
                    String schedule = "本月" + split[0] + "号-" + split[1] + "号" + "：" + (StringUtils.isBlank(robotAppSchedule.getExecAreaTime()) ? "实时" : robotAppSchedule.getExecAreaTime());
                    robotAppSchedule.setSchedule(schedule);
                }
            }
        }

        return robotFlowVOS;
    }

    @Override
    public int addFlowSchedule(RobotAppSchedule robotAppSchedule, String appCode) {
        Session session = SecurityContext.currentUser();
        Example flowExample = new Example(RobotFlow.class);
        flowExample.createCriteria().andEqualTo("appCode", appCode);
        List<RobotFlow> robotFlows = robotFlowDao.selectByExample(flowExample);
        List<String> flowCodes = robotFlows.stream().map(RobotFlow::getFlowCode).collect(Collectors.toList());
        int count = robotFlowDao.selectScheduleCountByFlowCode(flowCodes);
        Example scheExam = new Example(RobotAppSchedule.class);
        scheExam.createCriteria().andEqualTo("flowCode", robotAppSchedule.getFlowCode());
        List<RobotAppSchedule> robotAppSchedules = robotAppScheduleDao.selectByExample(scheExam);
        if (CollectionUtils.isNotEmpty(robotAppSchedules)) {
            throw new BusinessException("该流程已存在执行计划，请勿重复添加");
        }
        //添加通用计划
        Optional<RobotFlow> first = robotFlows.stream().filter(it -> it.getFlowCode().equals(robotAppSchedule.getFlowCode())).findFirst();
        String flowInfo = "";
        if (first.isPresent()) {
            flowInfo = first.get().getFlowCode().concat("-").concat(first.get().getFlowName());
        }
        int result = 0;
        if (robotAppSchedule.getTaskType() == 1) {
            Example example = new Example(RobotGeneralPlan.class);
            example.createCriteria().andEqualTo("appCode", appCode);
            RobotGeneralPlan plan = generalPlanDao.selectOneByExample(example);
            if (plan == null) {
                throw new BusinessException("此应用下没有设置通用计划，请先设置通用计划");
            }
            RobotAppSchedule schedule = new RobotAppSchedule();
            schedule.setFlowCode(robotAppSchedule.getFlowCode());
            schedule.setGeneralPlanId(plan.getId());
            schedule.setExecOrder(count + 1);
            schedule.setEditId((int) session.getId());
            schedule.setTaskType(1);
            //更新robotApp状态
            robotAppDao.updateStatus(appCode);
            result = robotAppScheduleDao.insert(schedule);
            saveAddrBusinessProcess(appCode, String.format("流程：%s，计划类型：通用计划，执行计划：%s", flowInfo, getGeneralPlanText(plan)), null, "新增执行计划");
        } else {
            //自定义计划
            robotAppSchedule.setEditId((int) session.getId());
            robotAppSchedule.setExecOrder(count + 1);
            //更新robotApp状态
            robotAppDao.updateStatus(appCode);
            result = robotAppScheduleDao.insert(robotAppSchedule);
            saveAddrBusinessProcess(appCode, String.format("流程：%s，计划类型：自定义计划，执行计划：%s", flowInfo, getExecPlantText(robotAppSchedule)), null, "新增执行计划");
        }
        return result;
    }

    private String getExecPlantText(RobotAppSchedule robotAppSchedule) {
        String execCycle = robotAppSchedule.getExecCycle();
        String[] split = execCycle.split("-");
        return "本月" + split[0] + "号-" + split[1] + "号" + "：" + (StringUtils.isBlank(robotAppSchedule.getExecAreaTime()) ? "实时" : robotAppSchedule.getExecAreaTime());
    }

    private String getGeneralPlanText(RobotGeneralPlan plan) {
        if (plan == null) {
            return "";
        }
        StringBuffer stringBuffer = new StringBuffer();
        String execCycle = plan.getExecCycle();
        String[] split = execCycle.split("-");
        stringBuffer.append("本月").append(split[0]).append("号").append("-").append(split[1]).append("号").append("：").append(StringUtils.isBlank(plan.getExecAreaTime()) ? "实时" : plan.getExecAreaTime());
        return stringBuffer.toString();
    }

    private void saveAddrBusinessProcess(String appCode, String newChedule, String oldChedule, String comment) {
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
                dto.setChangeType("调整RPA执行计划");
//                dto.setChangeReason(StringUtils.isBlank(oldChedule)?"新增执行计划":"调整执行计划");
                dto.setCreateId((int)session.getId());
                dto.setCreateName(session.getName());
                dto.setCreateTime(DateUtils.dateToStr(new Date(), "yyyy-MM-dd HH:mm:ss"));
                dto.setOriginalValue(oldChedule);
                dto.setNewValue(newChedule);
                dto.setComment(comment);
                rpaSaasService.saveAddrBusinessProcess(dto);
            }catch (Exception e) {
                log.info("保存地区业务变动数据异常：{}", e.getMessage());
            }
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public int upOrDownMove(String appCode, String flowCode, Integer flag) {
        //flag=1为上移，flag=2为下移
        Example example = new Example(RobotFlow.class);
        example.createCriteria().andEqualTo("appCode", appCode);
        List<RobotFlow> robotFlows = robotFlowDao.selectByExample(example);
        List<String> flowCodes = robotFlows.stream().map(RobotFlow::getFlowCode).collect(Collectors.toList());
        Example scheduleExample = new Example(RobotAppSchedule.class);
        scheduleExample.createCriteria().andIn("flowCode", flowCodes);
        List<RobotAppSchedule> robotAppSchedules = robotAppScheduleDao.selectByExample(scheduleExample);
        scheduleExample.clear();
        scheduleExample.createCriteria().andEqualTo("flowCode", flowCode);
        //要移的那条数据
        RobotAppSchedule originalRobotSchedule = robotAppScheduleDao.selectOneByExample(scheduleExample);
        if (flag == 1) {
            //查出上面那条schedule
            RobotAppSchedule schedule = robotAppSchedules.stream().filter(s -> s.getExecOrder() == originalRobotSchedule.getExecOrder() - 1).findFirst().get();
            originalRobotSchedule.setExecOrder(originalRobotSchedule.getExecOrder() - 1);
            robotAppScheduleDao.updateByPrimaryKey(originalRobotSchedule);
            schedule.setExecOrder(schedule.getExecOrder() + 1);
            robotAppScheduleDao.updateByPrimaryKey(schedule);
            //更新robotApp状态
            robotAppDao.updateStatus(appCode);
            return 1;
        }
        if (flag == 2) {
            //查询出下面那条schedule
            RobotAppSchedule schedule = robotAppSchedules.stream()
                    .filter(s -> s.getExecOrder() == originalRobotSchedule.getExecOrder() + 1)
                    .findFirst()
                    .orElseThrow(() -> {
                        return new BusinessException("下面无流程计划");
                    });
            originalRobotSchedule.setExecOrder(originalRobotSchedule.getExecOrder() + 1);
            robotAppScheduleDao.updateByPrimaryKey(originalRobotSchedule);
            schedule.setExecOrder(schedule.getExecOrder() - 1);
            robotAppScheduleDao.updateByPrimaryKey(schedule);
            //更新robotApp状态
            robotAppDao.updateStatus(appCode);
            return 1;
        }
        return 0;
    }

    @Override
    public RobotFlowVO editEcho(String flowCode) {
        Example example = new Example(RobotFlow.class);
        example.createCriteria().andEqualTo("flowCode", flowCode);
        RobotFlow robotFlow = robotFlowDao.selectOneByExample(example);
        Example scheduleExam = new Example(RobotAppSchedule.class);
        scheduleExam.createCriteria().andEqualTo("flowCode", flowCode);
        RobotAppSchedule robotAppSchedule = robotAppScheduleDao.selectOneByExample(scheduleExam);
        if (robotAppSchedule.getTaskType() == 1) {
            RobotGeneralPlan plan = generalPlanDao.selectByPrimaryKey(robotAppSchedule.getGeneralPlanId());
            robotAppSchedule.setExecCycle(plan.getExecCycle());
            robotAppSchedule.setExecAreaTime(plan.getExecAreaTime());
            robotAppSchedule.setExecStyle(plan.getExecStyle());
        }
        RobotFlowVO robotFlowVO = new RobotFlowVO();
        robotFlowVO.setFlowName(robotFlow.getFlowName());
        robotFlowVO.setRobotAppSchedule(robotAppSchedule);
        return robotFlowVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int removeSchedule(String flowCode) {
        Example example = new Example(RobotAppSchedule.class);
        //更新robotApp状态
        Example flowExam = new Example(RobotFlow.class);
        flowExam.createCriteria().andEqualTo("flowCode", flowCode);
        RobotFlow robotFlow = robotFlowDao.selectOneByExample(flowExam);
        flowExam.clear();
        flowExam.createCriteria().andEqualTo("appCode", robotFlow.getAppCode());
        List<RobotFlow> robotFlows = robotFlowDao.selectByExample(flowExam);
        List<String> flowCodes = robotFlows.stream().map(RobotFlow::getFlowCode).collect(Collectors.toList());
        int count = robotFlowDao.selectScheduleCountByFlowCode(flowCodes);
        example.createCriteria().andEqualTo("flowCode", flowCode);
        RobotAppSchedule schedule = robotAppScheduleDao.selectOneByExample(example);
        if (count != schedule.getExecOrder()) {
            robotAppScheduleDao.updateByExecOrder(schedule.getExecOrder(), flowCodes);
        }
        robotAppDao.updateStatus(robotFlow.getAppCode());

        Example e = new Example(RobotTaskSchedule.class);
        e.createCriteria().andEqualTo("flowCode", flowCode);
        List<RobotTaskSchedule> robotTaskSchedules = robotTaskScheduleDao.selectByExample(e);
        if (CollectionUtils.isNotEmpty(robotTaskSchedules)) {
            robotTaskScheduleDao.deleteByExample(e);
            for (RobotTaskSchedule robotTaskSchedule : robotTaskSchedules) {
                if (StringUtils.isNotBlank(robotTaskSchedule.getTaskCode())) {
                    //更新同步状态
                    clientTaskDao.cleanSyncTime(robotTaskSchedule.getTaskCode());
                    robotTaskDao.cleanSyncTime(robotTaskSchedule.getTaskCode());
                }
            }
        }

        example.clear();
        example.clear();
        example.createCriteria().andEqualTo("flowCode", flowCode);

        RobotAppSchedule robotAppSchedule = robotAppScheduleDao.selectOneByExample(example);

        int i = robotAppScheduleDao.deleteByExample(example);

        String flowInfo = robotFlow.getFlowCode().concat("-").concat(robotFlow.getFlowName());
        Integer taskType = robotAppSchedule.getTaskType();
        if (taskType == 1) {

            Example example1 = new Example(RobotGeneralPlan.class);
            example1.createCriteria().andEqualTo("appCode", robotFlow.getAppCode());
            RobotGeneralPlan plan = generalPlanDao.selectOneByExample(example1);

            saveAddrBusinessProcess(robotFlow.getAppCode(), String.format("移除流程：%s执行计划，计划类型：通用计划，执行计划：%s", flowInfo, getGeneralPlanText(plan)), null, "移除执行计划");
        } else {
            saveAddrBusinessProcess(robotFlow.getAppCode(), String.format("移除流程：%s执行计划，计划类型：自定义计划，执行计划：%s", flowInfo, getExecPlantText(robotAppSchedule)), null, "移除执行计划");
        }


        return i;
    }

    @Override
    public int updateGeneralPlan(RobotGeneralPlan robotGeneralPlan) {
        Example example = new Example(RobotGeneralPlan.class);
        example.createCriteria().andEqualTo("appCode", robotGeneralPlan.getAppCode());
        RobotGeneralPlan plan = generalPlanDao.selectOneByExample(example);
        robotGeneralPlan.setEditTime(new Date());
        //不存在则新增
        if (plan == null) {
            //更新robotApp状态
            robotAppDao.updateStatus(robotGeneralPlan.getAppCode());
            return generalPlanDao.insert(robotGeneralPlan);
        }
        //存在则编辑
        robotGeneralPlan.setId(plan.getId());

        //更新robotApp状态
        robotAppDao.updateStatus(robotGeneralPlan.getAppCode());
        return generalPlanDao.updateByPrimaryKeySelective(robotGeneralPlan);
    }

    @Override
    public RobotGeneralPlan listGeneralPlanByAppCode(String appCode) {
        Example example = new Example(RobotGeneralPlan.class);
        example.createCriteria().andEqualTo("appCode", appCode);
        return generalPlanDao.selectOneByExample(example);
    }

    @Override
    public List<RobotFlowVO> listScheduleFlow(String appCode) {
        return robotFlowDao.selectScheduleFlow(appCode);
    }

    private void updateClientTask(String taskCode) {
        clientTaskDao.cleanSyncTime(taskCode);
    }


    @Override
    public List<RobotFlowVO> taskScheduleList(String taskCode, String appCode) {
        List<RobotFlowVO> robotFlowVOS = robotFlowDao.selectTaskSchedule(taskCode, appCode);
        for (RobotFlowVO robotFlowVO : robotFlowVOS) {
            RobotAppSchedule robotAppSchedule = robotFlowVO.getRobotAppSchedule();
            if (robotAppSchedule.getTaskType() == 1) {
                RobotGeneralPlan plan = generalPlanDao.selectByPrimaryKey(robotAppSchedule.getGeneralPlanId());
                StringBuffer stringBuffer = new StringBuffer();
                String execCycle = plan.getExecCycle();
                String[] split = execCycle.split("-");
                stringBuffer.append("本月").append(split[0]).append("号").append("-").append(split[1]).append("号").append("：").append(StringUtils.isBlank(plan.getExecAreaTime()) ? "实时" : plan.getExecAreaTime());
                String s = stringBuffer.toString();
                robotAppSchedule.setSchedule(s);
                robotAppSchedule.setExecStyle(plan.getExecStyle());
                robotAppSchedule.setExecAreaTime(plan.getExecAreaTime());
                robotAppSchedule.setExecCycle(plan.getExecCycle());
                robotAppSchedule.setEditTime(plan.getEditTime());
            }
            if (robotAppSchedule.getTaskType() == 2) {
                String execCycle = robotAppSchedule.getExecCycle();
                String[] split = execCycle.split("-");
                String schedule = "本月" + split[0] + "号-" + split[1] + "号" + "：" + (StringUtils.isBlank(robotAppSchedule.getExecAreaTime()) ? "实时" : robotAppSchedule.getExecAreaTime());
                robotAppSchedule.setSchedule(schedule);
            }
        }
        return robotFlowVOS;
    }

    @Override
    public int addTaskSchedule(RobotTaskSchedule taskSchedule, String appCode) {
        Session session = SecurityContext.currentUser();
        Example flowExample = new Example(RobotFlow.class);
        flowExample.createCriteria().andEqualTo("appCode", appCode);
        List<RobotFlow> robotFlows = robotFlowDao.selectByExample(flowExample);
        List<String> flowCodes = robotFlows.stream().map(RobotFlow::getFlowCode).collect(Collectors.toList());
        int count = robotFlowDao.selectTaskScheduleCountByFlowCode(taskSchedule.getTaskCode(), flowCodes);
        Example scheExam = new Example(RobotTaskSchedule.class);
        scheExam.createCriteria().andEqualTo("taskCode", taskSchedule.getTaskCode()).andEqualTo("flowCode", taskSchedule.getFlowCode());
        List<RobotTaskSchedule> robotAppSchedules = taskScheduleDao.selectByExample(scheExam);
        if (CollectionUtils.isNotEmpty(robotAppSchedules)) {
            throw new BusinessException("该流程已存在执行计划，请勿重复添加");
        }
        //添加通用计划
        if (taskSchedule.getTaskType() == 1) {
            Example example = new Example(RobotGeneralPlan.class);
            example.createCriteria().andEqualTo("appCode", appCode);
            RobotGeneralPlan plan = generalPlanDao.selectOneByExample(example);
            if (plan == null) {
                throw new BusinessException("此应用下没有设置通用计划，请先设置通用计划");
            }
            RobotTaskSchedule schedule = new RobotTaskSchedule();
            schedule.setFlowCode(taskSchedule.getFlowCode());
            schedule.setTaskCode(taskSchedule.getTaskCode());
            schedule.setGeneralPlanId(plan.getId());
            schedule.setExecOrder(count + 1);
            schedule.setEditId((int) session.getId());
            schedule.setTaskType(1);
            //更新robotApp状态
            robotAppDao.updateStatus(appCode);
            return taskScheduleDao.insertSelective(schedule);
        }
        //自定义计划
        taskSchedule.setEditId((int) session.getId());
        taskSchedule.setExecOrder(count + 1);
        //更新robotApp状态
        robotAppDao.updateStatus(appCode);
        return taskScheduleDao.insertSelective(taskSchedule);
    }

    @Override
    public int editTaskSchedule(RobotTaskSchedule taskSchedule, String appCode) {
        Session session = SecurityContext.currentUser();
        //自定义计划变通用计划
        if (taskSchedule.getTaskType() == 1) {
            Example example = new Example(RobotGeneralPlan.class);
            example.createCriteria().andEqualTo("appCode", appCode);
            RobotGeneralPlan plan = generalPlanDao.selectOneByExample(example);
            if (plan == null) {
                throw new BusinessException("此应用下没有设置通用计划，请先设置通用计划");
            }
            RobotTaskSchedule schedule = new RobotTaskSchedule();
            BeanUtils.copyProperties(taskSchedule, schedule);
            schedule.setId(taskSchedule.getId());
            schedule.setGeneralPlanId(plan.getId());
            schedule.setEditTime(new Date());
            schedule.setEditId((int) session.getId());
            taskScheduleDao.updateByPrimaryKeySelective(schedule);

            RobotGeneralPlan robotGeneralPlan = new RobotGeneralPlan();
            BeanUtils.copyProperties(taskSchedule, robotGeneralPlan);
            robotGeneralPlan.setAppCode(appCode);
            robotGeneralPlan.setEditTime(new Date());
            robotGeneralPlan.setId(plan.getId());
            //更新robotApp状态
            robotAppDao.updateStatus(appCode);
            return generalPlanDao.updateByPrimaryKeySelective(robotGeneralPlan);
        }
        //自定义计划
        taskSchedule.setEditTime(new Date());
        taskSchedule.setEditId((int) session.getId());
        taskSchedule.setGeneralPlanId(null);
        //如果执行方式是实时，则清空exec_area_time
        if (taskSchedule.getExecStyle() == 3) {
            taskSchedule.setExecAreaTime(null);
        }
        //更新robotApp状态
        robotAppDao.updateStatus(appCode);
        return taskScheduleDao.updateByPrimaryKeySelective(taskSchedule);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int removeTaskSchedule(String taskCode, String flowCode) {
        Example flowExam = new Example(RobotFlow.class);
        flowExam.createCriteria().andEqualTo("flowCode", flowCode);
        RobotFlow robotFlow = robotFlowDao.selectOneByExample(flowExam);

        flowExam.clear();
        flowExam.createCriteria().andEqualTo("appCode", robotFlow.getAppCode());
        List<RobotFlow> robotFlows = robotFlowDao.selectByExample(flowExam);
        List<String> flowCodes = robotFlows.stream().map(RobotFlow::getFlowCode).collect(Collectors.toList());
        int count = robotFlowDao.selectTaskScheduleCountByFlowCode(taskCode, flowCodes);
        Example example = new Example(RobotTaskSchedule.class);
        example.createCriteria().andEqualTo("taskCode", taskCode).andEqualTo("flowCode", flowCode);
        RobotTaskSchedule schedule = taskScheduleDao.selectOneByExample(example);
        if (count != schedule.getExecOrder()) {
            taskScheduleDao.updateByExecOrder(schedule.getExecOrder(), taskCode, flowCodes);
        }
        robotAppDao.updateStatus(robotFlow.getAppCode());
        example.clear();
        example.createCriteria().andEqualTo("flowCode", flowCode);
        return taskScheduleDao.deleteByExample(example);
    }

    @Override
    public RobotFlowVO getTaskSchedule(String taskCode, String flowCode) {
        Example example = new Example(RobotFlow.class);
        example.createCriteria().andEqualTo("flowCode", flowCode);
        RobotFlow robotFlow = robotFlowDao.selectOneByExample(example);
        Example scheduleExam = new Example(RobotTaskSchedule.class);
        scheduleExam.createCriteria().andEqualTo("taskCode", taskCode).andEqualTo("flowCode", flowCode);
        RobotTaskSchedule taskSchedule = taskScheduleDao.selectOneByExample(scheduleExam);
        if (taskSchedule.getTaskType() == 1) {
            RobotGeneralPlan plan = generalPlanDao.selectByPrimaryKey(taskSchedule.getGeneralPlanId());
            taskSchedule.setExecCycle(plan.getExecCycle());
            taskSchedule.setExecAreaTime(plan.getExecAreaTime());
            taskSchedule.setExecStyle(plan.getExecStyle());
        }
        RobotFlowVO robotFlowVO = new RobotFlowVO();
        robotFlowVO.setFlowName(robotFlow.getFlowName());
        robotFlowVO.setRobotTaskSchedule(taskSchedule);
        return robotFlowVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int upOrDownMoveTask(String appCode, String taskCode, String flowCode, Integer flag) {
        //flag=1为上移，flag=2为下移
        Example example = new Example(RobotFlow.class);
        example.createCriteria().andEqualTo("appCode", appCode);
        List<RobotFlow> robotFlows = robotFlowDao.selectByExample(example);
        List<String> flowCodes = robotFlows.stream().map(RobotFlow::getFlowCode).collect(Collectors.toList());
        Example scheduleExample = new Example(RobotTaskSchedule.class);
        scheduleExample.createCriteria().andEqualTo("taskCode", taskCode).andIn("flowCode", flowCodes);
        List<RobotTaskSchedule> taskSchedules = taskScheduleDao.selectByExample(scheduleExample);
        scheduleExample.clear();
        scheduleExample.createCriteria().andEqualTo("taskCode", taskCode).andEqualTo("flowCode", flowCode);
        //要移的那条数据
        RobotTaskSchedule originalRobotSchedule = taskScheduleDao.selectOneByExample(scheduleExample);
        if (flag == 1) {
            //查出上面那条schedule
            RobotTaskSchedule schedule = taskSchedules.stream().filter(s -> s.getExecOrder() == originalRobotSchedule.getExecOrder() - 1).findFirst().get();
            originalRobotSchedule.setExecOrder(originalRobotSchedule.getExecOrder() - 1);
            taskScheduleDao.updateByPrimaryKey(originalRobotSchedule);
            schedule.setExecOrder(schedule.getExecOrder() + 1);
            taskScheduleDao.updateByPrimaryKey(schedule);
            //更新robotApp状态
            robotAppDao.updateStatus(appCode);
            return 1;
        }
        if (flag == 2) {
            //查询出下面那条schedule
            RobotTaskSchedule schedule = taskSchedules.stream().filter(s -> s.getExecOrder() == originalRobotSchedule.getExecOrder() + 1).findFirst().orElseThrow(() -> {
                return new BusinessException("下面无流程计划");
            });
            originalRobotSchedule.setExecOrder(originalRobotSchedule.getExecOrder() + 1);
            taskScheduleDao.updateByPrimaryKey(originalRobotSchedule);
            schedule.setExecOrder(schedule.getExecOrder() - 1);
            taskScheduleDao.updateByPrimaryKey(schedule);
            //更新robotApp状态
            robotAppDao.updateStatus(appCode);
            return 1;
        }
        return 0;
    }

    @Override
    public List<RobotServiceItem> selectServiceList(String appCode){
        Example example=new Example(RobotServiceItem.class);
        example.createCriteria().andEqualTo("appCode",appCode);
        List<RobotServiceItem> robotServiceItems=robotServiceItemDao.selectByExample(example);
        return robotServiceItems;
    }
}
