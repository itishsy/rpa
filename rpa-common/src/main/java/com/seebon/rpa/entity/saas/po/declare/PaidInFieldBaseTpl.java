package com.seebon.rpa.entity.saas.po.declare;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Table(name = "paid_in_field_base_tpl")
@ApiModel("费用信息客户模板信息")
@Data
public class PaidInFieldBaseTpl implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("主键id")
    protected Integer id;

    @Column
    @ApiModelProperty("paid_in_field_base uuid")
    private String baseUuid;

    @Column
    @ApiModelProperty("客户id")
    private Integer custId;

    @Column
    @ApiModelProperty("模板类型code，对应字典值10022")
    private String tplType;

    @Column
    @ApiModelProperty("参保城市id")
    private Integer addrId;

    @Column
    @ApiModelProperty("业务类型，1--社保；2--公积金；0--社保公积金联合导出模板")
    private Integer businessType;

    @Column
    @ApiModelProperty("文件id")
    private Integer fileId;


    @Column
    @ApiModelProperty("创建人id")
    private Integer createId;

    @Column
    @ApiModelProperty("创建时间")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE ,pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale="zh",timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

}
