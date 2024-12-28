package com.seebon.rpa.entity.saas.dto.declare;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.io.Serializable;

@ApiModel("客户费用模版-添加")
@Data
public class PaidInFieldBaseTplDTO implements Serializable {

    @ApiModelProperty("id")
    private Integer id;

    @ApiModelProperty(value = "费用配置uuid", required = true)
    private String uuid;

    @ApiModelProperty(value = "模版类型code", required = true)
    private String tplType;

    @ApiModelProperty("模版类型名称")
    private String tplTypeName;

    @ApiModelProperty(value = "客户id", required = true)
    private Integer custId;

    @ApiModelProperty("客户名称")
    private String custName;

    @ApiModelProperty(value = "模版文件id", required = true)
    private Integer fileId;

    @ApiModelProperty("关联文件名称")
    private String fileName;

    @Column
    @ApiModelProperty("参保城市id")
    private Integer addrId;

    @Column
    @ApiModelProperty("业务类型，1--社保；2--公积金；0--社保公积金联合导出模板")
    private Integer businessType;

    @ApiModelProperty("导出方式名称")
    private String businessTypeName;

}
