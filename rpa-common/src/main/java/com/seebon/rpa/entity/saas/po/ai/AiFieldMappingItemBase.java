package com.seebon.rpa.entity.saas.po.ai;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@ApiModel("ai映射字段主表")
@Table(name = "ai_field_mapping_item_base")
@Data
public class AiFieldMappingItemBase implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer id;

    @ApiModelProperty("uuid")
    @Column
    private String uuid;

    @ApiModelProperty("表ai_field_mapping_base uuid")
    @Column
    private String baseUuid;

    @ApiModelProperty("映射字段原值")
    @Column
    private String fieldValue;

    @ApiModelProperty("状态，1：启用，0：禁用")
    @Column
    private Integer status;

}
