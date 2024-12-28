package com.seebon.rpa.entity.agent.po.declare.employee;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Document(collection = "employee_multiple_declare_import")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeMultipleDeclareImport implements Serializable {

    private static final long serialVersionUID = 3967325181800516298L;

    @ApiModelProperty("文件id")
    private Integer fileId;

    @ApiModelProperty("参保地id")
    private Integer addrId;

    @ApiModelProperty("参保地名称")
    private String addrName;

    @ApiModelProperty("业务类型")
    private Integer businessType;

    @ApiModelProperty("业务类型名称")
    private String businessTypeName;

    @ApiModelProperty("申报类型")
    private Integer declareType;

    @ApiModelProperty("申报类型名称")
    private String declareTypeName;

    @ApiModelProperty("批次号")
    private String batchNumber;

    @ApiModelProperty("失败记录数")
    private Integer failureCount;

    @ApiModelProperty("警告记录数")
    private Integer warnCount;

    @ApiModelProperty("账户没有在册的记录数")
    private Integer noRegisterCount;

}
