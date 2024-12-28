package com.seebon.rpa.entity.agent.vo.declare.employee;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author lsh
 * @dateTime 2022-04-20
 */
@ApiModel(value = "查询员工参保VO")
@Data
public class InsuredDetailsVo {
    @ApiModelProperty("人员主键id")
    private Integer id;
    /*证件号码*/
    @ApiModelProperty("证件号码")
    private String idCard;

    /*姓名*/
    @ApiModelProperty("姓名")
    private String name;

    /*性别*/
    @ApiModelProperty("性别")
    private String sex;

    /*手机号*/
    @ApiModelProperty("手机号")
    private String telephone;

    @ApiModelProperty("户口性质")
    private String residenceType;

    @ApiModelProperty("户籍所在省")
    private String domicileAddrProvinceName;

    @ApiModelProperty("户籍所在市")
    private String domicileAddrCityName;


    /*社保参保状态*/
    @ApiModelProperty("社保参保状态")
    private String socialStatus;

    /*公积金参保状态*/
    @ApiModelProperty("公积金参保状态")
    private String accfundStatus;

    @ApiModelProperty(value = "查询总页数,内部处理参数，不对外暴露", required = true, hidden = true)
    private Integer countNum;
}
