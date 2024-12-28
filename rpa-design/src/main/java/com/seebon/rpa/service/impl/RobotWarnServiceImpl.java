package com.seebon.rpa.service.impl;

import com.seebon.common.utils.SmsUtils;
import com.seebon.rpa.BusinessException;
import com.seebon.rpa.auth.SecurityContext;
import com.seebon.rpa.auth.Session;
import com.seebon.rpa.entity.IgGridDefaultPage;
import com.seebon.rpa.entity.robot.dto.robotWarn.RobotWarnMsgDTO;
import com.seebon.rpa.entity.robot.dto.robotWarn.RobotWarnMsgForwardDTO;
import com.seebon.rpa.entity.robot.po.robotWarn.RobotWarnConfigBase;
import com.seebon.rpa.entity.robot.po.robotWarn.RobotWarnMsgList;
import com.seebon.rpa.entity.robot.po.robotWarn.RobotWarnRecipient;
import com.seebon.rpa.entity.robot.vo.robotWarn.RobotWarnConfigBaseVO;
import com.seebon.rpa.repository.mysql.RobotWarnConfigBaseDao;
import com.seebon.rpa.repository.mysql.RobotWarnMsgListDao;
import com.seebon.rpa.repository.mysql.RobotWarnRecipientDao;
import com.seebon.rpa.schedule.RobotWarnComponent;
import com.seebon.rpa.schedule.RobotWarnTask.RobotExecWarnTask;
import com.seebon.rpa.service.RobotWarnService;
import com.seebon.rpa.utils.EmailUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ZhenShijun
 * @dateTime 2022-12-02 11:43:03
 */
@Slf4j
@Service
public class RobotWarnServiceImpl implements RobotWarnService {

    @Autowired
    private RobotWarnComponent robotWarnComponent;

    @Autowired
    private RobotWarnConfigBaseDao robotWarnConfigBaseDao;

    @Autowired
    private RobotWarnRecipientDao robotWarnRecipientDao;

    @Autowired
    private RobotWarnMsgListDao robotWarnMsgListDao;

    @Autowired
    private RobotExecWarnTask robotExecWarnTask;

    @Override
    public void updateStatus(Integer id, Integer status) {
        RobotWarnConfigBase robotWarnConfigBase = robotWarnConfigBaseDao.selectByPrimaryKey(id);
        if (robotWarnConfigBase != null) {
            robotWarnConfigBase.setStatus(status);
            Session session = SecurityContext.currentUser();
            robotWarnConfigBase.setUpdateTime(new Date());
            robotWarnConfigBase.setUpdateId((int)session.getId());
            robotWarnComponent.stop(id);
            if (status == 1) {
                robotWarnComponent.start(robotWarnConfigBase);
            }
            robotWarnConfigBaseDao.updateByPrimaryKeySelective(robotWarnConfigBase);
        } else {
            throw new BusinessException("预警配置信息不存在，不能调整预警状态");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int addOrUpdate(RobotWarnConfigBaseVO vo) {
        Integer id = vo.getId();
        boolean add = false;
        Session session = SecurityContext.currentUser();
        if (id == null) { // 新增
            add = true;
            String type = vo.getType();
            Example example = new Example(RobotWarnConfigBase.class);
            example.createCriteria().andEqualTo("type", type);
            int count = robotWarnConfigBaseDao.selectCountByExample(example);
            if (count > 0) {
                throw new BusinessException(String.format("预警类型%s已存在，不能再次新增",vo.getTypeName()));
            }
            vo.setCreateId((int)session.getId());
            vo.setCreateTime(new Date());
            robotWarnConfigBaseDao.insert(vo);
        } else {
            vo.setUpdateId((int) session.getId());
            vo.setUpdateTime(new Date());
            robotWarnConfigBaseDao.updateByPrimaryKey(vo);
            Example example = new Example(RobotWarnRecipient.class);
            example.createCriteria().andEqualTo("warnBaseId", vo.getId());
            robotWarnRecipientDao.deleteByExample(example);
        }

        List<RobotWarnRecipient> recipients = vo.getRecipients();
        if (CollectionUtils.isNotEmpty(recipients)) {
            recipients.stream().forEach(item -> {
                item.setWarnBaseId(vo.getId());
            });
            robotWarnRecipientDao.insertList(recipients);
        }

        if (add && vo.getStatus() == 1) {
            robotWarnComponent.start(vo);
        }

        if (!add) {
            robotWarnComponent.stop(id);
            if (vo.getStatus() == 1) {
                robotWarnComponent.start(vo);
            }
        }
        return 1;
    }

    @Override
    public IgGridDefaultPage<RobotWarnConfigBaseVO> getByPage(Map<String, Object> params) {
        int count = robotWarnConfigBaseDao.selectCountByParams(params);
        List<RobotWarnConfigBaseVO> list = robotWarnConfigBaseDao.selectListByParams(params);
        return new IgGridDefaultPage<>(list, count);
    }

    @Override
    public void sendExecErrorMsg(RobotWarnMsgDTO msgDTO) {
        /*Example example = new Example(RobotWarnConfigBase.class);
        example.createCriteria().andEqualTo("type", "1028002");
        RobotWarnConfigBase robotWarnConfigBase = robotWarnConfigBaseDao.selectOneByExample(example);*/
        robotExecWarnTask.sendExecErrorMsg(msgDTO);
    }

    @Override
    public IgGridDefaultPage<RobotWarnMsgList> getMsgPage(Map<String, Object> params) {
        int count = robotWarnMsgListDao.getCountByParams(params);
        List<RobotWarnMsgList> list = robotWarnMsgListDao.getListByParams(params);
        return new IgGridDefaultPage<RobotWarnMsgList>(list, count);
    }

    @Override
    public void forwardMsg(RobotWarnMsgForwardDTO dto) {
        Integer msgId = dto.getMsgId();
        List<RobotWarnRecipient> recipients = dto.getRecipients();
        if (msgId==null || CollectionUtils.isEmpty(recipients)) {
            throw new BusinessException("预警信息转发失败，失败原因：转发信息缺失");
        }
        RobotWarnMsgList robotWarnMsgList = robotWarnMsgListDao.selectByPrimaryKey(dto.getMsgId());

        if (robotWarnMsgList == null) {
            throw new BusinessException("预警信息转发失败，失败原因：未能找到所转发的预警信息");
        }
        if (CollectionUtils.isNotEmpty(recipients)) {

            for (RobotWarnRecipient recipient : recipients) {

                if (robotWarnMsgList.getSendWay() == 1 && StringUtils.isNotBlank(recipient.getPhoneNumber())) {
                    RobotWarnMsgList msg = getMsgEntity(robotWarnMsgList);
                    msg.setEmpName(recipient.getEmpName());
                    msg.setPhoneNumber(recipient.getPhoneNumber());
                    sendMsg(msg);
                }
                if (robotWarnMsgList.getSendWay() == 2 && StringUtils.isNotBlank(recipient.getEmail())) {
                    RobotWarnMsgList msg = getMsgEntity(robotWarnMsgList);
                    msg.setEmpName(recipient.getEmpName());
                    msg.setEmail(recipient.getEmail());
                    sendMsg(msg);
                }
            }
        }

    }

    private RobotWarnMsgList getMsgEntity(RobotWarnMsgList robotWarnMsgList) {
        RobotWarnMsgList msg = new RobotWarnMsgList();
        msg.setSendWay(robotWarnMsgList.getSendWay());
        msg.setSmsContent(robotWarnMsgList.getSmsContent());
        msg.setEmailTheme(robotWarnMsgList.getEmailTheme());
        msg.setEmailContent(robotWarnMsgList.getEmailContent());
        msg.setSendStatus(0);
        msg.setForwardId(robotWarnMsgList.getId());
        msg.setWarnBaseId(robotWarnMsgList.getWarnBaseId());
        msg.setWarnType(robotWarnMsgList.getWarnType());
        msg.setWarnTypeName(robotWarnMsgList.getWarnTypeName());
        msg.setCreateTime(new Date());
        return msg;
    }

    /**
     * 发送预警信息
     * @param msg
     */
    private final void sendMsg(RobotWarnMsgList msg) {
        Integer sendWay = msg.getSendWay();
        try {
            msg.setSendTime(new Date());
            if (sendWay == 1) { // 短信
                if (StringUtils.isNotBlank(msg.getPhoneNumber())) {
                    log.info("开始发送短信给{"+msg.getPhoneNumber()+"}......");
                    SmsUtils.send(msg.getPhoneNumber(), msg.getSmsContent());
                    log.info("成功结束发短信给{"+msg.getPhoneNumber()+"}......");
                    msg.setSendStatus(1);
                } else {
                    log.info("接收短信的手机号码为空，不执行短信发送");
                }
            } else if (sendWay == 2) { // 邮件
                if (StringUtils.isNotBlank(msg.getEmail())) {
                    Map<String, Object> emailMap = new HashMap<String, Object>();
                    emailMap.put("subject", msg.getEmailTheme());
                    emailMap.put("tplName", "mailBaseTemplate");
                    emailMap.put("mail_content", msg.getEmailContent());
                    emailMap.put("receiverEmail", msg.getEmail());
                    log.info("开始发送邮件给{"+msg.getEmail()+"}......");
                    EmailUtil.sendEmailAttachRetiree(emailMap);
                    msg.setSendStatus(1);
                    log.info("成功结束发邮件给{"+msg.getEmail()+"}......");
                } else {
                    log.info("接收邮件的邮箱地址为空，不执行邮件发送");
                }
            }
        } catch (Exception e) {
            log.error("消息发送失败：{}", e.getMessage());
            e.printStackTrace();
            msg.setSendStatus(2);
        } finally {
            robotWarnMsgListDao.insert(msg);
        }
    }
}
