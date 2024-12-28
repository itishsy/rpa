package com.seebon.rpa.entity.robot;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Table(name = "robot_app_ca")
public class RobotAppCa implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "主键Id")
    private Integer id;

    @Column
    @ApiModelProperty(value = "应用编码")
    private String appCode;

    @Column
    @ApiModelProperty(value = "申报系统 字典 10007,10008")
    private String declareSystem;

    @Column
    @ApiModelProperty(value = "执行文件名称")
    private String fileName;

    @Column
    @ApiModelProperty(value = "申报账户，逗号隔开")
    private String declareAccount;

    @Column
    @ApiModelProperty(value = "同步机器标识码，逗号隔开")
    private String syncMachineCode;

    @Column
    @ApiModelProperty(value = "备注")
    private String remark;

    @Column
    @ApiModelProperty(value = "同步时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date syncTime;

    @Column
    @ApiModelProperty(value = "启用状态：0-禁用，1-启用")
    private Integer disabled;

    @Column
    @ApiModelProperty(value = "创建者Id")
    private Integer createId;

    @Column
    @ApiModelProperty(value = "创建者名称")
    private String createName;

    @Column
    @ApiModelProperty(value = "创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @Column
    @ApiModelProperty(value = "更新时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}