package com.seebon.rpa.entity.saas.vo;

import lombok.Data;

@Data
public class DevAddrCountVO {

    /**
     * 申报地区
     */
    private String addrName;
    /**
     * 客户id
     */
    private Integer customerId;
    /**
     * 申报类型
     */
    private Integer changeType;
    /**
     * 未申报数量
     */
    private Integer num;
    /**
     * 业务类型  社保， 公积金
     */
    private String businessType;
}
