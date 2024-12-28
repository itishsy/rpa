package com.seebon.rpa.entity.saas.po.social;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class SocialCustManageVO {
    private Integer id;
    private String custName;
    /**
     * 筛查人数
     */
    private Integer totalPersonNum;
    /**
     * 筛查符合月份
     */
    private Integer totalMatchinghMonthNum;
    /**
     * 筛查符合人数
     */
    private Integer totalMatchinghNum;
    /**
     *退税金额
     */
    private BigDecimal refundTaxAmt;
    private Date createTime;
    private Integer status;
    private String saleName;

}
