package com.seebon.rpa.entity.saas.dto.social;
import lombok.Data;

import java.util.Date;

/**
 *      tmp.id_card AS idCard,
 *             tmp.`name`,
 *             pinkun.entry_time as poorEntryTime,
 *             pinkun.prove_file_id  as proveFileId,
 *             pinkun.batch_num  AS poorBatchNum,
 *             shiye.entry_time  AS lostWorkEntryTime,
 *             shiye.batch_num as lostWorkBatchNum
 * 符合贫困人口表
 */
@Data
public class PoorPersonDTO {
    private Integer pinkunId;
    private Integer shiyeId;
    private String name;
    private String idCard;
    private Date poorEntryTime;
    private Date poorCreateTime;
    private Integer proveFileId;
    private String poorBatchNum;
    private Date lostWorkEntryTime;
    private String lostWorkBatchNum;
    private Date lostWorkCreateTime;
}