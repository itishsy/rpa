package com.seebon.rpa.service.impl.component;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Dict;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.seebon.common.utils.CovertUtil;
import com.seebon.common.utils.UUIDGenerator;
import com.seebon.rpa.BusinessException;
import com.seebon.rpa.auth.SecurityContext;
import com.seebon.rpa.auth.Session;
import com.seebon.rpa.entity.agent.dto.openApi.DeclareOfferExportParamsDTO;
import com.seebon.rpa.entity.robot.RobotFlow;
import com.seebon.rpa.entity.robot.RobotFlowStep;
import com.seebon.rpa.entity.robot.RobotTaskArgs;
import com.seebon.rpa.entity.robot.RobotTaskQueueItem;
import com.seebon.rpa.entity.robot.dto.RobotLoginAuthDTO;
import com.seebon.rpa.entity.robot.enums.LoginTypeEnum;
import com.seebon.rpa.entity.robot.enums.OfferLabelTypeEnum;
import com.seebon.rpa.entity.robot.enums.QueueItemTypeEnum;
import com.seebon.rpa.entity.robot.enums.ServiceItemTypeEnum;
import com.seebon.rpa.entity.robot.vo.CheckDeclareDataVO;
import com.seebon.rpa.entity.robot.vo.RobotFlowVO;
import com.seebon.rpa.entity.robot.vo.RobotTaskQueueVO;
import com.seebon.rpa.entity.robot.vo.RobotTaskVO;
import com.seebon.rpa.entity.system.User;
import com.seebon.rpa.remote.RpaSaasAgentService;
import com.seebon.rpa.repository.mongodb.RobotTaskQueueGenerateRepository;
import com.seebon.rpa.repository.mysql.RobotFlowDao;
import com.seebon.rpa.repository.mysql.RobotFlowStepDao;
import com.seebon.rpa.repository.mysql.RobotTaskArgsDao;
import com.seebon.rpa.repository.mysql.RobotTaskDao;
import com.seebon.rpa.service.RobotLoginAuthService;
import com.seebon.rpa.service.RobotTaskQueueService;
import com.seebon.rpa.utils.ConvertUtl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class RobotTaskQueueComponent {

    @Autowired
    private RobotTaskDao robotTaskDao;
    @Autowired
    private RobotFlowDao flowDao;
    @Autowired
    private RobotFlowStepDao flowStepDao;
    @Autowired
    private RpaSaasAgentService saasAgentService;
    @Autowired
    private RobotTaskQueueService robotTaskQueueService;
    @Autowired
    private RobotLoginAuthService robotLoginAuthService;
    @Autowired
    private RobotTaskQueueGenerateRepository queueGenerateRepository;
    @Autowired
    private RobotTaskArgsDao robotTaskArgsDao;


    public void generateQueue(Date nowDate) {
        generateQueue(nowDate,null,null);
    }

    public void generateQueue(Date nowDate,List<String> taskCodeList,List<String> appCodeList) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("status",1);
        params.put("loginTypeList", LoginTypeEnum.getValues());
        if (CollectionUtils.isNotEmpty(taskCodeList)) {
            params.put("taskCodeList",taskCodeList);
        }
        if (CollectionUtils.isNotEmpty(appCodeList)) {
            params.put("appCodeList",appCodeList);
        }
        List<RobotTaskVO> robotTasks = robotTaskDao.selectAllTask(params);
        if (CollectionUtils.isEmpty(robotTasks)) {
            log.info("自动任务队列为空.");
            return;
        }

        //待申报数据
        Map<String, Object> declareDataMap = this.getDeclareData(robotTasks);

        //待网站核验数据
        Map<String, Object> checkDataMap = this.getCheckData(robotTasks);

        Dict dict = Dict.create().set("source", 1);

        try {
            for (RobotTaskVO task : robotTasks) {
                //流程
                List<RobotFlowVO> flows = this.getFlows(task.getTaskCode(), Lists.newArrayList(), Boolean.TRUE);
                if (CollectionUtils.isEmpty(flows)) {
                    ConvertUtl.log(task, null, "流程为空,不生成自动任务.");
                    continue;
                }

                //待申报数据
                Map<String, Object> declareData = ConvertUtl.getDeclareData(task, declareDataMap);

                //待网站核验数据
                Map<String, Object> checkData = ConvertUtl.getDeclareData(task, checkDataMap);

                // 运行所有服务项目
                try {
                    allRunTask(nowDate, dict, task, flows, declareData, checkData);
                } catch (Exception e) {
                    queueGenerateRepository.save(task,flows,"执行生成任务异常："+e.getMessage(),null);
                    log.error("执行生成任务异常:",e);
                }
            }
        } finally {
            // 执行队列任务刷新
            robotTaskQueueService.updatePreTime(null,null);
        }
    }

    private void allRunTask(Date nowDate, Dict dict, RobotTaskVO task, List<RobotFlowVO> flows, Map<String, Object> declareData, Map<String, Object> checkData) {
        //优先执行数据
        //1：增减员（增员、减员、调基、补缴）
        this.generateDataTasks(task, flows, 1, nowDate, declareData, checkData, dict);

        //12：查审核结果
        this.generateDataTasks(task, flows, 12, nowDate, declareData, checkData, dict);

        //6：缴费
        this.generateDataTasks(task, flows, 6, nowDate, declareData, checkData, dict);

        //7：在册名单
        this.generateDataTasks(task, flows, 7, nowDate, declareData, checkData, dict);

        //8：费用明细
        this.generateDataTasks(task, flows, 8, nowDate, declareData, checkData, dict);

        //9：政策通知
        this.generateDataTasks(task, flows, 9, nowDate, declareData, checkData, dict);

        //10：基数申报
        this.generateDataTasks(task, flows, 10, nowDate, declareData, checkData, dict);
    }

    private void generateDataTasks(RobotTaskVO task, List<RobotFlowVO> flows, Integer queueItem, Date nowDate,
                                   Map<String, Object> declareData, Map<String, Object> checkData, Dict dict) {

        // 申报账户配置信息
        Example example = new Example(RobotTaskArgs.class);
        example.createCriteria().andEqualTo("taskCode",task.getTaskCode());
        List<RobotTaskArgs> robotTaskArgs = robotTaskArgsDao.selectByExample(example);

        List<RobotFlowVO> flowList = Lists.newArrayList();
        if (queueItem == 1 || queueItem == 12) {
            flowList = flows.stream().filter(vo -> Lists.newArrayList(1, 2, 3, 5).contains(vo.getServiceItem())).collect(Collectors.toList());
            if (queueItem == 1) {
                flowList = flowList.stream().filter(vo -> StringUtils.isBlank(vo.getTagCode()) || !"10018010".equals(vo.getTagCode())).collect(Collectors.toList());
            } else {
                flowList = flowList.stream().filter(vo -> StringUtils.isNotBlank(vo.getTagCode()) && "10018010".equals(vo.getTagCode())).collect(Collectors.toList());
            }
        } else if (queueItem == 11) {
            flowList = flows.stream().filter(vo -> "10018008".equals(vo.getTagCode())).collect(Collectors.toList());
        } else {
            flowList = flows.stream().filter(vo -> vo.getServiceItem().equals(queueItem)).collect(Collectors.toList());
        }
        if (CollectionUtils.isEmpty(flowList)) {
            ConvertUtl.log(task, null, "流程为空,不自动生成" + QueueItemTypeEnum.getNameByCode(queueItem) + "任务.");
            queueGenerateRepository.save(task,flows,queueItem,"流程为空，不自动生成",dict);
            return;
        }
        Map<String, List<RobotFlowVO>> declareSystemMap = flowList.parallelStream().collect(Collectors.groupingBy(RobotFlow::getDeclareSystem));
        for (String declareSystem : declareSystemMap.keySet()) {
            List<RobotTaskQueueItem> queueItemList = Lists.newArrayList();
            List<RobotFlowVO> values = declareSystemMap.get(declareSystem);

            // 是否H5登录
            boolean isH5Login = ConvertUtl.isH5Login(declareSystem, task.getTaskArgsList());

            // 临时处理不对H5登录入登录队列
            if (isH5Login && dict.getInt("source") == 1 && !isNeedGenerateH5Queue(task, queueItem, dict, values)) {
                continue;
            }

            //设置手机号
            String mobile = isH5Login ? getDeclareMobile(robotTaskArgs) : null;

            List<String> flowCodes = Lists.newArrayList();
            for (RobotFlowVO flow : values) {

                //1、如果在执行计划内生成任务，如果不在计划内都不生成任务
                if (nowDate != null) {  // 如果日期没有值，则不需要判断
                    boolean checkSchedule = ConvertUtl.checkSchedule(flow, nowDate);
                    if (!checkSchedule) {
                        ConvertUtl.log(task, flow, "执行计划于当前时间不匹配，不自动生成" + QueueItemTypeEnum.getNameByCode(queueItem) + "任务.");
                        queueGenerateRepository.save(task,Collections.singletonList(flow),queueItem,"执行计划于当前时间不匹配，不自动生成",dict);
                        continue;
                    }
                }

                //2、校验待申报数据、待审核数据
                if (queueItem == 1) {
                    boolean hasDeclareData = ConvertUtl.hasDeclareData(flow, declareData);
                    if (!hasDeclareData) {
                        ConvertUtl.log(task, flow, "没有待申报数据，不自动生成" + QueueItemTypeEnum.getNameByCode(queueItem) + "任务.");
                        queueGenerateRepository.save(task,Collections.singletonList(flow),queueItem,"没有待申报数据，不自动生成",dict);
                        continue;
                    }
                } else if (queueItem == 12) {
                    boolean hasCheckData = ConvertUtl.hasDeclareData(flow, checkData);
                    if (!hasCheckData) {
                        ConvertUtl.log(task, flow, "没有待审核数据，不自动生成" + QueueItemTypeEnum.getNameByCode(queueItem) + "任务.");
                        queueGenerateRepository.save(task,Collections.singletonList(flow),queueItem,"没有待审核数据，不自动生成",dict);
                        continue;
                    }
                }

                //3、校验是否有可申报数据
                Dict checkDeclareOffer = this.checkDeclareOffer(task, flow, queueItem, declareData, checkData);
                if (!checkDeclareOffer.getBool("isRunFlow")) {
                    ConvertUtl.log(task, flow, "没有可申报数据，不自动生成" + QueueItemTypeEnum.getNameByCode(queueItem) + "任务.");
                    queueGenerateRepository.save(task,Collections.singletonList(flow),queueItem,"没有可申报数据，不自动生成",dict);
                    continue;
                }


                //4、如果是H5登录，跳转只至H5校验处理
//                if (isH5Login) {
//                    // 针对H5登录，只生成待登录信息
//                    RobotLoginAuthDTO robotLoginAuthDTO = new RobotLoginAuthDTO();
//                    robotLoginAuthDTO.setClientId(task.getClientId());
//                    robotLoginAuthDTO.setDeclareSystem(declareSystem);
//                    robotLoginAuthDTO.setQueueItem(queueItem);
//                    robotLoginAuthDTO.setSource(dict.getInt("source"));
//                    robotLoginAuthDTO.setTaskCode(task.getTaskCode());
//                    robotLoginAuthDTO.setFlows(Collections.singletonList(flow));
//                    robotLoginAuthDTO.setCheckDeclareOffer(checkDeclareOffer);
//                    try {
//                        robotLoginAuthService.addLoginAuthOrAddQueue(robotLoginAuthDTO);
//                        ConvertUtl.log(task, flow, "H5登录，不自动生成" + QueueItemTypeEnum.getNameByCode(queueItem) + "任务.");
//                    } catch (Exception e) {
//                        ConvertUtl.log(task, flow, "入H5登录失败:" + e.getMessage());
//                        queueGenerateRepository.save(task,Collections.singletonList(flow),queueItem,"入H5登录失败:"+ e.getMessage(),dict);
//                    }
//                    continue;
//                }
                flowCodes.add(flow.getFlowCode());
                queueItemList.add(ConvertUtl.newTaskQueueItem(flow, checkDeclareOffer));
            }
            this.save(queueItemList, task, queueItem, declareSystem, dict, mobile, flowCodes);
        }
    }

    // 针对H5登录判断是否需要执行
    private boolean isNeedGenerateH5Queue(RobotTaskVO task, Integer queueItem, Dict dict, List<RobotFlowVO> values) {
        // H5登录的地区，任何时间不自动生成【在册】【费用】的任务，只能手动触发。
        if (queueItem == 7  || queueItem == 8 || queueItem == 9) {
            queueGenerateRepository.save(task,values,queueItem,"H5登录的地区，任何时间不自动生成【在册】【费用】的任务",dict);
            return false;
        }

        // 设置时间界限
        LocalDateTime now = LocalDateTime.now(); // 获取当前时间
        DayOfWeek dayOfWeek = now.getDayOfWeek();
        LocalTime currentTime = now.toLocalTime();
        // 如果是周末，则总是满足条件
        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            queueGenerateRepository.save(task,values,queueItem,"H5登录的地区，当前时间不生成队列任务",dict);
            return false;
        }

        // 设置时间范围
        LocalTime startTimeEvening = LocalTime.of(18, 0);
        LocalTime endTimeMorning = LocalTime.of(9, 0);
        LocalTime startTimeLunch = LocalTime.of(12, 0);
        LocalTime endTimeLunch = LocalTime.of(14, 0);

        // 检查当前时间是否在指定的时间段内
        boolean isTimeInRange =
                (currentTime.isAfter(startTimeEvening) || currentTime.isBefore(endTimeMorning)) ||
                        (currentTime.isAfter(startTimeLunch) && currentTime.isBefore(endTimeLunch));
        //H5登录的地区，18：00到次日9：00所有事项都不自动生成队列
        if (isTimeInRange) {
            // 在当前时间范围，不执行操作所有操作
            queueGenerateRepository.save(task,values,queueItem,"H5登录的地区，当前时间不生成队列任务",dict);
            return false;
        }
        return true;
    }

    /**
     * 获取经办人手机号
     */
    private String getDeclareMobile(List<RobotTaskArgs> robotTaskArgs) {
        String mobile = null;
        RobotTaskArgs mobileTaskArg = robotTaskArgs.parallelStream()
                .filter(x -> StringUtils.isNotEmpty(x.getArgsKey()) && (x.getArgsKey().toLowerCase().contains("mobile") || x.getArgsKey().toLowerCase().contains("phone")))
                .findFirst().orElse(null);
        if (mobileTaskArg != null) {
            mobile = mobileTaskArg.getArgsValue();
        }
        return mobile;
    }

    private void save(List<RobotTaskQueueItem> queueItemList, RobotTaskVO task, Integer queueItem, String declareSystem, Dict dict, String mobile, List<String> flowCodes) {
        if (CollectionUtils.isEmpty(queueItemList)) {
            return;
        }

        // 业务类型
        Integer businessType = 1;
        JSONObject appArgs = JSONObject.parseObject(task.getAppArgs());
        if ("1001002".equals(appArgs.getString("businessType"))) {
            businessType = 2;
        }

        RobotTaskQueueVO taskQueue = new RobotTaskQueueVO();
        taskQueue.setClientId(task.getClientId());
        taskQueue.setTaskCode(task.getTaskCode());
        taskQueue.setAppCode(task.getAppCode());
        taskQueue.setCompanyName(task.getOrgName());
        taskQueue.setDeclareAccount(task.getAccountNumber());
        taskQueue.setExecutionCode(UUIDGenerator.uuidStringWithoutLine());
        taskQueue.setQueueItem(queueItem);
        taskQueue.setBusinessType(businessType);
        taskQueue.setComment("2".equals(dict.getStr("source")) ? "手动触发" : "计划任务触发");
        taskQueue.setDeclareSystem(declareSystem);
        taskQueue.setLoginType(ConvertUtl.getLoginType(declareSystem, task.getTaskArgsList()));
        taskQueue.setSource(Optional.ofNullable(dict.getInt("source")).orElse(1));
        taskQueue.setTaskQueueItemList(queueItemList);
        taskQueue.setRefreshPreTime(Boolean.FALSE);
        taskQueue.setPhoneNumber(mobile);
        taskQueue.setFlowCodeList(flowCodes);
        try {
            if ("2".equals(dict.getStr("source"))) {
                // 手动生成任务，进行排序
                taskQueue.setRefreshPreTime(Boolean.TRUE);
                User user = SecurityContext.currentUser();
                taskQueue.setCreateId((int)user.getId());
                taskQueue.setCreateName(user.getName());
            }
        } catch (Exception ignored) {}

        try {
            Map<String, Object> addTaskQueueResultMap = robotTaskQueueService.addTaskQueue(taskQueue);
            queueGenerateRepository.save(task,null,queueItem,"调用入队列",addTaskQueueResultMap);
            if (addTaskQueueResultMap.get("code").toString().equals("500")) {
                throw new BusinessException(addTaskQueueResultMap.get("msg").toString());
            }
        }  catch (Exception e) {
            String s = UUIDGenerator.uuidStringWithoutLine();
            ConvertUtl.log(task, null, String.format("入队列异常(%s)：%s",s,e.getMessage()));
            log.error("入队列异常("+s+")：",e);
            queueGenerateRepository.save(task,null,queueItem,"入队列异常:"+ e.getMessage(),taskQueue);
        }
    }

    public Dict checkDeclareOffer(RobotTaskVO task, RobotFlowVO flow, Integer queueItem, Map<String, Object> declareMap, Map<String, Object> checkData) {
        Dict resultMap = Dict.create().set("isRunFlow", true);
        if (queueItem == 1) {
            String flowCode = StringUtils.defaultIfBlank(flow.getTemplateFlowCode(), flow.getFlowCode());
            //申报主流程
            RobotFlowStep flowStep = flowStepDao.getByActionArgs(flowCode, "generateOffer", "declareMonth");
            if (flowStep != null) {
                return this.checkDeclareData(resultMap, flowStep, queueItem, task, flow, declareMap);
            }
            //申报主流程
            flowStep = flowStepDao.getByTargetArgs(flowCode, "httpDownload", "\"method\":\"postDeclare\"");
            if (flowStep != null) {
                return this.checkDeclareData(resultMap, null, queueItem, task, flow, declareMap);
            }
        } else if (queueItem == 12) {
            //核验主流程
            RobotFlowStep flowStep = flowStepDao.getByActionArgs(flow.getFlowCode(), "generateVerify", "declareMonth");
            if (flowStep != null) {
                return this.checkDeclareData(resultMap, flowStep, queueItem, task, flow, checkData);
            } else {
                resultMap.put("isRunFlow", false);
            }
        }
        return resultMap;
    }

    private Dict checkDeclareData(Dict resultMap, RobotFlowStep flowStep, Integer queueItem, RobotTaskVO task, RobotFlowVO flowVO, Map<String, Object> declareMap) {
        JSONObject appArgs = JSONObject.parseObject(task.getAppArgs());
        DeclareOfferExportParamsDTO reqParams = new DeclareOfferExportParamsDTO();
        reqParams.setAddrName(appArgs.getString("addrName"));
        reqParams.setBusinessType(Integer.parseInt(ConvertUtl.getBusinessType(appArgs.getString("businessType"))));
        reqParams.setAccountNumber(task.getAccountNumber());
        reqParams.setTplTypeCode(flowVO.getDeclareSystem());
        reqParams.setTbDate(DateUtil.date().toString("yyyy-MM"));
        reqParams.setIsOnlyQuery(Boolean.TRUE);
        List<String> declareTypes = Lists.newArrayList();
        if (ConvertUtl.hasData(1, flowVO, declareMap, "addCount")) {
            declareTypes.add("1");
        }
        if (ConvertUtl.hasData(2, flowVO, declareMap, "stopCount")) {
            declareTypes.add("2");
        }
        if (ConvertUtl.hasData(3, flowVO, declareMap, "adjustCount")) {
            declareTypes.add("3");
        }
        if (ConvertUtl.hasData(5, flowVO, declareMap, "suppCount")) {
            declareTypes.add("5");
        }
        if (CollectionUtils.isNotEmpty(declareTypes)) {
            reqParams.setDeclareTypes(declareTypes.stream().collect(Collectors.joining(",")));
        }
        if ("10007007".equals(flowVO.getDeclareSystem())) {
            reqParams.setOperationType("1016");
        }
        if (flowStep != null) {
            JSONObject jsonObject = JSON.parseObject(flowStep.getActionArgs());
            String businessNode = jsonObject.getString("businessNode");
            if (StringUtils.isNotBlank(businessNode)) {
                reqParams.setOperationType(OfferLabelTypeEnum.getCode(businessNode));
            }
        }
        return checkOfferExport(task, queueItem, resultMap, reqParams, flowVO);
    }

    private Dict checkOfferExport(RobotTaskVO task, Integer queueItem, Dict resultMap, DeclareOfferExportParamsDTO reqParams, RobotFlowVO flow) {
        String msg = queueItem == 1 ? "未申报" : "未核验";
        ConvertUtl.log(task, flow, "查询" + msg + "数据: reqJson=" + JSON.toJSONString(reqParams));
        Map<String, Object> respMap = saasAgentService.checkOfferExport(reqParams);
        ConvertUtl.log(task, flow, "查询" + msg + "数据: respJson=" + JSON.toJSONString(respMap));
        if (MapUtils.isEmpty(respMap)) {
            resultMap.put("isRunFlow", false);
            return resultMap;
        }
        Object num = 0;
        Object fileSuffix = ".xlsx";//默认
        if (flow.getServiceItem() == 1) {//增员
            num = respMap.get("增员");
            fileSuffix = respMap.get("增员后缀");
        } else if (flow.getServiceItem() == 2) {
            num = respMap.get("减员");
            fileSuffix = respMap.get("减员后缀");
        } else if (flow.getServiceItem() == 3) {
            num = respMap.get("调基");
            fileSuffix = respMap.get("调基后缀");
        } else if (flow.getServiceItem() == 5) {
            num = respMap.get("补缴");
            fileSuffix = respMap.get("补缴后缀");
        }
        if (fileSuffix != null) {
            resultMap.put("fileSuffix", fileSuffix.toString());
        }
        if (num == null || Integer.parseInt(num.toString()) == 0) {
            resultMap.put("isRunFlow", false);
        }
        return resultMap;
    }

    private List<RobotFlowVO> getFlows(String taskCode, List<String> flowCodes, boolean checkScheduleStatus) {
        List<RobotFlowVO> appFlows = flowDao.findAppFlows(taskCode, flowCodes);
        if (CollectionUtils.isEmpty(appFlows)) {
            return Lists.newArrayList();
        }
        List<RobotFlowVO> taskFlows = flowDao.findTaskFlows(taskCode, flowCodes);
        if (CollectionUtils.isEmpty(taskFlows)) {
            return appFlows;
        }
        List<RobotFlowVO> flowList = Lists.newArrayList();
        Map<String, RobotFlowVO> taskFlowMap = taskFlows.stream().collect(Collectors.toMap(k -> k.getFlowCode(), v -> v, (x, y) -> x));
        for (RobotFlowVO appFlowVO : appFlows) {
            RobotFlowVO taskFlowVo = taskFlowMap.get(appFlowVO.getFlowCode());
            if (taskFlowVo != null) {
                if (checkScheduleStatus && taskFlowVo.getScheduleStatus() == 0) {
                    continue;
                }
                //替换流程私有计划
                appFlowVO.setExecCycle(taskFlowVo.getExecCycle());
                appFlowVO.setExecAreaTime(taskFlowVo.getExecAreaTime());
                appFlowVO.setExecStyle(taskFlowVo.getExecStyle());
            }
            flowList.add(appFlowVO);
        }
        return flowList;
    }

    private Map<String, Object> getDeclareData(List<RobotTaskVO> list) {
        CheckDeclareDataVO dataVO = ConvertUtl.getCheckDeclareDataVO(list);
        log.info("获取待申报数据-reqJson：" + JSON.toJSONString(dataVO));
        Map<String, Object> declareData = saasAgentService.getUnDeclareData(dataVO);
        log.info("获取待申报数据-respJson：" + JSON.toJSONString(declareData));
        return declareData;
    }

    private Map<String, Object> getCheckData(List<RobotTaskVO> list) {
        CheckDeclareDataVO dataVO = ConvertUtl.getCheckDeclareDataVO(list);
        log.info("获取待网站核验数据-reqJson：" + JSON.toJSONString(dataVO));
        Map<String, Object> declareDataResp = saasAgentService.getUnCheckData(dataVO);
        log.info("获取待网站核验数据-respJson：" + JSON.toJSONString(declareDataResp));
        return declareDataResp;
    }

    /**
     * @param source 0客户应用列表  1在册(不判断执行时间)
     */
    public void runTask(String taskCode, String machineCode, List<String> flowCodes, String serviceItemName, Integer source,Boolean checkSchedule) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("loginTypeList", LoginTypeEnum.getValues());
        params.put("taskCode", taskCode);
        List<RobotTaskVO> robotTasks = robotTaskDao.selectAllTask(params);
        if (CollectionUtils.isEmpty(robotTasks)) {
            throw new BusinessException("任务已停用，请核对信息.");
        }
        RobotTaskVO task = robotTasks.get(0);
        Date nowDate = null;
        // 重新确定，对手动执行的，不再基于执行计划时间限制
//        if (source == 0) {  // 在册处调用，未对该流程过滤
//            nowDate = new Date();
//        }

        Integer queueItem = ConvertUtl.getQueueItem(serviceItemName);
        //流程
        List<RobotFlowVO> flows = Lists.newArrayList();
        if ("登录".equals(serviceItemName)) {
            flows = this.getFlows(task.getTaskCode(), flowCodes, Boolean.FALSE);
        } else {
            flows = this.getFlows(task.getTaskCode(), Lists.newArrayList(), Boolean.FALSE);
        }
        if (CollectionUtils.isEmpty(flows)) {
            throw new BusinessException("流程为空,不生成自动任务.");
        }

        try {
            if (queueItem == null) {

                // 是否需要判断执行计划
                isCheckSchedule(checkSchedule, flows);

                //待申报数据
                Map<String, Object> declareData = ConvertUtl.getDeclareData(task, this.getDeclareData(robotTasks));
                //待网站核验数据
                Map<String, Object> checkData = ConvertUtl.getDeclareData(task, this.getCheckData(robotTasks));
                // 运行所有服务项目
                allRunTask(nowDate, Dict.create().set("source", 2), task, flows, declareData, checkData);

            } else if (queueItem == 1) {
                //待申报数据
                Map<String, Object> declareData = ConvertUtl.getDeclareData(task, this.getDeclareData(robotTasks));
                //待网站核验数据
                Map<String, Object> checkData = ConvertUtl.getDeclareData(task, this.getCheckData(robotTasks));

                List<RobotFlowVO> flowList = flows.stream().filter(vo -> ServiceItemTypeEnum.getCodeByName(serviceItemName).equals(vo.getServiceItem())).collect(Collectors.toList());
                if (CollectionUtils.isEmpty(flowList)) {
                    throw new BusinessException("流程为空,不再生成任务.");
                }

                // 是否需要判断执行计划
                isCheckSchedule(checkSchedule, flowList);

                //1：增减员（增员、减员、调基、补缴）
                this.generateDataTasks(task, flowList, 1,nowDate, declareData, checkData,Dict.create().set("source", 2));

                //12：查审核结果
                this.generateDataTasks(task, flowList, 12,nowDate, declareData, checkData,Dict.create().set("source", 2));

            }

            //6：缴费\7：在册名单\8：费用明细\9：政策通知\10：基数申报\11:登录
            if ("缴费".equals(serviceItemName) || "在册名单".equals(serviceItemName) || "费用明细".equals(serviceItemName) ||
                    "政策通知".equals(serviceItemName) || "基数申报".equals(serviceItemName) || "登录".equals(serviceItemName)) {
                List<RobotFlowVO> flowList = flows;
                if (!"登录".equals(serviceItemName)) {
                    flowList = flows.stream().filter(vo -> vo.getServiceItem().equals(queueItem)).collect(Collectors.toList());
                    if (CollectionUtils.isEmpty(flowList)) {
                        throw new BusinessException("流程为空,不再生成任务.");
                    }
                }

                // 是否需要判断执行计划
                isCheckSchedule(checkSchedule, flowList);

                // 执行队列
                this.generateDataTasks(task, flows, QueueItemTypeEnum.getByCodeName(serviceItemName), nowDate, null, null, Dict.create().set("source", 2));
            }
        } catch (Exception e) {
            log.error("生成任务异常：",e);
            if (source == 1 && !(e instanceof BusinessException)) {
                // 在册处理
                throw new BusinessException("流程配置有误，请稍后再执行");
            }
            throw e;
        } finally {
            // 执行队列任务刷新
//            robotTaskQueueService.updatePreTime(task.getClientId());
        }
    }


    /**
     * 是否需要判断执行计划
     */
    private void isCheckSchedule(Boolean checkSchedule, List<RobotFlowVO> flowList) {
        if (checkSchedule == null || checkSchedule) {
            for(RobotFlowVO flow : flowList) {
                if (!ConvertUtl.checkSchedule(flow, new Date())) {
                    throw new BusinessException("该任务不在执行计划期内，确定执行?","该任务不在执行计划期内");
                }
            }
        }
    }

}
