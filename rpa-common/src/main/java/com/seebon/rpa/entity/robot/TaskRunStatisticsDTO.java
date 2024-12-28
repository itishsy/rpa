package com.seebon.rpa.entity.robot;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.ContentStyle;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel("管理者桌面-任务运行状态")
@Data
@ExcelIgnoreUnannotated
@ContentRowHeight(20)
@HeadRowHeight(20)
@ContentStyle(wrapped=true)
public class TaskRunStatisticsDTO implements Serializable {

    @ExcelProperty(value="城市")
    @ApiModelProperty(value = "城市")
    private String addrName;

    @ExcelProperty(value="【月】总等待时长")
    @ApiModelProperty(value = "【月】总等待时长")
    private Integer totalDispatchTime;

    @ExcelProperty(value="【月】平均等待时长")
    @ApiModelProperty(value = "【月】平均等待时长")
    private Integer avgDispatchTime;

    @ExcelProperty(value="【月】任务执行总时长")
    @ApiModelProperty(value = "【月】任务执行总时长")
    private Integer totalFinishTime;

    @ExcelProperty(value="【月】平均任务执行时长")
    @ApiModelProperty(value = "【月】平均任务执行时长")
    private Integer avgFinishTime;

    @ExcelProperty(value="【月】机器人运行准确率")
    @ApiModelProperty(value = "【月】机器人运行准确率")
    private String successRate;

    @ExcelProperty(value="在册自然人数")
    @ApiModelProperty(value = "在册自然人数")
    private Integer registerNumber;

}
