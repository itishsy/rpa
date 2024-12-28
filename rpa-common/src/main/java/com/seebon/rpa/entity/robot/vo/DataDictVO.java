package com.seebon.rpa.entity.robot.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

/**
 * 机器人数据字典
 */
@Getter
public class DataDictVO {

    public DataDictVO(String dictCode, String dictName){
        this.dictCode = dictCode;
        this.dictName = dictName;
    }

    @ApiModelProperty(value = "字典编码")
    private String dictCode;

    @ApiModelProperty(value = "字典名称")
    private String dictName;
}
