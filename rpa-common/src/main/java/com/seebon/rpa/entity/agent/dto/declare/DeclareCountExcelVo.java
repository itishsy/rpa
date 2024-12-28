package com.seebon.rpa.entity.agent.dto.declare;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.ContentStyle;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * TODO
 *
 * @author zjf
 * @describe 申报汇总信息
 * @date 2023-07-06 13:54
 */
@Data
@ApiModel("申报汇总信息")
@ExcelIgnoreUnannotated
@ContentRowHeight(20)
@HeadRowHeight(20)
@ContentStyle(wrapped = true)
public class DeclareCountExcelVo{


    @ExcelProperty(index = 0, value = "城市")
    @ApiModelProperty("地区名字")
    private String addrName;

    @ExcelProperty(index = 1, value = "业务类型")
    @ApiModelProperty("业务类型")
    private String businessType;

    @ExcelProperty(index = 2, value = "客户")
    @ApiModelProperty("客户")
    private String clientName;

    @ExcelProperty(index = 3, value = "申报期")
    @ApiModelProperty("申报期")
    private String declareTime;

    @ExcelProperty(index = 4, value = "当日待申报人数")
    @ApiModelProperty("当日待申报人数")
    private Integer todayPendDeclare;

    @ExcelProperty(index = 5, value = "待申报超过3日")
    @ApiModelProperty("待申报超过3日")
    private Integer pendDeclareThree;

    @ExcelProperty(index = 6, value = "待申报超过5日")
    @ApiModelProperty("待申报超过5日")
    private Integer pendDeclareFive;

    @ExcelProperty(index = 7, value = "当日申报中人数")
    @ApiModelProperty("当日申报中人数")
    private Integer todayDeclareProgress;

    @ExcelProperty(index = 8, value = "申报中超1日")
    @ApiModelProperty("申报中超1日")
    private Integer progressDeclareOne;

    @ExcelProperty(index = 9, value = "当日完成申报数据")
    @ApiModelProperty("当日完成申报数据")
    private Integer todayCompleteDeclare;

    @ExcelProperty(index = 10, value = "本月完成申报数据")
    @ApiModelProperty("本月完成申报数据")
    private Integer monthCompleteDeclare;

    @ExcelProperty(index = 11, value = "本月申报失败数据")
    @ApiModelProperty("本月申报失败数据")
    private Integer monthFailDeclare;
}
