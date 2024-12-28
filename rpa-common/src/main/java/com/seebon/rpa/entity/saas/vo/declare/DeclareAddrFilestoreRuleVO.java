package com.seebon.rpa.entity.saas.vo.declare;

import com.seebon.rpa.entity.saas.po.declare.DeclareAddrFilestoreRule;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @author ZhenShijun
 * @dateTime 2022-12-27 17:54:38
 */
@Data
@ApiModel("参保城市附件规则信息VO")
public class DeclareAddrFilestoreRuleVO extends DeclareAddrFilestoreRule {

    @ApiModelProperty("示例文件名称")
    private String clientFileName;

    @ApiModelProperty("示例文件路径")
    private String serverFilePath;

    @ApiModelProperty("示例文件http地址")
    private String fileUrl;

    @ApiModelProperty("示例文件类型")
    private Integer filestoreType;

}
