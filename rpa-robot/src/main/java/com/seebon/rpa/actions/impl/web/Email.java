package com.seebon.rpa.actions.impl.web;

import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;
import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.ElementTarget;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.RobotAction;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zjf
 * @dateTime 2023-12-28
 */
@RobotAction(name = "发送邮件", targetType = ElementTarget.class)
@Slf4j
public class Email extends AbstractAction {

    @ActionArgs(value = "发送人电子邮箱", required = true)
    private String mailTo;

    @ActionArgs(value = "邮件标题", required = true)
    private String title;

    @ActionArgs(value = "邮件内容", required = true)
    private String content;

    @Override
    public void run() {
        try {
            MailAccount account = new MailAccount();
            account.setHost("smtp.seebon.com");
            account.setFrom("spfcore@seebon.com");
            account.setUser("spfcore@seebon.com");
            account.setPass("spf@1233");
            MailUtil.send(account, mailTo, title, content, false);
        } catch (Exception e) {
            log.error("发送邮件失败" + e.getMessage(), e);
        }
    }
}
