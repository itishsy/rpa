package com.seebon.rpa.entity.robot;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@Data
@ApiModel(value = "客户指令")
@Table(name = "robot_customer_command")
public class CustomerCommand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer id;

    @Column
    @ApiModelProperty(value = "客户ID")
    private Integer clientId;

    @Column
    @ApiModelProperty(value = "手动执行批次号")
    private String uuid;

    @Column
    @ApiModelProperty(value = "指令参数")
    private String args;

    @Column
    @ApiModelProperty(value = "指令 component/app")
    private String command;

    @Column
    @ApiModelProperty(value = "备注")
    private String remark;

    @Column
    @ApiModelProperty(value = "同步状态  0-未同步  1-已同步 2-同步失败")
    private Integer status;

    @Column
    @ApiModelProperty(value = "失败原因")
    private String failReason;

    @Column
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date syncTime;

    @Column
    @ApiModelProperty(value = "创建人id")
    private Integer createId;

    @Column
    @ApiModelProperty(value = "创建人名称")
    private String createName;

    @Column
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @Column
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}
