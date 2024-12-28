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
 * 公积金增减员流水表
 */
@ApiModel(value = "公积金增减员流水表")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "employee_accfund_declare_changee_process")
public class EmployeeAccfundDeclareChangeeProcess implements Serializable {
    @Column
    @ApiModelProperty(value = "")
    private Integer id;

    /**
     * 人员社保增减员uuid 对应表employee_accfund_declare_change uuid
     */
    @Column
    @ApiModelProperty(value = "人员社保增减员uuid 对应表employee_accfund_declare_change uuid")
    private String changeUuid;

    /**
     * 流水类型：0、作废，1、创建  3、导出 4、回盘 5、修改申报状态
     */
    @Column
    @ApiModelProperty(value = "流水类型：0、作废，1、创建  3、导出 4、回盘 5、修改申报状态")
    private Byte processType;

    /**
     * 申报系统code
     */
    @Column
    @ApiModelProperty(value = "申报系统code")
    private String tplTypeCode;

    /**
     * 状态变更描述
     */
    @Column
    @ApiModelProperty(value = "状态变更描述")
    private String processComment;

    @Column
    @ApiModelProperty("机器人执行批次号")
    private String robotExecCode;

    /**
     * 备注
     */
    @Column
    @ApiModelProperty(value = "备注")
    private String remarks;

    /**
     * 节点结果备注
     */
    @Column
    @ApiModelProperty(value = "节点结果备注")
    private String nodeComment;

    /**
     * 调整原因
     */
    @Column
    @ApiModelProperty(value = "调整原因")
    private String adjustReason;

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

    public static final String COL_PROCESS_TYPE = "process_type";

    public static final String COL_PROCESS_COMMENT = "process_comment";

    public static final String COL_CREATE_ID = "create_id";

    public static final String COL_CREATE_TIME = "create_time";
}