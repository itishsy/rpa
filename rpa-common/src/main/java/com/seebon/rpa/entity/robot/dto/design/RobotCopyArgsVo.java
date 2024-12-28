package com.seebon.rpa.entity.robot.dto.design;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * TODO
 *
 * @author zjf
 * @describe 信息配置-复制参数请求vo
 * @date 2024-02-22 22:10
 */
@Data
public class RobotCopyArgsVo{
    @ApiModelProperty("机器人应用代码")
    private String appCode;

    @ApiModelProperty("地区id")
    private String addrId;

    @ApiModelProperty("地区名字")
    private String addrName;

    @ApiModelProperty("业务类型 社保:1001001,公积金:1001002")
    private String businessType;

    @ApiModelProperty("业务名称")
    private String businessName;
}
