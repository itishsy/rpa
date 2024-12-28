package com.seebon.rpa.entity.saas.dto.openApi;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author ZhenShijun
 * @dateTime 2022-09-27 16:59:10
 */
@ApiModel("参保城市业务申报类型DTO")
@Data
public class AddrDeclareTypeDTO {

    @ApiModelProperty("参保城市名称")
    private String addrName;

    @ApiModelProperty("业务类型")
    private Integer businessType;

    @ApiModelProperty("业务类型名称")
    private String businessTypeName;

    @ApiModelProperty("增补是否合并申报，0：不合并申报 1：合并申报")
    private String declareMergeTag;

    @ApiModelProperty("参保城市业务申报类型")
    private List<DeclareTypeDTO> declareTypes;

}
