package com.seebon.rpa.entity.agent.vo.declare.employee;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author lsh
 * @dataTime 2022-05-26
 */
@Data
public class InsuredEmployeeImportVO {

    /*姓名*/
    @ApiModelProperty("姓名")
    private String name;

    /*姓名*/
    @ApiModelProperty("证件号码")
    private String idCard;

    @ApiModelProperty("人员参保导出-社保")
    private List<ImportSocialsVO> socials;


    @ApiModelProperty("人员参保导出-公积金")
    private List<ImportAccfundsVO> accfunds;
}
