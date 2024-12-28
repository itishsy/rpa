package com.seebon.rpa.entity.agent.vo.declare.employee;


import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

@Data
public class InsureWrongDataExportVO {


    @ApiModelProperty("姓名")
    @ExcelProperty({"参保人", "姓名"})
    private String name;

    @ApiModelProperty("身份证号")
    @ExcelProperty({"参保人", "身份证号"})
    private String idCard;

    @ApiModelProperty("参保险种")
    @ExcelProperty({"社保信息", "参保险种"})
    private String insureItemName;

    @ApiModelProperty("开始缴纳月")
    @ExcelProperty({"社保信息", "开始缴纳月"})
    private String insuredDateSocial;

    @ApiModelProperty("缴纳基数")
    @ExcelProperty({"社保信息", "缴纳基数"})
    private String empTbBaseSocial;

    @ApiModelProperty("单位比列")
    @ExcelProperty({"公积金信息", "单位比例"})
    private String compRatio;

    @ApiModelProperty("个人比列")
    @ExcelProperty({"公积金信息", "个人比例"})
    private String empRatio;

    @ApiModelProperty("缴纳基数")
    @ExcelProperty({"公积金信息", "缴纳基数"})
    private String empTbBaseAccfund;


    @ApiModelProperty("开始缴纳月")
    @ExcelProperty({"公积金信息", "开始缴纳月"})
    private String insuredDateAccfund;

    @ApiModelProperty("动态字段")
    private Map<String, Object> dynamics;


    @ApiModelProperty("失败原因")
    @ExcelProperty({"失败原因"})
    private String failCase;
}
