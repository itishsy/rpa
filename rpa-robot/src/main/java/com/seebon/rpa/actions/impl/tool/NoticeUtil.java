package com.seebon.rpa.actions.impl.tool;

import cn.hutool.core.img.ImgUtil;
import com.seebon.common.utils.SmsUtils;
import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.ObjectTarget;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.ActionUtils;
import com.seebon.rpa.context.annotation.Conditions;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.enums.NoticeMethod;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.awt.image.BufferedImage;
import java.io.File;

/**
 * @author ZhenShijun
 * @dateTime 2023-02-13 10:19:03
 */
@Slf4j
@ActionUtils
@RobotAction(name = "消息通知", targetType = ObjectTarget.class, comment = "消息通知")
public class NoticeUtil extends AbstractAction {

    private static final String mess_mail_user = "spfcore@seebon.com";

    private static final String mess_mail_service_host = "smtp.seebon.com";

    private static final String mess_mail_passwd = "spf@1233";

    @Conditions({"email:mailTo,subject,emailContent,imgPath",
            "sms:phoneNumber,smsContent"})
    @ActionArgs(value = "操作类型", required = true, dict = NoticeMethod.class)
    private String actionType;

    @ActionArgs(value = "电子邮箱", required = true, comment = "接收邮件电子邮箱地址，多个可用,隔开")
    private String mailTo;

    @ActionArgs(value = "邮件主题")
    private String subject;

    @ActionArgs(value = "邮件内容")
    private String emailContent;

    @ActionArgs(value = "图片地址")
    private String imgPath;

    @ActionArgs(value = "手机号", required = true, comment = "接收短信手机号，多个可用,隔开")
    private String phoneNumber;

    @ActionArgs(value = "短信内容", required = true)
    private String smsContent;

    @Override
    public void run() {
        switch (NoticeMethod.valueOf(actionType)) {
            case sms: {
                if (StringUtils.isNotBlank(phoneNumber) && StringUtils.isNotBlank(smsContent)) {
                    for (String pn : phoneNumber.split(",")) {
                        SmsUtils.send(pn, smsContent);
                    }
                }
                break;
            }
            case email: {
                if (StringUtils.isBlank(imgPath)) {
                    com.seebon.rpa.utils.sms.SmsUtils.setImgEmail(subject, emailContent, mailTo);
                } else {
                    File file = new File(imgPath);
                    BufferedImage bufferedImage = ImgUtil.read(file);
                    String image = ImgUtil.toBase64(bufferedImage, imgPath.substring(imgPath.lastIndexOf(".") + 1, imgPath.length()));
                    if (StringUtils.isNotBlank(mailTo)) {
                        com.seebon.rpa.utils.sms.SmsUtils.setImgEmail(StringUtils.isBlank(emailContent) ? subject : emailContent, image, mailTo);
                    }
                }
                break;
            }
            default:
                break;
        }
    }
}
