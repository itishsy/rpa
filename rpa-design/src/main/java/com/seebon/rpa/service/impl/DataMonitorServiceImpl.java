package com.seebon.rpa.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.seebon.common.utils.DateUtils;
import com.seebon.common.utils.JsonUtils;
import com.seebon.rpa.entity.agent.dto.DeclareValidateQueryDTO;
import com.seebon.rpa.entity.agent.enums.BusinessTypeEnum;
import com.seebon.rpa.entity.robot.dto.RobotTaskScheduleDTO;
import com.seebon.rpa.entity.robot.dto.design.RobotExecutionMo;
import com.seebon.rpa.entity.robot.dto.monitor.NoDeclareDTO;
import com.seebon.rpa.entity.robot.dto.monitor.TodayExecDTO;
import com.seebon.rpa.entity.robot.dto.monitor.TodayExecDataDTO;
import com.seebon.rpa.entity.robot.dto.monitor.TodayExecQueryDTO;
import com.seebon.rpa.entity.saas.DevUserAddr;
import com.seebon.rpa.entity.saas.po.declare.PolicyAddr;
import com.seebon.rpa.entity.saas.po.declare.customer.CustomerBase;
import com.seebon.rpa.entity.saas.vo.DevAddrMsgVO;
import com.seebon.rpa.entity.saas.vo.PolicyAddrDeadlineSettingVO;
import com.seebon.rpa.remote.RpaSaasAgentService;
import com.seebon.rpa.remote.RpaSaasService;
import com.seebon.rpa.repository.mongodb.RobotExecutionRepository;
import com.seebon.rpa.repository.mysql.RobotTaskDao;
import com.seebon.rpa.repository.mysql.RobotTaskScheduleDao;
import com.seebon.rpa.service.DataMonitorService;
import com.seebon.rpa.utils.ScheduleUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DataMonitorServiceImpl implements DataMonitorService {

    @Autowired
    private RobotTaskDao robotTaskDao;

    @Autowired
    private RobotTaskScheduleDao robotTaskScheduleDao;

    @Autowired
    private RpaSaasService rpaSaasService;

    @Autowired
    private RpaSaasAgentService rpaSaasAgentService;

    @Autowired
    private RobotExecutionRepository robotExecutionRepository;

    private static final Integer staticlqDay = 5;

    @Override
    public TodayExecDTO getTodayExecList(TodayExecQueryDTO dto) {
        TodayExecDTO result = new TodayExecDTO();

        /*DevAddrMsgVO msgVO = new DevAddrMsgVO();
        msgVO.setDevUserName(dto.getDevUserName());
        msgVO.setTestUserName(dto.getTestUserName());
        msgVO.setYwUserName(dto.getYwUserName());*/
        List<DevAddrMsgVO> devAddrMsgVOS = rpaSaasAgentService.listAddrMsg(dto.getDevUserName()==null?"":dto.getDevUserName(),
                dto.getTestUserName()==null?"":dto.getTestUserName(),dto.getYwUserName()==null?"":dto.getYwUserName());
        DevUserAddr devUserAddr = new DevUserAddr();
        List<DevUserAddr> userAddrs = rpaSaasService.listUserAddr(devUserAddr);

        if (StringUtils.isNotBlank(dto.getDevUserName())
                || StringUtils.isNotBlank(dto.getTestUserName())
                || StringUtils.isNotBlank(dto.getYwUserName())) {
            if (CollectionUtils.isEmpty(devAddrMsgVOS)) {
                result.setList(Lists.newArrayList());
                result.setNoDeclaredCount(0);
                return result;
            } else {
                List<PolicyAddr> cityList = rpaSaasService.getCityList();
                Map<String, List<PolicyAddr>> cityMap
                        = cityList.stream().collect(Collectors.groupingBy(it -> it.getAddrName()));
                Set<String> appArgs = devAddrMsgVOS.stream().map(it -> {
                    String addrName = it.getAddrName();
                    List<PolicyAddr> policyAddrs = cityMap.get(addrName);
                    Integer addrId = null;
                    if (CollectionUtils.isNotEmpty(policyAddrs)) {
                        addrId = policyAddrs.get(0).getAddrId();
                    }
                    String businessTypeStr = it.getBusinessType();
                    String businessType = "1001001";
                    if ("公积金".equals(businessTypeStr)) {
                        businessType = "1001002";
                    }
                    return "{\"addrName\":\"" +addrName+"\",\"addrId\":\""+addrId+"\",\"businessType\":\""+businessType+"\"}";
                }).collect(Collectors.toSet());
                dto.setAppArgs(Lists.newArrayList(appArgs));
            }
        }

        List<TodayExecDataDTO> list = robotTaskDao.getRobotTaskExecList(dto);
        List<Integer> noDeclaredCountList = Lists.newCopyOnWriteArrayList();

        if (CollectionUtils.isNotEmpty(list)) {

            List<CustomerBase> customerBases = rpaSaasService.listCustomer(false, "");
            Map<Integer, List<CustomerBase>> custMap = customerBases.stream().collect(Collectors.groupingBy(it -> it.getId()));

            Map<String, List<PolicyAddrDeadlineSettingVO>> deadlineMap = Maps.newConcurrentMap();

            Integer lqDay = rpaSaasService.getLqDay();
            if(lqDay == null) {
                lqDay = staticlqDay;
            }

            final Integer finalLqDay = lqDay;

            list.parallelStream().forEach(it -> {
                getNoDeclareInfo(it, noDeclaredCountList);
                getScheduleInfo(it, finalLqDay);
                getDeclareInfo(it, custMap, deadlineMap);
                Integer clientId = it.getClientId();

                Optional<DevUserAddr> first = userAddrs.stream().filter(msg -> StringUtils.isNotBlank(it.getBusinessTypeName())
                        && StringUtils.isNotBlank(msg.getAddrName()) && StringUtils.isNotBlank(it.getAddrName()) &&
                        StringUtils.isNotBlank(msg.getBusinessType()) &&
                        msg.getAddrName().equals(it.getAddrName()) && msg.getBusinessType().equals(it.getBusinessTypeName())
                ).findFirst();
                if (first.isPresent()) {
                    DevUserAddr userAddr = first.get();
                    it.setDevUserName(userAddr.getDevUserName());
                    it.setTestUserName(userAddr.getTestUserName());
                    it.setYwUserName(userAddr.getYwUserName());
                    it.setXqUserName(userAddr.getXqUserName());
                }
                Optional<DevAddrMsgVO> first1 = devAddrMsgVOS.stream().filter(msg -> StringUtils.isNotBlank(it.getBusinessTypeName())
                        && StringUtils.isNotBlank(msg.getAddrName()) && StringUtils.isNotBlank(it.getAddrName()) &&
                        StringUtils.isNotBlank(msg.getBusinessType()) &&
                        msg.getAddrName().equals(it.getAddrName()) && msg.getBusinessType().equals(it.getBusinessTypeName())
                        && clientId!=null && msg.getCustomerId()!=null && clientId.equals(msg.getCustomerId())
                ).findFirst();
                if (first1.isPresent()) {
                    DevAddrMsgVO msgVO1 = first1.get();
                    it.setRemark(msgVO1.getRemark());
                }
            });


            List<TodayExecDataDTO> resultList = Lists.newArrayList();

            List<TodayExecDataDTO> lqList = list.stream().filter(it -> it.getFlag() == 1).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(lqList)) {
                lqList.sort(Comparator.comparing(TodayExecDataDTO::getAdventDays)
                        .thenComparing(TodayExecDataDTO::getNoDeclaredCount, Comparator.reverseOrder()));
                resultList.addAll(lqList);
            }

            List<TodayExecDataDTO> flqList = list.stream().filter(it -> it.getFlag() == 0 && it.getInnerPlant()==1).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(flqList)) {
                flqList.sort(Comparator.comparing(TodayExecDataDTO::getNoDeclaredCount, Comparator.reverseOrder()).thenComparing(TodayExecDataDTO::getAdventDays));
                resultList.addAll(flqList);
            }

            List<TodayExecDataDTO> flqList1 = list.stream().filter(it -> it.getFlag() == 0 && it.getInnerPlant()==0).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(flqList1)) {
                flqList1.sort(Comparator.comparing(TodayExecDataDTO::getNoDeclaredCount, Comparator.reverseOrder()).thenComparing(TodayExecDataDTO::getAdventDays));
                resultList.addAll(flqList1);
            }
            resultList.stream().filter(it -> it.getFlag() == 0 && it.getAdventDays()!=null && it.getAdventDays() == 31).forEach(it -> {
                it.setAdventDays(null);
            });
            resultList.stream().filter(it -> (it.getNoDeclaredCount()==null || it.getNoDeclaredCount()==0) || (
                    it.getNoDeclaredCount()!=null && it.getNoDeclaredCount()>0 && it.getAdventDays()!=null && it.getAdventDays()>finalLqDay && it.getInnerPlant()==1
            )).forEach(it -> {
                it.setFlag(null);
            });
            result.setList(resultList);

            int sum = resultList.stream().filter(it -> it.getFlag() != null && it.getFlag() == 1 && it.getNoDeclaredCount() != null).mapToInt(TodayExecDataDTO::getNoDeclaredCount).sum();
            result.setNoDeclaredCount(sum);
        } else {
            result.setList(list);
            result.setNoDeclaredCount(0);
        }
        return result;
    }

    private String getMonthStr(Integer month) {
        if (month == -1) {
            return "上月";
        } else if (month == 0) {
            return "当月";
        } else if (month == 1) {
            return "下月";
        }
        return "";
    }

    /**
     * 获取流程执行计划信息
     * @param it
     */
    private void getScheduleInfo (TodayExecDataDTO it, Integer finalLqDay) {
        String appCode = it.getAppCode();
        String taskCode = it.getTaskCode();

        if (StringUtils.isNotBlank(appCode) && StringUtils.isNotBlank(taskCode)) {
            List<RobotTaskScheduleDTO> scheduleDTOS = robotTaskScheduleDao.selectSchedules(appCode, taskCode, 0); // 拿私有
            List<String> prvtFlowCodes = Lists.newArrayList();
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(scheduleDTOS)) {
                prvtFlowCodes = scheduleDTOS.stream().map(item -> item.getFlowCode()).collect(Collectors.toList());
            }
            //那一次公共的除了私有外的
            List<RobotTaskScheduleDTO> scheduleDTOS1 = robotTaskScheduleDao.selectCommonSchedules(appCode, taskCode, prvtFlowCodes, 0); // 拿通用
            scheduleDTOS.addAll(scheduleDTOS1);
            scheduleDTOS = scheduleDTOS.stream().filter(flow -> flow.getExecOrder()!=null && flow.getServiceItem()!=null
                    && Lists.newArrayList(1,2,3,5).contains(flow.getServiceItem())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(scheduleDTOS)) {
                try {
                    scheduleDTOS.sort((o1, o2) -> o1.getExecOrder()-o2.getExecOrder());
                }catch (Exception e) {
                    log.error("e========>{}", e.getMessage(),e);
                }

                for (int i=0; i<scheduleDTOS.size(); i++) {
                    RobotTaskScheduleDTO robotTaskScheduleDTO = scheduleDTOS.get(i);
                    robotTaskScheduleDTO.setExecOrder(i+1);
                    StringBuffer stringBuffer = new StringBuffer();
                    String execCycle = robotTaskScheduleDTO.getExecCycle();
                    String[] split = execCycle.split("-");
                    stringBuffer.append("本月").append(split[0]).append("号").append("-").append(split[1]).append("号")
                            .append("：").append(StringUtils.isBlank(robotTaskScheduleDTO.getExecAreaTime()) ? "实时" : robotTaskScheduleDTO.getExecAreaTime());
                    robotTaskScheduleDTO.setExecPlant(stringBuffer.toString());
                }
                it.setSchedules(scheduleDTOS);

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
                    it.setExecPlant("当月".concat(days.get(0)).concat("号-").concat(days.get(days.size()-1)).concat("号：").
                            concat(hours.get(0)).concat(":00-").concat(hours.get(hours.size()-1)).concat(":00"));
                }
                List<Integer> lqDays = Lists.newCopyOnWriteArrayList();
                List<Integer> lqDays1 = Lists.newCopyOnWriteArrayList();
                boolean innerPlant = false;
                List<NoDeclareDTO> noDeclareList = it.getNoDeclareList();
                if (CollectionUtils.isNotEmpty(noDeclareList)) {
                    for (RobotTaskScheduleDTO item : scheduleDTOS) {

                        String execCycle = item.getExecCycle();
                        if (StringUtils.isNotBlank(execCycle)) {
                            String[] split = execCycle.split("-");
                            Integer endDay = Integer.valueOf(split[1]);
                            int monthDays = DateUtils.getMonthDaysByDate(new Date());
                            if (endDay > monthDays) {
                                endDay = monthDays;
                            }
                            int byDate = DateUtils.getByDate(new Date(), Calendar.DATE);
                            Integer serviceItem = item.getServiceItem();
                            Optional<NoDeclareDTO> first = noDeclareList.stream().filter(noDec -> noDec.getChangeType().equals(serviceItem) && noDec.getEmpCount() != null && noDec.getEmpCount() > 0).findFirst();
                            if (first.isPresent()) {
                                lqDays.add(endDay - byDate);
                            }
                            lqDays1.add(endDay - byDate);
                            if (!innerPlant) {
                                innerPlant = ScheduleUtils.matchSchedule(execCycle, item.getExecAreaTime(), item.getExecStyle(), new Date());
                            }
                        }
                    }
                    Integer adventDays = 0;
                    List<Integer> byFilter = Lists.newArrayList();
                    if (CollectionUtils.isNotEmpty(lqDays)) {
                        lqDays.sort((o1,o2) -> o1-o2);
                        byFilter = lqDays.stream().filter(day -> day >= 0 && day <= finalLqDay).collect(Collectors.toList());
                        adventDays = lqDays.get(0);
                    }else {
                        lqDays1.sort((o1,o2) -> o1-o2);
                        byFilter = lqDays1.stream().filter(day -> day >= 0 && day <= finalLqDay).collect(Collectors.toList());
                        adventDays = lqDays1.get(0);
                    }
                    if (CollectionUtils.isNotEmpty(byFilter)) {
                        adventDays = byFilter.get(0);
                    }
                    it.setAdventDays(adventDays);
                    it.setInnerPlant(innerPlant?1:0);
                    if (adventDays <= finalLqDay  && adventDays >=0) {
                        it.setFlag(1);
                    } else {
                        it.setFlag(0);
                    }
                } else {
                    it.setSchedules(Lists.newArrayList());
                    it.setFlag(0);
                    it.setAdventDays(31);
                    it.setInnerPlant(0);
                }
            } else {
                it.setSchedules(Lists.newArrayList());
                it.setFlag(0);
                it.setAdventDays(31);
                it.setInnerPlant(0);
            }
        } else {
            it.setSchedules(Lists.newArrayList());
            it.setFlag(0);
            it.setAdventDays(31);
            it.setInnerPlant(0);
        }
    }

    private void getNoDeclareInfo (TodayExecDataDTO it, List<Integer> noDeclaredCountList) {
        String appArgs = it.getAppArgs();
        if (StringUtils.isNotBlank(appArgs) && it.getClientId()!=null && StringUtils.isNotBlank(it.getAccountNumber())) {
            JSONObject jsonObject = JSON.parseObject(appArgs);
            String addrName = jsonObject.getString("addrName");
            Integer addrId = jsonObject.getInteger("addrId");
            String businessTypeStr = jsonObject.getString("businessType");
            it.setAddrId(addrId);
            it.setAddrName(addrName);
            Integer businessType = businessTypeStr.equals("1001001") ? 1 : 2;

            //获取未申报数据
            List<NoDeclareDTO> maps = rpaSaasAgentService.noDeclareCount(businessType, it.getClientId(), it.getAddrId(), it.getAccountNumber());
            if (CollectionUtils.isNotEmpty(maps)) {
                List<RobotTaskScheduleDTO> schedules = it.getSchedules();
                if (CollectionUtils.isNotEmpty(schedules)) {
                    maps.stream().forEach(nd ->{
                        Integer changeType = nd.getChangeType();
                        List<String> flowCodes =
                                schedules.stream().filter(sch -> sch.getServiceItem() != null && sch.getServiceItem().equals(changeType))
                                        .map(sch -> sch.getFlowCode()).collect(Collectors.toList());
                        RobotExecutionMo mo = robotExecutionRepository.selectRecentlyOne(it.getClientId(),it.getAppCode(), it.getTaskCode(), flowCodes);
                        if (mo!=null && mo.getStatus() == 0) {
                            nd.setError(1);
                        } else {
                            nd.setError(0);
                        }
                        nd.setFlowCodes(flowCodes);
                    });
                }
                int empCount = maps.stream().mapToInt(map -> map.getEmpCount()).sum();
                it.setNoDeclaredCount(empCount);
                int failCount = maps.stream().mapToInt(map -> map.getFailCount()).sum();
                it.setFailCount(failCount);
                int successCount = maps.stream().mapToInt(map -> map.getSuccessCount()).sum();
                it.setSuccessCount(successCount);
                noDeclaredCountList.add(empCount);
                if (empCount > 0) {
                    int minuteTime = maps.stream().map(map -> map.getMinuteTime() == null?0:map.getMinuteTime()).mapToInt(Integer::intValue).max().getAsInt();
                    int noDays = minuteTime / (24 * 60);
                    if (noDays == 0) {
                        int noHours = minuteTime / 60;
                        int ys = minuteTime % 60;
                        it.setNoDeclareTimeLong((noHours + "." + (ys/6)) + "小时");
                    } else {
                        it.setNoDeclareTimeLong(noDays + "天");
                    }
                } else {
                    it.setFlag(0);
                }
                maps = maps.stream().filter(map -> map.getEmpCount()!=null && map.getEmpCount()>0).collect(Collectors.toList());
                it.setNoDeclareList(maps);
            } else {
                it.setNoDeclaredCount(0);
                it.setFlag(0);
                it.setSuccessCount(0);
                it.setFailCount(0);
                it.setNoDeclareList(Lists.newArrayList());
            }

            DeclareValidateQueryDTO dto = new DeclareValidateQueryDTO();
            dto.setCustId(it.getClientId());
            dto.setAccountNumber(it.getAccountNumber());
            dto.setAddrName(it.getAddrName());
            dto.setCreateMonth(DateUtils.dateToStr(new Date(), "yyyy-MM"));
            dto.setBeginDate(DateUtils.dateToStr(new Date(), "yyyy-MM-dd"));
            dto.setEndDate(DateUtils.dateToStr(new Date(), "yyyy-MM-dd"));
            dto.setBusinessType(businessType);
            dto.setValidateStatus(Lists.newArrayList(0));
            dto.setDistinctBy(new String[]{"idCard","declareType"});

            Integer validateFailCount = rpaSaasAgentService.countDistinctByParams(dto);
            it.setValidateFailCount(validateFailCount>0?validateFailCount:null);
        } else {
            it.setNoDeclaredCount(0);
            it.setFlag(0);
            it.setSuccessCount(0);
            it.setFailCount(0);
            it.setDeadlineSettings(Lists.newArrayList());
            it.setNoDeclareList(Lists.newArrayList());
        }

    }

    private void getDeclareInfo(TodayExecDataDTO it, Map<Integer, List<CustomerBase>> custMap,
                                Map<String, List<PolicyAddrDeadlineSettingVO>> deadlineMap) {
        String appArgs = it.getAppArgs();
        if (StringUtils.isNotBlank(appArgs)) {
            JSONObject jsonObject = JSON.parseObject(appArgs);
            String addrName = jsonObject.getString("addrName");
            Integer addrId = jsonObject.getInteger("addrId");
            String businessTypeStr = jsonObject.getString("businessType");
            it.setAddrId(addrId);
            it.setAddrName(addrName);
            Integer businessType = businessTypeStr.equals("1001001") ? 1 : 2;
            it.setBusinessTypeName(BusinessTypeEnum.getNameByCode(businessType));
            if (it.getClientId()!=null && custMap.keySet().contains(it.getClientId())) {
                it.setCustomerName(custMap.get(it.getClientId()).get(0).getName());
            }

            String key = addrId + "-" + businessType;
            List<PolicyAddrDeadlineSettingVO> policyAddrDeadlineSettings = Lists.newArrayList();
            if (deadlineMap.containsKey(key)) {
                policyAddrDeadlineSettings = deadlineMap.get(key);
            } else {
                policyAddrDeadlineSettings = rpaSaasService.getPolicyAddrDeadlineSettings(addrId, businessType);
                if (CollectionUtils.isNotEmpty(policyAddrDeadlineSettings)) {
                    deadlineMap.put(key, policyAddrDeadlineSettings);
                }
            }

            // 获取申报期
            it.setDeadlineSettings(policyAddrDeadlineSettings);
            Integer minMonth = null;
            Integer minDay = null;
            Integer minHour = null;
            Integer maxMonth = null;
            Integer maxDay = null;
            Integer maxHour = null;
            if (CollectionUtils.isNotEmpty(policyAddrDeadlineSettings)) {
                for (PolicyAddrDeadlineSettingVO st : policyAddrDeadlineSettings) {
                    Integer monthBegin = st.getMonthBegin() == 2 ? -1 : st.getMonthBegin();
                    Integer monthEnd = st.getMonthEnd() == 2 ? -1 : st.getMonthEnd();
                    Integer dayBegin = st.getDayBegin();
                    Integer dayEnd = st.getDayEnd();
                    Integer hourBegin = st.getHourBegin();
                    Integer hourEnd = st.getHourEnd();
                    if (minMonth == null && maxMonth == null) {
                        minMonth = monthBegin;
                        maxMonth = monthEnd;
                        minDay = dayBegin;
                        maxDay = dayEnd;
                        minHour = hourBegin;
                        maxHour = hourEnd;
                    } else {
                        if (monthBegin < minMonth) {
                            minMonth = monthBegin;
                            minDay = dayBegin;
                            minHour = hourBegin;
                        } else if (monthBegin == minMonth && dayBegin < minDay) {
                            minDay = dayBegin;
                            minHour = hourBegin;
                        } else if(monthBegin == minMonth && dayBegin == minDay && hourBegin < minHour){
                            minHour = hourBegin;
                        }
                        if (monthEnd > maxMonth) {
                            maxMonth = monthEnd;
                            maxDay = dayEnd;
                            maxHour = hourEnd;
                        } else if (monthEnd == maxMonth && dayEnd > maxDay) {
                            maxDay = dayEnd;
                            maxHour = hourEnd;
                        } else if (monthEnd == maxMonth && dayEnd == maxDay && hourEnd > maxHour) {
                            maxHour = hourEnd;
                        }
                    }
                }
                it.setDeclareCycle(getMonthStr(minMonth).concat(minDay + "号").concat((minHour>10?minHour+"":"0"+minHour) + "时~")
                        .concat(getMonthStr(maxMonth).concat(maxDay + "号"))
                        .concat((maxHour>10?maxHour+"":"0"+maxHour) + "时"));
            }
        } else {
            it.setDeadlineSettings(Lists.newArrayList());
        }
    }

}
