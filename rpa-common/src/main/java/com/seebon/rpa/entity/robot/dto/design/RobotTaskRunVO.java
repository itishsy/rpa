package com.seebon.rpa.entity.robot.dto.design;

import lombok.Data;

/**
 * @Author hao
 * @Date 2022/9/29 18:38
 * @Version 1.0
 **/
@Data
public class RobotTaskRunVO {
    /**
     * 默认 仕邦
     */
    private Integer clientId = 1;
    /**
     * 地区名称
     */
    private String addrName;
    /**
     * 业务类型：社保、公积金
     */
    private String businessType;
    /**
     * 申报类型：增员，减员
     */
    private String declareType;
    /**
     * 机器标识码
     */
    private String machineCode;
    /**
     * 申报账户
     */
    private String accountNumber;
    /**
     * 执行编码
     */
    private String executionCode;
}
