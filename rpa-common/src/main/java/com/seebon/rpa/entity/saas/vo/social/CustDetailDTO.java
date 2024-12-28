package com.seebon.rpa.entity.saas.vo.social;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CustDetailDTO {

    private Integer custId;
    private String custName;
    private String saleName;
    /**
     * 交付人员  手动输入
     */
    private String serviceName;
    /**
     * 符合贫苦人员数
     */
    private Integer poorCount;
    /**
     * 符合失业人员数
     */
    private Integer lostWorkCount;

    /**
     * 总人数  去重
     */
    private Integer poorAndlostWorkCount;
    /**
     * 符合月份
     */
    private Integer monthCount;

    /**
     * 失业申领金额  手动输入
     */
    private BigDecimal lostWorkAmt;

    /**
     * 退税申领金额  手动输入
     */
    private BigDecimal refundTaxAmt;


}
