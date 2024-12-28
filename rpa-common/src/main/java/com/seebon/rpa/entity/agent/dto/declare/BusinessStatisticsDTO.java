package com.seebon.rpa.entity.agent.dto.declare;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("业务查询统计实体DTO")
@Data
public class BusinessStatisticsDTO {

    @ExcelProperty({"申报时间"})
    @ApiModelProperty("申报时间")
    public String submitMonth;

    @ExcelIgnore
    @ApiModelProperty("客户id")
    public Integer customerId;

    @ExcelProperty({"客户名称"})
    @ApiModelProperty("客户名称")
    public String customerName;

    @ExcelProperty({"设备编号"})
    @ApiModelProperty("设备编号")
    public String machineName;

    @ExcelIgnore
    @ApiModelProperty("参保城市id")
    public Integer addrId;

    @ExcelProperty({"参保城市"})
    @ApiModelProperty("参保城市")
    public String addrName;

    @ExcelProperty({"业务类型"})
    @ApiModelProperty("业务类型")
    public String businessName;

    @ExcelIgnore
    @ApiModelProperty("申报账户公司id")
    public Integer baseId;

    @ExcelProperty({"申报账户"})
    @ApiModelProperty("申报账户")
    public String companyAccount;

    @ExcelIgnore
    @ApiModelProperty("申报类型code")
    public Integer changeType;

    @ExcelProperty({"申报类型"})
    @ApiModelProperty("申报类型")
    public String changeTypeName;

    @ExcelProperty({"校验失败", "人数"})
    @ApiModelProperty("校验失败人数")
    public Integer validateFailedNumber;

    @ExcelProperty({"校验失败", "次数"})
    @ApiModelProperty("校验失败人次")
    public Integer validateFailedCount;

    @ExcelProperty({"待申报", "人数"})
    @ApiModelProperty("待申报人数")
    public Integer toBeDeclaredNumber;

    @ExcelProperty({"待申报", "次数"})
    @ApiModelProperty("待申报人次")
    public Integer toBeDeclaredCount;

    @ExcelProperty({"申报中", "人数"})
    @ApiModelProperty("申报中人数")
    public Integer declarationInProgressNumber;

    @ExcelProperty({"申报中", "次数"})
    @ApiModelProperty("申报中人次")
    public Integer declarationInProgressCount;

    @ExcelProperty({"申报失败", "人数"})
    @ApiModelProperty("申报失败人数")
    public Integer failedNumber;

    @ExcelProperty({"申报失败", "次数"})
    @ApiModelProperty("申报失败人次")
    public Integer failedCount;

    @ExcelProperty({"申报成功", "人数"})
    @ApiModelProperty("申报成功人数")
    public Integer successNumber;

    @ExcelProperty({"申报成功", "次数"})
    @ApiModelProperty("申报成功人次")
    public Integer successCount;

    @ExcelIgnore
    @ApiModelProperty("数据和")
    public Integer CountNumber;


}
