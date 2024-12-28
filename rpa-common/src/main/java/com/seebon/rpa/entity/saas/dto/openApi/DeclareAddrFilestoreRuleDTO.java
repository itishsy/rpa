package com.seebon.rpa.entity.saas.dto.openApi;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author ZhenShijun
 * @dateTime 2022-12-28 14:18:23
 */
@ApiModel("参保城市附件规则信息")
@Data
public class DeclareAddrFilestoreRuleDTO {

    @ApiModelProperty("参保城市")
    private String addrName;

    @ApiModelProperty("附件类型字典值10014")
    private String fileType;

    @ApiModelProperty("附件类型名称")
    private String fileTypeName;

    @ApiModelProperty("格式 1：文件类型不限，2：图片类型不限，3：JPG图片")
    private Integer format;

    @ApiModelProperty("备注信息")
    private String comment;

    @ApiModelProperty("底色 1：白色，2：蓝色，3：红色")
    private Integer backgroundColor;

    @ApiModelProperty("图片最小大小，单位KB")
    private Float minSize;

    @ApiModelProperty("图片最大大小，单位KB")
    private Float maxSize;

    @ApiModelProperty("图片最小宽度，单位px")
    private Integer minWidth;

    @ApiModelProperty("图片最大宽度，单位px")
    private Integer maxWidth;

    @ApiModelProperty("图片最小高度，单位px")
    private Integer minHeight;

    @ApiModelProperty("图片最大高度，单位px")
    private Integer maxHeight;

    @ApiModelProperty("最小份数")
    private Integer minCount;

    @ApiModelProperty("最大份数")
    private Integer maxCount;

    @ApiModelProperty("示例文件http地址")
    private String fileUrl;

}
