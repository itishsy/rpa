package com.seebon.rpa.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Dict;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.seebon.rpa.BusinessException;
import com.seebon.rpa.auth.SecurityContext;
import com.seebon.rpa.auth.Session;
import com.seebon.rpa.entity.IgGridDefaultPage;
import com.seebon.rpa.entity.agent.enums.BusinessTypeEnum;
import com.seebon.rpa.entity.agent.enums.DeclareTypeEnum;
import com.seebon.rpa.entity.agent.enums.RelationEnum;
import com.seebon.rpa.entity.agent.enums.TplTypeEnum;
import com.seebon.rpa.entity.agent.vo.declare.employee.EmployeeAccfundDeclareChangeeProcessVO;
import com.seebon.rpa.entity.agent.vo.declare.employee.EmployeeSocialDeclareChangeeProcessVO;
import com.seebon.rpa.entity.robot.*;
import com.seebon.rpa.entity.robot.dto.ChangeLoginStatusDTO;
import com.seebon.rpa.entity.robot.dto.EmployeeChangeTrackDTO;
import com.seebon.rpa.entity.robot.dto.RobotTaskScheduleDTO;
import com.seebon.rpa.entity.robot.dto.design.RobotExecutionMo;
import com.seebon.rpa.entity.robot.enums.ColumnTypeEnum;
import com.seebon.rpa.entity.robot.enums.ContrastModexEnum;
import com.seebon.rpa.entity.robot.enums.QueueItemTypeEnum;
import com.seebon.rpa.entity.robot.enums.ServiceItemTypeEnum;
import com.seebon.rpa.entity.robot.vo.*;
import com.seebon.rpa.entity.saas.po.declare.customer.CustomerBase;
import com.seebon.rpa.entity.system.User;
import com.seebon.rpa.remote.RpaSaasAgentService;
import com.seebon.rpa.remote.RpaSaasService;
import com.seebon.rpa.repository.mongodb.RobotExecutionRepository;
import com.seebon.rpa.repository.mysql.*;
import com.seebon.rpa.repository.redis.TaskQueueDao;
import com.seebon.rpa.service.RobotClientRuleService;
import com.seebon.rpa.service.RobotTaskQueueService;
import com.seebon.rpa.utils.ConvertUtl;
import com.seebon.rpa.utils.ELParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RobotTaskQueueServiceImpl implements RobotTaskQueueService {
    @Autowired
    private RobotTaskDao taskDao;
    @Autowired
    private RobotClientDao clientDao;
    @Autowired
    private RobotTaskQueueDao taskQueueDao;
    @Autowired
    private RobotClientRuleDao clientRuleDao;
    @Autowired
    private RobotClientPriorityDao clientPriorityDao;
    @Autowired
    private RobotTaskQueueItemDao taskQueueItemDao;
    @Autowired
    private RobotTaskSessionKeepDao taskSessionKeepDao;
    @Autowired
    private RobotFlowDao flowDao;
    @Autowired
    private RobotFlowStepDao flowStepDao;
    @Autowired
    private TaskQueueDao taskQueueRedisDao;
    @Autowired
    private RobotDataPresetDao dataPresetDao;
    @Autowired
    private RpaSaasService rpaSaasService;
    @Autowired
    private RpaSaasAgentService rpaSaasAgentService;
    @Autowired
    private RobotClientRuleService clientRuleService;
    @Autowired
    private RobotExecutionRepository robotExecutionRepository;
    @Autowired
    private RobotTaskScheduleDao robotTaskScheduleDao;

    @Override
    public IgGridDefaultPage<RobotTaskQueueVO> page(Map<String, Object> map) {
        User session = SecurityContext.currentUser();
        Integer userType = session.getUserType();
        if (userType != null && userType == 2) {// 客户
            map.put("clientId", session.getCustId());
        }
        Integer start = (Integer) map.get("start");
        Integer size = (Integer) map.get("size");
        PageHelper.offsetPage(start, size);
        Page<RobotTaskQueueVO> page = (Page<RobotTaskQueueVO>) taskQueueDao.selectByParams(map);
        if (CollectionUtils.isEmpty(page.getResult())) {
            return new IgGridDefaultPage<RobotTaskQueueVO>(Lists.newArrayList(), 0);
        }
        Map<String, List<RobotTaskQueueItem>> queueItemMap = this.getTaskQueueItemMap(page.getResult());
        Map<String, RobotClient> robotClientMap = this.getRobotClientMap(page.getResult());
        List<CustomerBase> customerList = rpaSaasService.listCustomer(false, "");
        Map<Integer, String> customerMap = customerList.stream().collect(Collectors.toMap(k -> k.getId(), v -> v.getName(), (x, y) -> x));
        page.getResult().stream().forEach(it -> {
            JSONObject appArgs = JSONObject.parseObject(it.getAppArgs());
            it.setAddrName(appArgs.getString("addrName"));
            it.setCustomerName(customerMap.get(it.getClientId()));
            it.setBusinessTypeName(BusinessTypeEnum.getNameByCode(it.getBusinessType()));
            it.setQueueItemName(QueueItemTypeEnum.getNameByCode(it.getQueueItem()));
            RobotAppVO appVO = rpaSaasService.getRobotDeclareType(it.getDeclareSystem());
            if (appVO != null) {
                it.setDeclareSystemName(appVO.getBusinessType());
            }
            if (queueItemMap.containsKey(it.getExecutionCode())) {
                it.setTaskQueueItemList(queueItemMap.get(it.getExecutionCode()));
                it.setServiceItemName(ConvertUtl.getServiceItemNames(it.getTaskQueueItemList()));
            }
            if (robotClientMap.get(it.getMachineCode()) != null) {
                it.setMachineFactory(robotClientMap.get(it.getMachineCode()).getMachineFactory());
            }
        });
        return new IgGridDefaultPage<RobotTaskQueueVO>(page.getResult(), (int) page.getTotal());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer toTop(Integer id) {
        RobotTaskQueue taskQueue = taskQueueDao.selectByPrimaryKey(id);
        //更新排序
        this.updateSort(taskQueue.getClientId(), taskQueue.getId(), 1);
        //更新预估时间
        this.updatePreTime(taskQueue.getClientId(), null);
        return 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer revoke(String ids) {
        if (StringUtils.isBlank(ids)) {
            throw new BusinessException("请选择需要撤销任务.");
        }
        List<String> idList = Lists.newArrayList(ids.split(","));
        for (String id : idList) {
            RobotTaskQueue taskQueue = taskQueueDao.selectByPrimaryKey(id);
            if (taskQueue.getStatus() != 2) {
                continue;
            }
            taskQueueDao.updateStatus(taskQueue.getId(), 3, "手动撤销任务");
            //更新排序
            this.updateSort(taskQueue.getClientId(), taskQueue.getId(), 1);
        }
        //更新预估时间
        this.updatePreTime(null, null);
        return 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void stopTask(String taskCode, String comment) {
        Example example = new Example(RobotTaskQueue.class);
        example.createCriteria().andEqualTo("taskCode", taskCode).andEqualTo("status", 2);
        List<RobotTaskQueue> list = taskQueueDao.selectByExample(example);
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        for (RobotTaskQueue taskQueue : list) {
            taskQueueDao.updateStatus(taskQueue.getId(), 3, "申报账户停用:" + StringUtils.defaultIfBlank(comment, ""));
            //更新排序
            this.updateSort(taskQueue.getClientId(), taskQueue.getId(), 1);
        }
        //更新预估时间
        this.updatePreTime(list.get(0).getClientId(), null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> addTaskQueue(RobotTaskQueueVO reqVO) {
        Map<String, Object> result = Maps.newHashMap();
        if (CollectionUtils.isEmpty(reqVO.getTaskQueueItemList())) {
            result.put("code", 500);
            result.put("msg", "任务项不能为空.");
            return result;
        }
        List<RobotClient> clientList = this.checkClient(reqVO.getClientId());
        if (CollectionUtils.isEmpty(clientList)) {
            result.put("code", 500);
            result.put("msg", "无可用盒子.");
            return result;
        }
        //待执行和执行中的数据
        Map<String, RobotTaskQueueVO> allQueueMap = this.getTaskQueueMap(reqVO.getClientId());
        RobotTaskQueueVO queueVO = allQueueMap.get(reqVO.getClientId() + "_" + reqVO.getTaskCode() + "_" + reqVO.getDeclareSystem() + "_" + reqVO.getQueueItem());
        if (queueVO != null && queueVO.getStatus() == 1) {
            result.put("code", 202);
            result.put("msg", "任务正在执行中,不再生成" + QueueItemTypeEnum.getNameByCode(reqVO.getQueueItem()) + "任务.");
            result.put("queue", queueVO);
            return result;
        }
        //1：增减员（增员、减员、调基、补缴）、12：查审核结果
        if (reqVO.getQueueItem() == 12) {
            boolean hasTaskQueue = this.hasTaskQueue(reqVO, 1, 2);
            if (hasTaskQueue) {
                result.put("code", 202);
                result.put("msg", "已存在待执行的增减员任务,不再生成" + QueueItemTypeEnum.getNameByCode(reqVO.getQueueItem()) + "任务.");
                result.put("queue", queueVO);
                return result;
            }
            if (1 == reqVO.getSource()) {
                String checkIntervalTime = this.checkIntervalTime(reqVO);
                if (StringUtils.isNotBlank(checkIntervalTime)) {
                    result.put("code", 202);
                    result.put("msg", checkIntervalTime + ",不再生成" + QueueItemTypeEnum.getNameByCode(reqVO.getQueueItem()) + "任务.");
                    result.put("queue", queueVO);
                    return result;
                }
            }
        }
        Lock lock = taskQueueRedisDao.lock("inQueue");
        if (!taskQueueRedisDao.tryLock(lock)) {
            result.put("code", 500);
            result.put("msg", "获取队列锁异常，请稍后再试.");
            return result;
        }
        try {
            List<RobotTaskQueueItem> queueItemList = Lists.newArrayList();
            for (RobotTaskQueueItem taskQueueItem : reqVO.getTaskQueueItemList()) {
                if (queueVO != null) {
                    if (ConvertUtl.hasTaskItem(queueVO.getTaskQueueItemList(), taskQueueItem)) {
                        log.info(reqVO.getCompanyName() + " " + reqVO.getDeclareAccount() + " " + reqVO.getAppName() + " " + reqVO.getDeclareSystem() + ",不再生成" + ServiceItemTypeEnum.getNameByCode(taskQueueItem.getServiceItem()) + "任务.");
                        continue;
                    }
                    taskQueueItem.setExecutionCode(queueVO.getExecutionCode());
                    taskQueueItemDao.insertSelective(taskQueueItem);
                } else {
                    if (ConvertUtl.hasTaskItem(queueItemList, taskQueueItem)) {
                        log.info(reqVO.getCompanyName() + " " + reqVO.getDeclareAccount() + " " + reqVO.getAppName() + " " + reqVO.getDeclareSystem() + ",不再生成" + ServiceItemTypeEnum.getNameByCode(taskQueueItem.getServiceItem()) + "任务.");
                        continue;
                    }
                    queueItemList.add(taskQueueItem);
                }
            }
            Integer taskQueueId = null;
            Integer sort = null;
            if (queueVO == null) {
                RobotTaskQueue robotTaskQueue = this.saveRobotTaskQueue(reqVO, queueItemList);
                taskQueueId = robotTaskQueue.getId();
                sort = robotTaskQueue.getSort();
                log.info("更新排序1：sort=" + robotTaskQueue.getSort());
            } else {
                taskQueueId = queueVO.getId();
                sort = queueVO.getSort();
                log.info("更新排序2：sort=" + queueVO.getSort());
            }
            if (reqVO.getRefreshPreTime()) {
                this.refreshSort(reqVO.getClientId(), taskQueueId, sort == null ? 1 : sort);
            }
            RobotTaskQueue queue = taskQueueDao.selectByPrimaryKey(taskQueueId);
            result.put("code", 200);
            result.put("queue", queue);
            return result;
        } finally {
            lock.unlock();
        }
    }

    private void refreshSort(Integer clientId, Integer taskQueueId, Integer sort) {
        CompletableFuture.runAsync(() -> {
            if (!taskQueueRedisDao.tryLock("refreshSort")) {
                log.info("入队列，已有线程在刷新排序和预估时间.");
                return;
            }
            try {
                //更新排序
                this.updateSort(clientId, taskQueueId, sort);
                //更新预估时间
                this.updatePreTime(clientId, sort);
            } finally {
                taskQueueRedisDao.unLock("refreshSort");
            }
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RobotTaskQueueVO findTaskQueue(String machineCode) {
        log.info("【拉取任务】开始拉取任务,machineCode=" + machineCode);
        if (!taskQueueRedisDao.tryLock("outQueue")) {
            throw new BusinessException("拉取任务，获取队列锁异常，请稍后再试.");
        }
        try {
            taskQueueDao.updateStatusByMachineCode(machineCode, "盒子队列数据缺失，系统自动中断", 3);

            //查询所有待执行和执行中数据
            List<RobotTaskQueueVO> list = taskQueueDao.findQueueFirst(SecurityContext.currentUser().getCustId());
            if (CollectionUtils.isEmpty(list)) {
                log.info("【拉取任务】没有待执行和执行中的任务,machineCode=" + machineCode);
                return null;
            }

            //待执行数据
            List<RobotTaskQueueVO> unRunQueueList = list.stream().filter(vo -> vo.getStatus() == 2).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(unRunQueueList)) {
                log.info("【拉取任务】没有待执行的任务,machineCode=" + machineCode);
                return null;
            }

            //执行中数据
            List<RobotTaskQueueVO> runQueueList = list.stream().filter(vo -> vo.getStatus() == 1).collect(Collectors.toList());
            Map<String, List<RobotTaskQueueVO>> runQueueMap = runQueueList.stream().collect(Collectors.groupingBy(k -> k.getClientId() + "_" + k.getTaskCode()));
            Map<String, List<RobotTaskQueueVO>> runPhoneMap = runQueueList.stream().filter(vo -> StringUtils.isNotBlank(vo.getPhoneNumber())).collect(Collectors.groupingBy(k -> k.getPhoneNumber()));

            //出队列
            RobotTaskQueueVO queueVO = this.findQueueFirst(unRunQueueList, runQueueMap, runPhoneMap, machineCode);
            if (queueVO == null) {
                log.info("【拉取任务】未拉取到任务,machineCode=" + machineCode);
                return null;
            }
            log.info("【拉取任务】完成拉取任务,machineCode=" + machineCode + ",任务数据：" + JSON.toJSONString(queueVO));

            queueVO.setTaskQueueItemList(this.getTaskQueueItem(queueVO.getExecutionCode()));

            //更新为执行中
            taskQueueDao.updateById(queueVO.getId(), 1, 0, machineCode);

            return queueVO;
        } finally {
            taskQueueRedisDao.unLock("outQueue");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int callbackTaskQueue(List<RobotTaskQueue> list) {
        if (CollectionUtils.isEmpty(list)) {
            return 1;
        }
        List<RobotTaskQueue> updateList = Lists.newArrayList();
        for (RobotTaskQueue queue : list) {
            RobotTaskQueue update = new RobotTaskQueue();
            update.setClientId(queue.getClientId());
            update.setExecutionCode(queue.getExecutionCode());
            update.setPraTime(DateUtil.between(queue.getPraStartTime(), queue.getPraEndTime(), DateUnit.MINUTE));
            update.setPraStartTime(queue.getPraStartTime());
            update.setPraEndTime(queue.getPraEndTime());
            update.setStatus(queue.getStatus());
            update.setComment(queue.getComment());
            update.setUpdateTime(new Date());
            updateList.add(update);
        }
        taskQueueDao.batchUpdatePraTime(updateList);
        return 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePreTime(Integer clientId, Integer sort) {
        //运行标识  1-执行中  2-待执行 3-执行中断 4-执行成功
        Map<String, Object> params = Maps.newHashMap();
        params.put("status", 2);
        params.put("clientId", clientId);
        params.put("sort", sort);
        List<RobotTaskQueueVO> unRunList = taskQueueDao.findByParams(params);
        if (CollectionUtils.isEmpty(unRunList)) {
            return;
        }
        Map<Integer, List<RobotTaskQueueVO>> runMap = Maps.newHashMap();
        //获取执行中的数据
        params.remove("sort");
        params.put("status", 1);
        params.put("clientId", clientId);
        List<RobotTaskQueueVO> runList = taskQueueDao.findByParams(params);
        if (CollectionUtils.isNotEmpty(runList)) {
            runMap = runList.stream().collect(Collectors.groupingBy(vo -> vo.getClientId()));
        }
        Date nowDate = new Date();
        Map<Integer, List<RobotTaskQueueVO>> unRunMap = unRunList.stream().collect(Collectors.groupingBy(vo -> vo.getClientId()));
        List<RobotTaskQueue> updateList = Lists.newArrayList();
        for (Integer clientIdKey : unRunMap.keySet()) {
            //待执行数据
            List<RobotTaskQueueVO> unRunValues = unRunMap.get(clientIdKey);
            //执行中数据
            List<RobotTaskQueueVO> runValues = runMap.get(clientIdKey);
            //执行中数据-最小预计时长(单位：分钟)
            Long minPreTime = 0L;
            if (CollectionUtils.isNotEmpty(runValues)) {
                minPreTime = runValues.stream().mapToLong(RobotTaskQueueVO::getPreTime).min().orElse(0);
            }
            for (RobotTaskQueue it : unRunList) {
                RobotTaskQueue update = new RobotTaskQueue();
                update.setId(it.getId());
                update.setUpdateTime(new Date());
                Long lastPreTime = ConvertUtl.getLastPreTime(unRunValues, it.getSort());
                Date preStartTime = DateUtil.offset(nowDate, DateField.MINUTE, lastPreTime.intValue() + minPreTime.intValue());
                update.setPreStartTime(preStartTime);
                Date preEndTime = DateUtil.offset(preStartTime, DateField.MINUTE, it.getPreTime().intValue());
                update.setPreEndTime(preEndTime);
                updateList.add(update);
            }
        }
        taskQueueDao.batchUpdatePreTime(updateList);
    }

    @Override
    public RobotTrackVO getTrackStatus(EmployeeChangeTrackDTO trackDTO) {
        if (trackDTO.getDeclareStatus() != null && Lists.newArrayList(4, 5).contains(trackDTO.getDeclareStatus())) {
            return new RobotTrackVO("6", "已完成");
        }
        if (CollectionUtils.isNotEmpty(trackDTO.getOperationTypes()) && trackDTO.getOperationTypes().contains("1015")) {
            return new RobotTrackVO("5", "审核中");
        }
        Dict dict = this.checkAppSchedule(trackDTO.getTaskCode(), trackDTO.getDeclareType());
        if (dict.getBool("deadLineFlag")) {
            return new RobotTrackVO("8", "未到申报期", dict.getStr("declareCycle"));
        }
        //4,1,5,7,3,2,8,6
        Map<String, Object> params = Maps.newHashMap();
        params.put("clientId", trackDTO.getClientId());
        params.put("taskCode", trackDTO.getTaskCode());
        params.put("businessType", trackDTO.getBusinessType());
        params.put("declareAccounts", trackDTO.getDeclareAccount());
        params.put("queueItem", 1);
        params.put("status", 1);
        params.put("sidx", "qu.pra_end_time");
        params.put("sort", "desc");
        List<RobotTaskQueueVO> list = taskQueueDao.selectByParams(params);
        if (CollectionUtils.isNotEmpty(list)) {
            return new RobotTrackVO("4", "执行中");
        }
        params.put("status", 2);
        list = taskQueueDao.selectByParams(params);
        if (CollectionUtils.isNotEmpty(list)) {
            return new RobotTrackVO("1", "排队中");
        }
        Example example = new Example(RobotTask.class);
        example.createCriteria().andEqualTo("clientId", trackDTO.getClientId()).andEqualTo("taskCode", trackDTO.getTaskCode()).andEqualTo("status", 0);
        List<RobotTask> taskList = taskDao.selectByExample(example);
        if (CollectionUtils.isNotEmpty(taskList)) {
            return new RobotTrackVO("3", "暂停", taskList.get(0).getComment());
        }
        params.put("status", 3);
        list = taskQueueDao.selectByParams(params);
        if (CollectionUtils.isNotEmpty(list)) {
            return new RobotTrackVO("7", "执行中断", list.get(0).getComment());
        }
        List<RobotClient> clientList = this.checkClient(trackDTO.getClientId());
        if (CollectionUtils.isEmpty(clientList)) {
            return new RobotTrackVO("2", "离线");
        }
        return null;
    }

    @Override
    public RobotTrackVO getTrackList(EmployeeChangeTrackDTO trackDTO) {
        if (trackDTO.getDeclareStatus() != null && Lists.newArrayList(4, 5).contains(trackDTO.getDeclareStatus())) {
            //6：已完成 数据申报成功或申报失败
            RobotTrackVO trackVO = new RobotTrackVO("6", "已完成");
            this.getProcessDetail(trackDTO, trackVO, "6");
            return trackVO;
        }
        if (CollectionUtils.isNotEmpty(trackDTO.getOperationTypes()) && trackDTO.getOperationTypes().contains("1015")) {
            //5：审核中 根据数据是否打上网站审核节点
            RobotTrackVO trackVO = new RobotTrackVO("5", "审核中", trackDTO.getFailReason());
            this.getProcessDetail(trackDTO, trackVO, "5");
            this.setPreTime(trackDTO, trackVO);
            return trackVO;
        }
        Dict dict = this.checkAppSchedule(trackDTO.getTaskCode(), trackDTO.getDeclareType());
        if (dict.getBool("deadLineFlag")) {
            //8：未到申报期 根据申报期数据，--不存整体预期、执行线路
            return new RobotTrackVO("8", "未到申报期", dict.getStr("declareCycle"));
        }
        //4,1,5,7,3,2,8,6
        Map<String, Object> params = Maps.newHashMap();
        params.put("clientId", trackDTO.getClientId());
        params.put("taskCode", trackDTO.getTaskCode());
        params.put("businessType", trackDTO.getBusinessType());
        params.put("declareAccounts", trackDTO.getDeclareAccount());
        params.put("queueItem", 1);
        params.put("status", 1);
        params.put("sidx", "qu.pra_end_time");
        params.put("sort", "desc");
        List<RobotTaskQueueVO> list = taskQueueDao.selectByParams(params);
        if (CollectionUtils.isNotEmpty(list)) {
            //4：执行中 根据申报户在执行队列中执行中的数据，那么这批数据就是执行中
            RobotTrackVO trackVO = new RobotTrackVO("4", "执行中");
            RobotTaskQueueVO queueVO = list.get(0);
            queueVO.setTaskQueueItemList(this.getTaskQueueItem(queueVO.getExecutionCode()));
            trackVO.setPreTime(queueVO.getPreTime());
            trackVO.setPreStartTime(queueVO.getPreStartTime());
            trackVO.setPreEndTime(queueVO.getPreEndTime());

            Map<String, List<Long>> praTimeMap = this.getPraTimeMap(trackDTO);
            List<RobotTrackDetailVO> detailList = this.addTrackDetail(trackDTO, queueVO);
            Map<String, RobotExecutionMo> executionMap = Maps.newHashMap();
            List<RobotExecutionMo> executionList = robotExecutionRepository.listRobotExecutionByCode(queueVO.getExecutionCode());
            if (CollectionUtils.isNotEmpty(executionList)) {
                for (RobotExecutionMo mo : executionList) {
                    executionMap.put(queueVO.getDeclareSystem() + "-" + mo.getFlowCode(), mo);
                }
            }
            List<RobotFlowVO> flowList = this.getFlows(queueVO);
            Date startTime = trackVO.getPreStartTime();
            for (RobotFlowVO flowVO : flowList) {
                String processName = TplTypeEnum.getNameByCode(queueVO.getDeclareSystem()) + " " + flowVO.getFlowName();
                RobotTrackDetailVO detailVO = new RobotTrackDetailVO(processName, null);
                RobotExecutionMo executionMo = executionMap.get(queueVO.getDeclareSystem() + "-" + flowVO.getFlowCode());
                if (executionMo != null) {
                    detailVO = new RobotTrackDetailVO(processName, executionMo.getEndTime());
                    detailVO.setPraStartTime(DateUtil.parseDateTime(executionMo.getStartTime()));
                    if (StringUtils.isNotBlank(executionMo.getEndTime())) {
                        detailVO.setPraEndTime(DateUtil.parseDateTime(executionMo.getEndTime()));
                        detailVO.setPraTime(DateUtil.between(detailVO.getPraStartTime(), detailVO.getPraEndTime(), DateUnit.MINUTE));
                    }
                }
                List<Long> praTimes = praTimeMap.get(flowVO.getFlowCode());
                if (CollectionUtils.isNotEmpty(praTimes)) {
                    Long preTime = praTimes.stream().collect(Collectors.averagingLong(s -> s)).longValue();
                    detailVO.setPreTime(preTime);
                    detailVO.setPreStartTime(startTime);
                    detailVO.setPreEndTime(DateUtil.offset(startTime, DateField.MINUTE, preTime.intValue()));
                    startTime = detailVO.getPreEndTime();
                }
                detailList.add(detailVO);
            }
            trackVO.setTrackDetailList(detailList);
            return trackVO;
        }
        params.remove("limitPageSize");
        params.put("status", 2);
        list = taskQueueDao.selectByParams(params);
        if (CollectionUtils.isNotEmpty(list)) {
            //1：排队中：根据申报户在执行队列中待执行的数据，那么这批数据就是排队中
            RobotTrackVO trackVO = new RobotTrackVO("1", "排队中");
            RobotTaskQueueVO queueVO = list.get(0);
            queueVO.setTaskQueueItemList(this.getTaskQueueItem(queueVO.getExecutionCode()));
            trackVO.setPreTime(queueVO.getPreTime());
            trackVO.setPreStartTime(queueVO.getPreStartTime());
            trackVO.setPreEndTime(queueVO.getPreEndTime());

            Map<String, List<Long>> praTimeMap = this.getPraTimeMap(trackDTO);
            List<RobotTrackDetailVO> detailList = this.addTrackDetail(trackDTO, queueVO);
            List<RobotFlowVO> flowList = this.getFlows(queueVO);
            Date startTime = trackVO.getPreStartTime();
            for (RobotFlowVO flowVO : flowList) {
                RobotTrackDetailVO detailVO = new RobotTrackDetailVO(TplTypeEnum.getNameByCode(queueVO.getDeclareSystem()) + " " + flowVO.getFlowName(), null);
                List<Long> praTimes = praTimeMap.get(flowVO.getFlowCode());
                if (CollectionUtils.isNotEmpty(praTimes)) {
                    Long preTime = praTimes.stream().collect(Collectors.averagingLong(s -> s)).longValue();
                    detailVO.setPreTime(preTime);
                    detailVO.setPreStartTime(startTime);
                    Date preEndTime = DateUtil.offset(startTime, DateField.MINUTE, preTime.intValue());
                    detailVO.setPreEndTime(preEndTime);
                    startTime = preEndTime;
                }
                detailList.add(detailVO);
            }
            trackVO.setTrackDetailList(detailList);
            return trackVO;
        }
        Example example = new Example(RobotTask.class);
        example.createCriteria().andEqualTo("clientId", trackDTO.getClientId()).andEqualTo("taskCode", trackDTO.getTaskCode()).andEqualTo("status", 0);
        List<RobotTask> taskList = taskDao.selectByExample(example);
        if (CollectionUtils.isNotEmpty(taskList)) {
            //3：暂停 申报账户停用，--不存整体预期、执行线路
            return new RobotTrackVO("3", "暂停", taskList.get(0).getComment());
        }
        params.put("status", 3);
        list = taskQueueDao.selectByParams(params);
        if (CollectionUtils.isNotEmpty(list)) {
            //7：执行中断 根据申报户最新一条记录，如果是执行中断的，那么就用改条数的状态，并显示中断原因
            RobotTrackVO trackVO = new RobotTrackVO("7", "执行中断", list.get(0).getComment());
            this.getProcessDetail(trackDTO, trackVO, "7");
            return trackVO;
        }
        List<RobotClient> clientList = this.checkClient(trackDTO.getClientId());
        if (CollectionUtils.isEmpty(clientList)) {
            //2：离线：盒子离线 ，这里有问题？按申报分配的盒子离线，--不存整体预期、执行线路
            return new RobotTrackVO("2", "离线");
        }
        return null;
    }

    @Override
    public String getMachineCode(RobotTaskQueue taskQueue) {
        Example example = new Example(RobotClientRule.class);
        example.orderBy("sort").asc();
        example.createCriteria().andEqualTo("clientId", taskQueue.getClientId()).andEqualTo("status", 1);
        List<RobotClientRule> ruleList = clientRuleDao.selectByExample(example);
        if (CollectionUtils.isEmpty(ruleList)) {
            return null;
        }
        Map<Integer, List<RobotClientRuleCondition>> ruleConditionMap = clientRuleService.getRuleConditionList(ruleList.stream().map(e -> e.getId()).distinct().collect(Collectors.toList()));
        if (MapUtils.isEmpty(ruleConditionMap)) {
            return null;
        }
        for (RobotClientRule rule : ruleList) {
            List<RobotClientRuleCondition> conditionList = ruleConditionMap.get(rule.getId());
            if (CollectionUtils.isEmpty(conditionList)) {
                continue;
            }
            Map<String, Object> map = Maps.newHashMap();
            map.put("appCode", taskQueue.getAppCode());
            map.put("taskCode", taskQueue.getTaskCode());
            map.put("declareSystem", taskQueue.getDeclareSystem());
            map.put("queueItem", taskQueue.getQueueItem());
            map.put("loginType", taskQueue.getSortRule());
            boolean flag = this.isMatch(map, rule, conditionList);
            if (flag) {
                taskQueue.setRuleId(rule.getId());
                return rule.getMachineCode();
            }
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateLoginStatus(ChangeLoginStatusDTO statusDTO) {
        log.info("更新登录状态,参数:{}", JSON.toJSONString(statusDTO));
        taskQueueDao.updateLoginStatus(statusDTO.getExecutionCode(), statusDTO.getTaskCode(), statusDTO.getLoginStatus());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cleanTaskQueue() {
        RobotDataPreset dataPreset = dataPresetDao.getByType(1001);
        if (dataPreset == null || StringUtils.isBlank(dataPreset.getContext())) {
            return;
        }
        Date offsetHour = DateUtil.offsetHour(new Date(), -Integer.parseInt(dataPreset.getContext()));
        Example example = new Example(RobotTaskQueue.class);
        example.createCriteria().andEqualTo("status", 2).andLessThanOrEqualTo("createTime", DateUtil.formatDateTime(offsetHour));
        List<RobotTaskQueue> taskQueueList = taskQueueDao.selectByExample(example);
        if (CollectionUtils.isEmpty(taskQueueList)) {
            log.info("没有待执行任务,无需清理.");
            return;
        }
        for (RobotTaskQueue taskQueue : taskQueueList) {
            if (taskQueue.getStatus() != 2) {
                continue;
            }
            taskQueueDao.updateStatus(taskQueue.getId(), 3, "超过5小时未执行的任务作废.");
        }
        List<Integer> clientIds = taskQueueList.stream().map(e -> e.getClientId()).distinct().collect(Collectors.toList());
        for (Integer clientId : clientIds) {
            example = new Example(RobotTaskQueue.class);
            example.orderBy("sort").asc();
            example.createCriteria().andEqualTo("clientId", clientId).andEqualTo("status", 2);
            List<RobotTaskQueue> queueList = taskQueueDao.selectByExample(example);
            if (CollectionUtils.isEmpty(queueList)) {
                continue;
            }
            List<RobotTaskQueue> updateList = Lists.newArrayList();
            Integer order = 0;
            for (RobotTaskQueue queue : queueList) {
                RobotTaskQueue update = new RobotTaskQueue();
                update.setId(queue.getId());
                update.setSort((++order));
                update.setUpdateTime(new Date());
                updateList.add(update);
            }
            taskQueueDao.batchUpdateSort(updateList);
        }
        this.updatePreTime(null, null);
    }

    private Dict checkAppSchedule(String taskCode, Integer declareType) {
        Example example = new Example(RobotTask.class);
        example.createCriteria().andEqualTo("taskCode", taskCode);
        RobotTask task = taskDao.selectOneByExample(example);
        List<RobotTaskScheduleDTO> scheduleList = robotTaskScheduleDao.selectSchedules(task.getAppCode(), taskCode, 0); // 拿私有
        List<String> flowCodes = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(scheduleList)) {
            flowCodes = scheduleList.stream().map(item -> item.getFlowCode()).collect(Collectors.toList());
        }
        List<RobotTaskScheduleDTO> schedules = robotTaskScheduleDao.selectCommonSchedules(task.getAppCode(), taskCode, flowCodes, 0); // 拿通用
        scheduleList.addAll(schedules);
        scheduleList = scheduleList.stream().filter(flow -> flow.getExecOrder() != null && flow.getServiceItem() != null && flow.getServiceItem().equals(declareType)).collect(Collectors.toList());
        return ConvertUtl.checkAppSchedule(scheduleList);
    }

    private RobotTaskQueueVO findQueueFirst(List<RobotTaskQueueVO> unRunQueueList, Map<String, List<RobotTaskQueueVO>> runQueueMap, Map<String, List<RobotTaskQueueVO>> runPhoneMap, String machineCode) {
        for (RobotTaskQueueVO queueVO : unRunQueueList) {
            //1、同一申报户不能同时出队列
            if (MapUtils.isNotEmpty(runQueueMap) && runQueueMap.get(queueVO.getClientId() + "_" + queueVO.getTaskCode()) != null) {
                log.info("【拉取任务】同一申报户不能同时出队列,machineCode=" + machineCode);
                continue;
            }
            //2、优先会话维持的机器
            String keepMachineCode = this.getKeepMachineCode(queueVO);
            if (StringUtils.isNotBlank(keepMachineCode)) {
                if (keepMachineCode.equals(machineCode)) {
                    log.info("【拉取任务】优先会话维持的机器,machineCode=" + machineCode + ",keepMachineCode=" + keepMachineCode);
                    return queueVO;
                }
                continue;
            }
            //3、同一手机号不能同时登录
            if (MapUtils.isNotEmpty(runPhoneMap) && queueVO.getSortRule() == 1) {
                List<RobotTaskQueueVO> runPhoneList = runPhoneMap.get(queueVO.getPhoneNumber());
                if (CollectionUtils.isNotEmpty(runPhoneList)) {
                    boolean flag = false;
                    for (RobotTaskQueueVO runPhone : runPhoneList) {
                        if (0 == runPhone.getLoginStatus()) {
                            flag = true;
                            break;
                        }
                    }
                    if (flag) {
                        log.info("【拉取任务】同一手机号不能同时登录,machineCode=" + machineCode + ",phoneNumber=" + queueVO.getPhoneNumber());
                        continue;
                    }
                }
            }
            //4、优先指定盒子的机器
            if (StringUtils.isNotBlank(queueVO.getFixMachineCode()) && Lists.newArrayList(queueVO.getFixMachineCode().split(",")).contains(machineCode)) {
                log.info("【拉取任务】优先指定盒子的机器,machineCode=" + machineCode + ",keepMachineCode=" + queueVO.getFixMachineCode());
                return queueVO;
            }
            if (StringUtils.isBlank(queueVO.getFixMachineCode())) {
                return queueVO;
            }
        }
        return null;
    }

    private RobotClientPriority getPriority(Integer clientId, String appCode) {
        Example example = new Example(RobotClientPriority.class);
        example.createCriteria().andEqualTo("clientId", clientId).andEqualTo("appCode", appCode);
        List<RobotClientPriority> list = clientPriorityDao.selectByExample(example);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    private Integer getSort(RobotTaskQueue taskQueue, Integer clientId) {
        List<RobotTaskQueueVO> list = taskQueueDao.getSortByClientId(clientId);
        if (CollectionUtils.isEmpty(list)) {
            return 1;
        }
        //排序规则：1-H5认证 2-配置优先城市 3-ukey认证 4-账号密码认证
        Map<Integer, List<RobotTaskQueueVO>> groupMap = list.stream().collect(Collectors.groupingBy(vo -> vo.getSortRule()));
        Integer size1 = ConvertUtl.null2Zero(groupMap.get(1));
        Integer size2 = ConvertUtl.null2Zero(groupMap.get(2));
        Integer size3 = ConvertUtl.null2Zero(groupMap.get(3));
        Integer size4 = ConvertUtl.null2Zero(groupMap.get(4));
        if (taskQueue.getSortRule() == 1) {
            return size1 + 1;
        } else if (taskQueue.getSortRule() == 2) {
            return size1 + size2 + 1;
        } else if (taskQueue.getSortRule() == 3) {
            return size1 + size2 + size3 + 1;
        } else if (taskQueue.getSortRule() == 4) {
            return size1 + size2 + size3 + size4 + 1;
        }
        return 1;
    }

    private void updateSort(Integer clientId, Integer newId, Integer newSort) {
        Example example = new Example(RobotTaskQueue.class);
        example.orderBy("sort").asc();
        example.createCriteria().andEqualTo("clientId", clientId).andEqualTo("status", 2);
        List<RobotTaskQueue> queueList = taskQueueDao.selectByExample(example);
        if (CollectionUtils.isEmpty(queueList)) {
            return;
        }
        //重置排序
        Integer order = 0;
        for (RobotTaskQueue queue : queueList) {
            if (queue.getId().intValue() == newId.intValue()) {
                continue;
            }
            queue.setSort((++order));
        }
        //插入排序
        List<RobotTaskQueue> updateList = Lists.newArrayList();
        for (RobotTaskQueue queue : queueList) {
            RobotTaskQueue update = new RobotTaskQueue();
            update.setId(queue.getId());
            update.setUpdateTime(new Date());

            if (queue.getId().intValue() == newId.intValue()) {
                update.setSort(newSort);
            }
            if (queue.getId().intValue() != newId.intValue() && queue.getSort() >= newSort) {
                update.setSort(queue.getSort() + 1);
            }
            updateList.add(update);
        }
        // 批量更新所有需要变更的任务队列
        taskQueueDao.batchUpdateSort(updateList);
    }

    private void updateSort(Integer clientId, Integer queueId) {
        Example example = new Example(RobotTaskQueue.class);
        example.orderBy("sort").asc();
        example.createCriteria().andEqualTo("clientId", clientId).andEqualTo("status", 2).andNotEqualTo("id", queueId);
        List<RobotTaskQueue> queueList = taskQueueDao.selectByExample(example);
        if (CollectionUtils.isEmpty(queueList)) {
            return;
        }
        List<RobotTaskQueue> updateList = Lists.newArrayList();
        Integer order = 0;
        for (RobotTaskQueue queue : queueList) {
            RobotTaskQueue update = new RobotTaskQueue();
            update.setId(queue.getId());
            update.setSort((++order));
            update.setUpdateTime(new Date());
            updateList.add(update);
        }
        taskQueueDao.batchUpdateSort(updateList);
    }

    private RobotTaskQueue saveRobotTaskQueue(RobotTaskQueueVO reqVO, List<RobotTaskQueueItem> queueItemList) {
        //优先执行数据
        RobotClientPriority priority = this.getPriority(reqVO.getClientId(), reqVO.getAppCode());
        //队列信息
        RobotTaskQueue taskQueue = new RobotTaskQueue();
        BeanUtils.copyProperties(reqVO, taskQueue);
        taskQueue.setStatus(2);
        taskQueue.setType(2);
        taskQueue.setLoginStatus(-1);
        taskQueue.setCreateTime(new Date());
        taskQueue.setUpdateTime(new Date());
        taskQueue.setComment(reqVO.getComment());
        Map<String, Object> param = Maps.newHashMap();
        param.put("clientId", reqVO.getClientId());
        param.put("appCode", reqVO.getAppCode());
        param.put("queueItem", reqVO.getQueueItem());
        param.put("declareSystem", reqVO.getDeclareSystem());
        Long preTime = taskQueueDao.getAvgPraTime(param);
        if (preTime == null) {
            preTime = 0L;
        }
        taskQueue.setPreTime(preTime);
        Integer sortRule = ConvertUtl.getSortRule(reqVO.getLoginType());
        if (sortRule != null && sortRule == 1) {
            taskQueue.setSortRule(1);
            taskQueue.setComment("H5认证");
        } else if (priority != null) {
            taskQueue.setSortRule(2);
            taskQueue.setComment("配置优先城市");
        } else if (sortRule != null && sortRule == 3) {
            taskQueue.setSortRule(3);
            taskQueue.setComment("ukey认证");
        } else {
            taskQueue.setSortRule(sortRule);
        }
        //如果是H5登录，先获取维持的机器码
        String machineCode = null;
        if (sortRule == 1) {
            machineCode = this.getKeepMachineCode(reqVO);
        }
        if (StringUtils.isBlank(machineCode)) {
            machineCode = this.getMachineCode(taskQueue);
        }
        if (StringUtils.isNotBlank(machineCode)) {
            taskQueue.setType(1);
            taskQueue.setFixMachineCode(machineCode);
        }
        Integer sort = this.getSort(taskQueue, reqVO.getClientId());
        taskQueue.setSort(sort == null ? 1 : sort);
        taskQueueDao.insertSelective(taskQueue);
        queueItemList.stream().forEach(vo -> vo.setExecutionCode(taskQueue.getExecutionCode()));
        taskQueueItemDao.insertList(queueItemList);
        return taskQueue;
    }

    private boolean hasTaskQueue(RobotTaskQueueVO reqVO, Integer queueItem, Integer status) {
        Example example = new Example(RobotTaskQueue.class);
        example.createCriteria().andEqualTo("clientId", reqVO.getClientId()).andEqualTo("taskCode", reqVO.getTaskCode()).andEqualTo("declareSystem", reqVO.getDeclareSystem()).andEqualTo("queueItem", queueItem).andEqualTo("status", status);
        int count = taskQueueDao.selectCountByExample(example);
        if (count != 0) {
            return true;
        }
        return false;
    }

    private String checkIntervalTime(RobotTaskQueueVO reqVO) {
        if (CollectionUtils.isEmpty(reqVO.getFlowCodeList())) {
            return null;
        }
        RobotDataPreset dataPreset = dataPresetDao.getByType(1003);
        List<Integer> intervalTimes = Lists.newArrayList();
        for (String flowCode : reqVO.getFlowCodeList()) {
            RobotFlowStep flowStep = flowStepDao.getByActionArgs(flowCode, "generateVerify", "declareMonth");
            if (flowStep == null || StringUtils.isBlank(flowStep.getActionArgs())) {
                continue;
            }
            JSONObject jsonObject = JSON.parseObject(flowStep.getActionArgs());
            String websiteVerify = jsonObject.getString("websiteVerify");
            String intervalTime = jsonObject.getString("intervalTime");
            if ("TRUE".equalsIgnoreCase(websiteVerify)) {
                if (StringUtils.isNotBlank(intervalTime)) {
                    intervalTimes.add(Integer.valueOf(intervalTime));
                } else {
                    if (dataPreset != null && StringUtils.isNotBlank(dataPreset.getContext())) {
                        intervalTimes.add(Integer.valueOf(dataPreset.getContext()));
                    }
                }
            }
        }
        if (CollectionUtils.isEmpty(intervalTimes)) {
            return null;
        }
        //获取最小值
        int minIntervalTime = Collections.min(intervalTimes);//默认间隔时间

        Map<String, Object> param = Maps.newHashMap();
        param.put("clientId", reqVO.getClientId());
        param.put("taskCode", reqVO.getTaskCode());
        param.put("declareSystem", reqVO.getDeclareSystem());
        param.put("queueItem", reqVO.getQueueItem());
        param.put("source", 1);
        param.put("limitPageSize", 1);
        param.put("sidx", "qu.id");
        param.put("sort", "desc");
        List<RobotTaskQueueVO> queueList = taskQueueDao.selectByParams(param);
        if (CollectionUtils.isEmpty(queueList)) {
            return null;
        }
        RobotTaskQueueVO queue = queueList.get(0);
        Date afterDate = DateUtil.offsetHour(queue.getCreateTime(), minIntervalTime);
        if (DateUtil.compare(new Date(), afterDate) < 0) {
            return "当前时间：" + DateUtil.formatDateTime(new Date()) + ",上一次任务生成时间：" + DateUtil.formatDateTime(queue.getCreateTime()) + " 间隔时间：" + minIntervalTime + " 小时，任务执行间隔时间未到.";
        }
        return null;
    }

    private Map<String, RobotTaskQueueVO> getTaskQueueMap(Integer clientId) {
        List<RobotTaskQueueVO> list = taskQueueDao.selectQueueByClientId(clientId, Lists.newArrayList(1, 2));
        return list.stream().collect(Collectors.toMap(k -> k.getClientId() + "_" + k.getTaskCode() + "_" + k.getDeclareSystem() + "_" + k.getQueueItem(), v -> v, (x, y) -> x));
    }

    private List<RobotTaskQueueItem> getTaskQueueItem(String executionCode) {
        Example example = new Example(RobotTaskQueueItem.class);
        example.createCriteria().andEqualTo("executionCode", executionCode);
        return taskQueueItemDao.selectByExample(example);
    }

    private Map<String, List<RobotTaskQueueItem>> getTaskQueueItemMap(List<RobotTaskQueueVO> queueList) {
        List<String> executionCodes = queueList.stream().map(vo -> vo.getExecutionCode()).distinct().collect(Collectors.toList());
        Example example = new Example(RobotTaskQueueItem.class);
        example.createCriteria().andIn("executionCode", executionCodes);
        List<RobotTaskQueueItem> list = taskQueueItemDao.selectByExample(example);
        if (CollectionUtils.isEmpty(list)) {
            return Maps.newHashMap();
        }
        return list.stream().collect(Collectors.groupingBy(RobotTaskQueueItem::getExecutionCode));
    }

    private Map<String, RobotClient> getRobotClientMap(List<RobotTaskQueueVO> queueList) {
        List<String> machineCodes = queueList.stream().filter(vo -> StringUtils.isNotBlank(vo.getMachineCode())).map(vo -> vo.getMachineCode()).distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(machineCodes)) {
            return Maps.newHashMap();
        }
        Example example = new Example(RobotClient.class);
        example.createCriteria().andIn("machineCode", machineCodes);
        List<RobotClient> list = clientDao.selectByExample(example);
        if (CollectionUtils.isEmpty(list)) {
            return Maps.newHashMap();
        }
        return list.stream().collect(Collectors.toMap(k -> k.getMachineCode(), v -> v, (x, y) -> x));
    }

    private Boolean isMatch(Map<String, Object> map, RobotClientRule rule, List<RobotClientRuleCondition> conditionList) {
        List<String> contrastStrs = Lists.newArrayList();
        //条件字段：1-应用名称、2-申报账户、3-申报系统、4-事项、5-登录方式
        //条件满足关系 1：等于，2：不等于，3：包含，4：不包含
        for (RobotClientRuleCondition condition : conditionList) {
            String value = ColumnTypeEnum.getValueByCode(condition.getColumnType());
            log.info("条件字段：columnType=" + value);
            String valueKey = "";
            if (condition.getContrastMode() == 3 || condition.getContrastMode() == 4) {
                valueKey = value + "s";
                if (condition.getColumnType() == 4 || condition.getColumnType() == 5) {
                    map.put(valueKey, Lists.newArrayList(condition.getColumnValue().split(",")).stream().map(vo -> Integer.parseInt(vo)).collect(Collectors.toList()));
                } else {
                    map.put(valueKey, Lists.newArrayList(condition.getColumnValue().split(",")));
                }
            } else {
                valueKey = value + "Value";
                map.put(valueKey, condition.getColumnValue());
            }
            String contrastStr = ContrastModexEnum.getContrastStr(value, valueKey, condition.getContrastMode());
            log.info("contrastStr条件：{}", contrastStr);
            contrastStrs.add(contrastStr);
        }
        String scripts = StringUtils.join(contrastStrs, RelationEnum.getSymbolByCode(rule.getRelation()));
        log.info("scripts：{}", scripts);
        try {
            boolean flag = ELParser.parse("${" + scripts + "}", map, Boolean.class);
            log.info("flag：{}", flag);
            return flag;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private String getKeepMachineCode(RobotTaskQueueVO reqVO) {
        Example example = new Example(RobotTaskSessionKeep.class);
        example.createCriteria().andEqualTo("clientId", reqVO.getClientId()).andEqualTo("appCode", reqVO.getAppCode()).andEqualTo("taskCode", reqVO.getTaskCode()).andEqualTo("declareSystem", reqVO.getDeclareSystem()).andEqualTo("status", 3);
        RobotTaskSessionKeep sessionKeep = taskSessionKeepDao.selectOneByExample(example);
        if (sessionKeep == null) {
            return null;
        }
        return sessionKeep.getMachineCode();
    }

    private void getProcessDetail(EmployeeChangeTrackDTO trackDTO, RobotTrackVO trackVO, String trackStatus) {
        //流水类型：0、作废，1、创建  3、导出 4、回盘 5、修改申报状态 6、运维人员已处理
        List<RobotTrackDetailVO> detailList = Lists.newArrayList();
        detailList.add(new RobotTrackDetailVO("数据提交", DateUtil.formatDateTime(trackDTO.getSubmitTime())));
        if (trackDTO.getBusinessType() == 1) {
            List<EmployeeSocialDeclareChangeeProcessVO> processList = rpaSaasAgentService.listSocialProcess(trackDTO.getUuid(), 1);
            if (CollectionUtils.isNotEmpty(processList)) {
                String executionCode = ConvertUtl.getSocialExecutionCode(processList, trackStatus);
                this.addRobotTrackDetail(trackDTO, trackVO, detailList, executionCode);
            }
        } else if (trackDTO.getBusinessType() == 2) {
            List<EmployeeAccfundDeclareChangeeProcessVO> processList = rpaSaasAgentService.listAccfundProcess(trackDTO.getUuid(), 1);
            if (CollectionUtils.isNotEmpty(processList)) {
                String executionCode = ConvertUtl.getAccfundExecutionCodes(processList, trackStatus);
                this.addRobotTrackDetail(trackDTO, trackVO, detailList, executionCode);
            }
        }
        trackVO.setTrackDetailList(detailList);
    }

    private void addRobotTrackDetail(EmployeeChangeTrackDTO trackDTO, RobotTrackVO trackVO, List<RobotTrackDetailVO> detailList, String executionCode) {
        List<RobotTaskQueue> taskQueueList = this.getRobotTaskQueue(trackDTO.getClientId(), trackDTO.getTaskCode(), executionCode);
        if (CollectionUtils.isEmpty(taskQueueList)) {
            return;
        }
        RobotTaskQueue taskQueue = taskQueueList.get(0);
        detailList.add(new RobotTrackDetailVO("分配机器人", DateUtil.formatDateTime(taskQueue.getCreateTime()), taskQueue.getMachineCode()));
        trackVO.setPraTime(taskQueue.getPraTime());
        trackVO.setPraStartTime(taskQueue.getPraStartTime());
        trackVO.setPraEndTime(taskQueue.getPraEndTime());
        trackVO.setPreTime(taskQueue.getPreTime());
        trackVO.setPreStartTime(taskQueue.getPreStartTime());
        trackVO.setPreEndTime(taskQueue.getPreEndTime());

        String declareTypeName = DeclareTypeEnum.getNameByCode(trackDTO.getDeclareType());
        List<RobotExecutionMo> executionList = robotExecutionRepository.listRobotExecutionByCode(taskQueue.getExecutionCode());
        if (CollectionUtils.isNotEmpty(executionList)) {
            for (RobotExecutionMo mo : executionList) {
                if (mo.getFlowName().contains("登录") || mo.getFlowName().contains("登出") ||
                        "登陆".equalsIgnoreCase(mo.getFlowName()) || !mo.getFlowName().contains(declareTypeName)) {
                    continue;
                }
                detailList.add(new RobotTrackDetailVO(TplTypeEnum.getNameByCode(taskQueue.getDeclareSystem()) + " " + mo.getFlowName(), mo.getEndTime()));
            }
        }
    }

    private void setPreTime(EmployeeChangeTrackDTO trackDTO, RobotTrackVO trackVO) {
        if (trackDTO.getBusinessType() == 1) {
            EmployeeSocialDeclareChangeeProcessVO processVO = rpaSaasAgentService.findSocialProcess(trackDTO.getUuid(), trackDTO.getAddrId(), trackDTO.getClientId());
            if (processVO != null) {
                trackVO.setPreTime(processVO.getAvgCheckTime());
                trackVO.setPreStartTime(processVO.getMinCheckTime());
                trackVO.setPreEndTime(DateUtil.offset(processVO.getMinCheckTime(), DateField.MINUTE, processVO.getAvgCheckTime().intValue()));
            }
        } else if (trackDTO.getBusinessType() == 2) {
            EmployeeAccfundDeclareChangeeProcessVO processVO = rpaSaasAgentService.findAccfundProcess(trackDTO.getUuid(), trackDTO.getAddrId(), trackDTO.getClientId());
            if (processVO != null) {
                trackVO.setPreTime(processVO.getAvgCheckTime());
                trackVO.setPreStartTime(processVO.getMinCheckTime());
                trackVO.setPreEndTime(DateUtil.offset(processVO.getMinCheckTime(), DateField.MINUTE, processVO.getAvgCheckTime().intValue()));
            }
        }
    }

    private List<RobotTaskQueue> getRobotTaskQueue(Integer clientId, String taskCode, String executionCode) {
        if (StringUtils.isBlank(executionCode)) {
            return Lists.newArrayList();
        }
        Example example = new Example(RobotTaskQueue.class);
        example.orderBy("id").desc();
        example.createCriteria().andEqualTo("clientId", clientId).andEqualTo("taskCode", taskCode).andEqualTo("executionCode", executionCode).andIsNotNull("machineCode");
        return taskQueueDao.selectByExample(example);
    }

    private List<RobotFlowVO> getFlows(RobotTaskQueueVO queueVO) {
        List<Integer> serviceItemList = queueVO.getTaskQueueItemList().stream().map(vo -> vo.getServiceItem()).distinct().collect(Collectors.toList());
        Map<String, Object> params = Maps.newHashMap();
        params.put("declareSystem", queueVO.getDeclareSystem());
        params.put("serviceItemList", serviceItemList);
        params.put("taskCode", queueVO.getTaskCode());
        return flowDao.findFlows(params);
    }

    private List<RobotTrackDetailVO> addTrackDetail(EmployeeChangeTrackDTO trackDTO, RobotTaskQueueVO queueVO) {
        List<RobotTrackDetailVO> detailList = Lists.newArrayList();
        detailList.add(new RobotTrackDetailVO("数据提交", DateUtil.formatDateTime(trackDTO.getSubmitTime())));
        detailList.add(new RobotTrackDetailVO("分配机器人", DateUtil.formatDateTime(queueVO.getCreateTime()), queueVO.getMachineCode()));
        return detailList;
    }

    private Map<String, List<Long>> getPraTimeMap(EmployeeChangeTrackDTO trackDTO) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("clientId", trackDTO.getClientId());
        params.put("taskCode", trackDTO.getTaskCode());
        params.put("businessType", trackDTO.getBusinessType());
        params.put("declareAccounts", trackDTO.getDeclareAccount());
        params.put("queueItem", 1);
        params.put("limitPageSize", 30);
        params.put("status", 4);
        List<RobotTaskQueueVO> successList = taskQueueDao.selectByParams(params);
        if (CollectionUtils.isEmpty(successList)) {
            return Maps.newHashMap();
        }
        List<String> executionCodes = successList.stream().map(vo -> vo.getExecutionCode()).distinct().collect(Collectors.toList());
        Map<String, List<Long>> praTimeMap = Maps.newHashMap();
        for (String executionCode : executionCodes) {
            List<RobotExecutionMo> executionList = robotExecutionRepository.listRobotExecutionByCode(executionCode);
            if (CollectionUtils.isEmpty(executionList)) {
                continue;
            }
            for (RobotExecutionMo mo : executionList) {
                if (StringUtils.isBlank(mo.getEndTime())) {
                    continue;
                }
                List<Long> praTimes = praTimeMap.get(mo.getFlowCode());
                if (CollectionUtils.isEmpty(praTimes)) {
                    praTimes = Lists.newArrayList();
                }
                long praTime = DateUtil.between(DateUtil.parseDateTime(mo.getStartTime()), DateUtil.parseDateTime(mo.getEndTime()), DateUnit.MINUTE);
                praTimes.add(praTime);
                praTimeMap.put(mo.getFlowCode(), praTimes);
            }
        }
        return praTimeMap;
    }

    private List<RobotClient> checkClient(Integer clientId) {
        Example example = new Example(RobotClient.class);
        example.createCriteria().andEqualTo("clientId", clientId).andIn("status", Lists.newArrayList(1, 2, 6));
        return clientDao.selectByExample(example);
    }
}
