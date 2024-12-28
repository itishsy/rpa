package com.seebon.rpa.entity.saas.po.register;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@ApiModel("名册附件表")
@Table(name = "customer_insured_register_file")
@Data
public class CustomerInsuredRegisterFile implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ApiModelProperty("名册id")
    @Column
    private Integer registerId;

    @ApiModelProperty("附件id")
    @Column
    private Integer fileId;

}
