package com.seebon.rpa.entity.robot.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel("app参数定义vo")
@Data
public class RobotAppArgsVO {

    @ApiModelProperty(value = "id")
    private Integer id;

    @ApiModelProperty(value = "参数编码")
    private String argsCode;

    @ApiModelProperty(value = "分组")
    private String groupName;

    @ApiModelProperty(value = "字段key")
    private String fieldKey;

    @ApiModelProperty(value = "字段名称")
    private String fieldName;

    @ApiModelProperty(value = "字段类型")
    private String fieldType;

    @ApiModelProperty(value = "显示顺序")
    private Integer displayOrder;

    @ApiModelProperty(value = "说明")
    private String comment;

    @ApiModelProperty(value = "联动条件")
    private String cond;

    @ApiModelProperty(value = "是否必填")
    private Integer required;

    private List<DataDictVO> robotDataDicts;

}
