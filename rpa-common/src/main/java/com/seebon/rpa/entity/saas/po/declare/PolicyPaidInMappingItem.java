package com.seebon.rpa.entity.saas.po.declare;

import lombok.Data;

import javax.persistence.*;

@Data
@Table(name = "policy_paid_in_mapping_item")
public class PolicyPaidInMappingItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String localParentFieldName;

    @Column
    private String webItemFieldName;

    @Column
    private String localItemFieldName;

    @Column
    private Integer fieldId;
}
