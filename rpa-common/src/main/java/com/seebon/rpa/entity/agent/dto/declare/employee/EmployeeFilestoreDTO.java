package com.seebon.rpa.entity.agent.dto.declare.employee;

import com.seebon.rpa.entity.saas.po.system.SysFilestore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author ZhenShijun
 * @dateTime 2022-12-29 11:10:33
 */
@ApiModel("人员附件信息")
@Data
public class EmployeeFilestoreDTO {

    @ApiModelProperty("附件类型code")
    private String fileType;

    @ApiModelProperty("附件类型")
    private String fileTypeName;

    @ApiModelProperty("附件信息")
    private List<SysFilestore> files;

    @ApiModelProperty("附件Id")
    private Integer fileId;
}
