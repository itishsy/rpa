package com.seebon.rpa.entity.saas.dto.register;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@ApiModel("在册人数列表信息")
@Data
public class CustomerInsuredRegisterStatisticsDTO implements Serializable {

    @ExcelIgnore
    @ApiModelProperty("客户id")
    private Integer custId;

    @ExcelProperty(value = "客户名称", index = 0)
    @ColumnWidth(20)
    @ApiModelProperty("客户名称")
    private String customerName;

    @ExcelIgnore
    @ApiModelProperty("申报单位id")
    private Integer orgId;

    @ExcelProperty(value = "申报单位", index = 1)
    @ColumnWidth(20)
    @ApiModelProperty("申报单位")
    private String orgName;

    @ExcelProperty(value = "参保城市", index = 2)
    @ColumnWidth(20)
    @ApiModelProperty("参保城市")
    private String addrName;

    @ExcelProperty(value = "参保时间", index = 3)
    @ColumnWidth(20)
    @ApiModelProperty("参保时间")
    private String dataMonth;

    @ExcelProperty(value = "在户人数", index = 4)
    @ColumnWidth(20)
    @ApiModelProperty("在户人数")
    private Integer registerNumber;

    @ExcelIgnore
    @ApiModelProperty("社保人数")
    private List<CustomerInsuredSocialRegisterDTO> registerList;

    @ExcelIgnore
    @ApiModelProperty("公积金人数")
    private Integer accfundRegisterNumber;

    @ExcelIgnore
    @ApiModelProperty("最新更新时间")
    @JsonFormat(locale="zh",timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ExcelIgnore
    @ApiModelProperty("公积金最新更新时间")
    @JsonFormat(locale="zh",timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date accfundCreateTime;

}
