package com.seebon.rpa.entity.saas.vo;

import com.seebon.rpa.entity.saas.DevAddrMsg;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("地区消息表")
public class DevAddrMsgVO extends DevAddrMsg {

    @ApiModelProperty("阶段（调研、配置、上线、运维）")
    private String stage;

    @ApiModelProperty("开发用户名")
    private String devUserName;

    @ApiModelProperty("测试用户名")
    private String testUserName;

    @ApiModelProperty("运维用户名")
    private String ywUserName;

    @ApiModelProperty("调研用户名")
    private String xqUserName;

    @ApiModelProperty("上线阶段（0：调研，1：配置，2：上线，3：运维）")
    private Integer stateCode;
}
