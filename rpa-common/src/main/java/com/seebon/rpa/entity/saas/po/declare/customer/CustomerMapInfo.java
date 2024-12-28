package com.seebon.rpa.entity.saas.po.declare.customer;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

@ApiModel("驾驶餐标题、中心城市配置信息")
@Table(name = "customer_map_info")
@Data
public class CustomerMapInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer id;

    @ApiModelProperty("客户id")
    @Column
    private Integer custId;

    @ApiModelProperty("中心城市id")
    @Column
    private Integer centerAddrId;

    @ApiModelProperty("中心城市名称")
    @Column
    private String centerAddrName;

    @ApiModelProperty("大屏标题")
    @Column
    private String title;

    @ApiModelProperty("图标文件id")
    @Column
    private Integer logoFileId;

}
