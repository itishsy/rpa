package com.seebon.rpa.entity.robot.vo;

import com.seebon.rpa.entity.Identity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("附件信息po")
@Data
public class FileStoreVO extends Identity {
    @ApiModelProperty("文件名称")
    private String clientFileName;

    @ApiModelProperty("文件路径")
    private String serverFilePath;

    @ApiModelProperty("文件http地址")
    private String fileUrl;

    @ApiModelProperty("文件类型")
    private Integer filestoreType;
}
