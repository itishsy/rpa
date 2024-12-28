package com.seebon.rpa.entity.agent.dto.declare;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author hao
 * @Date 2022/6/29 17:10
 * @Version 1.0
 **/
@Data
public class RegisterRequest {

    private String phone;

    private String email;

    private String password;

    private String name;

    private String shortName;

    private String code;

    @ApiModelProperty("手机验证码用途类型")
    private String type;

    @ApiModelProperty("唯一码")
    private String stamp;
}
