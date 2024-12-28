package com.seebon.rpa.entity.saas.dto.register;

import com.seebon.rpa.entity.saas.po.system.SysFilestore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@ApiModel("在册附件DTO")
@Data
public class CustomerInsureRegisterFileDTO implements Serializable {

    @ApiModelProperty("名册id")
    private Integer id;

    @ApiModelProperty("业务类型")
    private Integer businessType;

    @ApiModelProperty("参保城市")
    private String addrName;

    @ApiModelProperty("申报单位id")
    private Integer orgId;

    @ApiModelProperty("申报单位")
    private String orgName;

    @ApiModelProperty("单位申报号或单位公积金号")
    private String accountNumber;

    @ApiModelProperty("在册数据获取时间")
    private Date createTime;

    @ApiModelProperty("名册原始附件表")
    private List<SysFilestore> files;

}
