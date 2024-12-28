package com.seebon.rpa.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.seebon.rpa.BusinessException;
import com.seebon.rpa.auth.SecurityContext;
import com.seebon.rpa.auth.Session;
import com.seebon.rpa.entity.IgGridDefaultPage;
import com.seebon.rpa.entity.robot.RobotClientRule;
import com.seebon.rpa.entity.robot.RobotClientRuleCondition;
import com.seebon.rpa.entity.robot.RobotTaskQueue;
import com.seebon.rpa.entity.robot.vo.RobotClientRuleVO;
import com.seebon.rpa.entity.saas.po.declare.customer.CustomerBase;
import com.seebon.rpa.remote.RpaSaasService;
import com.seebon.rpa.repository.mysql.RobotClientRuleConditionDao;
import com.seebon.rpa.repository.mysql.RobotClientRuleDao;
import com.seebon.rpa.service.RobotClientRuleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RobotClientRuleServiceImpl implements RobotClientRuleService {
    @Autowired
    private RpaSaasService rpaSaasService;
    @Autowired
    private RobotClientRuleDao clientRuleDao;
    @Autowired
    private RobotClientRuleConditionDao clientRuleConditionDao;

    @Override
    public IgGridDefaultPage<RobotClientRuleVO> page(Map<String, Object> map) {
        Example example = new Example(RobotClientRule.class);
        example.orderBy("sort").asc();
        Example.Criteria ca = example.createCriteria();
        if (map.get("clientId") != null) {
            ca.andEqualTo("clientId", map.get("clientId"));
        }
        List<Integer> statusList = (List<Integer>) map.get("statusList");
        if (CollectionUtils.isNotEmpty(statusList)) {
            ca.andIn("status", statusList);
        }
        Integer start = (Integer) map.get("start");
        Integer size = (Integer) map.get("size");
        PageHelper.offsetPage(start, size);
        Page<RobotClientRule> page = (Page<RobotClientRule>) clientRuleDao.selectByExample(example);
        List<RobotClientRuleVO> result = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(page.getResult())) {
            List<CustomerBase> customerList = rpaSaasService.listCustomer(false, "");
            Map<Integer, String> customerMap = customerList.stream().collect(Collectors.toMap(k -> k.getId(), v -> v.getName(), (x, y) -> x));
            Map<Integer, List<RobotClientRuleCondition>> ruleConditionMap = this.getRuleConditionList(page.getResult().stream().map(item -> item.getId()).collect(Collectors.toList()));
            page.getResult().stream().forEach(it -> {
                RobotClientRuleVO ruleVO = new RobotClientRuleVO();
                BeanUtils.copyProperties(it, ruleVO);
                ruleVO.setCustomerName(customerMap.get(it.getClientId()));
                List<RobotClientRuleCondition> conditionList = ruleConditionMap.get(it.getId());
                if (CollectionUtils.isNotEmpty(conditionList)) {
                    List<String> conclusions = Lists.newArrayList();
                    conditionList.stream().forEach(item -> {
                        conclusions.add(item.getConclusion());
                    });
                    ruleVO.setConclusion(String.join(",", conclusions));
                }
                result.add(ruleVO);
            });
        }
        return new IgGridDefaultPage<RobotClientRuleVO>(result, (int) page.getTotal());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer enableOrStop(Integer id, Integer status) {
        Session session = SecurityContext.currentUser();
        RobotClientRule clientRule = clientRuleDao.selectByPrimaryKey(id);
        RobotClientRule update = new RobotClientRule();
        update.setId(id);
        update.setStatus(status);
        update.setUpdateId((int) session.getId());
        update.setUpdateTime(new Date());
        clientRuleDao.updateByPrimaryKeySelective(update);
        if (status == 2) {
            this.updateSortEx(clientRule.getClientId(), id);
        } else {
            this.updateSort(id, clientRule.getSort());
        }
        return 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer save(RobotClientRuleVO reqVO) {
        if (CollectionUtils.isEmpty(reqVO.getRuleConditionList())) {
            throw new BusinessException("规则条件不能为空");
        }
        Session session = SecurityContext.currentUser();
        if (reqVO.getId() == null) {
            RobotClientRule clientRule = new RobotClientRule();
            BeanUtils.copyProperties(reqVO, clientRule);
            clientRule.setCreateId((int) session.getId());
            clientRule.setCreateTime(new Date());
            clientRule.setUpdateId((int) session.getId());
            clientRule.setUpdateTime(new Date());
            clientRuleDao.insertSelective(clientRule);
            for (RobotClientRuleCondition item : reqVO.getRuleConditionList()) {
                item.setRuleId(clientRule.getId());
                clientRuleConditionDao.insertSelective(item);
            }
            this.updateSort(clientRule.getId(), 1);
        } else {
            //先删除
            this.deleteByRuleId(reqVO.getId());
            //保存数据
            RobotClientRule update = new RobotClientRule();
            BeanUtils.copyProperties(reqVO, update);
            update.setUpdateId((int) session.getId());
            update.setUpdateTime(new Date());
            clientRuleDao.updateByPrimaryKeySelective(update);
            for (RobotClientRuleCondition item : reqVO.getRuleConditionList()) {
                item.setRuleId(update.getId());
                clientRuleConditionDao.insertSelective(item);
            }
        }
        return 1;
    }

    @Override
    public RobotClientRuleVO getById(Integer id) {
        RobotClientRule clientRule = clientRuleDao.selectByPrimaryKey(id);
        RobotClientRuleVO ruleVO = new RobotClientRuleVO();
        BeanUtils.copyProperties(clientRule, ruleVO);
        Map<Integer, List<RobotClientRuleCondition>> ruleConditionMap = this.getRuleConditionList(Lists.newArrayList(id));
        if (MapUtils.isNotEmpty(ruleConditionMap)) {
            List<RobotClientRuleCondition> conditions = ruleConditionMap.get(id);
            ruleVO.setRuleConditionList(conditions);
        }
        return ruleVO;
    }

    @Override
    public Integer updateSort(Integer id, Integer sort) {
        RobotClientRule clientRule = clientRuleDao.selectByPrimaryKey(id);
        Example example = new Example(RobotClientRule.class);
        example.orderBy("sort").asc();
        example.createCriteria().andEqualTo("clientId", clientRule.getClientId()).andEqualTo("status", 1).andNotEqualTo("id", id);
        List<RobotClientRule> ruleList = clientRuleDao.selectByExample(example);
        if (CollectionUtils.isNotEmpty(ruleList)) {
            List<RobotClientRule> updateList = Lists.newArrayList();
            Integer order = 0;
            for (RobotClientRule rule : ruleList) {
                RobotClientRule update = new RobotClientRule();
                update.setId(rule.getId());
                update.setSort((++order));
                update.setUpdateTime(new Date());
                updateList.add(update);
            }
            clientRuleDao.batchUpdateSort(updateList);
        }

        example = new Example(RobotTaskQueue.class);
        example.orderBy("sort").asc();
        example.createCriteria().andEqualTo("clientId", clientRule.getClientId()).andEqualTo("status", 1);
        List<RobotClientRule> clientRuleList = clientRuleDao.selectByExample(example);
        if (CollectionUtils.isNotEmpty(clientRuleList)) {
            List<RobotClientRule> updateList = Lists.newArrayList();
            for (RobotClientRule queue : clientRuleList) {
                if (queue.getId().intValue() == id.intValue()) {
                    RobotClientRule update = new RobotClientRule();
                    update.setId(queue.getId());
                    update.setSort(sort);
                    update.setUpdateTime(new Date());
                    updateList.add(update);
                }
                if (queue.getId().intValue() != id.intValue() && queue.getSort() >= sort) {
                    RobotClientRule update = new RobotClientRule();
                    update.setId(queue.getId());
                    update.setSort(queue.getSort() + 1);
                    update.setUpdateTime(new Date());
                    updateList.add(update);
                }
            }
            clientRuleDao.batchUpdateSort(updateList);
        }
        return 1;
    }

    private void updateSortEx(Integer clientId, Integer id) {
        Example example = new Example(RobotClientRule.class);
        example.orderBy("sort").asc();
        example.createCriteria().andEqualTo("clientId", clientId).andEqualTo("status", 1).andNotEqualTo("id", id);
        List<RobotClientRule> ruleList = clientRuleDao.selectByExample(example);
        if (CollectionUtils.isNotEmpty(ruleList)) {
            List<RobotClientRule> updateList = Lists.newArrayList();
            Integer order = 0;
            for (RobotClientRule rule : ruleList) {
                RobotClientRule updateRulue = new RobotClientRule();
                updateRulue.setId(rule.getId());
                updateRulue.setSort((++order));
                updateRulue.setUpdateTime(new Date());
                updateList.add(updateRulue);
            }
            clientRuleDao.batchUpdateSort(updateList);
        }
    }

    private void deleteByRuleId(Integer ruleId) {
        Example example = new Example(RobotClientRuleCondition.class);
        example.createCriteria().andEqualTo("ruleId", ruleId);
        clientRuleConditionDao.deleteByExample(example);
    }

    @Override
    public Map<Integer, List<RobotClientRuleCondition>> getRuleConditionList(List<Integer> ruleIds) {
        Example example = new Example(RobotClientRuleCondition.class);
        example.createCriteria().andIn("ruleId", ruleIds);
        List<RobotClientRuleCondition> list = clientRuleConditionDao.selectByExample(example);
        if (CollectionUtils.isEmpty(list)) {
            return Maps.newHashMap();
        }
        return list.stream().collect(Collectors.groupingBy(RobotClientRuleCondition::getRuleId));
    }
}
