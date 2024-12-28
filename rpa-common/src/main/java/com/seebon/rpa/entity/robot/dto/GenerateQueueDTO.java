package com.seebon.rpa.entity.robot.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class GenerateQueueDTO {

    // 客户id
    private Integer clientId;

    // 地区
    private String addrName;

    // 地区ID
    private String addrId;

    // 业务类型 1社保  2公积金
    private Integer businessType;

    // 操作类型
    private Integer changeType;

    // 申报账户
    private List<String> accountNumber;

    // 执行事项，1：增减员（增员、减员、调基、补缴），6：缴费，7：在册名单，8：费用明细，9：政策通知 10：基数申报 11：登录 12：查审核结果
    private Integer queueItem;

    // 操作来源   1:定时任务  2:手动任务
    private Integer source;

}

