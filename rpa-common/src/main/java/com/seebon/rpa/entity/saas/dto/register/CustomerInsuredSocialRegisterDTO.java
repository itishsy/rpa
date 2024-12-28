package com.seebon.rpa.entity.saas.dto.register;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@ApiModel("社保在册信息")
@Data
public class CustomerInsuredSocialRegisterDTO implements Serializable {

    @ApiModelProperty("申报系统类型编码")
    private String tplTypeCode;

    @ApiModelProperty("申报系统类型")
    private String tplTypeName;

    @ApiModelProperty("申报系统字典值排序")
    private Integer tplOrder;

    @ApiModelProperty("单位申报号或单位公积金号")
    private String accountNumber;

    @ApiModelProperty("在户人数")
    private Integer registerNumber;

    @ApiModelProperty("更新时间")
    @JsonFormat(locale="zh",timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

}
