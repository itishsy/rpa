package com.seebon.rpa.actions.impl.declare;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.seebon.common.utils.JsonUtils;
import com.seebon.rpa.BusinessException;
import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.NoneTarget;
import com.seebon.rpa.context.annotation.ActionUtils;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.enums.OfferLabelType;
import com.seebon.rpa.entity.agent.dto.openApi.DeclareCounterOfferDTO;
import com.seebon.rpa.entity.robot.RobotOfferRuleItem;
import com.seebon.rpa.entity.robot.vo.RobotOfferRuleVO;
import com.seebon.rpa.remote.RpaDesignService;
import com.seebon.rpa.service.RobotCommonService;
import com.seebon.rpa.utils.Convert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@ActionUtils
@RobotAction(name = "申报回盘", targetType = NoneTarget.class, comment = "申报回盘")
public class DeclareOffer extends AbstractAction {
    @Autowired
    private RobotCommonService robotCommonService;
    @Autowired
    private RpaDesignService rpaDesignService;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final Pattern pattern = Pattern.compile("\\b[a-zA-Z]+\\b(\\s[a-zA-Z]+\\b){4,}");

    @Override
    public void run() {
        try {
            List<DeclareCounterOfferDTO> list = this.getDeclareReturn();
            if (CollectionUtils.isEmpty(list)) {
                log.info("没有申报回盘的数据");
                return;
            }
            List<RobotOfferRuleVO> ruleList = this.getRobotOfferRule();
            List<DeclareCounterOfferDTO> declareOfferList = Lists.newArrayList();
            for (DeclareCounterOfferDTO data : list) {
                if (StringUtils.isNotBlank(data.getFailReason())) {
                    //添加默认回盘规则，失败原因包括特殊英文，自动保持申报中
                    //1、失败原因超过5个英文单词
                    //2、失败原因包含单词：error、exception
                    if (data.getFailReason().toLowerCase().contains("error") || data.getFailReason().toLowerCase().contains("exception")) {
                        data.setDeclareStatus(2);
                    } else {
                        if (this.matcherFailReason(data)) {
                            data.setDeclareStatus(2);
                        }
                    }
                }
                if (data.getDeclareStatus() != 4) {
                    RobotOfferRuleItem ruleItem = Convert.getRuleItem(ruleList, data.getFailReason());
                    this.setFailReason(data, ruleItem);
                }
                if (StringUtils.isNotBlank(data.getFailReason()) && "null".equals(data.getFailReason())) {
                    data.setFailReason(null);
                }
                declareOfferList.add(data);
            }
            if (CollectionUtils.isEmpty(declareOfferList)) {
                log.info("没有需要申报回盘的数据");
                return;
            }
            log.info("申报回盘请求数据.data=" + JSON.toJSONString(declareOfferList));
            String resp = robotCommonService.declareCounterOffer(declareOfferList);
            log.info("申报回盘响应.resp=" + resp);
            if (StringUtils.isBlank(resp)) {
                log.error("申报回盘失败.");
                return;
            }
            JSONObject jsonObject = JSON.parseObject(resp);
            JSONArray array = jsonObject.getJSONArray("data");
            for (int i = 0; i < array.size(); i++) {
                JSONObject jsonData = array.getJSONObject(i);
                Integer outId = jsonData.getInteger("outId");
                String syncFailReason = jsonData.getString("syncFailReason");
                Integer counterOfferStatus = jsonData.getInteger("counterOfferStatus");
                if (counterOfferStatus == 1) {
                    // 更新报盘状态
                    this.updateSuccess(outId);
                } else {
                    // 更新报盘状态
                    this.updateFail(syncFailReason, outId);
                }
            }
            log.info("申报回盘执行完成");
        } catch (Exception e) {
            log.error("申报回盘异常." + e.getMessage(), e);
            throw new BusinessException("申报回盘异常." + e.getMessage());
        }
    }

    private List<RobotOfferRuleVO> getRobotOfferRule() {
        RobotOfferRuleVO ruleVO = new RobotOfferRuleVO();
        ruleVO.setAddrName(ctx.get("addrName"));
        ruleVO.setBusinessType(ctx.get("businessTypeInt"));
        ruleVO.setDeclareType(ctx.get("declareType"));
        ruleVO.setDeclareWebsite(ctx.get("tplTypeCode"));
        log.info("获取规则,ruleVO={}", JSON.toJSONString(ruleVO));
        return rpaDesignService.listOfferRule(ruleVO);
    }

    private void setFailReason(DeclareCounterOfferDTO data, RobotOfferRuleItem ruleItem) {
        if (ruleItem == null) {
            return;
        }
        if (ruleItem.getReplaceType() == 2) {//2:申报中信息
            data.setFailReason(null);
            data.setKeepLastFailReason(1);
        } else if (StringUtils.isNotBlank(ruleItem.getReplaceContent())) {
             if (ruleItem.getReplaceType() == 3) {//3：自定义
                data.setFailReason(ruleItem.getReplaceContent());
            } else if (ruleItem.getReplaceType() == 4) {//4：拼接前缀
                data.setFailReason(ruleItem.getReplaceContent() + " " + data.getFailReason());
            } else if (ruleItem.getReplaceType() == 5) {//5：追加
                data.setFailReason(data.getFailReason() + " " + ruleItem.getReplaceContent());
            }
        }
        if (StringUtils.isNotBlank(ruleItem.getNextNode())) {
            if (data.getBusinessType() == 1 || OfferLabelType.check.getCode().equals(ruleItem.getNextNode())) {
                data.setOperationType(ruleItem.getNextNode());
            }
            data.setNodeComment(OfferLabelType.getName(ruleItem.getNextNode()));
            data.setDeclareStatus(2);
        } else {
            data.setDeclareStatus(ruleItem.getDeclareStatus());
        }
    }

    private void updateSuccess(Integer outId) {
        String sql = "update robot_execution_data set sync=1,syncTime=now() WHERE id= ?";
        jdbcTemplate.update(sql, outId);
    }

    private void updateFail(String syncFailReason, Integer outId) {
        String sql = "update robot_execution_data set sync=1,syncFailReason=?,syncTime=now() WHERE id= ?";
        jdbcTemplate.update(sql, syncFailReason, outId);
    }

    public List<DeclareCounterOfferDTO> getDeclareReturn() {
        String addrName = ctx.get("addrName");
        String accountNumber = ctx.getAccountNumber();
        String tplTypeCode = ctx.get("tplTypeCode");
        Integer businessType = Convert.getBusinessType(ctx.get("businessType"));
        Integer declareType = ctx.get("declareType");
        String sql = "SELECT id as outId,addrName,businessType,declareType,accountNumber,idCard,declareMonth,tplTypeCode,declareStatus,robotExecStatus,failReason,failType,dataType,operationType,nodeComment,execution_code as robotExecCode FROM robot_execution_data  " +
                "WHERE sync = 0 and robotExecStatus = 2 and declareStatus in (2,4,5) and addrName =? and accountNumber =? and tplTypeCode=? and businessType=? and declareType=?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(DeclareCounterOfferDTO.class), addrName, accountNumber, tplTypeCode, businessType, declareType);
    }

    private boolean matcherFailReason(DeclareCounterOfferDTO data) {
        List<Map> maps = JsonUtils.strToList(data.getFailReason(), Map.class);
        if (maps!=null) {
            return false;
        }

        log.info("匹配失败原因:" + data.getFailReason());
        Matcher matcher = pattern.matcher(data.getFailReason());
        boolean flag = matcher.find();
        log.info("失败原因超过5个英文单词：flag=" + flag);
        return flag;
    }
}
