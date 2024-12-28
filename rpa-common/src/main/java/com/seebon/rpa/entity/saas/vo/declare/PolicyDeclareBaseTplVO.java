package com.seebon.rpa.entity.saas.vo.declare;

import com.seebon.rpa.entity.saas.po.declare.PolicyDeclareBaseTpl;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("报盘设置关联文件信息VO")
@Data
public class PolicyDeclareBaseTplVO extends PolicyDeclareBaseTpl {

    @ApiModelProperty("模板类型名称")
    private String tplTypeName;

    @ApiModelProperty("模版分支类型名称")
    private String operationTypeName;

}
