package com.seebon.rpa.entity.saas.dto.message;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@ApiModel("发送政策通知参数")
@Data
public class MessagePolicyNoticeDTO implements Serializable {

    @ApiModelProperty("参保城市")
    private String addrName;

    @ApiModelProperty("业务类型，1：社保，2：公积金")
    private Integer BusinessType;

    @ApiModelProperty("系统类型")
    private String tplTypeCode;

    @ApiModelProperty("附件通知截图上传后的文件id")
    private List<Integer> fileIds;

}
