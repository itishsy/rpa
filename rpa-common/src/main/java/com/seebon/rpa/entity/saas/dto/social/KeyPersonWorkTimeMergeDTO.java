package com.seebon.rpa.entity.saas.dto.social;

import com.google.common.collect.Sets;
import lombok.Data;


import java.util.Date;
import java.util.Set;

/**
 * 贫困与失业人员列表
 */
@Data
public class KeyPersonWorkTimeMergeDTO {
    private String name;

    private String idCard;

    private Set<String> types = Sets.newHashSetWithExpectedSize(2);

    private Date entryTime;
}
