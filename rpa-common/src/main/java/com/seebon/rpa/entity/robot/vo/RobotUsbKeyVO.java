package com.seebon.rpa.entity.robot.vo;

import com.seebon.rpa.entity.robot.RobotUsbKey;
import lombok.Data;

@Data
public class RobotUsbKeyVO extends RobotUsbKey {
    private String appName;
    private String declareSystemName;
    private String customerName;
    /**
     * 基础标识
     */
    private String baseHash;
    /**
     * 内容标识
     */
    private String contextHash;
    /**
     * 宿主机macAddress
     */
    private String hostMacAddress;
    /**
     * 状态:1-已插入未初始化/2-可用/3-已被挂载/4-重复KEY不可用/5-未插入
     */
    private Integer status;
    /**
     * 挂载客户端
     */
    private String mountClient;
}
