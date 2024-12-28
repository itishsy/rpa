package com.seebon.rpa.entity.saas;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel("业务场景提醒")
public class BusinessSceneWarnVO {
    @ApiModelProperty("主键Id")
    private Integer id;

    @ApiModelProperty("参保地id")
    private Integer addrId;

    @ApiModelProperty("参保地")
    private String addrName;

    @ApiModelProperty("业务类型 1社保 2公积金")
    private Integer businessType;

    @ApiModelProperty("申报类型 1：增员，2：减员，3：调基，5：补缴")
    private Integer declareType;

    @ApiModelProperty("功能点(当前节点)")
    private String functionPoints;

    @ApiModelProperty("场景类型 0 来自网站 2 来自程序")
    private Integer sceneType;

    @ApiModelProperty("申报网址")
    private String declareWebsite;

    @ApiModelProperty("提示信息")
    private String warnMessage;

    @ApiModelProperty("处置动作")
    private String handleAction;

    @ApiModelProperty("场景说明")
    private String scenarioDescription;

    @ApiModelProperty("注解")
    private String annotate;

    @ApiModelProperty("替换提示")
    private String replaceWarn;

    @ApiModelProperty("是否上线 0 否 1是")
    private Integer isLive;

    @ApiModelProperty("申报状态，1：待申报，2：申报中，4：申报成功，5：申报失败")
    private Integer declareStatus;

    @ApiModelProperty("匹配规则：1-equals 2-contains  3-startsWith 4-endsWith")
    private Integer matchRule;

    @ApiModelProperty("结果信息")
    private String resultMsg;

    @ApiModelProperty("下一个申报节点")
    private String nextDeclare;

    @ApiModelProperty("操作人")
    private String operator;

    @ApiModelProperty("操作时间")
    private Date operatorTime;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;
}
