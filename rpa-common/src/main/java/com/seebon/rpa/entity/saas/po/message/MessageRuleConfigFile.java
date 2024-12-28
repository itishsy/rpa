package com.seebon.rpa.entity.saas.po.message;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Table(name = "message_rule_config_file")
@Data
public class MessageRuleConfigFile implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer id;

    @ApiModelProperty("对应表message_rule_config id")
    @Column
    private Integer messageRuleId;

    @ApiModelProperty("附件id")
    @Column
    private Integer fileId;
}
