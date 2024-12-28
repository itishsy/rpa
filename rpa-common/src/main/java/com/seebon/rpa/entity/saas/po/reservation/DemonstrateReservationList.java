package com.seebon.rpa.entity.saas.po.reservation;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel("产品演示预约信息PO")
@Table(name = "demonstrate_reservation_list")
public class DemonstrateReservationList implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("主键id")
    private Integer id;

    @ApiModelProperty("姓名")
    @Column
    private String name;

    @ApiModelProperty("公司名称")
    @Column
    private String companyName;

    @ApiModelProperty("手机号码")
    @Column
    private String phoneNumber;

    @ApiModelProperty("预约时间")
    @Column
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE ,pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale="zh",timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;


}
