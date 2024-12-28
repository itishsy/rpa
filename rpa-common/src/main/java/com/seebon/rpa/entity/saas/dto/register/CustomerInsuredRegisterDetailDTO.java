package com.seebon.rpa.entity.saas.dto.register;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Map;

@ApiModel("名册详细信息")
@Document(collection = "customer_insured_register_detail")
@Data
public class CustomerInsuredRegisterDetailDTO implements Serializable {

    @ExcelIgnore
    @ApiModelProperty("id")
    private String id;

    @ExcelIgnore
    @ApiModelProperty("名册id")
    private Integer registerId;

    @ExcelIgnore
    @ApiModelProperty("客户id")
    private Integer custId;

    @ExcelIgnore
    private Integer orgId;

    @ExcelIgnore
    private String orgName;

    @ExcelIgnore
    @ApiModelProperty("参保城市id")
    private Integer addrId;

    @ExcelProperty(value = "参保城市", index = 4)
    @ColumnWidth(20)
    @ApiModelProperty("参保城市名称")
    private String addrName;

    @ExcelIgnore
    @ApiModelProperty("业务类型，1：社保，2：公积金")
    private Integer businessType;

    @ExcelProperty(value = "单位社报号", index = 2)
    @ColumnWidth(20)
    @ApiModelProperty("单位社报号或单位公积金号")
    private String accountNumber;

    @ExcelIgnore
    @ApiModelProperty("系统类型")
    private String tplTypeCode;

    @ExcelProperty(value = "参保年月", index = 5)
    @ColumnWidth(20)
    @ApiModelProperty("名册年月")
    private String dataMonth;

    @ExcelProperty(value = "证件号码", index = 1)
    @ColumnWidth(20)
    @ApiModelProperty("证件号码")
    private String idCard;

    @ExcelProperty(value = "姓名", index = 0)
    @ColumnWidth(20)
    @ApiModelProperty("姓名")
    private String empName;

    @ExcelProperty(value = "个人社保号", index = 3)
    @ColumnWidth(20)
    @ApiModelProperty("个人社保号或个人公积金号")
    private String empAccount;

    @ExcelProperty(value = "缴纳基数", index = 6)
    @ColumnWidth(20)
    @ApiModelProperty("缴纳基数")
    private String base;

    @ExcelIgnore
    @ApiModelProperty("其他信息，多余的字段按源文件字段原名：字段值形成存入")
    private Map<String, Object> otherInfo;

}
