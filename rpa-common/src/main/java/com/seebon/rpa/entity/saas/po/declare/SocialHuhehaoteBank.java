package com.seebon.rpa.entity.saas.po.declare;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@ApiModel("内蒙社保申报网站银行信息")
@Table(name = "social_huhehaote_bank")
@Data
public class SocialHuhehaoteBank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("主键id")
    private Integer id;

    @ApiModelProperty("层级，1,2")
    @Column
    private Integer level;

    @ApiModelProperty("上级id")
    @Column
    private Integer parentId;

    @ApiModelProperty("银行编码")
    @Column
    private String bankTypeCode;

    @ApiModelProperty("银行名称")
    @Column
    private String bankName;

    @ApiModelProperty("爬取时间")
    @Column
    private Date createTime;

}
