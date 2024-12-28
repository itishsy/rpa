package com.seebon.rpa.entity.saas.vo.declare.account;

import com.seebon.rpa.entity.saas.po.declare.account.DeclareAccountBase;
import com.seebon.rpa.entity.saas.po.declare.account.DeclareAccountItem;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author ZhenShijun
 * @dateTime 2022-04-11 18:31:25
 */
@ApiModel("申报账户基本信息VO")
@Data
public class DeclareAccountBaseVO extends DeclareAccountBase {

    @ApiModelProperty("单位社保账号")
    private List<DeclareAccountItem> socialAccounts;

    @ApiModelProperty("单位公积金账号")
    private List<DeclareAccountItem> accfundAccounts;

    @ApiModelProperty("单位社保和公积金账号")
    private List<DeclareAccountItem> accounts;

    @ApiModelProperty("参保主体名字")
    private String name;

    @ApiModelProperty("申报账户")
    private String accountNumber;

    @ApiModelProperty("申报账户id")
    private Integer itemId;

    @ApiModelProperty("申报账号总数")
    private Integer accountCount;

    @ApiModelProperty("申报账号集合")
    List<String> accountCountList;

    @ApiModelProperty("客户名称")
    private String customerName;

    @ApiModelProperty("业务申报类型")
    private String businessDeclareType;

    @ApiModelProperty("截止日期")
    private String deadLine;

    @ApiModelProperty("剩余天数")
    private Integer restDay;
}
