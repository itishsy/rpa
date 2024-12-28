package com.seebon.rpa.entity.saas.vo.declare;

import com.seebon.rpa.entity.saas.po.declare.PaidInField;
import com.seebon.rpa.entity.saas.po.declare.PolicyPaidInMappingItem;
import lombok.Data;

import java.util.List;

/**
 * @Author hao
 * @Date 2022/12/29 17:54
 * @Version 1.0
 **/
@Data
public class PaidInFieldVO extends PaidInField {

    private String addressName;

    private String prefixName;

    private String localPaidFieldName;

    private List<PolicyPaidInMappingItem> items;
}
