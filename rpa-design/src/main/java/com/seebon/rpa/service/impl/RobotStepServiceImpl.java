package com.seebon.rpa.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.seebon.common.utils.UUIDGenerator;
import com.seebon.rpa.BusinessException;
import com.seebon.rpa.auth.SecurityContext;
import com.seebon.rpa.auth.Session;
import com.seebon.rpa.entity.robot.*;
import com.seebon.rpa.entity.robot.dto.design.RobotFlowVO;
import com.seebon.rpa.entity.robot.dto.design.RobotStepAppDescribeVO;
import com.seebon.rpa.entity.robot.vo.RobotAppArgsVO;
import com.seebon.rpa.entity.robot.vo.RobotFlowStepArgsVO;
import com.seebon.rpa.entity.robot.vo.RobotFlowStepVO;
import com.seebon.rpa.repository.mysql.*;
import com.seebon.rpa.service.RobotActionService;
import com.seebon.rpa.service.RobotStepService;
import com.seebon.rpa.utils.ConvertUtl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class RobotStepServiceImpl implements RobotStepService {
    @Autowired
    private RobotAppDao appDao;
    @Autowired
    private RobotFlowDao flowDao;
    @Autowired
    private RobotFlowStepDao flowStepDao;
    @Autowired
    private RobotArgsDefineDao argsDefineDao;
    @Autowired
    private RobotFlowStepArgsDao flowStepArgsDao;
    @Autowired
    private RobotActionService robotActionService;

    @Override
    public List<RobotFlowStepVO> list(String flowCode, String stepCode, String templateFlowCode) {
        /*
         * 1. 仅flowCode有值：
         *      返回flowCode下的步骤
         * 2. 仅flowCode和templateFlowCode有值
         *      返回templateFlowCode下的步骤，并用flowCode的参数（robot_flow_step_args)替换模板相同stepCode的参数
         * 3. 仅flowCode和stepCode有值
         *      返回flowCode的代码块的步骤。即robot_flow_step的group_code等于stepCode的步骤
         * 4. 三者都有值
         *      返回？
         *
         * 1. stepCode为空
         *      . flowCode未引用模板
         *          返回flowCode下的所有步骤
         *      . flowCode引用模板
         *          返回templateFlowCode下的所有步骤，并替换步骤的args（robot_flow_step_args)
         * 2. stepCode不为空
         *      . stepCode是子流程
         *          获取子流程的编码subFlowCode。返回subFlowCode下的所有步骤
         *      . stepCode是代码块
         *          获取代码块的编码group_code。返回group_code下的所有步骤
         *
         */
        RobotFlowVO flow = flowDao.getByFlowCode(flowCode);
        if( null == flow){
            throw new BusinessException("传参错误，流程不存在");
        }

        List<RobotFlowStep> steps;
        if(StringUtils.isBlank(stepCode)) {
            if (StringUtils.isBlank(templateFlowCode)) {
                Example stepExample = new Example(RobotFlowStep.class);
                stepExample.createCriteria().andEqualTo("flowCode", StringUtils.defaultIfBlank(flow.getRelationFlowCode(), flowCode)).andIsNull("groupCode");
                steps = flowStepDao.selectByExample(stepExample);
            } else {
                if(!templateFlowCode.equals(flow.getTemplateFlowCode())){
                    throw new BusinessException("传参错误，流程模版不匹配");
                }
                // 查找替换模板步骤的参数
                Example stepArgsExample = new Example(RobotFlowStepArgs.class);
                stepArgsExample.createCriteria().andEqualTo("flowCode", flowCode);
                List<RobotFlowStepArgs> stepArgs = flowStepArgsDao.selectByExample(stepArgsExample);
                Map<String, RobotFlowStepArgs> usingTemplateSteps = new HashMap<>();
                for (RobotFlowStepArgs args : stepArgs) {
                    usingTemplateSteps.put(args.getStepCode(), args);
                }
                Example stepExample = new Example(RobotFlowStep.class);
                stepExample.createCriteria().andEqualTo("flowCode", templateFlowCode);
                steps = flowStepDao.selectByExample(stepExample);
                // 引用模板的流程，使用模板的步骤+流程的参数
                for (RobotFlowStep step : steps) {
                    if (usingTemplateSteps.containsKey(step.getStepCode())) {
                        step.setTargetArgs(usingTemplateSteps.get(step.getStepCode()).getTargetArgs());
                        step.setActionArgs(usingTemplateSteps.get(step.getStepCode()).getActionArgs());
                    }
                }
            }
        } else {
            RobotFlowStep flowStep = flowStepDao.selectByStepCode(stepCode);
            if (flowStep == null) {
                throw new BusinessException("传参错误，流程步骤不存在");
            }

            Example example = new Example(RobotFlowStep.class);
            Example.Criteria ca = example.createCriteria();
            ca.andEqualTo("flowCode", StringUtils.defaultIfBlank(flow.getRelationFlowCode(), flowCode));
            if ("subFlow".equals(flowStep.getActionCode())) {
                ca.andIsNull("groupCode");
            } else {
                ca.andEqualTo("groupCode", stepCode);
            }
            example.orderBy("number").asc();
            steps = flowStepDao.selectByExample(example);
        }
        List<RobotFlowStepVO> stepVOS = Lists.newArrayList();
        for (RobotFlowStep step : steps) {
            RobotFlowStepVO stepVO = new RobotFlowStepVO();
            BeanUtils.copyProperties(step, stepVO);
            String actionCode = step.getActionCode();
            //流程步骤参数
            JSONObject actionArgsJson = JSON.parseObject(step.getActionArgs());
            List<RobotFlowStepArgsVO> actionArgsVOS = Lists.newArrayList();
            List<RobotAppArgsVO> actionArgsList = robotActionService.findActionArgs(actionCode);
            for (RobotAppArgsVO actionArgs : actionArgsList) {
                RobotFlowStepArgsVO argsVO = new RobotFlowStepArgsVO();
                BeanUtils.copyProperties(actionArgs, argsVO);
                if (actionArgsJson != null && actionArgsJson.containsKey(actionArgs.getFieldKey())) {
                    argsVO.setFieldValue(actionArgsJson.getString(actionArgs.getFieldKey()));
                }
                actionArgsVOS.add(argsVO);
            }
            stepVO.setActionArgsVOS(actionArgsVOS);
            JSONObject targetArgsJson = JSON.parseObject(step.getTargetArgs());
            List<RobotFlowStepArgsVO> targetArgsVOS = Lists.newArrayList();
            List<RobotAppArgsVO> targetArgsList = robotActionService.findTargetArgs(actionCode);
            for (RobotAppArgsVO actionArgs : targetArgsList) {
                RobotFlowStepArgsVO argsVO = new RobotFlowStepArgsVO();
                BeanUtils.copyProperties(actionArgs, argsVO);
                if (targetArgsJson != null && targetArgsJson.containsKey(actionArgs.getFieldKey())) {
                    argsVO.setFieldValue(targetArgsJson.getString(actionArgs.getFieldKey()));
                }
                targetArgsVOS.add(argsVO);
            }
            stepVO.setTargetArgsVOS(targetArgsVOS);
            stepVO.setTrueSkipTo(step.getSkipTo());
            stepVOS.add(stepVO);
        }
        return stepVOS;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(List<RobotFlowStep> steps, String flowCode, String stepCode, String templateFlowCode) {
        Session session = SecurityContext.currentUser();
        //流程
        RobotFlowVO flow = flowDao.getByFlowCode(flowCode);
        RobotFlowStep flowStep = null;
        if (StringUtils.isBlank(stepCode)) {
            flowStep = flowStepDao.selectByStepCode(stepCode);
        }

        //非代码块
        if (null == flowStep || !"codeBlock".equals(flowStep.getActionCode())) {
            //先删除
            flowStepDao.deleteByFlowCode(flowCode);
            for (RobotFlowStep step : steps) {
                step.setId(null);
                step.setType(flow.getTemplateType());
                step.setCreateId((int) session.getId());
                step.setCreateTime(new Date());
                step.setUpdateId((int) session.getId());
                step.setUpdateTime(new Date());
                this.add(step);
            }
        } else {
            //是否引用模板
            if (StringUtils.isBlank(templateFlowCode)) {
                //先删除
                flowStepDao.deleteByFlowCodeAndGroupCode(flowCode, stepCode);
                for (RobotFlowStep step : steps) {
                    step.setType(flow.getTemplateType());
                    step.setGroupCode(stepCode);
                    ConvertUtl.setFlowStep(step);
                    this.add(step);
                }
            } else {
                //先删除
                flowStepDao.deleteByFlowCodeAndGroupCode(flowCode, stepCode);
                flowStepArgsDao.deleteByFlowCodeAndGroupCode(flowCode, stepCode);
                for (RobotFlowStep step : steps) {
                    step.setType(flow.getTemplateType());
                    step.setGroupCode(stepCode);
                    ConvertUtl.setFlowStep(step);
                    this.add(step);

//                    RobotFlowStepArgs args = new RobotFlowStepArgs();
//                    args.setFlowCode(flowCode);
//                    args.setGroupCode(stepCode);
//                    args.setStepCode(step.getStepCode());
//                    args.setTargetArgs(step.getTargetArgs());
//                    args.setActionArgs(step.getActionArgs());
//                    args.setCreateId((int) session.getId());
//                    args.setCreateTime(new Date());
//                    args.setUpdateId((int) session.getId());
//                    args.setUpdateTime(new Date());
//                    flowStepArgsDao.insertSelective(args);
                }
            }
        }

        //更新状态
        this.updateStatus(flowCode);

        return true;
    }

    private void updateStatus(String flowCode) {
        //流程版本为运行中且没有未发布的版本
        RobotApp app = appDao.selectAppByFlowCode(flowCode);
        if (app == null) {
            return;
        }
        if (app.getStatus() == 1) {
            //设置appcode状态为未发布
            appDao.updateStatus(app.getAppCode());
        }
    }

    @Override
    public int countByCode(String flowCode) {
        Example example = new Example(RobotFlowStep.class);
        example.createCriteria().andEqualTo("flowCode", flowCode);
        return flowStepDao.selectCountByExample(example);
    }

    @Override
    public boolean add(RobotFlowStep flowStep) {
        flowStep.setStepName(flowStep.getStepName());
        String flowCode = flowStep.getFlowCode();//流程code
        Integer number = flowStep.getNumber();//步骤序号
        int size = countByCode(flowCode);//步骤数量
        if (number == null || number < 0 || number > size) {
            number = size + 1;
            flowStep.setNumber(number);
        }
        flowStepDao.increaseStepNumber(flowCode, (number - 1));
        Example example = new Example(RobotFlowStep.class);
        example.createCriteria().andEqualTo("flowCode", flowCode).andEqualTo("stepName", flowStep.getStepName());
        //查询是否有重复的步骤名称
        List<RobotFlowStep> step = flowStepDao.selectRepeatStep(flowCode, flowStep.getStepName());
        if (step.size() > 1) {
            throw new BusinessException("名称不可重复!");
        }
        if (StringUtils.isBlank(flowStep.getStepCode())) {
            flowStep.setStepCode(UUIDGenerator.uuidStringWithoutLine());
        }
        flowStep.setCreateTime(new Date());
        int i = flowStepDao.insert(flowStep);
        return i > 0;
    }

    @Override
    public List<RobotArgsDefine> findArgs(String dataType, String actionCode) {
        Example example = new Example(RobotArgsDefine.class);
        example.createCriteria().andEqualTo("scope", dataType).andEqualTo("argsCode", actionCode);
        List<RobotArgsDefine> targetArgsList = argsDefineDao.selectByExample(example);
        return targetArgsList;
    }

    @Override
    public RobotStepAppDescribeVO listDescribe(Integer id) {
        RobotStepAppDescribeVO describeVO = new RobotStepAppDescribeVO();
        RobotFlow robotFlow = flowDao.selectByPrimaryKey(id);
        if (robotFlow == null) {
            return describeVO;
        }
        if (StringUtils.isNotBlank(robotFlow.getRelationFlowCode())) {
            RobotFlowVO flowVO = flowDao.findByFlowCode(robotFlow.getRelationFlowCode());
            describeVO.setRelationFlowCode(robotFlow.getRelationFlowCode());
            describeVO.setRelationAppNameAndFlowName(flowVO.getAppName() + " " + flowVO.getFlowName());
        }
        describeVO.setFlowName(robotFlow.getFlowName());
        describeVO.setFlowType(robotFlow.getFlowType() == 1 ? "主流程" : "子流程");
        if (StringUtils.isBlank(robotFlow.getAppCode())) {
            return describeVO;
        }
        Example example = new Example(RobotApp.class);
        example.createCriteria().andEqualTo("appCode", robotFlow.getAppCode());
        RobotApp robotApp = appDao.selectOneByExample(example);
        describeVO.setAppName(robotApp.getAppName());
        if (robotApp.getAppName().contains("社保")) {
            describeVO.setBusinessType("社保申报");
        }
        if (robotApp.getAppName().contains("公积金")) {
            describeVO.setBusinessType("公积金申报");
        }
        return describeVO;
    }
}
