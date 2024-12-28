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
public class RobotComponent extends Identity {

    /**
     * 组件名称
     */
    @Column
    @ApiModelProperty(value = "组件名称")
    private String component;

    /**
     * 文件名称
     */
    @Column
    @ApiModelProperty(value = "文件名称")
    private String fileName;

    /**
     * 版本号
     */
    @Column
    @ApiModelProperty(value = "版本号")
    private String version;

    /**
     * 版本说明
     */
    @Column
    @ApiModelProperty(value = "版本说明")
    private String comment;

    /**
     * 发布时间
     */
    @Column
    @ApiModelProperty(value = "发布时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date releaseTime;

    /**
     * 状态
     */
    @Column
    @ApiModelProperty(value = "状态（1 运行中，0 停止， 2 历史版本）")
    private Integer status;

    /**
     * 创建人名字
     */
    @Column
    @ApiModelProperty(value = "创建人名字")
    private String createName;

    /**
     * 创建人id
     */
    @Column
    @ApiModelProperty(value = "创建人id")
    private Integer createId;
}
