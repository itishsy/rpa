package com.seebon.rpa.entity.po.declare;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.util.Map;

/**
 * @Author hao
 * @Date 2023/1/4 11:37
 * @Version 1.0
 **/
@Data
@Document(collection = "social_insurance_policy_paid_in_field_name")
public class SocialInsurancePolicyPaidInFieldName {


    @Field(targetType = FieldType.STRING)
    private String id;

    @ApiModelProperty("客户id")
    private Integer custId;

    @ApiModelProperty("关联policy_paid_in_situation表")
    private Integer mysqlId;

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
    private Double compTotalAmount;

    @ApiModelProperty("个人合计金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Double empTotalAmount;

    @ApiModelProperty("缴纳合计金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Double totalAmount;

    @ApiModelProperty("养老保险单位基数")
    private Double endowmentCompBase;

    @ApiModelProperty("养老保险单位金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Double endowmentCompAmount;

    @ApiModelProperty("养老保险个人基数")
    private Double endowmentEmpBase;

    @ApiModelProperty("养老保险个人金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Double endowmentEmpAmount;

    @ApiModelProperty("失业保险单位基数")
    private Double unemploymentCompBase;

    @ApiModelProperty("失业保险单位金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Double unemploymentCompAmount;

    @ApiModelProperty("失业保险个人基数")
    private Double unemploymentEmpBase;

    @ApiModelProperty("失业保险个人金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Double unemploymentEmpAmount;

    @ApiModelProperty("医疗保险单位基数")
    private Double medicalCompBase;

    @ApiModelProperty("医疗保险单位金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Double medicalCompAmount;

    @ApiModelProperty("医疗保险个人基数")
    private Double medicalEmpBase;

    @ApiModelProperty("医疗保险个人金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Double medicalEmpAmount;

    @ApiModelProperty("生育保险单位基数")
    private Double maternityCompBase;

    @ApiModelProperty("生育保险单位金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Double maternityCompAmount;

    @ApiModelProperty("生育保险个人基数")
    private Double maternityEmpBase;

    @ApiModelProperty("生育保险个人金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Double maternityEmpAmount;

    @ApiModelProperty("工伤保险基数")
    private Double industrialInjuryBase;

    @ApiModelProperty("工伤保险金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Double industrialInjuryAmount;

    @ApiModelProperty("职工补充医疗保险单位基数")
    private Double complementaryMedicalCompBase;

    @ApiModelProperty("职工补充医疗保险单位金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Double complementaryMedicalCompAmount;

    @ApiModelProperty("职工重大疾病医疗补助基数")
    private Double complementaryMedicalEmpBase;

    @ApiModelProperty("职工重大疾病医疗补助金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Double complementaryMedicalEmpAmount;

    @ApiModelProperty("医疗保险（含生育）单位基数")
    private Double medicalAndMaternityCompBase;

    @ApiModelProperty("医疗保险（含生育）单位金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Double medicalAndMaternityCompAmount;

    @ApiModelProperty("医疗保险（含生育）个人基数")
    private Double medicalAndMaternityEmpBase;

    @ApiModelProperty("医疗保险（含生育）个人金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Double medicalAndMaternityEmpAmount;

    @ApiModelProperty("用工单位编号")
    private String companyNumber;

    @ApiModelProperty("用工单位名称")
    private String companyName;

    @ApiModelProperty("公积金单位基数")
    private Double accfundCompBase;

    @ApiModelProperty("公积金单位比例")
    private String accfundCompRatio;

    @ApiModelProperty("公积金单位金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Double accfundCompAmount;

    @ApiModelProperty("公积金个人基数")
    private Double accfundEmpBase;

    @ApiModelProperty("公积金个人比例")
    private String accfundEmpRatio;

    @ApiModelProperty("公积金个人金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Double accfundEmpAmount;

    @ApiModelProperty("补充公积金单位基数")
    private Double supplementaryAccfundCompBase;

    @ApiModelProperty("补充公积金单位比例")
    private String supplementaryAccfundCompRatio;

    @ApiModelProperty("补充公积金单位金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Double supplementaryAccfundCompAmount;

    @ApiModelProperty("补充公积金个人基数")
    private Double supplementaryAccfundEmpBase;

    @ApiModelProperty("补充公积金个人比例")
    private String supplementaryAccfundEmpRatio;

    @ApiModelProperty("补充公积金个人金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Double supplementaryAccfundEmpAmount;

    @ApiModelProperty("大额互助资金单位应缴金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Double largeMutualFundCompAmount;

    @ApiModelProperty("大额互助资金个人应缴金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Double largeMutualFundEmpAmount;

    @ApiModelProperty("组织名称")
    private String orgName;

    @ApiModelProperty("单位社保号")
    private String accountNumber;

    @ApiModelProperty("参保城市")
    private String addressName;

    @ApiModelProperty("原始费用信息")
    private Map<String, Object> originalInfo;

    @ApiModelProperty("重疾保险单位基数")
    private Double seriousIllnessCompBase;

    @ApiModelProperty("重疾保险单位金额")
    private Double seriousIllnessCompAmount;

    @ApiModelProperty("重疾保险个人基数")
    private Double seriousIllnessEmpBase;

    @ApiModelProperty("重疾保险个人金额")
    private Double seriousIllnessEmpAmount;

    @ApiModelProperty("大病医疗保险单位基数")
    private Double seriousIllnessMedicalCompBase;

    @ApiModelProperty("大病医疗保险单位金额")
    private Double seriousIllnessMedicalCompAmount;

    @ApiModelProperty("大病医疗保险个人基数")
    private Double seriousIllnessMedicalEmpBase;

    @ApiModelProperty("大病医疗保险个人金额")
    private Double seriousIllnessMedicalEmpAmount;

    @ApiModelProperty("门诊医疗保险单位基数")
    private Double outpatientMedicalCompBase;

    @ApiModelProperty("门诊医疗保险单位金额")
    private Double outpatientMedicalCompAmount;

    @ApiModelProperty("门诊医疗保险个人基数")
    private Double outpatientMedicalEmpBase;

    @ApiModelProperty("门诊医疗保险个人金额")
    private Double outpatientMedicalEmpAmount;

    @ApiModelProperty("大额医保保险单位基数")
    private Double largeMedicalCompBase;

    @ApiModelProperty("大额医保保险单位金额")
    private Double largeMedicalCompAmount;

    @ApiModelProperty("大额医保保险个人基数")
    private Double largeMedicalEmpBase;

    @ApiModelProperty("大额医保保险个人金额")
    private Double largeMedicalEmpAmount;

    @ApiModelProperty("住院医疗保险单位基数")
    private Double hospitalizationCompBase;

    @ApiModelProperty("住院医疗保险单位金额")
    private Double hospitalizationCompAmount;

    @ApiModelProperty("住院医疗保险个人基数")
    private Double hospitalizationEmpBase;

    @ApiModelProperty("住院医疗保险个人金额")
    private Double hospitalizationEmpAmount;

    @ApiModelProperty("个人医疗账户基础金基数")
    private Double personalMedicalAccountBase;

    @ApiModelProperty("个人医疗账户基础金金额")
    private Double personalMedicalAccountAmount;

    @ApiModelProperty("险种名称")
    private String productNmae;

    @ApiModelProperty("费用类型")
    private String priceType;

}
