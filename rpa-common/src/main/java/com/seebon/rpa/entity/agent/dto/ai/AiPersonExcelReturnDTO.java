package com.seebon.rpa.entity.agent.dto.ai;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * TODO
 *
 * @author zjf
 * @describe
 * @date 2023-09-25 11:18
 */
@Data
public class AiPersonExcelReturnDTO{

    @ApiModelProperty("表头字段集合")
    List<List<String>> heads ;

    @ApiModelProperty("表体数据集合")
    List<List<String>> dataList ;
}
