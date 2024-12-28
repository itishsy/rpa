package com.seebon.rpa.entity.robot.device.request;

import lombok.Data;

import java.util.List;

@Data
public class UsbKeyRequest {
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
     * 状态:1-已插入未初始化/2-可用/3-已被挂载/4-重复KEY不可用/5-未插入
     */
    private Integer status;
    /**
     * 宿主机macAddress
     */
    private String hostMacAddress;
    /**
     * 分页页码
     */
    private Integer pageStart;
    /**
     * 分页大小
     */
    private Integer pageSize;
    /**
     * ids
     */
    private List<Integer> ids;
    /**
     * 关键词
     */
    private String keyword;
}
