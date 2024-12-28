package com.seebon.rpa.entity.saas.po.declare;

import com.seebon.rpa.entity.Identity;
import lombok.Data;

/**
 * @Author: tanyong
 * @Description:
 * @Date: 2022/11/23 10:23
 * @Modified By:
 */
@Data
public class PolicyBaseProductMapping extends Identity {
    private Integer businessType;

    private String businessTypeName;

    private String addrName;

    private String rpaProductCode;

    private String rpaProductName;

    private String jiuyanProductCode;

    private String jiuyanProductName;

    private Integer minBaseRule;

    private Integer maxBaseRule;
}
