package com.seebon.rpa.entity.robot.device.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class ReadUsbContentRequest {
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
     * 更新人Id
     */
    private Integer updateId;

    /**
     * 分页页码
     */
    private Integer pageStart;
    /**
     * 分页大小
     */
    private Integer pageSize;

    private List<Map<String,Object>> tableData;

    public ReadUsbContentRequest(Integer id) {
        this.id = id;
    }
}
