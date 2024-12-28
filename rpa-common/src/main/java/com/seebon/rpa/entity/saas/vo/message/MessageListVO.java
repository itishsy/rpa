package com.seebon.rpa.entity.saas.vo.message;

import com.seebon.rpa.entity.saas.po.message.MessageList;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel("消息列表VO")
@Data
public class MessageListVO extends MessageList {

    @ApiModelProperty("消息类型名称")
    private String messageTypeName;

    @ApiModelProperty("消息类型项目")
    private String warnTypeName;

    @ApiModelProperty("是否有附件，1：是，0：否")
    private Integer hasFile;

    @ApiModelProperty("消息附件信息")
    private List<MessageListFileVO> files;

}
