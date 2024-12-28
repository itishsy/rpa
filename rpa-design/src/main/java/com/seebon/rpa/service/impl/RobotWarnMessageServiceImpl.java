package com.seebon.rpa.service.impl;

import cn.hutool.core.util.StrUtil;
import com.seebon.rpa.BusinessException;
import com.seebon.rpa.entity.IgGridDefaultPage;
import com.seebon.rpa.entity.robot.dto.design.OperationRequestVo;
import com.seebon.rpa.entity.robot.enums.WarnStatus;
import com.seebon.rpa.entity.robot.po.design.RobotOperationMessageConfig;
import com.seebon.rpa.entity.robot.po.design.RobotWarnPerson;
import com.seebon.rpa.repository.mysql.RobotWarnMessageDao;
import com.seebon.rpa.repository.mysql.RobotWarnPersonDao;
import com.seebon.rpa.service.OperationMonitorService;
import com.seebon.rpa.service.RobotWarnMessageService;
import com.seebon.rpa.utils.EmailUtil;
import com.seebon.rpa.utils.SmsUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO
 *
 * @author zjf
 * @describe 消息业务实现类
 * @date 2023/4/21 10:20
 */
@Slf4j
@Service
public class RobotWarnMessageServiceImpl implements RobotWarnMessageService{

    @Autowired
    private RobotWarnMessageDao robotWarnMessageDao;

    @Autowired
    private RobotWarnPersonDao robotWarnPersonDao;

    @Autowired
    private OperationMonitorService operationMonitorService;

    @Override
    public IgGridDefaultPage<RobotOperationMessageConfig> listPage(Map<String,Object> map){
        log.info("查询消息列表入参{}",map);
        List<RobotOperationMessageConfig> byMessages=robotWarnMessageDao.findByMessage(map);
        int records=byMessages.size();
        return new IgGridDefaultPage<>(byMessages,records);

    }

    @Override
    public void insertOrUpdateMessage(RobotOperationMessageConfig vo){
        log.info("消息新增或修改入参{}",vo);
        Integer id=vo.getId();
        //新增消息
        if(id==null){
            Example example=new Example(RobotOperationMessageConfig.class);
            example.createCriteria().andEqualTo("warnType",vo.getWarnType());
            int count=robotWarnMessageDao.selectCountByExample(example);
            if(count>0){
                throw new BusinessException(String.format("预警类型%s已存在，不能再新增",vo.getWarnName()));
            }
            vo.setCreateTime(new Date());
            vo.setSmsSendStatus(0);
            vo.setEmailSendStatus(0);
            robotWarnMessageDao.insert(vo);

        }else{
            //更新消息
            Example example=new Example(RobotOperationMessageConfig.class);
            example.createCriteria().andEqualTo("id",vo.getId());
            vo.setUpdateTime(new Date());
            robotWarnMessageDao.updateByExampleSelective(vo,example);

        }
    }

    @Override
    public void updateStatus(RobotOperationMessageConfig vo){
        Example example=new Example(RobotOperationMessageConfig.class);
        example.createCriteria().andEqualTo("id",vo.getId());
        robotWarnMessageDao.updateByExampleSelective(vo,example);
    }

    @Override
    public void sendMsg(RobotOperationMessageConfig robotOperationMessageConfig){
        if(robotOperationMessageConfig==null){
            return;
        }
        List<RobotWarnPerson> personInfoList=robotWarnPersonDao.getPersonInfo(robotOperationMessageConfig.getPersonTypeId());
        String sendWay=robotOperationMessageConfig.getSendWay();
        try{
            if(StringUtils.isNotBlank(sendWay)){
                String[] split=StrUtil.split(sendWay,",");
                for(String way: split){
                    if(way.equals("1")){
                        for(RobotWarnPerson robotWarnPerson: personInfoList){
                            if(StringUtils.isNotBlank(robotWarnPerson.getPhoneNumber())){
                                log.info("开始发送短信给{"+robotWarnPerson.getPhoneNumber()+"}......");
                                SmsUtil.send(robotWarnPerson.getPhoneNumber(),robotOperationMessageConfig.getSmsContent());
                                log.info("成功结束发短信给{"+robotWarnPerson.getPhoneNumber()+"}......");
                            }else{
                                log.info("接收短信的手机号码为空，不执行短信发送");
                            }
                        }
                    }
                    if(way.equals("0")){
                        for(RobotWarnPerson robotWarnPerson: personInfoList){
                            if(StringUtils.isNotBlank(robotWarnPerson.getEmail())){
                                Map<String,Object> emailMap=new HashMap<String,Object>();
                                emailMap.put("subject",robotOperationMessageConfig.getWarnName()+"提醒");
                                emailMap.put("tplName","mailBaseTemplate");
                                emailMap.put("mail_content",robotOperationMessageConfig.getEmailContent());
                                emailMap.put("receiverEmail",robotWarnPerson.getEmail());
                                log.info("开始发送邮件给{"+robotWarnPerson.getEmail()+"}......");
                                EmailUtil.sendEmailAttachRetiree(emailMap);
                            }else{
                                log.info("接收邮件的邮箱地址为空，不执行邮件发送");
                            }
                        }
                    }
                }
            }
        }catch(Exception e){
            log.error("消息发送失败：{}",e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public RobotOperationMessageConfig selectByWarnType(String warnType){
        Example example=new Example(RobotOperationMessageConfig.class);
        example.createCriteria().andEqualTo("warnType",warnType)
               .andEqualTo("messageStatus",1);
        RobotOperationMessageConfig robotOperationMessageConfig=robotWarnMessageDao.selectOneByExample(example);
        return robotOperationMessageConfig;
    }

    @Override
    public RobotOperationMessageConfig buildContent(List<OperationRequestVo> list, String warnType){
        RobotOperationMessageConfig robotOperationMessageConfig=this.selectByWarnType(warnType);
        if(robotOperationMessageConfig==null){
            return null;
        }
        if(warnType.equals(WarnStatus.API_EXCEPTION.getCode())){
            return robotOperationMessageConfig;
        }
        String emailContent=robotOperationMessageConfig.getEmailContent();
        String smsContent=robotOperationMessageConfig.getSmsContent();
        String emailContents="";
        String smsContents="";
        if(robotOperationMessageConfig!=null){
            for(OperationRequestVo operationRequestVo:list){
                if(StringUtils.isNotBlank(emailContent)){
                    String content=replaceContent(operationRequestVo,emailContent);
                    emailContents+="<br>"+content+"</br>";
                }
                if(StringUtils.isNotBlank(smsContent)){
                    String contents=replaceContent(operationRequestVo,smsContent);
                    smsContents+=contents+"\n";
                }
            }

        }
        robotOperationMessageConfig.setEmailContent(emailContents);
        robotOperationMessageConfig.setSmsContent(smsContents);
        return robotOperationMessageConfig;
    }

    public String replaceContent(OperationRequestVo operationRequestVo,String content){
        if(StringUtils.isNotBlank(operationRequestVo.getCustName())){
            content=content.replace("{company}", operationRequestVo.getCustName());
        }
        if(StringUtils.isNotBlank(operationRequestVo.getMachineCode())){
            content=content.replace("{machineCode}", operationRequestVo.getMachineCode());
        }
        if(StringUtils.isNotBlank(operationRequestVo.getAccountNumber())){
            content=content.replace("{accountNumber}", operationRequestVo.getAccountNumber());
        }
        if(StringUtils.isNotBlank(operationRequestVo.getErrorResult())){
            content=content.replace("{errorResult}", operationRequestVo.getErrorResult());
        }
        if(StringUtils.isNotBlank(operationRequestVo.getAppArgs())){
            Map<String,String> map=operationMonitorService.analysisAppArgs(operationRequestVo.getAppArgs());
            String addrName = map.get("addrName");
            String serviceName = map.get("serviceName");
            content=content.replace("{addrName}", addrName).replace("{serviceName}", serviceName);
        }
        return content;
    }
}
