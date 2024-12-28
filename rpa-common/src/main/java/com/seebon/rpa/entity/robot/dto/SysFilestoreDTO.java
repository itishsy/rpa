package com.seebon.rpa.entity.robot.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("文件DTO")
@Data
public class SysFilestoreDTO {

    @ApiModelProperty("文件名称")
    private String originalFilename;

    @ApiModelProperty("byte数组")
    private byte[] bytes;
}
