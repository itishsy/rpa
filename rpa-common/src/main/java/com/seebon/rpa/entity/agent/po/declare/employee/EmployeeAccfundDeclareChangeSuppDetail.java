package com.seebon.rpa.entity.agent.po.declare.employee;

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
 * 公积金增减员补充信息表
 */
@ApiModel(value = "公积金增减员补充信息表")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "employee_accfund_declare_change_supp_detail")
public class EmployeeAccfundDeclareChangeSuppDetail implements Serializable {
    @Column
    @ApiModelProperty(value = "")
    private Integer id;

    /**
     * 增减员uuid 对应表employee_accfund_declare_change uuid
     */
    @Column
    @ApiModelProperty(value = "增减员uuid 对应表employee_accfund_declare_change uuid")
    private String changeUuid;

    /**
     * 列名称
     */
    @Column
    @ApiModelProperty(value = "列名称")
    private String columName;

    /**
     * 列值
     */
    @Column
    @ApiModelProperty(value = "列值")
    private String columValue;

    /**
     * 创建人id
     */
    @Column
    @ApiModelProperty(value = "创建人id")
    private Integer createId;

    /**
     * 创建时间
     */
    @Column
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    private static final long serialVersionUID = 1L;

    public static final String COL_ID = "id";

    public static final String COL_CHANGE_UUID = "change_uuid";

    public static final String COL_COLUM_NAME = "colum_name";

    public static final String COL_COLUM_VALUE = "colum_value";

    public static final String COL_CREATE_ID = "create_id";

    public static final String COL_CREATE_TIME = "create_time";
}