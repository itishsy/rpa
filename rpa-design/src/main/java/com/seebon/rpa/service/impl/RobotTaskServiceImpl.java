package com.seebon.rpa.service.impl;

import cn.hutool.core.date.BetweenFormatter;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.seebon.common.utils.JsonUtils;
import com.seebon.common.utils.UUIDGenerator;
import com.seebon.rpa.BusinessException;
import com.seebon.rpa.auth.SecurityContext;
import com.seebon.rpa.auth.Session;
import com.seebon.rpa.constant.DynamicFieldType;
import com.seebon.rpa.entity.agent.dto.CustOrgTaskDTO;
import com.seebon.rpa.entity.agent.dto.CustomerOrgBusinessDTO;
import com.seebon.rpa.entity.agent.enums.BusinessTypeEnum;
import com.seebon.rpa.entity.auth.po.SysUser;
import com.seebon.rpa.entity.robot.*;
import com.seebon.rpa.entity.robot.dto.DeclareAccountBaseDTO;
import com.seebon.rpa.entity.robot.dto.RobotTaskScheduleDTO;
import com.seebon.rpa.entity.robot.dto.TaskInfoDTO;
import com.seebon.rpa.entity.robot.dto.design.RobotExecutionMo;
import com.seebon.rpa.entity.robot.dto.design.RobotTaskDataDTO;
import com.seebon.rpa.entity.robot.dto.design.RobotTaskParamsDTO;
import com.seebon.rpa.entity.robot.dto.monitor.TodayExecDataDTO;
import com.seebon.rpa.entity.robot.dto.monitor.TodayExecQueryDTO;
import com.seebon.rpa.entity.robot.enums.RobotClientStatusEnum;
import com.seebon.rpa.entity.robot.vo.*;
import com.seebon.rpa.entity.saas.dto.register.RegisterRecentlyQueryDTO;
import com.seebon.rpa.entity.saas.po.declare.account.DeclareAccountItem;
import com.seebon.rpa.entity.saas.vo.system.SysUserDeclareAccountVO;
import com.seebon.rpa.entity.sms.vo.ForwardConfigVO;
import com.seebon.rpa.entity.system.User;
import com.seebon.rpa.remote.RpaAuthService;
import com.seebon.rpa.remote.RpaSaasAgentService;
import com.seebon.rpa.remote.RpaSaasService;
import com.seebon.rpa.repository.mongodb.RobotExecutionRepository;
import com.seebon.rpa.repository.mysql.*;
import com.seebon.rpa.service.RobotAppService;
import com.seebon.rpa.service.RobotTaskQueueService;
import com.seebon.rpa.service.RobotTaskService;
import com.seebon.rpa.service.impl.component.RobotTaskQueueComponent;
import com.seebon.rpa.utils.BeanCopyUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.persistence.Column;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RobotTaskServiceImpl implements RobotTaskService {
    @Autowired
    private RobotTaskDao robotTaskDao;
    @Autowired
    private RobotTaskArgsDao robotTaskArgsDao;
    @Autowired
    private RobotArgsDefineDao argsDefineDao;
    @Autowired
    private RobotAppDao robotAppDao;
    @Autowired
    private RobotAppFormGroupDao formGroupDao;
    @Autowired
    private RobotAppFormDao formDao;
    @Autowired
    private RobotAppConditionDao conditionDao;
    @Autowired
    private RobotAppGroupDao groupDao;
    @Autowired
    private RobotAppGeneralPlanDao generalPlanDao;
    @Autowired
    private RobotCommandDao commandDao;
    @Autowired
    private RobotClientDao robotClientDao;
    @Autowired
    private RobotAppService robotAppService;
    @Autowired
    private RobotExecutionDao robotExecutionDao;
    @Autowired
    private RobotClientTaskDao clientTaskDao;
    @Autowired
    private RobotTaskScheduleDao robotTaskScheduleDao;
    @Autowired
    private RpaSaasService rpaSaasService;
    @Autowired
    private RpaSaasAgentService saasAgentService;
    @Autowired
    private RpaAuthService rpaAuthService;
    @Autowired
    private RobotAccountStatusHistoryDao robotAccountStatusHistoryDao;
    @Autowired
    private RobotTaskNoticeDao robotTaskNoticeDao;
    @Autowired
    private RobotTaskQueueDao taskQueueDao;
    @Autowired
    private RobotTaskQueueService taskQueueService;
    @Autowired
    private RobotTaskQueueComponent taskQueueComponent;

    //单位名称
    private static final String COMPANY_NAME = "companyName";
    //纳税人识别号
    private static final String COMPANY_NUMBER = "companyNumber";
    //单位社保号
    private static final String SOCIAL_NUMBER = "socialNumber";
    //公积金号
    private static final String ACCFUND_NUMBER = "accfundNumber";
    //经办人手机号
    private static final String FORWARD_PHONE = "forwardPhone";
    //接收器手机号
    private static final String RECEIVER_PHONE = "receiverPhone";

    @Override
    public List<RobotClientAppVO> listTaskByCustId() {
        List<SysUserDeclareAccountVO> userDeclareAccountFundList = new ArrayList<>();
        List<SysUserDeclareAccountVO> userDeclareAccountSocialList = new ArrayList<>();
        User user = SecurityContext.currentUser();
        List<String> roles = user.getRoles();
        if (CollectionUtils.isEmpty(roles) || !roles.contains("admin")) {
            userDeclareAccountFundList = rpaSaasService.getUserDeclareAccountList((int) user.getId(), BusinessTypeEnum.Accfund.getCode());
            userDeclareAccountSocialList = rpaSaasService.getUserDeclareAccountList((int) user.getId(), BusinessTypeEnum.Social.getCode());
        }
        List<SysUserDeclareAccountVO> userDeclareAccountList = new ArrayList<>();
        Session session = SecurityContext.currentUser();
        List<RobotClientAppVO> clientApps = robotTaskDao.selectByClientId(session.getCustId(), userDeclareAccountList);
        if (CollectionUtils.isEmpty(clientApps)) {
            return Lists.newArrayList();
        }
        for (RobotClientAppVO clientApp : clientApps) {
            List<RobotTaskVO> robotTaskVOS = clientApp.getRobotTaskVOS();
            if (CollectionUtils.isEmpty(robotTaskVOS) && roles.contains("admin")) {
                continue;
            }
            JSONObject appArgs = JSONObject.parseObject(clientApp.getAppArgs());
            Integer addrId = appArgs.getInteger("addrId");

            String type = appArgs.getString("businessType");
            String businessType = "1001001".equals(type) ? "1" : "2";
            List<RobotTaskVO> robotTaskVOSByFilter = Lists.newArrayList();
            if (CollectionUtils.isEmpty(roles) || !roles.contains("admin")) {
                if ("1".equals(businessType)) {
                    if (CollectionUtils.isEmpty(userDeclareAccountSocialList)) {
                        clientApp.setRobotTaskVOS(Lists.newArrayList());
                        continue;
                    } else {
                        List<SysUserDeclareAccountVO> socialList = userDeclareAccountSocialList.stream().filter(e -> e.getAddrId().equals(addrId)).collect(Collectors.toList());
                        List<String> socialAccountList = socialList.stream().map(SysUserDeclareAccountVO::getAccountNumber).collect(Collectors.toList());
                        robotTaskVOSByFilter = robotTaskVOS.stream().filter(e -> socialAccountList.contains(e.getAccountNumber())).collect(Collectors.toList());
                    }

                }
                if ("2".equals(businessType)) {
                    if (CollectionUtils.isEmpty(userDeclareAccountFundList) ) {
                        clientApp.setRobotTaskVOS(Lists.newArrayList());
                        continue;
                    } else {
                        List<SysUserDeclareAccountVO> fundList = userDeclareAccountFundList.stream().filter(e -> e.getAddrId().equals(addrId)).collect(Collectors.toList());
                        List<String> fundAccountList = fundList.stream().map(SysUserDeclareAccountVO::getAccountNumber).collect(Collectors.toList());
                        robotTaskVOSByFilter = robotTaskVOS.stream().filter(e -> fundAccountList.contains(e.getAccountNumber())).collect(Collectors.toList());
                    }
                }
            } else {
                robotTaskVOSByFilter = robotTaskVOS;
            }
            for (RobotTaskVO robotTaskVO : robotTaskVOSByFilter) {
                String accountNumber = robotTaskVO.getAccountNumber();
                if (StringUtils.isEmpty(accountNumber)) {
                    robotTaskVO.setAwaitDeclareNumber(0);
                } else {
                    Integer awaitDeclare = saasAgentService.awaitDeclareNumber(businessType, accountNumber, addrId);
                    robotTaskVO.setAwaitDeclareNumber(awaitDeclare);
                    robotTaskVO.setBusinessType(businessType);
                }
                Example example = new Example(RobotTaskArgs.class);
                example.createCriteria().andEqualTo("taskCode", robotTaskVO.getTaskCode()).andEqualTo("argsKey", "companyName");
                List<RobotTaskArgs> robotTaskArgs = robotTaskArgsDao.selectByExample(example);
                if (CollectionUtils.isEmpty(robotTaskArgs)) {
                    continue;
                }
                String declareAccount = robotTaskArgs.get(0).getArgsValue();
                robotTaskVO.setDeclareAccount(declareAccount);
                Integer completedCount = saasAgentService.getLastCompletedCount(session.getCustId(), addrId, Integer.valueOf(businessType), accountNumber);
                robotTaskVO.setLastCompletedCount(completedCount);
            }
            clientApp.setRobotTaskVOS(robotTaskVOSByFilter);

        }
        clientApps = clientApps.stream().filter(e -> e.isFlag()).collect(Collectors.toList());
        return clientApps;
    }

    @Override
    public List<RobotClientVO> listTaskTableByCustId(String keyword, String hasNotice) {
        List<SysUserDeclareAccountVO> userDeclareAccountFundList = new ArrayList<>();
        List<SysUserDeclareAccountVO> userDeclareAccountSocialList = new ArrayList<>();
        User user = SecurityContext.currentUser();
        List<String> roles = user.getRoles();
        if (CollectionUtils.isEmpty(roles) || !roles.contains("admin")) {
            userDeclareAccountFundList = rpaSaasService.getUserDeclareAccountList((int) user.getId(), BusinessTypeEnum.Accfund.getCode());
            userDeclareAccountSocialList = rpaSaasService.getUserDeclareAccountList((int) user.getId(), BusinessTypeEnum.Social.getCode());
//            if (CollectionUtils.isEmpty(userDeclareAccountFundList) && CollectionUtils.isEmpty(userDeclareAccountSocialList)) {
//                return new ArrayList<>();
//            }
        }
        List<SysUserDeclareAccountVO> userDeclareAccountList = new ArrayList<>();
        Session session = SecurityContext.currentUser();
        Example robotClientExample = new Example(RobotClient.class);
        robotClientExample.createCriteria().andEqualTo("clientId", session.getCustId());
        robotClientExample.orderBy("loginTime");
        List<RobotClient> robotClients = robotClientDao.selectByExample(robotClientExample);
        List<RobotClientVO> robotClientVOS = BeanCopyUtils.copyListProperties(robotClients, RobotClientVO::new);
        List<SysUserDeclareAccountVO> finalUserDeclareAccountSocialList1 = userDeclareAccountSocialList;
        List<SysUserDeclareAccountVO> finalUserDeclareAccountFundList1 = userDeclareAccountFundList;

        List<SysUser> allCustUser = rpaAuthService.getAllCustUser();
        if (CollectionUtils.isNotEmpty(allCustUser)) {
            allCustUser = allCustUser.stream().filter(it -> it.getCustId().equals(session.getCustId())).collect(Collectors.toList());
        }
        List<SysUser> custUsers = allCustUser;
        final String keyWord = StringUtils.isNotBlank(keyword)?keyword.replaceAll("%", "\\\\%"):null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        robotClientVOS.parallelStream().forEach(robotClientVO -> {
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String machineCode = robotClientVO.getMachineCode();
            Integer integer = robotExecutionDao.selectDataNumberByMachineCode(machineCode);
            robotClientVO.setTotalDeclareNumber(integer);

            robotClientVO.setStatusName(RobotClientStatusEnum.getStatusNameByCode(robotClientVO.getStatus()));

            String businessTypeStr = null;
            if (StringUtils.isNotBlank(keyWord)) {
                if ("社保".contains(keyWord)) {
                    businessTypeStr = "1001001";
                }
                if ("公积金".contains(keyWord)) {
                    businessTypeStr = "1001002";
                }
            }
            Map<String, Object> params = Maps.newHashMap();
            params.put("clientId", session.getCustId());
            params.put("machineCode", robotClientVO.getMachineCode());
            params.put("keyword", keyWord);
            params.put("businessTypeStr", businessTypeStr);
            params.put("userDeclareAccountList", userDeclareAccountList);
            List<RobotClientAppVO> clientApps = robotTaskDao.selectTableTaskVO(params);
            String substring = robotClientVO.getMachineCode().substring(robotClientVO.getMachineCode().length() - 4);
            if (CollectionUtils.isEmpty(clientApps)) {
                robotClientVO.setFlag(false);//如果没有查到符合的数据则不进行展示
            } else {
                for (RobotClientAppVO clientApp : clientApps) {
                    List<RobotTaskVO> robotTaskVOS = clientApp.getRobotTaskVOS();
                    if (CollectionUtils.isEmpty(robotTaskVOS) && roles.contains("admin")) {
                        continue;
                    }
                    JSONObject appArgs = JSONObject.parseObject(clientApp.getAppArgs());
                    Integer addrId = appArgs.getInteger("addrId");
                    String addrName = appArgs.getString("addrName");
                    String type = appArgs.getString("businessType");
                    String businessType = "1001001".equals(type) ? "1" : "2";
                    List<RobotTaskVO> robotTaskVOSByFilter = Lists.newArrayList();
                    if (CollectionUtils.isEmpty(roles) || !roles.contains("admin")) {
                        if ("1".equals(businessType)) {
                            if (CollectionUtils.isEmpty(finalUserDeclareAccountSocialList1)) {
                                clientApp.setRobotTaskVOS(Lists.newArrayList());
                                continue;
                            } else {
                                List<SysUserDeclareAccountVO> socialList = finalUserDeclareAccountSocialList1.stream().filter(e -> e.getAddrId().equals(addrId)).collect(Collectors.toList());
                                List<String> socialAccountList = socialList.stream().map(SysUserDeclareAccountVO::getAccountNumber).collect(Collectors.toList());
                                robotTaskVOSByFilter = robotTaskVOS.stream().filter(e -> socialAccountList.contains(e.getAccountNumber())).collect(Collectors.toList());
                            }
                        }
                        if ("2".equals(businessType)) {
                            if (CollectionUtils.isEmpty(finalUserDeclareAccountFundList1)) {
                                clientApp.setRobotTaskVOS(Lists.newArrayList());
                                continue;
                            } else {
                                List<SysUserDeclareAccountVO> fundList = finalUserDeclareAccountFundList1.stream().filter(e -> e.getAddrId().equals(addrId)).collect(Collectors.toList());
                                List<String> fundAccountList = fundList.stream().map(SysUserDeclareAccountVO::getAccountNumber).collect(Collectors.toList());
                                robotTaskVOSByFilter = robotTaskVOS.stream().filter(e -> fundAccountList.contains(e.getAccountNumber())).collect(Collectors.toList());
                            }
                        }
                    } else {
                        robotTaskVOSByFilter = robotTaskVOS;
                    }

                    for (RobotTaskVO robotTaskVO : robotTaskVOSByFilter) {
                        String accountNumber = robotTaskVO.getAccountNumber();
                        if (StringUtils.isEmpty(accountNumber)) {
                            robotTaskVO.setAwaitDeclareNumber(0);
                        } else {
                            Integer awaitDeclare = saasAgentService.awaitDeclareNumber(businessType, accountNumber, addrId);
                            robotTaskVO.setAwaitDeclareNumber(awaitDeclare);
                            robotTaskVO.setBusinessType(businessType);
                        }

                        RegisterRecentlyQueryDTO recentlyQueryDTO = RegisterRecentlyQueryDTO.builder()
                                .addrName(addrName)
                                .accountNumber(accountNumber)
                                .businessType(Integer.valueOf(businessType)).build();
                        boolean accountRegisterRecently = rpaSaasService.isAccountRegisterRecently(recentlyQueryDTO);
                        robotTaskVO.setIsAccountRegisterRecently(accountRegisterRecently);

                        if (robotTaskVO.getStatus() == 0) {
                            String editName = robotTaskVO.getEditName();
                            if (StringUtils.isNotBlank(editName)) {
                                Optional<SysUser> first = custUsers.stream().filter(it -> it.getName().equals(editName)).findFirst();
                                if (!first.isPresent()) {
                                    robotTaskVO.setComment("【设备更新中】");
                                }
                            } else {
                                robotTaskVO.setComment("【设备更新中】");
                            }
                            robotTaskVO.setRobotTaskNotice(robotTaskNoticeDao.selectByTaskCode(robotTaskVO.getTaskCode()));
                        }
                        if (StringUtils.isBlank(robotTaskVO.getDeclareAccount())) {
                            continue;
                        }

//                    Example example = new Example(RobotTaskArgs.class);
//                    example.createCriteria().andEqualTo("taskCode", robotTaskVO.getTaskCode()).andEqualTo("argsKey", "companyName");
//                    List<RobotTaskArgs> robotTaskArgs = robotTaskArgsDao.selectByExample(example);
//                    if (CollectionUtils.isEmpty(robotTaskArgs)) {
//                        continue;
//                    }
//                    String declareAccount = robotTaskArgs.get(0).getArgsValue();
//                    robotTaskVO.setDeclareAccount(robotTaskArgs.get(0).getArgsValue());
                        Integer lastCompletedCount = saasAgentService.getLastCompletedCount(session.getCustId(), addrId,
                                Integer.parseInt(businessType), accountNumber);
                        robotTaskVO.setLastCompletedCount(lastCompletedCount);

                        String appCode = robotTaskVO.getAppCode();
                        String taskCode = robotTaskVO.getTaskCode();
                        if (StringUtils.isNotBlank(appCode) && StringUtils.isNotBlank(taskCode)) {
                            List<RobotTaskScheduleDTO> scheduleDTOS = robotTaskScheduleDao.selectSchedules(appCode, taskCode, 0); // 拿私有
                            List<String> prvtFlowCodes = Lists.newArrayList();
                            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(scheduleDTOS)) {
                                prvtFlowCodes = scheduleDTOS.stream().map(item -> item.getFlowCode()).collect(Collectors.toList());
                            }
                            //那一次公共的除了私有外的
                            List<RobotTaskScheduleDTO> scheduleDTOS1 = robotTaskScheduleDao.selectCommonSchedules(appCode, taskCode, prvtFlowCodes, 0); // 拿通用
                            scheduleDTOS.addAll(scheduleDTOS1);
                            scheduleDTOS = scheduleDTOS.stream().filter(it -> it.getServiceItem()!=null && Lists.newArrayList(1, 2, 3, 5).contains(it.getServiceItem())).collect(Collectors.toList());
                            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(scheduleDTOS)) {
                                List<String> days = Lists.newArrayList();
                                List<String> hours = Lists.newArrayList();
                                scheduleDTOS.stream().filter(item -> item.getExecStyle()==1 || item.getExecStyle()==2).forEach(item -> {
                                    String execCycle = item.getExecCycle();
                                    Integer execStyle = item.getExecStyle();
                                    String[] split = execCycle.split("-");
                                    days.addAll(Lists.newArrayList(split));
                                    String execAreaTime = item.getExecAreaTime();
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
                                if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(days) && org.apache.commons.collections4.CollectionUtils.isNotEmpty(hours)) {
                                    days.sort((o1,o2)->Integer.valueOf(o1)-Integer.valueOf(o2));
                                    hours.sort((o1,o2)->Integer.valueOf(o1)-Integer.valueOf(o2));
                                    robotTaskVO.setSchedule("当月".concat(days.get(0)).concat("号-").concat(days.get(days.size()-1)).concat("号：").concat(hours.get(0)).concat(":00-").concat(hours.get(hours.size()-1)).concat(":00"));
                                }
                            }
                        }
                    }
                    if ("1".equals(hasNotice)) {
                        robotTaskVOSByFilter = robotTaskVOS.stream().filter(e -> e.getRobotTaskNotice() != null).collect(Collectors.toList());
                    }
                    if (CollectionUtils.isEmpty(robotTaskVOSByFilter) && StringUtils.isNotBlank(hasNotice)) {
                        clientApp.setFlag(false);
                    }
                    clientApp.setRobotTaskVOS(robotTaskVOSByFilter);
                }
                clientApps = clientApps.stream().filter(e -> e.isFlag()).collect(Collectors.toList());
                robotClientVO.setRobotClientAppVOS(clientApps);
                String seebotName = substring + "(" + clientApps.size() + ")";
                robotClientVO.setSeebotName(seebotName);
                if (CollectionUtils.isEmpty(clientApps)) {
                    robotClientVO.setFlag(false);//如果没有查到符合的数据则不进行展示
                }
                if (robotClientVO.getStatus().equals(RobotClientStatusEnum.Executing.getStatus())) {
                    Optional<RobotClientAppVO> first = clientApps.stream().filter(it -> {
                        List<RobotTaskVO> robotTaskVOS = it.getRobotTaskVOS();
                        return CollectionUtils.isNotEmpty(robotTaskVOS) &&
                                robotTaskVOS.stream().filter(task -> task.getRun() != null && task.getRun() == 1).findFirst().isPresent();
                    }).findFirst();
                    if (first.isPresent()) {
                        Optional<RobotTaskVO> first1 = first.get().getRobotTaskVOS().stream().filter(task -> task.getRun() != null && task.getRun() == 1).findFirst();
                        robotClientVO.setExecutingAccount(first1.get().getDeclareAccount().concat("-").concat(first1.get().getAccountNumber()));
                        if (StringUtils.isBlank(robotClientVO.getExecutingAccount())) {
                            Example example = new Example(RobotTaskQueue.class);
                            example.createCriteria().andEqualTo("machineCode", robotClientVO.getMachineCode()).andEqualTo("status", 1);
                            List<RobotTaskQueue> list = taskQueueDao.selectByExample(example);
                            if (CollectionUtils.isNotEmpty(list)) {
                                robotClientVO.setExecutingAccount(list.get(0).getCompanyName().concat("-").concat(list.get(0).getDeclareAccount()));
                            }
                        }
                    }
                }
            }
        });
        robotClientVOS = robotClientVOS.stream().filter(e -> e.isFlag()).collect(Collectors.toList());

        return robotClientVOS;
    }

    @Override
    public List<RobotAppFormVO> queryFormVO(String appCode) {
        Example example = new Example(RobotAppFormGroup.class);
        example.createCriteria().andEqualTo("appCode", appCode).andEqualTo("isDelete", 0);
        List<RobotAppFormGroup> formGroups = formGroupDao.selectByExample(example);
        List<Integer> formIds = formGroups.stream().map(RobotAppFormGroup::getFormId).distinct().collect(Collectors.toList());
        List<RobotAppFormVO> robotAppFormVOList = Lists.newArrayList();
        for (Integer formId : formIds) {
            RobotAppConfigForm robotAppConfigForm = formDao.selectByPrimaryKey(formId);
            RobotAppFormVO robotAppFormVO = new RobotAppFormVO();
            Optional.ofNullable(robotAppConfigForm).ifPresent(s -> BeanUtils.copyProperties(s, robotAppFormVO));
            Example example1 = new Example(RobotAppFormGroup.class);
            example1.createCriteria().andEqualTo("formId", formId).andEqualTo("appCode", appCode);
            List<RobotAppFormGroup> formGroupList = formGroupDao.selectByExample(example1);
            List<RobotAppFormGroup> robotAppFormGroups = formGroupList.stream().filter(s -> s.getIsDelete().equals(0)).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(robotAppFormGroups)) {
                robotAppFormVO.setSort(robotAppFormGroups.get(0).getId());
            }
            List<Integer> groupIds = robotAppFormGroups.stream().map(RobotAppFormGroup::getGroupId).collect(Collectors.toList());
            List<RobotAppGroupVO2> robotAppGroupVOS = Lists.newArrayList();
            if (CollectionUtils.isNotEmpty(groupIds)) {
                Example group1Exa = new Example(RobotAppConfigGroup.class);
                group1Exa.createCriteria().andIn("id", groupIds);
                List<RobotAppConfigGroup> robotAppConfigGroups = groupDao.selectByExample(group1Exa);
                robotAppGroupVOS = BeanCopyUtils.copyListProperties(robotAppConfigGroups, RobotAppGroupVO2::new);
            }
            for (RobotAppGroupVO2 robotAppGroupVO : robotAppGroupVOS) {
                Example example2 = new Example(RobotAppFormGroup.class);
                example2.createCriteria().andEqualTo("isDelete", 0).andEqualTo("appCode", appCode).andEqualTo("formId", formId).andEqualTo("groupId", robotAppGroupVO.getId());
                RobotAppFormGroup formGroup = formGroupDao.selectOneByExample(example2);
                robotAppGroupVO.setSort(formGroup.getId());
                Example example3 = new Example(RobotArgsDefine.class);
                example3.createCriteria().andEqualTo("scope", "app").andEqualTo("formGroupId", formGroup.getId()).andEqualTo("isDelete", 0);
                List<RobotArgsDefine> robotArgsDefines = argsDefineDao.selectByExample(example3);
                Optional<RobotArgsDefine> first = robotArgsDefines.stream().filter(it -> it.getDisplayOrder() == null).findFirst();
                if (first.isPresent()) {
                    robotArgsDefines.sort(Comparator.comparing(RobotArgsDefine::getId));
                    for (int i = 1; i<=robotArgsDefines.size(); i++) {
                        robotArgsDefines.get(i-1).setDisplayOrder(i);
                    }
                } else {
                    robotArgsDefines.sort(Comparator.comparing(RobotArgsDefine::getDisplayOrder).thenComparing(RobotArgsDefine::getId));
                }
                List<RobotArgsDefineVO2> list = Lists.newArrayList();
                for (RobotArgsDefine robotArgsDefine : robotArgsDefines) {
                    RobotArgsDefineVO2 robotArgsDefineVO = new RobotArgsDefineVO2();
                    BeanUtils.copyProperties(robotArgsDefine, robotArgsDefineVO);
                    Example example4 = new Example(RobotAppConfigCondition.class);
                    example4.createCriteria().andEqualTo("argsDefineId", robotArgsDefine.getId());
                    List<RobotAppConfigCondition> conditions = conditionDao.selectByExample(example4);
                    robotArgsDefineVO.setConfigConditions(conditions);
                    list.add(robotArgsDefineVO);
                }
                robotAppGroupVO.setRobotArgsDefineVO2s(list);
            }
            List<RobotAppGroupVO2> groupVOS = robotAppGroupVOS.stream().sorted(Comparator.comparingInt(RobotAppGroupVO2::getSort)).collect(Collectors.toList());
            robotAppFormVO.setRobotAppGroupVO2s(groupVOS);
            robotAppFormVOList.add(robotAppFormVO);
        }
        return robotAppFormVOList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer updateStatus(String appCode, Integer status, String taskCode, String comment) {
        if (status == 0 && StringUtils.isBlank(comment)) {
            throw new BusinessException("停用任务必须填写原因！");
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
        //中断任务队列
        taskQueueService.stopTask(taskCode, comment);

        return robotTaskDao.updateTask(robotTask);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Integer batchEnableOrStop(RobotTaskDataDTO dto) {
        List<String> taskCodes = dto.getTaskCodes();
        if (CollectionUtils.isEmpty(taskCodes)) {
            throw new BusinessException("申报账户必填！");
        }
        Integer status = dto.getStatus();
        if (status == null) {
            throw new BusinessException("调整状态必填！");
        }
        String comment = dto.getComment();
        if (status == 0 && StringUtils.isBlank(comment)) {
            throw new BusinessException("停用任务必须填写原因！");
        }
        Session session = SecurityContext.currentUser();
        int count = 0;
        for (String taskCode: taskCodes) {
            Example example = new Example(RobotTask.class);
            example.createCriteria().andEqualTo("taskCode", taskCode);
            RobotTask robotTask = robotTaskDao.selectOneByExample(example);
            if (robotTask !=null) {
                //更新同步状态
                clientTaskDao.cleanSyncTime(taskCode);

                robotTask.setStatus(status);
                robotTask.setComment(comment);
                robotTask.setEditName(session.getName());
                robotTask.setUpdateTime(new Date());
                if (robotTask.getStatus() == 1) {
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
                robotTaskDao.updateTask(robotTask);
                taskQueueService.stopTask(taskCode, comment);
                count++;
            }
        }
        return count;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer addTask(List<RobotTaskArgs> robotTaskArgs, String appCode, String machineCode) {
        List<RobotTaskArgs> accountList = robotTaskArgs.stream().filter(RobotTaskArgs ->RobotTaskArgs.getArgsKey().equals(SOCIAL_NUMBER)||
                RobotTaskArgs.getArgsKey().equals(ACCFUND_NUMBER)).collect(Collectors.toList());
        Map<String, String> accountMap = accountList.stream().collect(Collectors.toMap(RobotTaskArgs::getArgsKey, RobotTaskArgs::getArgsValue));
        Session session = SecurityContext.currentUser();
        Example taskExam = new Example(RobotTask.class);
        taskExam.createCriteria().andEqualTo("appCode", appCode);
        List<RobotTask> robotTasks = robotTaskDao.selectByExample(taskExam);
        //启动应用收集客户申报信息时，需一并生成申报账户信息
        //http://192.168.0.87:8080/browse/RPA-905
        Example e = new Example(RobotApp.class);
        e.createCriteria().andEqualTo("appCode", appCode);
        RobotApp robotApp = robotAppDao.selectOneByExample(e);
        DeclareAccountBaseDTO dto = new DeclareAccountBaseDTO();
        List<RobotTaskArgs> orgNames = robotTaskArgs.stream().filter(s -> COMPANY_NAME.equals(s.getArgsKey())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(orgNames)) {
            throw new BusinessException("不存在单位名称，请先配置单位名称");
        }
        JSONObject appArgs = JSONObject.parseObject(robotApp.getAppArgs());
        String businessTypeVal = appArgs.getString("businessType");
        Integer addrId = appArgs.getInteger("addrId");
        dto.setOrgName(orgNames.get(0).getArgsValue());
        dto.setAddressId(addrId);
        List<RobotTaskArgs> taxs = robotTaskArgs.stream().filter(s -> COMPANY_NUMBER.equals(s.getArgsKey())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(taxs)) {
            dto.setTaxNumber("");
        } else {
            String argsValue = taxs.get(0).getArgsValue();
            dto.setTaxNumber(argsValue);
        }
        int businessType = "1001001".equals(businessTypeVal) ? 1 : 2;
        int t = 0;
        if (businessType == 1) {
            List<RobotTaskArgs> argsList = robotTaskArgs.stream().filter(s -> SOCIAL_NUMBER.equals(s.getArgsKey())).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(argsList)) {
                throw new BusinessException("请先配置单位社保号");
            }
            t++;
        } else {
            List<RobotTaskArgs> argsList = robotTaskArgs.stream().filter(s -> ACCFUND_NUMBER.equals(s.getArgsKey())).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(argsList)) {
                throw new BusinessException("请先配置单位公积金号");
            }
            t++;
        }
        if (t > 1) {
            throw new BusinessException("单位社保号和公积金号只能存在一个，请确保您的配置信息正确！");
        }
        List<DeclareAccountItem> itemList = getDeclareAccountItemList(robotTaskArgs, businessType);
        dto.setDeclareAccountItems(itemList);
        dto.setBusinessType(businessType);
        User user = SecurityContext.currentUser();
        dto.setPermissionId((int) user.getId());
        int count=0;
        if(accountMap.containsKey(SOCIAL_NUMBER)){
            int accountCount=rpaSaasService.getAccountCount(accountMap.get(SOCIAL_NUMBER),addrId,businessType);
            if(accountCount>0){
                count++;
            }

        }
        if(accountMap.containsKey(ACCFUND_NUMBER)){
            int accountCount=rpaSaasService.getAccountCount(accountMap.get(ACCFUND_NUMBER),addrId,businessType);
            if(accountCount>0){
                count++;
            }
        }
        if(count==0){ //申报配置新增的账号如果在申报账号已经存在则不新增
            Integer saveDeclareAccount = robotAppService.saveDeclareAccount(dto);
            if (saveDeclareAccount == -1) {
                throw new BusinessException("地区错误！");
            }
        }
        if (CollectionUtils.isNotEmpty(robotTasks)) {
            RobotAppVO robotAppVO = robotAppDao.selectOneRobotAppVO(appCode);
            int id = robotTasks.size() + 1;
            RobotTask robotTask = new RobotTask();
            Example generalExam = new Example(RobotGeneralPlan.class);
            generalExam.createCriteria().andEqualTo("appCode", appCode);
            RobotGeneralPlan robotGeneralPlan = generalPlanDao.selectOneByExample(generalExam);
            robotTask.setTaskName(robotAppVO.getAppName() + "执行任务" + id);
            String uuidStringWithoutLine = UUIDGenerator.uuidStringWithoutLine();
            robotTask.setTaskCode(uuidStringWithoutLine);
            robotTask.setMachineCode(machineCode);
            robotTask.setAppCode(appCode);
            robotTask.setStatus(0);
            robotTask.setCreateTime(new Date());
            robotTask.setClientId(session.getCustId());
            //给任务初始化默认通用计划
            robotTask.setTaskType(1);
            robotTask.setExecCycle(robotGeneralPlan.getExecCycle());
            robotTask.setExecAreaTime(robotGeneralPlan.getExecAreaTime());
            robotTask.setExecStyle(robotGeneralPlan.getExecStyle());
            String execCycle = robotGeneralPlan.getExecCycle();
            String[] split = execCycle.split("-");
            String str = split[0] + "号" + "-" + split[1] + "号";
            String areaTime = "";
            if (robotGeneralPlan.getExecStyle() == 1) {
                String execAreaTime = robotGeneralPlan.getExecAreaTime();
                String[] strings = execAreaTime.split("-");
                areaTime = strings[0] + "时" + "-" + strings[1] + "时";
            }
            if (robotGeneralPlan.getExecStyle() == 2) {
                areaTime = robotGeneralPlan.getExecAreaTime();
            }
            if (robotGeneralPlan.getExecStyle() == 3) {
                areaTime = "实时";
            }
            String schedule = str + areaTime;
            robotTask.setSchedule(schedule);
            robotTaskDao.insertSelective(robotTask);
            robotTaskArgs.forEach(s -> s.setTaskCode(uuidStringWithoutLine));
            //保存短信转发配置
            this.saveRobotTaskArgs(robotAppVO, robotTaskArgs, robotTask.getTaskCode());
            return robotTaskArgsDao.insertList(robotTaskArgs);
        } else {
            RobotAppVO robotAppVO = robotAppDao.selectOneRobotAppVO(appCode);
            RobotTask robotTask = new RobotTask();
            Example generalExam = new Example(RobotGeneralPlan.class);
            generalExam.createCriteria().andEqualTo("appCode", appCode);
            RobotGeneralPlan robotGeneralPlan = generalPlanDao.selectOneByExample(generalExam);
            String stringWithoutLine = UUIDGenerator.uuidStringWithoutLine();
            robotTask.setAppCode(appCode);
            robotTask.setMachineCode(machineCode);
            robotTask.setTaskName(robotAppVO.getAppName() + "执行任务" + 1);
            robotTask.setTaskCode(stringWithoutLine);
            robotTask.setStatus(0);
            robotTask.setCreateTime(new Date());
            robotTask.setClientId(session.getCustId());
            //给任务初始化默认通用计划
            robotTask.setTaskType(1);
            robotTask.setExecCycle(robotGeneralPlan.getExecCycle());
            robotTask.setExecAreaTime(robotGeneralPlan.getExecAreaTime());
            robotTask.setExecStyle(robotGeneralPlan.getExecStyle());
            String execCycle = robotGeneralPlan.getExecCycle();
            String[] split = execCycle.split("-");
            String str = split[0] + "号" + "-" + split[1] + "号";
            String areaTime = "";
            if (robotGeneralPlan.getExecStyle() == 1) {
                String execAreaTime = robotGeneralPlan.getExecAreaTime();
                String[] strings = execAreaTime.split("-");
                areaTime = strings[0] + "时" + "-" + strings[1] + "时";
            }
            if (robotGeneralPlan.getExecStyle() == 2) {
                areaTime = robotGeneralPlan.getExecAreaTime();
            }
            if (robotGeneralPlan.getExecStyle() == 3) {
                areaTime = "实时";
            }
            String schedule = str + areaTime;
            robotTask.setSchedule(schedule);
            robotTaskDao.insertSelective(robotTask);
            robotTaskArgs.forEach(robotTaskArgs1 -> robotTaskArgs1.setTaskCode(stringWithoutLine));
            //保存短信转发配置
            this.saveRobotTaskArgs(robotAppVO, robotTaskArgs, robotTask.getTaskCode());
            return robotTaskArgsDao.insertList(robotTaskArgs);
        }
    }

    @Override
    public void check(List<RobotTaskArgs> robotTaskArgs, String appCode,Integer addrId) {
        Integer custId = SecurityContext.currentUser().getCustId();
        Optional<RobotTaskArgs> first = robotTaskArgs.stream().filter(s -> COMPANY_NAME.equals(s.getArgsKey())).findFirst();
        if (!first.isPresent() || StringUtils.isBlank(first.get().getArgsValue())) {
            throw new BusinessException("申报单位信息必填");
        }
        if (robotTaskArgs.stream().anyMatch(s -> SOCIAL_NUMBER.equals(s.getArgsKey()) || ACCFUND_NUMBER.equals(s.getArgsKey()))) {
            List<RobotTaskArgs> social = robotTaskArgs.stream().filter(s -> SOCIAL_NUMBER.equals(s.getArgsKey())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(social)) {
                RobotTaskArgs taskArgs = social.get(0);

                if (StringUtils.isBlank(taskArgs.getArgsValue())) {
                    throw new BusinessException("单位社保号必填");
                }

                RobotTaskArgs appAndArgsKey = robotTaskArgsDao.selectByAppAndArgsKey(appCode, taskArgs.getArgsKey(), taskArgs.getArgsValue(), null, null);
                if (Objects.nonNull(appAndArgsKey)) {
                    throw new BusinessException("单位社保号已存在");
                }


                Map<String,Object> params = Maps.newHashMap();
                params.put("accountNumber", taskArgs.getArgsValue());
                params.put("businessType", 1);
                params.put("addrId", addrId);
                params.put("orgName", first.get().getArgsValue());
                Integer accountNumber = rpaSaasService.getAccountNumber(params);
                if(accountNumber!=null && accountNumber>0){
                    throw new BusinessException("单位社保号已存在系统");
                }

//                List<DeclareAccountBaseDTO> accountItem=rpaSaasService.getAllAccount(taskArgs.getArgsValue(),1,0,addrId);
               /* List<DeclareAccountBaseDTO> declareAccountItem=rpaSaasService.getAllAccount(taskArgs.getArgsValue(),1, (int)SecurityContext.currentUser().getId(),addrId);
                List<DeclareAccountBaseDTO> declareAccountBaseDTOS=rpaSaasService.getAllAccount(taskArgs.getArgsValue(),1, (int)SecurityContext.currentUser().getId(),addrId);
                if(CollectionUtils.isNotEmpty(accountItem)&&CollectionUtils.isEmpty(declareAccountItem)&&CollectionUtils.isNotEmpty(declareAccountBaseDTOS)){*/
                /*if(CollectionUtils.isNotEmpty(accountItem)){
                    throw new BusinessException("单位社保号已存在系统");
                }*/

            }
            List<RobotTaskArgs> accfund = robotTaskArgs.stream().filter(s -> ACCFUND_NUMBER.equals(s.getArgsKey())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(accfund)) {
                RobotTaskArgs taskArgs = accfund.get(0);
                if (StringUtils.isBlank(taskArgs.getArgsValue())) {
                    throw new BusinessException("单位公积金号必填");
                }
                RobotTaskArgs byAppAndArgsKey = robotTaskArgsDao.selectByAppAndArgsKey(appCode, taskArgs.getArgsKey(), taskArgs.getArgsValue(), null, null);
                if (Objects.nonNull(byAppAndArgsKey)) {
                    throw new BusinessException("单位公积金号已存在");
                }

                Map<String,Object> params = Maps.newHashMap();
                params.put("accountNumber", taskArgs.getArgsValue());
                params.put("businessType", 1);
                params.put("addrId", addrId);
                params.put("orgName", first.get().getArgsValue());
                Integer accountNumber = rpaSaasService.getAccountNumber(params);
                if(accountNumber!=null && accountNumber>0){
                    throw new BusinessException("单位社保号已存在系统");
                }

//                List<DeclareAccountBaseDTO> accountItem=rpaSaasService.getAllAccount(taskArgs.getArgsValue(),2,0,0);
               /* List<DeclareAccountBaseDTO> declareAccountItem=rpaSaasService.getAllAccount(taskArgs.getArgsValue(),2, (int)SecurityContext.currentUser().getId(),0);
                List<DeclareAccountBaseDTO> declareAccountBaseDTOS=rpaSaasService.getAllAccount(taskArgs.getArgsValue(),2, (int)SecurityContext.currentUser().getId(),addrId);
                if(CollectionUtils.isNotEmpty(accountItem)&&CollectionUtils.isEmpty(declareAccountItem)&&CollectionUtils.isNotEmpty(declareAccountBaseDTOS)){*/
                /*if(CollectionUtils.isNotEmpty(accountItem)){
                    throw new BusinessException("单位公积金号已存在系统");
                }*/
            }
            /*Example taskEx = new Example(RobotTask.class);
            taskEx.createCriteria().andEqualTo("appCode", appCode);
            List<RobotTask> robotTasks = robotTaskDao.selectByExample(taskEx);
            List<String> taskCodes = robotTasks.stream().map(ta -> ta.getTaskCode()).collect(Collectors.toList());

            String argsValue = robotTaskArgs.stream().filter(s -> "companyName".equals(s.getArgsKey())).map(ar -> ar.getArgsValue()).findFirst().get();
            Example example = new Example(RobotTaskArgs.class);
            example.createCriteria().andEqualTo("argsValue", argsValue).andEqualTo("argsKey", "companyName");
            List<RobotTaskArgs> robotTaskArgsList = robotTaskArgsDao.selectByExample(example);
            List<RobotTaskArgs> list = Lists.newArrayList();
            for (String taskCode : taskCodes) {
                List<RobotTaskArgs> collect = robotTaskArgsList.stream().filter(s -> s.getTaskCode().equals(taskCode)).collect(Collectors.toList());
                list.addAll(collect);
            }
            if (CollectionUtils.isNotEmpty(list)) {
                throw new BusinessException("该帐号已存在机器人");
            }*/
        }
    }

    @Override
    public void checkEdit(List<RobotTaskArgs> taskArgs, String appCode) {
        Integer custId = SecurityContext.currentUser().getCustId();
        if (taskArgs.stream().anyMatch(s -> SOCIAL_NUMBER.equals(s.getArgsKey()))) {
            List<RobotTaskArgs> social = taskArgs.stream().filter(s -> SOCIAL_NUMBER.equals(s.getArgsKey())).collect(Collectors.toList());
            RobotTaskArgs args = social.get(0);
            RobotTaskArgs robotTaskArgs = robotTaskArgsDao.selectByAppAndArgsKey(appCode, args.getArgsKey(), args.getArgsValue(), null, args.getId());
            if (Objects.nonNull(robotTaskArgs)) {
                throw new BusinessException("单位社保号已存在");
            }
        }
        if (taskArgs.stream().anyMatch(s -> ACCFUND_NUMBER.equals(s.getArgsKey()))) {
            List<RobotTaskArgs> args = taskArgs.stream().filter(s -> ACCFUND_NUMBER.equals(s.getArgsKey())).collect(Collectors.toList());
            RobotTaskArgs robotTaskArgs = args.get(0);
            RobotTaskArgs selectByAppAndArgsKey = robotTaskArgsDao.selectByAppAndArgsKey(appCode, robotTaskArgs.getArgsKey(), robotTaskArgs.getArgsValue(), null, robotTaskArgs.getId());
            if (Objects.nonNull(selectByAppAndArgsKey)) {
                throw new BusinessException("单位公积金号已存在");
            }
        }
    }

    @Override
    public List<RobotTaskArgs> echoTaskArgs(String taskCode) {
        Example example = new Example(RobotTaskArgs.class);
        example.createCriteria().andEqualTo("taskCode", taskCode);
        example.orderBy("displayOrder");
        List<RobotTaskArgs> taskArgs = robotTaskArgsDao.selectByExample(example);
        for (RobotTaskArgs args : taskArgs) {
            if (StringUtils.isBlank(args.getArgsValue()) || StringUtils.isBlank(args.getArgsType())) {
                continue;
            }
            if (DynamicFieldType.photoUpload.equals(DynamicFieldType.valueOf(args.getArgsType())) || DynamicFieldType.fileUpload.equals(DynamicFieldType.valueOf(args.getArgsType()))) {
                List<FileStoreVO> fileList = rpaSaasService.getByFileIds(args.getArgsValue());
                args.setArgsValue(JSON.toJSONString(fileList));
            }
        }
        return taskArgs;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer editArgs(List<RobotTaskArgs> robotTaskArgs, String taskCode, String originalCompanyName, String originalAccountNumber) {
        Integer custId = SecurityContext.currentUser().getCustId();
        long createId = SecurityContext.currentUser().getId();
        RobotTaskArgs taskArgs = robotTaskArgs.stream().filter(s -> COMPANY_NAME.equals(s.getArgsKey())).findFirst().orElseGet(RobotTaskArgs::new);


        RobotApp robotApp = robotAppDao.selectAppByTaskCode(taskCode);
        JSONObject jsonObject = JSONObject.parseObject(robotApp.getAppArgs());
        String businessTypeVal = jsonObject.getString("businessType");
        Integer addrId = jsonObject.getInteger("addrId");
        DeclareAccountBaseDTO dto = new DeclareAccountBaseDTO();
        dto.setCustId(custId);
        dto.setCreateId((int)createId);
        dto.setOriginalCompanyName(originalCompanyName);
        dto.setOriginalAccountNumber(originalAccountNumber);
        dto.setOrgName(taskArgs.getArgsValue());
        dto.setAddressId(addrId);
        List<RobotTaskArgs> collect = robotTaskArgs.stream().filter(s -> SOCIAL_NUMBER.equals(s.getArgsKey())).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(collect)) {
            dto.setBusinessType(1);
        } else {
            dto.setBusinessType(2);
        }
        List<RobotTaskArgs> taxs = robotTaskArgs.stream().filter(s -> COMPANY_NUMBER.equals(s.getArgsKey())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(taxs)) {
            dto.setTaxNumber("");
        } else {
            String argsValue = taxs.get(0).getArgsValue();
            dto.setTaxNumber(argsValue);
        }
        int businessType = "1001001".equals(businessTypeVal) ? 1 : 2;
        String nowValue = null;
        int t = 0;
        if (businessType == 1) {
            List<RobotTaskArgs> argsList = robotTaskArgs.stream().filter(s -> SOCIAL_NUMBER.equals(s.getArgsKey())).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(argsList)) {
                throw new BusinessException("请先配置单位社保号");
            }
            t++;
            nowValue = argsList.get(0).getArgsValue();
        } else {
            List<RobotTaskArgs> argsList = robotTaskArgs.stream().filter(s -> ACCFUND_NUMBER.equals(s.getArgsKey())).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(argsList)) {
                throw new BusinessException("请先配置单位公积金号");
            }
            t++;
            nowValue = argsList.get(0).getArgsValue();
        }
        if (t > 1) {
            throw new BusinessException("单位社保号和公积金号只能存在一个，请确保您的配置信息正确！");
        }
        List<DeclareAccountItem> itemList = getDeclareAccountItemList(robotTaskArgs, businessType);
        dto.setDeclareAccountItems(itemList);
        if (originalAccountNumber.equals(nowValue)) {
            dto.setAddItems(false);
        }

        Integer editDeclareAccount = rpaSaasService.editDeclareAccount(dto);
        if (editDeclareAccount == -1) {
            throw new BusinessException("此申报账户已被其他公司绑定，不能使用");
        }
        Example example = new Example(RobotTaskArgs.class);
        example.createCriteria().andEqualTo("taskCode", taskCode);
        robotTaskArgsDao.deleteByExample(example);
        robotTaskArgs.forEach(s -> s.setTaskCode(taskCode));
        int res = robotTaskArgsDao.insertList(robotTaskArgs);
        if (res > 0) {
            robotTaskDao.cleanSyncTime(taskCode);
            clientTaskDao.cleanSyncTime(taskCode);
        }
        //保存短信转发配置
        this.saveRobotTaskArgs(robotApp, robotTaskArgs, taskCode);
        return res;
    }

    /**
     * 保存短信转发配置
     *
     * @param robotTaskArgs
     * @param taskCode
     */
    private void saveRobotTaskArgs(RobotApp robotApp, List<RobotTaskArgs> robotTaskArgs, String taskCode) {
        JSONObject jsonObject = JSONObject.parseObject(robotApp.getAppArgs());
        String businessType = jsonObject.getString("businessType");
        Integer addrId = jsonObject.getInteger("addrId");
        Optional<RobotTaskArgs> forwardPhone = robotTaskArgs.stream().filter(s -> FORWARD_PHONE.equals(s.getArgsKey())).findFirst();
        Optional<RobotTaskArgs> receiverPhone = robotTaskArgs.stream().filter(s -> RECEIVER_PHONE.equals(s.getArgsKey())).findFirst();
        if (forwardPhone.isPresent() && receiverPhone.isPresent()) {
            ForwardConfigVO config = new ForwardConfigVO();
            config.setTaskCode(taskCode);
            config.setReceiverPhone(receiverPhone.get().getArgsValue());//接收器手机号
            config.setForwardPhone(forwardPhone.get().getArgsValue());//经办人手机号
            config.setAddrId(addrId);
            config.setBusinessType(businessType);
            if (config.getForwardPhone().equals(config.getReceiverPhone())) {
                throw new BusinessException("接收器手机号不能和经办人手机号一样.");
            }
            rpaSaasService.saveForwardConfig(config);
        }
    }

    @Override
    public void addTaskCommand(Integer clientId, String addrName, String accountNumber, Integer businessType) {
        Map<String,Object> params = Maps.newHashMap();
        params.put("clientId", clientId);
        params.put("addrName", addrName);
        params.put("accountNumber", accountNumber);
        params.put("businessType", businessType);
        RobotTask task = robotTaskDao.selectRobotTaskByParams(params);
        if (task != null && task.getStatus() == 1) {
            RobotCommand robotCommand = new RobotCommand();
            robotCommand.setClientId(task.getClientId());
            robotCommand.setAppCode(task.getAppCode());
            robotCommand.setTaskCode(task.getTaskCode());
            robotCommand.setMachineCode(task.getMachineCode());
            try {
                startTask(robotCommand);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("=====添加机器人任务执行指令失败：{}", e.getMessage(), e);
            }
        }
    }

    @Override
    public Map<String, Object> runTestCommand(String taskCode, String flowCode, String stepCode) {
        Example example = new Example(RobotTask.class);
        example.createCriteria().andEqualTo("taskCode", taskCode);
        RobotTask task = robotTaskDao.selectOneByExample(example);

        Map<String, String> args = Maps.newHashMap();
        args.put("flowCode", flowCode);
        args.put("stepCode", stepCode);

        RobotCommand command = new RobotCommand();
        command.setClientId(task.getClientId());
        command.setAppCode(task.getAppCode());
        command.setTaskCode(task.getTaskCode());
        command.setMachineCode(task.getMachineCode());
        command.setCommand("runTestFlow");
        command.setArgs(JSON.toJSONString(args));
        command.setCreateTime(new Date());
        command.setUpdateTime(new Date());

        Map<String, Object> resultMap = Maps.newHashMap();
        try {
            startTask(command);
        } catch (Exception e) {
            resultMap.put("errorMsg", e.getMessage());
            log.error("远程运行流程，添加机器人任务执行指令异常：{}", e.getMessage(), e);
        }
        resultMap.put("uuid", command.getUuid());
        return resultMap;
    }

    @Override
    public Integer getTestTaskStatus(String taskCode) {
        Example example = new Example(RobotTask.class);
        example.createCriteria().andEqualTo("taskCode", taskCode);
        RobotTask task = robotTaskDao.selectOneByExample(example);
        return task.getRun();
    }

    @Override
    public List<CustomerOrgBusinessDTO> getCustOrgBusiness(Integer clientId, String addrName, String orgName) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("clientId", clientId);
        params.put("addrName", addrName);
        params.put("orgName", orgName);
        params.put("serviceItem", 7);// 名册名单
        List<CustOrgTaskDTO> taskDTOS = robotTaskDao.getRobotTaskByParams(params);

        List<CustomerOrgBusinessDTO> result = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(taskDTOS)) {
            List<String> socialTaskCodes = Lists.newArrayList();
            List<String> accfundTaskCodes = Lists.newArrayList();
            List<CustOrgTaskDTO> socialStopTaskList = Lists.newArrayList();
            List<CustOrgTaskDTO> accfundStopTaskList = Lists.newArrayList();
            taskDTOS.stream().forEach(it -> {
                String appArgs = it.getAppArgs();
                if (appArgs.contains("1001001")) {
                    socialTaskCodes.add(it.getTaskCode());
                    if (it.getStatus() == 0) {
                        socialStopTaskList.add(it);
                    }
                } else {
                    accfundTaskCodes.add(it.getTaskCode());
                    if (it.getStatus() == 0) {
                        accfundStopTaskList.add(it);
                    }
                }
            });
            result = taskDTOS.stream().map(it -> {
                String appArgs = it.getAppArgs();
                if (appArgs.contains("1001001")) {
                    return 1;
                } else {
                    return 2;
                }
            }).collect(Collectors.toSet()).stream().map(it -> {
                List<CustOrgTaskDTO> orgTaskDTOS = it == 1 ? socialStopTaskList : accfundStopTaskList;
                CustomerOrgBusinessDTO dto = new CustomerOrgBusinessDTO();
                dto.setBusinessType(it);
                dto.setBusinessTypeName(BusinessTypeEnum.getNameByCode(it));
                dto.setStatus(orgTaskDTOS.size() > 0 ? 0 : 1); // 0停用 1启用
                dto.setComment(StringUtils.join(orgTaskDTOS.parallelStream().filter(x->
                        StringUtils.isNotEmpty(x.getComment())).map(CustOrgTaskDTO::getComment)
                        .collect(Collectors.toList()), ";"));
                int h5LoginCount = 0;
                if (it == 1) {
                    h5LoginCount = robotTaskDao.selectH5LoginCount(socialTaskCodes);
                } else {
                    h5LoginCount = robotTaskDao.selectH5LoginCount(accfundTaskCodes);
                }
                dto.setIsH5Login(h5LoginCount>0?1:0);
                return dto;
            }).collect(Collectors.toList());
        }
        return result;
    }

    @Override
    public String runTask(Integer clientId, String addrName, String orgName, List<Integer> businessTypes) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("clientId", clientId);
        params.put("addrName", addrName);
        params.put("orgName", orgName);
        params.put("serviceItem", 7);// 名册名单
        if (CollectionUtils.isNotEmpty(businessTypes) && businessTypes.size() == 1) {
            params.put("businessType", businessTypes.get(0) == 1 ? "1001001" : "1001002");
        }
        List<CustOrgTaskDTO> taskDTOS = robotTaskDao.getRobotTaskByParams(params);
        if (CollectionUtils.isNotEmpty(taskDTOS)) {
            for (CustOrgTaskDTO task : taskDTOS) {
                List<String> flowCodes = task.getFlows().stream().map(flow -> flow.getFlowCode()).collect(Collectors.toList());
                //taskQueueService.runTask(task.getTaskCode(), task.getMachineCode(), flowCodes, "在册名单");
                taskQueueComponent.runTask(task.getTaskCode(), task.getMachineCode(), flowCodes, "在册名单",1,Boolean.FALSE);
            }
        }
        return "机器人已启动执行";
    }

    @Override
    public String startTask(RobotCommand commandVo) {
        Integer custId = commandVo.getClientId();
        if (commandVo.getClientId() == null) {
            custId = SecurityContext.currentUser().getCustId();
            commandVo.setClientId(custId);
        }
        String appCode = commandVo.getAppCode();
        String taskCode = commandVo.getTaskCode();
        Example example = new Example(RobotCommand.class);
        example.createCriteria().andEqualTo("clientId", custId).andEqualTo("machineCode", commandVo.getMachineCode())
                .andEqualTo("appCode", appCode)
                .andEqualTo("taskCode", taskCode)
                .andEqualTo("command", StringUtils.defaultIfBlank(commandVo.getCommand(), "run")).andEqualTo("status", 0);
        List<RobotCommand> commands = commandDao.selectByExample(example);
        if (CollectionUtils.isNotEmpty(commands)) {
            throw new BusinessException("当前任务已存在队列里,请等待上一个指令执行完成。");
        }

        String result = "机器人正在执行中，已将该指令添加至队列，稍后运行";

        String machineCode = commandVo.getMachineCode();
        Example exa = new Example(RobotClient.class);
        exa.createCriteria().andEqualTo("clientId", commandVo.getClientId()).andEqualTo("machineCode", machineCode);
        RobotClient robotClient = robotClientDao.selectOneByExample(exa);
        if (robotClient == null) {
            throw new BusinessException(String.format("未能找到客户对应{%s}的盒子，请确认配置是否正确", machineCode));
        } else {
            if (robotClient.getStatus() == null) {
                throw new BusinessException(String.format("未能识别盒子当前状态，未能触发执行，请联系管理员处理！"));
            }
            if (robotClient.getStatus()!=1 && robotClient.getStatus() != 2) {
                if (robotClient.getStatus().equals(RobotClientStatusEnum.Upgrading.getStatus())) {
                    throw new BusinessException(String.format("盒子当前处于{%s}状态，未能触发执行，请等待升级完毕！", RobotClientStatusEnum.getStatusNameByCode(robotClient.getStatus())));
                } else {
                    throw new BusinessException(String.format("盒子当前处于{%s}状态，未能触发执行，请联系管理员处理！", RobotClientStatusEnum.getStatusNameByCode(robotClient.getStatus())));
                }
            } else if (robotClient.getStatus().equals(RobotClientStatusEnum.Ready.getStatus())) {
                result = "机器人已启动执行";
            }
        }
        String uuid = UUIDGenerator.uuidStringWithoutLine();
        commandVo.setUuid(uuid);
        RobotCommand command = new RobotCommand();
        command.setClientId(custId);
        command.setMachineCode(commandVo.getMachineCode());
        command.setAppCode(commandVo.getAppCode());
        command.setTaskCode(commandVo.getTaskCode());
        command.setUuid(uuid);
        command.setCommand(StringUtils.defaultIfBlank(commandVo.getCommand(), "run"));
        command.setArgs(commandVo.getArgs());
        command.setCreateId((int) SecurityContext.currentUser().getId());
        command.setCreateName(SecurityContext.currentUser().getName());
        command.setCreateTime(new Date());
        command.setUpdateTime(new Date());
        commandDao.insertSelective(command);
        return result;
    }

    @Override
    public Integer selectCustAccount() {
        return this.listTaskByCustId().size();
    }

    @Override
    public List<RobotCommandVO> getRobotCommandExeList() {
        Session session = SecurityContext.currentUser();
        List<RobotCommandVO> robotCommandExeList = commandDao.getRobotCommandExeList(session.getCustId());
        if(CollectionUtils.isNotEmpty(robotCommandExeList)) {
            Example e = new Example(RobotCommand.class);
            e.createCriteria().andEqualTo("clientId", session.getCustId()).andEqualTo("tipStatus", 0).andNotEqualTo("status", 0);
            RobotCommand command = new RobotCommand();
            command.setTipStatus(1);
            commandDao.updateByExampleSelective(command, e);
        }
        return robotCommandExeList;
    }

    @Override
    public Boolean checkTaskRunning(List<TaskInfoDTO> taskInfoList) {
        Integer count = 0;
        if (CollectionUtils.isNotEmpty(taskInfoList)) {
            Map<String, Object> params = Maps.newHashMap();
            params.put("businessType", taskInfoList.get(0).getBusinessType());
            params.put("list", taskInfoList);
            count = robotTaskDao.selectRunningCount(params);
        }
        return count!=null && count>0;
    }

    private List<DeclareAccountItem> getDeclareAccountItemList(List<RobotTaskArgs> taskArgs, Integer businessType) {
        List<DeclareAccountItem> itemList = Lists.newArrayList();
        List<RobotTaskArgs> socials = taskArgs.stream().filter(s -> SOCIAL_NUMBER.equals(s.getArgsKey())).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(socials)) {
            socials.forEach(account -> {
                DeclareAccountItem item = new DeclareAccountItem();
                item.setBussinssType(businessType);
                item.setAccountNumber(account.getArgsValue());
                itemList.add(item);
            });
        }
        List<RobotTaskArgs> accfunds = taskArgs.stream().filter(s -> ACCFUND_NUMBER.equals(s.getArgsKey())).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(accfunds)) {
            accfunds.forEach(account -> {
                DeclareAccountItem item = new DeclareAccountItem();
                item.setBussinssType(businessType);
                item.setAccountNumber(account.getArgsValue());
                itemList.add(item);
            });
        }
        return itemList;
    }

    @Override
    public List<RobotTaskNotice> listTaskNotice() {
        Session user = SecurityContext.currentUser();
        Example example = new Example(RobotTaskNotice.class);
        example.createCriteria().andEqualTo("clientId", user.getCustId()).andEqualTo("regainStatus", 0);
        return robotTaskNoticeDao.selectByExample(example);
    }

    @Override
    public void regainTask(String appCode, String taskCode) {
        //恢复申报账户
        robotTaskDao.updateStatus(appCode, taskCode, 1, "");
        //恢复历史记录
        Session user = SecurityContext.currentUser();
        RobotAccountStatusHistory accountStatusHistory = new RobotAccountStatusHistory();
        accountStatusHistory.setTaskCode(taskCode);
        accountStatusHistory.setStatus(0);
        accountStatusHistory.setRemark("恢复");
        accountStatusHistory.setCreateId((int) user.getId());
        accountStatusHistory.setCreateName(user.getName());
        accountStatusHistory.setCreateTime(new Date());
        robotAccountStatusHistoryDao.insertSelective(accountStatusHistory);

        //修改通知状态为已恢复
        robotTaskNoticeDao.updateRegainStatus(1, taskCode);
    }

    @Override
    public void delayTask(String taskCode, String payDate) {
        Example example = new Example(RobotTaskNotice.class);
        example.createCriteria().andEqualTo("taskCode", taskCode).andEqualTo("regainStatus", 0);
        List<RobotTaskNotice> notices = robotTaskNoticeDao.selectByExample(example);
        if (CollectionUtils.isEmpty(notices)) {
            return;
        }
        for (RobotTaskNotice notice : notices) {
            Date regainTime = DateUtil.parseDateTime(payDate + " 00:00:00");
            String remark = "存在未缴费款项导致当前月无法申报，已暂停至：" + DateUtil.formatDateTime(regainTime);

            RobotTaskNotice update = new RobotTaskNotice();
            update.setId(notice.getId());
            update.setPayTime(regainTime);
            update.setRegainTime(regainTime);
            update.setError(remark);
            update.setUpdateTime(new Date());
            robotTaskNoticeDao.updateByPrimaryKeySelective(update);

            robotTaskDao.updateComment(taskCode, remark);

            //恢复历史记录
            Session user = SecurityContext.currentUser();
            RobotAccountStatusHistory accountStatusHistory = new RobotAccountStatusHistory();
            accountStatusHistory.setTaskCode(taskCode);
            accountStatusHistory.setStatus(0);
            accountStatusHistory.setRemark(remark);
            accountStatusHistory.setCreateId((int) user.getId());
            accountStatusHistory.setCreateName(user.getName());
            accountStatusHistory.setCreateTime(new Date());
            robotAccountStatusHistoryDao.insertSelective(accountStatusHistory);
        }
    }

    @Override
    public RobotTaskDataDTO getTaskList(RobotTaskParamsDTO dto) {

        RobotTaskDataDTO result = new RobotTaskDataDTO();

        List<RobotTaskVO> taskList = Lists.newArrayList();
        User user = SecurityContext.currentUser();

        Session session = SecurityContext.currentUser();
        Example robotClientExample = new Example(RobotClient.class);
        robotClientExample.createCriteria().andEqualTo("clientId", session.getCustId());
        List<RobotClient> robotClients = robotClientDao.selectByExample(robotClientExample);
        Boolean isStop = dto.getIsStop();
        String addrName = dto.getAddrName();
        if (CollectionUtils.isNotEmpty(robotClients)) {
            List<SysUserDeclareAccountVO> userDeclareAccountFundList = rpaSaasService.getUserDeclareAccountList((int) user.getId(), BusinessTypeEnum.Accfund.getCode());
            List<SysUserDeclareAccountVO> userDeclareAccountSocialList = rpaSaasService.getUserDeclareAccountList((int) user.getId(), BusinessTypeEnum.Social.getCode());

            List<String> businessTypes = dto.getBusinessTypes();
            robotClients.parallelStream().forEach(robotClient -> {
                if (businessTypes.contains("1001002") && CollectionUtils.isNotEmpty(userDeclareAccountFundList)) {

                    Map<String, Object> params = Maps.newHashMap();
                    params.put("clientId", session.getCustId());
                    params.put("machineCode", robotClient.getMachineCode());
                    params.put("addrName", addrName);
                    params.put("businessType", "1001002");

                    List<RobotClientAppVO> clientApps = robotTaskDao.selectTableTaskVO(params);
                    for (RobotClientAppVO clientApp : clientApps) {
                        List<RobotTaskVO> robotTaskVOS = clientApp.getRobotTaskVOS();
                        JSONObject appArgs = JSONObject.parseObject(clientApp.getAppArgs());
                        Integer addrId = appArgs.getInteger("addrId");
                        String addrName1 = appArgs.getString("addrName");
                        List<RobotTaskVO> taskVOList = filterTaskVOList(userDeclareAccountFundList, robotTaskVOS, addrId, isStop);
                        if (CollectionUtils.isNotEmpty(taskVOList)) {
                            taskList.addAll(taskVOList);
                        }
                    }
                }

                if (businessTypes.contains("1001001") && CollectionUtils.isNotEmpty(userDeclareAccountSocialList)) {
                    Map<String, Object> params = Maps.newHashMap();
                    params.put("clientId", session.getCustId());
                    params.put("machineCode", robotClient.getMachineCode());
                    params.put("addrName", addrName);
                    params.put("businessType", "1001001");
                    List<RobotClientAppVO> clientApps = robotTaskDao.selectTableTaskVO(params);
                    for (RobotClientAppVO clientApp : clientApps) {
                        List<RobotTaskVO> robotTaskVOS = clientApp.getRobotTaskVOS();
                        JSONObject appArgs = JSONObject.parseObject(clientApp.getAppArgs());
                        Integer addrId = appArgs.getInteger("addrId");
                        String addrName1 = appArgs.getString("addrName");
                        List<RobotTaskVO> taskVOList = filterTaskVOList(userDeclareAccountSocialList, robotTaskVOS, addrId, isStop);
                        if (CollectionUtils.isNotEmpty(taskVOList)) {
                            taskList.addAll(taskVOList);
                        }
                    }
                }
            });
        }

        if (CollectionUtils.isNotEmpty(taskList)) {
            List<String> taskCodes = taskList.stream().map(it -> it.getTaskCode()).collect(Collectors.toList());
            result.setTaskCodes(taskCodes);
            List<Map<String,Object>> taskData = Lists.newArrayList();
            if (isStop!=null && isStop) {
                Map<String, Object> map = Maps.newHashMap();
                map.put("status", "已停用");
                map.put("count", taskList.size());
                taskData.add(map);
            } else {
                List<Integer> runStatus = Lists.newArrayList(0, 1);
                Map<Integer, List<RobotTaskVO>> group = taskList.stream().collect(Collectors.groupingBy(RobotTaskVO::getRun));
                for (int i=0; i<runStatus.size(); i++) {
                    String status = runStatus.get(i)==0?"未运行":"运行中";
                    List<RobotTaskVO> taskVOList = group.get(runStatus.get(i));
                    Map<String, Object> map = Maps.newHashMap();
                    map.put("status", status);
                    map.put("count", CollectionUtils.isNotEmpty(taskVOList)?taskVOList.size():0);
                    taskData.add(map);
                }
            }
            result.setTaskData(taskData);
        }
        return result;
    }

    private List<RobotTaskVO> filterTaskVOList(List<SysUserDeclareAccountVO> userDeclareAccountVOList, List<RobotTaskVO> robotTaskVOS,
                                               Integer addrId, Boolean isStop) {
        List<SysUserDeclareAccountVO> accountVOList = userDeclareAccountVOList.stream().filter(e -> e.getAddrId().equals(addrId)).collect(Collectors.toList());
        List<String> accountList = accountVOList.stream().map(SysUserDeclareAccountVO::getAccountNumber).collect(Collectors.toList());
        List<RobotTaskVO> taskVOList = Lists.newArrayList();

        if (isStop!=null && isStop) {
            taskVOList = robotTaskVOS.stream().filter(e -> accountList.contains(e.getAccountNumber()) && e.getStatus()==0).collect(Collectors.toList());
        } else {
            taskVOList = robotTaskVOS.stream().filter(e -> accountList.contains(e.getAccountNumber())).collect(Collectors.toList());
        }
        return taskVOList;
    }
}
