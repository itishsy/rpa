package com.seebon.rpa.entity.agent.vo.declare.employee;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class SocialVO {

    @ApiModelProperty("个人社保账号")
    private String socialnum;

    @ApiModelProperty("参保地id")
    private Integer addrId;

    @ApiModelProperty("参保地名字")
    private String addrName;

    @ApiModelProperty("户口性质")
    private String residenceType;

    /*社保参保状态*/
    @ApiModelProperty("社保参保状态")
    private String socialStatus;


    private List<SocialStatusVO> socialStatusVO;
}
