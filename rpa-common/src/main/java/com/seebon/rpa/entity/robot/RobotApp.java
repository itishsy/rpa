package com.seebon.rpa.entity.robot;

import com.seebon.rpa.entity.Identity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * 机器人应用
 */
@Data
@ApiModel(value = "机器人应用")
@Table(name = "robot_app")
public class RobotApp extends Identity {

    @Column
    @ApiModelProperty(value = "机器人编码")
    private String robotCode;

    @Column
    @ApiModelProperty(value = "应用编码")
    private String appCode;

    @Column
    @ApiModelProperty(value = "应用名称")
    private String appName;

    @Column
    @ApiModelProperty(value = "应用参数")
    private String appArgs;

    @Column
    @ApiModelProperty(value = "版本号")
    private String version;

    @Column
    @ApiModelProperty(value = "版本说明")
    private String comment;

    @Column
    @ApiModelProperty(value = "状态")
    private Integer status;

    @Column
    @ApiModelProperty(value = "应用状态：0调研 1配置 2调试通过 3等待数据 4验证有效 5正常运行 6修复问题")
    private Integer appStatus;

    @Column
    @ApiModelProperty(value = "上线阶段")
    private Integer onlineStatus;

    @Column
    @ApiModelProperty(value = "上下架状态")
    private Integer runStatus;

    @Column
    @ApiModelProperty(value = "网站链接")
    private String websiteLinks;

    @Column
    @ApiModelProperty(value = "下架备注")
    private String remark;

    @Column
    @ApiModelProperty(value = "应用备注")
    private String appRemark;

    @Column
    @ApiModelProperty(value = "发布时间")
    private Date releaseTime;

    @Column
    @ApiModelProperty(value = "session维持（0：否，1-是）")
    private Integer sessionKeep;

    @Column
    @ApiModelProperty(value = "并行执行（0-不可以，1-可以）")
    private Integer batchRun;
}
