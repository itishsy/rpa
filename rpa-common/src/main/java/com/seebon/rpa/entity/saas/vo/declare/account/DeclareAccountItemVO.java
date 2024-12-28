package com.seebon.rpa.entity.saas.vo.declare.account;

import com.seebon.rpa.entity.saas.po.declare.account.DeclareAccountItem;
import lombok.Data;

/**
 * @Author hao
 * @Date 2023/5/16 14:54
 * @Version 1.0
 **/
@Data
public class DeclareAccountItemVO extends DeclareAccountItem {
    private String name;

    private Integer serviceStatus;

    private String businessTypeName;

    private String addrName;

    private Integer virtually;

    private String custName;

    private String orgName;

    private String taxNumber;
}
