package com.seebon.rpa.entity.saas.po.social;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@ApiModel("社保费用表(威哥)")
@Data
@Table(name = "social_cost_item")
public class SocialCostItem {
    @ApiModelProperty("主键id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ApiModelProperty("批次号")
    @Column
    private String batchNum;

    @ApiModelProperty("身份证")
    @Column
    private String idCard;

    @ApiModelProperty("姓名")
    @Column
    private String name;

    @ApiModelProperty("检查结果")
    @Column
    private String checkResult;

    @ApiModelProperty("证明文件Id")
    @Column
    private Integer proveFileId;

    @ApiModelProperty("sheet名称")
    @Column
    private String sheetName;

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

    @ApiModelProperty("入职时间")
    @Column
    private Date entryTime;
}
