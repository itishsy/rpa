package com.seebon.rpa.entity.saas.dto.system;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author ZhenShijun
 * @dateTime 2022-12-26 13:51:36
 */
@ApiModel("客户信息dto")
@Data
public class CustomerOpenDTO {

    @ApiModelProperty("公司id")
    private Integer custId;

    @ApiModelProperty("公司名称")
    private String name;

    @ApiModelProperty("对接人")
    private String contactPerson;

    @ApiModelProperty("联系手机号")
    private String phoneNumber;

    @ApiModelProperty("对接人电子邮箱")
    private String email;

    @ApiModelProperty("联系地址（机器人盒子寄件地址）")
    private String addressDetail;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("用户密码")
    private String password;

    @ApiModelProperty("客户端id")
    private String clientId;

}
