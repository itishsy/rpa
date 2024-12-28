package com.seebon.rpa.entity.agent.po.declare.employee;


import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.seebon.rpa.entity.Identity;
import com.seebon.rpa.utils.EasyexcelConverter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel("人员社保增减员表")
@Table(name = "employee_social_declare_change")
@ExcelIgnoreUnannotated
@ContentRowHeight(20)
@HeadRowHeight(20)
public class EmployeeSocialDeclareChange extends Identity {

    @Column
    @ApiModelProperty("")
    private String uuid;

    @Column
    @ApiModelProperty("人员id")
    private Integer employeeId;

    @Column
    @ApiModelProperty("人员姓名")
    @ExcelProperty(index = 0, value = "姓名")
    @ColumnWidth(20)
    private String employeeName;

    @Column
    @ApiModelProperty("人员证件号码")
    @ExcelProperty(index = 1, value = "证件号码")
    @ColumnWidth(20)
    private String idCard;

    @Column
    @ApiModelProperty("方案id")
    private Integer productId;

    @Column
    @ApiModelProperty("方案名称")
    private String productName;

    @Column
    @ApiModelProperty("社保地id")
    private Integer addrId;

    @Column
    @ApiModelProperty("社保地名称")
    @ExcelProperty(index = 2, value = "参保城市")
    @ColumnWidth(20)
    private String addrName;

    @Column
    @ApiModelProperty("客户id")
    private Integer customerId;

    @Column
    @ApiModelProperty("单位社保号id 关联declare_account_item id")
    private Integer compAccountId;

    @Column
    @ApiModelProperty("单位社保号")
    private String compAccount;

    @Column
    @ApiModelProperty("变更类型（1增，2减，3调基，5补缴）")
    @ExcelProperty(index = 3, value = "申报类型", converter = EasyexcelConverter.class)
    @ColumnWidth(20)
    private Integer changeType;

    @Column
    @ApiModelProperty("参保基数")
    private BigDecimal empTbBase;

    @Column
    @ApiModelProperty("缴纳起始月")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @ExcelProperty(index = 5, value = "缴纳起始月")
    @ColumnWidth(20)
    @DateTimeFormat(value = "yyyy-MM")
    private Date insuredDate;

    @Column
    @ApiModelProperty("申报状态（0作废，1待申报，2申报中，3部分成功，4申报成功，5申报失败）")
    private Integer declareStatus;

    @Column
    @ApiModelProperty("申报失败原因")
    private String failCause;

    @Column
    @ApiModelProperty("是否需要同步状态 1：是，0：否")
    private Integer needPushStatus;

    @Column
    @ApiModelProperty("上一条change_id")
    private Integer lastChangeId;

    @Column
    @ApiModelProperty("是否已经删除")
    private Integer deteled;

    @Column
    @ApiModelProperty("备注/减员原因")
    private String comment;

    @Column
    @ApiModelProperty("数据形成方式（1：页面录入，2：批量导入，3：接口录入，4：钉钉接入）")
    private String createType;

    @Column
    @ApiModelProperty("投保结束年月")
    private Date tbEndDate;

    @Column
    @ApiModelProperty("失败原因")
    private String failReason;

    @Transient
    @ApiModelProperty("同一申报状态的险种名字拼接")
    @ExcelProperty(index = 4, value = "参保险种")
    @ColumnWidth(20)
    private String sameStatusName;

    @Column
    @ApiModelProperty("对应mongoDB数据的id")
    private String mongoId;

    @Column
    @ApiModelProperty("原始申报信息")
    private String declareInfo;


    @Column
    @ApiModelProperty("提交人id")
    private Integer submitId;

    @Column
    @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE ,pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale="zh",timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("提交时间")
    private Date submitTime;

}
