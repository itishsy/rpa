package com.seebon.rpa.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Dict;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.seebon.common.utils.UUIDGenerator;
import com.seebon.rpa.BusinessException;
import com.seebon.rpa.auth.SecurityContext;
import com.seebon.rpa.auth.Session;
import com.seebon.rpa.entity.IgGridDefaultPage;
import com.seebon.rpa.entity.agent.dto.openApi.DeclareOfferExportParamsDTO;
import com.seebon.rpa.entity.agent.enums.BusinessTypeEnum;
import com.seebon.rpa.entity.agent.enums.TplTypeEnum;
import com.seebon.rpa.entity.robot.*;
import com.seebon.rpa.entity.robot.dto.ChangeLoginStatusDTO;
import com.seebon.rpa.entity.robot.dto.EmployeeChangeTrackDTO;
import com.seebon.rpa.entity.robot.dto.GenerateQueueDTO;
import com.seebon.rpa.entity.robot.dto.RobotLoginAuthDTO;
import com.seebon.rpa.entity.robot.dto.design.RobotTaskQueueGenerate;
import com.seebon.rpa.entity.robot.enums.*;
import com.seebon.rpa.entity.robot.vo.*;
import com.seebon.rpa.entity.saas.vo.system.SysUserDeclareAccountVO;
import com.seebon.rpa.entity.system.User;
import com.seebon.rpa.remote.RpaSaasAgentService;
import com.seebon.rpa.remote.RpaSaasService;
import com.seebon.rpa.repository.mongodb.RobotTaskQueueGenerateRepository;
import com.seebon.rpa.repository.mysql.*;
import com.seebon.rpa.repository.redis.LoginAuthDao;
import com.seebon.rpa.service.RobotLoginAuthService;
import com.seebon.rpa.service.RobotTaskQueueService;
import com.seebon.rpa.utils.ConvertUtl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RobotLoginAuthServiceImpl implements RobotLoginAuthService {

    @Autowired
    private RobotLoginAuthDao loginAuthDao;
    @Autowired
    private RobotTaskDao robotTaskDao;
    @Autowired
    private RpaSaasService rpaSaasService;
    @Autowired
    private RobotClientDao robotClientDao;
    @Autowired
    private RobotTaskQueueService robotTaskQueueService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private RobotFlowDao flowDao;
    @Autowired
    private RpaSaasAgentService saasAgentService;
    @Autowired
    private LoginAuthDao loginAuthRedisDao;
    @Autowired
    private RobotFlowStepDao flowStepDao;
    @Autowired
    private RobotTaskSessionKeepDao taskSessionKeepDao;
    @Autowired
    private RobotTaskSessionKeepDao sessionKeepDao;
    @Autowired
    private RobotTaskQueueGenerateRepository queueGenerateRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RobotLoginAuthVO addLoginAuthOrAddQueue(RobotLoginAuthDTO loginAuthDTO) {

        if (StringUtils.isEmpty(loginAuthDTO.getTaskCode())) {
            throw new BusinessException("任务编码 不能为空");
        } else if (StringUtils.isEmpty(loginAuthDTO.getDeclareSystem())) {
            throw new BusinessException("申报系统 不能为空");
        } else if (loginAuthDTO.getQueueItem() == null) {
            throw new BusinessException("任务编码 不能为空");
        }

        // 根据taskCode查申报账户信息
        HashMap<String, Object> params2 = Maps.newHashMap();
        params2.put("taskCode", loginAuthDTO.getTaskCode());
        List<RobotTaskVO> robotTaskVOS = robotTaskDao.selectAllTask(params2);
        if (CollectionUtils.isEmpty(robotTaskVOS)) {
            throw new BusinessException("申报账户信息不存在");
        }

        // 获取流程
        RobotTaskVO robotTaskVO = robotTaskVOS.get(0);
        List<RobotFlowVO> flows = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(loginAuthDTO.getFlows())) {
            flows = loginAuthDTO.getFlows();
        } else {
            flows = getRobotFlowVOS(robotTaskVO, flows, loginAuthDTO.getQueueItem(), loginAuthDTO.getDeclareSystem()).parallelStream().filter(x -> x.getDeclareSystem()
                    .equals(loginAuthDTO.getDeclareSystem())).collect(Collectors.toList());
        }
        // 判断流程是否为空，为空则不生成
        if (CollectionUtils.isEmpty(flows)) {
            ConvertUtl.log(robotTaskVO, null, "流程为空,不生成任务.");
            throw new BusinessException("流程为空,不生成任务");
        }

        // 是否H5登录
        boolean isH5Login = this.isH5Login(loginAuthDTO.getDeclareSystem(), robotTaskVO.getTaskArgsList());

        if (isH5Login && loginAuthDTO.getDetailSource() != null && loginAuthDTO.getDetailSource() == 1 ) {

            // 设置时间界限
            LocalDateTime now = LocalDateTime.now(); // 获取当前时间
            DayOfWeek dayOfWeek = now.getDayOfWeek();
            LocalTime currentTime = now.toLocalTime();
            // 如果是周末，则总是满足条件
            if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
                queueGenerateRepository.save(robotTaskVO,flows,loginAuthDTO.getQueueItem(),"H5登录的地区，当前时间不生成队列任务",now);
                return null;
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
                queueGenerateRepository.save(robotTaskVO,flows,loginAuthDTO.getQueueItem(),"H5登录的地区，当前时间不生成队列任务",now);
                return null;
            }
        }

        // 是否需要校验规则
        if (loginAuthDTO.getIsVerifyRule() != null && loginAuthDTO.getIsVerifyRule()) {
            // 校验业务逻辑
            verifyRule(loginAuthDTO, robotTaskVOS, robotTaskVO, flows);
        }
        loginAuthDTO.setFlows(flows);
        RobotTaskQueue robotTaskQueue = toAddQueueByTask(robotTaskVO, loginAuthDTO);
        RobotLoginAuthVO robotLoginAuth = new RobotLoginAuthVO();
        robotLoginAuth.setRobotTaskQueue(robotTaskQueue);
        return robotLoginAuth;


        // 校验是否h5登录
//        if (!isH5Login) {
//            // 调用入队列
//            log.info("非H5登录,直接进队列");
//            loginAuthDTO.setFlows(flows);
//            RobotTaskQueue robotTaskQueue = toAddQueueByTask(robotTaskVO, loginAuthDTO);
//            RobotLoginAuthVO robotLoginAuth = new RobotLoginAuthVO();
//            robotLoginAuth.setRobotTaskQueue(robotTaskQueue);
//            return robotLoginAuth;
//        }
//
//
//        // session共享时存在已维持，则默认进队列，不需要生成登录信息
////        RobotTaskSessionKeep robotTaskSessionKeep = new RobotTaskSessionKeep();
////        robotTaskSessionKeep.setDeclareSystem(loginAuthDTO.getDeclareSystem());
////        robotTaskSessionKeep.setClientId(loginAuthDTO.getClientId());
////        robotTaskSessionKeep.setTaskCode(loginAuthDTO.getTaskCode());
////        robotTaskSessionKeep.setStatus(3);
//        Example example = new Example(RobotTaskSessionKeep.class);
//        example.createCriteria()
//                .andEqualTo("declareSystem",loginAuthDTO.getDeclareSystem())
//                .andEqualTo("clientId",loginAuthDTO.getClientId())
//                .andEqualTo("taskCode",loginAuthDTO.getTaskCode())
//                .andEqualTo("status",3)
//                ;
//        int keepSessionCount = sessionKeepDao.selectCountByExample(example);
//        if(keepSessionCount > 0) {
//            // 调用入队列
//            log.info("共享session,直接进队列");
//            loginAuthDTO.setFlows(flows);
//            RobotTaskQueue robotTaskQueue = toAddQueueByTask(robotTaskVO, loginAuthDTO);
//            RobotLoginAuthVO robotLoginAuth = new RobotLoginAuthVO();
//            robotLoginAuth.setRobotTaskQueue(robotTaskQueue);
//            return robotLoginAuth;
//        }
//
//        // 查询登录信息
//        Map<String, Object> params = Maps.newHashMap();
//        params.put("declareSystem", loginAuthDTO.getDeclareSystem());
//        params.put("taskCode", loginAuthDTO.getTaskCode());
//        params.put("queueItem", loginAuthDTO.getQueueItem());
//        params.put("loginStatus", LoginAuthLoginStatusEnum.LOGIN_MAINTAIN.getStatus());
//        List<RobotLoginAuthVO> loginAuthVOList = loginAuthDao.selectVOByParams(params);
//
//        // 判断是否已经登录
//        if (CollectionUtils.isNotEmpty(loginAuthVOList)) {
//            RobotLoginAuthVO authVO = loginAuthVOList.get(0);
//            // 重新生成执行编号
//            authVO.setExecutionCode(UUIDGenerator.uuidStringWithoutLine());
//            // 调用入队列
//            log.info("已经登录直接进队列：executionCode-" + authVO.getExecutionCode() + ";地区：" + loginAuthVOList.get(0).getAddrName());
//            RobotTaskQueue robotTaskQueue = toAddQueue(robotTaskVO, authVO, Boolean.FALSE);
//            authVO.setRobotTaskQueue(robotTaskQueue);
//            queueGenerateRepository.save(robotTaskVO,flows,authVO.getQueueItem(),"已经登录直接进队列",authVO);
//            return authVO;
//        }
//
//        // 存在未登录记录 ？
//        params.remove("loginStatus");
//        params.put("processStatus", LoginAuthProcessStatusEnum.NO_LOGIN.getStatus());
//        params.put("loginStatus", LoginAuthLoginStatusEnum.NO_LOGIN.getStatus());
//        loginAuthVOList = loginAuthDao.selectVOByParams(params);
//        if (CollectionUtils.isNotEmpty(loginAuthVOList)) {
//            queueGenerateRepository.save(robotTaskVO,null,loginAuthDTO.getQueueItem(),"H5已存在待登录记录",loginAuthVOList);
//            return loginAuthVOList.get(0);
//        }
//
//        // 生成H5集中登录信息
//        return generateLoginAuth(robotTaskVO, loginAuthDTO);
    }

    private void verifyRule(RobotLoginAuthDTO loginAuthDTO, List<RobotTaskVO> robotTaskVOS, RobotTaskVO robotTaskVO, List<RobotFlowVO> flows) {
        RobotFlowVO flowVO = flows.get(0);

        // 校验规则
        CheckDeclareDataVO dataVO = ConvertUtl.getCheckDeclareDataVO(robotTaskVOS);
        Map<String, Object> declareDataMap = saasAgentService.getUnDeclareData(dataVO);
        Map<String, Object> checkDataMap = saasAgentService.getUnCheckData(dataVO);

        //待申报数据
        Map<String, Object> declareData = ConvertUtl.getDeclareData(robotTaskVO, declareDataMap);

        //待网站核验数据
        Map<String, Object> checkData = ConvertUtl.getDeclareData(robotTaskVO, checkDataMap);
        Integer queueItem = loginAuthDTO.getQueueItem();

        if (queueItem == 1) {
            boolean hasDeclareData = ConvertUtl.hasDeclareData(flowVO, declareData);
            if (!hasDeclareData) {
                ConvertUtl.log(robotTaskVO, flowVO, "没有待申报数据，不生成" + QueueItemTypeEnum.getNameByCode(queueItem) + "任务.");
                throw new BusinessException("没有待申报数据，不生成任务");
            }
        } else if (queueItem == 12) {
            boolean hasCheckData = ConvertUtl.hasDeclareData(flowVO, checkData);
            if (!hasCheckData) {
                ConvertUtl.log(robotTaskVO, flowVO, "没有待审核数据，不生成" + QueueItemTypeEnum.getNameByCode(queueItem) + "任务.");
                throw new BusinessException("没有待审核数据，不生成任务");
            }
        }
    }


    private List<RobotFlowVO> getRobotFlowVOS(RobotTaskVO robotTaskVO, List<RobotFlowVO> flows, Integer queueItem, String declareSystem) {
        List<String> flowCodes = Lists.newArrayList();
        flows = Optional.ofNullable(flows).orElse(Lists.newArrayList());
        if (queueItem == 11) {
            Example example = new Example(RobotFlow.class);
            Example.Criteria criteria = example.createCriteria().andEqualTo("tagCode", "10018008")
                    .andEqualTo("appCode", robotTaskVO.getAppCode());
            if (StringUtils.isNotEmpty(declareSystem)) {
                criteria.andEqualTo("declareSystem",declareSystem);
            }
            flowCodes = flowDao.selectByExample(example).parallelStream().map(RobotFlow::getFlowCode).collect(Collectors.toList());
        }
        List<RobotFlowVO> appFlows = flowDao.findAppFlows(robotTaskVO.getTaskCode(), flowCodes);
        if (CollectionUtils.isNotEmpty(appFlows)) {
            List<RobotFlowVO> taskFlows = flowDao.findTaskFlows(robotTaskVO.getTaskCode(), Lists.newArrayList());
            if (CollectionUtils.isEmpty(taskFlows)) {
                flows = appFlows;
            } else {
                Map<String, RobotFlowVO> taskFlowMap = taskFlows.stream().collect(Collectors.toMap(k -> k.getFlowCode(), v -> v, (x, y) -> x));
                for (RobotFlowVO appFlowVO : appFlows) {
                    RobotFlowVO taskFlowVo = taskFlowMap.get(appFlowVO.getFlowCode());
                    if (taskFlowVo != null) {
                        if (taskFlowVo.getScheduleStatus() == 0) {
                            continue;
                        }
                        appFlowVO.setExecCycle(taskFlowVo.getExecCycle());
                        appFlowVO.setExecAreaTime(taskFlowVo.getExecAreaTime());
                        appFlowVO.setExecStyle(taskFlowVo.getExecStyle());
                    }
                    flows.add(appFlowVO);
                }
            }
        }
        if (queueItem == null) return flows;
        // 过滤事项
        if (queueItem == 1 || queueItem == 12) {
            flows = flows.stream().filter(vo -> Lists.newArrayList(1, 2, 3, 5).contains(vo.getServiceItem())).collect(Collectors.toList());
            if (queueItem == 1) {
                flows = flows.stream().filter(vo -> StringUtils.isBlank(vo.getTagCode()) || !"10018010".equals(vo.getTagCode())).collect(Collectors.toList());
            } else {
                flows = flows.stream().filter(vo -> StringUtils.isNotBlank(vo.getTagCode()) && "10018010".equals(vo.getTagCode())).collect(Collectors.toList());
            }
        } else if (queueItem == 11) {
            flows = flows.stream().filter(vo -> "10018008".equals(vo.getTagCode())).collect(Collectors.toList());
        } else {
            flows = flows.stream().filter(vo -> vo.getServiceItem().equals(queueItem)).collect(Collectors.toList());
        }
        return flows;
    }


    public Dict checkDeclareOffer(RobotTaskVO task, RobotFlowVO flow, Integer queueItem) {
        Dict resultMap = Dict.create().set("isRunFlow", true);
        if (queueItem == 1) {
            String flowCode = StringUtils.defaultIfBlank(flow.getTemplateFlowCode(), flow.getFlowCode());
            //申报主流程
            RobotFlowStep flowStep = flowStepDao.getByActionArgs(flowCode, "generateOffer", "declareMonth");
            if (flowStep != null) {
                return this.checkDeclareData(resultMap, flowStep, queueItem, task, flow);
            }
            //申报主流程
            flowStep = flowStepDao.getByTargetArgs(flowCode, "httpDownload", "\"method\":\"postDeclare\"");
            if (flowStep != null) {
                return this.checkDeclareData(resultMap, null, queueItem, task, flow);
            }
        } else if (queueItem == 12) {
            //核验主流程
            RobotFlowStep flowStep = flowStepDao.getByActionArgs(flow.getFlowCode(), "generateVerify", "declareMonth");
            if (flowStep != null) {
                return this.checkDeclareData(resultMap, flowStep, queueItem, task, flow);
            }
        }
        return resultMap;
    }

    private Dict checkDeclareData(Dict resultMap, RobotFlowStep flowStep, Integer queueItem, RobotTaskVO task, RobotFlowVO flowVO) {
        JSONObject appArgs = JSONObject.parseObject(task.getAppArgs());
        DeclareOfferExportParamsDTO reqParams = new DeclareOfferExportParamsDTO();
        reqParams.setAddrName(appArgs.getString("addrName"));
        reqParams.setBusinessType(Integer.parseInt(ConvertUtl.getBusinessType(appArgs.getString("businessType"))));
        reqParams.setAccountNumber(task.getAccountNumber());
        reqParams.setTplTypeCode(flowVO.getDeclareSystem());
        reqParams.setTbDate(DateUtil.date().toString("yyyy-MM"));
        reqParams.setDeclareTypes(flowVO.getServiceItem().toString());
        reqParams.setIsOnlyQuery(Boolean.TRUE);
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

    private boolean isH5Login(String declareSystem, List<RobotTaskArgs> taskArgsList) {
        String loginType = this.getLoginType(declareSystem, taskArgsList);
        if (StringUtils.isNotBlank(loginType)) {
            boolean isH5Login = StringUtils.endsWithAny(loginType, "扫码登录", "短信登录", "验证码登录");
            log.info("登录方式：loginType=" + loginType + ",是否H5登录 isH5Login=" + isH5Login);
            return isH5Login;
        }
        return false;
    }

    private String getLoginType(String declareSystem, List<RobotTaskArgs> taskArgsList) {
        if (CollectionUtils.isEmpty(taskArgsList)) {
            return null;
        }
        String loginTypeValue = LoginTypeEnum.getValueByCode(declareSystem);
        Optional<RobotTaskArgs> first = taskArgsList.stream().filter(vo -> vo.getArgsKey().equals(loginTypeValue)).findFirst();
        return first.map(RobotTaskArgs::getArgsValue).orElse(null);
    }


    private RobotLoginAuthVO generateLoginAuth(RobotTaskVO task, RobotLoginAuthDTO loginAuthDTO) {

        // 获取手机号
        String mobile = null;
        RobotTaskArgs mobileTaskArg = task.getTaskArgsList().parallelStream()
                .filter(x -> StringUtils.isNotEmpty(x.getArgsKey()) && (x.getArgsKey().toLowerCase().contains("mobile") || x.getArgsKey().toLowerCase().contains("phone")))
                .findFirst().orElse(null);
        if (mobileTaskArg != null) {
            mobile = mobileTaskArg.getArgsValue();
        }
        // 获取登录方式
        String loginTypeValue = LoginTypeEnum.getValueByCode(loginAuthDTO.getDeclareSystem());
        String loginType = task.getTaskArgsList().parallelStream().filter(x -> x.getArgsKey().equals(loginTypeValue))
                .map(RobotTaskArgs::getArgsValue).findFirst().orElse("");

        Integer businessType = 1;
        JSONObject appArgs = JSONObject.parseObject(task.getAppArgs());
        if ("1001002".equals(appArgs.getString("businessType"))) {
            businessType = 2;
        }

        // 基于taskCode 生成登录记录
        Integer createId = 0;
        try{
            Session session = SecurityContext.currentUser();
            createId = Long.valueOf(session.getId()).intValue();
        } catch (Exception ignored) {}


        RobotLoginAuthVO robotLoginAuth = new RobotLoginAuthVO();
        robotLoginAuth.setAppArgs(task.getAppArgs());
        robotLoginAuth.setClientId(loginAuthDTO.getClientId());
        robotLoginAuth.setQueueItem(loginAuthDTO.getQueueItem());
        robotLoginAuth.setDeclareSystem(loginAuthDTO.getDeclareSystem());
        robotLoginAuth.setExecutionCode(UUIDGenerator.uuidStringWithoutLine());
        robotLoginAuth.setSource(loginAuthDTO.getSource());
        robotLoginAuth.setTaskCode(task.getTaskCode());
        robotLoginAuth.setMachineCode(task.getMachineCode());
        robotLoginAuth.setDeclareAccount(task.getOrgName() + "-" + task.getAccountNumber());
        robotLoginAuth.setLoginType(loginType);
        robotLoginAuth.setPhoneNumber(mobile);
        robotLoginAuth.setLoginStatus(LoginAuthLoginStatusEnum.NO_LOGIN.getStatus());
        robotLoginAuth.setProcessStatus(LoginAuthProcessStatusEnum.NO_LOGIN.getStatus());
        robotLoginAuth.setCreateId(createId);
        robotLoginAuth.setBusinessType(businessType);
        robotLoginAuth.setCreateTime(new Date());
        loginAuthDao.insertSelective(robotLoginAuth);
        queueGenerateRepository.save(task,null,loginAuthDTO.getQueueItem(),"H5生成待登录记录",robotLoginAuth);

        return robotLoginAuth;
    }

    @Override
    public IgGridDefaultPage<RobotLoginAuthVO> pageList(Map<String, Object> params) {

        User session = SecurityContext.currentUser();
        Integer userType = session.getUserType();
        if (userType != null && userType == 2) {// 客户
            List<SysUserDeclareAccountVO> userDeclareAccountList = rpaSaasService.getUserDeclareAccountList((int) session.getId(), null);
            if (CollectionUtils.isEmpty(userDeclareAccountList)) {
                return new IgGridDefaultPage<>(Lists.newArrayList(), 0);
            }
            params.put("userDeclareAccountList", userDeclareAccountList.parallelStream().map(x->x.getOrgName() + "-" + x.getAccountNumber()).collect(Collectors.toList()));
        }
        params.put("clientId", session.getCustId());

        Integer count = loginAuthDao.selectCountByParams(params);
        if (count == 0) {
            new IgGridDefaultPage<>(Lists.newArrayList(), count);
        }

        List<RobotLoginAuthVO> page = loginAuthDao.selectVOByParams(params);
        page.parallelStream().forEach(la -> {
            JSONObject appArgs = JSONObject.parseObject(la.getAppArgs());
            la.setAddrName(appArgs.getString("addrName"));
            la.setCustomerName(session.getCustName());
            String businessType = appArgs.getString("businessType");
            if ("1001001".equals(businessType)) {
                la.setBusinessTypeName("社保");
            } else if ("1001002".equals(businessType)) {
                la.setBusinessTypeName("公积金");
            }
            la.setDeclareSystemName(TplTypeEnum.getNameByCode(la.getDeclareSystem()));
            la.setQueueItemName(QueueItemTypeEnum.getNameByCode(la.getQueueItem()));
        });
        return new IgGridDefaultPage<>(page, count);
    }

    private RobotTaskQueue toAddQueueByTask(RobotTaskVO task, RobotLoginAuthDTO loginAuthDTO) {
        // 业务类型
        Integer businessType = 1;
        JSONObject appArgs = JSONObject.parseObject(task.getAppArgs());
        if ("1001002".equals(appArgs.getString("businessType"))) {
            businessType = 2;
        }
        // 获取手机号
        String mobile = null;
        RobotTaskArgs mobileTaskArg = task.getTaskArgsList().parallelStream()
                .filter(x -> StringUtils.isNotEmpty(x.getArgsKey()) && (x.getArgsKey().toLowerCase().contains("mobile") || x.getArgsKey().toLowerCase().contains("phone")))
                .findFirst().orElse(null);
        if (mobileTaskArg != null) {
            mobile = mobileTaskArg.getArgsValue();
        }

        Integer queueItem = loginAuthDTO.getQueueItem();
        List<RobotFlowVO> robotFlowVOS = loginAuthDTO.getFlows();
        if (CollectionUtils.isEmpty(robotFlowVOS)) {
            robotFlowVOS = getRobotFlowVOS(task, Lists.newArrayList(),queueItem, loginAuthDTO.getDeclareSystem());
        }
        List<String> flowCodeList = Lists.newArrayList();
        List<RobotTaskQueueItem> itemList = Lists.newArrayList();
        for (RobotFlowVO flow : robotFlowVOS) {
            if (!flow.getDeclareSystem().equals(loginAuthDTO.getDeclareSystem())) {
                continue;
            }
            Dict checkDeclareOffer = this.checkDeclareOffer(task, flow, queueItem);
            if (!checkDeclareOffer.getBool("isRunFlow")) {
                ConvertUtl.log(task, flow, "没有可申报数据，不生成" + QueueItemTypeEnum.getNameByCode(queueItem) + "任务.");
                continue ;
            }
            itemList.add(ConvertUtl.newTaskQueueItem(flow, checkDeclareOffer));
            flowCodeList.add(flow.getFlowCode());
        }

        if(CollectionUtils.isEmpty(itemList)) {
            throw new BusinessException("没有可执行流程");
        }

        RobotTaskQueueVO taskQueueVO = new RobotTaskQueueVO();
        taskQueueVO.setClientId(task.getClientId());
        taskQueueVO.setTaskCode(task.getTaskCode());
        taskQueueVO.setAppCode(task.getAppCode());
        taskQueueVO.setCompanyName(task.getOrgName());
        taskQueueVO.setDeclareAccount(task.getAccountNumber());
        taskQueueVO.setExecutionCode(UUIDGenerator.uuidStringWithoutLine());
        taskQueueVO.setQueueItem(loginAuthDTO.getQueueItem());
        taskQueueVO.setBusinessType(businessType);
        taskQueueVO.setComment("手动触发");
        taskQueueVO.setPhoneNumber(mobile);
        taskQueueVO.setDeclareSystem(loginAuthDTO.getDeclareSystem());
        taskQueueVO.setLoginType(ConvertUtl.getLoginType(loginAuthDTO.getDeclareSystem(), task.getTaskArgsList()));
        taskQueueVO.setSource(Optional.ofNullable(loginAuthDTO.getSource()).orElse(1));
        taskQueueVO.setTaskQueueItemList(itemList);
        taskQueueVO.setRefreshPreTime(Boolean.TRUE);
        taskQueueVO.setFlowCodeList(flowCodeList);
        try {
            User user = SecurityContext.currentUser();
            taskQueueVO.setCreateId((int)user.getId());
            taskQueueVO.setCreateName(user.getName());
        } catch (Exception ignored) {}
        try {
            Map<String, Object> addTaskQueueResultMap = robotTaskQueueService.addTaskQueue(taskQueueVO);
            queueGenerateRepository.save(task,null,loginAuthDTO.getQueueItem(),"非H5入队列",addTaskQueueResultMap);
            if (!addTaskQueueResultMap.get("code").toString().equals("500")) {
                return (RobotTaskQueue) addTaskQueueResultMap.get("queue");
            } else {
                throw new BusinessException(addTaskQueueResultMap.get("msg").toString());
            }
        } catch (Exception e) {
            queueGenerateRepository.save(task,null,loginAuthDTO.getQueueItem(),"非H5入队列异常:"+ e.getMessage(),taskQueueVO);
            throw e;
        }
    }

    private RobotTaskQueue toAddQueue(RobotTaskVO taskVO, RobotLoginAuthVO loginAuthVO, Boolean needUpdateExecCode) {

        // 获取手机号
        String mobile = null;
        RobotTaskArgs mobileTaskArg = taskVO.getTaskArgsList().parallelStream()
                .filter(x -> StringUtils.isNotEmpty(x.getArgsKey()) && (x.getArgsKey().toLowerCase().contains("mobile") || x.getArgsKey().toLowerCase().contains("phone")))
                .findFirst().orElse(null);
        if (mobileTaskArg != null) {
            mobile = mobileTaskArg.getArgsValue();
        }

        if (loginAuthVO.getId() != null && needUpdateExecCode) {
            // 重新设置执行编号
            updateExecCode(loginAuthVO,"重新生成");
        }
        // 生成队列明细
        Integer queueItem = loginAuthVO.getQueueItem();
        List<RobotFlowVO> robotFlowVOS = getRobotFlowVOS(taskVO, Lists.newArrayList(),queueItem, loginAuthVO.getDeclareSystem());
        List<RobotTaskQueueItem> itemList = Lists.newArrayList();
        List<String> flowCodeList = Lists.newArrayList();
        for (RobotFlowVO flow : robotFlowVOS) {
            boolean equals = flow.getDeclareSystem().equals(loginAuthVO.getDeclareSystem());
            if (!equals) {
                continue;
            }
            Dict checkDeclareOffer = this.checkDeclareOffer(taskVO, flow, queueItem);
            if (!checkDeclareOffer.getBool("isRunFlow")) {
                ConvertUtl.log(taskVO, flow, "没有可申报数据，不生成" + QueueItemTypeEnum.getNameByCode(queueItem) + "任务.");
                continue;
            }
            itemList.add(ConvertUtl.newTaskQueueItem(flow, checkDeclareOffer));
            flowCodeList.add(flow.getFlowCode());
        }

        if(CollectionUtils.isEmpty(itemList)) {
            throw new BusinessException("没有可执行流程");
        }

        // 调用入队列流程
        RobotTaskQueueVO taskQueueVO = new RobotTaskQueueVO();
        taskQueueVO.setClientId(loginAuthVO.getClientId());
        taskQueueVO.setExecutionCode(Optional.ofNullable(loginAuthVO.getExecutionCode()).orElse(UUIDGenerator.uuidStringWithoutLine()));
        taskQueueVO.setAppCode(loginAuthVO.getAppCode());
        taskQueueVO.setCompanyName(loginAuthVO.getCompanyName());
        taskQueueVO.setTaskCode(loginAuthVO.getTaskCode());
        taskQueueVO.setDeclareSystem(loginAuthVO.getDeclareSystem());
        taskQueueVO.setDeclareAccount(taskVO.getAccountNumber());
        taskQueueVO.setQueueItem(loginAuthVO.getQueueItem());
        taskQueueVO.setSource(loginAuthVO.getSource());
        taskQueueVO.setLoginType(loginAuthVO.getLoginType());
        taskQueueVO.setPhoneNumber(mobile);
        taskQueueVO.setBusinessType(loginAuthVO.getBusinessType());
        taskQueueVO.setTaskQueueItemList(itemList);
        taskQueueVO.setRefreshPreTime(Boolean.TRUE);
        taskQueueVO.setComment("登录验证触发");
        taskQueueVO.setFlowCodeList(flowCodeList);
        try {
            User user = SecurityContext.currentUser();
            taskQueueVO.setCreateId((int)user.getId());
            taskQueueVO.setCreateName(user.getName());
        } catch (Exception ignored) {}
        try {
            Map<String, Object> addTaskQueueResultMap = robotTaskQueueService.addTaskQueue(taskQueueVO);
            queueGenerateRepository.save(taskVO,null,loginAuthVO.getQueueItem(),"H5入队列",addTaskQueueResultMap);
            if (!addTaskQueueResultMap.get("code").toString().equals("500")) {
                return (RobotTaskQueue) addTaskQueueResultMap.get("queue");
            } else {
                throw new BusinessException(addTaskQueueResultMap.get("msg").toString());
            }
        } catch (Exception e) {
            queueGenerateRepository.save(taskVO,null,loginAuthVO.getQueueItem(),"H5入队列异常:"+ e.getMessage(),taskQueueVO);
            throw e;
        }
    }


    private static final String ROBOT_LOGIN_AUTH_ = "ROBOT_LOGIN_AUTH_%s";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean startLogin(Integer id) {

        Map<String , Object> params = Maps.newHashMap();
        params.put("id",id);
        List<RobotLoginAuthVO> loginAuthVOList = loginAuthDao.selectVOByParams(params);
        if (CollectionUtils.isEmpty(loginAuthVOList)) {
            throw new BusinessException("未查到登录验证信息");
        }
        RobotLoginAuthVO robotLoginAuth = loginAuthVOList.get(0);
        Integer processStatus = robotLoginAuth.getProcessStatus();
        if (LoginAuthProcessStatusEnum.IS_LOGIN.getStatus().equals(processStatus)) {
            throw new BusinessException("已登录记录，不能再次操作");
        }

        // 获取申报账户配置信息
        HashMap<String, Object> params2 = Maps.newHashMap();
        params2.put("taskCode", robotLoginAuth.getTaskCode());
        List<RobotTaskVO> robotTaskVOS = robotTaskDao.selectAllTask(params2);
        if (CollectionUtils.isEmpty(robotTaskVOS)) {
            throw new BusinessException("未找到申报账户配置信息");
        }
        RobotTaskVO robotTaskVO = robotTaskVOS.get(0);

        // 校验是否有存在可用盒子
        RobotTaskQueue robotTaskQueue = new RobotTaskQueue();
        robotTaskQueue.setClientId(robotLoginAuth.getClientId());
        robotTaskQueue.setAppCode(robotLoginAuth.getAppCode());
        robotTaskQueue.setTaskCode(robotLoginAuth.getTaskCode());
        robotTaskQueue.setDeclareSystem(robotLoginAuth.getDeclareSystem());
        robotTaskQueue.setQueueItem(robotLoginAuth.getQueueItem());
        robotTaskQueue.setSortRule(1); //当前默认为H5登录
        String machineCode = robotTaskQueueService.getMachineCode(robotTaskQueue);
        List<String> machineCodeList = Lists.newArrayList();
        if (StringUtils.isNotEmpty(machineCode)) {
            machineCodeList = Arrays.asList(machineCode.split(","));
        }
        int i = robotClientDao.countFreeRobotClient(robotLoginAuth.getClientId(),machineCodeList);
        if (i == 0) {
            throw new BusinessException("暂时无空闲设备执行此任务，请30分钟再试");
        }

        // 判断是否已经点击进入队列 : 同一个经办人或者同一个通知手机号码时，申报账户A登录成功后，才可以登录下一个申报账号，再点登录时进行提示
        String redisKey = String.format(ROBOT_LOGIN_AUTH_, robotLoginAuth.getPhoneNumber());
        Boolean b = redisTemplate.opsForValue().setIfAbsent(redisKey, id.toString(), 60 * 10, TimeUnit.SECONDS);
        if (b == null) {
            throw new BusinessException("操作失败，队列加锁失败");
        } else if (!b) {
            String s = redisTemplate.opsForValue().get(redisKey);
            RobotLoginAuth robotLoginAuth1 = loginAuthDao.selectByPrimaryKey(s);
            throw new BusinessException("当前存在申报账户正在登录，请登录完成之后再操作", robotLoginAuth1 == null ? "" : robotLoginAuth1.toString());
        }

        try {
            toAddQueue(robotTaskVO, robotLoginAuth, Boolean.TRUE);
            // 设置登录信息
            loginAuthRedisDao.setLoginProcessInfo(robotLoginAuth.getDeclareSystem(),robotLoginAuth.getAccountNumber(),
                    robotLoginAuth.getCompanyName(),LoginAuthLoginStatusEnum.NO_LOGIN.getStatus(),"未登录");
        } catch (Exception e) {
            // 已登录，调用入队列操作
            redisTemplate.delete(String.format(ROBOT_LOGIN_AUTH_, robotLoginAuth.getPhoneNumber()));
            throw e;
        }

        // 加队列
        return Boolean.TRUE;
    }

    @Override
    public Integer curUserNoLoginCount(Map<String, Object> params) {

        User session = SecurityContext.currentUser();
        Integer userType = session.getUserType();
        if (userType != null && userType == 2) {// 客户
            List<SysUserDeclareAccountVO> userDeclareAccountList = rpaSaasService.getUserDeclareAccountList((int) session.getId(), null);
            if (CollectionUtils.isEmpty(userDeclareAccountList)) {
                return 0;
            }
            params.put("userDeclareAccountList", userDeclareAccountList.parallelStream().map(x->x.getOrgName() + "-" + x.getAccountNumber()).collect(Collectors.toList()));
        }
        params.put("clientId", session.getCustId());
        params.put("processStatus", LoginAuthProcessStatusEnum.NO_LOGIN.getStatus());
        return loginAuthDao.selectCountByParams(params);
    }

    @Override
    public Boolean updateLoginStatus(ChangeLoginStatusDTO changeLoginStatusDTO) {

        Map<String, Object> params = Maps.newHashMap();
        params.put("executionCode", changeLoginStatusDTO.getExecutionCode());
        params.put("taskCode", changeLoginStatusDTO.getTaskCode());
        List<RobotLoginAuthVO> loginAuthVOList = loginAuthDao.selectVOByParams(params);
        if (CollectionUtils.isEmpty(loginAuthVOList)) {
            log.info("未查到登录验证信息");
            return Boolean.FALSE;
        }
        RobotLoginAuthVO authVO = loginAuthVOList.get(0);

        try {
            Session session = SecurityContext.currentUser();
            authVO.setUpdateId(Long.valueOf(session.getId()).intValue());
        } catch (Exception e) {
            log.info("设置更新人失败", e);
        }

        // 对于未登录需要推送至redis同步
        if (authVO.getLoginStatus().equals(LoginAuthLoginStatusEnum.NO_LOGIN.getStatus())) {
            // 设置登录信息
            loginAuthRedisDao.setLoginProcessInfo(authVO.getDeclareSystem(),authVO.getAccountNumber(),
                    authVO.getCompanyName(),changeLoginStatusDTO.getLoginStatus(),changeLoginStatusDTO.getComment());
        }

        // 删除登录锁
        redisTemplate.delete(String.format(ROBOT_LOGIN_AUTH_, authVO.getPhoneNumber()));

        if (changeLoginStatusDTO.getLoginStatus() == 1) {
            // 查询是否已维持登录
//            RobotTaskSessionKeep robotTaskSessionKeep = new RobotTaskSessionKeep();
//            robotTaskSessionKeep.setTaskCode(authVO.getTaskCode());
//            robotTaskSessionKeep.setDeclareSystem(authVO.getDeclareSystem());
//            robotTaskSessionKeep.setClientId(authVO.getClientId());
//            robotTaskSessionKeep.setStatus(3);
            Example example1 = new Example(RobotTaskSessionKeep.class);
            example1.createCriteria()
                    .andEqualTo("declareSystem",authVO.getDeclareSystem())
                    .andEqualTo("clientId",authVO.getClientId())
                    .andEqualTo("taskCode",authVO.getTaskCode())
                    .andEqualTo("status",3);
            Integer loginStatus = taskSessionKeepDao.selectCountByExample(example1) > 0
                    ? LoginAuthLoginStatusEnum.LOGIN_MAINTAIN.getStatus() : LoginAuthLoginStatusEnum.NO_LOGIN.getStatus();

            RobotLoginAuth robotLoginAuth = new RobotLoginAuth();
            robotLoginAuth.setProcessStatus(LoginAuthProcessStatusEnum.IS_LOGIN.getStatus());
            robotLoginAuth.setLoginStatus(loginStatus);
            robotLoginAuth.setLoginTime(new Date());
            robotLoginAuth.setComment(changeLoginStatusDTO.getComment());
            Example example = new Example(robotLoginAuth.getClass());
            example.createCriteria().andEqualTo("id",authVO.getId());
            // 更新登录验证信息
            loginAuthDao.updateByExampleSelective(robotLoginAuth,example);
        } else {
            // 更新执行编码
            updateExecCode(authVO,changeLoginStatusDTO.getComment());

        }
        return Boolean.TRUE;
    }

    private RobotLoginAuthVO updateExecCode(RobotLoginAuthVO authVO,String comment) {

        String executionCode = UUIDGenerator.uuidStringWithoutLine();
        // 修改执行编号
        RobotLoginAuth robotLoginAuth = new RobotLoginAuth();
        robotLoginAuth.setExecutionCode(executionCode);
        robotLoginAuth.setId(authVO.getId());
        loginAuthDao.updateByPrimaryKeySelective(robotLoginAuth);
        if (StringUtils.isNotEmpty(authVO.getExecutionCode())) {
            // 设置失败记录
            RobotLoginAuthVO updateAuthVO = new RobotLoginAuthVO();
            BeanUtils.copyProperties(authVO,updateAuthVO);
            updateAuthVO.setId(null);
            updateAuthVO.setProcessStatus(LoginAuthProcessStatusEnum.NO_LOGIN.getStatus());
            updateAuthVO.setLoginStatus(LoginAuthLoginStatusEnum.INVALID.getStatus());
            updateAuthVO.setLoginTime(new Date());
            updateAuthVO.setComment(comment);
            updateAuthVO.setIsDelete(1);
            loginAuthDao.insertSelective(updateAuthVO);
        }
        authVO.setExecutionCode(executionCode);
        return authVO;
    }

    private final static String keep_session_login_auth_key = "keep_session_login_auth_key:%s:%s";
    @Override
    public Boolean updateLoginStatus(String taskCode, String declareSystem, Integer clientId, Integer sessionKeepStatus) {
        log.info(String.format("session维持登录状态更新：taskCode:%s declareSystem:%s  clientId:%d  sessionKeepStatus:%d",taskCode,declareSystem,clientId,sessionKeepStatus));
        // Session维持回调
        if (sessionKeepStatus == 3) {
            String redisKey = String.format(keep_session_login_auth_key, taskCode, declareSystem);
            Boolean aBoolean = redisTemplate.opsForValue().setIfAbsent(redisKey, "1", 10, TimeUnit.MINUTES);
            if(aBoolean) {
                try {
                    // 更新维持状态
                    loginAuthDao.updateLoginStatus(clientId, taskCode, declareSystem, LoginAuthLoginStatusEnum.LOGIN_MAINTAIN.getStatus());

                    // 维持中 调用相同信息的数据入队列
                    HashMap<String, Object> params = Maps.newHashMap();
                    params.put("clientId",clientId);
                    params.put("declareSystem",declareSystem);
                    params.put("taskCode",taskCode);
                    params.put("processStatus" , LoginAuthProcessStatusEnum.NO_LOGIN.getStatus());
                    List<RobotLoginAuthVO> loginAuthVOList = loginAuthDao.selectVOByParams(params);
                    if (CollectionUtils.isEmpty(loginAuthVOList)) {
                        return Boolean.TRUE;
                    }
                    HashMap<String,Object> params2 = Maps.newHashMap();
                    params2.put("taskCode",taskCode);
                    List<RobotTaskVO> robotTaskVOS = robotTaskDao.selectAllTask(params2);
                    if (CollectionUtils.isEmpty(robotTaskVOS)) {
                        throw new BusinessException("申报账户信息不存在");
                    }
                    RobotTaskVO robotTaskVO = robotTaskVOS.get(0);
                    for (RobotLoginAuthVO authVO : loginAuthVOList) {
                        // 全部入队列
                        try {
                            // 加入队列
                            toAddQueue(robotTaskVO,authVO,Boolean.TRUE);

                            // 处理登录信息
                            RobotLoginAuth robotLoginAuth = new RobotLoginAuth();
                            robotLoginAuth.setProcessStatus(LoginAuthProcessStatusEnum.IS_LOGIN.getStatus());
                            robotLoginAuth.setLoginStatus(LoginAuthLoginStatusEnum.LOGIN_MAINTAIN.getStatus());
                            robotLoginAuth.setLoginTime(new Date());
                            Example example = new Example(robotLoginAuth.getClass());
                            example.createCriteria().andEqualTo("id",authVO.getId());
                            // 更新登录验证信息
                            loginAuthDao.updateByExampleSelective(robotLoginAuth,example);
                        } catch (Exception e) {
                            log.error("session维持通知处理登录信息异常：",e);
                        }
                    }
                } finally {
                    redisTemplate.delete(redisKey);
                }
            }

        } else {
            // 已失效 更新登录状态
            loginAuthDao.updateLoginStatus(clientId, taskCode, declareSystem, LoginAuthLoginStatusEnum.INVALID.getStatus());
        }

        return Boolean.TRUE;
    }

    @Override
    public Map<String, Object> checkLoginAuthInfo(String declareSystem, String accountNumber, String orgName, String phoneNumber) {
        return loginAuthRedisDao.getLoginProcessInfo(declareSystem,accountNumber,orgName);
    }

    @Override
    public List<RobotLoginAuthVO> queryList(Map<String, Object> map) {
        List<RobotLoginAuthVO> loginAuthVOList = loginAuthDao.selectVOByParams(map);
        loginAuthVOList.parallelStream().forEach(x-> x.setQueueItemName(QueueItemTypeEnum.getNameByCode(x.getQueueItem())));
        return loginAuthVOList;
    }

    @Override
    public GenerateQueueVO generateQueueOrLoginAuth(List<GenerateQueueDTO> generateQueueDTOList) {

        GenerateQueueVO generateQueueVO = new GenerateQueueVO();
        try {
            for (GenerateQueueDTO generateQueueDTO : generateQueueDTOList) {

                HashMap<String , Object> params = Maps.newHashMap();
                params.put("status",1);
                params.put("appArgs_businessType",BusinessTypeEnum.getEnumByCode(generateQueueDTO.getBusinessType()).getKey());
                params.put("appArgs_addrName",generateQueueDTO.getAddrName());
//            params.put("appName", generateQueueDTO.getAddrName().concat("-").concat(BusinessTypeEnum.getNameByCode(generateQueueDTO.getBusinessType())));
                List<RobotTaskVO> robotTaskVOS = robotTaskDao.selectAllTask(params);
                // 过滤符合业务和申报账户的数据
                List<RobotTaskVO> toAddTaskQueueList = robotTaskVOS.parallelStream().filter(taskVO -> {
                    JSONObject appArgs = JSONObject.parseObject(taskVO.getAppArgs());
                    String type = appArgs.getString("businessType");
                    String businessType = "1001001".equals(type) ? "1" : "2";
                    if (!businessType.equals(generateQueueDTO.getBusinessType().toString())) {
                        return false;
                    }
                    return generateQueueDTO.getAccountNumber().contains(taskVO.getAccountNumber());
                }).collect(Collectors.toList());

                // 根据任务查找对应流程并生成集中登录或进入队列
                Date nowDate = new Date();
                for (RobotTaskVO task : toAddTaskQueueList) {

                    // 查找任务对应流程
                    List<RobotFlowVO> robotFlowVOS = getRobotFlowVOS(task, Lists.newArrayList(), generateQueueDTO.getQueueItem(), null);

                    // 过滤不符合的流程
                    List<RobotFlowVO> filterToAddFlow = robotFlowVOS.parallelStream().filter(robotFlowVO ->
                                    robotFlowVO.getServiceItem().equals(generateQueueDTO.getChangeType())
                                            && ConvertUtl.checkSchedule(robotFlowVO, nowDate)
//                                && robotFlowVO.getScheduleStatus() == 1
                    ).collect(Collectors.toList());
                    if (CollectionUtils.isEmpty(filterToAddFlow)) {
                        continue;
                    }

                    // 根据流程的系统进行分组
                    Map<String, List<RobotFlowVO>> declareSystemMap = filterToAddFlow.parallelStream().collect(Collectors.groupingBy(vo -> vo.getDeclareSystem()));
                    for (String declareSystem : declareSystemMap.keySet()) {
                        List<RobotFlowVO> values = declareSystemMap.get(declareSystem);
                        RobotLoginAuthDTO robotLoginAuthDTO = new RobotLoginAuthDTO();
                        robotLoginAuthDTO.setClientId(task.getClientId());
                        robotLoginAuthDTO.setDeclareSystem(declareSystem);
                        robotLoginAuthDTO.setQueueItem(generateQueueDTO.getQueueItem());
                        robotLoginAuthDTO.setSource(generateQueueDTO.getSource());
                        robotLoginAuthDTO.setTaskCode(task.getTaskCode());
                        robotLoginAuthDTO.setIsVerifyRule(Boolean.TRUE); // 设置需要校验规则
                        robotLoginAuthDTO.setFlows(values);
                        robotLoginAuthDTO.setDetailSource(1);
                        // 添加到待登录或提交队列
                        RobotLoginAuthVO authVO = null;
                        try {
                            authVO = addLoginAuthOrAddQueue(robotLoginAuthDTO);
                            if (authVO != null) {
                                if (authVO.getRobotTaskQueue() != null) {
                                    generateQueueVO.getRobotTaskQueueS().add(authVO.getRobotTaskQueue());
                                } else {
                                    JSONObject appArgs = JSONObject.parseObject(authVO.getAppArgs());
                                    authVO.setAddrName(appArgs.getString("addrName"));
                                    authVO.setDeclareSystemName(TplTypeEnum.getNameByCode(authVO.getDeclareSystem()));
                                    authVO.setQueueItemName(QueueItemTypeEnum.getNameByCode(authVO.getQueueItem()));
                                    generateQueueVO.getRobotLoginAuthVOS().add(authVO);
                                }
                            }
                        } catch (Exception e) {
                            log.error("添加任务入登录、队列异常：",e);
                        }

                    }
                }

            }
        } catch (Exception e) {
            log.error("提交数据时任务入队列异常：",e);
        }
        return generateQueueVO;
    }

    @Override
    public RobotTrackVO queryEmployeeChangeTrackStatus(EmployeeChangeTrackDTO employeeChangeTrackDTO) {

        // 针对待提交状态不返回信息
        if(employeeChangeTrackDTO.getDeclareStatus() == 6) {
            return null;
        }

        if (StringUtils.isEmpty(employeeChangeTrackDTO.getTaskCode())) {
            // 查taskCode
            HashMap<String , Object> params = Maps.newHashMap();
            params.put("argsKey", BusinessTypeEnum.Social.getCode().equals(employeeChangeTrackDTO.getBusinessType()) ? "socialNumber":"accfundNumber");
            params.put("argsValue",employeeChangeTrackDTO.getDeclareAccount());
            params.put("appArgs_businessType",BusinessTypeEnum.getEnumByCode(employeeChangeTrackDTO.getBusinessType()).getKey());
            params.put("appArgs_addrName",employeeChangeTrackDTO.getAddrName());
//            params.put("appName", employeeChangeTrackDTO.getAddrName().concat("-").concat(BusinessTypeEnum.getNameByCode(employeeChangeTrackDTO.getBusinessType())));
            List<RobotTaskVO> robotTaskVOS = robotTaskDao.selectAllTask(params);
            if (CollectionUtils.isEmpty(robotTaskVOS)) {
                log.info("未找到taskCode");
                return null;
            }
            employeeChangeTrackDTO.setTaskCode(robotTaskVOS.get(0).getTaskCode());
        }
        return robotTaskQueueService.getTrackStatus(employeeChangeTrackDTO);
    }

    @Override
    public RobotTrackVO queryEmployeeChangeTrackInfo(EmployeeChangeTrackDTO employeeChangeTrackDTO) {

        // 针对待提交状态不返回信息
        if(employeeChangeTrackDTO.getDeclareStatus() == 6) {
            return null;
        }

        if (StringUtils.isEmpty(employeeChangeTrackDTO.getTaskCode())) {
            // 查taskCode
            HashMap<String , Object> params = Maps.newHashMap();
            params.put("argsKey", BusinessTypeEnum.Social.getCode().equals(employeeChangeTrackDTO.getBusinessType()) ? "socialNumber":"accfundNumber");
            params.put("argsValue",employeeChangeTrackDTO.getDeclareAccount());
            params.put("appArgs_businessType",BusinessTypeEnum.getEnumByCode(employeeChangeTrackDTO.getBusinessType()).getKey());
            params.put("appArgs_addrName",employeeChangeTrackDTO.getAddrName());
//            params.put("appName", employeeChangeTrackDTO.getAddrName().concat("-").concat(BusinessTypeEnum.getNameByCode(employeeChangeTrackDTO.getBusinessType())));
            List<RobotTaskVO> robotTaskVOS = robotTaskDao.selectAllTask(params);
            if (CollectionUtils.isEmpty(robotTaskVOS)) {
                log.info("未找到taskCode");
                return null;
            }
            employeeChangeTrackDTO.setTaskCode(robotTaskVOS.get(0).getTaskCode());
        }
        return robotTaskQueueService.getTrackList(employeeChangeTrackDTO);
    }

    @Override
    public List<RobotTaskQueueGenerate> findTaskQueueGenerate(String queryStr, Integer limit, String orderBy, String orderDirection) {
        return queueGenerateRepository.findByCustomQueryWithLimitAndOrder(queryStr, limit, orderBy, orderDirection);
    }

}
