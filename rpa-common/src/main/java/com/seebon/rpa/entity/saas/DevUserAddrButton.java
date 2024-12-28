package com.seebon.rpa.entity.saas;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Table(name = "dev_user_addr_button")
public class DevUserAddrButton implements Serializable {
    /**
     * 主键Id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 角色编号
     */
    @Column(name = "role_code")
    private String roleCode;

    /**
     * 角色编号
     */
    @Column(name = "page_code")
    private String pageCode;
    /**
     * 按钮编码
     */
    @Column(name = "button_code")
    private String buttonCode;

    /**
     * 按钮名称
     */
    @Column(name = "button_name")
    private String buttonName;

    /**
     * 创建者Id
     */
    @Column(name = "create_id")
    private Integer createId;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    private static final long serialVersionUID = 1L;
}