package com.seebon.rpa.entity.robot;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel(value = "回盘规则配置item表")
@Table(name = "robot_offer_rule_item")
public class RobotOfferRuleItem implements Serializable {

    @ApiModelProperty(value = "主键id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ApiModelProperty(value = "回盘配置id")
    @Column(name = "rule_id")
    private Integer ruleId;

    @ApiModelProperty(value = "申报状态，2：申报中，4：申报成功，5：申报失败")
    @Column(name = "declare_status")
    private Integer declareStatus;

    @ApiModelProperty(value = "匹配规则：1-equals 2-contains  3-startsWith 4-endsWith")
    @Column(name = "match_rule")
    private Integer matchRule;

    @ApiModelProperty(value = "内容")
    private String content;

    @ApiModelProperty(value = "替换类型：1：不替换，2：申报中信息，3：自定义，4：拼接前缀，5：追加")
    @Column(name = "replace_type")
    private Integer replaceType;

    @ApiModelProperty(value = "替换信息")
    @Column(name = "replace_content")
    private String replaceContent;

    @ApiModelProperty(value = "下一节点(表:policy_declare_operation_type_dict-code)")
    @Column(name = "next_node")
    private String nextNode;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "状态 0：禁用 1：启用")
    private Integer status;

    @ApiModelProperty(value = "是否删除 0：否 1：是")
    private Integer deleted;

    @ApiModelProperty(value = "创建时间")
    @Column(name = "create_time")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @Column(name = "update_time")
    private Date updateTime;
}