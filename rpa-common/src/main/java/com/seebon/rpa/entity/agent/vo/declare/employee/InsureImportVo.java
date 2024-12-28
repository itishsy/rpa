package com.seebon.rpa.entity.agent.vo.declare.employee;


import com.seebon.excel.annotations.ExcelEntity;
import com.seebon.excel.annotations.ExcelProperty;
import com.seebon.rpa.entity.agent.dto.declare.InsuranceTypeDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

/**
 * @author lsh
 * @dateTime 2022-05-17
 * 批量新增导入VO
 */
@Data
@ExcelEntity
@Document(collection = "insure_import_vo")
public class InsureImportVo {
    @ApiModelProperty("姓名")
    @NotBlank(message = "姓名必填")
    @ExcelProperty("参保人.姓名")
    private String name;

    @ApiModelProperty("身份证号")
    @NotBlank(message = "身份证号必填")
    @ExcelProperty("参保人.身份证号")
    private String idCard;

    @ApiModelProperty("证件类型")
    @ExcelProperty("参保人.证件类型")
    private String nationality;

    @ApiModelProperty("参保险种")
    @ExcelProperty("社保信息.参保险种")
    private String insureItemName;

    @ApiModelProperty("开始缴纳月")
    @ExcelProperty("社保信息.开始缴纳月")
    private String insuredDateSocial;

    @ApiModelProperty("结束缴纳月")
    @ExcelProperty("社保信息.社保最后缴纳月")
    private String endDateSocial;

    @ApiModelProperty("缴纳基数")
    @ExcelProperty("社保信息.缴纳基数")
    private String empTbBaseSocial;

    @ApiModelProperty("单位比列")
    @ExcelProperty("公积金信息.单位比例")
    private String compRatio;

    @ApiModelProperty("个人比列")
    @ExcelProperty("公积金信息.个人比例")
    private String empRatio;

    @ApiModelProperty("缴纳基数")
    @ExcelProperty("公积金信息.缴纳基数")
    private String empTbBaseAccfund;


    @ApiModelProperty("开始缴纳月")
    @ExcelProperty("公积金信息.开始缴纳月")
    private String insuredDateAccfund;

    @ApiModelProperty("结束缴纳月")
    @ExcelProperty("公积金信息.公积金最后缴纳月")
    private String endDateAccfund;

    @ApiModelProperty("动态字段")
    @ExcelProperty(isMap = true)
    private Map<String, Object> dynamics;

    @ApiModelProperty("社保动态字段")
    private Map<String, String> dynamicSocial;

    @ApiModelProperty("公积金动态字段")
    private Map<String, String> dynamicAccfund;

    @ApiModelProperty("参保地id")
    private Integer addrId;

    @ApiModelProperty("参保地")
    private String addrName;

    @ApiModelProperty("社保：单位社保号id")
    private Integer compAccountIdSocial;

    @ApiModelProperty("单位社保号")
    @ExcelProperty("社保信息.单位社保号")
    private String compAccountSocial;

    @ApiModelProperty("公积金：单位社保号id")
    private Integer compAccountIdAccfund;

    @ApiModelProperty("单位公积金号")
    @ExcelProperty("公积金信息.单位公积金号")
    private String compAccountAccfund;

    @ApiModelProperty("方案id")
    private Integer productId;

    @ApiModelProperty("方案名称")
    private String productName;

    @ApiModelProperty("险种对象")
    private List<InsuranceTypeDTO> codings;

    @ApiModelProperty("失败原因")
    private String failCase;

    @ApiModelProperty("状态 1：成功，0失败 ")
    private Integer status = 1;

    @ApiModelProperty("业务类型：1批量增员，2批量调基，3批量停保")
    private Integer changeTypeName;

    @ApiModelProperty("批次号")
    private String batchNumber;
}
