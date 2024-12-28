package com.seebon.rpa.entity.agent.dto.declare;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.seebon.rpa.entity.agent.po.declare.employee.EmployeeSocialDeclareChangeItemBase;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DeclareRenewalDTO implements Serializable {

    @ExcelIgnore
    @ApiModelProperty("记录id")
    private Integer id;

    @ExcelIgnore
    @ApiModelProperty("记录uuid")
    private String uuid;

    @ExcelIgnore
    @ApiModelProperty("客户id")
    private Integer custId;

    @ExcelIgnore
    @ApiModelProperty("申报单位id")
    private Integer accountBaseId;

    @ExcelIgnore
    @ApiModelProperty("社保险种")
    List<EmployeeSocialDeclareChangeItemBase> itemBaseList;

    @ExcelIgnore
    @ApiModelProperty("业务类型")
    private Integer businessType;

    @ExcelProperty(value = "序号",index = 0)
    @ColumnWidth(10)
    private Integer index;

    @ExcelProperty(value = "参保城市",index = 1)
    @ColumnWidth(25)
    @ApiModelProperty("参保城市")
    private String addrName;

    @ExcelProperty(value = "业务类型",index = 2)
    @ColumnWidth(25)
    @ApiModelProperty("业务类型")
    private String businessTypeName;

    @ExcelProperty(value = "申报单位",index = 3)
    @ColumnWidth(25)
    @ApiModelProperty("申报单位")
    private String orgName;

    @ExcelProperty(value = "申报账户",index = 4)
    @ColumnWidth(20)
    @ApiModelProperty("申报账户")
    private String accountNumber;

    @ExcelProperty(value = "姓名",index = 5)
    @ColumnWidth(20)
    @ApiModelProperty("姓名")
    private String empName;

    @ExcelProperty(value = "证件号码",index = 6)
    @ColumnWidth(25)
    @ApiModelProperty("证件号码")
    private String idCard;


    @ExcelProperty(value = "备注",index = 7)
    @ColumnWidth(30)
    @ApiModelProperty("备注")
    private String failCause;

}
