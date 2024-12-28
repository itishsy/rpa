package com.seebon.rpa.entity.robot.device.response;

import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
public class ReadUsbContentResponse {
    /**
     * 自增唯一主键
     */
    private Integer id;
    /**
     * idVendor(填入10进制)
     */
    private Integer idVendor;
    /**
     * idProduct(填入10进制)
     */
    private Integer idProduct;
    /**
     * usb_interface
     */
    private Integer usbInterface;
    /**
     * 内容(json格式)
     */
    private String content;
    /**
     * 创建者Id
     */
    private Integer createId;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新人Id
     */
    private Integer updateId;
    /**
     * 更新时间
     */
    private Date updateTime;

    private List<Map<String,Object>> tableData;
}
