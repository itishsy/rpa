package com.seebon.rpa.entity.saas.dto.system;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author ZhenShijun
 * @dateTime 2022-11-22 18:35:13
 */
@ApiModel("客户信息dto")
@Data
public class CustomerAddDTO {

    @ApiModelProperty("客户id")
    private Integer id;

    @ApiModelProperty("公司名称")
    private String name;

    @ApiModelProperty("公司名称简称")
    private String shortName;

    @ApiModelProperty("平台方")
    private String platform;

   /* @ApiModelProperty("用户id")
    private Integer userId;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("用户姓名")
    private String userRealName;

    @ApiModelProperty("用户密码")
    private String password;*/

    @ApiModelProperty("企业地址")
    private String custAddress;

    @ApiModelProperty("企业对接人")
    private String contactPerson;

    @ApiModelProperty("对接人联系手机号")
    private String phoneNumber;

    @ApiModelProperty("企业性质")
    private String nature;

    @ApiModelProperty("所属行业")
    private String industry;

}
