package com.seebon.rpa.entity.robot.dto.robotWarn;

import lombok.Data;

/**
 * @author ZhenShijun
 * @dateTime 2022-12-26 11:07:36
 */
@Data
public class RobotWarnMsgDTO {

    /*
    * 短信提示信息
    * */
    private String smsContent;

    /*
    * 邮件内容
    * */
    private String emailContent;

}
