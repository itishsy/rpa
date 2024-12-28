package com.seebon.rpa.entity.robot.dto.design;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.seebon.rpa.entity.robot.vo.design.RobotTaskVO1;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@ApiModel("机器人执行记录")
@Data
@ExcelIgnoreUnannotated
@ContentRowHeight(20)
@HeadRowHeight(20)
public class RobotExecutionExportVO {
    @ExcelProperty(value = "应用名称", index = 0)
    @ColumnWidth(20)
    private String appName;

    @ExcelProperty(value = "流程名称", index = 1)
    @ColumnWidth(20)
    private String flowName;

    @ExcelProperty(value = "申报账户", index = 2)
    @ColumnWidth(20)
    private String accountNumber;

    @ExcelProperty(value = "执行时间", index = 3)
    @ColumnWidth(20)
    private String startTime;

    @ExcelProperty(value = "状态", index = 4)
    @ColumnWidth(20)
    private String status;

    @ExcelProperty(value = "步骤名称", index = 5)
    @ColumnWidth(20)
    private String stepName;

    @ExcelProperty(value = "原因", index = 6)
    @ColumnWidth(20)
    private String error;

    @ExcelProperty(value = "城市负责人", index = 7)
    @ColumnWidth(20)
    private String devUserName;

    @ExcelProperty(value = "运维负责人", index = 8)
    @ColumnWidth(20)
    private String ywUserName;
}
