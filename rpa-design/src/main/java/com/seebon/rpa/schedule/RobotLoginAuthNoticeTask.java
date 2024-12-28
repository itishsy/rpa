package com.seebon.rpa.schedule;

import cn.hutool.core.date.TimeInterval;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.seebon.common.utils.DateUtil;
import com.seebon.rpa.entity.agent.enums.TplTypeEnum;
import com.seebon.rpa.entity.auth.po.SysUser;
import com.seebon.rpa.entity.robot.RobotTaskQueue;
import com.seebon.rpa.entity.robot.enums.LoginAuthProcessStatusEnum;
import com.seebon.rpa.entity.robot.vo.RobotLoginAuthVO;
import com.seebon.rpa.entity.saas.po.message.MessageList;
import com.seebon.rpa.entity.saas.vo.message.MessageRuleConfigVO;
import com.seebon.rpa.entity.saas.vo.system.SysUserDeclareAccountVO;
import com.seebon.rpa.remote.RpaAuthService;
import com.seebon.rpa.remote.RpaSaasService;
import com.seebon.rpa.repository.mysql.RobotClientDao;
import com.seebon.rpa.repository.mysql.RobotLoginAuthDao;
import com.seebon.rpa.service.RobotTaskQueueService;
import com.seebon.rpa.utils.EncryptUtil;
import com.seebon.rpa.utils.SmsUtil;
import com.seebon.rpa.utils.WorkdayUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 登录认证-待登录通知
*/

@Component
@Slf4j
public class RobotLoginAuthNoticeTask {


    @Autowired
    private RobotClientDao robotClientDao;
    @Autowired
    private RobotLoginAuthDao robotLoginAuthDao;

    @Autowired
    private RpaSaasService rpaSaasService;
    @Autowired
    private RpaAuthService rpaAuthService;
    @Autowired
    private RobotTaskQueueService robotTaskQueueService;



    @Value("${task.login.userId}")
    private Integer userId;
    @Value("${task.login.userName}")
    private String userName;
    @Value("${task.login.custId}")
    private Integer custId;

    @Autowired
    private SimpleLogin simpleLogin;


    @Value("${h5.page.base.url:http://172.172.4.221:8001/api/h5/}")
    private String baseShotUrl;


    //    @Scheduled(cron = "0 0 10,14,16,18 * * ?")
    public void notifyLoginRobot() {
        simpleLogin.doLogin(userId, userName, custId);
        try {
            TimeInterval timer = cn.hutool.core.date.DateUtil.timer();
            log.info("开始-执行发送登录短信通知提醒.");
            run();
            log.info("完成-执行发送登录短信通知提醒,耗时：" + timer.intervalSecond() + " 秒.");
        } finally {
            simpleLogin.loginOut();
        }

    }


    public void run(){

        // 判断是否工作日
        boolean workDay = WorkdayUtils.isWorkDay_Appworlds(DateUtil.formatYMD(new Date()));
        if (!workDay) {
            return ;
        }
        /**
         * 当需要H5登录认证时，且申报账户未登录时，需要通过短信、企微的方式通知申报账户的负责人，
         * 通知时间节点为工作日的【10：00】，【14：00】，【16：00】，【18:00】
         * 通知内容：你好，社保机器人将要执行任务且需要手机认证登录，点击链接https：//xxxx.com/？=333，进行操作，请关注。
        */

        // 查询待登录列表数据
        List<RobotLoginAuthVO> robotLoginAuthVOS = getPushDate();

        // 已推送手机号
        HashMap<String, Object> pushPersonMap = Maps.newHashMap();

        if (CollectionUtils.isNotEmpty(robotLoginAuthVOS)) {
            // 获取所有账号信息
            List<SysUser> allCustUser = rpaAuthService.getAllCustUser();
            for (SysUser sysUser : allCustUser) {

                // 判断负责人是否已推送过
                if (StringUtils.isEmpty(sysUser.getPhoneNumber()) || pushPersonMap.containsKey(sysUser.getPhoneNumber())) {
                    continue;
                }

                // 查找账号权限
                List<SysUserDeclareAccountVO> userSocialDeclareAccountVOList = rpaSaasService.getUserDeclareAccountList(sysUser.getId(),1);
                List<SysUserDeclareAccountVO> userAccfundDeclareAccountVOList = rpaSaasService.getUserDeclareAccountList(sysUser.getId(),2);

                // 查询符合人员条件的待推送登录信息
                RobotLoginAuthVO filterLoginAuthVo = robotLoginAuthVOS.parallelStream().filter(robotLoginAuthVO -> {

                    // 地区id
                    Integer addrId = robotLoginAuthVO.getAddrId();
                    // 申报账户
                    String accountNumber = robotLoginAuthVO.getAccountNumber();

                    // 社保/公积金权限
                    List<SysUserDeclareAccountVO> userDeclareAccountVOList = robotLoginAuthVO.getBusinessTypeName().equals("社保") ? userSocialDeclareAccountVOList : userAccfundDeclareAccountVOList;
                    // 匹配账号的地区和申报账户权限
                    Optional<SysUserDeclareAccountVO> first = userDeclareAccountVOList.stream().filter(it ->
                            it.getAddrId() != null && it.getAddrId().equals(addrId)
                                    && (accountNumber != null && accountNumber.equals(it.getAccountNumber()))
                    ).findFirst();
                    // 判断是否有空闲设备
                    RobotTaskQueue robotTaskQueue = new RobotTaskQueue();
                    robotTaskQueue.setClientId(robotLoginAuthVO.getClientId());
                    robotTaskQueue.setAppCode(robotLoginAuthVO.getAppCode());
                    robotTaskQueue.setTaskCode(robotLoginAuthVO.getTaskCode());
                    robotTaskQueue.setDeclareSystem(robotLoginAuthVO.getDeclareSystem());
                    robotTaskQueue.setQueueItem(robotLoginAuthVO.getQueueItem());
                    robotTaskQueue.setSortRule(1); //当前默认为H5登录
                    String machineCode = robotTaskQueueService.getMachineCode(robotTaskQueue);
                    List<String> machineCodeList = Lists.newArrayList();
                    if (StringUtils.isNotEmpty(machineCode)) {
                        machineCodeList = Arrays.asList(machineCode.split(","));
                    }
                    Integer countFreeRobotClient = robotClientDao.countFreeRobotClient(robotLoginAuthVO.getClientId(),machineCodeList);
                    if (countFreeRobotClient == 0) {
                        log.info("不存在空闲设备，不进行提醒:{}",robotLoginAuthVO.toString());
                        return false;
                    }
                    return first.isPresent();
                }).findFirst().orElse(null);

                // 判断用户是否有地区需推送
                if (filterLoginAuthVo != null) {

                    // 查询推送消息规则
                    MessageRuleConfigVO messageRule = rpaSaasService.getMessageRule("10026002", "10027014");

                    // 推送并登录提醒 ，模版：你好，社保机器人将要执行任务且需要手机认证登录，点击链接https：//xxxx.com/？=333，进行操作，请关注。
                    sendMessage(messageRule, sysUser, filterLoginAuthVo.getPhoneNumber());

                    // 添加到已推送手机号，本次将不再对该手机号推送
                    pushPersonMap.put(sysUser.getPhoneNumber(),sysUser);

                } else {
                    log.info(String.format("客户用户{%s}无登录消息需推送！", sysUser.getUsername()));
                }
            }

        } else {
            log.info("无未登录的集中登录数据");
        }
    }

    /**
     * 查询未登录列表数据
    */

    private List<RobotLoginAuthVO> getPushDate(){
        HashMap<String, Object> params = Maps.newHashMap();
        params.put("processStatus", LoginAuthProcessStatusEnum.NO_LOGIN.getStatus());
        List<RobotLoginAuthVO> robotLoginAuthVOS = robotLoginAuthDao.selectVOByParams(params);

        // 处理待登录完整信息
        robotLoginAuthVOS.parallelStream().forEach(la->{
            JSONObject appArgs = JSONObject.parseObject(la.getAppArgs());
            la.setAddrName(appArgs.getString("addrName"));
            String businessType = appArgs.getString("businessType");
            if ("1001001".equals(businessType)) {
                la.setBusinessTypeName("社保");
            } else if ("1001002".equals(businessType)) {
                la.setBusinessTypeName("公积金");
            }
            la.setAddrId(appArgs.getIntValue("addrId"));
            la.setDeclareSystemName(TplTypeEnum.getNameByCode(la.getDeclareSystem()));
        });
        return robotLoginAuthVOS;
    }

    /**
     * 推送邮件、短息消息
     * @param messageRule
     * @param user
    */

    private void sendMessage(MessageRuleConfigVO messageRule, SysUser user, String phoneNumber) {

        if (StringUtils.isBlank(user.getPhoneNumber()) || StringUtils.isNotBlank(messageRule.getSmsContent())) {
            log.info("缺少参数，不进行消息发送：user:{} --- messageRule:{}",user.toString(),messageRule.toString());
            return ;
        }

        String theme = messageRule.getMessageTopic(); // 主题

        MessageList ml = new MessageList();
        try {
            String smsContent = String.format(messageRule.getMessageTopic(),baseShotUrl + "?pageType=list&loginPhone=" + EncryptUtil.aesEncrypt(phoneNumber));
            ml.setCustId(user.getCustId());
            ml.setUserId(user.getId());
            ml.setEmpName(user.getName());
            ml.setCreateTime(new Date());
            ml.setMessageRuleId(messageRule.getId());
            ml.setMessageType(messageRule.getMessageType());
            ml.setWarnType(messageRule.getWarnType());
            ml.setStatus(0);
            ml.setTheme(theme);
            ml.setEmail(user.getEmail());

            ml.setPhoneNumber(user.getPhoneNumber());
            ml.setSmsContent(smsContent);
            ml.setWxContent(smsContent);
            SmsUtil.send(user.getPhoneNumber(), smsContent.replace("&","&amp;"));
            ml.setSmsStatus(1);
        }catch (Exception e) {
            ml.setSmsStatus(2);
            ml.setSmsFailReason("短信消息发送失败！");
            log.error("登录验证通知发送短信异常：",e);
        }

//        try {
//            wxSendMsg(phoneNumber, smsContent);
//            ml.setWxStatus(1);
//        }catch (Exception e) {
//            ml.setWxStatus(2);
//            ml.setWxFailReason("短信消息发送失败！");
//        }

        rpaSaasService.saveMessage(ml);
    }

    /**
     * 发送企业微信
    */
//
//    private void wxSendMsg(String phoneNumber,String msgContent){
//        List<SysWechatOfficialAccountSetting> sysWechatOfficialAccountSettings = wechatOfficialAccountSettingDao.selectAll();
//        SysWechatOfficialAccountSetting sysWechatOfficialAccountSetting = new SysWechatOfficialAccountSetting();
//        sysWechatOfficialAccountSetting.setServiceType(1);
//        sysWechatOfficialAccountSetting.setSwitchType(2);
//        if (CollectionUtils.isNotEmpty(sysWechatOfficialAccountSettings)) {
//            sysWechatOfficialAccountSetting = sysWechatOfficialAccountSettings.get(0);
//        }
//
//        // 企微消息
//        wxUserService.sendCompanyWechatMessage(sysWechatOfficialAccountSetting,phoneNumber,msgContent);
//    }



}
