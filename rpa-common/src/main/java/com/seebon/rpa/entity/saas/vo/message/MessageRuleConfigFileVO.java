package com.seebon.rpa.entity.saas.vo.message;

import com.seebon.rpa.entity.saas.po.message.MessageRuleConfigFile;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class MessageRuleConfigFileVO extends MessageRuleConfigFile {

    @ApiModelProperty("文件名称")
    private String clientFileName;

    @ApiModelProperty("文件路径")
    private String serverFilePath;

    @ApiModelProperty("文件http地址")
    private String fileUrl;

}
