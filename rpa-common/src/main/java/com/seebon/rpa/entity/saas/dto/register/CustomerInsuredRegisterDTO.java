package com.seebon.rpa.entity.saas.dto.register;

import com.seebon.rpa.entity.saas.po.register.CustomerInsuredRegisterFile;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author ZhenShijun
 * @dateTime 2023-03-27 15:34:20
 */
@ApiModel("customerInsuredRegisterDTO")
@Data
public class CustomerInsuredRegisterDTO {

    @ApiModelProperty("参保城市")
    private String addrName;

    @ApiModelProperty("业务类型 1：社保，2：公积金")
    private String businessType;

    @ApiModelProperty("单位社保号/公积金号")
    private String accountNumber;

    @ApiModelProperty("系统类型")
    private String tplTypeCode;

    @ApiModelProperty("名册月份（yyyy-MM）")
    private String dataMonth;

    @ApiModelProperty("名册人数")
    private String registerNumber;

    @ApiModelProperty("默认保存在测试数据时，对申报数据进行申报数据核验打标签")
    private Boolean valid = Boolean.TRUE;

    @ApiModelProperty("名册原始附件表")
    private List<CustomerInsuredRegisterFile> files;

    @ApiModelProperty("名册明细")
    private List<CustomerInsuredRegisterDetailSaveDTO> details;

}
