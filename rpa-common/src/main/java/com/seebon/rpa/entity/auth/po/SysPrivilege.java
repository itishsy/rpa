package com.seebon.rpa.entity.auth.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

/**
 * @author ZhenShijun
 * @dateTime 2022-11-29 10:38:16
 */
@ApiModel("sys_privilege")
@Table(name = "sys_privilege")
@Data
public class SysPrivilege {

    @ApiModelProperty("id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ApiModelProperty("角色id")
    @Column
    private Integer roleId;

    @ApiModelProperty("resourceId")
    @Column
    private Integer resourceId;

}
