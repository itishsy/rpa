package com.seebon.rpa.entity.saas.dto;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@ExcelIgnoreUnannotated
@ContentRowHeight(20)
@HeadRowHeight(20)
@Data
@Document(collection = "addr_business_change_process")
public class AddrBusinessChangeProcessDTO implements Serializable {

    @ApiModelProperty("id")
    private String id;

    @ApiModelProperty("参保城市id")
    private Integer addrId;

    @ExcelProperty(value = "参保城市", index = 0)
    @ColumnWidth(15)
    @ApiModelProperty("参保城市")
    private String addrName;

    @ApiModelProperty("业务类型，1：社保，2：公积金")
    private Integer businessType;

    @ExcelProperty(value = "业务类型", index = 1)
    @ColumnWidth(20)
    @ApiModelProperty("业务类型名称")
    private String businessTypeName;

    @ExcelProperty(value = "变更类型", index = 2)
    @ColumnWidth(20)
    @ApiModelProperty("变更类型")
    private String changeType;

    @ExcelProperty(value = "变更原因", index = 3)
    @ColumnWidth(20)
    @ApiModelProperty("变更原因")
    private String changeReason;

    @ApiModelProperty("修改人 id")
    private Integer createId;

    @ExcelProperty(value = "修改人", index = 7)
    @ColumnWidth(20)
    @ApiModelProperty("修改人姓名")
    private String createName;

    @ExcelProperty(value = "修改时间", index = 8)
    @ColumnWidth(25)
    @ApiModelProperty("修改时间")
    private String createTime;

    @ExcelProperty(value = "变更前内容", index = 5)
    @ColumnWidth(50)
    @ApiModelProperty("原值")
    private String originalValue;

    @ExcelProperty(value = "变更内容", index = 4)
    @ColumnWidth(50)
    @ApiModelProperty("变更后值")
    private String newValue;

    @ExcelProperty(value = "原因备注", index = 6)
    @ColumnWidth(30)
    @ApiModelProperty("备注信息")
    private String comment;

}
