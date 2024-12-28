package com.seebon.rpa.entity.robot.dto.design;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ExcelIgnoreUnannotated
@ContentRowHeight(20)
@HeadRowHeight(20)
public class RobotClientAppExportVO implements Serializable {

    @ApiModelProperty(value = "应用名称")
    @ExcelProperty(value = "应用名称",index = 0)
    @ColumnWidth(20)
    private String appName;

    /**
     * 机器标识码
     */
    @ExcelProperty(value = "所在设备",index = 1)
    @ColumnWidth(20)
    @ApiModelProperty("设备编码")
    private String machineCode;

    @ExcelProperty(value = "设备厂商",index = 2)
    @ColumnWidth(20)
    @ApiModelProperty("设备厂商")
    private String machineFactory;

    @ExcelProperty(value = "设备状态",index = 3)
    @ColumnWidth(20)
    @ApiModelProperty("设备状态")
    private String machineStatus;

    @ExcelProperty(value = "申报单位",index = 4)
    @ColumnWidth(30)
    @ApiModelProperty("申报单位")
    private String orgName;

    @ExcelProperty(value = "申报账号",index = 5)
    @ColumnWidth(30)
    @ApiModelProperty("申报账户-传参用")
    private String accNumber;

    @ExcelProperty(value = "账户状态",index = 6)
    @ColumnWidth(20)
    @ApiModelProperty("申报账户状态")
    private String taskStatus;

    @ExcelProperty(value = "停用账户原因",index = 7)
    @ColumnWidth(30)
    @ApiModelProperty("申报账户停用原因")
    private String taskComment;

    @ExcelProperty(value = "本月已完成",index = 8)
    @ColumnWidth(20)
    private Integer declaredCount;

    @ExcelProperty(value = "今日已完成",index = 9)
    @ColumnWidth(20)
    private Integer todayDeclaredCount;

    @ExcelProperty(value = "本月未完成",index = 10)
    @ColumnWidth(20)
    private Integer noDeclaredCount;

    @ExcelProperty(value = "网站审核中",index = 11)
    @ColumnWidth(20)
    private Integer auditCount;

    @ExcelProperty(value = "执行计划",index = 12)
    @ColumnWidth(30)
    @ApiModelProperty("执行计划")
    private String execPlant;

    @ExcelProperty(value = "是否存在自定义计划",index = 13)
    @ColumnWidth(30)
    @ApiModelProperty("是否存在自定义执行计划流程")
    private String haveCustom;

    @ApiModelProperty("业务类型，1：社保，2：公积金")
    private Integer businessType;

    @ExcelProperty(value = "今日执行次数",index = 14)
    @ColumnWidth(20)
    private Integer execCount;

}
