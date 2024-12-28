package com.seebon.rpa.entity.saas.dto.social;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 贫困与失业人员列表
 */
@Data
public class PoorAndLostWorkDTO {
    private Integer pinkunId;

    private Integer shiyeId;

    private String name;

    private String idCard;

    private Integer poorMonthCount;

    private BigDecimal poorAmt;

    private Integer lostWorkCount;

    private BigDecimal lostWorkAmt;

    private Integer proveFileId;

    private String poorBatchNum;

    private String lostWordBatchNum;

    private Date poorCreateTime;

    private Date lostWorkCreateTime;






}
