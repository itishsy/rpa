package com.seebon.rpa.entity.robot.dto.design;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.util.Date;

/**
 * @Author: tanyong
 * @Description:
 * @Date: 2023/1/6 18:04
 * @Modified By:
 */
@Data
@ApiModel("客户机器人执行明细文件信息")
public class RobotExecutionFileInfo {
    private Integer id;

    /**
     * 执行编码
     */
    @Column
    @ApiModelProperty(value = "执行编码")
    private String executionCode;

    /**
     * 流程编码
     */
    @Column
    @ApiModelProperty(value = "流程编码")
    private String flowCode;

    /**
     * 步骤编码
     */
    @Column
    @ApiModelProperty(value = "步骤编码")
    private String stepCode;

    /**
     * 步骤名称
     */
    @Column
    @ApiModelProperty(value = "步骤名称")
    private String stepName;

    /**
     * 文件id
     */
    @Column
    @ApiModelProperty(value = "文件id")
    private Integer fileId;

    /**
     * 文件名称
     */
    @Column
    @ApiModelProperty(value = "文件名称")
    private String fileName;

    /**
     * 文件地址
     */
    @Column
    @ApiModelProperty(value = "文件地址")
    private String filePath;

    /**
     * 文件完整路径
     */
    @Column
    @ApiModelProperty(value = "文件完整路径")
    private String fileFullPath;

    /**
     * 创建时间
     */
    @Column
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
}
