package com.seebon.rpa.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.seebon.common.utils.UUIDGenerator;
import com.seebon.rpa.BusinessException;
import com.seebon.rpa.auth.SecurityContext;
import com.seebon.rpa.auth.Session;
import com.seebon.rpa.entity.robot.*;
import com.seebon.rpa.entity.robot.dto.design.*;
import com.seebon.rpa.entity.robot.vo.RobotFlowStepVO;
import com.seebon.rpa.remote.RpaDesignProdService;
import com.seebon.rpa.repository.mongodb.RobotFlowDesignRepository;
import com.seebon.rpa.repository.mysql.*;
import com.seebon.rpa.service.RobotFlowDesignService;
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
import java.util.stream.Collectors;

@Slf4j
@Service
public class RobotFlowDesignServiceImpl implements RobotFlowDesignService {
    @Autowired
    private RobotFlowDesignRepository flowDesignRepository;
    @Autowired
    private RobotFlowDao flowDao;
    @Autowired
    private RobotAppDao appDao;
    @Autowired
    private RobotFlowStepDao flowStepDao;
    @Autowired
    private RobotFlowStepArgsDao flowStepArgsDao;
    @Autowired
    private RobotFlowStepFlowDao flowStepFlowDao;
    @Autowired
    private RobotFlowTemplateDao flowTemplateDao;
    @Autowired
    private RobotFlowTemplateStepDao flowTemplateStepDao;
    @Autowired
    private RobotActionDao robotActionDao;
    @Autowired
    private RpaDesignProdService rpaDesignProdService;
    @Autowired
    private RobotStepService stepService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String add(RobotFlowDesignVO flowDesign) {
        ConvertUtl.checkFlowDesign(flowDesign.getSteps());

        //stepCode一定为空。即限制代码块不以流程图的方式保存
        if(StringUtils.isNotBlank(flowDesign.getStepCode())){
            throw new BusinessException("子流程步骤不再引用模板");
        }

        Session session = SecurityContext.currentUser();

        String flowCode = flowDesign.getFlowCode();
        String templateFlowCode = flowDesign.getTemplateFlowCode();

        //流程
        RobotFlowVO flow = flowDao.getByFlowCode(flowDesign.getFlowCode());
        flowDesign.setTemplateType(flow.getTemplateType());

        if(StringUtils.isBlank(templateFlowCode)){
            flowStepDao.deleteByFlowCodeNotGroupCode(flowCode);
            for (RobotFlowStepVO step : flowDesign.getSteps()) {
                step.setId(null);
                step.setNumber(0);
                step.setLevel(0);
                step.setType(flow.getTemplateType());
                step.setFlowCode(flowDesign.getFlowCode());
                step.setSkipTo(step.getTrueSkipTo());
                step.setCreateId((int) session.getId());
                step.setCreateTime(new Date());
                step.setUpdateId((int) session.getId());
                step.setUpdateTime(new Date());
                flowStepDao.insert(step);

                //关联步骤子流程
                this.addStepFlow(flowDesign.getFlowCode(), step);
            }
            //更新data
            this.updateFlowData(flowDesign);
            //保存数据
            return flowDesignRepository.save(flowDesign);
        } else {
            if(!templateFlowCode.equals(flow.getTemplateFlowCode())) {
                throw new BusinessException("参数错误。 流程编码与模板编码不一致");
            }
            flowStepArgsDao.deleteByFlowCode(flowCode);
            for (RobotFlowStepVO step : flowDesign.getSteps()) {
                if (step.getOpenEdit() == 1) {
                    RobotFlowStepArgs flowStepArgs = new RobotFlowStepArgs();
                    flowStepArgs.setFlowCode(flowCode);
                    if(StringUtils.isNotBlank(flowDesign.getStepCode())){
                        step.setGroupCode(flowDesign.getStepCode());
                    }
                    flowStepArgs.setStepCode(step.getStepCode());
                    flowStepArgs.setActionArgs(step.getActionArgs());
                    flowStepArgs.setTargetArgs(step.getTargetArgs());
                    flowStepArgs.setCreateId((int) session.getId());
                    flowStepArgs.setCreateTime(new Date());
                    flowStepArgs.setUpdateId((int) session.getId());
                    flowStepArgs.setUpdateTime(new Date());
                    flowStepArgsDao.deleteByFlowCodeAndGroupCode(flowCode,step.getStepCode());
                    flowStepArgsDao.insert(flowStepArgs);
                }
            }
            //更新data
            this.updateFlowData(flowDesign);
            RobotFlowDesign design = flowDesignRepository.findByFlowCode(templateFlowCode);
            return design.getId();
        }
    }


//    @Override
    @Transactional(rollbackFor = Exception.class)
    public String add2(RobotFlowDesignVO flowDesign) {
        ConvertUtl.checkFlowDesign(flowDesign.getSteps());

        Session session = SecurityContext.currentUser();
        //流程
        RobotFlowVO flow = flowDao.getByFlowCode(flowDesign.getFlowCode());
        flowDesign.setTemplateType(flow.getTemplateType());

        //流程
        if (StringUtils.isBlank(flowDesign.getStepCode())) {
            //先清除
            flowStepDao.deleteByFlowCodeNotGroupCode(flowDesign.getFlowCode());
            for (RobotFlowStepVO step : flowDesign.getSteps()) {
                step.setId(null);
                step.setNumber(0);
                step.setLevel(0);
                step.setType(flow.getTemplateType());
                step.setFlowCode(flowDesign.getFlowCode());
                step.setSkipTo(step.getTrueSkipTo());
                step.setCreateId((int) session.getId());
                step.setCreateTime(new Date());
                step.setUpdateId((int) session.getId());
                step.setUpdateTime(new Date());
                flowStepDao.insert(step);

                //关联步骤子流程
                this.addStepFlow(flowDesign.getFlowCode(), step);
            }
        } else {
            //判断是否引用模板
            String flowCode = UUIDGenerator.uuidStringWithoutLine();
            if (StringUtils.isNotBlank(flow.getTemplateFlowCode())) {
                RobotFlowTemplateStep templateStep = flowTemplateStepDao.selectByTemplateFlowCode(flowDesign.getTemplateFlowCode(), flowDesign.getStepCode(), flowDesign.getFlowCode());
                if (templateStep == null) {
                    //步骤流程
                    RobotFlow robotFlow = new RobotFlow();
                    robotFlow.setFlowName(flowDesign.getStepName());//流程名称
                    robotFlow.setExecOrder(0);//执行顺序
                    robotFlow.setFlowCode(flowCode);//flowCode
                    robotFlow.setAppCode(flowDesign.getAppCode());//appCode
                    robotFlow.setFlowType(2);//类型（1主流程，2子流程）
                    robotFlow.setAddType("add");//自定义新增
                    robotFlow.setOpenType(1);//开放类型  0-开放  1-私有
                    robotFlow.setTemplateType(flow.getTemplateType());//开是否模板(0：否,1：是)
                    robotFlow.setCreateId((int) session.getId());
                    robotFlow.setCreateTime(new Date());
                    robotFlow.setUpdateId((int) session.getId());
                    robotFlow.setUpdateTime(new Date());
                    flowDao.insertSelective(robotFlow);

                    templateStep = new RobotFlowTemplateStep();
                    templateStep.setTemplateFlowCode(flowDesign.getTemplateFlowCode());
                    templateStep.setTemplateStepCode(flowDesign.getStepCode());
                    templateStep.setFlowCode(flowCode);
                    templateStep.setMainFlowCode(flowDesign.getFlowCode());
                    templateStep.setCreateId((int) session.getId());
                    templateStep.setCreateTime(new Date());
                    templateStep.setUpdateId((int) session.getId());
                    templateStep.setUpdateTime(new Date());
                    flowTemplateStepDao.insertSelective(templateStep);
                } else {
                    flowCode = templateStep.getFlowCode();
                }
            } else {
                RobotFlowStepFlow stepFlow = flowStepFlowDao.selectByStepCode(flowDesign.getFlowCode(), flowDesign.getStepCode());
                if (stepFlow == null) {
                    //步骤流程
                    RobotFlow robotFlow = new RobotFlow();
                    robotFlow.setFlowName(flowDesign.getStepName());//流程名称
                    robotFlow.setExecOrder(0);//执行顺序
                    robotFlow.setFlowCode(flowCode);//flowCode
                    robotFlow.setAppCode(flowDesign.getAppCode());//appCode
                    robotFlow.setFlowType(2);//类型（1主流程，2子流程）
                    robotFlow.setAddType("add");//自定义新增
                    robotFlow.setOpenType(1);//开放类型  0-开放  1-私有
                    robotFlow.setTemplateType(flow.getTemplateType());//开是否模板(0：否,1：是)
                    robotFlow.setCreateId((int) session.getId());
                    robotFlow.setCreateTime(new Date());
                    robotFlow.setUpdateId((int) session.getId());
                    robotFlow.setUpdateTime(new Date());
                    flowDao.insertSelective(robotFlow);

                    stepFlow = new RobotFlowStepFlow();
                    stepFlow.setFlowCode(flowDesign.getFlowCode());
                    stepFlow.setStepCode(flowDesign.getStepCode());
                    stepFlow.setChildFlowCode(flowCode);
                    stepFlow.setCreateId((int) session.getId());
                    stepFlow.setCreateTime(new Date());
                    stepFlow.setUpdateId((int) session.getId());
                    stepFlow.setUpdateTime(new Date());
                    flowStepFlowDao.insertSelective(stepFlow);
                } else {
                    flowCode = stepFlow.getChildFlowCode();
                }
            }
            //先清除
            flowStepDao.deleteByFlowCodeNotGroupCode(flowCode);
            for (RobotFlowStepVO step : flowDesign.getSteps()) {
                step.setId(null);
                step.setNumber(0);
                step.setLevel(0);
                step.setSkipTo(step.getTrueSkipTo());
                step.setFlowCode(flowCode);
                step.setCreateId((int) session.getId());
                step.setCreateTime(new Date());
                step.setUpdateId((int) session.getId());
                step.setUpdateTime(new Date());
                flowStepDao.insert(step);
            }
            //保存数据
            flowDesign.setFlowCode(flowCode);
            flowDesign.setFlowName(flowDesign.getStepName());
        }

        //设置appCode状态为未发布
        this.updateStatus(flowDesign);

        //更新data
        this.updateFlowData(flowDesign);

        //保存数据
        return flowDesignRepository.save(flowDesign);
    }

    /***
     * 判断逻辑：
     *
     * 当参数stepCode未传值时：
     *
     * 根据flowCode去`robot_flow`表查，
     * template_type=1，打开方式是flowchart，返回值｛"opentype":1,"flowCode": flowCode｝
     * template_type=0，flowCode查`robot_flow_step`，无数据，打开方式是flowchart，返回值｛"opentype":1,"flowCode": flowCode｝；有数据，打开方式是flowstep，返回值｛"opentype":0,"flowCode": flowCode｝
     *
     *
     * 当参数stepCode有传值时：
     *
     * 第一步：查flowName
     * 根据flowCode+stepCode查`robot_flow_step_args`，字段action_args中能查出flowName，
     * 若查不出flowName，则用stepCode查`robot_flow_step`，字段action_args中能查出flowName，
     * 若查不出flowName，返回值NULL
     *
     * 第二步：查flowCode
     * 根据appCode+flowName查`robot_flow`表的字段flowCode
     * 若查不出flowCode，返回值NULL
     *
     * 第三步：传参appCode、flowCode调用自身
     * @param exVO
     * @return
     */
    public RobotFlowOpenTypeVO getFlowOpenType(String appCode, String flowCode, String stepCode){
        RobotFlowOpenTypeVO flowOpenTypeVO = new RobotFlowOpenTypeVO();
        if (StringUtils.isBlank(stepCode)) {
            RobotFlowVO flowVO = flowDao.getByFlowCode(flowCode);
            if(flowVO != null){
                flowOpenTypeVO.setFlowCode(flowCode);
                flowOpenTypeVO.setTemplateFlowCode(flowVO.getTemplateFlowCode());
                if((flowVO.getTemplateType() != null && flowVO.getTemplateType() == 1) || StringUtils.isNotBlank(flowVO.getTemplateFlowCode())){
                    flowOpenTypeVO.setOpenType(1);
                }
                else {
                    RobotFlowDesign flowDesign = flowDesignRepository.findByFlowCode(flowCode);
                    if (flowDesign == null || StringUtils.isBlank(flowDesign.getFlowCode())) {
                        flowOpenTypeVO.setOpenType(0);
                    } else {
                        flowOpenTypeVO.setOpenType(1);
                    }
                }
            }
        } else {
            RobotFlowStep step = flowStepDao.selectByStepCode(stepCode);
            if(step == null){
                throw new RuntimeException("找不到步骤：" + stepCode);
            }

            if("codeBlock".equals(step.getActionCode())){
                flowOpenTypeVO.setFlowCode(flowCode);
                flowOpenTypeVO.setOpenType(0);
            } else {
                String flowName = "";
                RobotFlowStepArgs stepArgs = flowStepArgsDao.selectByFlowCode(flowCode, null, stepCode);
                if (stepArgs != null) {
                    JSONObject object = JSONObject.parseObject(stepArgs.getActionArgs());
                    if (object != null && object.containsKey("flowName") && StringUtils.isNotBlank(object.getString("flowName"))) {
                        flowName = object.getString("flowName");
                    }
                }
                if (StringUtils.isBlank(flowName)) {
                    if (StringUtils.isNotBlank(step.getActionArgs())) {
                        JSONObject object = JSONObject.parseObject(step.getActionArgs());
                        if (object != null && object.containsKey("flowName") && StringUtils.isNotBlank(object.getString("flowName"))) {
                            flowName = object.getString("flowName");
                        }
                    }
                }
                if (StringUtils.isNotBlank(flowName)) {
                    Example example = new Example(RobotFlow.class);
                    example.createCriteria().andEqualTo("appCode", appCode).andEqualTo("flowName", flowName);
                    RobotFlow flow = flowDao.selectOneByExample(example);
                    if (flow != null) {
                        return getFlowOpenType(appCode, flow.getFlowCode(), null);
                    }
                }
                return getFlowOpenType(appCode, flowCode, null);
            }
        }
        return flowOpenTypeVO;
    }

    @Override
    public RobotFlowDesign getByCode(RobotFlowDesignExVO exVO) {
        //模板流程
        if (StringUtils.isNotBlank(exVO.getTemplateFlowCode())) {
            if (StringUtils.isNotBlank(exVO.getStepCode())) {
                //流程引用模板
                RobotFlowTemplateStep templateStep = flowTemplateStepDao.selectByTemplateFlowCode(exVO.getTemplateFlowCode(), exVO.getStepCode(), exVO.getFlowCode());
                if (templateStep != null) {
                    return flowDesignRepository.findByFlowCode(templateStep.getFlowCode());
                }
                //步骤流程
                RobotFlowStepFlow stepFlow = flowStepFlowDao.selectByStepCode(exVO.getTemplateFlowCode(), exVO.getStepCode());
                if (stepFlow != null) {
                    return flowDesignRepository.findByFlowCode(stepFlow.getChildFlowCode());
                }
                RobotFlowOpenTypeVO openTypeVO = getFlowOpenType(exVO.getAppCode(), exVO.getFlowCode(), exVO.getStepCode());
                if (openTypeVO != null && openTypeVO.getOpenType() == 1) {
                    return flowDesignRepository.findByFlowCode(openTypeVO.getFlowCode());
                }
                return null;
            }
            return flowDesignRepository.findByFlowCode(exVO.getTemplateFlowCode());
        } else {
            if (StringUtils.isNotBlank(exVO.getStepCode())) {
                //步骤流程
                RobotFlowStepFlow stepFlow = flowStepFlowDao.selectByStepCode(exVO.getFlowCode(), exVO.getStepCode());
                if (stepFlow != null) {
                    return flowDesignRepository.findByFlowCode(stepFlow.getChildFlowCode());
                }
                return null;
            }
            return flowDesignRepository.findByFlowCode(exVO.getFlowCode());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RobotFlowDesign copyByCode(RobotFlowDesignCopyVO exVO) {
        RobotFlowDesign design = flowDesignRepository.findByFlowCode(exVO.getTargetFlowCode());
        if (design == null) {
            throw new BusinessException("请先添加开始和结束两个指令并保存流程，再复制.");
        }
        List<RobotAction> actionList = robotActionDao.findAll();
        Map<String, String> actionMap = actionList.stream().collect(Collectors.toMap(k -> k.getActionCode(), v -> v.getActionName()));

        //删除原有步骤
        List<RobotFlowStepVO> oldSteps = stepService.list(exVO.getTargetFlowCode(), null, null);
        for (RobotFlowStepVO stepVO : oldSteps) {
            if ("start".equals(stepVO.getActionCode()) || "end".equals(stepVO.getActionCode())) {
                continue;
            }
            flowStepDao.deleteByPrimaryKey(stepVO.getId());
        }

        //复制新的步骤
        List<RobotFlowStepVO> newSteps = stepService.list(exVO.getSourceFlowCode(), null, null);
        for (RobotFlowStepVO stepVO : newSteps) {
            if ("start".equals(stepVO.getActionCode()) || "end".equals(stepVO.getActionCode())) {
                continue;
            }
            RobotFlowStep step = new RobotFlowStep();
            BeanUtils.copyProperties(stepVO, step);
            step.setId(null);
            step.setLevel(0);
            step.setGroupCode(null);
            step.setStepCode(UUIDGenerator.uuidStringWithoutLine());
            step.setFlowCode(exVO.getTargetFlowCode());
            flowStepDao.insertSelective(step);
        }
        List<JSONObject> cells = Lists.newCopyOnWriteArrayList();
        //开始
        JSONObject start = null;
        //最后一个指令
        JSONObject end = null;

        List<RobotFlowStepVO> stepVOS = stepService.list(exVO.getTargetFlowCode(), null, null);
        for (int i = 0; i < stepVOS.size(); i++) {
            RobotFlowStepVO stepVO = stepVOS.get(i);
            if ("start".equals(stepVO.getActionCode())) {
                start = ConvertUtl.getJson("start.json", stepVO.getStepCode());
                ConvertUtl.setData(stepVO, start, actionMap, exVO);
            }
            if ("end".equals(stepVO.getActionCode())) {
                end = ConvertUtl.getJson("end.json", stepVO.getStepCode());
                ConvertUtl.setData(stepVO, end, actionMap, exVO);
            }
        }

        cells.add(start);
        cells.add(end);

        //线
        String source_port = ConvertUtl.getPortId(start, "bottom");
        String source_cell = start.getString("id");
        String target_port = null;
        String target_cell = null;

        for (int i = 0; i < stepVOS.size(); i++) {
            RobotFlowStepVO stepVO = stepVOS.get(i);
            if ("start".equals(stepVO.getActionCode()) || "end".equals(stepVO.getActionCode())) {
                continue;
            }
            if (!"match".equals(stepVO.getActionCode())) {
                JSONObject step = ConvertUtl.getJson("step.json", stepVO.getStepCode());
                ConvertUtl.setData(stepVO, step, actionMap, exVO);
                String step_id = step.getString("id");
                String top_step_port_id = ConvertUtl.getPortId(step, "top");
                String bottom_step_port_id = ConvertUtl.getPortId(step, "bottom");
                cells.add(step);

                target_port = top_step_port_id;
                target_cell = step_id;

                JSONObject edge = ConvertUtl.getJson("edge.json", null);
                ConvertUtl.setEdge(edge, source_port, source_cell, target_port, target_cell);
                cells.add(edge);

                source_port = bottom_step_port_id;
                source_cell = step_id;
            } else if ("match".equals(stepVO.getActionCode())) {
                JSONObject polygon = ConvertUtl.getJson("polygon.json", stepVO.getStepCode());
                ConvertUtl.setData(stepVO, polygon, actionMap, exVO);
                polygon.getJSONObject("attrs").getJSONObject("label").put("text", stepVO.getStepName());

                String step_id = polygon.getString("id");
                String top_step_port_id = ConvertUtl.getPortId(polygon, "top");
                String bottom_step_port_id = ConvertUtl.getPortId(polygon, "bottom");

                cells.add(polygon);

                target_port = top_step_port_id;
                target_cell = step_id;

                JSONObject edge = ConvertUtl.getJson("edge.json", null);
                ConvertUtl.setEdge(edge, source_port, source_cell, target_port, target_cell);
                cells.add(edge);

                source_port = bottom_step_port_id;
                source_cell = step_id;
            }
        }

        JSONObject end_edge = ConvertUtl.getJson("edge.json", null);
        ConvertUtl.setEdge(end_edge, source_port, source_cell, ConvertUtl.getPortId(end, "top"), end.getString("id"));
        cells.add(end_edge);

        //对判断节点重新连线
        for (JSONObject jsonObj : cells) {
            JSONObject data = jsonObj.getJSONObject("data");
            if (data == null) {
                continue;
            }
            String step = data.getString("step");
            if (StringUtils.isBlank(step)) {
                continue;
            }
            RobotFlowStep flowStep = JSON.parseObject(step, RobotFlowStep.class);
            if (StringUtils.isBlank(flowStep.getSkipTo())) {
                continue;
            }
            JSONObject skipToJson = ConvertUtl.getSkipTo(cells, flowStep.getSkipTo());
            if (skipToJson == null) {
                continue;
            }
            //判断节点
            if ("custom-polygon".equals(jsonObj.getString("shape"))) {
                source_cell = jsonObj.getString("id");
                source_port = ConvertUtl.getPortId(jsonObj, "bottom");
                String source_port_left = ConvertUtl.getPortId(jsonObj, "left");
                String source_port_right = ConvertUtl.getPortId(jsonObj, "right");

                target_cell = skipToJson.getString("id");
                target_port = ConvertUtl.getPortId(skipToJson, "top");

                JSONObject edge_true = ConvertUtl.getJson("edge_true.json", null);
                ConvertUtl.setEdge(edge_true, source_port_left, source_cell, target_port, target_cell);
                cells.add(edge_true);

                JSONObject edgeObj = ConvertUtl.getEdgeCell(cells, source_cell, source_port);
                if (edgeObj != null) {
                    edgeObj.put("labels", Lists.newArrayList("否"));
                    JSONObject edgeData = new JSONObject();
                    edgeData.put("quote", false);
                    edgeData.put("nodeState", "0");
                    edgeData.put("nodeLogic", "0");
                    edgeData.put("flowName", "否");
                    edgeData.put("desc", "否");
                    edgeObj.put("data", edgeData);
                    edgeObj.getJSONObject("source").put("port", source_port_right);
                }
            } else {
                source_cell = jsonObj.getString("id");
                source_port = ConvertUtl.getPortId(jsonObj, "bottom");

                target_cell = skipToJson.getString("id");
                target_port = ConvertUtl.getPortId(skipToJson, "top");

                JSONObject edgeObj = ConvertUtl.getEdgeCell(cells, source_cell, source_port);
                if (edgeObj != null) {
                    edgeObj.getJSONObject("target").put("cell", target_cell);
                    edgeObj.getJSONObject("target").put("port", target_port);
                }
            }
        }
        JSONObject cellsJson = new JSONObject();
        cellsJson.put("cells", cells);
        design.setData(cellsJson.toJSONString());

        return design;
    }

    @Override
    public List<RobotFlow> getSubByAppCode(String appCode) {
        Example example = new Example(RobotFlow.class);
        example.createCriteria().andEqualTo("appCode", appCode).andEqualTo("flowType", 2);
        return flowDao.selectByExample(example);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int syncIn(RobotFlowDesignSyncVO syncVO) {
        //流程设计
        flowDesignRepository.batchSave(syncVO.getDesignList());

        //删除旧流程
        Example flowExample = new Example(RobotFlow.class);
        flowExample.createCriteria().andIn("flowCode", syncVO.getFlowCodes());
        flowDao.deleteByExample(flowExample);
        //保存新流程
        if (CollectionUtils.isNotEmpty(syncVO.getFlowList())) {
            syncVO.getFlowList().stream().forEach(it -> {
                it.setId(null);
                it.setCreateTime(new Date());
            });
            flowDao.insertList(syncVO.getFlowList());
        }

        //删除旧流程步骤
        Example stepExample = new Example(RobotFlowStep.class);
        stepExample.createCriteria().andIn("flowCode", syncVO.getFlowCodes());
        flowStepDao.deleteByExample(stepExample);
        //保存新流程步骤
        if (CollectionUtils.isNotEmpty(syncVO.getFlowStepList())) {
            syncVO.getFlowStepList().stream().forEach(it -> {
                it.setId(null);
                it.setCreateTime(new Date());
            });
            flowStepDao.insertList(syncVO.getFlowStepList());
        }

        //删除旧流程步骤流程
        Example stepFlowExample = new Example(RobotFlowStepFlow.class);
        stepFlowExample.createCriteria().andIn("flowCode", syncVO.getFlowCodes());
        flowStepFlowDao.deleteByExample(stepFlowExample);
        //保存新流程步骤流程
        if (CollectionUtils.isNotEmpty(syncVO.getFlowStepFlow())) {
            syncVO.getFlowStepFlow().stream().forEach(it -> {
                it.setId(null);
                it.setCreateTime(new Date());
            });
            flowStepFlowDao.insertList(syncVO.getFlowStepFlow());
        }

        //如果有流程引用了模板，则需要发布
        Example example = new Example(RobotFlowTemplate.class);
        example.createCriteria().andIn("templateFlowCode", syncVO.getFlowCodes());
        List<RobotFlowTemplate> templates = flowTemplateDao.selectByExample(example);
        if (CollectionUtils.isNotEmpty(templates)) {
            for (RobotFlowTemplate template : templates) {
                RobotFlowVO flow = flowDao.getByFlowCode(template.getFlowCode());
                if (flow == null) {
                    continue;
                }
                this.updateRobotApp(flow.getFlowCode());
            }
        }
        return 1;
    }

    @Override
    public int syncOut(String comment) {
        List<RobotFlowDesign> designList = flowDesignRepository.listByTemplateType(1);
        if (CollectionUtils.isEmpty(designList)) {
            return 1;
        }
        //流程编码
        List<String> flowCodes = designList.stream().map(vo -> vo.getFlowCode()).distinct().collect(Collectors.toList());

        RobotFlowDesignSyncVO syncVO = new RobotFlowDesignSyncVO();
        syncVO.setComment(comment);
        syncVO.setDesignList(designList);
        syncVO.setFlowCodes(flowCodes);

        //应用流程
        Example exampleFlow = new Example(RobotFlow.class);
        exampleFlow.createCriteria().andIn("flowCode", flowCodes);
        List<RobotFlow> flowList = flowDao.selectByExample(exampleFlow);
        if (CollectionUtils.isNotEmpty(flowList)) {
            syncVO.setFlowList(flowList);
        }

        //流程步骤
        Example stepExample = new Example(RobotFlowStep.class);
        stepExample.createCriteria().andIn("flowCode", flowCodes);
        List<RobotFlowStep> flowStepList = flowStepDao.selectByExample(stepExample);
        if (CollectionUtils.isNotEmpty(flowStepList)) {
            syncVO.setFlowStepList(flowStepList);
        }

        //机器人流程步骤流程
        Example stepFlowExample = new Example(RobotFlowStepFlow.class);
        stepFlowExample.createCriteria().andIn("flowCode", flowCodes);
        List<RobotFlowStepFlow> flowStepFlowList = flowStepFlowDao.selectByExample(stepFlowExample);
        if (CollectionUtils.isNotEmpty(flowStepFlowList)) {
            syncVO.setFlowStepFlow(flowStepFlowList);
        }
        return rpaDesignProdService.syncInDesign(syncVO);
    }

    private void updateFlowData(RobotFlowDesignVO flowDesign) {
        String jsonData = flowDesign.getData();
        if (StringUtils.isBlank(jsonData)) {
            return;
        }
        JSONObject jsonObject = JSON.parseObject(jsonData);
        JSONArray cells = jsonObject.getJSONArray("cells");
        for (int i = 0; i < cells.size(); i++) {
            JSONObject json = cells.getJSONObject(i);
            String shape = json.getString("shape");
            if (!"vue-shape".equals(shape)) {
                continue;
            }
            JSONObject data = json.getJSONObject("data");
            if (data == null) {
                continue;
            }
            data.put("parentFlowCode", flowDesign.getFlowCode());
        }
        flowDesign.setData(JSON.toJSONString(jsonObject));
    }

    private void updateStatus(RobotFlowDesignVO flowDesign) {
        RobotFlowVO robotFlow = flowDao.getByFlowCode(flowDesign.getFlowCode());
        if (robotFlow.getTemplateType() == null || robotFlow.getTemplateType() == 0) {
            //非模板，则需要区分：引用模板、普通流程
            this.updateRobotApp(robotFlow.getFlowCode());
        } else {
            //是模板，则需要判断是否子流程模板
            String flowCode = this.getStepFlowCode(flowDesign.getFlowCode());

            //获取引用模板的流程
            List<RobotFlowTemplate> templates = flowTemplateDao.selectByTemplateFlowCode(flowCode);
            if (CollectionUtils.isEmpty(templates)) {
                return;
            }
            for (RobotFlowTemplate template : templates) {
                RobotFlowVO flow = flowDao.getByFlowCode(template.getFlowCode());
                if (flow == null) {
                    continue;
                }
                this.updateRobotApp(flow.getFlowCode());
            }
        }
    }

    private void addStepFlow(String flowCode, RobotFlowStepVO step) {
        //关联子流程
        if (!"subFlow".equals(step.getActionCode())) {
            return;
        }
        String subFlowCode = JSON.parseObject(step.getActionArgs()).getString("flowCode");
        if (StringUtils.isBlank(subFlowCode)) {
            return;
        }
        Session session = SecurityContext.currentUser();
        //步骤流程
        RobotFlowStepFlow stepFlow = flowStepFlowDao.selectByStepCode(flowCode, step.getStepCode());
        if (stepFlow == null) {
            stepFlow = new RobotFlowStepFlow();
            stepFlow.setFlowCode(flowCode);
            stepFlow.setStepCode(step.getStepCode());
            stepFlow.setChildFlowCode(subFlowCode);
            stepFlow.setCreateId((int) session.getId());
            stepFlow.setCreateTime(new Date());
            stepFlow.setUpdateId((int) session.getId());
            stepFlow.setUpdateTime(new Date());
            flowStepFlowDao.insertSelective(stepFlow);
        }
    }

    private void updateRobotApp(String flowCode) {
        //流程版本为运行中且没有未发布的版本
        RobotApp app = appDao.selectAppByFlowCode(flowCode);
        if (app == null) {
            return;
        }
        if (app.getStatus() == 1) {
            appDao.updateStatus(app.getAppCode());
        }
    }

    private String getStepFlowCode(String flowCode) {
        String stepFlowCode = this.listStepFlowByFlowCode(flowCode);
        if (StringUtils.isNotBlank(stepFlowCode) && !flowCode.equals(stepFlowCode)) {
            return this.listStepFlowByFlowCode(stepFlowCode);
        }
        return stepFlowCode;
    }

    private String listStepFlowByFlowCode(String flowCode) {
        Example example = new Example(RobotFlowStepFlow.class);
        example.createCriteria().andEqualTo("childFlowCode", flowCode);
        List<RobotFlowStepFlow> list = flowStepFlowDao.selectByExample(example);
        if (CollectionUtils.isEmpty(list)) {
            return flowCode;
        }
        return list.stream().map(vo -> vo.getFlowCode()).findFirst().get();
    }
}
