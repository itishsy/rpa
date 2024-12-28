package com.seebon.rpa.entity.saas.po.bill;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * TODO
 *
 * @author zjf
 * @describe
 * @date 2024-01-04 18:12
 */
@ApiModel(value ="账单附件")
@Data
public class ReceiptFile{

    @ApiModelProperty("文件地址")
    private String fileUrl;

    @ApiModelProperty("文件名字")
    private String fileName;
}
