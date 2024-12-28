package com.seebon.rpa.entity.saas.po.declare;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@ApiModel("申报操作标签信息")
@Table(name = "policy_declare_operation_type_dict")
@Data
public class PolicyDeclareOperationTypeDict implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("主键id")
    private Integer id;

    @Column
    @ApiModelProperty("编码")
    private String code;

    @Column
    @ApiModelProperty("名称")
    private String name;

    @Column
    @ApiModelProperty("业务类型，1：社保，2：公积金，空标识表示社保公积金都适用")
    private Integer businessType;

    @Column
    @ApiModelProperty("申报类型，1：增员，2：减员，3：调基，5：补缴，空标识表示都适用")
    private Integer changeType;

    @Column
    @ApiModelProperty("是否默认 1：是，0：否")
    private Integer defaultType;

    @Column
    @ApiModelProperty("备注信息")
    private String comment;

}
