package com.seebon.rpa.entity.agent.po.declare.employee;


import com.seebon.rpa.entity.Identity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * 员工最基础的表
 */
@ApiModel(value = "员工最基础的表")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "employee_base")
public class EmployeeBase extends Identity implements Serializable {

    /**
     * 姓名
     */
    @Column
    @ApiModelProperty(value = "姓名")
    private String name;

    /**
     * 身份证号
     */
    @Column
    @ApiModelProperty(value = "身份证号")
    private String idCard;

    /**
     * 性别（男、女）
     */
    @Column
    @ApiModelProperty(value = "性别（男、女）")
    private String sex;

    /**
     * 国籍
     */
    @Column
    @ApiModelProperty(value = "国籍")
    private String nationality;

    /**
     * 证件类型
     */
    @Column
    @ApiModelProperty(value = "证件类型")
    private String cardType;

    /**
     * 出生日期
     */
    @Column
    @ApiModelProperty(value = "出生日期")
    private Date birthday;

    /**
     * 户口性质(城镇、农村)
     */
    @Column
    @ApiModelProperty(value = "户口性质(城镇、农村)")
    private String residenceType;

    /**
     * 学历
     */
    @Column
    @ApiModelProperty(value = "学历")
    private String education;

    /**
     * 民族
     */
    @Column
    @ApiModelProperty(value = "民族")
    private String nation;

    /**
     * 婚姻状况
     */
    @Column
    @ApiModelProperty(value = "婚姻状况")
    private String married;

    /**
     * 邮箱
     */
    @Column
    @ApiModelProperty(value = "邮箱")
    private String email;

    /**
     * 银行名称
     */
    @Column
    @ApiModelProperty(value = "银行名称")
    private String bankName;

    /**
     * 银行账号
     */
    @Column
    @ApiModelProperty(value = "银行账号")
    private String bankAccount;

    /**
     * 银行账号名.默认是该员工的姓名
     */
    @Column
    @ApiModelProperty(value = "银行账号名.默认是该员工的姓名")
    private String bankAccountName;

    /**
     * 银行所属省份
     */
    @Column
    @ApiModelProperty(value = "银行所属省份")
    private String bankProvince;

    /**
     * 开户市
     */
    @Column
    @ApiModelProperty(value = "开户市")
    private String bankCity;

    /**
     * 支行名称
     */
    @Column
    @ApiModelProperty(value = "支行名称")
    private String bankBranch;

    /**
     * 联系地址所在省
     */
    @Column
    @ApiModelProperty(value = "联系地址所在省")
    private String contactAddrProvinceName;

    /**
     * 联系定制所在市
     */
    @Column
    @ApiModelProperty(value = "联系定制所在市")
    private String contactAddrCityName;

    /**
     * 联系详细地址
     */
    @Column
    @ApiModelProperty(value = "联系详细地址")
    private String contactAddrDetail;

    /**
     * 手机号码
     */
    @Column
    @ApiModelProperty(value = "手机号码")
    private String telephone;

    /**
     * 户籍省
     */
    @Column
    @ApiModelProperty(value = "户籍省")
    private String domicileAddrProvinceName;

    /**
     * 户籍市
     */
    @Column
    @ApiModelProperty(value = "户籍市")
    private String domicileAddrCityName;

    /**
     * 户籍详细地址
     */
    @Column
    @ApiModelProperty(value = "户籍详细地址")
    private String domicileAddrDetail;

    private static final long serialVersionUID = 1L;

    public static final String COL_ID = "id";

    public static final String COL_NAME = "name";

    public static final String COL_ID_CARD = "id_card";

    public static final String COL_SEX = "sex";

    public static final String COL_NATIONALITY = "nationality";

    public static final String COL_CARD_TYPE = "card_type";

    public static final String COL_BIRTHDAY = "birthday";

    public static final String COL_RESIDENCE_TYPE = "residence_type";

    public static final String COL_EDUCATION = "education";

    public static final String COL_NATION = "nation";

    public static final String COL_MARRIED = "married";

    public static final String COL_EMAIL = "email";

    public static final String COL_BANK_NAME = "bank_name";

    public static final String COL_BANK_ACCOUNT = "bank_account";

    public static final String COL_BANK_ACCOUNT_NAME = "bank_account_name";

    public static final String COL_BANK_PROVINCE = "bank_province";

    public static final String COL_BANK_CITY = "bank_city";

    public static final String COL_BANK_BRANCH = "bank_branch";

    public static final String COL_CONTACT_ADDR_PROVINCE_NAME = "contact_addr_province_name";

    public static final String COL_CONTACT_ADDR_CITY_NAME = "contact_addr_city_name";

    public static final String COL_CONTACT_ADDR_DETAIL = "contact_addr_detail";

    public static final String COL_TELEPHONE = "telephone";

    public static final String COL_DOMICILE_ADDR_PROVINCE_NAME = "domicile_addr_province_name";

    public static final String COL_DOMICILE_ADDR_CITY_NAME = "domicile_addr_city_name";

    public static final String COL_DOMICILE_ADDR_DETAIL = "domicile_addr_detail";

    public static final String COL_CREATE_ID = "create_id";

    public static final String COL_CREATE_TIME = "create_time";

    public static final String COL_UPDATE_ID = "update_id";

    public static final String COL_UPDATE_TIME = "update_time";
}