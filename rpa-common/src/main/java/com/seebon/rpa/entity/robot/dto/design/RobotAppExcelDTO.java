package com.seebon.rpa.entity.robot.dto.design;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
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
 * @describe
 * @date 2024-01-10 13:55
 */
@Data
@ApiModel("RPA应用导出excel")
@ExcelIgnoreUnannotated
@ContentRowHeight(20)
@HeadRowHeight(20)
@ContentStyle(wrapped=true)
public class RobotAppExcelDTO{

    @ExcelProperty(value="应用名称")
    @ApiModelProperty(value = "应用名称")
    private String appName;

    @ExcelProperty(value="上线标记")
    @ApiModelProperty(value = "运行状态 0 下架  1 上架 2 待上线")
    private String runStatusStr;

    @ExcelProperty(value="所处状态")
    @ApiModelProperty(value = "所处状态：0:配置 1:测试 2:运维")
    private String onlineStatusStr;

    @ExcelProperty(value="应用状态")
    @ApiModelProperty(value = "应用状态：0调研 1配置 2调试通过 3等待数据 4验证有效 5正常运行 6修复问题")
    private String appStatusStr;

    @ExcelProperty(value="原因备注")
    @ApiModelProperty("原因备注")
    private String remark;

    @ExcelProperty(value="增员")
    @ApiModelProperty(value = "增员")
    private String addStr;

    @ExcelProperty(value="补缴")
    @ApiModelProperty(value = "补缴")
    private String bjStr;

    @ExcelProperty(value="减员")
    @ApiModelProperty(value = "减员")
    private String reduceStr;

    @ExcelProperty(value="调基")
    @ApiModelProperty(value = "调基")
    private String tjStr;

    @ExcelProperty(value="缴费")
    @ApiModelProperty(value = "缴费")
    private String jfStr;

    @ExcelProperty(value="在册名单")
    @ApiModelProperty(value = "在册名单")
    private String zcStr;

    @ExcelProperty(value="费用明细")
    @ApiModelProperty(value = "费用明细")
    private String fyStr;

    @ExcelProperty(value="政策通知")
    @ApiModelProperty(value = "政策通知")
    private String zctzStr;

    @ExcelProperty(value="问题流程数")
    @ApiModelProperty(value = "修复问题流程数")
    private Integer problemCount;

    @ExcelProperty(value="总流程数")
    @ApiModelProperty(value = "总流程数")
    private Integer flowCount;

    @ExcelProperty(value="当前版本状态")
    @ApiModelProperty(value = "发布状态")
    private String statusStr;

    @ExcelProperty(value="当前版本号")
    @ApiModelProperty(value = "版本号")
    private String version;

    @ExcelProperty(value="最新发布时间")
    @ApiModelProperty(value = "发布时间")
    private Date releaseTime;

    @ExcelProperty(value="备注")
    @ApiModelProperty(value = "应用备注")
    private String appRemark;

    @ExcelProperty(value="类型")
    @ApiModelProperty(value = "应用类型 凭证 申报机器人")
    private String appTypeStr;

    @ExcelProperty(value="所在省")
    @ApiModelProperty(value = "省")
    private String province;
}
