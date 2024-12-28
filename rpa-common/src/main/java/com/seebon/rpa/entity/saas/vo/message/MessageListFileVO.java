package com.seebon.rpa.entity.saas.vo.message;

import com.seebon.rpa.entity.saas.po.message.MessageListFile;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("消息附件VO")
@Data
public class MessageListFileVO extends MessageListFile {

    @ApiModelProperty("附件名称")
    private String fileName;

    @ApiModelProperty("附件地址")
    private String fileUrl;

}
