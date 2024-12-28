package com.seebon.rpa.entity.vo.declare;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class MemberImportVO {
    @ApiModelProperty("人员主键id")
    private Integer id;

    @ApiModelProperty("人员证件号码")
    private String idCard;

    @ApiModelProperty("姓名")
    private String name;
}
