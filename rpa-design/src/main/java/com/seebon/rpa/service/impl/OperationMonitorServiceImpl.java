package com.seebon.rpa.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.seebon.rpa.BusinessException;
import com.seebon.rpa.entity.Identity;
import com.seebon.rpa.entity.IgGridDefaultPage;
import com.seebon.rpa.entity.robot.RobotDataDict;
import com.seebon.rpa.entity.robot.dto.design.*;
import com.seebon.rpa.entity.robot.enums.WarnStatus;
import com.seebon.rpa.entity.robot.po.design.RobotOperationMonitorDetail;
import com.seebon.rpa.entity.robot.vo.design.RobotExecutionDetailMoVO;
import com.seebon.rpa.entity.robot.vo.design.RobotOperationMonitorDetailVO;
import com.seebon.rpa.entity.saas.po.declare.customer.CustomerBase;
import com.seebon.rpa.remote.RpaSaasService;
import com.seebon.rpa.repository.mongodb.RobotExecutionRepository;
import com.seebon.rpa.repository.mysql.OperationMonitorDao;
import com.seebon.rpa.repository.mysql.RobotClientDao;
import com.seebon.rpa.repository.mysql.RobotFlowStepDao;
import com.seebon.rpa.service.OperationMonitorService;
import com.seebon.rpa.service.RobotDataDictService;
import com.seebon.rpa.service.RobotWarnMessageService;
import com.seebon.rpa.utils.RespUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * TODO
 *
 * @author zjf
 * @describe 运维监控相关实现类
 * @date 2023/4/12 17:41
 */
@Slf4j
@Service
public class OperationMonitorServiceImpl implements OperationMonitorService{

    private final String key="OPERATION_SERVICE";
    @Autowired
    private OperationMonitorDao operationMonitorDao;

    @Autowired
    private RobotDataDictService dataDictService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RobotWarnMessageService robotWarnMessageService;

    @Autowired
    private RpaSaasService rpaSaasService;

    @Autowired
    private RobotExecutionRepository robotExecutionRepository;

    @Autowired
    private RobotFlowStepDao robotFlowStepDao;

    @Autowired
    private RobotClientDao robotClientDao;



    @Override
    public IgGridDefaultPage<OperationMonitorVo> listPage(Map<String,Object> map){
        log.info("监控运维入参{}",map);
        Integer page = Integer.parseInt(map.get("page").toString());
        Integer size = Integer.parseInt(map.get("size").toString());
        PageHelper.startPage(page, size);
        Page<OperationMonitorVo> monitorVoList=(Page<OperationMonitorVo>)operationMonitorDao.findByOperation(map);
        return new IgGridDefaultPage<>(monitorVoList,(int)monitorVoList.getTotal());
    }

    @Override
    public IgGridDefaultPage<RobotOperationMonitorDetailVO> queryDetailPage(Map<String,Object> map){
        log.info("预警明细入参{}",map);
        Integer page = Integer.parseInt(map.get("page").toString());
        Integer size = Integer.parseInt(map.get("size").toString());
        PageHelper.startPage(page, size);
        Page<RobotOperationMonitorDetailVO> monitorDetails=(Page<RobotOperationMonitorDetailVO>)operationMonitorDao.findDetailByOperation(map);
        monitorDetails.forEach(e->{
            String [] strList= StrUtil.split(e.getGroupId(), ",");
            List<String> list=Arrays.asList(strList);
            List<Long> groupIds = list.stream().map(Long::parseLong).collect(Collectors.toList());
            e.setGroupIds(groupIds);
            if(StringUtils.isNotBlank(e.getMessageContent())){
                OperationRequestVo operationRequestVo=new OperationRequestVo();
                BeanUtils.copyProperties(e, operationRequestVo);
                operationRequestVo.setCustName(e.getClientName());
                String content=robotWarnMessageService.replaceContent(operationRequestVo,e.getMessageContent());
                if(StringUtils.isNotBlank(e.getAddrName())){
                    content=content.replace("{addrName}", e.getAddrName());
                }
                if(StringUtils.isNotBlank(e.getServiceName())){
                    content=content.replace("{serviceName}", e.getServiceName());
                }
                e.setMessageContent(content);
            }
        });
        return new IgGridDefaultPage<>(monitorDetails,(int)monitorDetails.getTotal());
    }

    @Override
    public IgGridDefaultPage<RobotOperationMonitorDetailVO> operationPage(Map<String,Object> map) {

        //RPA-5251 运维预警：剔除不合作客户和未分配盒子的客户
        List<Integer> cooperationCustIdList = rpaSaasService.listCustomer(false,"1").parallelStream().map(Identity::getId).collect(Collectors.toList());
        map.put("cooperationCustIdList",cooperationCustIdList);
        if (CollectionUtils.isEmpty(cooperationCustIdList)){
            return new IgGridDefaultPage<>(Lists.newArrayList(), 0);
        }
        List<RobotOperationMonitorDetailVO> list = Lists.newArrayList();
        Integer count = operationMonitorDao.selectCountByParams(map);
        if (count > 0) {
            list = operationMonitorDao.selectPageListByParams(map);
            if (CollectionUtils.isNotEmpty(list)) {
                list.stream().forEach(it -> {
                    if (Lists.newArrayList("10017001", "10017002", "10017003").contains(it.getWarnCode())) {
                        it.setMessageTopic(String.format("%s%s系统{%s}执行异常", it.getAccountNumber(), it.getServiceName(), it.getFlowName()));
                    }
                    StringBuilder messageContent = new StringBuilder(String.format("<p>%s%s系统{%s}执行异常中断。</p>", it.getAddrName(), it.getServiceName(), it.getFlowName()));
                    messageContent.append(String.format("<p>申报账户：%s</p>", it.getAccountNumber()));
                    RobotOperationMonitorDetail robotOperationMonitorDetail = operationMonitorDao.selectByPrimaryKey(it.getId());
                    if (robotOperationMonitorDetail != null) {
                        messageContent.append(String.format("<p>异常原因：%s</p>", robotOperationMonitorDetail.getErrorResult()));
                        messageContent.append("<p>您可以采用以下处理方式：</p><p>1、请联系IT人员尽快排查问题，修复程序</p><p>2、如果是网站升级改造，需要通知客户，请尽快发出公告</p>");
                        it.setErrorResult(robotOperationMonitorDetail.getErrorResult());
                        String executionId = robotOperationMonitorDetail.getExecutionId();
                        it.setExecutionId(executionId);
                        it.setHandleDate(robotOperationMonitorDetail.getHandleDate());
                        String flowCode = robotOperationMonitorDetail.getFlowCode();
                        if (StringUtils.isNotBlank(executionId) && StringUtils.isNotBlank(flowCode)) {

                            RobotExecutionMo executionMo = robotExecutionRepository.selectOneById(executionId);
                            if (executionMo !=null) {
                                Map<String, Object> params = Maps.newHashMap();
                                params.put("executionCode", executionMo.getExecutionCode());
                                params.put("flowCode", flowCode);
                                params.put("status", 0);
                                RobotExecutionDetailMo detailMo = robotExecutionRepository.selectExecutionDetailOne(params);
                                if (detailMo != null) {
                                    RobotExecutionDetailMoVO moVO = new RobotExecutionDetailMoVO();
                                    BeanUtils.copyProperties(detailMo, moVO);
                                    params.put("executionCode", executionMo.getExecutionCode());
                                    params.put("stepCodes", Lists.newArrayList(detailMo.getStepCode()));
                                    params.put("flowCodes", Lists.newArrayList(flowCode));
                                    List<RobotExecutionFileInfo> pictures = robotFlowStepDao.selectByExecutionCode(params);
                                    if(CollectionUtils.isNotEmpty(pictures)){
                                        pictures=pictures.stream().filter(e -> e.getFileFullPath()!=null&&e.getFileFullPath().endsWith(".png")||e.getFileFullPath().endsWith(".jpg")).collect(Collectors.toList());
                                    }
                                    moVO.setFiles(pictures);
                                    it.setDetailMoVO(moVO);
                                    if (Lists.newArrayList("10017001",
                                            "10017002", "10017003").contains(it.getWarnCode()) && CollectionUtils.isNotEmpty(pictures)) {
                                        messageContent.append("<div style=\"width:100%; overflow-x:auto;\">");
                                        for (RobotExecutionFileInfo file : pictures) {
                                            messageContent.append(String.format("<br/><img src=\"%s\"/>", file.getFileFullPath()));
                                        }
                                        messageContent.append("</div>");
                                    }
                                }
                            }
                        }
                    } else {
                        messageContent.append(String.format("<p>异常原因：%s</p>", ""));
                        messageContent.append("<p>您可以采用以下处理方式：</p><p>1、请联系IT人员尽快排查问题，修复程序</p><p>2、如果是网站升级改造，需要通知客户，请尽快发出公告</p>");
                    }
                    if (Lists.newArrayList("10017001", "10017002", "10017003").contains(it.getWarnCode())) {
                        it.setMessageContent(messageContent.toString());
                    }
                });
            }
        }
        return new IgGridDefaultPage<>(list, count);
    }

    @Override
    public void export(HttpServletResponse response, Map<String,Object> map) {
        map.remove("size");
        map.remove("start");

        //RPA-5251 运维预警：剔除不合作客户和未分配盒子的客户
        List<Integer> cooperationCustIdList = rpaSaasService.listCustomer(false,"1").parallelStream().map(Identity::getId).collect(Collectors.toList());
        map.put("cooperationCustIdList",cooperationCustIdList);
        List<RobotOperationMonitorDetailVO> list = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(cooperationCustIdList)){
            list = operationMonitorDao.selectPageListByParams(map);
        }
        if (CollectionUtils.isNotEmpty(list)) {
            list.parallelStream().forEach(it -> {
                RobotOperationMonitorDetail robotOperationMonitorDetail = operationMonitorDao.selectByPrimaryKey(it.getId());
                String serviceName = it.getServiceName();
                String serviceItemName = it.getServiceItemName();
                if (StringUtils.isNotBlank(serviceItemName)) {
                    it.setServiceName(serviceName.concat("-").concat(serviceItemName));
                }
                if (robotOperationMonitorDetail != null) {
                    it.setErrorResult(robotOperationMonitorDetail.getErrorResult());
                    it.setMaxCreateTime(robotOperationMonitorDetail.getCreateTime());
                }
            });
        }
        RespUtils.write(response, "下载异常", RobotOperationMonitorDetailVO.class, list);
    }

    @Override
    public int finish(HandleMessageDTO dto) {
        List<RobotOperationMonitorDetailVO> detailVOList = dto.getDetailVOList();
        if (CollectionUtils.isEmpty(detailVOList)) {
            throw new BusinessException("请选择需要办结的记录！");
        }
        int count = 0;
        for (RobotOperationMonitorDetailVO detailVO : detailVOList) {
            count+=operationMonitorDao.updateFinish(detailVO, 1, dto.getHandleType(), dto.getHandleRemark(), dto.getHandleLink());
        }
        return count;
    }

    @Override
    public int updateWarnType(String warnType, RobotOperationMonitorDetailVO detailVO) {
        return operationMonitorDao.updateWarnType(warnType, detailVO);
    }

    @Override
    public List<CustomerBase> operationListCustomer() {
        //RPA-5251 运维预警：剔除不合作客户和未分配盒子的客户
        List<CustomerBase> customerBases = rpaSaasService.listCustomer(false, "1");
        if (CollectionUtils.isEmpty(customerBases)) return Lists.newArrayList();

        List<Integer> custIds = customerBases.parallelStream().map(Identity::getId).collect(Collectors.toList());
        List<Integer> existsCustIds = robotClientDao.selectExistsCustIds(custIds);
        if (CollectionUtils.isEmpty(existsCustIds)) return Lists.newArrayList();

        Map<Integer, Integer> existsCustIdMap = existsCustIds.parallelStream().collect(Collectors.toMap(Function.identity(), Function.identity()));
        return customerBases.parallelStream().filter(x->existsCustIdMap.containsKey(x.getId())).collect(Collectors.toList());
    }

    @Override
    public int updateExceptionMessage(HandleMessageVo handleMessageVo){
        log.info("预警明细办结信息处理入参{}",handleMessageVo);
        Example example=new Example(RobotOperationMonitorDetail.class);
        example.selectProperties("id");
        example.createCriteria().andEqualTo("clientId",handleMessageVo.getClientId())
               .andEqualTo("handleStatus",handleMessageVo.getHandleStatus())
               .andEqualTo("warnType",handleMessageVo.getWarnTypeCode())
               .andEqualTo("machineCode",handleMessageVo.getMachineCode())
               .andLessThanOrEqualTo("reportTime", handleMessageVo.getReportStartTime());
        List<RobotOperationMonitorDetail> list=operationMonitorDao.selectByExample(example);
        List<Long> ids=list.parallelStream().filter(e -> e.getId() !=null)
                         .map(RobotOperationMonitorDetail::getId).distinct().collect(Collectors.toList());
        int result=operationMonitorDao.updateExceptionMessage(ids,handleMessageVo.getHandleType()
                ,handleMessageVo.getHandleRemark(),handleMessageVo.getHandleLink());
        return result;
    }

    @Override
    public void insertOperationDetail(List<OperationRequestVo> operationRequestVos,String warnCode){
        log.info("监控运维数据录入参数{}",operationRequestVos);
        List<RobotOperationMonitorDetail> operationMonitorDetailVoList=new ArrayList<>();
        operationRequestVos.stream().forEach(e->{
            RobotOperationMonitorDetail operationMonitorDetailVo=new RobotOperationMonitorDetail();
            BeanUtils.copyProperties(e,operationMonitorDetailVo);
            operationMonitorDetailVo.setReportTime(DateUtil.today());
            operationMonitorDetailVo.setWarnType(warnCode);
            operationMonitorDetailVo.setClientName(e.getCustName());
            operationMonitorDetailVo.setCreateTime(new Date());
            operationMonitorDetailVo.setHandleStatus(0);
            operationMonitorDetailVo.setAppCode(e.getAppCode());
            String appArgs=e.getAppArgs();
            if(StringUtils.isNotBlank(appArgs)){
                Map<String,String> appMap=analysisAppArgs(appArgs);
                operationMonitorDetailVo.setAddrName(appMap.get("addrName"));
                operationMonitorDetailVo.setServiceName(appMap.get("serviceName"));

            }
            operationMonitorDetailVoList.add(operationMonitorDetailVo);
        });
        operationMonitorDao.insertOperationDetail(operationMonitorDetailVoList);
        log.info("当前预警项目是{},共插入运维监控表{}条数据",WarnStatus.getNameByCode(warnCode),operationMonitorDetailVoList.size());
    }

    @Override
    public Map<String,Integer> operationCount(){
        Map<String,Integer> map=new HashMap<>();
        Integer count=operationMonitorDao.selectOperationCount(DateUtil.today());
        if(count!=null){
            map.put("operationCount",count);
        }else {
            map.put("operationCount",0);
        }
        return map;
    }

    @Override
    public Map<String,String> analysisAppArgs(String appArgs){
        Map<String,String> appMap=new HashMap();
        List<RobotDataDict> dictVos;
        if(ObjectUtil.isNotNull(redisTemplate.opsForValue().get(key))){
            String value=redisTemplate.opsForValue().get(key).toString();
            dictVos=JSONArray.parseArray(value,RobotDataDict.class);
        }else{
            dictVos=dataDictService.findList("1001");
            redisTemplate.opsForValue().set(key,JSON.toJSONString(dictVos));
        }
        Map<String,String> map=dictVos.stream().collect(Collectors.toMap(RobotDataDict::getDictCode,RobotDataDict::getDictName));
        JSONObject jsonObject=JSONObject.parseObject(appArgs);
        String addressName=jsonObject.getString("addrName");
        appMap.put("addrName",addressName);
        String businessType=jsonObject.getString("businessType");
        if(StringUtils.isNotBlank(businessType)){
            appMap.put("serviceName",map.get(businessType));

        }
        return appMap;
    }

    @Override
    public List<OperationRequestVo> setCustomerName( List<OperationRequestVo> list){
        List<CustomerBase> customerBases= rpaSaasService.listCustomer(false, "");
        Map<Integer,String> customerMap=customerBases.stream().collect(Collectors.toMap(CustomerBase::getId,CustomerBase::getName));
        list.forEach(e->{
            if(e.getClientId()!=null){
                e.setCustName(customerMap.get(e.getClientId()));
            }
        });
        return list;
    }

    @Override
    public void downloadExcel(HttpServletResponse response,RobotOperationExcelVo vo){
        List<RobotOperationExcelVo> robotOperationExcelVos=operationMonitorDao.errorInfoList(vo);
        robotOperationExcelVos.forEach(e->{
            if(StringUtils.isNotBlank(e.getFailCode())){
                String [] strList= StrUtil.split(e.getFailCode(), ";");
                List<String> list=Arrays.asList(strList);
                List<String> collect = list.stream().distinct().collect(Collectors.toList());
                String join=StringUtils.join(collect,",");
                e.setFailCode(join);
            }

        });

        RespUtils.write(response, "异常情况信息下载", RobotOperationExcelVo.class, robotOperationExcelVos);

    }
}
