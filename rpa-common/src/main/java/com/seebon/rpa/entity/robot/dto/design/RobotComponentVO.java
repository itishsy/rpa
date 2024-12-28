package com.seebon.rpa.entity.robot.dto.design;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel("机器人组件VO")
public class RobotComponentVO {

    private Integer id;

    @ApiModelProperty(value = "组件名称")
    private String component;

    @ApiModelProperty(value = "版本号")
    private String version;

    @ApiModelProperty(value = "文件名称")
    private String fileName;

    @ApiModelProperty(value = "版本说明")
    private String comment;

    @ApiModelProperty(value = "发布时间")
    private Date releaseTime;

    @ApiModelProperty(value = "状态（1 运行中，0 停止， 2 历史版本）")
    private Integer status;

    @ApiModelProperty(value = "文件路径")
    private String filePath;

    @ApiModelProperty(value = "创建人名字")
    private String createName;

    @ApiModelProperty(value = "创建人id")
    private Integer createId;

}
