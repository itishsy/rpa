package com.seebon.rpa.entity.agent.dto.declare;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author maoxp
 */
@ApiModel("生成报盘文件条件DTO")
@Data
public class GenerateFileDTO {

    @ApiModelProperty("参保地id")
    private Integer addrId;

    @ApiModelProperty("参保地名称")
    private String addrName;

    @ApiModelProperty("组织名称")
    private String orgName;

    @ApiModelProperty("缴纳起始月")
    private String insuredDate;

    @ApiModelProperty("申报状态（0作废，1待申报，2申报中，4申报成功，5申报失败）")
    private Integer declareStatus;

    @ApiModelProperty("变更类型（1增，2减，3调基，5补缴）")
    private Integer changeType;
}
