package com.seebon.rpa.entity.saas.po.ai;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * TODO
 *
 * @author zjf
 * @describe ai字段配置
 * @date 2023-08-29 17:00
 */
@Data
@Table(name = "ai_filed_set")
@ApiModel("ai字段配置")
public class AiFiledSet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer id;

    @ApiModelProperty("客户id")
    @Column
    private Integer customerId;

    @ApiModelProperty("客户名称")
    @Column
    private String customerName;

    @ApiModelProperty("字段名称")
    @Column
    private String columnName;

    @ApiModelProperty("字段中文名字")
    @Column
    private String name;

    @ApiModelProperty("字段属性 0动态字段 1固定字段 ")
    @Column
    private Integer columnProperty;

    @ApiModelProperty("字段类型 0-文本，1-下拉，2-自增长")
    @Column
    private Integer type;

    @ApiModelProperty("创建时间")
    @Column
    private Date createTime;

    @ApiModelProperty("是否删除 0 未删除 1已删除")
    @Column
    private Integer isDelete;

}
