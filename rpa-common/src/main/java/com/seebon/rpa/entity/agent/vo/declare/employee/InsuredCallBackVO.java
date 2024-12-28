package com.seebon.rpa.entity.agent.vo.declare.employee;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class InsuredCallBackVO {

    @ApiModelProperty("社保参保城市")
    private List<SocialAddrVO> socialCity;


    @ApiModelProperty("公积金参保城市")
    private List<AccfundAddrVO> accfundCity;

}
