package com.seebon.rpa.entity.saas.dto.ai;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * TODO
 *
 * @author zjf
 * @describe AI导入信息
 * @date 2023-08-28 15:12
 */
@Data
public class AiPersonMessageDTO{
    @ApiModelProperty("状态 0失败 1成功")
    private Integer status;

    @ApiModelProperty("返回信息")
    private List<String> message;

}
