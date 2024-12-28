package com.seebon.rpa.entity.agent.dto.declare;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @author lsh
 * @dateTime 2022-04-26
 */
@Data
public class InsureByAddDTO {

    @ApiModelProperty("新增投保人请求参数")
    private AddNewApplicantDTO addNewApplicantDTO;

    @ApiModelProperty("社保参保请求参数")
    private InsureBySocial insureBySocial;

    @ApiModelProperty("公积金参保请求参数")
    private InsureByAccfund insureByAccfund;


}
