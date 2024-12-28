package com.seebon.rpa.entity.agent.dto.declare;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.seebon.rpa.entity.agent.po.declare.employee.EmployeeSocialDeclareChangeItemBase;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@ApiModel("增减员异常信息")
@Data
public class DeclareFailResultDTO implements Serializable {

    @ExcelProperty(value = "序号",index = 0)
    @ColumnWidth(10)
    private Integer index;

    @ExcelIgnore
    @ApiModelProperty("记录id")
    private Integer id;

    @ExcelIgnore
    @ApiModelProperty("记录uuid")
    private String uuid;

    @ExcelIgnore
    @ApiModelProperty("客户id")
    private Integer custId;

    @ExcelProperty(value = "姓名",index = 1)
    @ColumnWidth(20)
    @ApiModelProperty("姓名")
    private String empName;

    @ExcelProperty(value = "证件号码",index = 2)
    @ColumnWidth(25)
    @ApiModelProperty("证件号码")
    private String idCard;

    @ExcelProperty(value = "参保城市",index = 3)
    @ColumnWidth(20)
    @ApiModelProperty("参保城市")
    private String addrName;

    @ExcelIgnore
    @ApiModelProperty("业务类型")
    private Integer businessType;

    @ExcelProperty(value = "业务类型",index = 4)
    @ColumnWidth(15)
    @ApiModelProperty("业务类型名称")
    private String businessTypeName;

    @ExcelProperty(value = "申报类型",index = 5)
    @ColumnWidth(15)
    @ApiModelProperty("申报类型")
    private String declareType;

    @ExcelProperty(value = "参保时间",index = 6)
    @ColumnWidth(15)
    @ApiModelProperty("参保时间")
    private String insuredDate;

    @ExcelProperty(value = "参保险种",index = 7)
    @ColumnWidth(25)
    @ApiModelProperty("参保险种")
    private String productName;

    @ExcelProperty(value = "申报状态",index = 8)
    @ColumnWidth(15)
    @ApiModelProperty("申报状态")
    private String declareStatus;

    @ExcelIgnore
    @ApiModelProperty("公积金申报状态")
    private Integer status;

    @ExcelIgnore
    @ApiModelProperty("公积金单位比例")
    private String compRatio;

    @ExcelIgnore
    @ApiModelProperty("公积金个人比例")
    private String empRatio;

    @ExcelIgnore
    @ApiModelProperty("补充公积金单位比例")
    private String suppCompRatio;

    @ExcelIgnore
    @ApiModelProperty("补充公积金个人比例")
    private String suppEmpRatio;

    @ExcelIgnore
    @ApiModelProperty("补充公积金申报状态")
    private Integer suppStatus;

    @ExcelProperty(value = "原因备注",index = 9)
    @ColumnWidth(30)
    @ApiModelProperty("原因备注")
    private String failCause;

    @ExcelIgnore
    @ApiModelProperty("补充公积原因备注")
    private String suppFailCause;

    @ExcelProperty(value = "申报单位",index = 10)
    @ColumnWidth(25)
    @ApiModelProperty("申报单位")
    private String orgName;

    @ExcelProperty(value = "申报账户",index = 11)
    @ColumnWidth(20)
    @ApiModelProperty("申报账户")
    private String accountNumber;

    @ExcelIgnore
    @ApiModelProperty("申报单位id")
    private Integer accountBaseId;

    @ExcelIgnore
    @ApiModelProperty("社保险种")
    List<EmployeeSocialDeclareChangeItemBase> itemBaseList;

}
