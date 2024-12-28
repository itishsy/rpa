package com.seebon.rpa.entity.agent.vo.declare.employee;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AccfundAddrVO {
    @ApiModelProperty("参保地id")
    private Integer addrId;

    @ApiModelProperty("参保地名称")
    private String addrName;

    @ApiModelProperty("单位公积金号")
    private String compAccount;

    @ApiModelProperty("单位社保号ID")
    private Integer compAccountId;
    /**
     * 单位比例
     */
    @ApiModelProperty("单位比例")
    private String compRatio;

    /**
     * 个人比例
     */
    @ApiModelProperty("个人比例")
    private String empRatio;

    @ApiModelProperty("组织名称")
    private String orgName;
}
