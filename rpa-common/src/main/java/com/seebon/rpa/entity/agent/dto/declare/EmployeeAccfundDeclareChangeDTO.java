package com.seebon.rpa.entity.agent.dto.declare;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.ContentStyle;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.seebon.rpa.entity.agent.dto.declare.employee.EmployeeFilestoreDTO;
import com.seebon.rpa.entity.agent.po.declare.employee.EmployeeAccfundDeclareChangeSuppDetail;
import com.seebon.rpa.entity.robot.vo.RobotTrackVO;
import com.seebon.rpa.utils.EasyexcelConverter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @Author hao
 * @Date 2022/4/24 16:07
 * @Version 1.0
 **/
@Data
@ApiModel("人员公积金增减员表")
@ExcelIgnoreUnannotated
@ContentRowHeight(20)
@HeadRowHeight(20)
@ContentStyle(wrapped = true)
public class EmployeeAccfundDeclareChangeDTO {

    private Integer id;

    private String uuid;

    private Integer employeeFileStoreId;

    @ApiModelProperty("人员姓名")
    @ExcelProperty(index = 0, value = "姓名")
    @ColumnWidth(20)
    private String employeeName;

    @ApiModelProperty("组织名和公积金号拼接")
    private String companyNameAndAccountNumber;

    private String orgName; // 申报单位

    @ApiModelProperty("人员证件号码")
    @ExcelProperty(index = 1, value = "证件号码")
    @ColumnWidth(20)
    private String idCard;

    @ApiModelProperty("手机号码")
    private String telephone;

    @ApiModelProperty("公积金参保地名称")
    @ExcelProperty(index = 2, value = "参保城市")
    @ColumnWidth(20)
    private String addrName;

    @ApiModelProperty("公积金参保地id")
    private Integer addrId;

    @ApiModelProperty("变更类型（1增，2减，3调基，5补缴）")
    @ExcelProperty(index = 3, value = "申报类型", converter = EasyexcelConverter.class)
    @ColumnWidth(20)
    private Integer changeType;

    @ApiModelProperty("缴纳起始月")
    @ExcelProperty(index = 4, value = "缴纳起始月")
    @ColumnWidth(20)
    @DateTimeFormat(value = "yyyy-MM")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date insuredDate;

    private String strInsuredDate;

    @ApiModelProperty("单位公积金号")
    @Column
    private String compAccount;

    @ApiModelProperty("参保基数")
    private BigDecimal empTbBase;

    @ApiModelProperty("申报状态（0作废，1待申报，2申报中，4申报成功，5申报失败）")
    private Integer declareStatus;

    @ApiModelProperty("补充申报状态（0作废，1待申报，2申报中，4申报成功，5申报失败）")
    private Integer suppDeclareStatus;

    @ApiModelProperty("参保比例")
    @ExcelProperty(index = 6, value = "参保比例")
    @ColumnWidth(20)
    private String ratios;

    @ApiModelProperty("单位公积金比例")
    private BigDecimal compRatio;

    @ApiModelProperty("个人公积金比例")
    private BigDecimal empRatio;

    @ApiModelProperty("单位补充公积金比例")
    private BigDecimal suppCompRatio;

    @ApiModelProperty("个人补充公积金比例")
    private BigDecimal suppEmpRatio;

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty("创建人id")
    private Integer createId;

    @ExcelProperty(value = "申报状态", index = 7, converter = EasyexcelConverter.class)
    @ColumnWidth(20)
    private Integer status;

    @ApiModelProperty("创建人")
    private String createName;

    @ApiModelProperty("失败原因")
    @ExcelProperty(value = "原因", index = 8)
    @ColumnWidth(20)
    private String comment;

    @ExcelProperty(index = 5, value = "缴纳基数")
 //   @NumberFormat(roundingMode = RoundingMode.CEILING)
    @ColumnWidth(20)
    private String roundEmpTbBase;

    private String failCause;

    private String suppFailCause;

    private Integer compAccountId;

    private String cardType;

    private Integer robotExecStatus;

    private List<EmployeeAccfundDeclareChangeSuppDetail> suppDetails;

    private List<EmployeeFilestoreDTO> empFiles;

    private String annotate;

    private Integer customerId;

    private String clientName;

    private String machineCode;
    private String processComment;
    //申报时长
    private Integer declareTime;
    //1待处理，2已处理
    private Integer exportStatus;

    private Integer ignoreFlag;

    @ApiModelProperty("标记")
    @ExcelProperty(value = "标记", index = 9)
    private String ignoreFlagStr;

    private String failItemName;

    private String operationType;

    @ApiModelProperty
    @org.springframework.format.annotation.DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date submitTime;

    private List<String> operationTypes;

    private RobotTrackVO robotTrackVO;

}
