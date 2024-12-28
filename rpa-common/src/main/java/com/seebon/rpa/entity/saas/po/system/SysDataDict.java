package com.seebon.rpa.entity.saas.po.system;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author ZhenShijun
 * @dateTime 2022-03-31 16:22:15
 */
@Data
@Table(name = "sys_data_dict")
public class SysDataDict implements Serializable {

    /**
     * 字典ID
     */
    @Id
    @ApiModelProperty(value = "id")
    private Integer id;
    /**
     * 数据类型
     */
    @Column
    @ApiModelProperty(value = "字典的key")
    private String dataKey;
    /**
     * 字典的code
     */
    @Column
    @ApiModelProperty(value = "字典的code")
    private String dictCode;
    /**
     * 字典的name
     */
    @Column
    @ApiModelProperty(value = "字典的name")
    private String dictName;
    /**
     * 字典的顺序
     */
    @Column
    @ApiModelProperty(value = "字典顺序")
    private Integer dictDisplayOrder;
    /**
     * 字典状态
     */
    @Column
    @ApiModelProperty(value = "字典的状态 1：有效，0：无效")
    private String status;

    /**
     * 字典状态
     */
    @Column
    @ApiModelProperty(value = "扩展标记")
    private Integer flag;
    /**
     * 备注
     */
    @Column
    @ApiModelProperty(value = "备注")
    private String comment;

}
