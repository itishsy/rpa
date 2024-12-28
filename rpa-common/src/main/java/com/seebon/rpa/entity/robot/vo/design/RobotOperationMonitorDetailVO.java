package com.seebon.rpa.entity.robot.vo.design;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.seebon.rpa.entity.robot.po.design.RobotOperationMonitorDetail;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@ExcelIgnoreUnannotated
@ApiModel("监控运维详情VO")
@Data
public class RobotOperationMonitorDetailVO extends RobotOperationMonitorDetail {

    @ApiModelProperty("原因类型名称")
    @ExcelProperty(value = "原因归类",index = 13)
    @ColumnWidth(15)
    private String handleTypeName;

    @ApiModelProperty("执行失败步骤")
    private RobotExecutionDetailMoVO detailMoVO;

    private Long minId;

    @ExcelProperty(value = "首次预警执行时间",index = 10)
    @ColumnWidth(20)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE ,pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale="zh",timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date minCreateTime;

    @ExcelProperty(value = "最新预警执行时间",index = 11)
    @ColumnWidth(20)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE ,pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale="zh",timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date maxCreateTime;

    @ExcelProperty(value = "处理状态",index = 12)
    @ColumnWidth(15)
    private String handleStatusName;


    @ApiModelProperty("应用参数值")
    private String appArgs;

    @ApiModelProperty("预警数")
    @ExcelProperty(value = "预警次数",index = 2)
    @ColumnWidth(15)
    private Integer warnCount;

    @ApiModelProperty("预警编码")
    private String warnCode;

    @ApiModelProperty("组合id")
    private String groupId;

    @ApiModelProperty("组合id集合")
    private List<Long> groupIds;

    @ApiModelProperty("服务项目")
    private String serviceItemName;
}
