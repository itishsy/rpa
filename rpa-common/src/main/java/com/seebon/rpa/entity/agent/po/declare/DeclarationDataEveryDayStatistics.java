package com.seebon.rpa.entity.agent.po.declare;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.ContentStyle;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@ApiModel("驾驶仓-每日数据执行情况")
@Table(name = "declaration_data_every_day_statistics")
@Data
@ExcelIgnoreUnannotated
@ContentRowHeight(20)
@HeadRowHeight(20)
@ContentStyle(wrapped=true)
public class DeclarationDataEveryDayStatistics implements Serializable {

    /**
     * 自动增长ID
     */
    @Id
    @ApiModelProperty(value = "自动增长ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ApiModelProperty("日期")
    @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE ,pattern="yyyy-MM-dd")
    @JsonFormat(locale="zh",timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Column
    @ExcelProperty(value="日期", index=0)
    @DateTimeFormat("yyyy-MM-dd")
    private Date statisticsDate;

    @ApiModelProperty("提交数据")
    @Column
    @ExcelProperty(value="提交数据", index=1)
    private Integer submitCount;

    @ApiModelProperty("申报成功")
    @Column
    @ExcelProperty(value="申报成功", index=6)
    private Integer successCount;

    @ApiModelProperty("申报失败")
    @Column
    @ExcelProperty(value="申报失败", index=5)
    private Integer failCount;

    @ApiModelProperty("申报中")
    @Column
    @ExcelProperty(value="申报中", index=3)
    private Integer inProgressCount;

    @ApiModelProperty("待审核")
    @Column
    @ExcelProperty(value="审核中", index=4)
    private Integer auditCount;

    @ApiModelProperty("未申报")
    @Column
    @ExcelProperty(value="未申报", index=2)
    private Integer beDeclaredCount;

    @ApiModelProperty("创建时间")
    @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE ,pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale="zh",timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @Column
    private Date createTime;

}
