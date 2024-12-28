package com.seebon.rpa.entity.saas;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@ApiModel("地区消息表")
@Table(name = "dev_addr_msg")
public class DevAddrMsg {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("主键Id")
    private Integer id;

    @ApiModelProperty("参保地名称")
    private String addrName;

    @ApiModelProperty("业务类型  社保， 公积金")
    private String businessType;

    @ApiModelProperty("客户id")
    private Integer customerId;

    @ApiModelProperty("客户名称")
    private String customerName;

    @ApiModelProperty("core未申报数量")
    private Integer coreNum;

    @ApiModelProperty("core未申报增减员数量")
    private String coreChangeNum;

    @ApiModelProperty("RPA未申报数量")
    private Integer rpaNum;

    @ApiModelProperty("RPA未申报增减员数量")
    private String rpaChangeNum;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;
}
