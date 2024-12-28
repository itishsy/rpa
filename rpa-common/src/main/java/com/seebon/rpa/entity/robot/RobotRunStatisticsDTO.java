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

@ApiModel("管理者桌面-机器人运行状态")
@Data
@ExcelIgnoreUnannotated
@ContentRowHeight(20)
@HeadRowHeight(20)
@ContentStyle(wrapped=true)
public class RobotRunStatisticsDTO  implements Serializable {

    @ApiModelProperty(value = "客户id")
    private Integer clientId;

    @ApiModelProperty(value = "客户名称")
    private String customerName;

    @ExcelProperty(value="设备编号")
    @ApiModelProperty(value = "设备编号")
    private String machineCode;

    @ExcelProperty(value="【月】机器人离线次数")
    @ApiModelProperty(value = "【月】机器人离线次数")
    private Integer robotMonthOfflineCount;

    @ExcelProperty(value="【月】离线时长")
    @ApiModelProperty(value = "【月】离线时长")
    private Integer robotMonthOfflineTime;

    @ExcelProperty(value="【月】RPA运行时长")
    @ApiModelProperty(value = "【月】RPA运行时长")
    private Integer robotMonthRunTime;

    @ExcelProperty(value="【总】机器人离线次数")
    @ApiModelProperty(value = "【总】机器人离线次数")
    private Integer robotOfflineCount;

    @ExcelProperty(value="【总】离线时长")
    @ApiModelProperty(value = "【总】离线时长")
    private Integer robotOfflineTime;

    @ExcelProperty(value="【总】RPA运行时长")
    @ApiModelProperty(value = "【总】RPA运行时长")
    private Integer robotRunTime;

}
