package com.seebon.rpa.entity.agent.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * TODO
 *
 * @author zjf
 * @describe
 * @date 2023-07-11 10:33
 */

@Data
@ApiModel("业务场景提醒")
@Table(name = "business_scene_warn")
public class BusinessSceneWarnVo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer id;

    @Column
    @ApiModelProperty("参保地id")
    private Integer addrId;

    @Column
    @ApiModelProperty("参保地")
    private String addrName;

    @Column
    @ApiModelProperty("业务类型 1社保 2公积金")
    private Integer businessType;

    @Column
    @ApiModelProperty("申报类型 1：增员，2：减员，3：调基，5：补缴")
    private Integer declareType;

    @Column
    @ApiModelProperty("功能点")
    private String functionPoints;

    @Column
    @ApiModelProperty("场景类型 0 来自网站 2 来自程序")
    private Integer sceneType;

    @Column
    @ApiModelProperty("申报网址")
    private String declareWebsite;

    @Column
    @ApiModelProperty("提示信息")
    private String warnMessage;

    @Column
    @ApiModelProperty("处置动作")
    private String handleAction;

    @Column
    @ApiModelProperty("场景说明")
    private String scenarioDescription;

    @Column
    @ApiModelProperty("注解")
    private String annotate;

    @Column
    @ApiModelProperty("替换提示")
    private String replaceWarn;

    @Column
    @ApiModelProperty("是否上线 0 否 1是")
    private Integer isLive;

    @Column
    @ApiModelProperty("操作人")
    private String operator;

    @Column
    @ApiModelProperty("操作时间")
    private Date operatorTime;

    @Column
    @ApiModelProperty("创建时间")
    private Date createTime;

    @Column
    @ApiModelProperty("更新时间")
    private Date updateTime;

}
