package com.seebon.rpa.entity.robot;

import com.seebon.rpa.entity.Identity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Date;

@ApiModel("机器人组件")
@Data
@Table(name = "robot_component")
public class RobotClientComponent extends Identity {

    @Column
    @ApiModelProperty(value = "客户ID")
    private Integer clientId;

    @Column
    @ApiModelProperty(value = "机器标识码")
    private String machineCode;
    /**
     * 组件名称
     */
    @Column
    @ApiModelProperty(value = "组件名称")
    private String component;

    /**
     * 版本号
     */
    @Column
    @ApiModelProperty(value = "版本号")
    private String version;


    /**
     * 创建时间
     */
    @Column
    @ApiModelProperty(value = "创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新时间
     */
    @Column
    @ApiModelProperty(value = "更新时间")
    private Date loginTime;
}
