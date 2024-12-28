package com.seebon.rpa.entity.saas.po.social;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@ApiModel("失业人员item")
@Data
@Table(name = "unemployment_item")
public class UnemploymentItem {
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

    @ApiModelProperty("发证机构")
    @Column
    private String certOrg;
    @ApiModelProperty("发证时间")
    @Column
    private String certTime;
    @ApiModelProperty("发证编号")
    @Column
    private String certNo;

    @ApiModelProperty("发证地区 不一定有值")
    @Column
    private String certAddr;
    @ApiModelProperty("证书更新日期  不一定有值")
    @Column
    private String certUpdateTime;

}
