package com.seebon.rpa.entity.robot.device.response;

import lombok.Data;

import java.util.Date;

@Data
public class UsbKeyResponse {
    /**
     * 自增唯一主键
     */
    private Integer id;
    /**
     * 租户id
     */
    private Integer companyId;

    /**
     * idVendor(填入16进制, 不含0x)
     */
    private Integer idVendor;

    /**
     * idProduct(填入16进制, 不含0x)
     */
    private Integer idProduct;
    /**
     * 基础标识
     */
    private String baseHash;
    /**
     * 内容标识
     */
    private String contextHash;
    /**
     * busNumber
     */
    private Integer busNumber;
    /**
     * deviceAddress
     */
    private Integer deviceAddress;
    /**
     * portNumber
     */
    private Integer portNumber;
    /**
     * busId
     */
    private String busId;
    /**
     * 宿主机ip
     */
    private String hostIpAddress;
    /**
     * 宿主机hostName
     */
    private String hostName;
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
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 最后更新/上报时间
     */
    private Date updateTime;
    /**
     * 插入时间
     */
    private Date attachTime;

    private String companyName;

    private Integer bingNum;

    /**
     * idVendor(填入16进制, 不含0x)
     */
    private String idVendorStr;

    /**
     * idProduct(填入16进制, 不含0x)
     */
    private String idProductStr;
    /**
     * 申报账户
     */
    private String declareAccounts;
}
