package com.seebon.rpa.service.impl;

import cn.hutool.core.lang.Dict;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.seebon.common.utils.UUIDGenerator;
import com.seebon.rpa.BusinessException;
import com.seebon.rpa.auth.SecurityContext;
import com.seebon.rpa.auth.Session;
import com.seebon.rpa.entity.IgGridDefaultPage;
import com.seebon.rpa.entity.agent.enums.BusinessTypeEnum;
import com.seebon.rpa.entity.robot.*;
import com.seebon.rpa.entity.robot.enums.LoginAuthLoginStatusEnum;
import com.seebon.rpa.entity.robot.enums.QueueItemTypeEnum;
import com.seebon.rpa.entity.robot.vo.*;
import com.seebon.rpa.entity.saas.po.declare.customer.CustomerBase;
import com.seebon.rpa.entity.saas.po.system.SysDataDict;
import com.seebon.rpa.remote.RpaSaasService;
import com.seebon.rpa.repository.mysql.*;
import com.seebon.rpa.repository.redis.LoginAuthDao;
import com.seebon.rpa.service.RobotTaskQueueService;
import com.seebon.rpa.service.RobotTaskSessionKeepService;
import com.seebon.rpa.utils.ConvertUtl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RobotTaskSessionKeepImpl implements RobotTaskSessionKeepService {
    @Autowired
    private RobotTaskSessionKeepDao taskSessionKeepDao;
    @Autowired
    private RobotTaskArgsDao taskArgsDao;
    @Autowired
    private RobotClientAppDao clientAppDao;
    @Autowired
    private RobotTaskDao robotTaskDao;
    @Autowired
    private RobotClientDao robotClientDao;
    @Autowired
    private RobotTaskQueueService robotTaskQueueService;
    @Autowired
    private RpaSaasService rpaSaasService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private LoginAuthDao loginAuthRedisDao;

    private static final Integer minPort = 8000;
    private static final String ROBOT_LOGIN_AUTH_ = "ROBOT_LOGIN_AUTH_%s";

    @Override
    public IgGridDefaultPage<RobotTaskSessionKeepVO> list(Map<String, Object> map) {
        Integer start = (Integer) map.get("start");
        Integer size = (Integer) map.get("size");
        PageHelper.offsetPage(start, size);
        Page<RobotTaskSessionKeepVO> page = (Page<RobotTaskSessionKeepVO>) taskSessionKeepDao.selectByParams(map);
        if (CollectionUtils.isNotEmpty(page.getResult())) {
            List<CustomerBase> customerList = rpaSaasService.listCustomer(false, "");
            Map<Integer, String> customerMap = customerList.stream().collect(Collectors.toMap(k -> k.getId(), v -> v.getName(), (x, y) -> x));
            page.getResult().stream().forEach(it -> {
                it.setCustomerName(customerMap.get(it.getClientId()));
                RobotAppVO appVO = rpaSaasService.getRobotDeclareType(it.getDeclareSystem());
                if (appVO != null) {
                    it.setDeclareSystemName(appVO.getBusinessType());
                }
                List<RobotTaskArgs> taskArgs = this.getTaskArgs(it.getTaskCode());
                it.setPhoneNumber(ConvertUtl.getPhone(taskArgs));
                it.setLoginType(ConvertUtl.getLoginType(it.getDeclareSystem(), taskArgs));
            });
        }
        return new IgGridDefaultPage<RobotTaskSessionKeepVO>(page.getResult(), (int) page.getTotal());
    }

    @Override
    public Integer save(RobotTaskSessionKeep keep) {
        Integer port = taskSessionKeepDao.selectMinPort(keep.getClientId());
        if (port == null) {
            port = minPort;
        }
        if (keep.getId() == null) {
            keep.setPort(port + 1);
            keep.setSync(0);
            keep.setSyncTime(null);
            keep.setSyncMachineCode(null);
            keep.setMachineCode(null);
            keep.setCreateTime(new Date());
            keep.setUpdateTime(new Date());
            taskSessionKeepDao.insertSelective(keep);
        } else {
            keep.setSync(0);
            keep.setSyncTime(null);
            keep.setSyncMachineCode(null);
            keep.setMachineCode(null);
            keep.setUpdateTime(new Date());
            taskSessionKeepDao.updateByPrimaryKey(keep);
        }
        return 1;
    }

    @Override
    public RobotTaskSessionKeepVO getById(Integer id) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("id", id);
        List<RobotTaskSessionKeepVO> list = taskSessionKeepDao.selectByParams(map);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    @Override
    public void againSync(Integer id) {
        taskSessionKeepDao.againSync(id);
    }

    @Override
    public List<RobotClientApp> listApp(Integer clientId, String runSupport) {
        if (clientId == null) {
            clientId = SecurityContext.currentUser().getCustId();
        }
        return clientAppDao.getByClientId(clientId, runSupport);
    }

    @Override
    public List<SysDataDict> listDeclareSystem(String appCode) {
        List<String> list = clientAppDao.getDeclareSystem(appCode);
        if (CollectionUtils.isEmpty(list)) {
            return Lists.newArrayList();
        }
        List<SysDataDict> result = Lists.newArrayList();
        for (String declareSystem : list) {
            RobotAppVO appVO = rpaSaasService.getRobotDeclareType(declareSystem);
            if (appVO != null) {
                SysDataDict dict = new SysDataDict();
                dict.setDictCode(declareSystem);
                dict.setDictName(appVO.getBusinessType());
                result.add(dict);
            }
        }
        return result;
    }

    @Override
    public List<RobotTaskSessionKeepVO> listTask(RobotTaskSessionKeep sessionKeep) {
        List<RobotTaskSessionKeepVO> list = taskSessionKeepDao.selectTask(sessionKeep);
        if (CollectionUtils.isEmpty(list)) {
            return list;
        }
        list.stream().forEach(it -> {
            it.setAccountAndOrgName(it.getOrgName() + "-" + it.getAccountNumber());
        });
        return list;
    }

    @Override
    public void disabled(RobotTaskSessionKeep sessionKeep) {
        RobotTaskSessionKeep update = new RobotTaskSessionKeep();
        update.setId(sessionKeep.getId());
        update.setDisabled(sessionKeep.getDisabled());
        taskSessionKeepDao.updateByPrimaryKeySelective(update);
        taskSessionKeepDao.againSync(sessionKeep.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean startLogin(Integer id) {
        RobotTaskSessionKeep keep = taskSessionKeepDao.selectByPrimaryKey(id);
        if (keep.getStatus() != null && keep.getStatus() == 3) {
            throw new BusinessException("已登录已维持，不能再次操作");
        }
        // 校验是否有存在可用盒子
        RobotTaskQueue robotTaskQueue = new RobotTaskQueue();
        robotTaskQueue.setClientId(keep.getClientId());
        robotTaskQueue.setAppCode(keep.getAppCode());
        robotTaskQueue.setTaskCode(keep.getTaskCode());
        robotTaskQueue.setDeclareSystem(keep.getDeclareSystem());
        robotTaskQueue.setQueueItem(11);
        robotTaskQueue.setSortRule(1);
        String machineCode = robotTaskQueueService.getMachineCode(robotTaskQueue);
        List<String> machineCodeList = Lists.newArrayList();
        if (StringUtils.isNotEmpty(machineCode)) {
            machineCodeList = Arrays.asList(machineCode.split(","));
        }
        int i = robotClientDao.countFreeRobotClient(keep.getClientId(), machineCodeList);
        if (i == 0) {
            throw new BusinessException("暂时无空闲设备执行此任务，请30分钟再试");
        }
        // 获取申报账户配置信息
        Map<String, Object> params = Maps.newHashMap();
        params.put("taskCode", keep.getTaskCode());
        List<RobotTaskVO> taskList = robotTaskDao.selectAllTask(params);
        if (CollectionUtils.isEmpty(taskList)) {
            throw new BusinessException("未找到申报账户配置信息");
        }
        String phoneNumber = ConvertUtl.getPhone(taskList.get(0).getTaskArgsList());
        // 判断是否已经点击进入队列 : 同一个经办人或者同一个通知手机号码时，申报账户A登录成功后，才可以登录下一个申报账号，再点登录时进行提示
        String redisKey = String.format(ROBOT_LOGIN_AUTH_, phoneNumber);
        Boolean b = redisTemplate.opsForValue().setIfAbsent(redisKey, id.toString(), 60 * 10, TimeUnit.SECONDS);
        if (b == null) {
            throw new BusinessException("操作失败，队列加锁失败");
        } else if (!b) {
            String s = redisTemplate.opsForValue().get(redisKey);
            throw new BusinessException("当前存在申报账户正在登录，请登录完成之后再操作", keep == null ? "" : keep.toString());
        }
        RobotTaskVO robotTaskVO = taskList.get(0);
        try {
            toAddQueue(robotTaskVO, keep);
            // 设置登录信息
            loginAuthRedisDao.setLoginProcessInfo(keep.getDeclareSystem(), robotTaskVO.getAccountNumber(), robotTaskVO.getOrgName(), LoginAuthLoginStatusEnum.NO_LOGIN.getStatus(), "未登录");
        } catch (Exception e) {
            // 已登录，调用入队列操作
            redisTemplate.delete(String.format(ROBOT_LOGIN_AUTH_, phoneNumber));
            throw e;
        }
        // 加队列
        return Boolean.TRUE;
    }

    private RobotTaskQueue toAddQueue(RobotTaskVO taskVO, RobotTaskSessionKeep keep) {
        Session session = SecurityContext.currentUser();
        // 获取手机号
        String mobile = ConvertUtl.getPhone(taskVO.getTaskArgsList());
        String loginType = ConvertUtl.getLoginType(keep.getDeclareSystem(),taskVO.getTaskArgsList());
        // 业务类型
        JSONObject appArgs = JSONObject.parseObject(taskVO.getAppArgs());
        Integer businessType = BusinessTypeEnum.getCodeByKey(appArgs.getString("businessType"));

        // 设置队列项
        RobotTaskQueueItem taskQueueItem = new RobotTaskQueueItem();
        taskQueueItem.setServiceItem(11);
        taskQueueItem.setCreateTime(new Date());
        taskQueueItem.setUpdateTime(new Date());
        List<RobotTaskQueueItem> itemList = Lists.newArrayList(taskQueueItem);

        // 调用入队列流程
        RobotTaskQueueVO taskQueueVO = new RobotTaskQueueVO();
        taskQueueVO.setClientId(keep.getClientId());
        taskQueueVO.setExecutionCode(UUIDGenerator.uuidStringWithoutLine());
        taskQueueVO.setAppCode(keep.getAppCode());
        taskQueueVO.setCompanyName(taskVO.getOrgName());
        taskQueueVO.setTaskCode(keep.getTaskCode());
        taskQueueVO.setDeclareSystem(keep.getDeclareSystem());
        taskQueueVO.setDeclareAccount(taskVO.getAccountNumber());
        taskQueueVO.setQueueItem(11);
        taskQueueVO.setSource(2);
        taskQueueVO.setLoginType(loginType);
        taskQueueVO.setPhoneNumber(mobile);
        taskQueueVO.setBusinessType(businessType);
        taskQueueVO.setTaskQueueItemList(itemList);
        taskQueueVO.setRefreshPreTime(Boolean.TRUE);
        taskQueueVO.setCreateId((int) session.getId());
        taskQueueVO.setCreateName(session.getName());
        try {
            Map<String, Object> addTaskQueueResultMap = robotTaskQueueService.addTaskQueue(taskQueueVO);
            if (!"500".equals(addTaskQueueResultMap.get("code").toString())) {
                return (RobotTaskQueue) addTaskQueueResultMap.get("queue");
            } else {
                throw new BusinessException(addTaskQueueResultMap.get("msg").toString());
            }
        } catch (Exception e) {
            throw e;
        }
    }

    private List<RobotTaskArgs> getTaskArgs(String taskCode) {
        Example example = new Example(RobotTaskArgs.class);
        example.createCriteria().andEqualTo("taskCode", taskCode);
        return taskArgsDao.selectByExample(example);
    }
}
