package com.seebon.rpa.entity.vo.declare;

import lombok.Data;

/**
 * @author LY
 * @date 2023/7/25 17:38
 */
@Data
public class DeclareStatisticsVo {

    private Integer waitCount;

    private Integer doingCount;

    private Integer successCount;

    private Integer failCount;

    private Integer personCount;


}
