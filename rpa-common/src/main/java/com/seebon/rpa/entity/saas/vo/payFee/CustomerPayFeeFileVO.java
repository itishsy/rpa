package com.seebon.rpa.entity.saas.vo.payFee;


import com.seebon.rpa.entity.saas.po.payFee.CustomerPayFeeFile;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("客户缴费单据凭证VO")
@Data
public class CustomerPayFeeFileVO extends CustomerPayFeeFile {

    @ApiModelProperty("单据文件名称")
    private String fileName;

    @ApiModelProperty("文件全路径")
    private String fileUrl;

    @ApiModelProperty("单据文件类型名称")
    private String fileTypeName;

}
