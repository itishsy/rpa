package com.seebon.rpa.schedule;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.seebon.rpa.entity.robot.RobotExecutionData;
import com.seebon.rpa.entity.robot.RobotOfferRuleItem;
import com.seebon.rpa.entity.agent.dto.openApi.DeclareCounterOfferDTO;
import com.seebon.rpa.entity.robot.vo.RobotOfferRuleVO;
import com.seebon.rpa.entity.saas.PolicyDeclareOperationTypeDictVO;
import com.seebon.rpa.remote.RpaSaasAgentService;
import com.seebon.rpa.remote.RpaSaasService;
import com.seebon.rpa.repository.mysql.RobotExecutionDataDao;
import com.seebon.rpa.repository.mysql.RobotOfferRuleDao;
import com.seebon.rpa.utils.ConvertUtl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class RobotExecutionDataTask {
    @Autowired
    private RpaSaasService rpaSaasService;
    @Autowired
    private RpaSaasAgentService rpaSaasAgentService;
    @Autowired
    private RobotExecutionDataDao executionDataDao;
    @Autowired
    private SimpleLogin simpleLogin;
    @Autowired
    private RobotOfferRuleDao robotOfferRuleDao;
    @Value("${task.login.userId}")
    private Integer userId;
    @Value("${task.login.userName}")
    private String userName;
    @Value("${task.login.custId}")
    private Integer custId;

    @Scheduled(cron = "0 */1 * * * ?")
    public void task() {
        simpleLogin.doLogin(userId, userName, custId);
        this.handle();
        simpleLogin.loginOut();
    }

    public void handle() {
        Example example = new Example(RobotExecutionData.class);
        example.createCriteria().andEqualTo("sync", 0).andEqualTo("flagType", 1).andIsNotNull("clientId").andIsNotNull("machineCode")
                .andIsNotNull("addrName").andIsNotNull("businessType").andIsNotNull("declareType").andIsNotNull("tplTypeCode").andIsNotNull("operationType");
        List<RobotExecutionData> executionDataList = executionDataDao.selectByExample(example);
        if (CollectionUtils.isEmpty(executionDataList)) {
            log.info("没有需要回盘的数据.");
            return;
        }
        log.info("回盘的数据条数：" + executionDataList.size());
        Map<String, String> operationTypeMap = this.getOperationTypeMap();
        Map<String, List<RobotExecutionData>> executionMap = executionDataList.stream().collect(Collectors.groupingBy(ex -> ex.getAddrName() + "_" +
                ex.getBusinessType() + "_" + ex.getDeclareType() + "_" + ex.getTplTypeCode() + "_" + ex.getOperationType() + "_" + ex.getAccountNumber()));
        for (String key : executionMap.keySet()) {
            String[] keys = key.split("_");
            Map<String, Object> params = Maps.newHashMap();
            params.put("addrName", keys[0]);
            params.put("businessType", Lists.newArrayList(keys[1]));
            params.put("declareType", Lists.newArrayList(keys[2]));
            params.put("declareWebsite", keys[3]);
            params.put("currentNode", keys[4]);
            params.put("status", 1);
            List<RobotOfferRuleVO> ruleList = robotOfferRuleDao.selectByParams(params);
            List<DeclareCounterOfferDTO> declareOfferList = Lists.newArrayList();
            for (RobotExecutionData data : executionMap.get(key)) {
                DeclareCounterOfferDTO offerVO = new DeclareCounterOfferDTO();
                offerVO.setOutId(data.getId());
                offerVO.setAddrName(data.getAddrName());
                offerVO.setBusinessType(data.getBusinessType());
                offerVO.setDeclareType(data.getDeclareType());
                offerVO.setAccountNumber(data.getAccountNumber());
                offerVO.setIdCard(data.getIdCard());
                offerVO.setDeclareMonth(data.getDeclareMonth());
                offerVO.setTplTypeCode(data.getTplTypeCode());
                offerVO.setRobotExecStatus(data.getRobotExecStatus());
                offerVO.setFailReason(data.getFailReason());

                //申报状态，1：待申报，2：申报中，4：申报成功，5：申报失败
                RobotOfferRuleItem ruleItem = ConvertUtl.getRuleItem(ruleList, data.getFailReason());
                if (ruleItem != null) {
                    offerVO.setDeclareStatus(ruleItem.getDeclareStatus());
                    if (ruleItem.getDeclareStatus() == 5) {
                        offerVO.setFailType(1);//失败类型：1-全部失败,2-部分失败(同一个系统的部分失败，后期在处理)
                        //核验回盘
                        if ("1015".equals(keys[4])) {
                            if (ruleItem.getReplaceType() == 2) {
                                offerVO.setFailReason(null);
                                offerVO.setKeepLastFailReason(1);
                            }
                        }
                    } else if (ruleItem.getDeclareStatus() == 4) {
                        offerVO.setFailReason(" ");
                    }
                    //申报数据操作类型 （参考表 policy_declare_operation_type_dict）
                    offerVO.setOperationType(ruleItem.getNextNode());
                    //节点结果备注
                    String nodeComment = "当前" + operationTypeMap.get(keys[4]);
                    if (StringUtils.isNotBlank(ruleItem.getNextNode())) {
                        nodeComment = nodeComment + ",下一步" + operationTypeMap.get(ruleItem.getNextNode());
                    }
                    offerVO.setNodeComment(nodeComment);
                    if (ruleItem.getReplaceType() == 3) {
                        if (StringUtils.isNotBlank(ruleItem.getReplaceContent())) {
                            offerVO.setFailReason(ruleItem.getReplaceContent());
                        }
                    }
                } else {
                    RobotExecutionData updateData = new RobotExecutionData();
                    updateData.setId(data.getId());
                    updateData.setSyncFailReason("未配置业务场景提示.");
                    executionDataDao.updateByPrimaryKeySelective(updateData);

                    offerVO.setDeclareStatus(2);
                    offerVO.setFailReason(data.getFailReason());
                }
                declareOfferList.add(offerVO);
            }

            //更新状态
            if (CollectionUtils.isNotEmpty(declareOfferList)) {
                List<DeclareCounterOfferDTO> result = rpaSaasAgentService.declareCounterOffer(declareOfferList);
                for (DeclareCounterOfferDTO offerDTO : result) {
                    RobotExecutionData updateData = new RobotExecutionData();
                    updateData.setId(offerDTO.getOutId());
                    if (offerDTO.getCounterOfferStatus() == 1) {
                        updateData.setSync(1);
                        updateData.setSyncTime(new Date());
                    } else {
                        updateData.setSyncFailReason(offerDTO.getCounterOfferFailReason());
                    }
                    executionDataDao.updateByPrimaryKeySelective(updateData);
                }
            }
        }
    }

    private Map<String, String> getOperationTypeMap() {
        List<PolicyDeclareOperationTypeDictVO> operationTypes = rpaSaasService.getAllOperationTypes();
        if (CollectionUtils.isEmpty(operationTypes)) {
            return Maps.newHashMap();
        }
        return operationTypes.stream().collect(Collectors.toMap(k -> k.getCode(), v -> v.getName()));
    }
}
