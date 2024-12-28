package com.seebon.rpa.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.seebon.common.utils.DateUtil;
import com.seebon.rpa.auth.SecurityContext;
import com.seebon.rpa.auth.Session;
import com.seebon.rpa.entity.robot.*;
import com.seebon.rpa.entity.robot.dto.RobotExecutionDTO;
import com.seebon.rpa.entity.robot.dto.design.OperationRequestVo;
import com.seebon.rpa.entity.robot.dto.design.RobotExecutionDetailMo;
import com.seebon.rpa.entity.robot.dto.design.RobotExecutionMo;
import com.seebon.rpa.entity.robot.enums.FlowTypeEnum;
import com.seebon.rpa.entity.robot.enums.RobotAppTypeEnum;
import com.seebon.rpa.entity.robot.enums.WarnStatus;
import com.seebon.rpa.entity.robot.vo.FileStoreVO;
import com.seebon.rpa.entity.robot.vo.RobotAppVO;
import com.seebon.rpa.entity.saas.DevUserAddr;
import com.seebon.rpa.remote.RpaSaasService;
import com.seebon.rpa.repository.mongodb.RobotExecutionRepository;
import com.seebon.rpa.repository.mysql.*;
import com.seebon.rpa.service.OperationMonitorService;
import com.seebon.rpa.service.RobotExecutionService;
import com.seebon.rpa.service.RobotWarnMessageService;
import com.seebon.rpa.service.impl.component.RobotExecFailMessageComponent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(rollbackOn = Exception.class)
public class RobotExecutionServiceImpl implements RobotExecutionService {
    @Autowired
    private RobotTaskDao robotTaskDao;
    @Autowired
    private RobotFlowDao flowDao;
    @Autowired
    private RobotExecutionMonitorDao executionMonitorDao;
    @Autowired
    private RobotCommandDao commandDao;
    @Autowired
    private RobotExecutionDataDao executionDataDao;
    @Autowired
    private RobotTaskArgsDao robotTaskArgsDao;
    @Autowired
    private RobotAppDao robotAppDao;
    @Autowired
    private RobotFlowStatusHistoryDao statusHistoryDao;
    @Autowired
    private RobotClientDao robotClientDao;
    @Autowired
    private OperationMonitorService operationMonitorService;
    @Autowired
    private RobotWarnMessageService robotWarnMessageService;
    @Autowired
    private RobotExecutionRepository robotExecutionRepository;
    @Autowired
    private RobotExecFailMessageComponent robotExecFailMessageComponent;
    @Autowired
    private RpaSaasService rpaSaasService;
    @Autowired
    private RobotExecutionVoucherDao executionVoucherDao;

    @Override
    public int addExecution(RobotExecutionDTO executionDTO) {
        Date now = new Date();
        //保存机器人执行记录
        RobotExecutionMo mo = robotTaskDao.selectByTaskCode(executionDTO.getTaskCode(), executionDTO.getFlowCode());
        if (mo == null) {
            log.info("taskCode=" + executionDTO.getTaskCode() + " 或flowCode=" + executionDTO.getFlowCode() + " 不存在.");
            return 1;
        }
        RobotExecutionMo executionMo = robotExecutionRepository.selectExecutionOne(executionDTO.getExecutionCode(), executionDTO.getClientId(), executionDTO.getMachineCode(),
                mo.getAppCode(), executionDTO.getTaskCode(), executionDTO.getFlowCode());
        if (executionMo == null) {
            executionMo = new RobotExecutionMo();
            BeanUtils.copyProperties(executionDTO, executionMo);
            executionMo.setClientId(SecurityContext.currentUser().getCustId());
            executionMo.setAppCode(mo.getAppCode());
            executionMo.setAppName(mo.getAppName());
            executionMo.setTaskName(mo.getTaskName());
            executionMo.setFlowName(mo.getFlowName());
            executionMo.setStartTime(DateUtil.formatYMDHMS(executionDTO.getStartTime()));
            if (executionDTO.getEndTime() != null) {
                executionMo.setEndTime(DateUtil.formatYMDHMS(executionDTO.getEndTime()));
            }
            executionMo.setSyncTime(DateUtil.formatYMDHMS(now));
            robotExecutionRepository.save(executionMo);
        } else {
            executionMo.setStatus(executionDTO.getStatus());
            if (executionDTO.getEndTime() != null) {
                executionMo.setEndTime(DateUtil.formatYMDHMS(executionDTO.getEndTime()));
            }
            executionMo.setSyncTime(DateUtil.formatYMDHMS(now));
            robotExecutionRepository.save(executionMo);
        }

        //异常预警
        this.insertRpaErrorData(executionDTO, executionMo);
        if (executionDTO.getStatus() != null && executionDTO.getStatus() == 0) {
            try {
                robotExecFailMessageComponent.sendWarnMessage(executionMo);
            } catch (Exception e) {
                log.error("rpa流程执行失败预警异常：{}", e.getMessage(), e);
            }
        }

        //更新盒子的运行时间
        if (executionDTO.getEndTime() != null) {
            long countTime = executionDTO.getEndTime().getTime() - executionDTO.getStartTime().getTime();
            robotClientDao.updateBoxCountTime(executionDTO.getMachineCode(), countTime);
            //更新应用的运行时间
            robotClientDao.updateAppCountTime(mo.getAppCode(), countTime);
        }

        //保存机器人执行明细
        List<RobotExecutionDetail> detailList = executionDTO.getDetails();
        if (CollectionUtils.isNotEmpty(detailList)) {
            List<RobotExecutionDetailMo> list = detailList.stream().map(detail -> {
                RobotExecutionDetailMo detailMo = new RobotExecutionDetailMo();
                BeanUtils.copyProperties(detail, detailMo);
                detailMo.setStartTime(DateUtil.formatYMDHMS(detail.getStartTime()));
                detailMo.setEndTime(DateUtil.formatYMDHMS(detail.getEndTime()));
                detailMo.setClientId(SecurityContext.currentUser().getCustId());
                detailMo.setAppName(mo.getAppName());
                detailMo.setStartDate(detail.getStartTime().getTime());
                detailMo.setEndDate(detail.getEndTime().getTime());
                detailMo.setSyncTime(DateUtil.formatYMDHMS(now));
                return detailMo;
            }).collect(Collectors.toList());
            robotExecutionRepository.saveDetails(list);
        }
        return 1;
    }

    @Override
    public int addMonitor(List<RobotExecutionMonitor> monitorList) {
        if (CollectionUtils.isEmpty(monitorList)) {
            return 0;
        }
        List<RobotExecutionMonitor> addList = Lists.newArrayList();
        for (RobotExecutionMonitor monitor : monitorList) {
            if (0 == monitor.getSuccessNum() && 0 == monitor.getDeclareNum()) {
                continue;
            }
            Example example = new Example(RobotExecutionMonitor.class);
            example.createCriteria().andEqualTo("executionCode", monitor.getExecutionCode());
            List<RobotExecutionMonitor> list = executionMonitorDao.selectByExample(example);
            if (CollectionUtils.isNotEmpty(list)) {
                continue;
            }
            addList.add(monitor);
        }
        if (CollectionUtils.isNotEmpty(addList)) {
            executionMonitorDao.insertList(addList);
        }
        this.updateFlowStatus(addList);
        return 1;
    }

    private void updateFlowStatus(List<RobotExecutionMonitor> addList) {
        for (RobotExecutionMonitor monitor : addList) {
            if (monitor.getZyNum() == null && monitor.getJyNum() == null && monitor.getTjNum() == null && monitor.getBjNum() == null) {
                continue;
            }
            Example example = new Example(RobotCommand.class);
            example.createCriteria().andEqualTo("uuid", monitor.getExecutionCode());
            RobotCommand robotCommand = commandDao.selectOneByExample(example);
            if (robotCommand == null) {
                log.info("没有手动执行指令。");
                continue;
            }
            RobotAppVO robotAppVO = robotAppDao.selectOneRobotAppVO(robotCommand.getAppCode());
            JSONObject appArgs = JSONObject.parseObject(robotAppVO.getAppArgs());
            String addrName = appArgs.getString("addrName");
            String businessType = appArgs.getString("businessType");
            DevUserAddr devUserAddr = new DevUserAddr();
            devUserAddr.setAddrName(addrName);
            if ("1001001".equals(businessType)) {
                devUserAddr.setBusinessType("社保");
            } else {
                devUserAddr.setBusinessType("公积金");
            }
            List<DevUserAddr> userAddrs = rpaSaasService.listUserAddr(devUserAddr);
            if (CollectionUtils.isEmpty(userAddrs)) {
                continue;
            }
            DevUserAddr userAddr = userAddrs.get(0);
            String roleCode = "";
            if (robotCommand.getCreateName().equals(userAddr.getDevUserName())) {
                roleCode = "dev";
            }
            if (robotCommand.getCreateName().equals(userAddr.getTestUserName())) {
                roleCode = "test";
            }
            if (StringUtils.isBlank(roleCode)) {
                continue;
            }
            example = new Example(RobotFlow.class);
            example.createCriteria().andEqualTo("appCode", robotCommand.getAppCode());
            List<RobotFlow> flowList = flowDao.selectByExample(example);
            for (RobotFlow flow : flowList) {
                RobotFlow update = new RobotFlow();
                update.setId(flow.getId());
                update.setUpdateTime(new Date());
                //服务项目1：增员，2：减员，3：调基，5：补缴，6：缴费，7：名册名单，8：费用明细
                if (flow.getServiceItem() != null && flow.getServiceItem() == 1 && monitor.getZyNum() != null && monitor.getZyNum() != 0) {
                    if ("dev".equals(roleCode)) {
                        update.setStatus(FlowTypeEnum.TEST.getCode());
                    } else {
                        update.setStatus(FlowTypeEnum.CHECK.getCode());
                    }
                }
                if (flow.getServiceItem() != null && flow.getServiceItem() == 2 && monitor.getJyNum() != null && monitor.getJyNum() != 0) {
                    if ("dev".equals(roleCode)) {
                        update.setStatus(FlowTypeEnum.TEST.getCode());
                    } else {
                        update.setStatus(FlowTypeEnum.CHECK.getCode());
                    }
                }
                if (flow.getServiceItem() != null && flow.getServiceItem() == 3 && monitor.getTjNum() != null && monitor.getTjNum() != 0) {
                    if ("dev".equals(roleCode)) {
                        update.setStatus(FlowTypeEnum.TEST.getCode());
                    } else {
                        update.setStatus(FlowTypeEnum.CHECK.getCode());
                    }
                }
                if (flow.getServiceItem() != null && flow.getServiceItem() == 5 && monitor.getBjNum() != null && monitor.getBjNum() != 0) {
                    if ("dev".equals(roleCode)) {
                        update.setStatus(FlowTypeEnum.TEST.getCode());
                    } else {
                        update.setStatus(FlowTypeEnum.CHECK.getCode());
                    }
                }
                flowDao.updateByPrimaryKeySelective(update);

                //保存状态
                if (update.getStatus() != null) {
                    RobotFlowStatusHistory history = new RobotFlowStatusHistory();
                    history.setAppCode(robotCommand.getAppCode());
                    history.setFlowCode(flow.getFlowCode());
                    history.setFlowStatus(update.getStatus());
                    if (update.getStatus().equals(FlowTypeEnum.SURVEY.getCode())) {
                        history.setAppStatus(update.getStatus());
                    } else if (update.getStatus().equals(FlowTypeEnum.REPAIR.getCode())) {
                        history.setAppStatus(update.getStatus());
                    } else if (update.getStatus().equals(FlowTypeEnum.WAIT.getCode())) {
                        history.setAppStatus(update.getStatus());
                    } else if (update.getStatus().equals(FlowTypeEnum.TEST.getCode()) ||
                            update.getStatus().equals(FlowTypeEnum.CHECK.getCode())) {
                        history.setAppStatus(RobotAppTypeEnum.TEST.getCode());
                    }
                    if ("dev".equals(roleCode)) {
                        history.setActionName("运行流程");
                    } else {
                        history.setActionName("测试运行");
                    }
                    history.setType(2);
                    history.setCreateId(robotCommand.getCreateId());
                    history.setCreateName(robotCommand.getCreateName());
                    history.setCreateTime(new Date());
                    statusHistoryDao.insertSelective(history);
                    robotAppDao.updateAppStatus(flow.getAppCode(), history.getAppStatus(), null);
                }
            }
        }
    }

    @Override
    public int saveExecutionData(List<RobotExecutionData> executionDataList) {
        if (CollectionUtils.isEmpty(executionDataList)) {
            return 0;
        }
        executionDataDao.insertList(executionDataList);
        return executionDataList.size();
    }

    private void insertRpaErrorData(RobotExecutionDTO executionDTO, RobotExecutionMo executionMo) {
        if (executionDTO.getStatus() == 0) {
            Example robotTaskExample = new Example(RobotTaskArgs.class);
            String[] stringList = {"socialNumber", "accfundNumber"};
            robotTaskExample.createCriteria().andIn("argsKey", Arrays.asList(stringList)).andEqualTo("taskCode", executionMo.getTaskCode());
            List<RobotTaskArgs> RobotTaskArgsList = robotTaskArgsDao.selectByExample(robotTaskExample);
            Map<String, String> taskMap = RobotTaskArgsList.stream().collect(Collectors.toMap(RobotTaskArgs::getTaskCode, RobotTaskArgs::getArgsValue));
            Example companyExample = new Example(RobotTaskArgs.class);
            companyExample.createCriteria().andEqualTo("argsKey", "companyName").andEqualTo("taskCode", executionMo.getTaskCode());
            List<RobotTaskArgs> companyList = robotTaskArgsDao.selectByExample(companyExample);
            Map<String, String> companyMap = companyList.stream().collect(Collectors.toMap(RobotTaskArgs::getTaskCode, RobotTaskArgs::getArgsValue));
            Example appExample = new Example(RobotApp.class);
            appExample.createCriteria().andEqualTo("appCode", executionMo.getAppCode());
            RobotApp robotApp = robotAppDao.selectOneByExample(appExample);
            OperationRequestVo operationRequestVo = new OperationRequestVo();
            BeanUtils.copyProperties(executionMo, operationRequestVo);
            operationRequestVo.setExecutionId(executionMo.getId());
            operationRequestVo.setErrorResult(executionMo.getError());
            List<OperationRequestVo> list = new ArrayList<>();
            list.add(operationRequestVo);
            list = operationMonitorService.setCustomerName(list);
            list.forEach(e -> {
                e.setAppArgs(robotApp.getAppArgs());
                e.setAccountNumber(companyMap.get(e.getTaskCode()) + "-" + taskMap.get(e.getTaskCode()));
            });
            //RPA异常信息录入运维监控表
            if (executionMo.getFlowName().contains("登录")) {
                operationMonitorService.insertOperationDetail(list, WarnStatus.LOGIN_FAIL.getCode());
                //登录异常信息发送邮件短信通知运维人员
                /*RobotOperationMessageConfig robotOperationMessageConfig = robotWarnMessageService.buildContent(list, WarnStatus.LOGIN_FAIL.getCode());
                robotWarnMessageService.sendMsg(robotOperationMessageConfig);*/
            } else {
                //rpa异常信息发送邮件短信通知运维人员
                operationMonitorService.insertOperationDetail(list, WarnStatus.RPA_EXCEPTION.getCode());
                /*RobotOperationMessageConfig robotOperationMessageConfig = robotWarnMessageService.buildContent(list, WarnStatus.RPA_EXCEPTION.getCode());
                robotWarnMessageService.sendMsg(robotOperationMessageConfig);*/
            }
        }
    }


    @Override
    public int saveExecutionVoucher(RobotExecutionVoucher voucher) {
        List<FileStoreVO> fileStoreList = rpaSaasService.getByFileIds(voucher.getFileId().toString());
        Session session = SecurityContext.currentUser();
        voucher.setClientId(session.getCustId());
        voucher.setCreateTime(new Date());
        if (CollectionUtils.isEmpty(fileStoreList)) {
            FileStoreVO fileStore = fileStoreList.get(0);
            voucher.setFilePath(fileStore.getServerFilePath());
            voucher.setFileUrl(fileStore.getFileUrl());
            voucher.setFileName(fileStore.getClientFileName());
        }
        executionVoucherDao.insertSelective(voucher);
        return 1;
    }
}
