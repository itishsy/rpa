package com.seebon.rpa.entity.robot.dto;

import com.seebon.rpa.entity.saas.po.declare.account.DeclareAccountBase;
import com.seebon.rpa.entity.saas.po.declare.account.DeclareAccountItem;
import lombok.Data;

import java.util.List;

/**
 * @Author hao
 * @Date 2023/1/6 14:46
 * @Version 1.0
 **/
@Data
public class DeclareAccountBaseDTO extends DeclareAccountBase {
    private List<DeclareAccountItem> declareAccountItems;

    private String originalCompanyName;

    private String originalAccountNumber;

    private Integer businessType;

    private Boolean addItems = true;

    private Integer permissionId;
}
