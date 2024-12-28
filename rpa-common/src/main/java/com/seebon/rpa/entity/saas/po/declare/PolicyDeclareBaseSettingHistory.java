package com.seebon.rpa.entity.saas.po.declare;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel("人员社保增减员表")
@Table(name = "policy_declare_base_setting_history")
public class PolicyDeclareBaseSettingHistory implements Serializable {
    /**
    * 主键id
    */
    @Column
    @ApiModelProperty(value="id")
    private Integer id;

    /**
    * 表policy_declare_base_setting的uuid
    */
    @Column
    @ApiModelProperty(value="表policy_declare_base_setting的uuid")
    private String declareBaseUuid;


    /**
     * 创建人id
     */
    @Column
    @ApiModelProperty(value="创建人id")
    private int createId;

    /**
    * 版本号
    */
    @Column
    @ApiModelProperty(value="版本号")
    private String versions;

    /**
    * 版本说明
    */
    @Column
    @ApiModelProperty(value="版本说明")
    private String explainV;


    /**
     * 使用标识
     */
    @Column
    @ApiModelProperty(value="1表示使用0表示暂停使用")
    private int isUser;

    /**
    * 创建时间
    */
    @Column
    @ApiModelProperty(value="创建时间")
    private Date createTime;

    /**
    * 创建时间
    */
    @Column
    @ApiModelProperty(value="创建时间")
    private Date updateTime;


}