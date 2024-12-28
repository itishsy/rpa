package com.seebon.rpa.entity.agent.dto.declare;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author ZhenShijun
 * @dateTime 2022-12-30 14:52:34
 */
@ApiModel("人员附件信息")
@Data
public class FilestoreDTO {

    @ApiModelProperty("附件类型")
    private String fileType;

    @ApiModelProperty("附件类型名称")
    private String fileTypeName;

    @ApiModelProperty("文件ids")
    private List<Integer> fileIds;

}
