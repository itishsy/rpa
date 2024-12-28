package com.seebon.rpa.entity.robot;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel(value = "任务指定盒子条件规则主表")
@Table(name = "robot_client_rule")
public class RobotClientRule implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "主键Id")
    private Integer id;

    @Column
    @ApiModelProperty(value = "客户ID")
    private Integer clientId;

    @Column
    @ApiModelProperty(value = "优先级")
    private Integer sort;

    @Column
    @ApiModelProperty(value = "状态  1-启用  2-禁用")
    private Integer status;

    @Column
    @ApiModelProperty(value = "逻辑，1：所有(并且)，2：单一(或者)")
    private Integer relation;

    @Column
    @ApiModelProperty(value = "规则描述")
    private String comment;

    @Column
    @ApiModelProperty(value = "机器标识码（分配盒子编码或执行盒子编码）")
    private String machineCode;

    @Column
    @ApiModelProperty(value = "创建人id")
    private Integer createId;

    @Column
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @Column
    @ApiModelProperty(value = "更新人id")
    private Integer updateId;

    @Column
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}
