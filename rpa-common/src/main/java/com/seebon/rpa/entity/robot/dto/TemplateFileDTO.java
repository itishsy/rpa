package com.seebon.rpa.entity.robot.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@ApiModel("模板文件DTO")
@Data
public class TemplateFileDTO {

    @ApiModelProperty("费用模版类型，1：社保，2：公积金，0：社保公积金联合导出")
    private Integer businessType;

    @ApiModelProperty("模板路径")
    private String tplPath;

    @ApiModelProperty("文件名称")
    private String fileName;

    @ApiModelProperty("byte数组")
    private byte[] bytes;
}
