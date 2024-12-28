package com.seebon.rpa.entity.saas.po.ai;

import com.seebon.rpa.entity.Identity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

@ApiModel("ai字段映射主表")
@Table(name = "ai_field_mapping_base")
@Data
public class AiFieldMappingBase extends Identity {

    @ApiModelProperty("uuid")
    @Column
    private String uuid;

    @ApiModelProperty("字段合集名")
    @Column
    private String name;

    @ApiModelProperty("字段名合集json结构")
    @Column
    private String fieldJson;

}
