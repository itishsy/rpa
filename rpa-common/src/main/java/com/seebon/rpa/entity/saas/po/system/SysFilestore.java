package com.seebon.rpa.entity.saas.po.system;


import com.seebon.rpa.entity.Identity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

@ApiModel("附件信息po")
@Data
@Table(name = "sys_filestore")
public class SysFilestore extends Identity {
    @ApiModelProperty("文件名称")
    @Column
    private String clientFileName;

    @ApiModelProperty("文件路径")
    @Column
    private String serverFilePath;

    @ApiModelProperty("文件http地址")
    @Column
    private String fileUrl;

    @ApiModelProperty("文件类型")
    @Column
    private Integer filestoreType;
}
