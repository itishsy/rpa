package com.seebon.rpa.entity.saas.vo.message;

import com.seebon.rpa.entity.saas.po.message.MessageRuleConfig;
import com.seebon.rpa.entity.saas.po.message.MessageRulePersonnel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("消息配置信息VO")
public class MessageRuleConfigVO extends MessageRuleConfig {

    @ApiModelProperty("消息类型名称")
    private String messageTypeName;

    @ApiModelProperty("消息项目名称")
    private String warnTypeName;

    @ApiModelProperty("响应等级名称")
    private String responseGradeName;

    @ApiModelProperty("系统公告消息维度业务类型名称")
    private String businessTypeName;

    @ApiModelProperty("消息时效名称")
    private String messageStrategyName;

    @ApiModelProperty("状态名称")
    private String statusName;

    @ApiModelProperty("系统公告的文件")
    private List<MessageRuleConfigFileVO> files;

    @ApiModelProperty("运营通知对象")
    private List<MessageRuleOperatorVO> operatorList;

    @ApiModelProperty("指定人员")
    private List<MessageRulePersonnel> personnelList;

    @ApiModelProperty("指定客户")
    private List<MessageRuleCustomerVO> custList;

    @ApiModelProperty("通知对象")
    private String notificationObject;

    @ApiModelProperty("通知方式")
    private String notificationWay;

    @ApiModelProperty("创建人姓名")
    private String createName;

    @ApiModelProperty("修改人姓名")
    private String updateName;

    @ApiModelProperty("发布人姓名")
    private String releaseName;

}
