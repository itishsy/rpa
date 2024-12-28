package com.seebon.rpa.entity.saas.po.declare;

import com.seebon.rpa.entity.Identity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

@ApiModel("定制报盘设置基础信息")
@Data
@Table(name = "policy_declare_base_custom_made_setting")
public class PolicyDeclareBaseCustomMadeSetting extends Identity {

    @ApiModelProperty("uuid")
    @Column
    private String uuid;

    @ApiModelProperty("表policy_declare_base_setting的uuid")
    @Column
    private String declareBaseUuid;

    @ApiModelProperty("表declare_account_base id")
    @Column
    private String declareBaseId;

    @Column
    @ApiModelProperty("有效状态（1：有效，0：失效）")
    private Integer status;

}
