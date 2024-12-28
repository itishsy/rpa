package com.seebon.rpa.entity.saas.po.declare;


import com.seebon.rpa.entity.Identity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
* 报盘设置数据源字段取值范围表
*/
@ApiModel(value="报盘设置数据源字段取值范围表")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name= "policy_declare_data_source_item_scope")
public class PolicyDeclareDataSourceItemScope extends Identity implements Serializable {

    @Column
    @ApiModelProperty(value="")
    private Integer id;

    @Column
    @ApiModelProperty(value="")
    private String uuid;

    /**
     * 表policy_declare_data_source_item的code
     */
    @Column
    @ApiModelProperty(value="表policy_declare_data_source_item的code")
    private String dataSourceItemCode;

    /**
     * 取值范围值
     */
    @Column
    @ApiModelProperty(value="取值范围值")
    private String scopeValue;

    /**
     * 创建人id
     */
    @Column
    @ApiModelProperty(value="创建人id")
    private Integer createId;

    /**
     * 创建时间
     */
    @Column
    @ApiModelProperty(value="创建时间")
    private Date createTime;

    /**
     * 更新人id
     */
    @Column
    @ApiModelProperty(value="更新人id")
    private Integer updateId;

    /**
     * 更新时间
     */
    @Column
    @ApiModelProperty(value="更新时间")
    private Date updateTime;

    private static final long serialVersionUID = 1L;

}