package com.seebon.rpa.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.seebon.rpa.BusinessException;
import com.seebon.rpa.entity.IgGridDefaultPage;
import com.seebon.rpa.entity.robot.RobotApp;
import com.seebon.rpa.entity.robot.RobotGeneralPlan;
import com.seebon.rpa.entity.robot.dto.design.*;
import com.seebon.rpa.entity.robot.enums.RobotClientStatus;
import com.seebon.rpa.entity.robot.vo.RobotAppVO;
import com.seebon.rpa.entity.robot.vo.RobotClientVO;
import com.seebon.rpa.entity.saas.po.declare.PolicyAddrDeclareSetting;
import com.seebon.rpa.entity.saas.po.declare.customer.CustomerBase;
import com.seebon.rpa.remote.RpaSaasAgentService;
import com.seebon.rpa.remote.RpaSaasService;
import com.seebon.rpa.repository.mysql.RobotAppDao;
import com.seebon.rpa.repository.mysql.RobotExecutionDao;
import com.seebon.rpa.repository.mysql.RobotGeneralPlanDao;
import com.seebon.rpa.service.RobotAppService;
import com.seebon.rpa.service.RobotClientAppService;
import com.seebon.rpa.service.RobotMonitorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author LY
 * @date 2023/4/3 13:39
 */
@Slf4j
@Service
public class RobotMonitorServiceImpl implements RobotMonitorService {

    @Resource
    private RobotAppDao robotAppDao;

    @Resource
    private RobotGeneralPlanDao generalPlanDao;

    @Autowired
    private RpaSaasAgentService rpaSaasAgentService;

    @Autowired
    private RobotAppService robotAppService;

    @Resource
    private RobotExecutionDao robotExecutionDao;

    @Autowired
    private RpaSaasService rpaSaasService;

    @Autowired
    private RobotClientAppService clientAppService;


    @Override
    public IgGridDefaultPage<RobotCityListVo> listCityPage(Map<String, Object> params) {
        Integer page = Integer.parseInt(params.get("page").toString());
        Integer size = Integer.parseInt(params.get("size").toString());
        PageHelper.startPage(page, size);
        if (params.get("appType") != null && StrUtil.isNotBlank(params.get("appType").toString())) {
            String appTypeStr = params.get("appType").toString();
            int appTypeInt = Integer.parseInt(appTypeStr);
            if (appTypeInt == 1) {
                params.put("appType", "1001001");
            } else {
                params.put("appType", "1001002");
            }

        }
        List<PolicyAddrDeclareSetting> policyAddrDeclareSettings=rpaSaasService.selectPolicyDeclareSetList();
        List<DeclareAccountBaseVo> declareAccountBaseVOList=rpaSaasService.principalList();
        Map<String,Integer> declareMap=new HashMap();
        //根据城市id及业务类型获取是否增补合并
        policyAddrDeclareSettings.forEach(e->{
            declareMap.put(e.getAddrId()+"-"+e.getBusinessType(),e.getAddMergeDeclare());
        });
        //根据城市id获取主体数量
        Map<Integer,Integer> accountMap=declareAccountBaseVOList.stream().collect(Collectors.toMap(DeclareAccountBaseVo::getAddressId, DeclareAccountBaseVo::getAccountCount));
        Page<RobotCityListVo> list = (Page<RobotCityListVo>)robotAppDao.listCityPage(params);
        list.forEach(item ->{
            if(item.getExecTotalTime()!=null){
                item.setExecTotalTime(item.getExecTotalTime()/1000/3600);
            }else{
                item.setExecTotalTime(0L);
            }
            if(StrUtil.isNotBlank(item.getLatestDeclarationTime())){
                item.setLatestDeclarationTime(item.getLatestDeclarationTime().split("-")[1]);
            }
            item.setAddrName(JSONObject.parseObject(item.getAppArgs()).getString("addrName"));
            String json=item.getAppArgs();
            JSONObject appArgs=JSONObject.parseObject(json);
            Integer addrId=appArgs.getInteger("addrId");
            String type=appArgs.getString("businessType");
            Integer businessType="1001001".equals(type)?1:2;
            Integer isAddMergeDeclare=declareMap.get(addrId+"-"+businessType);
            //涉及子流程增补合并---需要查申报政策补缴模块是否存在增补合并，若有则设置补缴状态生效
            if(isAddMergeDeclare!=null&&isAddMergeDeclare==1&&item.getConfiguredAdd()>0){
                item.setConfiguredRepair(1);
            }
            Map<String,Integer> declareStatisticMap=rpaSaasAgentService.getDeclareStatistics(addrId,businessType);
            item.setLastCostTime(rpaSaasService.getLastCostDate(addrId));
            item.setExecTotalCount(Long.valueOf(declareStatisticMap.get("personCount").longValue()));
            Integer configuredPrincipalCount= accountMap.get(addrId)==null?0:accountMap.get(addrId);
            item.setConfiguredPrincipalCount(configuredPrincipalCount);
        });
        return new IgGridDefaultPage<RobotCityListVo>(list, (int)list.getTotal());
    }

    @Override
    public CityStatisticsVo cityStatistics() {
        return robotAppDao.cityStatistics();
    }

    @Override
    public Integer takeUpRobotRunStatus(RobotCityEditVo editVo) {
        if (editVo.getAppId() == null) {
            throw new BusinessException("参数错误");
        }
        RobotApp robotApp = robotAppDao.selectByPrimaryKey(editVo.getAppId());
        if (robotApp == null || robotApp.getRunStatus() != 0) {
            throw new BusinessException("该记录不存在或状态已变更，请刷新重试");
        }
        RobotApp updateEntity = new RobotApp();
        updateEntity.setId(editVo.getAppId().intValue());
        updateEntity.setRemark("");
        updateEntity.setRunStatus(1);
        return robotAppDao.updateByPrimaryKeySelective(updateEntity);
    }

    @Override
    public Integer takeDownRobotRunStatus(RobotCityEditVo editVo) {
        if (editVo.getAppId() == null || StrUtil.isBlank(editVo.getRemark())) {
            throw new BusinessException("参数错误");
        }
        RobotApp robotApp = robotAppDao.selectByPrimaryKey(editVo.getAppId());
        if (robotApp == null || robotApp.getRunStatus() != 1) {
            throw new BusinessException("该记录不存在或状态已变更，请刷新重试");
        }
        RobotApp updateEntity = new RobotApp();
        updateEntity.setId(editVo.getAppId().intValue());
        updateEntity.setRunStatus(0);
        updateEntity.setRemark(editVo.getRemark());
        return robotAppDao.updateByPrimaryKeySelective(updateEntity);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Integer editRobotRunStatus(RobotCityEditVo editVo) {
        if (editVo.getAppId() == null) {
            throw new BusinessException("参数错误");
        }
        RobotApp robotApp = robotAppDao.selectByPrimaryKey(editVo.getAppId());
        if (robotApp == null) {
            throw new BusinessException("该记录不存在或状态已变更，请刷新重试");
        }
        RobotApp updateEntity = new RobotApp();
        updateEntity.setId(editVo.getAppId().intValue());
        if (editVo.getOnlineStatus() != null) {
            updateEntity.setOnlineStatus(editVo.getOnlineStatus());
        }
        if (editVo.getWebsiteLinks() != null && StrUtil.isNotBlank(editVo.getWebsiteLinks())) {
            updateEntity.setWebsiteLinks(editVo.getWebsiteLinks());
        }
        Integer lastExecDay = editVo.getLastExecDay();
        if (lastExecDay != null) {
            if (lastExecDay > 31 || lastExecDay < 1) {
                throw new BusinessException("错误的申报时间，正常范围【1~31】！");
            }
            Example example = new Example(RobotGeneralPlan.class);
            example.createCriteria().andEqualTo("appCode", robotApp.getAppCode());
            RobotGeneralPlan plan = generalPlanDao.selectOneByExample(example);
            if (plan == null) {
                throw new BusinessException("通用计划不存在，请先添加");
            }
            RobotGeneralPlan updatePlan = new RobotGeneralPlan();
            updatePlan.setId(plan.getId());
            String execCycle = plan.getExecCycle();
            String newExecCycle = execCycle.split("-")[0] + "-" + lastExecDay;
            updatePlan.setExecCycle(newExecCycle);
            updatePlan.setEditTime(new Date());
            generalPlanDao.updateByPrimaryKeySelective(updatePlan);
        }
//        if (editVo.getVersionId() != null && StrUtil.isNotBlank(editVo.getVersionId())) {
//            robotAppService.activate(robotApp.getAppCode(), editVo.getVersionId());
//        }
        return robotAppDao.updateByPrimaryKeySelective(updateEntity);
    }

    @Override
    public Map<String, Integer> declareStatistics(Long id) {
        RobotApp robotApp = robotAppDao.selectByPrimaryKey(id);
        if (robotApp == null) {
            throw new BusinessException("该应用不存在");
        }
        JSONObject jsonObject = JSONObject.parseObject(robotApp.getAppArgs());
        Integer appType=null;
        if(jsonObject.getString("businessType").equals("1001001")){
            appType=1;
        }else{
            appType=2;
        }
        Map<String, Integer> resp = rpaSaasAgentService.getDeclareStatistics(jsonObject.getInteger("addrId"), appType);
        return resp;
    }

    @Override
    public IgGridDefaultPage<RobotClientVO> boxPage(Map<String, Object> params) {
        Integer page = Integer.parseInt(params.get("page").toString());
        Integer size = Integer.parseInt(params.get("size").toString());

        if (params.get("id") == null) {
            throw new BusinessException("参数错误");
        }
        RobotApp robotApp = robotAppDao.selectByPrimaryKey(params.get("id"));
        if (robotApp == null) {
            throw new BusinessException("该应用不存在");
        }
        params.put("appCode", robotApp.getAppCode());
        if (params.get("status") != null && StrUtil.isNotBlank(params.get("status").toString())) {
            List<Integer> statusList = new ArrayList<>();
            String status = params.get("status").toString();
            if (status.contains(",")){
                String[] split = status.split(",");
                for (String s : split) {
                    statusList.add(Integer.parseInt(s));
                }
            }else {
                statusList.add(Integer.parseInt(status));
            }
            params.put("status",statusList);
        }else {
            params.put("status",null);
        }
        Map<Integer,String> customerMap=getClientName();
        PageHelper.startPage(page, size);
        Page<RobotClientVO> list = (Page<RobotClientVO>)robotAppDao.boxPage(params);
        list.forEach(item -> {
            item.setStatusName(RobotClientStatus.getByIndex(item.getStatus()));
            item.setClientName(customerMap.get(item.getClientId()));
        });
        return new IgGridDefaultPage<RobotClientVO>(list, (int)list.getTotal());
    }

    @Override
    public IgGridDefaultPage<RobotClientAppVo> customerPage(Map<String, Object> params) {
        Integer page = Integer.parseInt(params.get("page").toString());
        Integer size = Integer.parseInt(params.get("size").toString());
        if (params.get("id") == null) {
            throw new BusinessException("参数错误");
        }
        RobotApp robotApp = robotAppDao.selectByPrimaryKey(params.get("id"));
        if (robotApp == null) {
            throw new BusinessException("该应用不存在");
        }
        params.put("appCode", robotApp.getAppCode());
        PageHelper.startPage(page, size);
        Page<RobotClientAppVo> list =(Page<RobotClientAppVo>) robotAppDao.customerPage(params);
        if(list.get(0)==null){
            return new IgGridDefaultPage<RobotClientAppVo>(Lists.newArrayList(), 0);
        }
        Map<Integer,String> customerMap=getClientName();
        Map<Integer,String> platformMap=getPlatform();
        for (RobotClientAppVo appVO : list) {
            Date date = robotExecutionDao.selectLastExecutionTimeByAppCode(appVO.getAppCode());
            appVO.setLastExecutionTime(date);
            appVO.setCustomerName(customerMap.get(appVO.getClientId()));
            String platFormId=platformMap.get(appVO.getClientId());
            RobotAppVO vo=rpaSaasService.getRobotDeclareType(platFormId);
            if(vo!=null){
                appVO.setPlatform(vo.getBusinessType());
            }
        }
        return new IgGridDefaultPage<RobotClientAppVo>(list, (int)list.getTotal());
    }

    @Override
    public IgGridDefaultPage<RobotCityPrincipalListVo> principalPage(Map<String, Object> params) {
        Integer page = Integer.parseInt(params.get("page").toString());
        Integer size = Integer.parseInt(params.get("size").toString());
        if (params.get("id") == null) {
            throw new BusinessException("参数错误");
        }
        RobotApp robotApp = robotAppDao.selectByPrimaryKey(params.get("id"));
        if (robotApp == null) {
            throw new BusinessException("该应用不存在");
        }
        params.put("appCode", robotApp.getAppCode());
        PageHelper.startPage(page, size);
        Page<RobotCityPrincipalListVo> list =(Page<RobotCityPrincipalListVo>) robotAppDao.principalPage(params);
        return new IgGridDefaultPage<>(list, list.size());
    }

    @Override
    public RobotCityListVo detail(Long appId) {
        RobotCityListVo item = robotAppDao.getCityDetailByAppId(appId);
        if (item.getExecTotalTime() != null) {
            item.setExecTotalTime(item.getExecTotalTime() / 3600);
        } else {
            item.setExecTotalTime(0L);
        }
        if (StrUtil.isNotBlank(item.getLatestDeclarationTime())) {
            item.setLatestDeclarationTime(item.getLatestDeclarationTime().split("-")[1]);
        }
        item.setAddrName(JSONObject.parseObject(item.getAppArgs()).getString("addrName"));
        return item;
    }

    @Override
    public Map getClientAndMaCode(Integer id,Integer number){
        Map dateMap=new HashMap();
        List<CitySelectVo> citySelectClientList=new ArrayList<>();
        List<CitySelectVo> citySelectCodeList=new ArrayList<>();
        RobotApp robotApp = robotAppDao.selectByPrimaryKey(id);
        Map<String,Object> map=new HashMap<>();
        map.put("appCode", robotApp.getAppCode());
        List <RobotClientVO> list = robotAppDao.boxPage(map);
        List<Integer> clientIds = list.stream().map(RobotClientVO::getClientId).collect(Collectors.toList());
        if(number==2){
            List<RobotClientAppVo> robotClientAppVOS=robotAppDao.customerPage(map);
            if(robotClientAppVOS.get(0)!=null){
                clientIds = robotClientAppVOS.stream().map(RobotClientAppVo::getClientId).collect(Collectors.toList());
            }else{
                clientIds=new ArrayList<>();
            }
        }
        List<String> machineCodes = list.stream().map(RobotClientVO::getMachineCode).collect(Collectors.toList());
        List<Integer> clientList = clientIds.stream().distinct().collect(Collectors.toList());
        List<String> machineCodeList = machineCodes.stream().distinct().collect(Collectors.toList());
        Map<Integer,String> customerMap=getClientName();
        clientList.forEach(e->{
            CitySelectVo citySelectVo=new CitySelectVo();
            citySelectVo.setClientId(e);
            citySelectVo.setClientName(customerMap.get(e));
            citySelectClientList.add(citySelectVo);
        });
        machineCodeList.forEach(e->{
            CitySelectVo citySelectVo=new CitySelectVo();
            citySelectVo.setMachineCode(e);
            citySelectCodeList.add(citySelectVo);
        });
        dateMap.put("clientList",citySelectClientList);
        dateMap.put("maCodeList",citySelectCodeList);
        return dateMap;
    }

    /**
     * 根据客户id获取客户名字
     * @return
     */
    public Map<Integer,String> getClientName(){
        List<CustomerBase> customers = rpaSaasService.listCustomer(false, "");
        Map<Integer,String> customerMap=customers.stream().collect(Collectors.toMap(CustomerBase::getId,CustomerBase::getName));
        return customerMap;
    }
    /**
     * 根据客户id获取客户平台方
     * @return
     */
    public Map<Integer,String> getPlatform(){
        List<CustomerBase> customers = rpaSaasService.listCustomer(false, "");
        Map<Integer,String> customerMap=customers.stream().collect(Collectors.toMap(CustomerBase::getId,CustomerBase::getPlatform));
        return customerMap;
    }
}
