package com.seebon.rpa.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Dict;
import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.seebon.common.utils.DateUtils;
import com.seebon.common.utils.JsonUtils;
import com.seebon.rpa.BusinessException;
import com.seebon.rpa.auth.SecurityContext;
import com.seebon.rpa.auth.Session;
import com.seebon.rpa.entity.IgGridDefaultPage;
import com.seebon.rpa.entity.auth.po.SysUser;
import com.seebon.rpa.entity.robot.*;
import com.seebon.rpa.entity.robot.dto.*;
import com.seebon.rpa.entity.robot.dto.design.*;
import com.seebon.rpa.entity.robot.dto.monitor.TodayExecDataDTO;
import com.seebon.rpa.entity.robot.dto.monitor.TodayExecQueryDTO;
import com.seebon.rpa.entity.robot.enums.RobotClientStatusEnum;
import com.seebon.rpa.entity.robot.vo.FileStoreVO;
import com.seebon.rpa.entity.robot.vo.design.RobotExecutionDetailMoVO;
import com.seebon.rpa.entity.robot.vo.design.RobotTaskVO1;
import com.seebon.rpa.entity.saas.DevUserAddr;
import com.seebon.rpa.entity.saas.po.declare.customer.CustomerBase;
import com.seebon.rpa.entity.saas.po.message.MessageList;
import com.seebon.rpa.entity.saas.vo.CustomerInsuredRegisterExVO;
import com.seebon.rpa.entity.saas.vo.message.MessageRuleConfigVO;
import com.seebon.rpa.entity.saas.vo.message.MessageRuleCustomerVO;
import com.seebon.rpa.entity.saas.vo.system.SysUserDeclareAccountVO;
import com.seebon.rpa.remote.RpaAuthService;
import com.seebon.rpa.remote.RpaSaasAgentService;
import com.seebon.rpa.remote.RpaSaasService;
import com.seebon.rpa.repository.mongodb.RobotExecutionRepository;
import com.seebon.rpa.repository.mysql.*;
import com.seebon.rpa.service.*;
import com.seebon.rpa.service.impl.component.RobotTaskQueueComponent;
import com.seebon.rpa.utils.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.persistence.Column;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;

@Slf4j
@Service
public class RobotClientAppServiceImpl implements RobotClientAppService {
    @Autowired
    private RobotClientAppDao robotClientAppDao;
    @Autowired
    private RobotExecutionDao robotExecutionDao;
    @Autowired
    private RobotAppDao robotAppDao;
    @Autowired
    private RobotFlowStepDao robotFlowStepDao;
    @Autowired
    private RobotClientTaskDao clientTaskDao;
    @Autowired
    private RobotTaskDao robotTaskDao;
    @Autowired
    private RobotTaskArgsDao robotTaskArgsDao;
    @Autowired
    private RobotClientDao robotClientDao;
    @Autowired
    private RobotCommandDao commandDao;
    @Autowired
    private RpaSaasService rpaSaasService;
    @Autowired
    private RpaAuthService rpaAuthService;
    @Autowired
    private RobotExecutionRepository robotExecutionRepository;
    @Autowired
    private RobotExecutionFileInfoDao robotExecutionFileInfoDao;
    @Autowired
    private RobotTaskScheduleDao robotTaskScheduleDao;
    @Autowired
    private RobotAppScheduleDao robotAppScheduleDao;
    @Autowired
    private RobotAppGeneralPlanDao robotAppGeneralPlanDao;
    @Autowired
    private RobotFlowDao robotFlowDao;
    @Autowired
    private RpaSaasAgentService saasAgentService;
    @Autowired
    private RpaSaasService saasService;
    @Autowired
    private RobotTaskService robotTaskService;
    @Autowired
    private RobotAppConditionDao conditionDao;
    @Autowired
    private RobotAccountStatusHistoryDao robotAccountStatusHistoryDao;
    @Autowired
    private RobotTaskNoticeDao robotTaskNoticeDao;
    @Autowired
    private RobotOfferRuleDao robotOfferRuleDao;
    @Autowired
    private CustomerCommandService customerCommandService;
    @Autowired
    private RobotComponentDao robotComponentDao;
    @Autowired
    private RobotTaskQueueService taskQueueService;
    @Autowired
    private RobotTaskQueueComponent taskQueueComponent;
    @Autowired
    private RobotLoginAuthService loginAuthService;

    @Override
    public IgGridDefaultPage<RobotClientAppVo> appList(Map<String, Object> map) {
        int count = robotClientAppDao.selectCountByParams(map);
        String keyWord = (String)map.get("keyWord");
        if (StringUtils.isNotBlank(keyWord)) {
            keyWord = keyWord.replaceAll("%", "\\\\%");
            map.put("keyWord", keyWord);
        }
        List<RobotClientAppVo> list = Lists.newArrayList();
        if (count > 0) {
            list = robotClientAppDao.selectAppListByClientId(map);
            if (CollectionUtils.isNotEmpty(list)) {
                List<CustomerBase> customerBases = rpaSaasService.listCustomer(false, "");
                Map<Integer, String> custMaps = Maps.newConcurrentMap();
                customerBases.stream().forEach(it -> {
                    custMaps.put(it.getId(), it.getName());
                });
                list.parallelStream().forEach(it ->{
                    it.setCustomerName(custMaps.get(it.getClientId()));
                    String appArgs = it.getAppArgs();
                    Integer declaredCount = 0;
                    Integer todayDeclaredCount = 0;
                    Integer noDeclaredCount = 0;
                    Integer auditCount = 0;
                    if (StringUtils.isNotBlank(appArgs)) {
                        Map<String, Object> objectMap = (Map<String, Object>)JsonUtils.jsonToBean(appArgs, Map.class);
                        if (!objectMap.isEmpty()) {
                            String businessType = (String)objectMap.get("businessType");
                            String addrId = (String)objectMap.get("addrId");
                            it.setBusinessType("1001001".equals(businessType)?1:2);
                            it.setAddrId(StringUtils.isNotBlank(addrId)?Integer.valueOf(addrId):null);
                            String accountNumber = it.getAccNumber();
                            if (StringUtils.isNotBlank(accountNumber)) {
                                Map<String, Integer> countMap = saasAgentService.selectDeclareCount("1001001".equals(businessType) ? 1 : 2, Integer.valueOf(addrId), accountNumber, "");
                                if (countMap != null && !countMap.isEmpty()) {
                                    declaredCount = countMap.get("declaredCount");
                                    todayDeclaredCount = countMap.get("todayDeclaredCount");
                                    noDeclaredCount = countMap.get("noDeclaredCount");
                                    auditCount = countMap.get("auditCount");
                                }
                            }
                        }
                    }

                    it.setDeclaredCount(declaredCount);
                    it.setTodayDeclaredCount(todayDeclaredCount);
                    it.setNoDeclaredCount(noDeclaredCount);
                    it.setAuditCount(auditCount);

                    String appCode = it.getAppCode();
                    String taskCode = it.getTaskCode();

                    if (StringUtils.isNotBlank(appCode) && StringUtils.isNotBlank(taskCode)) {

                        int execCount = robotExecutionRepository.getExecCount(it.getTaskCode(), null, DateUtils.dateToStr(new Date(), "yyyy-MM-dd"));
                        it.setExecCount(execCount);

                        List<RobotTaskScheduleDTO> scheduleDTOS = robotTaskScheduleDao.selectSchedules(appCode, taskCode, 0); // 拿私有
                        List<String> prvtFlowCodes = Lists.newArrayList();
                        if (CollectionUtils.isNotEmpty(scheduleDTOS)) {
                            prvtFlowCodes = scheduleDTOS.stream().map(item -> item.getFlowCode()).collect(Collectors.toList());
                        }
                        //那一次公共的除了私有外的
                        List<RobotTaskScheduleDTO> scheduleDTOS1 = robotTaskScheduleDao.selectCommonSchedules(appCode, taskCode, prvtFlowCodes, 0); // 拿通用
                        scheduleDTOS.addAll(scheduleDTOS1);
                        if (CollectionUtils.isNotEmpty(scheduleDTOS)) {
                            Optional<RobotTaskScheduleDTO> first = scheduleDTOS.stream().filter(item -> item.getTaskType() != null && item.getTaskType() == 2).findFirst();
                            if (first.isPresent()) {
                                it.setHaveCustom("是");
                            }else{
                                it.setHaveCustom("否");
                            }
                            List<String> days = Lists.newArrayList();
                            List<String> hours = Lists.newArrayList();
                            scheduleDTOS.stream().filter(item -> item.getExecStyle()==1 || item.getExecStyle()==2).forEach(item -> {
                                String execCycle = item.getExecCycle();
                                String[] split = execCycle.split("-");
                                days.addAll(Lists.newArrayList(split));
                                String execAreaTime = item.getExecAreaTime();
                                String[] split1 = null;
                                Integer execStyle = item.getExecStyle();
                                if (execStyle == 1) {
                                    split1 = execAreaTime.split("-");
                                } else {
                                    split1 = execAreaTime.split(",");
                                }
                                for (String hour : split1) {
                                    hours.add(hour.split(":")[0]);
                                }
                            });
                            if (CollectionUtils.isNotEmpty(days) && CollectionUtils.isNotEmpty(hours)) {
                                days.sort((o1,o2)->Integer.valueOf(o1)-Integer.valueOf(o2));
                                hours.sort((o1,o2)->Integer.valueOf(o1)-Integer.valueOf(o2));
                                it.setExecPlant("当月".concat(days.get(0)).concat("号-").concat(days.get(days.size()-1)).concat("号：").concat(hours.get(0)).concat(":00-").concat(hours.get(hours.size()-1)).concat(":00"));
                            }
                        }
                    }
                    if (StringUtils.isBlank(taskCode)) {
                        it.setTaskCode("");
                    }
                });
                list.sort(Comparator.comparing(RobotClientAppVo::getNoDeclaredTotal, Comparator.reverseOrder())
                        .thenComparing(RobotClientAppVo::getNoDeclaredNumber, Comparator.reverseOrder())
                        .thenComparing(RobotClientAppVo::getId, Comparator.reverseOrder()).thenComparing(RobotClientAppVo::getTaskCode, Comparator.reverseOrder()));
            }
        }
        return new IgGridDefaultPage<RobotClientAppVo>(list, count);
    }

    @Override
    public List<AppVersionDTO> findClientApps(String machineCode) {
        Integer clientId = SecurityContext.currentUser().getCustId();
        return robotClientAppDao.selectAppVersion(clientId, machineCode);
    }

    @Override
    public int saveTask(RobotTask clientTask) {
        return 0;
    }

    @Override
    public List<RobotClientApp> findAppDetail(String appCode, String version) {
        return null;
    }

    @Override
    public int addExecution(RobotClientExecutionVO executionVO) {
        RobotExecution execution = robotExecutionDao.existExecution(executionVO.getExecutionDetailVOS().get(0).getExecutionCode());
        List<RobotClientExecutionDetailVO> details = executionVO.getExecutionDetailVOS();
        if (execution != null) {//执行查询存在
            robotExecutionDao.deleteExecutionDetail(executionVO.getExecutionDetailVOS().get(0).getExecutionCode());
            details.forEach(executionDetail -> {
                robotExecutionDao.insertExecutionDetail(executionDetail);//添加执行明细
            });
            return robotExecutionDao.updateExecution(executionVO);//更新执行查询数据
        }
        details.forEach(detail -> {
            robotExecutionDao.insertExecutionDetail(detail);
        });
        return robotExecutionDao.insertExecution(executionVO);
    }

    @Override
    public List<ClientExecutionVO> appExecutions(String appCode) {
        return robotExecutionDao.appExecutions(appCode);
    }

    @Override
    public List<RobotExecutionDetailMoVO> appExecutionsDetail(String executionCode, String flowCode) {
        List<RobotExecutionDetailMo> detailList = robotExecutionRepository.listRobotExecutionDetail(executionCode, flowCode);
        if (CollectionUtils.isEmpty(detailList)) {
            return Lists.newArrayList();
        }
        List<String> stepCodes = detailList.stream().map(mo -> mo.getStepCode()).distinct().collect(Collectors.toList());
        List<String> flowCodes = detailList.stream().map(mo -> mo.getFlowCode()).distinct().collect(Collectors.toList());
        Map<String, Object> params = Maps.newHashMap();
        params.put("executionCode", executionCode);
        params.put("flowCodes", flowCodes);
        params.put("stepCodes", stepCodes);
        List<RobotExecutionFileInfo> files = robotFlowStepDao.selectByExecutionCode(params);
        Map<String, List<RobotExecutionFileInfo>> groupMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(files)) {
            groupMap = files.stream().collect(Collectors.groupingBy(file -> file.getExecutionCode() + "-" + file.getFlowCode() + "-" + file.getStepCode()));
        }
        List<RobotExecutionDetailMoVO> result = Lists.newArrayList();
        for (RobotExecutionDetailMo detailMo : detailList) {
            RobotExecutionDetailMoVO moVO = new RobotExecutionDetailMoVO();
            BeanUtils.copyProperties(detailMo, moVO);
            List<RobotExecutionFileInfo> pictures=groupMap.get(detailMo.getExecutionCode() + "-" + detailMo.getFlowCode() + "-" + detailMo.getStepCode());
            if(CollectionUtils.isNotEmpty(pictures)){
                pictures=pictures.stream().filter(e -> e.getFileFullPath()!=null&&e.getFileFullPath().endsWith(".png")||e.getFileFullPath().endsWith(".jpg")).collect(Collectors.toList());
            }
            moVO.setFiles(pictures);
            result.add(moVO);
        }
        return result;
    }

    @Override
    public Integer disableOrEnable(Integer status, Integer id) {
        return robotClientAppDao.updateStatus(status, id);
    }

    @Override
    public Integer removeApp(Integer id) {
        return robotClientAppDao.deleteByPrimaryKey(id);
    }

    @Override
    public Integer addApp(RobotClientApp clientApp) {
        List<String> version = robotAppDao.selectByAppCode(clientApp.getAppCode());
        List<String> numbers = new ArrayList<>();
        for (String s : version) {
            String substring = s.substring(5, s.length());
            numbers.add(substring);
        }
        String s = numbers.stream().max(Comparator.comparingInt(Integer::parseInt)).get();
        List<String> version2 = version.stream().filter(x -> {
            String substring = x.substring(5, x.length());
            return substring.equals(s);
        }).collect(Collectors.toList());

        clientApp.setStatus(0);
        clientApp.setVersion(version2.get(0));
        clientApp.setCreateTime(new Date());
        return robotClientAppDao.insertClientApp(clientApp);
    }

    @Override
    public RobotClientApp checkVersion(String appCode, String version) {
        Session session = SecurityContext.currentUser();
        List<RobotClientApp> robotClientApps = robotClientAppDao.selectTaskData(session.getCustId(), appCode);
        RobotClientApp robotClientApp = robotClientApps.stream().max((a, b) -> {
            String sa = a.getVersion().substring(5, a.getVersion().length());
            String sb = b.getVersion().substring(5, b.getVersion().length());
            return Integer.parseInt(sa) - Integer.parseInt(sb);
        }).get();
        if (version.equals(robotClientApp.getVersion())) {
            return null;
        }
        //机器人获取到数据不为NULL，新增一条到H2
        RobotClientApp robotClientApp2 = new RobotClientApp();
        BeanUtils.copyProperties(robotClientApp, robotClientApp2);
        robotClientApp2.setUpdateTime(new Date());
        robotClientAppDao.insertSelective(robotClientApp2);
        //同时将旧版本的数据状态改为2
        RobotClientApp robotClientApp3 = new RobotClientApp();
        robotClientApp3.setId(robotClientApp.getId());
        robotClientApp3.setStatus(2);
        robotClientAppDao.updateByPrimaryKeySelective(robotClientApp3);

        return robotClientApp;
    }

    @Override
    public List<RobotTask> listTask() {
        Example example = new Example(RobotTask.class);
        Integer custId = SecurityContext.currentUser().getCustId();
        example.createCriteria().andEqualTo("clientId", custId);
        return robotTaskDao.selectByExample(example);
    }

    @Override
    public RobotClientAppVo queryClientTask(String appCode, Integer clientId, String machineCode) {
        RobotClientAppVo robotClientAppVO = robotExecutionDao.selectExec(appCode, clientId, machineCode);
        CustomerBase customer = rpaSaasService.getCustomer(robotClientAppVO.getClientId());
        robotClientAppVO.setCustomerName(customer.getName());
        robotClientAppVO.setPlatform(customer.getPlatform());
        List<RobotTaskVO1> robotTaskVOS = robotClientAppVO.getRobotTaskVOS();
        List<RobotTaskVO1> collect = robotTaskVOS.stream().filter(s -> s.getTaskName() != null).collect(Collectors.toList());
        List<String> numbers = Lists.newArrayList("socialNumber", "accfundNumber");
        for (RobotTaskVO1 robotTaskVO : collect) {
            RobotTaskArgs taskArgs = robotTaskArgsDao.selectAccountNumber(robotTaskVO.getTaskCode(), numbers);
            robotTaskVO.setAccountNumber(taskArgs.getArgsValue());
        }
        robotClientAppVO.setRobotTaskVOS(collect);
        return robotClientAppVO;
    }

    @Override
    public List<CustomerBase> customerList() {
        return rpaSaasService.listCustomer(false, "1");
    }

    @Override
    public RobotTask editEcho(Integer id) {
        RobotTask robotTask = robotExecutionDao.selectTaskEdit(id);
        String[] split = robotTask.getExecCycle().split("-");
        String cycle = split[0] + "号-" + split[1] + "号";
        robotTask.setExecCycle(cycle);
        return robotTask;
    }

    @Override
    public int editTask(RobotTask robotTask) {
        Session session = SecurityContext.currentUser();
        robotTask.setEditName(session.getName());
        String[] split = robotTask.getExecCycle().split("-");
        String cycle = "当月" + split[0] + "号-" + split[1] + "号";
        if (robotTask.getExecStyle() == 1 || robotTask.getExecStyle() == 2) {
            //连续时间区间或者固定时间点
            String schedule = cycle + ": " + robotTask.getExecAreaTime();
            robotTask.setSchedule(schedule);
        }
        if (robotTask.getExecStyle() == 3) {
            //实时
            String schedule = cycle + ": 实时";
            robotTask.setSchedule(schedule);
            robotTaskDao.cleanAreaTime(robotTask.getId());
        }
        //更新同步状态
        RobotTask task = robotTaskDao.selectByPrimaryKey(robotTask.getId());
        robotTaskDao.cleanSyncTime(task.getTaskCode());
        return robotTaskDao.updateByPrimaryKeySelective(robotTask);
    }

    @Override
    public RobotClientAppVo appBasic(String appCode, Integer clientId) {
        RobotClientAppVo clientAppVO = robotExecutionDao.selectAppBasic(appCode, clientId);
        List<RobotTaskVO1> robotTaskVOS = clientAppVO.getRobotTaskVOS();
        Integer successTotal = 0;
        Integer failureTotal = 0;
        Integer dataNumberTotal = 0;
        Date lastExecTime = null;
        for (RobotTaskVO1 robotTaskVO : robotTaskVOS) {
            String taskCode = robotTaskVO.getTaskCode();
            Integer dataNumber = robotExecutionDao.selectDataNumber(taskCode);
            robotTaskVO.setDataNumber(dataNumber);
            successTotal += robotTaskVO.getSuccessNumber();
            failureTotal += robotTaskVO.getFailureNumber();
            dataNumberTotal += robotTaskVO.getDataNumber();
            lastExecTime = robotExecutionDao.selectLastExecTime(robotTaskVO.getAppCode(), clientId);
        }
        List<RobotTaskVO1> collect = robotTaskVOS.stream().filter(s -> s.getTaskName() != null).collect(Collectors.toList());
        clientAppVO.setRobotTaskVOS(collect);
        clientAppVO.setSuccessTotal(successTotal);
        clientAppVO.setFailureTotal(failureTotal);
        clientAppVO.setDataNumberTotal(dataNumberTotal);
        clientAppVO.setLastExecutionTime(lastExecTime);
        return clientAppVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int addClientApp(RobotClientAppVo appVO) {
        Example example = new Example(RobotApp.class);
        example.createCriteria().andEqualTo("appCode", appVO.getAppCode());
        RobotApp robotApp = robotAppDao.selectOneByExample(example);

        example = new Example(RobotClientApp.class);
        example.createCriteria().andEqualTo("machineCode", appVO.getMachineCode()).andEqualTo("appCode", appVO.getAppCode()).andEqualTo("clientId", appVO.getClientId());
        List<RobotClientApp> robotClientApps = robotClientAppDao.selectByExample(example);
        if (CollectionUtils.isNotEmpty(robotClientApps)) {
            throw new BusinessException("该盒子客户已存在此应用，请勿重复添加");
        }

        RobotClientApp clientApp = new RobotClientApp();
        clientApp.setStatus(1);
        clientApp.setVersion(robotApp.getVersion());
        clientApp.setAppCode(appVO.getAppCode());
        clientApp.setAppName(appVO.getAppName());
        clientApp.setClientId(appVO.getClientId());
        clientApp.setMachineCode(appVO.getMachineCode());
        clientApp.setCreateTime(new Date());
        clientApp.setUpdateTime(new Date());
        clientApp.setFixMachine(appVO.getFixMachine());
        clientApp.setFixRemark(appVO.getFixRemark());
        int i = robotClientAppDao.insertSelective(clientApp);
        try {
            CustomerBase customer = rpaSaasService.getCustomer(appVO.getClientId());
            if (customer != null && customer.getCategory() == 2) { // 独立部署的
                Dict dict = Dict.create().set("appCode", appVO.getAppCode());
                customerCommandService.addCustomerCommand(appVO.getClientId(), "app", JSON.toJSONString(dict), "分配应用");

                Map<String, Object> map = Maps.newHashMap();
                map.put("machineCode", appVO.getMachineCode());
                map.put("appCode", appVO.getAppCode());
                customerCommandService.addCustomerCommand(appVO.getClientId(), "machine", JSON.toJSONString(map), "分配盒子");

                String appArgs = robotApp.getAppArgs();
                JSONObject jsonObject = JSONObject.parseObject(appArgs);
                String addrId = jsonObject.getString("addrId");
                String businessType = jsonObject.getString("businessType");

                Example example1 = new Example(RobotOfferRule.class);
                example1.createCriteria().andEqualTo("addrId", addrId).andEqualTo("businessType", "1001001".equals(businessType)?1:2);
                List<RobotOfferRule> robotOfferRules = robotOfferRuleDao.selectByExample(example1);
                if (CollectionUtils.isNotEmpty(robotOfferRules)) {
                    robotOfferRules.stream().forEach(item ->{
                        Dict dict2 = Dict.create().set("ruleId", item.getId());
                        customerCommandService.addCustomerCommand(appVO.getClientId(), "offerRule", JSON.toJSONString(dict2), "回盘规则");
                    });
                }
                Example e = new Example(RobotComponent.class);
                e.createCriteria().andEqualTo("status", 1);
                List<RobotComponent> robotComponents = robotComponentDao.selectByExample(e);
                if (CollectionUtils.isNotEmpty(robotComponents)) {
                    robotComponents.stream().forEach(item -> {
                        JSONObject jsonObjects = new JSONObject();
                        jsonObjects.put("component", item.getComponent());
                        jsonObjects.put("type", "addComponent");
                        customerCommandService.addCustomerCommand(appVO.getClientId(), "component", JSON.toJSONString(jsonObjects), "");
                    });
                }
            }
        }catch (Exception e) {

        }
        return i;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int editRobotClientApp(RobotClientAppVo appVO) {
        RobotClientApp clientApp = new RobotClientApp();
        clientApp.setId(appVO.getId());
        clientApp.setUpdateTime(new Date());
        clientApp.setFixMachine(appVO.getFixMachine());
        clientApp.setFixRemark(appVO.getFixRemark());
        int i = robotClientAppDao.updateByPrimaryKeySelective(clientApp);
        return i;
    }

    @Override
    public List<RobotTaskVO1> getClientAppTask(RobotClientAppVo appVO) {
        //任务
        List<RobotTask> robotTasks = this.getRobotTask(appVO.getId());
        if (CollectionUtils.isEmpty(robotTasks)) {
            return Lists.newArrayList();
        }
        List<RobotTaskVO1> taskVOS = BeanCopyUtils.copyListProperties(robotTasks, RobotTaskVO1::new);
        for (RobotTaskVO1 taskVO : taskVOS) {
            RobotTaskArgs accountNumberArg = robotTaskArgsDao.selectAccountNumber(taskVO.getTaskCode(), Lists.newArrayList("socialNumber", "accfundNumber"));
            if (accountNumberArg != null) {
                taskVO.setAccountNumber(accountNumberArg.getArgsValue());
            }
            RobotTaskArgs companyNameArg = robotTaskArgsDao.selectAccountNumber(taskVO.getTaskCode(), Lists.newArrayList("companyName"));
            if (companyNameArg != null) {
                taskVO.setCompanyName(companyNameArg.getArgsValue());
            }
        }
        return taskVOS;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeRobotClientApp(RobotClientAppVo appVO) {
        //旧盒子所有任务
        List<RobotTask> robotTasks = this.getRobotTask(appVO.getId());
        for (RobotTask task : robotTasks) {
            if (StringUtils.isNotBlank(appVO.getTaskCode()) && !task.getTaskCode().equals(appVO.getTaskCode())) {
                continue;
            }
            if (task.getMachineCode().equals(appVO.getMachineCode())) {
                throw new RuntimeException("同一个盒子不需要变更.");
            }
            RobotTask update = new RobotTask();
            update.setId(task.getId());
            update.setMachineCode(appVO.getMachineCode());
            update.setUpdateTime(new Date());
            robotTaskDao.updateByPrimaryKeySelective(update);
            //更新状态为未同步
            robotTaskDao.cleanSyncTime(task.getTaskCode());
            //发送指令，等待盒子同步指令删除数据
            this.transferTask(task.getClientId(), task.getMachineCode(), task.getAppCode(), task.getTaskCode());
        }

        //客户应用
        RobotClientApp clientApp = robotClientAppDao.selectByPrimaryKey(appVO.getId());
        //任务
        Example example = new Example(RobotClientApp.class);
        example.createCriteria().andEqualTo("clientId", clientApp.getClientId()).andEqualTo("machineCode", appVO.getMachineCode());
        List<RobotClientApp> clientApps = robotClientAppDao.selectByExample(example);
        boolean isHavAppCode = false;
        if (CollectionUtils.isNotEmpty(clientApps)) {
            for (RobotClientApp app : clientApps) {
                if (app.getAppCode().equals(clientApp.getAppCode())) {
                    isHavAppCode = true;
                }
            }
        }
        //在拿旧盒子所有任务
        robotTasks = this.getRobotTask(appVO.getId());
        //删除前需要是否迁移了所有任务
        if (isHavAppCode) {
            if (CollectionUtils.isEmpty(robotTasks)) {
                robotClientAppDao.deleteByPrimaryKey(appVO.getId());
            }
        } else {
            //切换应用机器码
            if (CollectionUtils.isEmpty(robotTasks)) {
                RobotClientApp update = new RobotClientApp();
                update.setId(appVO.getId());
                update.setMachineCode(appVO.getMachineCode());
                update.setUpdateTime(new Date());
                robotClientAppDao.updateByPrimaryKeySelective(update);
            } else {
                RobotClientApp robotClientApp = new RobotClientApp();
                robotClientApp.setStatus(1);
                robotClientApp.setVersion(clientApp.getVersion());
                robotClientApp.setAppCode(clientApp.getAppCode());
                robotClientApp.setAppName(clientApp.getAppName());
                robotClientApp.setClientId(clientApp.getClientId());
                robotClientApp.setMachineCode(appVO.getMachineCode());
                robotClientApp.setCreateTime(new Date());
                robotClientApp.setUpdateTime(new Date());
                robotClientAppDao.insertSelective(robotClientApp);
            }
        }
        return 1;
    }

    private List<RobotTask> getRobotTask(Integer robotClientAppId) {
        //客户应用
        RobotClientApp clientApp = robotClientAppDao.selectByPrimaryKey(robotClientAppId);
        //任务
        Example example = new Example(RobotTask.class);
        example.createCriteria().andEqualTo("clientId", clientApp.getClientId()).andEqualTo("machineCode", clientApp.getMachineCode()).andEqualTo("appCode", clientApp.getAppCode());
        return robotTaskDao.selectByExample(example);
    }

    public void transferTask(Integer clientId, String machineCode, String appCode, String taskCode) {
        RobotCommand command = new RobotCommand();
        command.setClientId(clientId);
        command.setMachineCode(machineCode);
        command.setAppCode(appCode);
        command.setTaskCode(taskCode);
        command.setCommand("deleteTask");
        command.setCreateTime(new Date());
        command.setUpdateTime(new Date());
        commandDao.insertSelective(command);
    }

    @Override
    public List<RobotExecutionMo> executions(ExecutionQueryDTO dto) {
        List<RobotExecutionMo> executionList = robotExecutionRepository.listRobotExecution(dto);

        Map<String, String> accountMap = Maps.newConcurrentMap();
        Map<String, List<RobotTaskScheduleDTO>> scheduleMap = Maps.newConcurrentMap();

        executionList.parallelStream().forEach(e -> {
            //获取申报文件
            Example example = new Example(RobotExecutionFileInfo.class);
            example.createCriteria().andEqualTo("executionCode", e.getExecutionCode())
                    .andEqualTo("flowCode", e.getFlowCode());
            List<RobotExecutionFileInfo> robotExecutionFiles = robotExecutionFileInfoDao.selectByExample(example);
            if(CollectionUtils.isNotEmpty(robotExecutionFiles)){
                robotExecutionFiles= robotExecutionFiles.stream().filter(a->a.getFileFullPath()!=null&&a.getFileFullPath().endsWith(".xls")
                        ||a.getFileFullPath().endsWith(".xlsx")||a.getFileFullPath().endsWith(".xls")
                        ||a.getFileFullPath().endsWith(".CSV")).collect(Collectors.toList());
            }
            e.setReportFile(robotExecutionFiles);

            String taskCode = e.getTaskCode();
            if (accountMap.containsKey(taskCode)) {
                e.setAccountNumber(accountMap.get(taskCode));
            } else {
                String accountNumber = robotTaskDao.getAccountNumberByTaksCode(taskCode);
                if (StringUtils.isNotBlank(accountNumber)) {
                    e.setAccountNumber(accountNumber);
                    accountMap.put(taskCode, accountNumber);
                }
            }

            String appCode = e.getAppCode();
            String flowCode = e.getFlowCode();

            String key = appCode.concat("-").concat(taskCode);
            List<RobotTaskScheduleDTO> scheduleDTOS = Lists.newCopyOnWriteArrayList();
            if (!scheduleMap.containsKey(key)) {
                scheduleDTOS.addAll(robotTaskScheduleDao.selectSchedules(appCode, taskCode, 0)); // 拿私有
                List<String> prvtFlowCodes = Lists.newArrayList();
                if (CollectionUtils.isNotEmpty(scheduleDTOS)) {
                    prvtFlowCodes = scheduleDTOS.stream().map(it -> it.getFlowCode()).collect(Collectors.toList());
                }
                scheduleDTOS.addAll(robotTaskScheduleDao.selectCommonSchedules(appCode, taskCode, prvtFlowCodes, 0));
            } else {
                scheduleDTOS = scheduleMap.get(key);
            }
            if (CollectionUtils.isNotEmpty(scheduleDTOS)) {
                Optional<RobotTaskScheduleDTO> first = scheduleDTOS.stream().filter(it -> it.getFlowCode().equals(flowCode)).findFirst();
                if (first.isPresent()) {
                    RobotTaskScheduleDTO taskSchedule = first.get();
                    StringBuffer stringBuffer = new StringBuffer();
                    String execCycle = taskSchedule.getExecCycle();
                    String[] split = execCycle.split("-");
                    stringBuffer.append("本月").append(split[0]).append("号").append("-").append(split[1]).append("号")
                            .append("：").append(StringUtils.isBlank(taskSchedule.getExecAreaTime()) ? "实时" : taskSchedule.getExecAreaTime());
                    e.setExecutePlan(stringBuffer.toString());
                }
            }
        });
        return executionList;
    }

    @Override
    public void executionExport(HttpServletResponse response, ExecutionQueryDTO dto) throws Exception {
        List<DevUserAddr> userAddrs = rpaSaasService.listUserAddr(new DevUserAddr());
        Map<String, DevUserAddr> userAddrMap = userAddrs.stream().collect(Collectors.toMap(k -> k.getAddrName() + "-" + k.getBusinessType(), v -> v, (k, v) -> v));
        List<RobotExecutionExportVO> exportList = Lists.newArrayList();
        dto.setStatuses(Lists.newArrayList(0));
        List<RobotExecutionMo> executionList = robotExecutionRepository.listRobotExecution(dto);
        Map<String, String> accountMap = Maps.newConcurrentMap();
        executionList.parallelStream().forEach(e -> {
            RobotExecutionExportVO exportVO = new RobotExecutionExportVO();
            exportVO.setAppName(e.getAppName());
            exportVO.setFlowName(e.getFlowName());
            String taskCode = e.getTaskCode();
            if (accountMap.containsKey(taskCode)) {
                exportVO.setAccountNumber(accountMap.get(taskCode));
            } else {
                String accountNumber = robotTaskDao.getAccountNumberByTaksCode(taskCode);
                if (StringUtils.isNotBlank(accountNumber)) {
                    exportVO.setAccountNumber(accountNumber);
                    accountMap.put(taskCode, accountNumber);
                }
            }
            exportVO.setStartTime(e.getStartTime());
            exportVO.setStatus(e.getStatus() == 0 ? "执行失败" : e.getStatus() == 1 ? "执行成功" : "执行中");

            List<RobotExecutionDetailMo> detailList = robotExecutionRepository.listRobotExecutionDetail(e.getExecutionCode(), e.getFlowCode(), 0);
            if (CollectionUtils.isNotEmpty(detailList)) {
                exportVO.setStepName(detailList.stream().map(vo -> vo.getStepName()).distinct().collect(Collectors.joining(";")));
                exportVO.setError(detailList.stream().map(vo -> vo.getError()).distinct().collect(Collectors.joining(";")));
            }
            DevUserAddr userAddr = userAddrMap.get(e.getAppName());
            if (userAddr != null) {
                exportVO.setDevUserName(userAddr.getDevUserName());
                exportVO.setYwUserName(userAddr.getYwUserName());
            }
            exportList.add(exportVO);
        });

        Calendar calendar = Calendar.getInstance();
        String fileName = URLEncoder.encode("异常执行记录" + calendar.get(Calendar.YEAR) + (calendar.get(Calendar.MONTH) + 1) + calendar.get(Calendar.DAY_OF_MONTH), "UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + new String(fileName.getBytes(StandardCharsets.UTF_8), "GBK") + ".xlsx");
        response.setHeader("FileName", fileName + ".xlsx");
        response.setHeader("Access-Control-Expose-Headers", "FileName");
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        EasyExcel.write(response.getOutputStream(), RobotExecutionExportVO.class).sheet("异常执行记录列表").doWrite(exportList);
    }

    @Override
    public void exportClientApp(HttpServletResponse response, Map<String, Object> map) throws IOException {
//        map.remove("size");
//        map.remove("start");
        List<RobotClientAppVo> clientAppVOS = robotClientAppDao.selectAppListByClientId(map);
        if (CollectionUtils.isNotEmpty(clientAppVOS)) {
            List<CustomerBase> customerBases = rpaSaasService.listCustomer(false, "");
            Map<Integer, String> custMaps = Maps.newConcurrentMap();
            customerBases.stream().forEach(it -> {
                custMaps.put(it.getId(), it.getName());
            });
            clientAppVOS.parallelStream().forEach(it ->{
                it.setCustomerName(custMaps.get(it.getClientId()));

                String appArgs = it.getAppArgs();
                Integer declaredCount = 0;
                Integer todayDeclaredCount = 0;
                Integer noDeclaredCount = 0;
                Integer auditCount = 0;
                if (StringUtils.isNotBlank(appArgs)) {
                    Map<String, Object> objectMap = (Map<String, Object>)JsonUtils.jsonToBean(appArgs, Map.class);
                    String businessType = (String)objectMap.get("businessType");
                    String addrId = (String)objectMap.get("addrId");
                    it.setAddrId(Integer.valueOf(addrId));
                    it.setBusinessType("1001001".equals(businessType)?1:2);
                    String accountNumber = it.getAccNumber();
                    if (StringUtils.isNotBlank(accountNumber)) {
                        Map<String, Integer> countMap = saasAgentService.selectDeclareCount("1001001".equals(businessType) ? 1 : 2, Integer.valueOf(addrId), accountNumber, "");
                        if (countMap != null && !countMap.isEmpty()) {
                            declaredCount = countMap.get("declaredCount");
                            todayDeclaredCount = countMap.get("todayDeclaredCount");
                            noDeclaredCount = countMap.get("noDeclaredCount");
                            auditCount = countMap.get("auditCount");
                        }
                    }
                }

                it.setDeclaredCount(declaredCount);
                it.setTodayDeclaredCount(todayDeclaredCount);
                it.setNoDeclaredCount(noDeclaredCount);
                it.setAuditCount(auditCount);

                int execCount = robotExecutionRepository.getExecCount(it.getTaskCode(), null, DateUtils.dateToStr(new Date(), "yyyy-MM-dd"));
                it.setExecCount(execCount);

                String appCode = it.getAppCode();
                String taskCode = it.getTaskCode();
                if (StringUtils.isNotBlank(appCode) && StringUtils.isNotBlank(taskCode)) {
                    List<RobotTaskScheduleDTO> scheduleDTOS = robotTaskScheduleDao.selectSchedules(appCode, taskCode, 0); // 拿私有
                    List<String> prvtFlowCodes = Lists.newArrayList();
                    if (CollectionUtils.isNotEmpty(scheduleDTOS)) {
                        prvtFlowCodes = scheduleDTOS.stream().map(item -> item.getFlowCode()).collect(Collectors.toList());
                    }
                    //那一次公共的除了私有外的
                    List<RobotTaskScheduleDTO> scheduleDTOS1 = robotTaskScheduleDao.selectCommonSchedules(appCode, taskCode, prvtFlowCodes, 0); // 拿通用
                    scheduleDTOS.addAll(scheduleDTOS1);
                    if (CollectionUtils.isNotEmpty(scheduleDTOS)) {
                        Optional<RobotTaskScheduleDTO> first = scheduleDTOS.stream().filter(item -> item.getTaskType() != null && item.getTaskType() == 2).findFirst();
                        if (first.isPresent()) {
                            it.setHaveCustom("是");
                        }else{
                            it.setHaveCustom("否");
                        }
                        List<String> days = Lists.newArrayList();
                        List<String> hours = Lists.newArrayList();
                        scheduleDTOS.stream().filter(item -> item.getExecStyle()==1 || item.getExecStyle()==2).forEach(item -> {
                            String execCycle = item.getExecCycle();
                            String[] split = execCycle.split("-");
                            days.addAll(Lists.newArrayList(split));
                            String execAreaTime = item.getExecAreaTime();
                            Integer execStyle = item.getExecStyle();
                            String[] split1 = null;
                            if (execStyle == 1) {
                                split1 = execAreaTime.split("-");
                            } else {
                                split1 = execAreaTime.split(",");
                            }
                            for (String hour : split1) {
                                hours.add(hour.split(":")[0]);
                            }
                        });
                        if (CollectionUtils.isNotEmpty(days) && CollectionUtils.isNotEmpty(hours)) {
                            days.sort((o1,o2)->Integer.valueOf(o1)-Integer.valueOf(o2));
                            hours.sort((o1,o2)->Integer.valueOf(o1)-Integer.valueOf(o2));
                            it.setExecPlant("当月".concat(days.get(0)).concat("号-").concat(days.get(days.size()-1)).concat("号：").concat(hours.get(0)).concat(":00-").concat(hours.get(hours.size()-1)).concat(":00"));
                        }
                    }
                }
                if (StringUtils.isBlank(taskCode)) {
                    it.setTaskCode("");
                }
            });
            for (int i=0; i<clientAppVOS.size(); i++) {
                RobotClientAppVo it = clientAppVOS.get(i);
                it.setIndex(i+1);
            }
            clientAppVOS.sort(Comparator.comparing(RobotClientAppVo::getNoDeclaredTotal, Comparator.reverseOrder())
                    .thenComparing(RobotClientAppVo::getNoDeclaredNumber, Comparator.reverseOrder())
                    .thenComparing(RobotClientAppVo::getId, Comparator.reverseOrder()).thenComparing(RobotClientAppVo::getTaskCode, Comparator.reverseOrder()));
        }
        Calendar calendar = Calendar.getInstance();
        String fileName = URLEncoder.encode("客户应用情况" + calendar.get(Calendar.YEAR) + (calendar.get(Calendar.MONTH) + 1) + calendar.get(Calendar.DAY_OF_MONTH), "UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + new String(fileName.getBytes(StandardCharsets.UTF_8), "GBK") + ".xlsx");
        response.setHeader("FileName", fileName + ".xlsx");
        response.setHeader("Access-Control-Expose-Headers", "FileName");
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        EasyExcel.write(response.getOutputStream(), RobotClientAppVo.class).sheet("客户应用列表").doWrite(clientAppVOS);
    }

    @Override
    public RobotClient getRobotClient() {
        Session session = SecurityContext.currentUser();
        Example example = new Example(RobotClient.class);
        example.createCriteria().andEqualTo("clientId", session.getCustId());
        List<RobotClient> robotClients = robotClientDao.selectByExample(example);

        RobotClient rc = new RobotClient();
        if (CollectionUtils.isEmpty(robotClients)) {
            return rc;
        }
        rc.setIp(robotClients.get(0).getIp());
        rc.setPort(robotClients.get(0).getPort());
        return rc;
    }

    @Override
    public RobotClient getRobotClientByMachineCode(String machineCode) {
        Session session = SecurityContext.currentUser();
        Example example = new Example(RobotClient.class);
        example.createCriteria().andEqualTo("clientId", session.getCustId()).andEqualTo("machineCode", machineCode);
        List<RobotClient> robotClients = robotClientDao.selectByExample(example);
        if (CollectionUtils.isEmpty(robotClients)) {
            return null;
        } else {
            return robotClients.get(0);
        }
    }

    @Override
    public RobotClientApp getRobotClientApp(String appCode, String machineCode) {
        Session session = SecurityContext.currentUser();
        Example example = new Example(RobotClientApp.class);
        example.createCriteria().andEqualTo("clientId", session.getCustId())
                .andEqualTo("appCode", appCode).andEqualTo("machineCode", machineCode);
        List<RobotClientApp> robotClientApps = robotClientAppDao.selectByExample(example);
        if (CollectionUtils.isEmpty(robotClientApps)) {
            return null;
        } else {
            return robotClientApps.get(0);
        }
    }

    @Override
    public Integer selectExecutionCountByParams(Map<String, Object> params) {
        return robotExecutionRepository.selectExecutionCountByParams(params);
    }

    @Override
    public int robotUpdateTaskStatus(RobotTaskStatusChangeDTO statusChangeDTO) {
        List<String> stList = Lists.newArrayList();
        if (StringUtils.isEmpty(statusChangeDTO.getAppCode())) {
            stList.add("应用appCode必填");
        }
        if (StringUtils.isEmpty(statusChangeDTO.getTaskCode())) {
            stList.add("任务taskCode必填");
        }
        if (CollectionUtils.isNotEmpty(stList)) {
            throw new BusinessException(String.join(",", stList));
        }

        HashMap<String , Object> paramsMap = Maps.newHashMap();
        paramsMap.put("taskCode",statusChangeDTO.getTaskCode());
        paramsMap.put("appCode",statusChangeDTO.getAppCode());
        RobotTaskStatusChangeSendDTO changeSendDTO = robotTaskDao.getRobotTaskInfo(paramsMap);

        if (changeSendDTO == null) {
            throw new BusinessException("流程任务不存在");
        }

        if (changeSendDTO.getStatus() == 0) {
            throw new BusinessException("当前流程已停用");
        }
        int i = updateTaskStatus(statusChangeDTO.getAppCode(), statusChangeDTO.getTaskCode(), statusChangeDTO.getStatus(), statusChangeDTO.getComment());

        // 停用原因
        changeSendDTO.setStopReason(statusChangeDTO.getComment());

        // 设置地区参数
        JSONObject appArgs = JSONObject.parseObject(changeSendDTO.getAppArgs());
        String addrId=appArgs.getString("addrId");
        String addrName=appArgs.getString("addrName");
        String businessType= (appArgs.getString("businessType") == null || appArgs.getString("businessType").equals("1001001"))?"1":"2";
        changeSendDTO.setAddrId(addrId);
        changeSendDTO.setAddrName(addrName);
        changeSendDTO.setBusinessType(businessType);
        // 发送邮件
        sendPassWordFailMessage(changeSendDTO);

        return i;
    }

    private void sendPassWordFailMessage(RobotTaskStatusChangeSendDTO dto){
        MessageRuleConfigVO vo = rpaSaasService.getMessageRule("10026002", "10027013");
        // 查询消息模版
        if (vo == null) {
            throw new BusinessException("消息模版不存在");
        }
        vo.setBusinessType(Integer.valueOf(dto.getBusinessType()));
        vo.setAddrName(dto.getAddrName());
        vo.setAddrId(Integer.valueOf(dto.getAddrId()));

        // 替换邮件内容相关字段
        String theme = vo.getMessageTopic(); // 主题
        theme = theme.replace("$应用名称$", dto.getAppName()).replace("$状态$", "已停用")
                .replace("$原因$", dto.getStopReason());

        String emailContent = vo.getEmailContent(); // 邮件内容
        emailContent = emailContent.replace("$应用名称$", dto.getAppName()).replace("$申报账户$", dto.getCompanyName() + "-" + dto.getAccountNumber())
                .replace("$所在设备$", dto.getMachineCode())
                .replace("$状态$", "已停用")
                .replace("$原因$", dto.getStopReason());

        vo.setMessageTopic(theme);
        vo.setEmailContent(emailContent);

        // 发送邮件
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CompletableFuture.runAsync(() -> {
            SecurityContextHolder.getContext().setAuthentication(authentication);
            try {
                sendToCust(vo,dto.getClientId(),dto.getAccountNumber());
            } catch (Exception e) {
                e.printStackTrace();
                log.error("发送邮件通知失败：{}", e.getMessage(), e);
            }
        });
    }


    /**
     * @param custId 发送指定客户用户
     */
    private void sendToCust(MessageRuleConfigVO messageRule, Integer custId, String accountNumber) {
        Integer sendCust = messageRule.getSendCust();
        if (sendCust == 1) {
            List<SysUser> allCustUser = rpaAuthService.getAllCustUser();
            if (org.apache.commons.collections.CollectionUtils.isNotEmpty(allCustUser)) {
                allCustUser = allCustUser.stream().filter(user -> user.getStatus()!=null && user.getStatus()==1).collect(Collectors.toList());
            }
            List<MessageRuleCustomerVO> custList = messageRule.getCustList();
            if (org.apache.commons.collections.CollectionUtils.isNotEmpty(custList)) {
                Set<Integer> custIds = custList.stream().map(it -> it.getCustId()).collect(Collectors.toSet());
                allCustUser = allCustUser.stream().filter(user -> custIds.contains(user.getCustId())).collect(Collectors.toList());
            }
            // 指定客户用户
            if (custId != null) {
                allCustUser = allCustUser.stream().filter(user -> custId.equals(user.getCustId())).collect(Collectors.toList());
            }
            if (org.apache.commons.collections.CollectionUtils.isNotEmpty(allCustUser)) {
                Integer businessType = messageRule.getBusinessType();
                Integer addrId = messageRule.getAddrId();
                for (SysUser user : allCustUser) {
                    List<SysUserDeclareAccountVO> userDeclareAccountVOList = rpaSaasService.getUserDeclareAccountList(user.getId(), businessType);
                    Optional<SysUserDeclareAccountVO> first = userDeclareAccountVOList.stream().filter(it ->
                            it.getAddrId()!=null && it.getAddrId().equals(addrId)
                                    && (accountNumber == null || accountNumber.equals(it.getAccountNumber()))).findFirst();
                    if (first.isPresent()) {
                        sendMessage(messageRule, user, "客户用户");
                    } else {
                        log.info(String.format("客户用户{%s}未分配数据权限，略过消息推送！", user.getUsername()));
                    }
                }
            }
        } else {
            log.info("消息策略设置不推送消息给客户人员，略过向客户人员消息推送！");
        }
    }


    /**
     * 推送邮件、短息消息
     * @param messageRule
     * @param user
     * @param userType
     */
    private void sendMessage(MessageRuleConfigVO messageRule, SysUser user, String userType) {

        Integer emailWay = messageRule.getEmailWay(); // 是否推送邮件
        Integer smsWay = messageRule.getSmsWay(); // 是否推送短信

        String theme = messageRule.getMessageTopic(); // 主题

        String emailContent = messageRule.getEmailContent(); // 邮件内容

        String smsContent = messageRule.getSmsContent();

        MessageList ml = new MessageList();
        ml.setCustId(user.getCustId());
        ml.setUserId(user.getId());
        ml.setEmpName(user.getName());
        ml.setCreateTime(new Date());
        ml.setMessageRuleId(messageRule.getId());
        ml.setMessageType(messageRule.getMessageType());
        ml.setWarnType(messageRule.getWarnType());
        ml.setStatus(0);
        ml.setTheme(theme);
        ml.setEmailContent(emailContent);
        ml.setEmail(user.getEmail());
        if (emailWay == 1) {
            String email = user.getEmail();
            if (StringUtils.isNotBlank(email)) {
                ml.setEmail(email);
                Map<String, Object> emailMap = Maps.newHashMap();
                emailMap.put("receiverEmail", user.spiltEmail());
                emailMap.put("subject", theme);
                emailMap.put("mail_content", ml.getEmailContent());
                try {
                    EmailUtil.sendEmailAttachRetiree(emailMap);
                    ml.setEmailStatus(1);
                } catch (Exception e) {
                    log.error("邮件发送异常." + e.getMessage(), e);
                    ml.setEmailStatus(2);
                    ml.setEmailFailReason("邮件发送异常！");
                }
            } else {
                ml.setEmailStatus(2);
                ml.setEmailFailReason(String.format("%s邮箱地址为空，发送邮件失败", userType));
            }
        } else {
            ml.setEmailStatus(2);
            ml.setEmailFailReason("策略设置不发送邮件，略过邮件推送");
        }
        String phoneNumber = user.getPhoneNumber();
        ml.setPhoneNumber(phoneNumber);
        if (smsWay == 1) {
            if (StringUtils.isNotBlank(phoneNumber) && StringUtils.isNotBlank(smsContent)) {
                try {
                    SmsUtil.send(phoneNumber, smsContent);
                    ml.setSmsStatus(1);
                }catch (Exception e) {
                    ml.setSmsStatus(2);
                    ml.setSmsFailReason("短信消息发送失败！");
                }
            } else {
                ml.setSmsStatus(2);
                if (StringUtils.isBlank(phoneNumber)) {
                    ml.setSmsFailReason(String.format("%s手机号码为空，发送短信失败", userType));
                }
                if (StringUtils.isBlank(smsContent)) {
                    ml.setSmsFailReason(String.format("策略未设置短信内容，略过短信发送"));
                }
            }
        } else {
            ml.setSmsStatus(2);
            ml.setSmsFailReason("策略设置不发送短信，略过短信推送");
        }
        rpaSaasService.saveMessage(ml);
    }

    @Override
    public List<RobotTaskVO1> listAllTask(String appCode) {
        Example example = new Example(RobotTask.class);
        example.createCriteria().andEqualTo("appCode", appCode);
        List<RobotTask> tasks = robotTaskDao.selectByExample(example);
        if (CollectionUtils.isEmpty(tasks)) {
            return Lists.newArrayList();
        }
        List<RobotTaskVO1> taskVOS = BeanCopyUtils.copyListProperties(tasks, RobotTaskVO1::new);
        for (RobotTaskVO1 taskVO : taskVOS) {
            RobotTaskArgs accountNumberArg = robotTaskArgsDao.selectAccountNumber(taskVO.getTaskCode(), Lists.newArrayList("socialNumber", "accfundNumber"));
            if (accountNumberArg != null) {
                taskVO.setAccountNumber(accountNumberArg.getArgsValue());
            }
            RobotTaskArgs companyNameArg = robotTaskArgsDao.selectAccountNumber(taskVO.getTaskCode(), Lists.newArrayList("companyName"));
            if (companyNameArg != null) {
                taskVO.setCompanyName(companyNameArg.getArgsValue());
            }
        }
        return taskVOS;
    }

    @Override
    public Integer startTask(String taskCode) {
        return robotTaskDao.updateTaskStatus(1, taskCode);
    }

    @Override
    public Integer stopOrEnableTask(String argsValue, Integer status) {
        List<String> list = Lists.newArrayList();
        list.add("socialNumber");
        list.add("accfundNumber");
        Example example = new Example(RobotTaskArgs.class);
        example.createCriteria().andEqualTo("argsValue", argsValue).andIn("argsKey", list);
        List<RobotTaskArgs> robotTaskArgs = robotTaskArgsDao.selectByExample(example);
        List<String> taskCodes = robotTaskArgs.stream().map(RobotTaskArgs::getTaskCode).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(taskCodes)) {
            return 0;
        }
        Example taskExam = new Example(RobotTask.class);
        taskExam.createCriteria().andIn("taskCode", taskCodes);
        RobotTask robotTask = new RobotTask();
        robotTask.setStatus(status);
        return robotTaskDao.updateByExampleSelective(robotTask, taskExam);
    }

    @Override
    public List<RobotClient> getMachineByClientId(Integer clientId) {
        if (clientId == null) {
            return robotClientDao.selectAll();
        } else {
            Example e = new Example(RobotClient.class);
            e.createCriteria().andEqualTo("clientId", clientId);
            return robotClientDao.selectByExample(e);
        }
    }

    @Override
    public void downloadFile(String fileName, String filePath, HttpServletResponse response) {
        File file = new File(filePath);
        InputStream inputStream = null;
        if (file.exists()) {
            try {
                inputStream = new FileInputStream(file);
                byte[] bytes = IOUtils.toByteArray(inputStream);
                RespUtils.export(fileName, bytes, response);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            throw new BusinessException("文件不存在，下载失败");
        }
    }

    @Override
    public RobotExecutionMo getExecutionMessage(String executionCode){
        List<RobotExecutionMo> detailList = robotExecutionRepository.listRobotExecutionByCode(executionCode);
        if(CollectionUtils.isNotEmpty(detailList)){
            RobotExecutionMo mo=detailList.get(0);
            return mo;
        }else{
            return null;
        }

    }

    @Override
    public RobotClientDTO getRobotClientInfo(Integer clientId, String machineCode, String appCode) {
        RobotClientDTO robotClientInfo = robotClientDao.getRobotClientInfo(clientId, machineCode, appCode);
        if (robotClientInfo == null) {
            return new RobotClientDTO();
        }
        CustomerBase customer = rpaSaasService.getCustomer(clientId);
        if (customer != null) {
            robotClientInfo.setCustomerName(customer.getName());
        }
        Map<String,Object> appInfo = robotTaskDao.selectAppInfo(clientId, machineCode, appCode);
        if (appInfo != null) {
            robotClientInfo.setAppName((String)appInfo.get("appName"));
            robotClientInfo.setRunningCount(((Long)appInfo.get("runningCount")).intValue());
            robotClientInfo.setStopCount(((Long) appInfo.get("stopCount")).intValue());
            robotClientInfo.setStartCount(((Long) appInfo.get("startCount")).intValue());
            robotClientInfo.setFlowCount(((Long) appInfo.get("flowCount")).intValue());
        }
        return robotClientInfo;
    }

    @Override
    public List<RobotTaskInfoDTO> getRobotTasks(Map<String, Object> params) {
        List<RobotTaskInfoDTO> list = robotTaskDao.getRobotTasks(params);
        if (CollectionUtils.isNotEmpty(list)) {
            list.stream().forEach(it -> {
                String appArgs = it.getAppArgs();
                Integer declaredCount = 0;
                Integer todayDeclaredCount = 0;
                Integer noDeclaredCount = 0;
                Integer auditCount = 0;
                if (StringUtils.isNotBlank(appArgs)) {
                    Map<String, String> o = (Map<String, String>)JsonUtils.jsonToBean(appArgs, Map.class);
                    String businessType = o.get("businessType");
                    String addrId = o.get("addrId");
                    String accountNumber = it.getAccountNumber();
                    if (StringUtils.isNotBlank(accountNumber)) {
                        Map<String, Integer> countMap = saasAgentService.selectDeclareCount("1001001".equals(businessType) ? 1 : 2, Integer.valueOf(addrId), accountNumber, "");
                        if (countMap != null && !countMap.isEmpty()) {
                            declaredCount = countMap.get("declaredCount");
                            todayDeclaredCount = countMap.get("todayDeclaredCount");
                            noDeclaredCount = countMap.get("noDeclaredCount");
                            auditCount = countMap.get("auditCount");
                        }
                    }
                }
                it.setDeclaredCount(declaredCount);
                it.setTodayDeclaredCount(todayDeclaredCount);
                it.setNoDeclaredCount(noDeclaredCount);
                it.setAuditCount(auditCount);

                int execCount = robotExecutionRepository.getExecCount(it.getTaskCode(), null, DateUtils.dateToStr(new Date(), "yyyy-MM-dd"));
                it.setExecCount(execCount);

                String appCode = it.getAppCode();
                String taskCode = it.getTaskCode();

                if (StringUtils.isNotBlank(appCode) && StringUtils.isNotBlank(taskCode)) {
                    List<RobotTaskScheduleDTO> scheduleDTOS = robotTaskScheduleDao.selectSchedules(appCode, taskCode, 0); // 拿私有
                    List<String> prvtFlowCodes = Lists.newArrayList();
                    if (CollectionUtils.isNotEmpty(scheduleDTOS)) {
                        prvtFlowCodes = scheduleDTOS.stream().map(item -> item.getFlowCode()).collect(Collectors.toList());
                    }
                    //那一次公共的除了私有外的
                    List<RobotTaskScheduleDTO> scheduleDTOS1 = robotTaskScheduleDao.selectCommonSchedules(appCode, taskCode, prvtFlowCodes, 0); // 拿通用
                    scheduleDTOS.addAll(scheduleDTOS1);

                    int taskStartCount = 0;

                    if (CollectionUtils.isNotEmpty(scheduleDTOS)) {

                        Optional<RobotTaskScheduleDTO> first = scheduleDTOS.stream().filter(item -> item.getTaskType() != null && item.getTaskType() == 2).findFirst();
                        if (first.isPresent()) {
                            it.setHaveCustom("是");
                        }else{
                            it.setHaveCustom("否");
                        }

                        taskStartCount = (int)scheduleDTOS.stream().filter(item -> item.getStatus() != null && item.getStatus() == 1).count();

                        List<String> days = Lists.newArrayList();
                        List<String> hours = Lists.newArrayList();
                        scheduleDTOS.stream().filter(item -> item.getExecStyle()==1 || item.getExecStyle()==2).forEach(item -> {
                            String execCycle = item.getExecCycle();
                            String[] split = execCycle.split("-");
                            days.addAll(Lists.newArrayList(split));
                            String execAreaTime = item.getExecAreaTime();
                            String[] split1 = null;
                            Integer execStyle = item.getExecStyle();
                            if (execStyle == 1) {
                                split1 = execAreaTime.split("-");
                            } else {
                                split1 = execAreaTime.split(",");
                            }
                            for (String hour : split1) {
                                hours.add(hour.split(":")[0]);
                            }
                        });
                        if (CollectionUtils.isNotEmpty(days) && CollectionUtils.isNotEmpty(hours)) {
                            days.sort((o1,o2)->Integer.valueOf(o1)-Integer.valueOf(o2));
                            hours.sort((o1,o2)->Integer.valueOf(o1)-Integer.valueOf(o2));
                            it.setExecPlant("当月".concat(days.get(0)).concat("号-").concat(days.get(days.size()-1)).concat("号：").concat(hours.get(0)).concat(":00-").concat(hours.get(hours.size()-1)).concat(":00"));
                        }
                    }
                    it.setTaskStartCount(taskStartCount);
                }
            });
        }
        return list;
    }

    @Override
    public List<RobotTaskConfigDTO> getRobotTaskConfigInfo(String taskCode) {
        List<RobotTaskConfigDTO> list = robotTaskArgsDao.getRobotTaskConfigInfo(taskCode);
        ScriptEngine js = new ScriptEngineManager().getEngineByName("js");
        if (CollectionUtils.isNotEmpty(list)) {
            List<String> fileTypes = Lists.newArrayList("photoUpload","fileUpload");
            for (int i=0; i<list.size(); i++) {
                RobotTaskConfigDTO it = list.get(i);
                List<RobotTaskArgsDTO> args = it.getArgs();
                List<RobotTaskArgsDTO> args1 = Lists.newArrayList();
                if (CollectionUtils.isNotEmpty(args)) {
                    for (RobotTaskArgsDTO arg : args) {
                        Example example4 = new Example(RobotAppConfigCondition.class);
                        example4.createCriteria().andEqualTo("argsDefineId", arg.getId());
                        List<RobotAppConfigCondition> conditions = conditionDao.selectByExample(example4);
                        if (CollectionUtils.isNotEmpty(conditions)) {
                            boolean show = true;
                            StringBuilder str = new StringBuilder("");
                            for (int idx=0; idx<conditions.size(); idx++) {
                                RobotAppConfigCondition robotAppConfigCondition = conditions.get(idx);
                                String conditionLeft = robotAppConfigCondition.getConditionLeft();
                                String conditionRight = robotAppConfigCondition.getConditionRight();
                                String logicalOperator = robotAppConfigCondition.getLogicalOperator();
                                Optional<RobotTaskArgsDTO> first = args.stream().filter(a -> a.getFieldName().equals(conditionLeft)).findFirst();
                                if (first.isPresent()) {
                                    RobotTaskArgsDTO robotTaskArgsDTO = first.get();
                                    str.append(String.format("'%s'%s%s",robotTaskArgsDTO.getArgsValue()
                                            ,parseLogicalOperator(logicalOperator, conditionRight),
                                            idx==conditions.size()-1?"":parseRelation(robotAppConfigCondition.getRelation())
                                            ));
                                } else {
                                    show = false;
                                    break;
                                }
                            }
                            if (show) {
                                try {
                                    Boolean eval = (Boolean) js.eval(str.toString());
                                    if (eval) {
                                        if (fileTypes.contains(arg.getFieldType())) {
                                            String fileId = arg.getArgsValue();
                                            if (StringUtils.isNotBlank(fileId)) {
                                                List<FileStoreVO> byFileIds = rpaSaasService.getByFileIds(fileId);
                                                if (CollectionUtils.isNotEmpty(byFileIds)) {
                                                    arg.setFileName(byFileIds.get(0).getClientFileName());
                                                }
                                            }
                                        }
                                        args1.add(arg);
                                    }
                                } catch (Exception e) {
                                }
                            }
                        } else {
                            args1.add(arg);
                        }
                    }
                    it.setArgs(args1);
                }
            }
        }
        return list;
    }

    private String parseRelation(String relation) {
        if (StringUtils.isNotBlank(relation)) {
            if ("或者".equals(relation)) {
                return "||";
            } else if ("并且".equals(relation)) {
                return "&&";
            }
        }
        return "";
    }

    private String parseLogicalOperator(String logicalOperator, String conditionRight) {
        if (StringUtils.isNotBlank(logicalOperator)) {
            if ("等于".equals(logicalOperator)) {
                return "=='".concat(conditionRight).concat("'");
            } else if ("不等于".equals(logicalOperator)) {
                return "!='".concat(conditionRight).concat("'");
            } else if ("包含".equals(logicalOperator)) {
                return ".indexOf('".concat(conditionRight).concat("')>-1");
            } else if ("不包含".equals(logicalOperator)) {
                return ".indexOf('".concat(conditionRight).concat("')==-1");
            }
        }
        return "";
    }

    @Override
    public int updateTaskStatus(String appCode, String taskCode, Integer status, String comment) {
        if (status == 0 && StringUtils.isBlank(comment)) { // 停用时原因必填
            throw new BusinessException("停用账户时原因必填");
        }
        //更新同步状态
        clientTaskDao.cleanSyncTime(taskCode);
        Session session = SecurityContext.currentUser();
        RobotTask robotTask = new RobotTask();
        robotTask.setAppCode(appCode);
        robotTask.setTaskCode(taskCode);
        robotTask.setStatus(status);
        robotTask.setComment(comment);
        robotTask.setEditName(session.getName());
        robotTask.setUpdateTime(new Date());
        if (status == 1) {
            robotTask.setEnableTime(new Date());
        }
        //插入历史操作表
        RobotAccountStatusHistory history = new RobotAccountStatusHistory();
        history.setStatus(status);
        history.setTaskCode(taskCode);
        history.setRemark(comment);
        history.setCreateId((int) session.getId());
        history.setCreateName(session.getName());
        history.setCreateTime(new Date());
        robotAccountStatusHistoryDao.insertSelective(history);
        taskQueueService.stopTask(taskCode, comment);
        return robotTaskDao.updateTask(robotTask);
    }

    @Override
    public List<RobotTaskServiceItemDTO> showServiceItemFlow(String appCode, String taskCode, String accountNumber) {
        List<RobotTaskServiceItemDTO> list = Lists.newArrayList();

        //获取应用下的登录流程
        RobotTaskServiceItemDTO itemDTO = robotFlowDao.getLoginFlow(appCode);
        if (itemDTO != null) {
            itemDTO.setTaskCode(taskCode);
            itemDTO.setAppCode(appCode);
            itemDTO.setFlowCodes(Lists.newArrayList(itemDTO.getFlows().split(",")));
            itemDTO.setIndex(0);
            Example example = new Example(RobotTask.class);
            example.createCriteria().andEqualTo("appCode", appCode).andEqualTo("taskCode", taskCode);
            RobotTask robotTask = robotTaskDao.selectOneByExample(example);
            if (robotTask != null) {
                itemDTO.setMachineCode(robotTask.getMachineCode());
                itemDTO.setClientId(robotTask.getClientId());
            }
            int execCount = robotExecutionRepository.getExecCount(taskCode, Lists.newArrayList(itemDTO.getFlows().split(",")), DateUtils.dateToStr(new Date(), "yyyy-MM-dd"));
            itemDTO.setExecCount(execCount);
            list.add(itemDTO);
        }

        List<RobotTaskScheduleDTO> scheduleDTOS = robotTaskScheduleDao.selectSchedules(appCode, taskCode, 1); // 拿私有
        List<String> prvtFlowCodes = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(scheduleDTOS)) {
            prvtFlowCodes = scheduleDTOS.stream().map(it -> it.getFlowCode()).collect(Collectors.toList());
        }
        //那一次公共的除了私有外的
        List<RobotTaskScheduleDTO> scheduleDTOS1 = robotTaskScheduleDao.selectCommonSchedules(appCode, taskCode, prvtFlowCodes, 1); // 拿通用
        scheduleDTOS.addAll(scheduleDTOS1);
        if (CollectionUtils.isNotEmpty(scheduleDTOS)) {
            Map<Integer, List<RobotTaskScheduleDTO>> listMap = scheduleDTOS.stream().collect(Collectors.groupingBy(RobotTaskScheduleDTO::getServiceItem));
            for (int serviceItem : listMap.keySet()) {
                List<RobotTaskScheduleDTO> subList = listMap.get(serviceItem);
                RobotTaskServiceItemDTO dto = new RobotTaskServiceItemDTO();
                RobotTaskScheduleDTO robotTaskScheduleDTO = subList.get(0);
                BeanUtils.copyProperties(robotTaskScheduleDTO, dto);

                // 获取服务项目流程中的最先执行的流程
                Optional<RobotTaskScheduleDTO> collect = subList.stream().filter(flow -> flow.getExecOrder()!=null).collect(Collectors.minBy(comparing(RobotTaskScheduleDTO::getExecOrder)));
//                int execCount = subList.stream().mapToInt(RobotTaskScheduleDTO::getExecCount).sum();
                if (collect.isPresent()) {
                    dto.setIndex(collect.get().getExecOrder());
                } else {
                    dto.setIndex(100);
                }

                // 获取服务项目所有流程中的流程编号
                List<String> flowCodes = subList.stream().map(it -> it.getFlowCode()).collect(Collectors.toList());
                dto.setFlowCodes(flowCodes);
                int execCount = robotExecutionRepository.getExecCount(robotTaskScheduleDTO.getTaskCode(), flowCodes, DateUtils.dateToStr(new Date(), "yyyy-MM-dd"));
                dto.setExecCount(execCount);


                List<String> days = Lists.newArrayList();
                List<String> hours = Lists.newArrayList();
                subList.stream().filter(item -> item.getExecStyle()==1).forEach(item -> {
                    String execCycle = item.getExecCycle();
                    String[] split = execCycle.split("-");
                    days.addAll(Lists.newArrayList(split));
                    String execAreaTime = item.getExecAreaTime();
                    String[] split1 = execAreaTime.split("-");
                    for (String hour : split1) {
                        hours.add(hour.split(":")[0]);
                    }
                });
                if (CollectionUtils.isNotEmpty(days) && CollectionUtils.isNotEmpty(hours)) {
                    days.sort((o1,o2)->Integer.valueOf(o1)-Integer.valueOf(o2));
                    hours.sort((o1,o2)->Integer.valueOf(o1)-Integer.valueOf(o2));
                    dto.setExecPlant("当月".concat(days.get(0)).concat("号-").concat(days.get(days.size()-1)).concat("号：").concat(hours.get(0)).concat(":00-").concat(hours.get(hours.size()-1)).concat(":00"));
                } else {
                    StringBuffer stringBuffer = new StringBuffer();
                    String execCycle = robotTaskScheduleDTO.getExecCycle();
                    String[] split = execCycle.split("-");
                    stringBuffer.append("本月").append(split[0]).append("号").append("-").append(split[1]).append("号")
                            .append("：").append(StringUtils.isBlank(robotTaskScheduleDTO.getExecAreaTime()) ? "实时" : robotTaskScheduleDTO.getExecAreaTime());
                    dto.setExecPlant(stringBuffer.toString());
                }

                String appArgs = robotTaskScheduleDTO.getAppArgs();
                if (StringUtils.isNotBlank(appArgs) && Lists.newArrayList(1, 2, 3, 5).contains(robotTaskScheduleDTO.getServiceItem())) {
                    Integer declaredCount = 0;
                    Integer todayDeclaredCount = 0;
                    Integer noDeclaredCount = 0;
                    Integer auditCount = 0;
                    Map<String, String> argsMap = (Map<String, String>) JsonUtils.jsonToBean(appArgs, Map.class);
                    String businessTypeStr = argsMap.get("businessType");
                    String addrId = argsMap.get("addrId");
                    Integer businessType = "1001001".equals(businessTypeStr) ? 1 : 2;
                    Map<String, Integer> countMap = saasAgentService.selectDeclareCount(businessType, Integer.valueOf(addrId), accountNumber,
                            String.valueOf(robotTaskScheduleDTO.getServiceItem()));
                    if (countMap != null && !countMap.isEmpty()) {
                        declaredCount = countMap.get("declaredCount");
                        todayDeclaredCount = countMap.get("todayDeclaredCount");
                        noDeclaredCount = countMap.get("noDeclaredCount");
                        auditCount = countMap.get("auditCount");
                    }
                    dto.setDeclaredCount(declaredCount);
                    dto.setTodayDeclaredCount(todayDeclaredCount);
                    dto.setNoDeclaredCount(noDeclaredCount);
                    dto.setAuditCount(auditCount);
                }

                list.add(dto);
            }
        }
        if (CollectionUtils.isNotEmpty(list)) {
            list.sort((o1,o2)->o1.getIndex()-o2.getIndex());
            for (int i=0; i<list.size(); i++) {
                list.get(i).setIndex(i+1);
            }
        }
        return list;
    }

    @Override
    public int updateTaskFlowStatus(String appCode, String taskCode, Integer status, List<String> flowCodes, String comment) {
        if (CollectionUtils.isEmpty(flowCodes)) {
            throw new BusinessException("流程编号必填");
        }
        if (status == 0 && StringUtils.isBlank(comment)) { // 停用时原因必填
            throw new BusinessException("停用账户时原因必填");
        }
        //更新同步状态
        clientTaskDao.cleanSyncTime(taskCode);

        Example example = new Example(RobotTask.class);
        example.createCriteria().andEqualTo("appCode", appCode).andEqualTo("taskCode", taskCode);
        RobotTask task = robotTaskDao.selectOneByExample(example);
        if (task != null) {
            robotTaskDao.updateStatus(appCode, taskCode, task.getStatus(), null);
        }

        example.clear();
        example = new Example(RobotTaskSchedule.class);
        example.createCriteria().andEqualTo("taskCode", taskCode).andIn("flowCode", flowCodes);
        List<RobotTaskSchedule> robotTaskSchedules = robotTaskScheduleDao.selectByExample(example);
        List<String> privtFlowCodes = Lists.newArrayList();
        Session session = SecurityContext.currentUser();
        int count = 0;
        if (CollectionUtils.isNotEmpty(robotTaskSchedules)) {
            privtFlowCodes = robotTaskSchedules.stream().map(it -> it.getFlowCode()).collect(Collectors.toList());
            Map<String, Object> params = Maps.newHashMap();
            params.put("taskCode", taskCode);
            params.put("flowCodes", privtFlowCodes);
            params.put("status", status);
            params.put("comment", comment);
            params.put("editId", session.getId());
            count+= robotTaskScheduleDao.updateStatus(params);
        }
        if (flowCodes.size() > privtFlowCodes.size()) {
            flowCodes.removeAll(privtFlowCodes);
            example.clear();
            example = new Example(RobotAppSchedule.class);
            example.createCriteria().andIn("flowCode", flowCodes);
            List<RobotAppSchedule> robotAppSchedules = robotAppScheduleDao.selectByExample(example);
            if (CollectionUtils.isNotEmpty(robotAppSchedules)) {
                List<RobotTaskSchedule> nowSchedules = robotAppSchedules.stream().map(it -> {
                    RobotTaskSchedule taskSchedule = new RobotTaskSchedule();
                    BeanUtils.copyProperties(it, taskSchedule);
                    taskSchedule.setStatus(status);
                    taskSchedule.setComment(comment);
                    taskSchedule.setEditId((int)session.getId());
                    taskSchedule.setTaskCode(taskCode);
                    taskSchedule.setEditTime(new Date());
                    return taskSchedule;
                }).collect(Collectors.toList());
                count+=robotTaskScheduleDao.insertList(nowSchedules);
            }
        }
        return count;
    }

    @Override
    public int editRobotTaskSchedule(RobotTaskScheduleDTO dto) {
        List<String> errors = validateTaskSchedule(dto);
        if (CollectionUtils.isNotEmpty(errors)) {
            throw new BusinessException(String.format("参数【%s】必填，请核查", StringUtils.join(errors, "、")));
        }

        String taskCode = dto.getTaskCode();
        String appCode = dto.getAppCode();

        //更新同步状态
        clientTaskDao.cleanSyncTime(taskCode);

        Example example = new Example(RobotTask.class);
        example.createCriteria().andEqualTo("appCode", appCode).andEqualTo("taskCode", taskCode);
        RobotTask task = robotTaskDao.selectOneByExample(example);
        if (task != null) {
            robotTaskDao.updateStatus(appCode, taskCode, task.getStatus(), null);
        }
        String flowCode = dto.getFlowCode();
        example.clear();
        example = new Example(RobotTaskSchedule.class);
        example.createCriteria().andEqualTo("taskCode", taskCode).andEqualTo("flowCode", flowCode);
        RobotTaskSchedule taskSchedule = robotTaskScheduleDao.selectOneByExample(example);
        if (taskSchedule == null) {
            taskSchedule = new RobotTaskSchedule();
            BeanUtils.copyProperties(dto, taskSchedule);
            setScheduleInfo(taskSchedule, dto);
            taskSchedule.setStatus(1);
            return robotTaskScheduleDao.insert(taskSchedule);
        } else {
            setScheduleInfo(taskSchedule, dto);
            return robotTaskScheduleDao.updateByPrimaryKey(taskSchedule);
        }
    }

    @Override
    public List<RobotAppInfoDTO> getRobotAppList(Integer clientId) {
        return robotAppDao.getRobotAppList(clientId);
    }

    @Override
    public List<RobotTaskInfoDTO> getRobotTaskList(Integer clientId, String appCode) {
        return robotTaskDao.getRobotTaskList(clientId, appCode);
    }

    private void setScheduleInfo(RobotTaskSchedule taskSchedule, RobotTaskScheduleDTO dto) {
        Integer taskType = dto.getTaskType();
        taskSchedule.setTaskType(taskType);
        String appCode = dto.getAppCode();
        if (taskType == 1) { // 引用上级计划
            Example e = new Example(RobotGeneralPlan.class);
            e.createCriteria().andEqualTo("appCode", appCode);
            RobotGeneralPlan robotGeneralPlan = null;
            try {
                robotGeneralPlan = robotAppGeneralPlanDao.selectOneByExample(e);
            } catch (Exception exc) {
                throw new BusinessException("该应用设置了多个通用计划，请检查");
            }

            if (robotGeneralPlan == null) {
                throw new BusinessException("该应用未设置通用计划，请检查");
            }
            taskSchedule.setGeneralPlanId(robotGeneralPlan.getId());
            taskSchedule.setExecAreaTime(null);
            taskSchedule.setExecCycle(null);
            taskSchedule.setExecStyle(null);
        } else {
            taskSchedule.setGeneralPlanId(null);
            taskSchedule.setExecAreaTime(dto.getExecAreaTime());
            taskSchedule.setExecCycle(dto.getExecCycle());
            taskSchedule.setExecStyle(dto.getExecStyle());
        }
        Session session = SecurityContext.currentUser();
        taskSchedule.setEditId((int)session.getId());
        taskSchedule.setEditTime(new Date());
    }

    private List<String> validateTaskSchedule(RobotTaskScheduleDTO dto) {

        List<String> errors = Lists.newArrayList();
        String appCode = dto.getAppCode();
        if (StringUtils.isBlank(appCode)) {
            errors.add("应用编码:appCode");
        }
        String taskCode = dto.getTaskCode();
        if (StringUtils.isBlank(taskCode)) {
            errors.add("任务编码:taskCode");
        }
        String flowCode = dto.getFlowCode();
        if (StringUtils.isBlank(flowCode)) {
            errors.add("流程编码:flowCode");
        }
        Integer taskType = dto.getTaskType();
        if (taskType == null) {
            errors.add("计划类型:taskType");
        } else if (taskType == 2) { // 自定义计划
            String execCycle = dto.getExecCycle();
            if (StringUtils.isBlank(execCycle)) {
                errors.add("执行周期:execCycle");
            }

            Integer execStyle = dto.getExecStyle();
            if (execStyle == null) {
                errors.add("执行方式:execStyle");
            } else if (execStyle == 1 || execStyle == 2) {// 连续时间区间 、 固定时间执行
                String execAreaTime = dto.getExecAreaTime();
                if (StringUtils.isBlank(execAreaTime)) {
                    errors.add("执行时区:execAreaTime");
                }
            }
        }
        return errors;

    }

    @Override
    public void runTask(String appCode, String taskCode, String machineCode, List<String> flowCodes, String serviceItemName, Boolean checkSchedule) {
        taskQueueComponent.runTask(taskCode, machineCode, flowCodes, serviceItemName,0,checkSchedule);
    }

    /**
     * 备份代码逻辑
     */
    public void runTaskBak(String appCode, String taskCode, String machineCode, List<String> flowCodes, String serviceItemName) {
        Example example = new Example(RobotTask.class);
        example.createCriteria().andEqualTo("appCode", appCode).andEqualTo("taskCode", taskCode).andEqualTo("machineCode", machineCode);
        RobotTask task = robotTaskDao.selectOneByExample(example);
        if (task == null) {
            throw new BusinessException("未能获取到需要执行的任务流程，请核对信息");
        }
        Example exa = new Example(RobotClient.class);
        exa.createCriteria().andEqualTo("clientId", task.getClientId()).andEqualTo("machineCode", machineCode);
        RobotClient robotClient = robotClientDao.selectOneByExample(exa);
        if (robotClient == null) {
            throw new BusinessException(String.format("未能找到客户对应{%s}的盒子，请确认配置是否正确", machineCode));
        }
        if (robotClient.getStatus() == null) {
            throw new BusinessException(String.format("未能识别盒子当前状态，未能触发执行，请联系管理员处理！"));
        }
        if (robotClient.getStatus() != 1 && robotClient.getStatus() != 2) {
            if (robotClient.getStatus().equals(RobotClientStatusEnum.Upgrading.getStatus())) {
                throw new BusinessException(String.format("盒子当前处于{%s}状态，未能触发执行，请等待升级完毕！", RobotClientStatusEnum.getStatusNameByCode(robotClient.getStatus())));
            }
            throw new BusinessException(String.format("盒子当前处于{%s}状态，未能触发执行，请联系管理员处理！", RobotClientStatusEnum.getStatusNameByCode(robotClient.getStatus())));
        }
        RobotCommand robotCommand = new RobotCommand();
        robotCommand.setClientId(task.getClientId());
        robotCommand.setAppCode(appCode);
        robotCommand.setTaskCode(taskCode);
        robotCommand.setMachineCode(machineCode);
        List<RobotTaskScheduleDTO> scheduleDTOS = robotTaskScheduleDao.selectSchedules(appCode, taskCode, 0); // 拿私有
        List<String> prvtFlowCodes = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(scheduleDTOS)) {
            prvtFlowCodes = scheduleDTOS.stream().map(it -> it.getFlowCode()).collect(Collectors.toList());
        }
        //那一次公共的除了私有外的
        List<RobotTaskScheduleDTO> scheduleDTOS1 = robotTaskScheduleDao.selectCommonSchedules(appCode, taskCode, prvtFlowCodes, 0); // 拿通用
        scheduleDTOS.addAll(scheduleDTOS1);
        if (CollectionUtils.isNotEmpty(flowCodes)) {
            Example example1 = new Example(RobotFlow.class);
            example1.createCriteria().andIn("flowCode", flowCodes).andEqualTo("appCode", appCode);
            List<RobotFlow> robotFlows = robotFlowDao.selectByExample(example1);
            if (CollectionUtils.isEmpty(robotFlows) || StringUtils.isBlank(robotFlows.get(0).getTagCode()) || !"10018008".equals(robotFlows.get(0).getTagCode())) {
                Optional<RobotTaskScheduleDTO> first = scheduleDTOS.stream().filter(flow -> {
                    String execCycle = flow.getExecCycle();
                    String execAreaTime = flow.getExecAreaTime();
                    Integer execStyle = flow.getExecStyle();
                    return flowCodes.contains(flow.getFlowCode()) && ScheduleUtils.matchSchedule(execCycle, execAreaTime, execStyle, new Date());
                }).findFirst();
                if (!first.isPresent()) {
                    if (!("在册名单".equals(serviceItemName) || "费用明细".equals(serviceItemName))) {
                        throw new BusinessException("当前时间不在执行计划内，未能触发执行");
                    }
                }
            }
            robotCommand.setCommand("runFlow");
            Map<String, String> argsMap = Maps.newHashMap();
            if (StringUtils.isNotBlank(serviceItemName) && Lists.newArrayList("增员", "减员", "补缴", "调基").contains(serviceItemName)) { // 如是这些服务项目需要加多一个回盘流程
                Example example2 = new Example(RobotFlow.class);
                example2.createCriteria().andEqualTo("appCode", appCode).andEqualTo("tagCode", "10018009");
                List<RobotFlow> robotFlows1 = robotFlowDao.selectByExample(example2);
                if (CollectionUtils.isNotEmpty(robotFlows1)) {
                    List<String> flowCodes1 = robotFlows1.stream().map(it -> it.getFlowCode()).collect(Collectors.toList());
                    flowCodes.addAll(flowCodes1);
                }
            }
            argsMap.put("flowCode", StringUtils.join(flowCodes, ","));
            if (StringUtils.isNotBlank(serviceItemName) && "登录".equals(serviceItemName)) {
                argsMap.put("loginFlow", "1");
            }
            robotCommand.setArgs(JsonUtils.beanToJson(argsMap));
        } else {
            Optional<RobotTaskScheduleDTO> first = scheduleDTOS.stream().filter(flow -> {
                String execCycle = flow.getExecCycle();
                String execAreaTime = flow.getExecAreaTime();
                Integer execStyle = flow.getExecStyle();
                return ScheduleUtils.matchSchedule(execCycle, execAreaTime, execStyle, new Date());
            }).findFirst();
            if (!first.isPresent()) {
                throw new BusinessException("当前时间不在执行计划内，未能触发执行");
            }
        }
        robotTaskService.startTask(robotCommand);
    }

    @Override
    public List<RobotTaskInfoDTO> getRobotPlant(Map<String, Object> params) {
        List<RobotTaskInfoDTO> list = robotTaskDao.getRobotTasks(params);
        if (CollectionUtils.isNotEmpty(list)) {
            Map<Integer, SysUser> userMap = Maps.newHashMap();
            list.stream().forEach(it -> {
                String appCode = it.getAppCode();
                String taskCode = it.getTaskCode();
                if (StringUtils.isNotBlank(appCode) && StringUtils.isNotBlank(taskCode)) {
                    List<RobotTaskScheduleDTO> scheduleDTOS = robotTaskScheduleDao.selectSchedules(appCode, taskCode, 0); // 拿私有
                    List<String> prvtFlowCodes = Lists.newArrayList();
                    if (CollectionUtils.isNotEmpty(scheduleDTOS)) {
                        prvtFlowCodes = scheduleDTOS.stream().map(item -> item.getFlowCode()).collect(Collectors.toList());
                    }
                    //那一次公共的除了私有外的
                    List<RobotTaskScheduleDTO> scheduleDTOS1 = robotTaskScheduleDao.selectCommonSchedules(appCode, taskCode, prvtFlowCodes, 0); // 拿通用
                    scheduleDTOS.addAll(scheduleDTOS1);
                    scheduleDTOS = scheduleDTOS.stream().filter(flow -> flow.getExecOrder()!=null).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(scheduleDTOS)) {
                        Optional<RobotTaskScheduleDTO> first = scheduleDTOS.stream().filter(item -> item.getTaskType() != null && item.getTaskType() == 2).findFirst();
                        if (first.isPresent()) {
                            it.setHaveCustom("是");
                        }else{
                            it.setHaveCustom("否");
                        }
                        scheduleDTOS.sort((o1, o2)->o1.getExecOrder()-o2.getExecOrder());
                        int index = 1;
                        for (RobotTaskScheduleDTO robotTaskScheduleDTO: scheduleDTOS) {
                            robotTaskScheduleDTO.setExecOrder(index);
                            index++;
                            StringBuffer stringBuffer = new StringBuffer();
                            String execCycle = robotTaskScheduleDTO.getExecCycle();
                            String[] split = execCycle.split("-");
                            stringBuffer.append("本月").append(split[0]).append("号").append("-").append(split[1]).append("号")
                                    .append("：").append(StringUtils.isBlank(robotTaskScheduleDTO.getExecAreaTime()) ? "实时" : robotTaskScheduleDTO.getExecAreaTime());
                            robotTaskScheduleDTO.setExecPlant(stringBuffer.toString());
                            Integer editId = robotTaskScheduleDTO.getEditId();
                            if (editId!=null) {
                                SysUser sysUser = null;
                                if (!userMap.containsKey(editId)) {
                                    sysUser = rpaAuthService.getSysUser(editId);
                                } else {
                                    sysUser = userMap.get(editId);
                                }
                                if (sysUser !=null) {
                                    robotTaskScheduleDTO.setEditName(sysUser.getName());
                                    userMap.put(editId, sysUser);
                                }
                            }
                        }
                    }
                    it.setScheduleList(scheduleDTOS);
                }
            });
        }
        return list;
    }

    private static <T> Predicate<T> distinctByVariable(Function<? super T, ?> keyExtractor) {
        HashMap<Object, Boolean> map = new HashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    @Override
    public Integer checkRegister(CustomerInsuredRegisterExVO taskVO) {
        Session session = SecurityContext.currentUser();
        CustomerInsuredRegisterExVO registerVO = new CustomerInsuredRegisterExVO();
        registerVO.setCustId(session.getCustId());
        registerVO.setAddrName(taskVO.getAddrName());
        registerVO.setBusinessType(taskVO.getBusinessType());
        registerVO.setAccountNumber(taskVO.getAccountNumber());
        registerVO.setTplTypeCode(taskVO.getTplTypeCode());
        registerVO.setDataMonth(taskVO.getDataMonth());
        CustomerInsuredRegisterExVO insuredRegisterVO = saasService.findRegister(registerVO);
        log.info("在册人数：{}", JSON.toJSONString(insuredRegisterVO));
        if (insuredRegisterVO == null) {
            log.info("没有在册人数，需要获取在册");
            return 1;//需要拿在册
        }
        CustomerInsuredRegisterExVO maxProcessTime = saasAgentService.getMaxProcessTime(registerVO);
        log.info("在册人数 maxProcessTime：{}", maxProcessTime);
        if (maxProcessTime != null && maxProcessTime.getDataMonth() != null) {
            if (insuredRegisterVO.getCreateTime().getTime() < maxProcessTime.getDataMonth().getTime()) {
                log.info("有增减员数据，在册时间小于最新的回盘时间，需要获取在册");
                return 1;//需要拿在册
            }
        } else {
            //如果没有增减员，需要校验是否：晚9:00~晚23:59
            Integer hour = Integer.parseInt(DateUtil.format(new Date(), "HH:mm").replace(":", ""));
            if (hour > 2100 && hour < 2359) {
                log.info("没有增减员数据，如果是时晚9:00~晚23:59，需要获取在册");
                return 1;//需要拿在册
            }
        }
        return 0;//不需要拿在册
    }

    @Override
    public List<RobotAccountStatusHistory> getAccountStatusHistory(String taskCode){
        Example example=new Example(RobotAccountStatusHistory.class);
        example.orderBy("createTime").desc();
        example.createCriteria().andEqualTo("taskCode",taskCode);
        List<RobotAccountStatusHistory> list=robotAccountStatusHistoryDao.selectByExample(example);
        return list;
    }

    @Override
    public List<RobotFlowStep> listFlowStep(RobotFlowStep flowStep) {
        Example example = new Example(RobotFlowStep.class);
        Example.Criteria ca = example.createCriteria();
        ca.andIsNull("groupCode");
        if (StringUtils.isNotBlank(flowStep.getFlowCode())) {
            ca.andEqualTo("flowCode", flowStep.getFlowCode());
        }
        if (StringUtils.isNotBlank(flowStep.getStepCode())) {
            ca.andEqualTo("stepCode", flowStep.getStepCode());
        }
        List<RobotFlowStep> steps = robotFlowStepDao.selectByExample(example);
        if (CollectionUtils.isEmpty(steps)) {
            return Lists.newArrayList();
        }
        if (StringUtils.isBlank(flowStep.getStepCode())) {
            return steps;
        }
        //当前步骤
        RobotFlowStep step = steps.stream().filter(s -> s.getStepCode().equals(flowStep.getStepCode())).findFirst().get();
        //步骤列表
        List<RobotFlowStep> stepList = steps.stream().filter(s -> s.getNumber() >= step.getNumber()).collect(Collectors.toList());
        //排序
        return stepList.stream().sorted(Comparator.comparingInt(RobotFlowStep::getNumber)).collect(Collectors.toList());
    }

    @Override
    public com.seebon.rpa.entity.robot.vo.RobotFlowVO getByFlowCode(String flowCode) {
        Example example = new Example(RobotFlow.class);
        example.createCriteria().andEqualTo("flowCode", flowCode);
        RobotFlow flow = robotFlowDao.selectOneByExample(example);
        com.seebon.rpa.entity.robot.vo.RobotFlowVO flowVO = new com.seebon.rpa.entity.robot.vo.RobotFlowVO();
        BeanUtils.copyProperties(flow, flowVO);
        return flowVO;
    }

    @Override
    public String stopAccount(RobotTaskNotice taskNotice) {
        Example example = new Example(RobotTaskNotice.class);
        example.createCriteria().andEqualTo("executionCode", taskNotice.getExecutionCode());
        RobotTaskNotice notice = robotTaskNoticeDao.selectOneByExample(example);
        if (notice != null) {
            return "已存在通知";
        }
        TodayExecQueryDTO queryDTO = new TodayExecQueryDTO();
        queryDTO.setTaskCode(taskNotice.getTaskCode());
        List<TodayExecDataDTO> taskList = robotTaskDao.getRobotTaskExecList(queryDTO);
        if (CollectionUtils.isEmpty(taskList)) {
            return "不存在任务";
        }
        TodayExecDataDTO task = taskList.get(0);

        Date regainTime = DateUtil.offsetDay(new Date(), 1);
        String remark = "存在未缴费款项导致当前月无法申报，已暂停至：" + DateUtil.formatDate(regainTime);
        //保存通知记录
        taskNotice.setClientId(task.getClientId());
        taskNotice.setMachineCode(task.getMachineCode());
        taskNotice.setCompanyName(task.getOrgName());
        taskNotice.setAccountNumber(task.getAccountNumber());
        taskNotice.setStopTime(new Date());
        taskNotice.setRegainTime(regainTime);
        taskNotice.setRegainStatus(0);
        taskNotice.setCreateTime(new Date());
        taskNotice.setUpdateTime(new Date());
        taskNotice.setError(remark);
        robotTaskNoticeDao.insertSelective(taskNotice);
        //停用申报账户
        robotTaskDao.updateStatus(task.getAppCode(), taskNotice.getTaskCode(), 0, remark);
        //停用任务队列
        taskQueueService.stopTask(task.getTaskCode(), remark);
        //停用历史记录
        Session user = SecurityContext.currentUser();
        RobotAccountStatusHistory accountStatusHistory = new RobotAccountStatusHistory();
        accountStatusHistory.setTaskCode(taskNotice.getTaskCode());
        accountStatusHistory.setStatus(0);
        accountStatusHistory.setRemark(remark);
        accountStatusHistory.setCreateId((int) user.getId());
        accountStatusHistory.setCreateName(user.getName());
        accountStatusHistory.setCreateTime(new Date());
        robotAccountStatusHistoryDao.insertSelective(accountStatusHistory);
        return "1";
    }

    @Override
    public void loginSuccess(ChangeLoginStatusDTO statusDTO) {
        loginAuthService.updateLoginStatus(statusDTO);
        taskQueueService.updateLoginStatus(statusDTO);
    }
}
