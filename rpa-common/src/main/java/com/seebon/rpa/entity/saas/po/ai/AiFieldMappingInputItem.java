package com.seebon.rpa.entity.saas.po.ai;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@ApiModel("ai映射字段填值信息表")
@Table(name = "ai_field_mapping_input_item")
@Data
public class AiFieldMappingInputItem implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("id")
    protected Integer id;

    @ApiModelProperty("表ai_filed_mapping_item_base uuid")
    @Column
    private String itemBaseUuid;

    @ApiModelProperty(value = "字段填值", required = true)
    @Column
    private String fieldValue;

}
