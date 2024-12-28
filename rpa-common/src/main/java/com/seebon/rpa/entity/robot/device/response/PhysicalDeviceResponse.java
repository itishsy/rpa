package com.seebon.rpa.entity.robot.device.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PhysicalDeviceResponse {

    private int id;
    /**
     * 租户id
     */
    private int companyId;

    /**
     * 设备类型
     */
    private int type;
    /**
     * 主机名称
     */
    private String hostname;
    /**
     * 网卡mac地址
     */
    private String macAddress;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 最后更新时间
     */
    private LocalDateTime updateTime;

    //==================运行时数据================
    private String ipAddress;
    private String version;
    private LocalDateTime upTime;

    //==================扩展字段================
    /**
     * 租户名称
     */
    private String companyName;
}
