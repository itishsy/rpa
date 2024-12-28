package com.seebon.rpa.entity.sms.vo;

import com.seebon.rpa.entity.sms.po.ForwardConfig;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("短信转发配置")
@Data
public class ForwardConfigVO extends ForwardConfig {
    @ApiModelProperty("地区Id")
    private Integer addrId;

    @ApiModelProperty("业务类型（社保：1001001，公积金：1001002）")
    private String businessType;

    @ApiModelProperty("模板内容")
    private String templateContent;
}