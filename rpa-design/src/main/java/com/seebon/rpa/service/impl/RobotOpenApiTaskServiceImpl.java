package com.seebon.rpa.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.seebon.common.utils.DateUtils;
import com.seebon.rpa.BusinessException;
import com.seebon.rpa.auth.SecurityContext;
import com.seebon.rpa.auth.Session;
import com.seebon.rpa.entity.robot.RobotApp;
import com.seebon.rpa.entity.robot.RobotCommand;
import com.seebon.rpa.entity.robot.dto.ServiceDataStatisticsDTO;
import com.seebon.rpa.entity.robot.dto.design.RobotExecutionDetailMo;
import com.seebon.rpa.entity.robot.dto.design.RobotTaskRunRespVO;
import com.seebon.rpa.entity.robot.dto.design.RobotTaskRunVO;
import com.seebon.rpa.entity.robot.vo.design.RobotTaskNumVO;
import com.seebon.rpa.entity.saas.dto.register.CityRegisterDTO;
import com.seebon.rpa.entity.saas.dto.register.RegisterParamsDTO;
import com.seebon.rpa.entity.saas.dto.register.RegisterStatisticsDTO;
import com.seebon.rpa.entity.saas.dto.register.RegisterStatisticsGroupDTO;
import com.seebon.rpa.entity.saas.po.declare.PolicyAddr;
import com.seebon.rpa.entity.saas.po.declare.customer.CustomerAddrGroup;
import com.seebon.rpa.entity.saas.vo.declare.customer.CustomerMapInfoVO;
import com.seebon.rpa.remote.RpaSaasAgentService;
import com.seebon.rpa.remote.RpaSaasService;
import com.seebon.rpa.repository.mysql.RobotAppDao;
import com.seebon.rpa.repository.mysql.RobotCommandDao;
import com.seebon.rpa.repository.mysql.RobotTaskDao;
import com.seebon.rpa.service.MonitorStationService;
import com.seebon.rpa.service.RobotOpenApiTaskService;
import com.seebon.rpa.utils.FormatUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RobotOpenApiTaskServiceImpl implements RobotOpenApiTaskService {
    @Resource
    private RobotAppDao robotAppDao;
    @Autowired
    private RpaSaasService rpaSaasService;
    @Autowired
    private RobotCommandDao commandDao;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private MonitorStationService monitorStationService;
    @Autowired
    private RpaSaasAgentService rpaSaasAgentService;
    @Autowired
    private RobotTaskDao robotTaskDao;

    @Override
    public Integer taskRun(RobotTaskRunVO runVO) {
        Map<String, Object> params = this.getParams(runVO);
        RobotTaskRunRespVO respVO = robotAppDao.getTaskRun(params);
        if (respVO == null) {
            throw new BusinessException("无可执行流程.");
        }
        Example example = new Example(RobotCommand.class);
        Example.Criteria ca = example.createCriteria();
        ca.andEqualTo("clientId", runVO.getClientId());
        ca.andEqualTo("machineCode", runVO.getMachineCode());
        ca.andEqualTo("command", "runFlow");
        ca.andEqualTo("status", 0);
        List<RobotCommand> commands = commandDao.selectByExample(example);
        if (CollectionUtils.isNotEmpty(commands)) {
            throw new BusinessException("该机器有未执行的指令,等待上一个指令执行完成。");
        }
        Map<String, String> args = Maps.newHashMap();
        args.put("flowCode", respVO.getFlowCode());
        args.put("executionCode", runVO.getExecutionCode());

        RobotCommand command = new RobotCommand();
        command.setClientId(runVO.getClientId());
        command.setMachineCode(respVO.getMachineCode());
        command.setAppCode(respVO.getAppCode());
        command.setTaskCode(respVO.getTaskCode());
        command.setCommand("runFlow");
        command.setArgs(JSON.toJSONString(args));
        command.setCreateTime(new Date());
        command.setUpdateTime(new Date());
        commandDao.insertSelective(command);

        return 1;
    }

    @Override
    public String getRunStatus(RobotTaskRunVO runVO) {
        Map<String, Object> params = this.getParams(runVO);
        RobotTaskRunRespVO flowRespDTO = robotAppDao.getTaskRun(params);
        if (flowRespDTO != null) {
            return flowRespDTO.getRun().toString();
        }
        return "";
    }

    @Override
    public List<RobotExecutionDetailMo> getRunDetail(RobotTaskRunVO runVO) {
        Query query = new Query(Criteria.where("executionCode").is(runVO.getExecutionCode()));
        query.with(Sort.by(Sort.Direction.ASC, new String[]{"startTime"}));
        return mongoTemplate.find(query, RobotExecutionDetailMo.class);
    }

    @Override
    public List<RobotTaskNumVO> getRobot(RobotTaskRunVO runVO) {
        Map dataMap = monitorStationService.getCity();
        List<PolicyAddr> addrList = (List<PolicyAddr>) dataMap.get("addrOnlineList");
        if (CollectionUtils.isEmpty(addrList)) {
            return Lists.newArrayList();
        }
        if (StringUtils.isNotBlank(runVO.getAddrName())) {
            addrList = addrList.stream().filter(ad -> ad.getAddrName().equals(runVO.getAddrName())).collect(Collectors.toList());
        }
        if (CollectionUtils.isEmpty(addrList)) {
            return Lists.newArrayList();
        }
        return addrList.stream().map(addr -> {
            RobotTaskNumVO numVO = new RobotTaskNumVO();
            BeanUtils.copyProperties(addr, numVO);

            Map<String, Object> params = Maps.newHashMap();
            params.put("addrName", addr.getAddrName());
            if ("社保".equals(runVO.getBusinessType())) {
                params.put("appType", "1001001");
            } else if ("公积金".equals(runVO.getBusinessType())) {
                params.put("appType", "1001002");
            }
            List<RobotTaskNumVO> numList = robotAppDao.getRobot(params);
            if (CollectionUtils.isNotEmpty(numList)) {
                for (RobotTaskNumVO vo : numList) {
                    numVO.setNum(this.addNum(numVO.getNum(), vo.getNum()));
                    numVO.setRunNum(this.addNum(numVO.getRunNum(), vo.getRunNum()));
                    numVO.setMachineCode(this.addMachineCode(numVO.getMachineCode(), vo.getMachineCode()));
                    numVO.setRunMachineCode(this.addMachineCode(numVO.getRunMachineCode(), vo.getRunMachineCode()));
                }
            }
            return numVO;
        }).collect(Collectors.toList());
    }

    @Override
    public List<RobotTaskNumVO> getRobotMapData(RobotTaskRunVO runVO) {
        Session session = SecurityContext.currentUser();
        Map<String, Object> map = Maps.newHashMap();
        map.put("clientId", session.getCustId());
        map.put("runStatus", 1);
        List<RobotApp> robotApps = robotAppDao.selectListByParams(map);
        Set<Integer> addrIds = Sets.newHashSet();
        String addrName = runVO.getAddrName();
        robotApps.forEach(e->{
            JSONObject appArgs = JSONObject.parseObject(e.getAppArgs());
            //只统计社保的应用
            if(appArgs!=null){
                Integer addrId = appArgs.getInteger("addrId");
                String addrName1 = appArgs.getString("addrName");
                if (StringUtils.isNotBlank(addrName) && addrName.equals(addrName1)) {
                    addrIds.add(addrId);
                } else {
                    addrIds.add(addrId);
                }
            }
        });
        List<PolicyAddr> cityList = rpaSaasService.getCityList();
        List<PolicyAddr> addrList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(cityList)) {
            addrList = cityList.stream().filter(e->addrIds.contains(e.getAddrId())).collect(Collectors.toList());
        }
        if (CollectionUtils.isEmpty(addrList)) {
            return Lists.newArrayList();
        }
        return addrList.stream().map(addr -> {
            RobotTaskNumVO numVO = new RobotTaskNumVO();
            BeanUtils.copyProperties(addr, numVO);

            Map<String, Object> params = Maps.newHashMap();
            params.put("clientId", session.getCustId());
            params.put("addrName", addr.getAddrName());
            if ("社保".equals(runVO.getBusinessType())) {
                params.put("appType", "1001001");
            } else if ("公积金".equals(runVO.getBusinessType())) {
                params.put("appType", "1001002");
            }
            List<RobotTaskNumVO> numList = robotAppDao.getRobotTwo(params);
            if (CollectionUtils.isNotEmpty(numList)) {
                for (RobotTaskNumVO vo : numList) {
                    numVO.setNum(this.addNum(numVO.getNum(), vo.getNum()));
                    numVO.setRunNum(this.addNum(numVO.getRunNum(), vo.getRunNum()));
                    /*numVO.setMachineCode(this.addMachineCode(numVO.getMachineCode(), vo.getMachineCode()));
                    numVO.setRunMachineCode(this.addMachineCode(numVO.getRunMachineCode(), vo.getRunMachineCode()));*/
                }
            }
            return numVO;
        }).collect(Collectors.toList());
    }

    @Override
    public ServiceDataStatisticsDTO statistics() {

        Session session = SecurityContext.currentUser();

        ServiceDataStatisticsDTO dto = new ServiceDataStatisticsDTO();

        /*开通城市*/
        List<RobotApp> robotApps = robotAppDao.selectAllByTask(session.getCustId());
        Set<Integer> addrIds = robotApps.stream().filter(it -> StringUtils.isNotBlank(it.getAppArgs())).map(it -> {
            String appArgs = it.getAppArgs();
            JSONObject jsonObject = JSONObject.parseObject(appArgs);
            return jsonObject.getInteger("addrId");
        }).collect(Collectors.toSet());
        if (addrIds != null) {
            dto.setCityNumber(new String(addrIds.size() + ""));
        }

        /*社保户*/
        Map<String, Object> params = Maps.newHashMap();
        params.put("businessType", "1001001");
        params.put("clientId", session.getCustId());
        Integer socialAccountNumber = robotTaskDao.selectTaskCount(params);
        if (socialAccountNumber != null) {
            dto.setSocialAccountNumber(socialAccountNumber + "");
        }

        /*公积金户*/
        params.put("businessType", "1001002");
        Integer accfundAccountNumber = robotTaskDao.selectTaskCount(params);
        if (accfundAccountNumber != null) {
            dto.setAccfundAccountNumber(accfundAccountNumber + "");
        }

        /*在职员工*/
        RegisterParamsDTO map1 = new RegisterParamsDTO();
        map1.setCustId(session.getCustId());
        Integer registerNumber =  rpaSaasService.selectRegisterNumber(map1);
        if (registerNumber != null) {
            String registerNumberStr = "";
            if (registerNumber > 10000) {
                BigDecimal bigDecimal = new BigDecimal(registerNumber).divide(new BigDecimal(10000), 2, BigDecimal.ROUND_HALF_UP);
                registerNumberStr = bigDecimal.toString().concat("万");
            } else {
                registerNumberStr = registerNumber + "";
            }
            dto.setRegisterNumber(registerNumberStr);
        }

        /*增减员*/
        Map<String,Object> params1 = Maps.newHashMap();
        params1.put("createMonth", DateUtils.dateToStr(new Date(), "yyyy-MM"));
        params1.put("statusList", Lists.newArrayList(0, 1, 2, 4));
        params1.put("custId", session.getCustId());
        Integer declareNumber = rpaSaasAgentService.selectDeclareCountByParams(params1);
        if (declareNumber != null) {
            dto.setDeclareNumber(declareNumber + "");
        }

        /*总费用*/
        Map<String,Object> map = Maps.newHashMap();
        map.put("periodOfExpense", DateUtils.dateToStr(new Date(), "yyyy-MM"));
        map.put("custId", session.getCustId());
        String totalFee = rpaSaasAgentService.selectTotalFee(map);
        if (StringUtils.isNotBlank(totalFee)) {
            String totalFeeStr = "";
            if (new Double(totalFee) > 10000d) {
                BigDecimal bigDecimal = new BigDecimal(totalFee).divide(new BigDecimal(10000), 2, BigDecimal.ROUND_HALF_UP);
                totalFeeStr = totalFeeStr = bigDecimal.toString().concat("万");
            } else {
                totalFeeStr = new BigDecimal(totalFee).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
            }
            dto.setTotalFee(totalFeeStr);
        }
        return dto;
    }

    @Override
    public List<RegisterStatisticsGroupDTO> registerStatistics() {
        List<RegisterStatisticsGroupDTO> list = Lists.newArrayList();
        List<CustomerAddrGroup> customerAddrGroups = rpaSaasService.addrGroups();
        if (CollectionUtils.isNotEmpty(customerAddrGroups)) {

            LocalDate today = LocalDate.now();
            String nowMonth = today.toString().substring(0, 7);

            List<String> fiveMonth = getFiveMonth();

            List<String> addrNames = Lists.newArrayList();
            customerAddrGroups.stream().filter(it -> it.getType()==1).forEach(it -> {
                String addrName = it.getAddrNames();
                if (StringUtils.isNotBlank(addrName)) {
                    addrNames.addAll(Lists.newArrayList(addrName.split(",")));
                }
            });
            Session session = SecurityContext.currentUser();
            list = customerAddrGroups.stream().map(it -> {
                RegisterStatisticsGroupDTO group = new RegisterStatisticsGroupDTO();
                group.setGroupName(it.getGroupName());
                Integer type = it.getType();
                String addrName = it.getAddrNames();
                if (type == 1) {
                    List<RegisterStatisticsDTO> data = fiveMonth.stream().map(month -> {
                        RegisterStatisticsDTO sd = new RegisterStatisticsDTO();
                        sd.setDataMonth(month);
                        if (StringUtils.isNotBlank(addrName)) {
                            RegisterParamsDTO params = new RegisterParamsDTO();
                            params.setCustId(session.getCustId());
                            if (!nowMonth.equals(month)) {
                                params.setMonth(month);
                            }
                            params.setAddrList(Lists.newArrayList(addrName.split(",")));
                            Integer number = rpaSaasService.selectRegisterNumber(params);
                            sd.setRegisterNumber(number == null? 0 : number);
                        } else {
                            sd.setRegisterNumber(0);
                        }
                        return sd;
                    }).collect(Collectors.toList());
                    group.setDatas(data);
                } else {
                    List<RegisterStatisticsDTO> data = fiveMonth.stream().map(month -> {
                        RegisterStatisticsDTO sd = new RegisterStatisticsDTO();
                        sd.setDataMonth(month);
                        RegisterParamsDTO params = new RegisterParamsDTO();
                        params.setCustId(session.getCustId());
                        if (!nowMonth.equals(month)) {
                            params.setMonth(month);
                        }
                        params.setNotAddrList(addrNames);
                        Integer number = rpaSaasService.selectRegisterNumber(params);
                        sd.setRegisterNumber(number == null? 0 : number);
                        return sd;
                    }).collect(Collectors.toList());
                    group.setDatas(data);
                }
                return group;
            }).collect(Collectors.toList());
        }
        return list;
    }

    @Override
    public List<CityRegisterDTO> cityRegister() {
        Session session = SecurityContext.currentUser();
        List<CityRegisterDTO> list = Lists.newArrayList();
        Set<Integer> addrIds = Sets.newHashSet();
        List<RobotApp> robotApps = robotAppDao.selectAllByTask(session.getCustId());
        robotApps.stream().filter(it -> StringUtils.isNotBlank(it.getAppArgs())).forEach(it -> {
            String appArgs = it.getAppArgs();
            JSONObject jsonObject = JSONObject.parseObject(appArgs);
            Integer addrId = jsonObject.getInteger("addrId");
            String addrName = jsonObject.getString("addrName");
            if (!addrIds.contains(addrId)) {
                CityRegisterDTO dto = new CityRegisterDTO();
                dto.setAddrId(addrId);
                dto.setAddrName(addrName);
                dto.setRegisterNumber(0);
                list.add(dto);
                addrIds.add(addrId);
            }
        });
        List<CityRegisterDTO> selectList = rpaSaasService.selectCityRegister();
        if (CollectionUtils.isEmpty(selectList)) {
            return list;
        } else {
            List<Integer> addrIdList = selectList.stream().map(it -> {
                return it.getAddrId();
            }).collect(Collectors.toList());
            List<CityRegisterDTO> collect = list.stream().filter(it -> !addrIdList.contains(it.getAddrId())).collect(Collectors.toList());
            selectList.addAll(collect);
            return selectList;
        }
    }

    @Override
    public CustomerMapInfoVO mapInfo() {
        CustomerMapInfoVO mapInfoVO = rpaSaasService.getCustomerMapInfo();
        if (mapInfoVO == null) {
            mapInfoVO = new CustomerMapInfoVO();
        }
        return mapInfoVO;
    }

    private List<String> getFiveMonth() {
        List<String> mongths = Lists.newArrayList();
        LocalDate today = LocalDate.now();
        for (int i = 4; i>=0; i--) {
            LocalDate pastMonth = today.minus(i, ChronoUnit.MONTHS);
            mongths.add(pastMonth.toString().substring(0,7));
        }
        return mongths;
    }

    private Integer addNum(Integer num1, Integer num2) {
        if (num1 == null) {
            num1 = 0;
        }
        if (num2 == null) {
            num2 = 0;
        }
        return num1 + num2;
    }

    private String addMachineCode(String code1, String code2) {
        List<String> codes = Lists.newArrayList();
        if (StringUtils.isNotBlank(code1)) {
            codes.addAll(Lists.newArrayList(code1.split(",")));
        }
        if (StringUtils.isNotBlank(code2)) {
            codes.addAll(Lists.newArrayList(code2.split(",")));
        }
        return codes.stream().collect(Collectors.joining(","));
    }

    private Map<String, Object> getParams(RobotTaskRunVO runVO) {
        if (StringUtils.isBlank(runVO.getBusinessType()) || StringUtils.isBlank(runVO.getDeclareType()) || StringUtils.isBlank(runVO.getAddrName())) {
            throw new BusinessException("请求参数异常.");
        }
        log.info("请求参数：" + JSON.toJSONString(runVO));
        Map<String, Object> params = Maps.newHashMap();
        params.put("clientId", runVO.getClientId());
        params.put("accountNumber", runVO.getAccountNumber());
        params.put("machineCode", runVO.getMachineCode());
        if ("社保".equals(runVO.getBusinessType())) {
            params.put("businessType", "1001001");
        } else if ("公积金".equals(runVO.getBusinessType())) {
            params.put("businessType", "1001002");
        }
        params.put("addrName", runVO.getAddrName());
        params.put("declareType", runVO.getDeclareType());
        if (!"广州".equals(runVO.getAddrName())) {
            params.put("declareType", "医保" + runVO.getDeclareType());
        }
        List<PolicyAddr> policyAddrs = rpaSaasService.getCityList();
        Optional<PolicyAddr> policyAddr = policyAddrs.stream().filter(ad -> ad.getAddrName().equals(runVO.getAddrName())).findFirst();
        if (policyAddr.isPresent()) {
            params.put("addrId", policyAddr.get().getAddrId());
        }
        return params;
    }
}
