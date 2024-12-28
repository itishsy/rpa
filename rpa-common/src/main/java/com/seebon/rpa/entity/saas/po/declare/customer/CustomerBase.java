package com.seebon.rpa.entity.saas.po.declare.customer;


import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.seebon.rpa.entity.Identity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "customer_base")
@ApiModel("客户信息表")
public class CustomerBase extends Identity {

  @ExcelProperty(value = "客户名称",index = 0)
  @ColumnWidth(30)
  @ApiModelProperty("公司名称")
  @Column
  private String name;

  @ApiModelProperty("公司名称简称")
  @Column
  private String shortName;

  @ApiModelProperty("平台方")
  @Column
  private String platform;

  @ApiModelProperty("企业地址")
  @Column
  private String custAddress;

  @ApiModelProperty("盒子邮寄地址")
  @Column
  private String addressDetail;

  @ApiModelProperty("企业性质")
  @Column
  private String nature;

  @ApiModelProperty("所属行业")
  @Column
  private String industry;

  @ApiModelProperty("业务类型，对应字典值10025")
  @Column
  private String businessType;

  @ApiModelProperty("客户类别，1：saas客户，2：独立部署客户")
  @Column
  private Integer category;

  @ApiModelProperty("约定制单月1：当月，2：下月")
  @Column
  private Integer billMonth;

  @ApiModelProperty("约定制单天,1~31")
  @Column
  private Integer billDay;

  @ApiModelProperty("约定到款月1：当月，2：下月")
  @Column
  private Integer accountMonth;

  @ApiModelProperty("约定到款天,1~31")
  @Column
  private Integer accountDay;

  @ExcelProperty(value = "预计开通城市",index = 4)
  @ColumnWidth(20)
  @ApiModelProperty("预计开通城市数")
  @Column
  private Integer cityNumber;

  @ExcelProperty(value = "预计开通账户",index = 6)
  @ColumnWidth(20)
  @ApiModelProperty("预计开通账户数")
  @Column
  private Integer accountNumber;

  @ApiModelProperty("服务状态，1：合作中，0：终止服务")
  @Column
  private Integer status;

  @ApiModelProperty("脱敏配置，1：脱敏，0：不脱敏")
  @Column
  private Integer desensitization;

  @ApiModelProperty("是否开启数据申报前校验，1：是，0：否")
  @Column
  private Integer declareValidateRule;

  @ApiModelProperty("是否根据申报期导出报盘，1：是，0：否")
  @Column
  private Integer declareExpRule;

  @ApiModelProperty("服务状态终止日期")
  @Column
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE ,pattern="yyyy-MM-dd")
  @JsonFormat(locale="zh",timezone = "GMT+8", pattern = "yyyy-MM-dd")
  private Date endDate;

  @ExcelProperty(value = "终止合作原因",index = 10)
  @ColumnWidth(35)
  @ApiModelProperty("终止服务原因")
  @Column
  private String terminationReason;

  @ExcelProperty(value = "备注",index = 11)
  @ColumnWidth(35)
  @ApiModelProperty("备注")
  @Column
  private String comment;


}
