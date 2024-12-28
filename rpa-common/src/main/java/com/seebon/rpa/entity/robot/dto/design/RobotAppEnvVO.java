package com.seebon.rpa.entity.robot.dto.design;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author dpl
 */
@Data
public class RobotAppEnvVO {

    @ApiModelProperty(value = "主键Id")
    private Integer id;

    @ApiModelProperty(value = "应用编码")
    private String appCode;

    @ApiModelProperty(value = "应用名称")
    private String appName;

    @ApiModelProperty(value = "流程编码")
    private String flowCode;

    @ApiModelProperty(value = "证书安装路径")
    private String[] paths;

    @ApiModelProperty(value = "下载链接")
    private String downloadUrl;

    @ApiModelProperty(value = "类型（0-需启动(证书)，1-无需启动（浏览器插件））")
    private Integer type;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    //==============拓展====================//
    @ApiModelProperty(value = "流程名称")
    private String flowName;
}
