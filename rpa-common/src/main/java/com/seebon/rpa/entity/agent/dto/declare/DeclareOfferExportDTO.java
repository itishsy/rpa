package com.seebon.rpa.entity.agent.dto.declare;

import com.seebon.rpa.entity.agent.po.declare.employee.EmployeeSocialDeclareChangeItemBase;
import com.seebon.rpa.entity.agent.po.declare.employee.EmployeeSocialDeclareChangeSuppDetail;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author ZhenShijun
 * @dateTime 2022-10-25 10:51:04
 */
@ApiModel("社保公积金报盘导出类型")
@Data
public class DeclareOfferExportDTO {

    //参保信息
    @ApiModelProperty("增减员id")
    private Integer id;

    @ApiModelProperty("增减员uuid")
    private String uuid;

    @ApiModelProperty("参保城市id")
    private Integer addrId;

    @ApiModelProperty("参保城市")
    private String addrName;

    @ApiModelProperty("单位申报号 or 单位公积金号")
    private String accountNumber;

    @ApiModelProperty("参保起始时间")
    private Date insuredDate;

    @ApiModelProperty("补缴开始时间")
    private Date suppBeginDate;

    @ApiModelProperty("补缴结束时间")
    private Date suppEndDate;

    @ApiModelProperty("申报状态")
    private Integer declareStatus;

    @ApiModelProperty("补充公积金申报状态")
    private Integer suppDeclareStatus;

    @ApiModelProperty("参保基数")
    private String empTbBase;

    @ApiModelProperty("申报类型")
    private String declareType;

    private Integer changeType;

    @ApiModelProperty("创建人姓名")
    private String createName;

    @ApiModelProperty("公积金单位比例")
    private String compRatio;

    @ApiModelProperty("公积金个人比例")
    private String empRatio;

    @ApiModelProperty("补充单位比例")
    private String suppCompRatio;

    @ApiModelProperty("补充个人比例")
    private String suppEmpRatio;

    @ApiModelProperty("公积金失败原因")
    private String failCause;

    @ApiModelProperty("补充公积金失败原因")
    private String suppFailCause;

    @ApiModelProperty("申报参保险种方案信息")
    private String productName;

    @ApiModelProperty("社保申报险种信息")
    private List<EmployeeSocialDeclareChangeItemBase> items;


    // 人员基本信息
    @ApiModelProperty("人员姓名")
    private String name;

    @ApiModelProperty("人员证件号")
    private String idCard;

    @ApiModelProperty("证件类型")
    private String cardType;

    @ApiModelProperty("国籍")
    private String nationality;

    @ApiModelProperty("出生日期")
    private String birthday;

    @ApiModelProperty("手机号码")
    private String telephone;

    @ApiModelProperty("户口性质")
    private String residenceType;

    @ApiModelProperty("婚姻状况")
    private String married;

    @ApiModelProperty("民族")
    private String nation;

    @ApiModelProperty("学历")
    private String education;

    @ApiModelProperty("性别")
    private String sex;

    @ApiModelProperty("通讯地址")
    private String contactAddrDetail;

    @ApiModelProperty("户籍省")
    private String domicileAddrProvinceName;

    @ApiModelProperty("户籍市")
    private String domicileAddrCityName;

    @ApiModelProperty("户籍详细地址")
    private String domicileAddrDetail;

    @ApiModelProperty("银行名称")
    private String bankName;

    @ApiModelProperty("银行账号")
    private String bankAccount;

    @ApiModelProperty("银行账户名")
    private String bankAccountName;

    @ApiModelProperty("开户省")
    private String bankProvince;

    @ApiModelProperty("开户市")
    private String bankCity;

    @ApiModelProperty("开户支行")
    private String bankBranch;


    //人员合同信息
    @ApiModelProperty("合同开始日期")
    private Date contractStartDate;

    @ApiModelProperty("合同结束日期")
    private Date contractEndDate;

    @ApiModelProperty("离职日期")
    private Date leaveDate;


    //人员社保号
    @ApiModelProperty("人员社保号")
    private String socialnum;


    //人员公积金号与个人电脑编号
    @ApiModelProperty("人员公积金号")
    private String accfundnum;

    @ApiModelProperty("个人电脑编号")
    private String computerNumber;

    @ApiModelProperty("申报账户对应的baseId")
    private Integer compAccountId;

    @ApiModelProperty("组织名称")
    private String orgName;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("创建Id")
    private Integer createId;

    @ApiModelProperty("申报动态信息")
    private List<EmployeeSocialDeclareChangeSuppDetail> suppDetails;

    @ApiModelProperty("合并的补缴数据")
    private List<DeclareOfferExportDTO> bjDatas;

}
