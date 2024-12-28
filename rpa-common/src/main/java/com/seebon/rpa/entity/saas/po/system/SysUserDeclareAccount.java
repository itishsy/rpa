package com.seebon.rpa.entity.saas.po.system;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@ApiModel("用户申报账户权限信息")
@Data
@Table(name = "sys_user_declare_account")
public class SysUserDeclareAccount implements Serializable {

    @Id
    @ApiModelProperty(value="主键id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ApiModelProperty(value="用户id 对应sys_user id值")
    @Column
    private Integer userId;

    @ApiModelProperty(value="申报账户基础表id 对应declare_account_base id值")
    @Column
    private Integer accountBaseId;

    @ApiModelProperty(value="业务类型 1：社保，2：公积金")
    @Column
    private Integer businessType;

    @ApiModelProperty(value="申报账户")
    @Column
    private String accountNumber;

    @ApiModelProperty(value="创建id")
    @Column
    private Integer createId;

    @ApiModelProperty(value="创建时间")
    @Column
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE ,pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale="zh",timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

}
