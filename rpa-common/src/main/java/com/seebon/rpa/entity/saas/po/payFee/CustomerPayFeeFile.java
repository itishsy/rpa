package com.seebon.rpa.entity.saas.po.payFee;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@ApiModel("客户缴费单据凭证PO")
@Table(name = "customer_pay_fee_file")
@Data
public class CustomerPayFeeFile implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("主键id")
    protected Integer id;

    @ApiModelProperty("对应表customer_pay_fee_list uuid值")
    @Column
    private String payUuid;

    @ApiModelProperty(value = "文件id 对应表sys_filestore id值", required = true)
    @Column
    private Integer fileId;

    @ApiModelProperty(value = "文件类型，1：数据源文件，2：完税证明")
    @Column
    private Integer fileType;

    @ApiModelProperty("创建人id")
    @Column
    private Integer createId;

    @ApiModelProperty("创建时间")
    @Column
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE ,pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale="zh",timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

}
