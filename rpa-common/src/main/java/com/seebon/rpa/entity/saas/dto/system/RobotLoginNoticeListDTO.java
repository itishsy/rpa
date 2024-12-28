package com.seebon.rpa.entity.saas.dto.system;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@ApiModel("新增登录通知信息信息")
@Data
public class RobotLoginNoticeListDTO {

    @ApiModelProperty("机器人执行批次号")
    private String execBatchCode;

    @ApiModelProperty("消息模板id")
    private Integer tempId;

    @ApiModelProperty("参保城市")
    private String addrName;

    @ApiModelProperty("公司名称")
    private String companyName;

    @ApiModelProperty("单位社保号or公积金号")
    private String accountNumber;

    @ApiModelProperty("发送手机号码")
    private String recipientPhoneNumber;

    @ApiModelProperty("二维码文件id")
    private Integer imgFileId;

    /*@ApiModelProperty("推送短信内容")
    private String smsContent;

    @ApiModelProperty("语音消息内容")
    private String voiceContent;*/

    @ApiModelProperty("提示信息(在H5展示)")
    private String tips;
}
