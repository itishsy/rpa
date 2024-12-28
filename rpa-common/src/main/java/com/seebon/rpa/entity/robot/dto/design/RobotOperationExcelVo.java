package com.seebon.rpa.entity.robot.dto.design;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.ContentStyle;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * TODO
 *
 * @author zjf
 * @describe 运维监控错误信息实体
 * @date 2023-06-28 11:04
 */
@Data
@ApiModel("运维监控错误信息表")
@ExcelIgnoreUnannotated
@ContentRowHeight(20)
@HeadRowHeight(20)
@ContentStyle(wrapped = true)
public class RobotOperationExcelVo{

    @ApiModelProperty("设备编号")
    @ExcelProperty(index = 0, value = "设备编号")
    @ColumnWidth(20)
    private String machineCode;

    @ApiModelProperty("任务名称")
    @ExcelProperty(index = 1, value = "任务名称")
    @ColumnWidth(20)
    private String taskName;

    @ApiModelProperty("参保城市")
    @ExcelProperty(index = 2, value = "参保城市")
    @ColumnWidth(20)
    private String addrName;

    @ApiModelProperty("业务")
    @ExcelProperty(index = 3, value = "业务")
    @ColumnWidth(20)
    private String serviceName;

    @ApiModelProperty("流程名称")
    @ExcelProperty(index = 4, value = "流程名称")
    @ColumnWidth(20)
    private String flowName;

    @ApiModelProperty("申报网站地址")
    @ExcelProperty(index = 5, value = "申报网站地址")
    @ColumnWidth(20)
    private String webAddress;

    @ApiModelProperty("上报时间")
    @ExcelProperty(index = 6, value = "上报时间")
    @ColumnWidth(20)
    private String reportTime;

    @ApiModelProperty("首次预警执行时间")
    @ExcelProperty(index = 7, value = "首次预警执行时间")
    @ColumnWidth(20)
    private Date firstWarnTime;

    @ApiModelProperty("最新预警执行时间")
    @ExcelProperty(index = 8, value = "最新预警执行时间")
    @ColumnWidth(20)
    private Date newWarnTime;

    @ApiModelProperty("失败原因")
    @ExcelProperty(index = 9, value = "失败原因")
    @ColumnWidth(20)
    private String failReason;

    @ApiModelProperty("失败代码")
    @ExcelProperty(index = 10, value = "失败代码")
    @ColumnWidth(50)
    private String failCode;

    @ApiModelProperty("预警次数")
    @ExcelProperty(index = 11, value = "预警次数")
    @ColumnWidth(20)
    private Integer warnCount;

    private String errorResult;

    private String reportStartTime;

    private String reportEndTime;
}
