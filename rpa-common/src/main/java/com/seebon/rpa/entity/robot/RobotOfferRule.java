package com.seebon.rpa.entity.robot;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel(value = "回盘规则配置表")
@Table(name = "robot_offer_rule")
public class RobotOfferRule implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ApiModelProperty(value = "参保地id")
    @Column(name = "addr_id")
    private Integer addrId;

    @ApiModelProperty(value = "参保地名称")
    @Column(name = "addr_name")
    private String addrName;

    @ApiModelProperty(value = "业务类型 1：社保 2：公积金")
    @Column(name = "business_type")
    private Integer businessType;

    @ApiModelProperty(value = "申报类型 1：增员，2：减员，3：调基，5：补缴")
    @Column(name = "declare_type")
    private Integer declareType;

    @ApiModelProperty(value = "申报网站")
    @Column(name = "declare_website")
    private String declareWebsite;

    @ApiModelProperty(value = "当前节点(表:policy_declare_operation_type_dict-code)")
    @Column(name = "current_node")
    private String currentNode;

    @ApiModelProperty(value = "启动状态 0：否 1：是")
    private Integer status;

    @ApiModelProperty(value = "创建人Id")
    @Column(name = "create_id")
    private Integer createId;

    @ApiModelProperty(value = "创建时间")
    @Column(name = "create_time")
    private Date createTime;

    @ApiModelProperty(value = "更新人Id")
    @Column(name = "update_id")
    private Integer updateId;

    @ApiModelProperty(value = "更新时间")
    @Column(name = "update_time")
    private Date updateTime;
}