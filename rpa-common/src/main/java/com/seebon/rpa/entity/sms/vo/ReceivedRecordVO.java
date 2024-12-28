package com.seebon.rpa.entity.sms.vo;

import com.seebon.rpa.entity.sms.po.ReceivedRecord;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ReceivedRecordVO extends ReceivedRecord {

    @ApiModelProperty("接收器ip")
    private String receiverIp;
}