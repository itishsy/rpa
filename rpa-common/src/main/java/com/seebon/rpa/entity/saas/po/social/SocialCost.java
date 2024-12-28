package com.seebon.rpa.entity.saas.po.social;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@ApiModel("社保费用表(威哥)")
@Data
@Table(name = "social_cost")
public class SocialCost {
    @ApiModelProperty("主键id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ApiModelProperty("批次号")
    @Column
    private String batchNum;

    @ApiModelProperty("文件id")
    @Column
    private Integer fileId;

    @ApiModelProperty("文件名称")
    @Column
    private String fileName;

    @ApiModelProperty("导入状态：0-处理中,1-导入成功")
    @Column
    private Integer status;

    @ApiModelProperty("创建人Id")
    @Column
    private Integer createId;

    @ApiModelProperty("创建人名称")
    @Column
    private String createName;

    @ApiModelProperty("创建时间")
    @Column
    private Date createTime;

    @ApiModelProperty("客户ID")
    @Column
    private Integer custId;

}
