package com.seebon.rpa.entity.saas.po.declare;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @Author hao
 * @Date 2022/12/29 17:35
 * @Version 1.0
 **/
@Table(name = "policy_paid_in_field_dict")
@Data
public class PolicyPaidInFieldDict {

    @Id
    private Integer id;

    @Column
    private Integer businessType;

    @Column
    private String fieldName;

    @Column
    private String fieldCode;

    @Column
    private String entityFieldName;

    @Column
    private Integer sort;
}
