package com.seebon.rpa.entity.agent.vo.declare.employee;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 查询个人投保详情
 *
 * @author lsh
 * @datatime 2022-05-09
 */
@Data
public class InsuredParticularsVO {

    @ApiModelProperty("员工姓名")
    private String name;

    @ApiModelProperty("员工身份号")
    private String idCard;

    @ApiModelProperty("性别")
    private String sex;

    @ApiModelProperty("户口性质")
    private String residenceType;

    @ApiModelProperty("户籍所在省")
    private String domicileAddrProvinceName;

    @ApiModelProperty("户籍所在市")
    private String domicileAddrCityName;

    @ApiModelProperty("社保当前状态详情")
    private List<SocialVO> socialVOS;

    @ApiModelProperty("公积金当前状态详情")
    private List<AccfundVO> accfundVOS;

    @ApiModelProperty("社保详情对象")
    private List<InsuredSocialVO> socialVO;

    @ApiModelProperty("公积金详情对象")
    private List<InsuredAccfundVO> accfundVO;
}
