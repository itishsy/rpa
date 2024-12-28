package com.seebon.rpa.entity.saas;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

@Data
@ApiModel("用户地区配置表")
@Table(name = "dev_user_addr")
public class DevUserAddr {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("主键Id")
    private Integer id;

    @ApiModelProperty("参保地名称")
    @Column
    private String addrName;

    @ApiModelProperty("业务类型  社保， 公积金")
    @Column
    private String businessType;

    @ApiModelProperty("阶段（开发、测试、运维）")
    @Column
    private String stage;

    @ApiModelProperty("开发用户名")
    @Column
    private String devUserName;

    @ApiModelProperty("测试用户名")
    @Column
    private String testUserName;

    @ApiModelProperty("运维用户名")
    @Column
    private String ywUserName;

    @ApiModelProperty("调研用户名")
    @Column
    private String xqUserName;
}
