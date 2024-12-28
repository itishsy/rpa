package com.seebon.rpa.entity.saas.po.declare;


import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Date;

/**
 * 参保地社保补缴规则
 */
@ApiModel(value = "参保地社保补缴规则")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "policy_social_supp_rule")
public class PolicySocialSuppRule {
    /**
     * 主键id
     */
    @Column
    @ApiModelProperty(value = "主键id")
    private Integer id;

    /**
     * 参保地id 关联表declare_addr addr_id字段
     */
    @Column
    @ApiModelProperty(value = "参保地id 关联表declare_addr addr_id字段")
    private Integer addrId;

    /**
     * 险种编码
     */
    @Column
    @ApiModelProperty(value = "险种编码")
    private String itemCode;

    /**
     * 险种名称
     */
    @Column
    @ApiModelProperty(value = "险种名称")
    private String itemName;

    /**
     * 可允许补缴类型code 字典值
     */
    @Column
    @ApiModelProperty(value = "可允许补缴类型code 字典值")
    private String allowSuppTypeCode;

    /**
     * 可允许补缴类型名称
     */
    @Column
    @ApiModelProperty(value = "可允许补缴类型名称")
    private String allowSuppTypeName;

    /**
     * 往前补缴月数
     */
    @Column
    @ApiModelProperty(value = "往前补缴月数")
    private Integer allowMonths;

    /**
     * 最早可补缴年月
     */
    @Column
    @ApiModelProperty(value = "最早可补缴年月")
    @JsonFormat(pattern = "yyyy-MM", timezone = "GMT+8")
    private Date earliestAllowYearMonth;

    /**
     * 默认不参保 1:是，0:否
     */
    @Column
    @ApiModelProperty(value = "默认不参保 1:是，0:否")
    private Integer defaultInsured;

    /**
     * 是否允许跨月补缴 1:允许，0：否
     */
    @Column
    @ApiModelProperty(value = "是否允许跨月补缴 1:允许，0：否")
    private Integer acrossMonth;

    /**
     * 创建人
     */
    @Column
    @ApiModelProperty(value = "创建人")
    private Integer createId;

    /**
     * 创建时间
     */
    @Column
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 更新人
     */
    @Column
    @ApiModelProperty(value = "更新人")
    private Integer updateId;

    /**
     * 更新时间
     */
    @Column
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    private static final long serialVersionUID = 1L;

    public static final String COL_ID = "id";

    public static final String COL_ADDR_ID = "addr_id";

    public static final String COL_ITEM_CODE = "item_code";

    public static final String COL_ITEM_NAME = "item_name";

    public static final String COL_ALLOW_SUPP_TYPE_CODE = "allow_supp_type_code";

    public static final String COL_ALLOW_SUPP_TYPE_NAME = "allow_supp_type_name";

    public static final String COL_ALLOW_MONTHS = "allow_months";

    public static final String COL_EARLIEST_ALLOW_YEAR_MONTH = "earliest_allow_year_month";

    public static final String COL_DEFAULT_INSURED = "default_insured";

    public static final String COL_ACROSS_MONTH = "across_month";

    public static final String COL_CREATE_ID = "create_id";

    public static final String COL_CREATE_TIME = "create_time";

    public static final String COL_UPDATE_ID = "update_id";

    public static final String COL_UPDATE_TIME = "update_time";
}
