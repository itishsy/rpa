package com.seebon.rpa.entity.saas.po.declare;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

@Table(name = "policy_city_alias_setting")
@ApiModel("户籍市别名配置实体")
@Data
public class PolicyCityAliasSetting {

    @ApiModelProperty("主键id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ApiModelProperty("户籍市名称")
    @Column
    private String cityName;

    @ApiModelProperty("户籍市别名")
    @Column
    private String cityAlias;

}
