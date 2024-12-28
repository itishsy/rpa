package com.seebon.rpa.entity.saas.vo.declare;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author lsh
 * @dateTime 2023-01-12
 */
@ApiModel(value = "报盘设置信息查询版本号VO")
@Data
public class PolicyDeclareBaseSettingVersionsVO {

    @ApiModelProperty("新版本号")
    private String versions;

    @ApiModelProperty("旧版本号")
    private String oldVersions;

    @ApiModelProperty("变更原因")
    private String changeReason;

    @ApiModelProperty("版本号说明")
    private String  explain;

    @ApiModelProperty("上一个版本记录")
    private Integer id;

    @ApiModelProperty("保存参数")
    private PolicyDeclareBaseSettingVO policyDeclareBaseSettingVO;
}
