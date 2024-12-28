package com.seebon.rpa.entity.agent.vo.declare.employee;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class EmployeeDataVO {

    @ApiModelProperty("原始文件名称")
    private String clientFileName;

    @ApiModelProperty("服务器文件地址")
    private String serverFilePath;

    @ApiModelProperty("文件http地址")
    private String fileUrl;

    @ApiModelProperty("文件类型")
    private Integer filestoreType;

    private List<EmployeeSocialDeclareChangeItemBaseVO> itemBaseVOS;
}
