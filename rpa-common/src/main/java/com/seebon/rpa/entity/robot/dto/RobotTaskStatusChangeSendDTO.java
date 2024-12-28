package com.seebon.rpa.entity.robot.dto;

import lombok.Data;

@Data
public class RobotTaskStatusChangeSendDTO {

    // 所在设备
    private String machineCode;

    // 状态
    private String statusText;

    // 状态
    private Integer status;

    // 应用名称
    private String appName;

    // 申报账户编号
    private String accountNumber;

    // 申报账户公司名称
    private String companyName;

    // app应用参数
    private String appArgs;

    // 地址id
    private String addrId;

    // 地址id
    private String addrName;

    // 业务类型
    private String businessType;

    // 停用原因
    private String stopReason;

    // 客户ID
    private Integer clientId;


}
