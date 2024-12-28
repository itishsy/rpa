package com.seebon.rpa.entity.agent.po.ai;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

/**
 * TODO
 *
 * @author zjf
 * @describe
 * @date 2023-09-27 17:01
 */
@Data
@Table(name = "ai_constant_column")
@ApiModel("ai固定字段配置")
public class AiConstantColumn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer id;

    @ApiModelProperty("类型 1 社保 2 公积金")
    @Column
    private Integer type;

    @ApiModelProperty("字段名称")
    @Column
    private String columnName;

    @ApiModelProperty("字段类型")
    @Column
    private String columnType;

    @ApiModelProperty("创建时间")
    @Column
    private String createTime;
}
