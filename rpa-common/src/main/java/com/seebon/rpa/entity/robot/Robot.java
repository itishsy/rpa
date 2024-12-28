package com.seebon.rpa.entity.robot;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * 机器人
 */
@Data
@ApiModel(value = "机器人")
@Table(name = "robot")
public class Robot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    @ApiModelProperty(value = "编码")
    private String robotCode;


    @Column
    @ApiModelProperty(value = "名称")
    private String robotName;


    @Column
    @ApiModelProperty(value = "描述")
    private String comment;


    @Column
    @ApiModelProperty(value = "状态")
    private String status;

    @Column
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE ,pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale="zh",timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}
