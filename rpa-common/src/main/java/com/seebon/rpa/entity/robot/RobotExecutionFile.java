package com.seebon.rpa.entity.robot;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Table(name = "robot_execution_file")
@ApiModel(value = "客户机器人执行文件")
public class RobotExecutionFile implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "主键Id")
    private Integer id;

    @ApiModelProperty(value = "执行编码")
    private String executionCode;

    @ApiModelProperty(value = "任务编码")
    private String taskCode;

    @ApiModelProperty(value = "流程编码")
    private String flowCode;

    @ApiModelProperty(value = "步骤编码")
    private String stepCode;

    @ApiModelProperty(value = "文件名")
    private String fileName;

    @ApiModelProperty(value = "文件路径")
    private String filePath;

    @ApiModelProperty(value = "是否上传(0-未上传、1-上传成功，2-上传失败）")
    private Integer status;

    @ApiModelProperty(value = "错误信息")
    private String error;

    @ApiModelProperty(value = "上传时间")
    private Date uploadTime;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
}