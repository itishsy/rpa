package com.seebon.rpa.entity.auth.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author ZhenShijun
 * @dateTime 2022-11-22 20:21:14
 */
@ApiModel("角色按钮信息PO")
@Data
@Table(name = "sys_role_button")
public class SysRoleButton {

    @Id
    @ApiModelProperty("id")
    private Integer id;

    @Column
    @ApiModelProperty("角色id")
    private Integer roleId;

    @Column
    @ApiModelProperty("按钮id")
    private Integer buttonId;

}
