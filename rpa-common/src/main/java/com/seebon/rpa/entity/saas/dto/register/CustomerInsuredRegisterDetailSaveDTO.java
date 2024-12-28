package com.seebon.rpa.entity.saas.dto.register;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@ApiModel("名册明细")
@Data
public class CustomerInsuredRegisterDetailSaveDTO implements Serializable {

    @ApiModelProperty("证件号码")
    private String idCard;

    @ApiModelProperty("姓名")
    private String empName;

    @ApiModelProperty("个人社保号/个人公积金号")
    private String empAccount;

    @ApiModelProperty("缴纳基数")
    private String base;

    @ApiModelProperty("其他信息，多余的字段按源文件字段原名：字段值形成存入")
    private Map<String, Object> otherInfo;

}
