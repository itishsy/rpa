package com.seebon.rpa.entity.saas.po.system;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author hao
 * @Date 2022/7/1 17:11
 * @Version 1.0
 **/
@Data
@Table(name = "sys_declare_user")
@ApiModel("rpa申报业务用户注册表")
public class SysDeclareUser implements Serializable {
    private static final long serialVersionUID = 2553052160484760372L;

    @Id
    private Integer id;

    @Column
    @ApiModelProperty("用户注册的渠道的原始id")
    private String sourceId;

    @Column
    @ApiModelProperty("注册渠道")
    private String source;

    @Column
    @ApiModelProperty("关联customer_base")
    private Integer custId;

    @Column
    @ApiModelProperty("手机号")
    private String phone;

    @Column
    @ApiModelProperty("邮箱")
    private String email;

    @Column
    @ApiModelProperty("密码")
    private String password;

    @Column
    @ApiModelProperty("注册时间")
    private Date registerTime;

    @Column
    @ApiModelProperty("ip地址")
    private String ipAddress;
}
