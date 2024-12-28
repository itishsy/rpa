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

@ApiModel("管理者桌面-社保人员分工")
@Data
@ExcelIgnoreUnannotated
@ContentRowHeight(20)
@HeadRowHeight(20)
@ContentStyle(wrapped=true)
public class UserSocialDivideTheWorkDTO  implements Serializable {

    @ExcelProperty(value="姓名")
    @ApiModelProperty(value = "姓名")
    private String userName;

    @ExcelProperty(value="负责城市")
    @ApiModelProperty(value = "负责城市")
    private String addrNames;

    @ExcelProperty(value="社保账户数")
    @ApiModelProperty(value = "社保账户数")
    private Integer socialAccountNumber;

    @ExcelProperty(value="公积金账户数")
    @ApiModelProperty(value = "公积金账户数")
    private Integer accfundAccountNumber;

    @ExcelProperty(value="人数")
    @ApiModelProperty(value = "人数")
    private Integer registerNumber;

    @ExcelProperty(value="【月】【社保】任务量")
    @ApiModelProperty(value = "【月】【社保】任务量")
    private Integer socialMonthDataCount;

    /*@ExcelProperty(value="【日】【社保】任务量")
    @ApiModelProperty(value = "【日】【社保】任务量")
    private Integer socialDayDataCount;*/

    @ExcelProperty(value="【月】【公积金】任务量")
    @ApiModelProperty(value = "【月】【公积金】任务量")
    private Integer accfundMonthDataCount;

    /*@ExcelProperty(value="【日】【公积金】任务量")
    @ApiModelProperty(value = "【日】【公积金】任务量")
    private Integer accfundDayDataCount;*/

}
