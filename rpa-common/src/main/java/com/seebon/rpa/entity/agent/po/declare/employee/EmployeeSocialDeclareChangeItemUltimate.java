package com.seebon.rpa.entity.agent.po.declare.employee;


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
 * 人员社保增减员险种最终状态表
 */
@ApiModel(value = "人员社保增减员险种最终状态表")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "employee_social_declare_change_item_ultimate")
public class EmployeeSocialDeclareChangeItemUltimate extends Identity implements Serializable {


    /**
     * 人员id
     */
    @Column
    @ApiModelProperty(value = "人员id")
    private Integer employeeId;

    /**
     * 身份证号
     */
    @Column
    @ApiModelProperty(value = "身份证号")
    private String idCard;

    /**
     * 社保地id
     */
    @Column
    @ApiModelProperty(value = "社保地id")
    private Integer addrId;

    /**
     * 客户id
     */
    @Column
    @ApiModelProperty(value = "客户id")
    private Integer customerId;

    /**
     * 单位社保号id 关联declare_account_item id
     */
    @Column
    @ApiModelProperty(value = "单位社保号id 关联declare_account_item id")
    private Integer compAccountId;

    /**
     * 单位社保号
     */
    @Column
    @ApiModelProperty(value = "单位社保号")
    private String compAccount;

    /**
     * 险种code
     */
    @Column
    @ApiModelProperty(value = "险种code")
    private String itemCode;

    /**
     * 险种名称
     */
    @Column
    @ApiModelProperty(value = "险种名称")
    private String itemName;

    /**
     * 参保状态 （0作废，1待申报，2申报中，4申报成功，5申报失败）
     */
    @Column
    @ApiModelProperty(value = "参保状态 （0作废，1待申报，2申报中，4申报成功，5申报失败）")
    private Integer status;

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

    /**
     * 更新人id
     */
    @Column
    @ApiModelProperty(value = "更新人id")
    private Integer updateId;

    /**
     * 更新时间
     */
    @Column
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    private static final long serialVersionUID = 1L;

    public static final String COL_ID = "id";

    public static final String COL_EMPLOYEE_ID = "employee_id";

    public static final String COL_ID_CARD = "id_card";

    public static final String COL_ADDR_ID = "addr_id";

    public static final String COL_CUSTOMER_ID = "customer_id";

    public static final String COL_COMP_ACCOUNT_ID = "comp_account_id";

    public static final String COL_COMP_ACCOUNT = "comp_account";

    public static final String COL_ITEM_CODE = "item_code";

    public static final String COL_ITEM_NAME = "item_name";

    public static final String COL_STATUS = "status";

    public static final String COL_CREATE_ID = "create_id";

    public static final String COL_CREATE_TIME = "create_time";

    public static final String COL_UPDATE_ID = "update_id";

    public static final String COL_UPDATE_TIME = "update_time";
}