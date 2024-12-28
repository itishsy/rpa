package com.seebon.rpa.entity.agent.po.declare.employee;


import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.seebon.rpa.entity.Identity;
import com.seebon.rpa.utils.EasyexcelConverter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Date;

@Data
@ApiModel("人员社保增减员险种表")
@Table(name = "employee_accfund_declare_change_item_base")
@ExcelIgnoreUnannotated
public class EmployeeAccfundDeclareChangeItemBase extends Identity {


    @Column
    @ApiModelProperty("增减员uuid 对应表employee_accfund_declare_change uuid',")
    private String changeUuid;

    @Column
    @ApiModelProperty("险种code")
    private String itemCode;

    @Column
    @ApiModelProperty("险种名称")
    private String itemName;

    @Column
    @ApiModelProperty("参保状态 （0作废，1待申报，2申报中，4申报成功，5申报失败™")
    @ExcelProperty(value = "申报状态", index = 7, converter = EasyexcelConverter.class)
    @ColumnWidth(20)
    private Integer status;

    @Column
    @ApiModelProperty("投保截止月")
    private Date tbEndDate;

    @Column
    @ApiModelProperty("失败原因")
    private String failCase;

    @Column
    @ApiModelProperty("机器人执行状态0：未执行，1：已执行，2：执行成功")
    private Integer robotExecStatus;


}
