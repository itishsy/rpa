package com.seebon.rpa.service.impl;

import cn.hutool.core.lang.Dict;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.seebon.common.utils.DateUtils;
import com.seebon.rpa.BusinessException;
import com.seebon.rpa.auth.SecurityContext;
import com.seebon.rpa.auth.Session;
import com.seebon.rpa.entity.IgGridDefaultPage;
import com.seebon.rpa.entity.agent.enums.BusinessTypeEnum;
import com.seebon.rpa.entity.agent.enums.DeclareStatusEnum;
import com.seebon.rpa.entity.agent.enums.DeclareTypeEnum;
import com.seebon.rpa.entity.agent.enums.TplTypeEnum;
import com.seebon.rpa.entity.robot.RobotApp;
import com.seebon.rpa.entity.robot.RobotOfferRule;
import com.seebon.rpa.entity.robot.RobotOfferRuleItem;
import com.seebon.rpa.entity.robot.vo.RobotAppVO;
import com.seebon.rpa.entity.robot.vo.RobotOfferRuleVO;
import com.seebon.rpa.entity.saas.PolicyDeclareOperationTypeDictVO;
import com.seebon.rpa.entity.saas.dto.AddrBusinessChangeProcessDTO;
import com.seebon.rpa.remote.RpaSaasService;
import com.seebon.rpa.repository.mysql.RobotOfferRuleDao;
import com.seebon.rpa.repository.mysql.RobotOfferRuleItemDao;
import com.seebon.rpa.service.CustomerCommandService;
import com.seebon.rpa.service.RobotOfferRuleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RobotOfferRuleServiceImpl implements RobotOfferRuleService {
    @Autowired
    private RobotOfferRuleDao robotOfferRuleDao;
    @Autowired
    private RobotOfferRuleItemDao robotOfferRuleItemDao;
    @Autowired
    private RpaSaasService rpaSaasService;
    @Autowired
    private CustomerCommandService customerCommandService;

    @Override
    public IgGridDefaultPage<RobotOfferRuleVO> page(Map<String, Object> params) {
        log.info("回盘规则配置表入参{}", params);
        int total = robotOfferRuleDao.countByParams(params);
        if (total == 0) {
            return new IgGridDefaultPage<RobotOfferRuleVO>(Lists.newArrayList(), total);
        }
        List<RobotOfferRuleVO> list = robotOfferRuleDao.selectByParams(params);
        Map<String, String> operationTypeMap = this.getOperationTypeMap();
        list.stream().forEach(vo -> {
            vo.setCurrentNodeName(operationTypeMap.get(vo.getCurrentNode()));
            RobotAppVO appVO = rpaSaasService.getRobotDeclareType(vo.getDeclareWebsite());
            if (appVO != null) {
                vo.setDeclareWebsiteName(appVO.getBusinessType());
            }
            if (CollectionUtils.isNotEmpty(vo.getRuleItemList())) {
                List<RobotOfferRuleItem> ruleItems = vo.getRuleItemList().stream().filter(item -> StringUtils.isBlank(item.getNextNode())).collect(Collectors.toList());
                List<RobotOfferRuleItem> nextNodes = vo.getRuleItemList().stream().filter(item -> StringUtils.isNotBlank(item.getNextNode())).collect(Collectors.toList());
                vo.setRuleItemList(ruleItems);
                vo.setNextNodeList(nextNodes);
            }
        });
        return new IgGridDefaultPage<RobotOfferRuleVO>(list, total);
    }

    @Override
    public List<RobotOfferRuleVO> list(RobotOfferRuleVO ruleVO) {
        log.info("获取回盘规则配置表入参{}", JSON.toJSONString(ruleVO));

        Map<String, Object> params = Maps.newHashMap();
        params.put("addrName", ruleVO.getAddrName());
        params.put("businessType", Lists.newArrayList(ruleVO.getBusinessType()));
        params.put("declareType", Lists.newArrayList(ruleVO.getDeclareType()));
        params.put("declareWebsite", ruleVO.getDeclareWebsite());
        params.put("ruleType", ruleVO.getRuleType());
        if (StringUtils.isNotBlank(ruleVO.getNextNode())) {
            params.put("nextNode", ruleVO.getNextNode());
        }
        params.put("status", 1);
        return robotOfferRuleDao.selectByParams(params);
    }

    @Override
    public RobotOfferRuleVO getByRuleId(Integer ruleId) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("id", ruleId);
        List<RobotOfferRuleVO> list = robotOfferRuleDao.selectByParams(params);
        if (CollectionUtils.isNotEmpty(list)) {
            RobotOfferRuleVO vo = list.get(0);
            if (CollectionUtils.isNotEmpty(vo.getRuleItemList())) {
                List<RobotOfferRuleItem> ruleItems = vo.getRuleItemList().stream().filter(item -> StringUtils.isBlank(item.getNextNode())).collect(Collectors.toList());
                List<RobotOfferRuleItem> nextNodes = vo.getRuleItemList().stream().filter(item -> StringUtils.isNotBlank(item.getNextNode())).collect(Collectors.toList());
                vo.setRuleItemList(ruleItems);
                vo.setNextNodeList(nextNodes);
            }
            return vo;
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(RobotOfferRuleVO vo) {
        Integer ruleId = vo.getId();
        log.info("回盘规则新增或修改入参{}", vo);
        Session user = SecurityContext.currentUser();
        if (vo.getId() == null) {
            Example example = new Example(RobotOfferRule.class);
            example.createCriteria().andEqualTo("addrId", vo.getAddrId())
                    .andEqualTo("businessType", vo.getBusinessType())
                    .andEqualTo("declareWebsite", vo.getDeclareWebsite())
                    .andEqualTo("declareType", vo.getDeclareType())
                    .andEqualTo("currentNode", vo.getCurrentNode())
                    .andEqualTo("status", vo.getStatus());
            int count = robotOfferRuleDao.selectCountByExample(example);
            if (count > 0) {
                throw new BusinessException("该回盘规则已配置，请勿重复添加");
            }
            RobotOfferRule rule = new RobotOfferRule();
            BeanUtils.copyProperties(vo, rule);
            rule.setCreateId((int) user.getId());
            rule.setCreateTime(new Date());
            rule.setUpdateId((int) user.getId());
            rule.setUpdateTime(new Date());
            robotOfferRuleDao.insertSelective(rule);

            if (CollectionUtils.isNotEmpty(vo.getRuleItemList())) {
                for (RobotOfferRuleItem item : vo.getRuleItemList()) {
                    item.setRuleId(rule.getId());
                    item.setNextNode(null);
                    item.setCreateTime(new Date());
                    item.setUpdateTime(new Date());
                    robotOfferRuleItemDao.insertSelective(item);
                }
            }
            if (CollectionUtils.isNotEmpty(vo.getNextNodeList())) {
                for (RobotOfferRuleItem item : vo.getNextNodeList()) {
                    item.setRuleId(rule.getId());
                    item.setCreateTime(new Date());
                    item.setUpdateTime(new Date());
                    robotOfferRuleItemDao.insertSelective(item);
                }
            }
            ruleId = rule.getId();
            saveAddrBusinessProcess(vo, getRuleInfo(vo), "", "新增回盘规则");
        } else {

            RobotOfferRule robotOfferRule = robotOfferRuleDao.selectByPrimaryKey(vo.getId());
            RobotOfferRuleVO oldVo = new RobotOfferRuleVO();
            BeanUtils.copyProperties(robotOfferRule, oldVo);

            Example example = new Example(RobotOfferRuleItem.class);
            example.createCriteria().andEqualTo("ruleId", vo.getId());
            List<RobotOfferRuleItem> robotOfferRuleItems = robotOfferRuleItemDao.selectByExample(example);
            if (CollectionUtils.isNotEmpty(robotOfferRuleItems)) {
                List<RobotOfferRuleItem> ruleItems = robotOfferRuleItems.stream().filter(item -> StringUtils.isBlank(item.getNextNode())).collect(Collectors.toList());
                List<RobotOfferRuleItem> nextNodes = robotOfferRuleItems.stream().filter(item -> StringUtils.isNotBlank(item.getNextNode())).collect(Collectors.toList());
                oldVo.setRuleItemList(ruleItems);
                oldVo.setNextNodeList(nextNodes);
            }

            //先删除
            robotOfferRuleItemDao.deleteByRuleId(vo.getId());
            //保存数据
            RobotOfferRule update = new RobotOfferRule();
            BeanUtils.copyProperties(vo, update);
            update.setUpdateId((int) user.getId());
            update.setUpdateTime(new Date());
            robotOfferRuleDao.updateByPrimaryKeySelective(update);

            if (CollectionUtils.isNotEmpty(vo.getRuleItemList())) {
                for (RobotOfferRuleItem item : vo.getRuleItemList()) {
                    item.setRuleId(update.getId());
                    item.setNextNode(null);
                    item.setCreateTime(new Date());
                    item.setUpdateTime(new Date());
                    robotOfferRuleItemDao.insertSelective(item);
                }
            }
            if (CollectionUtils.isNotEmpty(vo.getNextNodeList())) {
                for (RobotOfferRuleItem item : vo.getNextNodeList()) {
                    item.setRuleId(update.getId());
                    item.setCreateTime(new Date());
                    item.setUpdateTime(new Date());
                    robotOfferRuleItemDao.insertSelective(item);
                }
            }
            saveAddrBusinessProcess(vo, getRuleInfo(vo), getRuleInfo(oldVo), "修改回盘规则");
        }
        Dict dict = Dict.create().set("ruleId", ruleId);
        customerCommandService.addCustomerCommand("offerRule", JSON.toJSONString(dict), "回盘规则");
    }

    private String getRuleInfo(RobotOfferRuleVO vo) {

        Map<Integer, String> ruleMap = Maps.newHashMap();
        ruleMap.put(1, "完全等于");
        ruleMap.put(2, "包含");
        ruleMap.put(3, "以此信息开头");
        ruleMap.put(4, "以此信息结尾");

        Integer declareType = vo.getDeclareType();

        String declareTypeName = DeclareTypeEnum.getNameByCode(declareType);

        String declareWebsite = vo.getDeclareWebsite();
        String declareWebsiteName = TplTypeEnum.getNameByCode(declareWebsite);

        Integer status = vo.getStatus();

        String statusName = status == 1 ? "启用" : "停用";

        List<RobotOfferRuleItem> ruleItemList = vo.getRuleItemList();
        List<String> ruleItemInfos = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(ruleItemList)) {
            Map<Integer, List<RobotOfferRuleItem>> mapList = ruleItemList.stream().collect(Collectors.groupingBy(it -> it.getDeclareStatus()));
            for (Integer declareStatus : mapList.keySet()) {
                List<String> subItemInfos = Lists.newArrayList();
                List<RobotOfferRuleItem> robotOfferRuleItems = mapList.get(declareStatus);
                for (RobotOfferRuleItem item : robotOfferRuleItems) {
                    subItemInfos.add(String.format("匹配规则：%s，内容：%s，替换信息：%s，备注说明：%s，状态：%s",
                            ruleMap.get(item.getMatchRule()), item.getContent(), getReplaceInfo(item), StringUtils.isNotBlank(item.getRemark())?item.getRemark():"",
                            item.getStatus() == 1 ? "启用" : "禁用"));
                }
                ruleItemInfos.add(String.format("%s：%s", DeclareStatusEnum.getNameByCode(declareStatus), StringUtils.join(subItemInfos, "，")));
            }
        }

        List<RobotOfferRuleItem> nextNodeList = vo.getNextNodeList();
        List<String> nextNodeInfos = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(nextNodeList)) {
            Map<String, String> operationTypeMap = getOperationTypeMap();
            for (RobotOfferRuleItem item : nextNodeList) {
                nextNodeInfos.add(String.format("匹配规则：%s，内容：%s，替换信息：%s，备注说明：%s，下一节点：%s，状态：%s",
                        ruleMap.get(item.getMatchRule()), item.getContent(), getReplaceInfo(item), StringUtils.isNotBlank(item.getRemark())?item.getRemark():"",
                        StringUtils.isNotBlank(item.getNextNode())?operationTypeMap.get(item.getNextNode()):"无",
                        item.getStatus() == 1 ? "启用" : "禁用"));
            }
        }
        return String.format("%s，%s，状态：%s，%s%s", declareWebsiteName, declareTypeName, statusName,
                CollectionUtils.isNotEmpty(ruleItemInfos)?String.format("判断回盘状态：%s。", StringUtils.join(ruleItemInfos, "；")):"",
                CollectionUtils.isNotEmpty(nextNodeInfos)?String.format("判断下一节点：%s。", StringUtils.join(nextNodeInfos, "；")):""
                );
    }

    private String getReplaceInfo(RobotOfferRuleItem item) {
        Map<Integer, String> replaceMap = Maps.newHashMap();
        replaceMap.put(1, "不替换");
        replaceMap.put(2, "申报中信息");
        replaceMap.put(3, "自定义");
        replaceMap.put(4, "拼接前缀");
        replaceMap.put(5, "追加");
        Integer replaceType = item.getReplaceType();
        if (Lists.newArrayList(1,2).contains(replaceType)) {
            return replaceMap.get(replaceType);
        } else {
            String replaceContent = item.getReplaceContent();
            return String.format("%s%s", replaceMap.get(replaceType), replaceContent);
        }
    }

    private void saveAddrBusinessProcess(RobotOfferRuleVO vo, String newChedule, String oldChedule, String comment) {
        try {
            String addrName = vo.getAddrName();
            Integer addrId = vo.getAddrId();
            Integer busType = vo.getBusinessType();

            Session session = SecurityContext.currentUser();
            AddrBusinessChangeProcessDTO dto = new AddrBusinessChangeProcessDTO();
            dto.setAddrId(addrId);
            dto.setAddrName(addrName);
            dto.setBusinessType(busType);
            dto.setBusinessTypeName(BusinessTypeEnum.getNameByCode(busType));
            dto.setChangeType("调整回盘规则");
            dto.setChangeReason(null);
            dto.setCreateId((int)session.getId());
            dto.setCreateName(session.getName());
            dto.setCreateTime(DateUtils.dateToStr(new Date(), "yyyy-MM-dd HH:mm:ss"));
            dto.setOriginalValue(oldChedule);
            dto.setNewValue(newChedule);
            dto.setComment(comment);
            rpaSaasService.saveAddrBusinessProcess(dto);
        }catch (Exception e) {
            log.info("保存地区业务变动数据异常：{}", e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Integer itemId) {
        log.info("回盘规则删除：{}", itemId);
        RobotOfferRuleItem item = new RobotOfferRuleItem();
        item.setId(itemId);
        item.setUpdateTime(new Date());
        robotOfferRuleItemDao.updateByPrimaryKeySelective(item);
    }

    private Map<String, String> getOperationTypeMap() {
        List<PolicyDeclareOperationTypeDictVO> operationTypes = rpaSaasService.getAllOperationTypes();
        if (CollectionUtils.isEmpty(operationTypes)) {
            return Maps.newHashMap();
        }
        return operationTypes.stream().collect(Collectors.toMap(k -> k.getCode(), v -> v.getName()));
    }
}
