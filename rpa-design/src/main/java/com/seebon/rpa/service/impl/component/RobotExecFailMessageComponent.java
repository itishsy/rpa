package com.seebon.rpa.service.impl.component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.seebon.rpa.entity.agent.enums.BusinessTypeEnum;
import com.seebon.rpa.entity.agent.enums.TplTypeEnum;
import com.seebon.rpa.entity.auth.po.SysUser;
import com.seebon.rpa.entity.robot.RobotApp;
import com.seebon.rpa.entity.robot.RobotFlow;
import com.seebon.rpa.entity.robot.RobotTask;
import com.seebon.rpa.entity.robot.RobotTaskArgs;
import com.seebon.rpa.entity.robot.dto.design.RobotExecutionMo;
import com.seebon.rpa.entity.saas.po.message.MessageList;
import com.seebon.rpa.entity.saas.po.message.MessageRulePersonnel;
import com.seebon.rpa.entity.saas.po.message.MessageUserReceiveSetting;
import com.seebon.rpa.entity.saas.vo.message.MessageRuleConfigVO;
import com.seebon.rpa.entity.saas.vo.message.MessageRuleCustomerVO;
import com.seebon.rpa.entity.saas.vo.message.MessageRuleOperatorVO;
import com.seebon.rpa.entity.saas.vo.system.SysUserDeclareAccountVO;
import com.seebon.rpa.remote.RpaAuthService;
import com.seebon.rpa.remote.RpaSaasService;
import com.seebon.rpa.repository.mysql.RobotAppDao;
import com.seebon.rpa.repository.mysql.RobotFlowDao;
import com.seebon.rpa.repository.mysql.RobotTaskArgsDao;
import com.seebon.rpa.repository.mysql.RobotTaskDao;
import com.seebon.rpa.utils.EmailUtil;
import com.seebon.rpa.utils.SmsUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class RobotExecFailMessageComponent {

    @Autowired
    private RobotFlowDao robotFlowDao;

    @Autowired
    private RobotTaskArgsDao robotTaskArgsDao;

    @Autowired
    private RobotTaskDao robotTaskDao;

    @Autowired
    private RobotAppDao robotAppDao;

    @Autowired
    private RpaSaasService rpaSaasService;

    @Autowired
    private RpaAuthService rpaAuthService;

    private final String loginFlowTagCode = "10018008"; // 登录流程编码


    public void sendWarnMessage(RobotExecutionMo executionMo) {

        String appCode = executionMo.getAppCode();
        if (StringUtils.isBlank(appCode)) {
            log.info("参数appCode为空，获取不到rpa应用信息，略过消息推送！");
            return;
        }

        Example appExample = new Example(RobotApp.class);
        appExample.createCriteria().andEqualTo("appCode", executionMo.getAppCode());
        RobotApp robotApp = robotAppDao.selectOneByExample(appExample);
        if (robotApp == null || StringUtils.isBlank(robotApp.getRobotCode()) || !"declare".equals(robotApp.getRobotCode())) {
            log.info(robotApp==null?"获取不到rpa应用信息，略过消息推送！":"当前应用非申报应用，略过消息推送！");
            return;
        }

        String flowCode = executionMo.getFlowCode(); // 流程编码
        Example example = new Example(RobotFlow.class);
        example.createCriteria().andEqualTo("flowCode", flowCode);

        RobotFlow robotFlow = robotFlowDao.selectOneByExample(example);
        String tagCode = robotFlow.getTagCode();
        MessageRuleConfigVO messageRule = null;
        if (StringUtils.isNotBlank(tagCode) && loginFlowTagCode.equals(tagCode)) { // 登录流程异常
            messageRule = rpaSaasService.getMessageRule("10026002", "10027008");
        } else { // rpa执行异常
            messageRule = rpaSaasService.getMessageRule("10026001", "10027006");
        }
        if (validateRule(messageRule)) {
            Example robotTaskExample = new Example(RobotTaskArgs.class);
            String[] stringList = {"socialNumber", "accfundNumber"};
            robotTaskExample.createCriteria().andIn("argsKey", Arrays.asList(stringList)).andEqualTo("taskCode", executionMo.getTaskCode());
            List<RobotTaskArgs> robotTaskArgsList = robotTaskArgsDao.selectByExample(robotTaskExample);

            String accountNumber = "";
            if (CollectionUtils.isNotEmpty(robotTaskArgsList)) {
                accountNumber = robotTaskArgsList.get(0).getArgsValue();
            }

            Example companyExample = new Example(RobotTaskArgs.class);
            companyExample.createCriteria().andEqualTo("argsKey", "companyName").andEqualTo("taskCode", executionMo.getTaskCode());
            List<RobotTaskArgs> companyList = robotTaskArgsDao.selectByExample(companyExample);
            String orgName = "";
            if (CollectionUtils.isNotEmpty(companyList)) {
                orgName = companyList.get(0).getArgsValue();
            }

            sendToPersonnel(messageRule, executionMo, orgName, accountNumber, robotFlow);

            sendToOperator(messageRule, executionMo, orgName, accountNumber, robotFlow);

            String taskCode = executionMo.getTaskCode();
            Example e = new Example(RobotTask.class);
            e.createCriteria().andEqualTo("taskCode", taskCode);
            RobotTask robotTask = robotTaskDao.selectOneByExample(e);

            if (robotTask != null) {
                Integer clientId = robotTask.getClientId(); // 客户id
                sendToCust(messageRule, executionMo, orgName, accountNumber, clientId, robotFlow);
            }

        }
    }

    /**
     * 推送消息给指定人员
     * @param messageRule
     * @param executionMo
     * @param orgName
     * @param accountNumber
     */
    private void sendToPersonnel(MessageRuleConfigVO messageRule, RobotExecutionMo executionMo,
                                 String orgName, String accountNumber, RobotFlow robotFlow) {
        Integer sendPersonnel = messageRule.getSendPersonnel();
        List<MessageRulePersonnel> personnelList = messageRule.getPersonnelList();
        if (sendPersonnel == 1 && CollectionUtils.isNotEmpty(personnelList)) {
            personnelList.stream().map(personnel -> {
                SysUser user = new SysUser();
                user.setName(personnel.getName());
                user.setUsername(personnel.getName());
                user.setEmail(personnel.getEmail());
                user.setPhoneNumber(personnel.getPhoneNumber());
                return user;
            }).forEach(user -> {
                sendMessage(messageRule, executionMo, orgName, accountNumber, user, 1, 1, "指定人员", robotFlow);
            });
        } else {
            log.info("消息策略未设置指定人员接收对象，略过推送给指定人员！");
        }
    }

    /**
     * 推送消息给运营人员
     * @param messageRule
     * @param executionMo
     * @param orgName
     * @param accountNumber
     * @param robotFlow
     */
    private void sendToOperator(MessageRuleConfigVO messageRule, RobotExecutionMo executionMo,
                                String orgName, String accountNumber, RobotFlow robotFlow) {
        List<MessageRuleOperatorVO> operatorList = messageRule.getOperatorList();
        Integer sendOperator = messageRule.getSendOperator();
        if (sendOperator == 1) {
            if (CollectionUtils.isNotEmpty(operatorList)) {
                List<Integer> roleIds = operatorList.stream().map(it -> it.getRoleId()).collect(Collectors.toList());
                List<SysUser> adminUsers = rpaAuthService.getOperatorUserByRoleIds(roleIds);
                if (CollectionUtils.isNotEmpty(adminUsers)) {

                    String appCode = executionMo.getAppCode();
                    Example e = new Example(RobotApp.class);
                    e.createCriteria().andEqualTo("appCode", appCode);
                    RobotApp robotApp = robotAppDao.selectOneByExample(e);

                    if (robotApp!= null) {
                        String appArgs = robotApp.getAppArgs();
                        Integer businessType = BusinessTypeEnum.Social.getCode();
                        JSONObject jsonObject = JSON.parseObject(appArgs);
                        String businessTypeStr = jsonObject.getString("businessType");
                        String addrName = jsonObject.getString("addrName");
                        if ("1001002".equals(businessTypeStr)) {
                            businessType = BusinessTypeEnum.Accfund.getCode();
                        }
                        Integer bt = businessType;
                        adminUsers.stream().filter(user -> user.getStatus()!= null && user.getStatus() == 1).forEach(user -> {
                            List<SysUserDeclareAccountVO> accountVOList = rpaSaasService.getUserDeclareAccountList(user.getId(), bt);
                            if (CollectionUtils.isNotEmpty(accountVOList) && accountVOList.stream().filter(it -> StringUtils.isNotBlank(it.getAddrName()) &&
                                    it.getAddrName().equals(addrName) && it.getBusinessType()!=null
                                    && bt.equals(it.getBusinessType())).findFirst().isPresent()) {
                                MessageUserReceiveSetting userReceiveSetting = rpaSaasService.getUserReceiveSetting(user.getId(), messageRule.getMessageType(), messageRule.getWarnType());
                                if (userReceiveSetting == null) {
                                    sendMessage(messageRule, executionMo, orgName, accountNumber, user, 1, 1, "运营人员", robotFlow);
                                } else {
                                    sendMessage(messageRule, executionMo, orgName, accountNumber, user, userReceiveSetting.getEmailWay(), userReceiveSetting.getSmsWay(), "运营人员", robotFlow);
                                }
                                
                            } else {
                                log.info(String.format("运营用户{%s}未分配数据权限，略过消息推送！", user.getUsername()));
                            }
                        });
                    }


                }
            } else {
                log.info("消息策略未设置运营人员接收对象，略过向运营人员消息推送！");
            }
        } else {
            log.info("消息策略设置不推送消息给运营人员，略过向运营人员消息推送！");
        }
    }

    /**
     * 推送消息给客户人员
     * @param messageRule
     * @param executionMo
     * @param orgName
     * @param accountNumber
     * @param clientId
     * @param robotFlow
     */
    private void sendToCust(MessageRuleConfigVO messageRule, RobotExecutionMo executionMo, String orgName,
                            String accountNumber, Integer clientId, RobotFlow robotFlow) {
        Integer sendCust = messageRule.getSendCust();
        String error = StringUtils.isBlank(executionMo.getError())?"未知":executionMo.getError();
        List<String> custError = Lists.newArrayList("用户未及时验证", "登录信息有误", "uKey异常");
        if (sendCust == 1 ) {
            if (messageRule.getWarnType().equals("10027008") && !custError.stream().filter(it -> error.contains(it)).findFirst().isPresent()) {
                return;
            }
            List<SysUser> allCustUser = rpaAuthService.getAllCustUser();
            List<SysUser> users = allCustUser.stream().filter(user -> user.getCustId().equals(clientId)).collect(Collectors.toList());
            List<MessageRuleCustomerVO> custList = messageRule.getCustList();
            if (CollectionUtils.isNotEmpty(custList)) {
                Set<Integer> custIds = custList.stream().map(it -> it.getCustId()).collect(Collectors.toSet());
                users = users.stream().filter(user -> custIds.contains(user.getCustId())).collect(Collectors.toList());
            }
            if (CollectionUtils.isNotEmpty(users)) {

                String appCode = executionMo.getAppCode();
                Example e = new Example(RobotApp.class);
                e.createCriteria().andEqualTo("appCode", appCode);
                RobotApp robotApp = robotAppDao.selectOneByExample(e);

                String appArgs = robotApp.getAppArgs();
                Integer businessType = BusinessTypeEnum.Social.getCode();
                JSONObject jsonObject = JSON.parseObject(appArgs);
                String businessTypeStr = jsonObject.getString("businessType");
                String addrName = jsonObject.getString("addrName");
                if ("1001002".equals(businessTypeStr)) {
                    businessType = BusinessTypeEnum.Accfund.getCode();
                }
                Integer bt = businessType;
                for (SysUser user : users) {
                    List<SysUserDeclareAccountVO> accountVOList = rpaSaasService.getUserDeclareAccountList(user.getId(), bt);
                    if (CollectionUtils.isNotEmpty(accountVOList) && accountVOList.stream().filter(it -> StringUtils.isNotBlank(it.getAddrName()) &&
                            it.getAddrName().equals(addrName) && it.getBusinessType()!=null
                            && bt.equals(it.getBusinessType())).findFirst().isPresent()) {
                        MessageUserReceiveSetting userReceiveSetting = rpaSaasService.getUserReceiveSetting(user.getId(), messageRule.getMessageType(), messageRule.getWarnType());
                        if (userReceiveSetting == null) {
                            sendMessage(messageRule, executionMo, orgName, accountNumber, user, 1,
                                    1, "客户人员", robotFlow);
                        } else {
                            sendMessage(messageRule, executionMo, orgName, accountNumber, user, userReceiveSetting.getEmailWay(),
                                    userReceiveSetting.getSmsWay(), "客户人员", robotFlow);
                        }
                    } else {
                        log.info(String.format("客户用户{%s}未分配数据权限，略过消息推送！", user.getUsername()));
                    }
                }
            }
        }
    }


    /**
     * 推送消息
     * @param messageRule
     * @param executionMo
     * @param orgName
     * @param accountNumber
     * @param user
     * @param userEmailWay
     * @param userSmsWay
     * @param userType
     * @param robotFlow
     */
    private void sendMessage(MessageRuleConfigVO messageRule, RobotExecutionMo executionMo, String orgName, String accountNumber, SysUser user,
                             Integer userEmailWay, Integer userSmsWay, String userType, RobotFlow robotFlow) {
        Integer emailWay = messageRule.getEmailWay(); // 是否推送邮件
        Integer smsWay = messageRule.getSmsWay(); // 是否推送短信

        String theme = messageRule.getMessageTopic(); // 主题
        theme = theme.replace("$申报单位$", orgName).replace("$申报账号$", accountNumber)
                .replace("$关联标记$", TplTypeEnum.getNameByCode(robotFlow.getDeclareSystem()))
                .replace("$主流程名称$", robotFlow.getFlowName());

        String emailContent = messageRule.getEmailContent(); // 邮件内容
        emailContent = emailContent.replace("$申报单位$", orgName).replace("$申报账号$", accountNumber)
                .replace("$关联标记$", TplTypeEnum.getNameByCode(robotFlow.getDeclareSystem()))
                .replace("$主流程名称$", robotFlow.getFlowName())
                .replace("$异常原因$", StringUtils.isBlank(executionMo.getError())?"未知":executionMo.getError());

        String smsContent = messageRule.getSmsContent();
        smsContent = smsContent.replace("$申报单位$", orgName).replace("$申报账号$", accountNumber)
                .replace("$关联标记$", TplTypeEnum.getNameByCode(robotFlow.getDeclareSystem()))
                .replace("$主流程名称$", robotFlow.getFlowName());

        MessageList ml = new MessageList();
        ml.setCustId(user.getCustId());
        ml.setUserId(user.getId());
        ml.setEmpName(user.getName());
        ml.setCreateTime(new Date());
        ml.setMessageRuleId(messageRule.getId());
        ml.setMessageType(messageRule.getMessageType());
        ml.setWarnType(messageRule.getWarnType());
        ml.setStatus(0);
        ml.setTheme(theme);
        ml.setEmailContent(emailContent);
        ml.setEmail(user.getEmail());
        if (emailWay == 1) {
            if (userEmailWay == 1) {
                String email = user.getEmail();
                if (StringUtils.isNotBlank(email)) {
                    ml.setEmail(email);
                    Map<String, Object> emailMap = Maps.newHashMap();
                    emailMap.put("receiverEmail", user.spiltEmail());
                    emailMap.put("subject", theme);
                    emailMap.put("mail_content", ml.getEmailContent());
                    try {
                        EmailUtil.sendEmailAttachRetiree(emailMap);
                        ml.setEmailStatus(1);
                    } catch (Exception e) {
                        log.error("邮件发送异常." + e.getMessage(), e);
                        ml.setEmailStatus(2);
                        ml.setEmailFailReason("邮件发送异常！");
                    }
                } else {
                    ml.setEmailStatus(2);
                    ml.setEmailFailReason(String.format("%s邮箱地址为空，发送邮件失败", userType));
                }
            } else {
                ml.setEmailStatus(2);
                ml.setEmailFailReason(String.format("%s设置不接受邮件，略过邮件推送", userType));
            }
        } else {
            ml.setEmailStatus(2);
            ml.setEmailFailReason("策略设置不发送邮件，略过邮件推送");
        }
        String phoneNumber = user.getPhoneNumber();
        ml.setPhoneNumber(phoneNumber);
        ml.setSmsContent(smsContent);
        if (smsWay == 1) {
            if (userSmsWay == 1) {
                if (StringUtils.isNotBlank(phoneNumber)) {
                    try {
                        SmsUtil.send(phoneNumber, smsContent);
                        ml.setSmsStatus(1);
                    }catch (Exception e) {
                        ml.setSmsStatus(2);
                        ml.setSmsFailReason("短信消息发送失败！");
                    }
                } else {
                    ml.setSmsStatus(2);
                    ml.setSmsFailReason(String.format("%s手机号码为空，发送短信失败", userType));
                }
            } else {
                ml.setSmsStatus(2);
                ml.setSmsFailReason(String.format("%s设置不接受短信，略过短信推送", userType));
            }
        } else {
            ml.setSmsStatus(2);
            ml.setSmsFailReason("策略设置不发送短信，略过短信推送");
        }
        rpaSaasService.saveMessage(ml);
    }

    /**
     * 校验消息策略规则
     * @param messageRule
     * @return
     */
    private boolean validateRule(MessageRuleConfigVO messageRule) {
        boolean flag = false;
        if (messageRule != null && messageRule.getStatus() == 1) {
            Integer sendCust = messageRule.getSendCust(); // 是否发送客户，1：是，0：否
            Integer sendOperator = messageRule.getSendOperator(); // 是否发送运营，1：是，0：否
            Integer sendPersonnel = messageRule.getSendPersonnel();// 是否发送指定人员，1：是，0：否
            List<MessageRulePersonnel> personnelList = messageRule.getPersonnelList();
            if (sendCust ==1 || sendOperator == 1 || (sendPersonnel == 1 && CollectionUtils.isNotEmpty(personnelList))) {
                flag = true;
            } else {
                log.info("消息策略未设置消息接收对象，略过消息推送！");
            }
        }else {
            if(messageRule == null) {
                log.info("未设置参保结果异常消息推送策略！");
            } else if (messageRule.getStatus() != 1) {
                log.info("参保结果异常消息推送策略已被禁用！");
            }
        }
        return flag;
    }
}
