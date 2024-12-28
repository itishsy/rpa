package com.seebon.rpa.entity.robot.vo;

import com.seebon.rpa.entity.robot.RobotOfferRule;
import com.seebon.rpa.entity.robot.RobotOfferRuleItem;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "回盘规则配置表")
public class RobotOfferRuleVO extends RobotOfferRule {

    @ApiModelProperty(value = "当前节点(表:policy_declare_operation_type_dict-code)")
    private String currentNodeName;

    @ApiModelProperty(value = "申报网站名称")
    private String declareWebsiteName;

    @ApiModelProperty(value = "下一节点")
    private String nextNode;

    @ApiModelProperty(value = "类型：checkStatus-根据原因判断回盘状态，checkNextNode-根据原因判断下一节点")
    private String ruleType;

    @ApiModelProperty(value = "判断回盘状态")
    private List<RobotOfferRuleItem> ruleItemList;

    @ApiModelProperty(value = "判断下一节点")
    private List<RobotOfferRuleItem> nextNodeList;
}