package com.seebon.rpa.entity.robot.dto;

import cn.hutool.core.lang.Dict;
import com.seebon.rpa.entity.robot.vo.RobotFlowVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


import java.io.Serializable;
import java.util.List;

@Data
public class RobotLoginAuthDTO implements Serializable {

    @ApiModelProperty(value = "客户id")
    private Integer clientId;

    @ApiModelProperty(value = "任务编码")
    private String taskCode;

    @ApiModelProperty(value = "申报系统 dictKey:10007")
    private String declareSystem;

    @ApiModelProperty(value = "执行事项，1：增减员（增员、减员、调基、补缴），6：缴费，7：在册名单，8：费用明细，9：政策通知 10：基数申报 11：登录 12：查审核结果")
    private Integer queueItem;

    @ApiModelProperty("操作来源   1:定时任务  2:手动任务")
    private Integer source;

    @ApiModelProperty("来源 1数据增减员")
    private Integer detailSource;


    /* ----------- 进数据校验  ----------------*/


    @ApiModelProperty("是否校验执行规则")
    private Boolean isVerifyRule;

    @ApiModelProperty("")
    private List<RobotFlowVO> flows;

    @ApiModelProperty("增减员文件后缀")
    private Dict checkDeclareOffer;



}
