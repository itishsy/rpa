package com.seebon.rpa.entity.saas.po.declare;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

/**
 * @Author hao
 * @Date 2022/12/20 13:48
 * @Version 1.0
 **/
@Table(name = "paid_in_field")
@Data
public class PaidInField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    @ApiModelProperty("对应paid_in_field_base uuid")
    private String baseUuid;

    @Column
    @ApiModelProperty("本地实缴字段")
    private String localPaidInField;

    @Column
    @ApiModelProperty("网站实缴字段")
    @ExcelProperty(index = 0)
    private String webPaidInField;

    @Column
    @ApiModelProperty("业务类型（1--社保；2--公积金）")
    private Integer businessType;

    @Column
    private String address;

    @Column
    @ApiModelProperty("状态（1--启用；2--停用）")
    private Integer status;

    @Column
    @ApiModelProperty("前缀")
    private String prefix;

    @Column
    @ApiModelProperty("是否展示 1--展示；2--不展示")
    private Integer requireShow;

    @Column
    @ApiModelProperty("是否删除")
    private Integer isDelete;

    @Column
    @ApiModelProperty("排序")
    private Integer sort;
}
