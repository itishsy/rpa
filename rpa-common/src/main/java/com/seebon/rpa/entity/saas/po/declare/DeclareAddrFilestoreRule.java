package com.seebon.rpa.entity.saas.po.declare;

import com.seebon.rpa.entity.Identity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * @author ZhenShijun
 * @dateTime 2022-12-27 17:37:18
 */
@Data
@ApiModel("参保城市附件规则信息")
@Table(name = "declare_addr_filestore_rule")
public class DeclareAddrFilestoreRule extends Identity {

    @ApiModelProperty("参保地id")
    @Column
    private Integer addrId;

    @ApiModelProperty("参保地名称")
    @Column
    private String addrName;

    /*@ApiModelProperty("字段名称")
    @Column
    private String name;*/

    @ApiModelProperty("字段类型，1：附件上传")
    @Column
    private Integer fieldType;

    @ApiModelProperty("附件类型字典值10014")
    @Column
    private String fileType;

    @ApiModelProperty("附件类型名称")
    @Column
    private String fileTypeName;

    @ApiModelProperty("格式 1：文件类型不限，2：图片类型不限，3：JPG图片")
    @Column
    private Integer format;

    @ApiModelProperty("状态 1：启用，0：停用")
    @Column
    private Integer status;

    @ApiModelProperty("备注信息")
    @Column
    private String comment;

    @ApiModelProperty("格式要求描述")
    @Column
    private String formatDescription;

    @ApiModelProperty("底色 1：白色，2：蓝色，3：红色")
    @Column
    private Integer backgroundColor;

    @ApiModelProperty("图片最小大小，单位KB")
    @Column
    private Float minSize;

    @ApiModelProperty("图片最大大小，单位KB")
    @Column
    private Float maxSize;

    @ApiModelProperty("图片最小宽度，单位px")
    @Column
    private Integer minWidth;

    @ApiModelProperty("图片最大宽度，单位px")
    @Column
    private Integer maxWidth;

    @ApiModelProperty("图片最小高度，单位px")
    @Column
    private Integer minHeight;

    @ApiModelProperty("图片最大高度，单位px")
    @Column
    private Integer maxHeight;

    @ApiModelProperty("最小份数")
    @Column
    private Integer minCount;

    @ApiModelProperty("最大份数")
    @Column
    private Integer maxCount;

    @ApiModelProperty("是否适用社保增员，1：是，0：否")
    @Column
    private Integer socialAdd;

    @ApiModelProperty("是否适用社保减员，1：是，0：否")
    @Column
    private Integer socialStop;

    @ApiModelProperty("是否适用社保调基，1：是，0：否")
    @Column
    private Integer socialAdjust;

    @ApiModelProperty("是否适用社保补缴，1：是，0：否")
    @Column
    private Integer socialSuppment;

    @ApiModelProperty("是否适用公积金增员，1：是，0：否")
    @Column
    private Integer accfundAdd;

    @ApiModelProperty("是否适用公积金减员，1：是，0：否")
    @Column
    private Integer accfundStop;

    @ApiModelProperty("是否适用公积金调基，1：是，0：否")
    @Column
    private Integer accfundAdjust;

    @ApiModelProperty("是否适用公积金补缴，1：是，0：否")
    @Column
    private Integer accfundSuppment;

    @ApiModelProperty("示例文件id")
    @Column
    private Integer exampleFileId;

}
