package com.seebon.rpa.entity.vo.declare;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author hao
 * @Date 2023/1/17 18:22
 * @Version 1.0
 **/
@Data
public class FundPolicyPaidInFieldNameVO {
    private List<String> entityNames;

    @ApiModelProperty("缴纳合计金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String totalAmountTotal;

    @ApiModelProperty("单位合计金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String compTotalAmountTotal;

    @ApiModelProperty("个人合计金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String empTotalAmountTotal;

    @ApiModelProperty("养老保险单位金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String endowmentCompAmountTotal;

    @ApiModelProperty("养老保险个人金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String endowmentEmpAmountTotal;

    @ApiModelProperty("工伤保险金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String industrialInjuryAmountTotal;

    @ApiModelProperty("失业保险单位金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String unemploymentCompAmountTotal;

    @ApiModelProperty("失业保险个人金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String unemploymentEmpAmountTotal;

    @ApiModelProperty("医疗保险单位金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String medicalCompAmountTotal;

    @ApiModelProperty("医疗保险个人金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String medicalEmpAmountTotal;

    @ApiModelProperty("生育保险单位金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String maternityCompAmountTotal;

    @ApiModelProperty("生育保险个人金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String maternityEmpAmountTotal;

    @ApiModelProperty("公积金单位金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String accfundCompAmountTotal;

    @ApiModelProperty("公积金个人金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String accfundEmpAmountTotal;


    @ApiModelProperty("职工补充医疗保险单位金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String complementaryMedicalCompAmountTotal;

    @ApiModelProperty("职工重大疾病医疗补助金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String complementaryMedicalEmpAmountTotal;

    @ApiModelProperty("医疗保险（含生育）单位金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String medicalAndMaternityCompAmountTotal;

    @ApiModelProperty("医疗保险（含生育）个人金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String medicalAndMaternityEmpAmountTotal;

    @ApiModelProperty("补充公积金单位金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String supplementaryAccfundCompAmountTotal;

    @ApiModelProperty("补充公积金个人金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String supplementaryAccfundEmpAmountTotal;

    private String id;

    @ApiModelProperty("客户id")
    private String custId;

    @ApiModelProperty("关联policy_paid_in_situation表")
    private String mysqlId;

    @ApiModelProperty("姓名")
    private String employeeName;

    @ApiModelProperty("证件号码")
    private String idCard;

    @ApiModelProperty("费用所属期")
    private String periodOfExpense;

    @ApiModelProperty("对应费款所属期开始")
    private String feeMonthStart;

    @ApiModelProperty("对应费款所属期截止")
    private String feeMonthEnd;

    @ApiModelProperty("缴费类型")
    private String feeType;

    @ApiModelProperty("单位合计金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String compTotalAmount;

    @ApiModelProperty("个人合计金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String empTotalAmount;

    @ApiModelProperty("缴纳合计金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String totalAmount;

    @ApiModelProperty("养老保险单位基数")
    private String endowmentCompBase;

    @ApiModelProperty("养老保险单位金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String endowmentCompAmount;

    @ApiModelProperty("养老保险个人基数")
    private String endowmentEmpBase;

    @ApiModelProperty("养老保险个人金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String endowmentEmpAmount;

    @ApiModelProperty("失业保险单位基数")
    private String unemploymentCompBase;

    @ApiModelProperty("失业保险单位金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String unemploymentCompAmount;

    @ApiModelProperty("失业保险个人基数")
    private String unemploymentEmpBase;

    @ApiModelProperty("失业保险个人金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String unemploymentEmpAmount;

    @ApiModelProperty("医疗保险单位基数")
    private String medicalCompBase;

    @ApiModelProperty("医疗保险单位金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String medicalCompAmount;

    @ApiModelProperty("医疗保险个人基数")
    private String medicalEmpBase;

    @ApiModelProperty("医疗保险个人金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String medicalEmpAmount;

    @ApiModelProperty("生育保险单位基数")
    private String maternityCompBase;

    @ApiModelProperty("生育保险单位金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String maternityCompAmount;

    @ApiModelProperty("生育保险个人基数")
    private String maternityEmpBase;

    @ApiModelProperty("生育保险个人金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String maternityEmpAmount;

    @ApiModelProperty("工伤保险基数")
    private String industrialInjuryBase;

    @ApiModelProperty("工伤保险金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String industrialInjuryAmount;

    @ApiModelProperty("职工补充医疗保险单位基数")
    private String complementaryMedicalCompBase;

    @ApiModelProperty("职工补充医疗保险单位金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String complementaryMedicalCompAmount;

    @ApiModelProperty("职工重大疾病医疗补助基数")
    private String complementaryMedicalEmpBase;

    @ApiModelProperty("职工重大疾病医疗补助金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String complementaryMedicalEmpAmount;

    @ApiModelProperty("医疗保险（含生育）单位基数")
    private String medicalAndMaternityCompBase;

    @ApiModelProperty("医疗保险（含生育）单位金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String medicalAndMaternityCompAmount;

    @ApiModelProperty("医疗保险（含生育）个人基数")
    private String medicalAndMaternityEmpBase;

    @ApiModelProperty("医疗保险（含生育）个人金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String medicalAndMaternityEmpAmount;

    @ApiModelProperty("用工单位编号")
    private String companyNumber;

    @ApiModelProperty("用工单位名称")
    private String companyName;

    @ApiModelProperty("公积金单位基数")
    private String accfundCompBase;

    @ApiModelProperty("公积金单位比例")
    private String accfundCompRatio;

    @ApiModelProperty("公积金单位金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String accfundCompAmount;

    @ApiModelProperty("公积金个人基数")
    private String accfundEmpBase;

    @ApiModelProperty("公积金个人比例")
    private String accfundEmpRatio;

    @ApiModelProperty("公积金个人金额")

    private String accfundEmpAmount;

    @ApiModelProperty("补充公积金单位基数")
    private String supplementaryAccfundCompBase;

    @ApiModelProperty("补充公积金单位比例")
    private String supplementaryAccfundCompRatio;

    @ApiModelProperty("补充公积金单位金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String supplementaryAccfundCompAmount;

    @ApiModelProperty("补充公积金个人基数")
    private String supplementaryAccfundEmpBase;

    @ApiModelProperty("补充公积金个人比例")
    private String supplementaryAccfundEmpRatio;

    @ApiModelProperty("补充公积金个人金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String supplementaryAccfundEmpAmount;

    @ApiModelProperty("大额互助资金单位应缴金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String largeMutualFundCompAmount;

    @ApiModelProperty("大额互助资金个人应缴金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String largeMutualFundEmpAmount;

    @ApiModelProperty("组织名称")
    private String orgName;

    @ApiModelProperty("单位社保号")
    private String accountNumber;

    @ApiModelProperty("参保城市")
    private String addressName;

    @ApiModelProperty("险种名称")
    private String productNmae;

    @ApiModelProperty("费用类型")
    private String priceType;

}
