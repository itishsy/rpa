package com.seebon.rpa.entity.saas;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel("申报操作标签信息")
@Data
public class PolicyDeclareOperationTypeDictVO implements Serializable {

    @ApiModelProperty("主键id")
    private Integer id;

    @ApiModelProperty("编码")
    private String code;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("业务类型，1：社保，2：公积金，空标识表示社保公积金都适用")
    private Integer businessType;

    @ApiModelProperty("申报类型，1：增员，2：减员，3：调基，5：补缴，空标识表示都适用")
    private Integer changeType;

    @ApiModelProperty("是否默认 1：是，0：否")
    private Integer defaultType;

    @ApiModelProperty("备注信息")
    private String comment;
}
