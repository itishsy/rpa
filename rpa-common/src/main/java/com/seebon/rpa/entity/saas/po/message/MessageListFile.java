package com.seebon.rpa.entity.saas.po.message;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@ApiModel("消息附件信息PO")
@Table(name = "message_list_file")
@Data
public class MessageListFile implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("主键id")
    private Integer id;

    @ApiModelProperty("消息id")
    @Column
    private Integer messageId;

    @ApiModelProperty("附件id")
    @Column
    private Integer fileId;

}
