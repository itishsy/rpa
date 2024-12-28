package com.seebon.rpa.entity.agent.dto.declare;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.seebon.rpa.entity.agent.po.declare.employee.EmployeeSocialDeclareChangeItemBase;
import com.seebon.rpa.entity.saas.po.system.SysFilestore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@ApiModel("参保接口录入mongo数据信息")
@Data
@ExcelIgnoreUnannotated
@Document(collection = "employee_declare_differences")
public class EmployeeDeclareDifferencesDTO implements Serializable {

    @ApiModelProperty("id")
    private String id;

    @ApiModelProperty("机器人执行批次号")
    private String robotExecCode;

    @ApiModelProperty("增减员记录id")
    private Integer declareId;

    @ApiModelProperty("增减员记录uuid")
    private String uuid;

    @ApiModelProperty("客户id")
    private Integer custId;

    @ApiModelProperty("客户名称")
    private String custName;

    @ApiModelProperty("参保城市id")
    private Integer addrId;

    @ApiModelProperty("参保城市名称")
    private String addrName;

    @ApiModelProperty("申报单位名称")
    private String orgName;

    @ApiModelProperty("申报单位id")
    private Integer orgId;

    @ApiModelProperty("单位申报账户")
    private String accountNumber;

    @ApiModelProperty("业务类型，1：社保，2：公积金")
    private Integer businessType;

    @ApiModelProperty("业务类型名称")
    private String businessTypeName;

    @ApiModelProperty("人员证件号码")
    private String idCard;

    @ApiModelProperty("人员姓名")
    private String name;

    @ApiModelProperty("申报系统code")
    private String tplTypeCode;

    @ApiModelProperty("申报系统名称")
    private String tplTypeCodeName;

    @ApiModelProperty("申报类型编码，1：增员，2：减员")
    private Integer changeType;

    @ApiModelProperty("申报类型名称")
    private String changeTypeName;;

    @ApiModelProperty("申报回盘状态编码，2：申报中，4：申报成功，5：申报失败")
    private Integer declareStatus;

    @ApiModelProperty("申报回盘状态名称")
    private String declareStatusName;

    @ApiModelProperty("社保申报险种信息")
    private List<EmployeeSocialDeclareChangeItemBase> items;

    @ApiModelProperty("差异生成时间")
    private Date createTime;

    @ApiModelProperty("差异刷新时间")
    private Date updateTime;

    @ApiModelProperty("在册情况状态，0：未获取，1：有在册，2：无在册")
    private Integer registerStatus;

    @ApiModelProperty("在册情况名称")
    private String registerStatusName;

    @ApiModelProperty("对应的在册批次唯一标识")
    private Integer registerId;

    @ApiModelProperty("申报账户获取在册时间")
    private Date registerDate;

    @ApiModelProperty("名册原始附件表")
    private List<SysFilestore> files;

    @ApiModelProperty("异常类别（是否异常），0：否，1：是")
    private Integer diffType;

    @ApiModelProperty("异常信息（备注）")
    private String comment;

    @ApiModelProperty("开发用户名")
    private String devUserName;

    @ApiModelProperty("测试用户名")
    private String testUserName;

    @ApiModelProperty("运维用户名")
    private String ywUserName;

    @ApiModelProperty("调研用户名")
    private String xqUserName;

}
