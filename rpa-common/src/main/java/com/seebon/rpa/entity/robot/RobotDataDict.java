package com.seebon.rpa.entity.robot;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * 机器人数据字典
 */
@Data
@ApiModel(value = "机器人数据字典")
@Table(name = "robot_data_dict")
public class RobotDataDict {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    @ApiModelProperty(value = "字典Key")
    private String dictKey;


    @Column
    @ApiModelProperty(value = "字典编码")
    private String dictCode;


    @Column
    @ApiModelProperty(value = "字典名称")
    private String dictName;

    @Id
    @ApiModelProperty(value = "显示顺序")
    private Integer displayOrder;

    @Column
    @ApiModelProperty(value = "父级字典Key")
    private String parentKey;

    @Column
    @ApiModelProperty(value = "状态")
    private String status;

    @Column
    @ApiModelProperty(value = "说明")
    private String comment;
}
