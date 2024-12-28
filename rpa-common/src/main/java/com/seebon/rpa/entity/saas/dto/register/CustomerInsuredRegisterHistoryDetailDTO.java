package com.seebon.rpa.entity.saas.dto.register;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Map;

@ApiModel("历史名册详细信息")
@Document(collection = "customer_insured_register_history_detail")
@Data
public class CustomerInsuredRegisterHistoryDetailDTO implements Serializable {

    @ApiModelProperty("id")
    private String id;

    @ApiModelProperty("名册id")
    private Integer registerId;

    @ApiModelProperty("客户id")
    private Integer custId;

    @ApiModelProperty("参保城市id")
    private Integer addrId;

    @ApiModelProperty("参保城市名称")
    private String addrName;

    @ApiModelProperty("业务类型，1：社保，2：公积金")
    private Integer businessType;

    @ApiModelProperty("单位申报号或单位公积金号")
    private String accountNumber;

    @ApiModelProperty("系统类型")
    private String tplTypeCode;

    @ApiModelProperty("名册年月")
    private String dataMonth;

    @ApiModelProperty("证件号码")
    private String idCard;

    @ApiModelProperty("姓名")
    private String empName;

    @ApiModelProperty("个人社保号或个人公积金号")
    private String empAccount;

    @ApiModelProperty("缴纳基数")
    private String base;

    @ApiModelProperty("其他信息，多余的字段按源文件字段原名：字段值形成存入")
    private Map<String, Object> otherInfo;

}
