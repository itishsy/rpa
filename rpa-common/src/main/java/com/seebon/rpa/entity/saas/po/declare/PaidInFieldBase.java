package com.seebon.rpa.entity.saas.po.declare;

import com.seebon.rpa.entity.Identity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

@ApiModel("费用信息配置主表信息")
@Table(name="paid_in_field_base")
@Data
public class PaidInFieldBase extends Identity {

    @Column
    @ApiModelProperty("uuid")
    private String uuid;

    @Column
    @ApiModelProperty("参保城市id")
    private Integer addrId;

    @Column
    @ApiModelProperty("业务类型，1--社保；2--公积金")
    private Integer businessType;

    @Column
    @ApiModelProperty("状态（1--启用；2--停用）")
    private Integer status;

}
