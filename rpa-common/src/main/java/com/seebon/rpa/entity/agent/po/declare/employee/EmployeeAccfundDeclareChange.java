package com.seebon.rpa.entity.agent.po.declare.employee;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.seebon.rpa.entity.Identity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Table(name = "employee_accfund_declare_change")
@ApiModel("人员公积金增减员表")
public class EmployeeAccfundDeclareChange extends Identity {


    @Column(name = "uuid")
    @ApiModelProperty("uuid")
    private String uuid;

    /**
     * 人员id
     */
    @Column(name = "employee_id")
    @ApiModelProperty("人员id")
    private Integer employeeId;

    /**
     * 人员姓名
     */
    @Column(name = "employee_name")
    @ApiModelProperty("姓名")
    private String employeeName;

    /**
     * 人员证件号码
     */
    @Column(name = "id_card")
    @ApiModelProperty("证件号码")
    private String idCard;

    /**
     * 公积金参保地id
     */
    @Column(name = "addr_id")
    @ApiModelProperty("公积金参保地id")
    private Integer addrId;

    /**
     * 公积金参保地名称
     */
    @Column(name = "addr_name")
    @ApiModelProperty("公积金参保地名称")
    private String addrName;

    /**
     * 客户id
     */
    @Column(name = "customer_id")
    @ApiModelProperty("客户id")
    private Integer customerId;

    /**
     * 单位公积金号id 关联declare_account_item id
     */
    @Column(name = "comp_account_id")
    @ApiModelProperty("单位公积金号id 关联declare_account_item id")
    private Integer compAccountId;

    /**
     * 单位公积金号
     */
    @Column(name = "comp_account")
    @ApiModelProperty("单位公积金号")
    private String compAccount;

    /**
     * 变更类型（1增，2减，3调基，5补缴）
     */
    @Column(name = "change_type")
    @ApiModelProperty("变更类型（1增，2减，3调基，5补缴）")
    private Integer changeType;

    /**
     * 参保基数
     */
    @Column(name = "emp_tb_base")
    @ApiModelProperty("参保基数")
    private BigDecimal empTbBase;

    /**
     * 单位比例
     */
    @Column(name = "comp_ratio")
    @ApiModelProperty("单位比例")
    private BigDecimal compRatio;

    /**
     * 个人比例
     */
    @Column(name = "emp_ratio")
    @ApiModelProperty("个人比例")
    private BigDecimal empRatio;

    /**
     * 补充单位比例
     */
    @Column(name = "supp_comp_ratio")
    @ApiModelProperty("补充单位比例")
    private BigDecimal suppCompRatio;

    /**
     * 补充个人比例
     */
    @Column(name = "supp_emp_ratio")
    @ApiModelProperty("补充个人比例")
    private BigDecimal suppEmpRatio;

    /**
     * 缴纳起始月
     */
    @Column(name = "insured_date")
    @ApiModelProperty("缴纳起始月")
    private Date insuredDate;

    /**
     * 申报状态（0作废，1待申报，2申报中，4申报成功，5申报失败）
     */
    @Column(name = "declare_status")
    @ApiModelProperty("申报状态（0作废，1待申报，2申报中，4申报成功，5申报失败）")
    private Integer declareStatus;

    /**
     * 补充申报状态（0作废，1待申报，2申报中，4申报成功，5申报失败）
     */
    @Column(name = "supp_declare_status")
    @ApiModelProperty("补充申报状态（0作废，1待申报，2申报中，4申报成功，5申报失败）")
    private Integer suppDeclareStatus;

    /**
     * 申报失败原因
     */
    @Column(name = "fail_cause")
    @ApiModelProperty("申报失败原因")
    private String failCause;

    /**
     * 补充公积金申报失败原因
     */
    @Column(name = "supp_fail_cause")
    @ApiModelProperty("申报失败原因")
    private String suppFailCause;

    @Column
    @ApiModelProperty("是否需要同步状态 1：是，0：否")
    private Integer needPushStatus;

    /**
     * 上一条change_id
     */
    @Column(name = "last_change_id")
    @ApiModelProperty("上一条change_id")
    private Integer lastChangeId;


    @Column(name = "warn_notice")
    @ApiModelProperty("申报失败是否已通知预警，1：是，0：否")
    private Integer warnNotice = 0;

    /**
     * 是否已经删除
     */
    @Column(name = "deteled")
    @ApiModelProperty("是否已经删除")
    private Integer deteled;

    /**
     * 备注/减员原因
     */
    @Column(name = "comment")
    @ApiModelProperty("备注/减员原因")
    private String comment;

    /**
     * 数据形成方式（1：页面录入，2：批量导入，3：接口录入，4：钉钉接入）
     */
    @Column(name = "create_type")
    @ApiModelProperty("数据形成方式（1：页面录入，2：批量导入，3：接口录入，4：钉钉接入）")
    private String createType;

    /**
     * 投保结束年月
     */
    @Column(name = "tb_end_date")
    @ApiModelProperty("投保结束年月")
    private Date tbEndDate;


    /**
     * 失败原因
     */
    @Column(name = "fail_reason")
    @ApiModelProperty("失败原因")
    private String failReason;


    @Column
    @ApiModelProperty("对应mongoDB数据的id")
    private String mongoId;

    @Column
    @ApiModelProperty("机器人执行状态0：未执行，1：已执行，2：执行成功")
    private Integer robotExecStatus;

    @Column
    @ApiModelProperty("申报数据操作类型")
    private String operationType;

    @Column
    @ApiModelProperty("原始申报信息")
    private String declareInfo;

    @Column
    @ApiModelProperty("提交人id")
    private Integer submitId;

    @Column
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE ,pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale="zh",timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("提交时间")
    private Date submitTime;

    @Column
    @ApiModelProperty("忽略失败标记，1：忽略，其他：不忽略")
    private Integer ignoreFlag;

    @Column
    @ApiModelProperty("是否已核验可申报，1：是，0：否")
    private Integer validateFlag;

}
